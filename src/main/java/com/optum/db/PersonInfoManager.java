package com.optum.db;

import java.io.IOException;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.optum.poc.AmazonSESSample;
import com.optum.poc.PersonInfoDataItem;
import com.optum.poc.PersonInfoDynamoDbClient;



public class PersonInfoManager {

    private static final String SLOT_PEOPLE = "People";
    private final  PersonInfoDynamoDbClient dynamoDbClient;
    
   public PersonInfoManager(final AmazonDynamoDBClient amazonDynamoDbClient) {
        dynamoDbClient =
        		new PersonInfoDynamoDbClient(amazonDynamoDbClient);
        //scoreKeeperDao = new ScoreKeeperDao(dynamoDbClient);
    }
        
    private SpeechletResponse getAskSpeechletResponse(String speechText, String repromptText) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Session");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText(repromptText);
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }
    
    private SpeechletResponse getTellSpeechletResponse(String speechText) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Session");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

	public SpeechletResponse getDescribePersonIntentResponse(Intent intent,
			Session session) {

        String speechText, repromptText;

        String personName = intent.getSlot(SLOT_PEOPLE).getValue();
       
        PersonInfoDataItem resultDataItem = getDataItemFromDB(personName);
        
        if(resultDataItem == null){
        	speechText = "Sorry, I could not recognize what you mean";
            return getAskSpeechletResponse(speechText, speechText);
        }
        
        else{// do the query
        	
        	String answer = resultDataItem.getDescription();
        		if(answer == null || answer.trim().length()==0){
        			speechText = "Sorry, I dont know much about "+ personName+".";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}
        		else{
        			speechText = answer;        	
        			repromptText = "Is there anything else you would like to ask?";
        		}

        	return getAskSpeechletResponse(speechText, repromptText);
        }
	}
	
	
	public SpeechletResponse getPersonSendEmailIntentResponse(Intent intent,
			Session session) {
		
		String speechText, repromptText;

        String personName = intent.getSlot(SLOT_PEOPLE).getValue();
        
        PersonInfoDataItem resultDataItem = getDataItemFromDB(personName);
        
        if(resultDataItem == null){
        	speechText = "Sorry, I could not recognize the person's name";
            return getAskSpeechletResponse(speechText, speechText);
        }
        
        else{// do the query
        	
        	String recepientEmailID = resultDataItem.getEmailID().trim();
        		if(recepientEmailID == null || recepientEmailID.trim().length()==0){
        			speechText = "Sorry, I dont have the email address of "+ personName+".";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}
        		else{
        			String emailSubject = "Message From Alexa!";
        			String emailText = "Hey "+personName+",\n"+"How is it going?";
        			
        			try {
						AmazonSESSample.sendEmail(emailSubject, emailText, recepientEmailID);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						speechText = "Sorry, I was unable to send the email.";
					}
        			
        			speechText = "Email has been sent to "+personName+".";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}

        	return getAskSpeechletResponse(speechText, repromptText);
        }
	}

	public SpeechletResponse getPersonSendSMSIntentResponse(Intent intent,
			Session session) {
		String speechText, repromptText;

        String personName = intent.getSlot(SLOT_PEOPLE).getValue();
        
        PersonInfoDataItem resultDataItem = getDataItemFromDB(personName);
        
        if(resultDataItem == null){
        	speechText = "Sorry, I could not recognize the person's name";
            return getAskSpeechletResponse(speechText, speechText);
        }
        
        else{// do the query
        	
        	String recepientCarrier = resultDataItem.getCarrier().trim();
        	String phoneNumber = resultDataItem.getPhoneNumber();
        	String recepientPhoneEmailID = phoneNumber+recepientCarrier;
        		if(recepientCarrier == null || recepientCarrier.trim().length()==0){
        			speechText = "Sorry, I dont have the email address of "+ personName+".";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}
        		else{
        			String emailSubject = "Message From Alexa!";
        			String emailText = "Hey "+personName+",\n"+"How is it going?";
        			
        			try {
						AmazonSESSample.sendEmail(emailSubject, emailText, recepientPhoneEmailID);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						speechText = "Sorry, I was unable to send the email.";
					}
        			
        			speechText = "Text has been sent to "+personName+".";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}

        	return getAskSpeechletResponse(speechText, repromptText);
        }
	}

	public SpeechletResponse getPersonSendEmailSMSIntentResponse(Intent intent,
			Session session) {
		String speechText, repromptText;

        String personName = intent.getSlot(SLOT_PEOPLE).getValue();
        
        PersonInfoDataItem resultDataItem = getDataItemFromDB(personName);
        
        if(resultDataItem == null){
        	speechText = "Sorry, I could not recognize the person's name";
            return getAskSpeechletResponse(speechText, speechText);
        }
        
        else{// do the query
        	
        	String recepientCarrier = resultDataItem.getCarrier().trim();
        	String phoneNumber = resultDataItem.getPhoneNumber();
        	String recepientPhoneEmailID = phoneNumber+recepientCarrier;
        	String recepientEmailID = resultDataItem.getEmailID().trim();
        		if(recepientCarrier == null || recepientCarrier.trim().length()==0 || recepientEmailID == null || recepientEmailID.trim().length()==0){
        			speechText = "Sorry, I dont have the email address of "+ personName+".";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}
        		else{
        			String emailSubject = "Message From Alexa!";
        			String emailText = "Hey "+personName+",\n"+"How is it going?";
        			
        			try {
						AmazonSESSample.sendEmail(emailSubject, emailText, recepientEmailID);
						AmazonSESSample.sendEmail(emailSubject, emailText, recepientPhoneEmailID);
        			} catch (IOException e) {
						// TODO Auto-generated catch block
						speechText = "Sorry, I was unable to send the email.";
					}
        			
        			speechText = "Email and text has been sent to "+personName+".";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}

        	return getAskSpeechletResponse(speechText, repromptText);
        }
	}
	
	private PersonInfoDataItem getDataItemFromDB(String personName){
		
		if(personName == null){
			return null;
		}
        else{// do the query
        	
        	personName = personName.toUpperCase();
        	
        	PersonInfoDataItem partitionKey = new PersonInfoDataItem();
        	partitionKey.setPersonName(personName);
        	
        	return dynamoDbClient.loadItem(partitionKey);
        }
    }

	public SpeechletResponse getHasCoverageIntentResponse(Intent intent,
			Session session) {
		String speechText, repromptText;

		String personName = intent.getSlot(SLOT_PEOPLE).getValue();

		speechText = personName + " has active UHC medical coverage from January 1 to December 31 2016";        	
		repromptText = "Is there anything else you would like to ask?";

		return getAskSpeechletResponse(speechText, repromptText);
	}

	public SpeechletResponse getRecentClaimsIntentResponse(Intent intent,
			Session session) {
		String speechText, repromptText;

		String personName = intent.getSlot(SLOT_PEOPLE).getValue();

		speechText = "A claim was submitted for " + personName + " with date of service on July 10. It is currently being processed and a payment will be made to Dr. Allen for 145 dollars";        	
		repromptText = "Is there anything else you would like to ask?";

		return getAskSpeechletResponse(speechText, repromptText);	}

}
