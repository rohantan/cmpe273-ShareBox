package com.acumen.sharebox.javaversion.rest;

import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;

public class S3ObjectLinks {
	
	public void CreateBucket(String randomnumber){
		try{
			AmazonS3Client s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
			Region usWest2 = Region.getRegion(Regions.US_WEST_2);
			s3.setRegion(usWest2);
			String bucketName = "sharebox" + randomnumber;
			s3.createBucket(bucketName);

			System.out.println("Listing buckets");
			for (Bucket bucket : s3.listBuckets()) {
				if(bucket.getName().contains("sharebox"))
				System.out.println(" - " + bucket.getName());
			}
			/*System.out.println("Deleting bucket now......");
			s3.deleteBucket(bucketName);
			System.out.println("bucket "+bucketName+" deleted");*/
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
        }
	}
	
	/*public void S3ObjecttDetails(){
		S3Object objectDetailsOnly = S3Service.getObjectDetails(testBucket, "helloWorld.txt");
		System.out.println("S3Object, details only: " + objectDetailsOnly);
	}*/
	
	public static void main(String[] args) {
		S3ObjectLinks s3objlink=new S3ObjectLinks();
		s3objlink.CreateBucket(UUID.randomUUID().toString());
	}
	
}
