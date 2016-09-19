package com.optum.db;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
//import com.fasterxml.jackson.databind.ObjectMapper;

@DynamoDBTable(tableName = "DrugsInfo")
public class DrugInfoDataItem {

	
    //private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private String drugName;
    private String costDescription;
    private int drugTier;
    private boolean isFormulary;
    private boolean requiresPreAuth;
    private String requirements;
    private boolean isBrandName;
    private boolean isSpecialityDrug;
    private String drugClass;
    private String description;
    private String emailID;
    private String carrier;
    private String phoneNumber;    
    
    @DynamoDBHashKey(attributeName = "DrugName")
    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String name) {
        this.drugName = name;
    }

    @DynamoDBAttribute(attributeName = "CostDescription")
    public String getCostDescription() {
        return costDescription;
    }

    public void setCostDescription(String value) {
        this.costDescription = value;
    }
    
    @DynamoDBAttribute(attributeName = "DrugTier")
    public int getDrugTier() {
        return drugTier;
    }

    public void setDrugTier(int value) {
        this.drugTier = value;
    }
    
    @DynamoDBAttribute(attributeName = "Formulary")
    public boolean getFormulary() {
        return isFormulary;
    }

    public void setFormulary(boolean value) {
        this.isFormulary = value;
    }
    
    @DynamoDBAttribute(attributeName = "PreAuth")
    public boolean getPreAuth() {
        return requiresPreAuth;
    }

    public void setPreAuth(boolean value) {
        this.requiresPreAuth = value;
    }
    
    @DynamoDBAttribute(attributeName = "Requirements")
    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String req) {
        this.requirements = req;
    }

    @DynamoDBAttribute(attributeName = "BrandName")
	public boolean getIsBrandName() {
		return isBrandName;
	}
    public void setIsBrandName(boolean boo) {
        this.isBrandName = boo;
    }
    
    @DynamoDBAttribute(attributeName = "SpecialityDrug")
	public boolean getIsSpecialityDrug() {
		return isSpecialityDrug;
	}
    public void setIsSpecialityDrug(boolean boo) {
        this.isSpecialityDrug = boo;
    }

    @DynamoDBAttribute(attributeName = "DrugClass")
    public String getDrugClass() {
        return drugClass;
    }

    public void setDrugClass(String value) {
        this.drugClass = value;
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
