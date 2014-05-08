package com.acumen.sharebox.javaversion.rest;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import com.sun.jersey.core.header.FormDataContentDisposition;

public class AwsS3BucketHandling {

	private String ipaddress="";

	public JSONObject doAuthentication(String emailid, String pwd) throws Exception{
		System.out.println("in awss3 doauthentication...");
		JSONObject mystring = new JSONObject();
		mystring.put("email", emailid);
		mystring.put("password", pwd);
		AwsS3BucketHandling m1 = new AwsS3BucketHandling();
		JSONObject result = m1.checkUser(mystring);

		return result;
	}

	public JSONObject checkUser(JSONObject user) throws Exception{ 
		String origPasswd = null; 
		JSONObject result= new JSONObject();
		System.out.println("in awss3 chkuser...");
		MongoClient mongo = new MongoClient( ipaddress , 27017 );
		try { 
			DB db = mongo.getDB("student");
			DBCollection table = db.getCollection("details");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("email", user.get("email"));
			DBCursor cursor = table.find(whereQuery);
			while(cursor.hasNext()) {
				DBObject getdata=cursor.next();
				System.out.println(getdata);
				origPasswd = getdata.get("password").toString();
				System.out.println("origPasswd:: "+origPasswd);
			}

			if(user.get("password").equals(origPasswd)){
				System.out.println("success");
				result.put("Msg", "success");
			}
			else{
				System.out.println("fail");
				result.put("Msg", "fail");
			}

		}catch (Exception e) { 
			e.printStackTrace(); 
		} 
		return(result);
	}

	public JSONObject registerUser(String firstname,String lastname,String emailid) throws Exception{
		S3ObjectLinks obj=new S3ObjectLinks();
		String bucketname=firstname.toLowerCase()+"-"+UUID.randomUUID().toString();
		obj.CreateBucket(bucketname);

		System.out.println("in awss3 registeruser");
		JSONObject mystring = new JSONObject();
		mystring.put("firstname", firstname);
		mystring.put("lastname", lastname);
		mystring.put("email", emailid);
		//		mystring.put("password", pwd);
		mystring.put("bucketname", bucketname);
		AwsS3BucketHandling m1 = new AwsS3BucketHandling();
		JSONObject output = m1.addUser(mystring);
		m1.logActivity(emailid,firstname+" registered with ShareBox","Registration");
		System.out.println(output);

		return output;
	}


	public JSONObject addUser(JSONObject user) throws Exception{
		JSONObject result= new JSONObject();
		MongoClient mongo = new MongoClient( ipaddress , 27017 );
		System.out.println("in awss3 adduser..");
		try{
			boolean flag = false;

			//create password..and after DB entry send mail to the user!
			Calendar time = Calendar.getInstance();
			String password=user.getString("firstname")+Integer.toString(time.get(Calendar.HOUR))+Integer.toString(time.get(Calendar.MINUTE))+Integer.toString(time.get(Calendar.MILLISECOND));

			BasicDBObject query = new BasicDBObject("firstname",user.get("firstname")).append("lastname", user.get("lastname")).append("email", user.get("email")).append("password", password).append("bucketname", user.get("bucketname"));
			DB db = mongo.getDB("student");
			DBCollection table = db.getCollection("details");

			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("email", user.get("email"));
			DBCursor cursor = table.find(whereQuery);
			while(cursor.hasNext()) {
				DBObject getdata=cursor.next();
				String email = getdata.get("email").toString();
				if(email.equals(user.get("email"))){
					flag = true;
					result.put("Msg", "failure");
					break; 
				}
			}
			if(flag == false){
				table.insert(query);

				//storage limit set start
				db = mongo.getDB("metadata");
				table = db.getCollection("userstoragelimit");
				BasicDBObject insertQuery = new BasicDBObject();
				insertQuery.put("username", user.get("email"));
				double currsize=0.00,limit=1024.00;
				insertQuery.put("currentsize",currsize);
				insertQuery.put("limit", limit);
				table.insert(insertQuery);
				//storage limit set end

				AmazonSESSample aSES=new AmazonSESSample();
				String response=aSES.sendRegisterMail(user.getString("firstname"),user.getString("email"),password);
				result.put("Msg", response);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return(result);
	}

	public String logActivity(String useremail,String details,String activity)throws Exception{
		JSONObject result= new JSONObject();
		MongoClient mongo = new MongoClient( ipaddress , 27017 );
		System.out.println("in awss3 logActivity..");
		try{
			DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			String dt=dateFormat.format(cal.getTime());

			BasicDBObject query = new BasicDBObject("email", useremail).append("details", details).append("activity", activity).append("timestmp", dt);
			DB db = mongo.getDB("student");
			DBCollection table = db.getCollection("logactivities");

			table.insert(query);
			System.out.println("insert logs query: "+query);
			System.out.println("logs created successfully!!");
		}catch (Exception e){
			System.out.println("something failed in logs!!");
			e.printStackTrace();
		}
		return(result).toString();
	}

	public String addS3BucketObjects( File fileobject,FormDataContentDisposition contentDispositionHeader,String useremail) throws Exception{
		String response="fail";
		//		contentDispositionHeader.
		AmazonS3 s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		s3.setRegion(usWest2);
		String bucketName=getBucketName(useremail);

		try {
			System.out.println("\nUploading a new object to S3...");
			System.out.println("upload file name:: "+contentDispositionHeader.getFileName());

			PutObjectRequest putObj=new PutObjectRequest(bucketName, contentDispositionHeader.getFileName(), fileobject);

			//making the object Public
			putObj.setCannedAcl(CannedAccessControlList.PublicRead);
			s3.putObject(putObj);


			//original - old version
			//s3.putObject(new PutObjectRequest(bucketName, contentDispositionHeader.getFileName(), fileobject));

			AmazonS3Client awss3client=new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
			awss3client.getResourceUrl(bucketName, contentDispositionHeader.getFileName());

			System.out.println("File uploaded on S3 - location: "+bucketName+" -> "+contentDispositionHeader.getFileName());
			ObjectMetadata objectMetadata=new ObjectMetadata();
			objectMetadata=s3.getObjectMetadata(bucketName, contentDispositionHeader.getFileName());
			JSONObject obj1 = new JSONObject();
			obj1.put("username", useremail);
			obj1.put("filename", contentDispositionHeader.getFileName());
			obj1.put("bucketname", bucketName);
			DecimalFormat df2 = new DecimalFormat("###.##");
			obj1.put("filesize", (df2.format(((double)objectMetadata.getContentLength())/(1024*1024))));
			obj1.put("lastmodified", objectMetadata.getLastModified());
			obj1.put("owner", useremail);
			obj1.put("downloadlink", awss3client.getResourceUrl(bucketName, contentDispositionHeader.getFileName()));
			obj1.put("rating", "none");

			System.out.println("result str:  "+obj1);
			String res=addMetaDataToDB(obj1);
			response=res;
			if(response.equals("success")){
				System.out.println("size is under limit so logging...");
				logActivity(useremail,useremail+" uploaded a file named " +contentDispositionHeader.getFileName()+ " in ShareBox","File Upload");
			}
			else if(response.equals("Limit full")){
				System.out.println("oversize..deleting object from:: "+bucketName);

				awss3client=new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
				awss3client.deleteObject(bucketName, contentDispositionHeader.getFileName());
				System.out.println("object deleted");
			}

		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon S3, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with S3, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
		return response;
	}

	public String viewS3BucketObjects(String username){
		String bucketObjectDetails=null;
		try{
			bucketObjectDetails=getBucketObjectDetailsFromDB(username);
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("bucketObjectDetails:::: "+bucketObjectDetails);
		return bucketObjectDetails;
	}

	public String getBucketObjectDetailsFromDB(String username){
		String result=getMetadataFromDB(username);
		return result;
	}

	public String getBucketName(String useremail) throws Exception{
		JSONObject obj1 = new JSONObject();
		System.out.println("in getBucketname:: "+useremail);
		try{
			MongoClient mongo = new MongoClient( ipaddress , 27017 );
			DB db = mongo.getDB("student");
			DBCollection table = db.getCollection("details");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("email", useremail);
			table.find(whereQuery);
			DBCursor cursor = table.find(whereQuery); 
			while(cursor.hasNext()){
				System.out.println("in while");
				DBObject getdata=cursor.next();
				System.out.println("$$$$Dbdata:::: "+getdata.toString());
				String bucketname = getdata.get("bucketname").toString();
				obj1.put("bucketname", bucketname);
			}
			System.out.println("final object::  "+obj1);
		}catch(Exception e){
			e.printStackTrace();
		}
		return obj1.get("bucketname").toString();

	}

	public String addMetaDataToDB(JSONObject metadata){
		String result="fail";
		try{
			MongoClient mongo = new MongoClient( ipaddress , 27017 );
			DB db = mongo.getDB("metadata");
			DBCollection table = db.getCollection("details");
			BasicDBObject query = new BasicDBObject("username",metadata.get("username")).append("filename",metadata.get("filename")).append("bucketname", metadata.get("bucketname")).append("filesize", metadata.get("filesize")).append("lastmodified", metadata.get("lastmodified")).append("owner",metadata.get("owner")).append("downloadlink",metadata.get("downloadlink")).append("rating",metadata.get("rating"));
			table.insert(query);

			// storage limit start

			table = db.getCollection("userstoragelimit");
			BasicDBObject newDocument = new BasicDBObject();
			BasicDBObject searchQuery = new BasicDBObject().append("username", metadata.get("username"));
			DBCursor cursor=table.find(searchQuery);
			DBObject getdata;
			double currsize=0.00,limit=0.00;
			while(cursor.hasNext()) {
				getdata=cursor.next();
				currsize=Double.parseDouble(getdata.get("currentsize").toString());
				limit=Double.parseDouble(getdata.get("limit").toString());
			}
			double newSize=metadata.getDouble("filesize")+currsize;
			if(newSize<=limit){
				newDocument.append("$set", new BasicDBObject().append("currentsize", newSize));
				searchQuery = new BasicDBObject().append("username", metadata.get("username"));
				table.update(searchQuery, newDocument);
				result="success";
			}else{
				result="Limit full";
			}

			//storage limit end

		}catch(Exception e){
			e.printStackTrace();
		}
		return (result);
	}

	public String getMetadataFromDB(String email){
		System.out.println(email);
		JSONArray data = new JSONArray();
		JSONObject result = new JSONObject();
		try{
			MongoClient mongo = new MongoClient( ipaddress , 27017 );
			DB db = mongo.getDB("metadata");
			DBCollection table = db.getCollection("details");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("username", email);
			System.out.println("emailid in view objects: "+email);
			table.find(whereQuery);
			DBCursor cursor = table.find(whereQuery); 
			while(cursor.hasNext()){
				DBObject getdata=cursor.next();
				System.out.println(getdata);
				String username = getdata.get("username").toString();
				String filename = getdata.get("filename").toString();
				String bucketname = getdata.get("bucketname").toString();
				String filesize = getdata.get("filesize").toString();
				String lastmodified = getdata.get("lastmodified").toString();
				String owner = getdata.get("owner").toString();
				String downloadlink = getdata.get("downloadlink").toString();
				String rating = getdata.get("rating").toString();

				JSONObject obj1 = new JSONObject();
				obj1.put("username", username);
				obj1.put("filename", filename);
				obj1.put("bucketname", bucketname);
				obj1.put("filesize", filesize);
				obj1.put("lastmodified", lastmodified);
				obj1.put("owner", owner);
				obj1.put("downloadlink", downloadlink);
				obj1.put("rating", rating);

				data.put(obj1) ;

			}
			result.put("entries", data);
			
			
			// get current storage size start
			table = db.getCollection("userstoragelimit");
			whereQuery = new BasicDBObject();
			whereQuery.put("username", email);
			table.find(whereQuery);
			DBCursor cur = table.find(whereQuery); 
			DBObject getval;
			while(cur.hasNext()){
				getval=cur.next();
				result.put("currentsize",getval.get("currentsize"));
				result.put("limit",getval.get("limit"));
			}
			//get current storage size end
			
			System.out.println("final object::  "+result);
		}catch(Exception e){

		}
		return result.toString();
	}


	public String downloadS3BucketObjects(String username,String objectname){
		String downloadlink=null;
		try{
			String bucketName=getBucketName(username);
			AmazonS3Client awss3client=new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
			downloadlink=awss3client.getResourceUrl(bucketName, objectname);
			System.out.println("downloadlink for "+objectname+ " : " +downloadlink);
		}catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon S3, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with S3, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}catch(Exception e){
			e.printStackTrace();
		}
		return downloadlink;
	}



	public String deleteS3BucketObjects(String username,String objectname){
		String response="fail";
		try{

			String res=deleteObjectFromDB(username,objectname);
			System.out.println("object deleted from db");
			if(res.equals("success")){
				String bucketName=getBucketName(username);
				System.out.println("in delete bucket objects method:: "+bucketName);

				AmazonS3Client awss3client=new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
				awss3client.deleteObject(bucketName, objectname);
				logActivity(username,username+" deleted a file named " +objectname+ " from ShareBox","File Delete");
				response="success";
			}			

		}catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon S3, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with S3, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}catch(Exception e){
			e.printStackTrace();
		}
		return response;
	}


	public String deleteObjectFromDB(String useremail,String objectname){
		String response="fail";
		try{
			MongoClient mongo = new MongoClient( ipaddress , 27017 );
			DB db = mongo.getDB("metadata");
			DBCollection table = db.getCollection("details");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("username", useremail);
			whereQuery.put("filename", objectname);
//			table.remove(whereQuery);

			// storage limit start
			DBCursor cur=table.find(whereQuery);
			DBObject getval;
			double oldObjectSize=0.00;
			while(cur.hasNext()){
				getval=cur.next();
				oldObjectSize=Double.parseDouble(getval.get("filesize").toString());
			}
			
			table.remove(whereQuery);
			table = db.getCollection("userstoragelimit");
			BasicDBObject newDocument = new BasicDBObject();
			BasicDBObject searchQuery = new BasicDBObject().append("username", useremail);
			DBCursor cursor=table.find(searchQuery);
			DBObject getdata;
			double currsize=0.00;
			while(cursor.hasNext()) {
				getdata=cursor.next();
				currsize=Double.parseDouble(getdata.get("currentsize").toString());
			}
			double newCurrSize=currsize-oldObjectSize;
			newDocument.append("$set", new BasicDBObject().append("currentsize", newCurrSize));
			searchQuery = new BasicDBObject().append("username", useremail);
			table.update(searchQuery, newDocument);

			//storage limit end



			response="success";
		}catch(Exception e){
			e.printStackTrace();
		}
		return response;
	}


	public JSONObject getFileMetadataFromDB(String email,String ufilename,String receiveruseremail){
		System.out.println(email);
		JSONObject obj1=new JSONObject();
		try{
			MongoClient mongo = new MongoClient( ipaddress , 27017 );
			DB db = mongo.getDB("metadata");
			DBCollection table = db.getCollection("details");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("username", email);
			whereQuery.put("filename", ufilename);
			table.find(whereQuery);
			DBCursor cursor = table.find(whereQuery); 
			while(cursor.hasNext()){
				DBObject getdata=cursor.next();
				System.out.println(getdata);
				String username = getdata.get("username").toString();
				String filename = getdata.get("filename").toString();
				String bucketname = getdata.get("bucketname").toString();
				String filesize = getdata.get("filesize").toString();
				String lastmodified = getdata.get("lastmodified").toString();
				String owner = getdata.get("owner").toString();
				String downloadlink = getdata.get("downloadlink").toString();
				String rating = getdata.get("rating").toString();

				obj1 = new JSONObject();
				obj1.put("username", username);
				obj1.put("filename", filename);
				obj1.put("bucketname", bucketname);
				obj1.put("filesize", filesize);
				obj1.put("lastmodified", lastmodified);
				obj1.put("owner", owner);
				obj1.put("downloadlink", downloadlink);
				obj1.put("rating", rating);
				obj1.put("receiver", receiveruseremail);

			}

		}catch(Exception e){

		}
		return obj1;
	}

	public String shareS3BucketObjects(String username,String objectname,String receiveruseremail){
		String result="fail";
		try{
			JSONObject jobj=new JSONObject();
			jobj=getFileMetadataFromDB(username,objectname,receiveruseremail);
			jobj.put("receiveruseremail",receiveruseremail);
			MongoClient mongo = new MongoClient( ipaddress , 27017 );
			DB db = mongo.getDB("metadata");
			DBCollection table = db.getCollection("shareFilesData");
			BasicDBObject document = new BasicDBObject();
			document.putAll((DBObject)JSON.parse(jobj.toString()));
			System.out.println("document--> "+document);
			table.insert(document);
			System.out.println("jobj--> "+jobj);
			result="success";

			AmazonSESSample aSESObj=new AmazonSESSample();
			String response=aSESObj.sendFileShareMail(username,receiveruseremail,jobj.getString("downloadlink"));
			if(!response.equals("success")){
				result="fail";
			}
			logActivity(username,username+" shared a file named " +objectname+ " with "+receiveruseremail,"Share a File");

		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	public String rateFile(String username,String objectname,int rating) throws Exception{
		JSONObject result=new JSONObject();
		String res="fail";
		try{
			MongoClient mongo = new MongoClient( ipaddress , 27017 );
			DB db = mongo.getDB("metadata");
			DBCollection table = db.getCollection("details");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("username", username);
			whereQuery.put("filename", objectname);
			System.out.println("after where set");
			BasicDBObject replaceQuery = new BasicDBObject();
			replaceQuery.append("$set", new BasicDBObject().append("rating", rating));

			table.update(whereQuery, replaceQuery);
			System.out.println("after update in db");
			result.put("rating", rating);
			logActivity(username,username+" changed the importance of a file named " +objectname +" in ShareBox","File Importance Change");
			res="success";
		}catch(Exception e){
			e.printStackTrace();
		}
		result.put("Msg", res);

		return result.toString();
	}

	public String viewSharedBucketObjects(String useremail)throws Exception{
		JSONObject result=new JSONObject();
		try{
			MongoClient mongo = new MongoClient( ipaddress , 27017 );
			DB db = mongo.getDB("metadata");
			DBCollection table = db.getCollection("shareFilesData");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("receiveruseremail", useremail);
			System.out.println("after where set: "+whereQuery);
			DBCursor cursor=table.find(whereQuery);
			DBObject getdata;
			JSONArray jarray=new JSONArray();
			JSONObject jobj;
			while(cursor.hasNext()){
				getdata=cursor.next();

				jobj=new JSONObject();
				jobj.put("filename",getdata.get("filename"));
				jobj.put("filesize",getdata.get("filesize"));
				jobj.put("owner",getdata.get("owner"));
				jobj.put("downloadlink",getdata.get("downloadlink"));

				jarray.put(jobj);
			}
			result.put("sharedfiles", jarray);
			result.put("Msg", "success");
		}catch(Exception e){
			result.put("Msg", "fail");
			e.printStackTrace();
		}


		return result.toString();
	}


	public String viewSharedByMeBucketObjects(String useremail)throws Exception{
		JSONObject result=new JSONObject();
		try{
			MongoClient mongo = new MongoClient( ipaddress , 27017 );
			DB db = mongo.getDB("metadata");
			DBCollection table = db.getCollection("shareFilesData");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("username", useremail);
			System.out.println("after where set: "+whereQuery);
			DBCursor cursor=table.find(whereQuery);
			DBObject getdata;
			JSONArray jarray=new JSONArray();
			JSONObject jobj;
			while(cursor.hasNext()){
				getdata=cursor.next();

				jobj=new JSONObject();
				jobj.put("filename",getdata.get("filename"));
				jobj.put("filesize",getdata.get("filesize"));
				jobj.put("receiver",getdata.get("receiveruseremail"));
				jobj.put("downloadlink",getdata.get("downloadlink"));

				jarray.put(jobj);
			}
			result.put("sharedfiles", jarray);
			result.put("Msg", "success");
		}catch(Exception e){
			result.put("Msg", "fail");
			e.printStackTrace();
		}


		return result.toString();
	}

	public String deleteSharedBucketObjects(String receiveremail,String objectname){
		String response="fail";
		try{

			String res=deleteSharedObjectFromDB(receiveremail,objectname);
			System.out.println("object deleted from db");
			if(res.equals("success")){
				response="success";
			}			
			logActivity(receiveremail,receiveremail+" deleted a shared file named "+objectname,"Delete Shared File");
		}catch(Exception e){
			e.printStackTrace();
		}
		return response;
	}


	public String deleteSharedObjectFromDB(String receiveremail,String objectname){
		String response="fail";
		try{
			MongoClient mongo = new MongoClient( ipaddress , 27017 );
			DB db = mongo.getDB("metadata");
			DBCollection table = db.getCollection("shareFilesData");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("receiveruseremail", receiveremail);
			whereQuery.put("filename", objectname);
			table.remove(whereQuery);
			response="success";
		}catch(Exception e){
			e.printStackTrace();
		}
		return response;
	}

	public String viewUserLogs(String useremail) throws Exception{
		JSONObject result=new JSONObject();
		try{
			MongoClient mongo = new MongoClient( ipaddress , 27017 );
			DB db = mongo.getDB("student");
			DBCollection table = db.getCollection("logactivities");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("email", useremail);
			System.out.println("after where set: "+whereQuery);
			DBCursor cursor=table.find(whereQuery);
			DBObject getdata;
			JSONArray jarray=new JSONArray();
			JSONObject jobj;
			while(cursor.hasNext()){
				getdata=cursor.next();

				jobj=new JSONObject();
				jobj.put("activity",getdata.get("activity"));
				jobj.put("details",getdata.get("details"));
				jobj.put("timestmp",getdata.get("timestmp"));

				jarray.put(jobj);
			}
			result.put("viewlogs", jarray);
			result.put("Msg", "success");
			System.out.println("viewlogs result: "+result);
		}catch(Exception e){
			result.put("Msg", "fail");
			e.printStackTrace();
		}
		return result.toString();
	}

	public static void main(String args[]) throws Exception{
		/*MongoClient mongo = new MongoClient( "54.187.22.200" , 27017 );
		DB db = mongo.getDB("student");
		DBCollection table = db.getCollection("details");
		table.remove(new BasicDBObject()); 
		table = db.getCollection("logactivities");
		table.remove(new BasicDBObject()); 
		db = mongo.getDB("metadata");
		table.remove(new BasicDBObject()); 
		table = db.getCollection("details");
		table.remove(new BasicDBObject()); 
		table = db.getCollection("shareFilesData");
		table.remove(new BasicDBObject()); 
		table = db.getCollection("userstoragelimit");
		table.remove(new BasicDBObject()); */
	}
}
