package com.optum.db;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.optum.poc.AmazonSESSample;
import com.optum.poc.DrugInfoDynamoDbClient;
import com.optum.poc.HTTPClient;



public class DrugInfoManager {

    private static final String SLOT_FORMULARY = "Formulary";
    private static final String SLOT_PHRASE = "Phrase";
    private static final String SLOT_PEOPLE = "People";
    private final  DrugInfoDynamoDbClient dynamoDbClient;
    
    public DrugInfoManager(final AmazonDynamoDBClient amazonDynamoDbClient) {
        dynamoDbClient =
        		new DrugInfoDynamoDbClient(amazonDynamoDbClient);
        //scoreKeeperDao = new ScoreKeeperDao(dynamoDbClient);
    }
    
	   public static String getHTML(String urlToRead) throws Exception {
		      StringBuilder result = new StringBuilder();
		      URL url = new URL(urlToRead);
		      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		      conn.setRequestMethod("GET");
		      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		      String line;
		      while ((line = rd.readLine()) != null) {
		         result.append(line);
		      }
		      rd.close();
		      return result.toString();
		   }
	   
		/**
		 * Sends a POST request to the webservice to get a response and parses that response using ParsePingHops class.\
		 * After that it builds the relationship and writes it to the necessary files
		 * @param link
		 * @throws Exception
		 */
		public String sendPost(String link) throws Exception {
			String url = link;//"http://u3c.uhc.com/upm3_gateway/upm3/optumrx/billingandpayment/paymentmethod/PostPaymentmethodV1?";
			int responseCode = 0;
			String responseContent = "";


				HttpClient httpClient = HttpClientBuilder.create().build();
				HttpPost httpPost = new HttpPost(url);

				String con = "{"+
					    "\"CustomerConfigRequest\": {"+
					        "\"requestHeader\":{"+
					            "\"consumerApplicationName\":\"APP_NAME\""+
					        "},"+
					        "\"contractDetails\": {"+
					            "\"contractNumber\" : \"0185002\""+
					        "},"+
					        "\"consumerEvent\": {"+
					            "\"eventName\" : \"MYUHC_SSO\","+
					            "\"eventDate\" : \"2014-04-27T00:00:00Z\""+
					        "}"+
					    "}"+
					"}";
				
				
/*				String con = "grant_type=client_credentials&client_id=l7xx1c6b17bde4e347b2b62e91d561fa3f50&client_secret=72640d99ea664dc088b0e8451723e9cf";*/

				StringEntity input = new StringEntity(con);
				input.setContentType("application/json");
				httpPost.setEntity(input);
				httpPost.addHeader("scope", "read");
				httpPost.addHeader("actor", "test");
				httpPost.addHeader("grant_type", "client_credentials");
				httpPost.addHeader("Authorization", "Bearer 98c649db-b994-4087-b7c8-5d0acbf357bb");
				
				HttpResponse response = httpClient.execute(httpPost);

				if (response != null && response.getStatusLine() != null)
				{
					responseCode = response.getStatusLine().getStatusCode();
					responseContent = EntityUtils.toString(response.getEntity());
				
					if(responseCode != 200){

						//System.out.println("Fault!!");
						return " fault";
					}
					else{
	/*					System.out.println(responseCode);
						System.out.println(responseContent);
						*/
						return " WON";
					}
				}
				else{
	/*				System.out.println("didnt get the response");*/
					return " didnt get the response";
				}

		}
    
    public SpeechletResponse welcome(){
    	
        // Speak welcome message and ask user questions
        // based on whether there are players or not.
        String speechText, repromptText;

        speechText = "Hi, this is Optum assistant. How can I help you?";
        
        
/*        try {
			speechText += " " + getHTML("https://api.optum.com:8443/F5/ping");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			speechText += " lost"; 
		}finally{
			if(!speechText.endsWith("lost")){
				speechText += " won";
			}
			else{
				speechText += " 1 2 3";
			}
		}*/
        
        
        //HTTPClient hc = new HTTPClient("","");
        try {
        	speechText +=  sendPost("https://api.optum.com:8443/api/config/wellness/routing/v1.0");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			speechText += " lost";
		}
        repromptText = "You can ask me about the information on various drugs.";
        return getAskSpeechletResponse(speechText, repromptText);
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

	public SpeechletResponse getIsFormularyIntentResponse(Intent intent,
			Session session) {

        String speechText, repromptText;

        String drugName = intent.getSlot(SLOT_FORMULARY).getValue();
       
        DrugInfoDataItem resultDataItem = getDataItemFromDB(drugName);
        
        if(resultDataItem == null){
        	speechText = "Sorry, I could not recognize what you mean";
            return getAskSpeechletResponse(speechText, speechText);
        }

        else{// do the query
        	boolean boo = resultDataItem.getFormulary();
        		if(boo){
        			speechText = "Yes, "+drugName+" is on formulary.";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}
        		else{
        			speechText = "No, "+drugName+" is not on formulary.";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}
        	

        	return getAskSpeechletResponse(speechText, repromptText);
        }
	}

	public SpeechletResponse getRequirementsIntentResponse(Intent intent,
			Session session) {

		String speechText, repromptText;

        String drugName = intent.getSlot(SLOT_FORMULARY).getValue();
        
        DrugInfoDataItem resultDataItem = getDataItemFromDB(drugName);
        
        if(resultDataItem == null){
        	speechText = "Sorry, I could not recognize what you mean";
            return getAskSpeechletResponse(speechText, speechText);
        }

        else{// do the query

            	String reqs = resultDataItem.getRequirements();
        		if(reqs == null || reqs.trim().length()==0){
        			speechText = drugName +" has no requirements.";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}
        		else{
        			List<String> list = Arrays.asList(reqs.split(","));
        			
        			if(list.size()==1){
        				speechText = reqs+" is the only requirement.";        	
            			repromptText = "Is there anything else you would like to ask?";
        			} else{
        				String ex = "";
        				for(int i=0; i<list.size()-1;i++){
        					ex += list.get(i).trim() +", ";
        				}
            			speechText = "There are "+ex.substring(0,ex.length()-2)+ " and "+ list.get(list.size()-1).trim() + " requirements.";        	
            			repromptText = "Is there anything else you would like to ask?";
        				
        			}
        		}
        	return getAskSpeechletResponse(speechText, repromptText);
        }
        
	}

	public SpeechletResponse getPreAuthIntentResponse(Intent intent,
			Session session) {
        String speechText, repromptText;

        String drugName = intent.getSlot(SLOT_FORMULARY).getValue();
        
        DrugInfoDataItem resultDataItem = getDataItemFromDB(drugName);
        
        if(resultDataItem == null){
        	speechText = "Sorry, I could not recognize what you mean";
            return getAskSpeechletResponse(speechText, speechText);
        }

        else{// do the query
  
        	boolean boo = resultDataItem.getPreAuth();
        		if(boo){
        			speechText = "Yes, "+drugName+" needs to be authorized.";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}
        		else{
        			speechText = "No, "+drugName+" does not need any prior authorization.";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}
        		
        		speechText += " what";
        	
        		if(Desktop.isDesktopSupported())
        		{
        			speechText += " Desktop identified!";
        		  try {
        			Desktop.getDesktop().browse(new URI("http://www.google.com"));
        			speechText += " Opening browser now.";
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} catch (URISyntaxException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		}

        	return getAskSpeechletResponse(speechText, repromptText);
        }
	}

	public SpeechletResponse getDrugTierIntentResponse(Intent intent,
			Session session) {
		String speechText, repromptText;

        String drugName = intent.getSlot(SLOT_FORMULARY).getValue();
        
        DrugInfoDataItem resultDataItem = getDataItemFromDB(drugName);
        
        if(resultDataItem == null){
        	speechText = "Sorry, I could not recognize what you mean";
            return getAskSpeechletResponse(speechText, speechText);
        }

        else{// do the query

        		int answer = resultDataItem.getDrugTier();
        		speechText =  drugName+" is a tier "+answer+" drug.";        	
        		repromptText = "Is there anything else you would like to ask?";
        		
        		return getAskSpeechletResponse(speechText, repromptText);
        }
	}

	public SpeechletResponse getCostDescriptionIntentResponse(Intent intent,
			Session session) {
		String speechText, repromptText;

        String drugName = intent.getSlot(SLOT_FORMULARY).getValue();
        
        DrugInfoDataItem resultDataItem = getDataItemFromDB(drugName);
        
        if(resultDataItem == null){
        	speechText = "Sorry, I could not recognize what you mean";
            return getAskSpeechletResponse(speechText, speechText);
        }

        else{// do the query

        		String answer = resultDataItem.getCostDescription();
        		if(answer == null || answer.trim().length()==0){
        			speechText = "I do not know the price range of "+ drugName+".";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}
        		else{
        			speechText = drugName+ " has a "+ answer +" cost.";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}

        		return getAskSpeechletResponse(speechText, repromptText);
        }

	}

	public SpeechletResponse getDrugClassIntentResponse(Intent intent,
			Session session) {
		String speechText, repromptText;

        String drugName = intent.getSlot(SLOT_FORMULARY).getValue();
        
        DrugInfoDataItem resultDataItem = getDataItemFromDB(drugName);
        
        if(resultDataItem == null){
        	speechText = "Sorry, I could not recognize what you mean";
            return getAskSpeechletResponse(speechText, speechText);
        }

        else{// do the query
        		String answer = resultDataItem.getDrugClass();
        		if(answer == null || answer.trim().length()==0){
        			speechText = "I do not know the drug class of "+ drugName+".";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}
        		else{
        			speechText = drugName+ " falls under "+ answer +" category.";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}

        	return getAskSpeechletResponse(speechText, repromptText);
        }
	}

	public SpeechletResponse getIsBrandNameIntentResponse(Intent intent,
			Session session) {
        String speechText, repromptText;

        String drugName = intent.getSlot(SLOT_FORMULARY).getValue();
        
        DrugInfoDataItem resultDataItem = getDataItemFromDB(drugName);
        
        if(resultDataItem == null){
        	speechText = "Sorry, I could not recognize what you mean";
            return getAskSpeechletResponse(speechText, speechText);
        }

        else{// do the query

        	boolean boo = resultDataItem.getIsBrandName();
        		if(boo){
        			speechText = "Yes, "+drugName+" is a brand name.";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}
        		else{
        			speechText = "No, "+drugName+" is not a brand name.";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}

        	return getAskSpeechletResponse(speechText, repromptText);
        }
	}

	public SpeechletResponse getIsSpecialityDrugIntentResponse(Intent intent,
			Session session) {
        String speechText, repromptText;

        String drugName = intent.getSlot(SLOT_FORMULARY).getValue();
        
        DrugInfoDataItem resultDataItem = getDataItemFromDB(drugName);
        
        if(resultDataItem == null){
        	speechText = "Sorry, I could not recognize what you mean";
            return getAskSpeechletResponse(speechText, speechText);
        }

        else{// do the query
        	
        	boolean boo = resultDataItem.getIsSpecialityDrug();
        		if(boo){
        			speechText = "You got it! Its a speciality drug.";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}
        		else{
        			speechText = "No, it not.";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}
        	

        	return getAskSpeechletResponse(speechText, repromptText);
        }
	}

	public SpeechletResponse getEndOptumIntentResponse(Intent intent,
			Session session, String intentName) {
		String speechText = "Goodbye!";
    	return getTellSpeechletResponse(speechText);
	}
	

	public SpeechletResponse getDescriptionIntentResponse(Intent intent,
			Session session) {

		String speechText, repromptText;

        String drugName = intent.getSlot(SLOT_FORMULARY).getValue();
        
        DrugInfoDataItem resultDataItem = getDataItemFromDB(drugName);
        
        if(resultDataItem == null){
        	speechText = "Sorry, I could not recognize what you mean";
            return getAskSpeechletResponse(speechText, speechText);
        }
        
        else{// do the query
        	
        	String answer = resultDataItem.getDescription();
        		if(answer == null || answer.trim().length()==0){
        			speechText = "Sorry, I dont know much about "+ drugName+".";        	
        			repromptText = "Is there anything else you would like to ask?";
        		}
        		else{
        			speechText = answer;        	
        			repromptText = "Is there anything else you would like to ask?";
        		}

        	return getAskSpeechletResponse(speechText, repromptText);
        }
	}
	
	
	private DrugInfoDataItem getDataItemFromDB(String drugName){
		
		if(drugName == null){
			return null;
		}
        else{// do the query
        	
        	drugName = drugName.toUpperCase();
        	
        	DrugInfoDataItem partitionKey = new DrugInfoDataItem();
        	partitionKey.setDrugName(drugName);
        	
        	return dynamoDbClient.loadItem(partitionKey);
        }
    }

	public SpeechletResponse getSendEmailIntentResponse(Intent intent,
			Session session) {
		
		String speechText, repromptText;

        String personName = intent.getSlot(SLOT_FORMULARY).getValue();
        
        DrugInfoDataItem resultDataItem = getDataItemFromDB(personName);
        
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

	public SpeechletResponse getSendSMSIntentResponse(Intent intent,
			Session session) {
		String speechText, repromptText;

        String personName = intent.getSlot(SLOT_FORMULARY).getValue();
        
        DrugInfoDataItem resultDataItem = getDataItemFromDB(personName);
        
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

	public SpeechletResponse getSendEmailSMSIntentResponse(Intent intent,
			Session session) {
		String speechText, repromptText;

        String personName = intent.getSlot(SLOT_FORMULARY).getValue();
        
        DrugInfoDataItem resultDataItem = getDataItemFromDB(personName);
        
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

	public SpeechletResponse getPhraseIntentResponse(Intent intent,
			Session session, String intentName) {
		String speechText, repromptText;

        String phrase = intent.getSlot(SLOT_PHRASE).getValue();
        if(phrase == null){
        	speechText = "Phrase is null.";
        }
        else if(phrase.length() == 0){
        	speechText = "Phrase is of length 0";
        }
        else{
        	speechText = phrase;
        }
        repromptText = "bye bye!";
        	return getAskSpeechletResponse(speechText, repromptText);

	}

	public SpeechletResponse getApiIntentResponse(Intent intent, Session session) {

		String speechText, repromptText;
        	
		String phrase = intent.getSlot("Number").getValue();
		String number = "";
		for(int i=0; i<phrase.length(); i++){
			number += phrase.charAt(i) + ", ";
		}
		number = number.substring(0,number.length()-2);
		
		//speechText = "Claim "+ phrase +" is currently being processed in the system. A check for 145 dollars will be sent on July 10";
		speechText = "Claim "+ number +" will be paid on August 17, 2016";
        repromptText = "Is there anything else you would like to ask?";
        		

        	return getAskSpeechletResponse(speechText, repromptText);
        
	}
}
