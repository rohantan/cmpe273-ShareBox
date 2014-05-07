package com.acumen.sharebox.javaversion.rest;

import java.io.IOException;

import com.amazonaws.services.simpleemail.*;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.*;

public class AmazonSESSample {
	public String sendRegisterMail(String firstname,String username,String password) throws IOException{
		String result="failure";

		final String FROM ="shareboxteam@gmail.com";  
		final String TO = username;
		final String BODY = "Dear "+firstname+","+"\n\n"+"You have successfully subscribed to ShareBox" +
				"\n" + " your username is : " +username + "\n "+ "your password is : "+password
				+"\n \n" + "Thank you for registering with us!"+"\n\n"+"Regards,\n ShareBox Team";
		final String SUBJECT = "ShareBOX Registration Confirmation";

		PropertiesCredentials credentials = new PropertiesCredentials(
				AmazonSESSample.class
				.getResourceAsStream("AwsCredentials.properties"));

		credentials.getAWSAccessKeyId();
		credentials.getAWSSecretKey();

		Destination destination = new Destination().withToAddresses(new String[]{TO});

		Content subject = new Content().withData(SUBJECT);
		Content textBody = new Content().withData(BODY); 
		Body body = new Body().withText(textBody);

		Message message = new Message().withSubject(subject).withBody(body);

		SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
		try
		{        
			System.out.println("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");

			AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(credentials);

			Region REGION = Region.getRegion(Regions.US_WEST_2);
			client.setRegion(REGION);

			client.sendEmail(request);  
			System.out.println("Email sent!");
			result="success";
		}
		catch (Exception ex) 
		{
			System.out.println("The email was not sent.");
			System.out.println("Error message: " + ex.getMessage());
		}
		return result;
	}
	
	
	public String sendFileShareMail(String senderemail,String useremail,String downloadlink) throws IOException{
		String result="failure";

		final String FROM ="shareboxteam@gmail.com";  
		final String TO = useremail;
		final String BODY = "Hi,"+"\n"+ senderemail+" has shared a file with you on ShareBox" +
				"\n" + " File download link : " +downloadlink
				+"\n \n" +"Regards,\n ShareBox Team";
		final String SUBJECT = "ShareBOX - "+senderemail+" shared a file with you";

		PropertiesCredentials credentials = new PropertiesCredentials(
				AmazonSESSample.class
				.getResourceAsStream("AwsCredentials.properties"));

		credentials.getAWSAccessKeyId();
		credentials.getAWSSecretKey();

		Destination destination = new Destination().withToAddresses(new String[]{TO});

		Content subject = new Content().withData(SUBJECT);
		Content textBody = new Content().withData(BODY); 
		Body body = new Body().withText(textBody);

		Message message = new Message().withSubject(subject).withBody(body);

		SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
		try
		{        
			System.out.println("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");

			AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(credentials);

			Region REGION = Region.getRegion(Regions.US_WEST_2);
			client.setRegion(REGION);

			client.sendEmail(request);  
			System.out.println("Email sent!");
			result="success";
		}
		catch (Exception ex) 
		{
			System.out.println("The email was not sent.");
			System.out.println("Error message: " + ex.getMessage());
		}
		return result;
	}
	
}
