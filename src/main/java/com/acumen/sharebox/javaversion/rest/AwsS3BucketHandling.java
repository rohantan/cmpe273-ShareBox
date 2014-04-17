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
	
	
	
	
	//original- do not delete
	/*public String addS3BucketObjects( File fileobject,String key){
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
			System.out.println("upload file name:: "+key);
			s3.putObject(new PutObjectRequest(bucketName, key, fileobject));
			System.out.println("File uploaded on S3 - location: "+bucketName+" -> "+key);
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
	}*/
	
	public String addS3BucketObjects( File fileobject,FormDataContentDisposition contentDispositionHeader) throws Exception{
		String response="fail";
		
		//create json object for db-start
		
		/*dbObjectHolder.put("filename",contentDispositionHeader.getFileName());
		dbObjectHolder.put("filesize",contentDispositionHeader.getSize());
		dbObjectHolder.put("lastmodified",contentDispositionHeader.getModificationDate());*/
		//create json object for db-end
		
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
	
	
	public String getMetadataFromDB(String email){
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
				  String username = getdata.get("username").toString();
				  String filename = getdata.get("filename").toString();
				  String bucketname = getdata.get("bucketname").toString();
				  String filesize = getdata.get("filesize").toString();
				  String lastmodified = getdata.get("lastmodified").toString();
				  String owner = getdata.get("owner").toString();
				  
				  JSONObject obj1 = new JSONObject();
				  obj1.put("username", username);
				  obj1.put("filename", filename);
				  obj1.put("bucketname", bucketname);
				  obj1.put("filesize", filesize);
				  obj1.put("lastmodified", lastmodified);
				  obj1.put("owner", owner);
				  
				  data.put(obj1) ;
				  
			  }
			  result.put("entries", data);
			  System.out.println("final object::  "+data);
		}catch(Exception e){
			
		}
		return result.toString();
	}
	
	public String getS3BucketObjects(String localStoragePath){
		String response="fail";
		AmazonS3 s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		s3.setRegion(usWest2);
		String bucketName = "shareboxbucket";
		System.out.println("\nDownloading an object...");

		System.out.println("===========================================");
		System.out.println("Getting Started with Amazon S3");
		System.out.println("===========================================\n");
		GetObjectRequest getobjreq=null;
		try{
			ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
			.withBucketName(bucketName));
			for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				System.out.println(" \t -> " + objectSummary.getKey() + "  " +
						"(Size = " + objectSummary.getSize() + ")"+
						"(Owner = " + objectSummary.getOwner().getDisplayName() + ")" +
						"(LastModified = " + objectSummary.getLastModified() + ")" +
						"(BucketName = " + objectSummary.getBucketName() + ")"
						);

				getobjreq=new GetObjectRequest(bucketName, objectSummary.getKey());
				File fin=new File(localStoragePath+objectSummary.getKey());
				s3.getObject(getobjreq,fin);
				s3.getObject(getobjreq);
				System.out.println("Object "+ objectSummary.getKey() +" downloaded to "+fin.getPath());

			}
			System.out.println("after for loop!!!!!!!! ");
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

	public String addFolderS3BucketObjects(File directory,String key){
		String response="fail";
		AmazonS3 s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		s3.setRegion(usWest2);
		String bucketName = "shareboxbucket";

		System.out.println("===========================================");
		System.out.println("Getting Started with Amazon S3");
		System.out.println("===========================================\n");

		try {
			TransferManager tm=new TransferManager(s3);
			tm.uploadDirectory(bucketName, key, directory, true);
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

	public String deleteS3BucketObjects(String key){
		String response="fail";
		AmazonS3 s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		s3.setRegion(usWest2);
		String bucketName = "shareboxbucket";

		System.out.println("===========================================");
		System.out.println("Getting Started with Amazon S3");
		System.out.println("===========================================\n");

		try {
			s3.deleteObject(new DeleteObjectRequest(bucketName, key));
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

	
	

	public static void main(String args[]) throws Exception{
		AwsS3BucketHandling ws=new AwsS3BucketHandling();
		/*JSONObject result=ws.doAuthentication("shriyanshjain@gmail.com","jain");
		System.out.println(result);*/
//		ws.getMetadataFromDB("shriyanshjain@gmail.com");
//		String res=ws.viewS3BucketObjects();
//		ws.registerUser("rohan", "tan","rohantan@gmail.com","rohan");
//		System.out.println(res);
		//		File  directory=new File("E:\\S3UploadFiles");
		//		String response=ws.addFolderS3BucketObjects(directory,directory.getName());
		//		String response=ws.deleteS3BucketObjects("python_tutorial.pdf");
		//		String response=ws.getS3BucketObjects("E:\\S3DownloadFiles\\");
		//		String response=ws.restoreS3BucketObjects("cmpe273-lec6-REST.pdf",2);
		//		System.out.println("response: "+response);
	}

}
