package com.acumen.sharebox.javaversion.rest;

import java.io.File;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("files")
public class FileHandling{

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void uploadFile (
			@FormDataParam("file") File fileobject,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader,@QueryParam("userid") String userid) throws Exception{

		String output="fail";
		System.out.println("inside class uploadFile###################");
		System.out.println("useremail::::::: "+userid);
		System.out.println("filename: $$$$ "+contentDispositionHeader.getFileName());
		
		System.out.println("filesize:: "+fileobject.length());
		
		if(fileobject.length()<524288000){
			System.out.println("within size limit!");
			AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
			output=awsS3BucketHandling.addS3BucketObjects(fileobject,contentDispositionHeader,userid);
		}else{
			output="oversize";
			System.out.println("OVERSIZE FILE!!");
		}

		JSONObject jsonObject=new JSONObject();
		jsonObject.put("Msg", output);
		try {
			if(output.equals("success")){
				java.net.URI location = new java.net.URI("../shareboxHome.html");
				throw new WebApplicationException(Response.temporaryRedirect(location).build());
			}else if(output.equals("oversize") || output.equals("Limit full")){
				java.net.URI location = new java.net.URI("../ErrorUploadOversize.html");
				throw new WebApplicationException(Response.temporaryRedirect(location).build());
			}
			else{
				java.net.URI location = new java.net.URI("../ErrorUpload.html");
				throw new WebApplicationException(Response.temporaryRedirect(location).build());
			}

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}	

	@GET
	@Path("/doAuthentication/{username}/{pwd}")
	public String doAuthentication(@PathParam("username") String username,@PathParam("pwd") String pwd) throws Exception{
		System.out.println("inside method doAuthentication###################");
		System.out.println(username+" "+pwd);
		AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
		JSONObject jsonObject=new JSONObject();
		jsonObject=awsS3BucketHandling.doAuthentication(username, pwd);
		System.out.println("jsonObject.toString(): "+jsonObject.toString());
		System.out.println("inside REST /authenticate");
		return jsonObject.toString();
	}


	@POST
	@Path("/register/{firstname}/{lastname}/{username}")
	public String registerUser (@PathParam("firstname") String firstname,@PathParam("lastname") String lastname,@PathParam("username") String username) throws Exception{
		System.out.println("inside registerUser###################");
		AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
		JSONObject jsonObject=new JSONObject();
		jsonObject=awsS3BucketHandling.registerUser(firstname,lastname,username);
		System.out.println("inside REST /register");
		return jsonObject.toString();
	}

	@GET
	@Path("/view/{username}")
	public String viewObjects(@PathParam("username") String username){
		System.out.println("inside view method######");
		AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
		String result= awsS3BucketHandling.viewS3BucketObjects(username);
		System.out.println("inside REST /view");
		return result;
	}

	@GET
	@Path("/download/{username}/{objectname}")
	public String downloadObjects(@PathParam("username") String username,@PathParam("objectname") String objectname) throws Exception{
		System.out.println("inside download method######");
		AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
		String result= awsS3BucketHandling.downloadS3BucketObjects(username,objectname);
		JSONObject s3objlink=new JSONObject();
		s3objlink.put("downloadlink", result);
		if(result!=null){
			s3objlink.put("Msg", "success");
		}
		else{
			s3objlink.put("Msg", "fail");
		}
		System.out.println("inside REST /downloadlink");
		return result;
	}

	@DELETE
	@Path("/{username}/{objectname}")
	public String deleteS3Objects(@PathParam("username") String username,@PathParam("objectname") String objectname) throws Exception{
		System.out.println("inside delete method######");
		String result="fail";
		AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
		result= awsS3BucketHandling.deleteS3BucketObjects(username,objectname);
		JSONObject s3objlink=new JSONObject();
		s3objlink.put("Msg", result);
		System.out.println("inside REST /deleteobject");
		return result;
	}


	@POST
	@Path("/rateFile/{username}/{objectname}/{rating}")
	public String rateFile(@PathParam("username") String username,@PathParam("objectname") String objectname,@PathParam("rating") int rating) throws Exception{
		System.out.println("inside ratefile method######");
		String result="fail";
		AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
		result= awsS3BucketHandling.rateFile(username,objectname,rating);
		System.out.println("inside REST /rate");
		return result;
	}

	@POST
	@Path("/share/{username}/{objectname}/{receiveruseremail}")
	public String shareS3Objects(@PathParam("username") String username,@PathParam("objectname") String objectname,@PathParam("receiveruseremail") String receiveruseremail) throws Exception{
		System.out.println("inside share method######");
		String result="fail";
		AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
		result= awsS3BucketHandling.shareS3BucketObjects(username,objectname,receiveruseremail);
		JSONObject s3objlink=new JSONObject();
		s3objlink.put("Msg", result);
		System.out.println("inside REST /share");
		return result;
	}

	@GET
	@Path("/viewshared/{username}")
	public String viewsharedObjects(@PathParam("username") String username) throws Exception{
		System.out.println("inside viewsharedObjects method######");
		AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
		String result= awsS3BucketHandling.viewSharedBucketObjects(username);
		System.out.println("inside REST /viewshared");
		return result;
	}
	
	@GET
	@Path("/viewsharedbyme/{username}")
	public String viewsharedByMeObjects(@PathParam("username") String username) throws Exception{
		System.out.println("inside viewsharedByMeObjects method######");
		AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
		String result= awsS3BucketHandling.viewSharedByMeBucketObjects(username);
		System.out.println("inside REST /viewsharedbyme");
		return result;
	}

	@DELETE
	@Path("shared/{username}/{objectname}")
	public String deleteSharedObjects(@PathParam("username") String username,@PathParam("objectname") String objectname) throws Exception{
		System.out.println("inside delshared method######");
		String result="fail";
		AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
		result= awsS3BucketHandling.deleteSharedBucketObjects(username,objectname);
		JSONObject s3objlink=new JSONObject();
		s3objlink.put("Msg", result);
		System.out.println("inside REST /deleteshared");
		return result;
	}

	@GET
	@Path("/viewlogs/{username}")
	public String viewLogs(@PathParam("username") String username) throws Exception{
		System.out.println("inside viewlogs method######");
		AwsS3BucketHandling awsS3BucketHandling=new AwsS3BucketHandling();
		String result= awsS3BucketHandling.viewUserLogs(username);
		System.out.println("inside REST /viewlogs");
		return result;
	}
	
}