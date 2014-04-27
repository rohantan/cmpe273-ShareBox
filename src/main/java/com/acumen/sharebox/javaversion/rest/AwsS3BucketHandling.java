package com.acumen.sharebox.javaversion.rest;

import java.io.File;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.RestoreObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.sun.jersey.core.header.FormDataContentDisposition;

public class AwsS3BucketHandling {
private String ipaddress="54.187.22.200";
//private JSONObject dbObjectHolder=new JSONObject();	
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
				 origPasswd = getdata.get("password").toString();
  			  }
  			  
  			if(user.get("password").equals(origPasswd)){
          	  result.put("Msg", "success");
            }
            else{
          	  result.put("Msg", "fail");
            }
  			
              }catch (Exception e) { 
                  e.printStackTrace(); 
              } 
          return(result);
        }
	
	
	
	public JSONObject registerUser(String firstname,String lastname,String emailid,String pwd) throws Exception{
//		MongoClient mongo = new MongoClient( "localhost" , 27017 );
		System.out.println("in awss3 registeruser");
		JSONObject mystring = new JSONObject();
		mystring.put("firstname", firstname);
		mystring.put("lastname", lastname);
		mystring.put("email", emailid);
		mystring.put("password", pwd);
		AwsS3BucketHandling m1 = new AwsS3BucketHandling();
//		m1.mongo = mongo;
		JSONObject output = m1.addUser(mystring);
		System.out.println(output);
		
		return output;
	}
	
	
	public JSONObject addUser(JSONObject user) throws Exception{
		JSONObject result= new JSONObject();
		MongoClient mongo = new MongoClient( ipaddress , 27017 );
		System.out.println("in awss3 adduser..");
		try{
			boolean flag = false;
			BasicDBObject query = new BasicDBObject("firstname",user.get("firstname")).append("lastname", user.get("lastname")).append("email", user.get("email")).append("password", user.get("password"));
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
				result.put("Msg", "success");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return(result);
	}
	
	
	
	
	
	public String addS3BucketObjects( File fileobject,FormDataContentDisposition contentDispositionHeader) throws Exception{
		String response="fail";
		
	
		
		AmazonS3 s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		s3.setRegion(usWest2);
		String bucketName = "shareboxbucket";

		System.out.println("===========================================");
		System.out.println("Getting Started with Amazon S3");
		System.out.println("===========================================\n");

		try {
			System.out.println("Listing buckets");

			System.out.println("\nUploading a new object to S3...");
			System.out.println("upload file name:: "+contentDispositionHeader.getFileName());
			s3.putObject(new PutObjectRequest(bucketName, contentDispositionHeader.getFileName(), fileobject));
			System.out.println("File uploaded on S3 - location: "+bucketName+" -> "+contentDispositionHeader.getFileName());
			response="success";

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
	
	
	public String getBucketObjectDetailsFromDB(String username){
		String result=getMetadataFromDB(username);
		return result;
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
			System.out.println("final object::  "+result);
		}catch(Exception e){

		}
		return result.toString();
	}
	
	public String restoreS3BucketObjects(String key,int expirationInDays){
		String response="fail";
		AmazonS3 s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		s3.setRegion(usWest2);
		String bucketName = "shareboxbucket";

		System.out.println("===========================================");
		System.out.println("Getting Started with Amazon S3");
		System.out.println("===========================================\n");

		try {
			RestoreObjectRequest request = new RestoreObjectRequest(bucketName, key, 2);
			s3.restoreObject(request);

			response="success";
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

	
	

}
