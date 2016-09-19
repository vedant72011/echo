package com.optum.poc;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
//import com.fasterxml.jackson.databind.ObjectMapper;

@DynamoDBTable(tableName = "PersonInfo")
public class PersonInfoDataItem {

	
    //private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private String personName;
    private String description;
    private String emailID;
    private String carrier;
    private String phoneNumber;    
    
    @DynamoDBHashKey(attributeName = "PersonName")
    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String name) {
        this.personName = name;
    }
    @DynamoDBAttribute(attributeName = "Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    @DynamoDBAttribute(attributeName = "Carrier")
    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String value) {
        this.carrier = value;
    }

    @DynamoDBAttribute(attributeName = "EmailID")
    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String value) {
        this.emailID = value;
    }

    @DynamoDBAttribute(attributeName = "PhoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String value) {
        this.phoneNumber = value;
    }

    
}
