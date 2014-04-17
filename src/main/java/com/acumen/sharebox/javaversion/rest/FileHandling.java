package com.acumen.sharebox.javaversion.rest;

import java.io.File;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("files")
public class FileHandling{
private String useremail;

public String getUseremail() {
	return useremail;
}

public void setUseremail(String useremail) {
	this.useremail = useremail;
}

	@POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String uploadFile (
            @FormDataParam("file") File fileobject,
            @FormDataParam("file") FormDataContentDisposition contentDispositionHeader) throws Exception{
    	System.out.println("inside class uploadFile###################");
    	System.out.println("useremail::::::: "+this.getUseremail());
    	AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
    	System.out.println("filename: $$$$ "+contentDispositionHeader.getFileName());
    	String output=awsS3BucketHandling.addS3BucketObjects(fileobject,contentDispositionHeader);
    	JSONObject jsonObject=new JSONObject();
    	jsonObject.put("Msg", output);
    	return jsonObject.toString();
    }
    
    @GET
    @Path("/doAuthentication/{username}/{pwd}")
    public String doAuthentication(@PathParam("username") String username,@PathParam("pwd") String pwd) throws Exception{
    	this.setUseremail(username);
    	System.out.println("inside method doAuthentication###################");
    	System.out.println(username+" "+pwd);
    	AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
    	JSONObject jsonObject=new JSONObject();
    	jsonObject=awsS3BucketHandling.doAuthentication(username, pwd);
    	System.out.println("jsonObject.toString(): "+jsonObject.toString());
        return jsonObject.toString();
    }
    
    
    @POST
    @Path("/register/{firstname}/{lastname}/{username}/{pwd}")
    public String registerUser (@PathParam("firstname") String firstname,@PathParam("lastname") String lastname,@PathParam("username") String username, @PathParam("pwd") String pwd) throws Exception{
    	System.out.println("inside class registerUser###################");
    	AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
    	JSONObject jsonObject=new JSONObject();
    	jsonObject=awsS3BucketHandling.registerUser(firstname,lastname,username,pwd);
    	return jsonObject.toString();
    }
    
    @GET
    @Path("/view/{username}")
//    @Produces("text/html")
    public String viewObjects(@PathParam("username") String username){
    	System.out.println("inside view method######");
    	AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
    	String result= awsS3BucketHandling.viewS3BucketObjects(username);
    	System.out.println("inside REST /view");
    	return result;
    }
    
    /*@GET
    @Path("/storeToDB/{username}")
//    @Produces("text/html")
    public String storeToDB(@PathParam("username") String username) throws Exception{
    	System.out.println("inside storeToDB method######");
    	AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
    	String result= awsS3BucketHandling.addMetaDataToDbAfterS3(username);
    	System.out.println("inside REST /storeToDB");
    	return result;
    }*/
    
    @GET
    @Path("/download")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response downloadObjects(){
    	
    	String output="Files downloaded at location: E:\\S3DownloadFiles";
    	AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
    	output=awsS3BucketHandling.getS3BucketObjects("E:\\S3DownloadFiles\\");
    	System.out.println("OBJECT NAME::::: "+output);
    	System.out.println("inside REST /download");
    	return Response.status(200).entity(output).build();
    }
    
    @POST
    @Path("/uploadDirectory")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadDirectory(
            @PathParam("file") File fileobject) {
 
    	System.out.println("inside class uploadFile###################");
    	AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
    	System.out.println("filename: $$$$ "+fileobject.getName());
    	String output=awsS3BucketHandling.addFolderS3BucketObjects(fileobject,fileobject.getName());
        return Response.status(200).entity(output).build();
    }
    
    @DELETE
    @Path("/delete/{objectkey}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response deleteObject(
    		@PathParam("objectkey") String key){
    	AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
    	System.out.println("key::::::: "+key);
    	String output=awsS3BucketHandling.deleteS3BucketObjects(key);
    	return Response.status(200).entity(output).build();
    }
    
    @PUT
    @Path("/restore")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response restoreObject(
            @PathParam("objectkey") String key,
            @PathParam("expirationInDays") int expirationInDays) {
 
    	System.out.println("inside class uploadFile###################");
    	AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
    	String output=awsS3BucketHandling.restoreS3BucketObjects(key,expirationInDays);
        return Response.status(200).entity(output).build();
    }
    
}