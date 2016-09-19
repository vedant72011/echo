package com.optum.poc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.optum.db.DrugInfoManager;
import com.optum.db.PersonInfoManager;

public class DrugInfoSpeechlet implements Speechlet {

    private AmazonDynamoDBClient amazonDynamoDBClient;
    private DrugInfoManager drugInfoManager;
    private PersonInfoManager personInfoManager;
    
    private static final Logger log = LoggerFactory.getLogger(DrugInfoSpeechlet.class);
	
	public void onSessionStarted(SessionStartedRequest request, Session session)
			throws SpeechletException {

        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
		
        initializeComponents();

	}

	public SpeechletResponse onLaunch(LaunchRequest request, Session session)
			throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        
        return drugInfoManager.welcome();
	}
	
	public SpeechletResponse onIntent(IntentRequest request, Session session)
			throws SpeechletException {

		log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        //initializeComponents();

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;
        
        
        if ("IsFormularyIntent".equals(intentName)) {
            return drugInfoManager.getIsFormularyIntentResponse(intent, session);

        }else if ("Api".equals(intentName)) {
            return drugInfoManager.getApiIntentResponse(intent, session);

        }
        else if ("DescriptionIntent".equals(intentName)) {
            return drugInfoManager.getDescriptionIntentResponse(intent, session);

        } else if ("HaveRequirementsIntent".equals(intentName)) {
            return drugInfoManager.getRequirementsIntentResponse(intent, session);

        } else if ("HavePreauthIntent".equals(intentName)) {
            return drugInfoManager.getPreAuthIntentResponse(intent, session);

        } else if ("DrugTierIntent".equals(intentName)) {
            return drugInfoManager.getDrugTierIntentResponse(intent, session);

        } else if ("CostRangeIntent".equals(intentName)) {
            return drugInfoManager.getCostDescriptionIntentResponse(intent, session);

        } else if ("DrugClassIntent".equals(intentName)) {
            return drugInfoManager.getDrugClassIntentResponse(intent, session);

        } else if ("IsBrandNameIntent".equals(intentName)) {
            return drugInfoManager.getIsBrandNameIntentResponse(intent, session);

        } else if ("IsSpecialityDrugIntent".equals(intentName)) {
            return drugInfoManager.getIsSpecialityDrugIntentResponse(intent, session);

        }/*else if("SendEmailIntent".equals(intentName)){
        	return drugInfoManager.getSendEmailIntentResponse(intent, session);
        }
        else if("SendSMSIntent".equals(intentName)){
        	return drugInfoManager.getSendSMSIntentResponse(intent, session);
        }
		else if("SendEmailSMSIntent".equals(intentName)){
			return drugInfoManager.getSendEmailSMSIntentResponse(intent, session);
		}*/
        else if ("EndOptum".equals(intentName)){
        	return drugInfoManager.getEndOptumIntentResponse(intent, session, intentName);
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
        	return drugInfoManager.welcome();
        }
        else if("PhraseIntent".equals(intentName)){
        	return drugInfoManager.getPhraseIntentResponse(intent, session, intentName);        	
        }
        
        else if("PersonSendEmailIntent".equals(intentName)){
        	return personInfoManager.getPersonSendEmailIntentResponse(intent, session);
        }
        else if("PersonSendSMSIntent".equals(intentName)){
        	return personInfoManager.getPersonSendSMSIntentResponse(intent, session);
        }
		else if("PersonSendEmailSMSIntent".equals(intentName)){
			return personInfoManager.getPersonSendEmailSMSIntentResponse(intent, session);
		}
		else if("DescribePersonIntent".equals(intentName)){
			return personInfoManager.getDescribePersonIntentResponse(intent, session);
		}
		else if("HasCoverageIntent".equals(intentName)){
			return personInfoManager.getHasCoverageIntentResponse(intent, session);
		}
		else if("RecentClaimsIntent".equals(intentName)){
			return personInfoManager.getRecentClaimsIntentResponse(intent, session);
		}
    
    	else {
            throw new IllegalArgumentException("Unrecognized intent: " + intent.getName());
            //throw new SpeechletException("Invalid Intent");
        }
	}




	public void onSessionEnded(SessionEndedRequest request, Session session)
			throws SpeechletException {
		
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

	}


	private void initializeComponents() {

        if (amazonDynamoDBClient == null) {
            amazonDynamoDBClient = new AmazonDynamoDBClient();
            drugInfoManager = new DrugInfoManager(amazonDynamoDBClient);
            personInfoManager = new PersonInfoManager(amazonDynamoDBClient);
        }
    }
}
