package com.optum.poc;
/*
 * Copyright 2014-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

public class AmazonSESSample {

    static final String FROM = "vedant.patel@optum.com";  // Replace with your "From" address. This address must be verified.
    static String TO;//"vedant.patel@optum.com"; // Replace with a "To" address. If you have not yet requested
                                                      // production access, this address must be verified.
    static String BODY;//"This email was sent through Amazon SES by using the AWS SDK for Java.";
    static String SUBJECT;// = "Amazon SES test (AWS SDK for Java)";
    //static final String TO1 = "7329257853@tmomail.net";
    
    /*public static void main(String args[]){
    
    	TO = "vedant.patel@optum.com";
    			SUBJECT = "SUBJECT";
    			BODY = "Hey!\n"+"How is it going?";
    	try {
			sendEmail(SUBJECT, BODY, TO);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }*/
    
    
    /*
     * Before running the code:
     *      Fill in your AWS access credentials in the provided credentials
     *      file template, and be sure to move the file to the default location
     *      (C:\\Users\\vpatel18\\.aws\\credentials) where the sample code will load the
     *      credentials from.
     *      https://console.aws.amazon.com/iam/home?#security_credential
     *
     * WARNING:
     *      To avoid accidental leakage of your credentials, DO NOT keep
     *      the credentials file in your source directory.
     */

    public static void sendEmail(String emailSubject, String emailText, String emailRecepient) throws IOException {

    	BODY = emailText;
    	SUBJECT = emailSubject;
    	TO = emailRecepient;
        // Construct an object to contain the recipient address.
        Destination destination = new Destination().withToAddresses(new String[]{TO});

        // Create the subject and body of the message.
        Content subject = new Content().withData(SUBJECT);
        Content textBody = new Content().withData(BODY);
        Body body = new Body().withText(textBody);

        // Create a message with the specified subject and body.
        Message message = new Message().withSubject(subject).withBody(body);

        // Assemble the email.
        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);

        try {
            //System.out.println("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");

            /*
             * The ProfileCredentialsProvider will return your [default]
             * credential profile by reading from the credentials file located at
             * (C:\\Users\\vpatel18\\.aws\\credentials).
             *
             * TransferManager manages a pool of threads, so we create a
             * single instance and share it throughout our application.
             */
            AWSCredentials credentials = new BasicAWSCredentials("AKIAITFEIFLYTZ6V5ZJA", "z+DOJNFasX2qualaNoMLYp3CCAXhLmdmeVgaU42f");//null;
            
            //BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIAITFEIFLYTZ6V5ZJA", "z+DOJNFasX2qualaNoMLYp3CCAXhLmdmeVgaU42f");
            /*AmazonS3 s3Client = new AmazonS3Client(awsCreds);
            
            try {
                credentials = new ProfileCredentialsProvider("default").getCredentials();
            } catch (Exception e) {
                throw new AmazonClientException(
                        "Cannot load the credentials from the credential profiles file. " +
                        "Please make sure that your credentials file is at the correct " +
                        "location (C:\\Users\\vpatel18\\.aws\\credentials), and is in valid format.",
                        e);
            }*/

            // Instantiate an Amazon SES client, which will make the service call with the supplied AWS credentials.
            AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(credentials);

            // Choose the AWS region of the Amazon SES endpoint you want to connect to. Note that your production
            // access status, sending limits, and Amazon SES identity-related settings are specific to a given
            // AWS region, so be sure to select an AWS region in which you set up Amazon SES. Here, we are using
            // the US East (N. Virginia) region. Examples of other regions that Amazon SES supports are US_WEST_2
            // and EU_WEST_1. For a complete list, see http://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html
            Region REGION = Region.getRegion(Regions.US_EAST_1);
            client.setRegion(REGION);

            // Send the email.
            client.sendEmail(request);
            System.out.println("Email sent!");

        } catch (Exception ex) {
            //System.out.println("The email was not sent.");
            //System.out.println("Error message: " + ex.getMessage());
        }
    }
}
