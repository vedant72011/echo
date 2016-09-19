package com.optum.poc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;


/**
 * 
 * @author Vedant Patel
 *
 */

/**
 * Sends a post reqeust to the webservice and parses the response to get names of foundation views
 */
public class HTTPClient {

	private final String USER_AGENT = "Chrome/49.0.2623.87";
	private String sourceFileName ="";
	private ArrayList<String> foundationViewNames = null;

	public HTTPClient(String url, String sourceFileName){
		try {
			this.sourceFileName = sourceFileName;
			sendPost(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// HTTP GET request
	private void sendGet() throws Exception {

		String url = "http://u3c.uhc.com/upm3_gateway/upm3/optumrx/billingandpayment/paymentmethod/PostPaymentmethodV1?wsdl";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();


		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();



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

/*			String con = "{"+
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
				"}";*/
			
			
			String con = "grant_type=client_credentials&client_id=l7xx1c6b17bde4e347b2b62e91d561fa3f50&client_secret=72640d99ea664dc088b0e8451723e9cf";

			StringEntity input = new StringEntity(con);
			input.setContentType("application/x-www-form-urlencoded");
			httpPost.setEntity(input);
			httpPost.addHeader("scope", "read");
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
					return " GOOD";
				}
			}
			else{
/*				System.out.println("didnt get the response");*/
				return " didnt get the response";
			}

	}
	
	
	public ArrayList<String> getFoundationViewNames(){
		return this.foundationViewNames;
	}
	
	private void setFoundationViewNames(ArrayList<String> names) {
		this.foundationViewNames = names;
	}

/*	*//**
	 *  returns the view name with first 8 characters
	 * @param name
	 * @return
	 *//*
	private static String extractFoundationViewName(String name){
		String result = "";
		if(name.contains("-")){
			result = name.substring(0,name.lastIndexOf("-"));

			if(result.contains("-")){
				result = result.substring(result.lastIndexOf("-")+1, result.length());
				if(result.length() <=8){
					return result;
				}
				return result.substring(0,8);
			}

		}
		return null;
	}*/


	/**
	 * Writes the relationship to their respective ontologies
	 * @param srcFileName file name that is interacting with the destination file
	 * @param destFileName file name that is being called/interacted
	 * @param relationshipTo srcFileName to destFileName relationship
	 * @param relationshipFrom destFileName to srcFileName relationship
	 */
/*	public void writeToContext(String srcFileName, String destFileName, String relationshipTo, String relationshipFrom){

		OntologyBuilderIndexRecord srcindexRecord = srcIndex;

				IndexRecordSearchParm[] ss = new IndexRecordSearchParm[3];
		ss[0] = new IndexRecordSearchParm(IndexConstants.DOCUMENT_NAME, srcFileName, ".*");
		ss[1] = new IndexRecordSearchParm(IndexConstants.DOCUMENT_TYPE, Entities.WEB_SERVICE,"");
		ss[2] = new IndexRecordSearchParm(IndexConstants.DOCUMENT_SYSTEM, Entities.AE,"");


		srcindexRecord = OntologyBuilderConfig.indexUtil.searchIndexRecordUsingSearchParms(
				srcFileName,
				ss,
				//(IndexRecordSearchParm[])s.toArray(),
				false);

		if(srcindexRecord == null){
			//System.out.println("srcindexrecord is --------NULL");
			return;
		}

		OntologyBuilderKey outputKey = new OntologyBuilderKey(
				srcindexRecord);

		OntologyBuilderIndexRecord destindexRecord = new OntologyBuilderIndexRecord();



		IndexRecordSearchParm[] dd = new IndexRecordSearchParm[2];
		dd[0] = new IndexRecordSearchParm(IndexConstants.DOCUMENT_NAME_UPPERCASE, destFileName.toUpperCase(), ".*");
		dd[1] = new IndexRecordSearchParm(IndexConstants.DOCUMENT_TYPE, Entities.FOUNDATION_VIEW, "");
		destindexRecord = OntologyBuilderConfig.indexUtil.searchIndexRecordUsingSearchParms(
				destFileName,
				dd,
				true);

		if(destindexRecord == null){
			return;
		}

		OntologyBuilderValue outputValue = new OntologyBuilderValue(srcFileName+"."+Entities.WEB_SERVICE.toLowerCase(),
				Entities.WEB_SERVICE,
				Entities.WSDL, destFileName.toUpperCase()+"."+Entities.FOUNDATION_VIEW.toLowerCase(),
				Entities.FOUNDATION_VIEW,
				relationshipTo, "",
				destindexRecord, Entities.FOUNDLOC);

		try {
			CodeMapper.mapperContext.write(outputKey, outputValue);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		outputKey = new OntologyBuilderKey(
				destindexRecord);




		outputValue = new OntologyBuilderValue(destFileName.toUpperCase()+"."+Entities.FOUNDATION_VIEW.toLowerCase(),
				Entities.FOUNDATION_VIEW,
				Entities.FOUNDLOC, srcFileName+"."+Entities.WEB_SERVICE.toLowerCase(),
				Entities.WEB_SERVICE,
				relationshipFrom, "", 
				srcindexRecord, Entities.WSDL);


		try {
			CodeMapper.mapperContext.write(outputKey, outputValue);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

}