/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dictionaries.lamis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.Demographics;
import model.DisplayScreen;
import model.Obs;
import model.lamis.CareCardInitialCoding;
import model.lamis.HIVEnrollment;
import model.lamis.LamisDrug;
import org.apache.commons.lang3.StringUtils;
import utils.Converter;
import utils.FileManager;

/**
 *
 * @author The Bright
 */
public class LamisEnrollmentDictionary {
    private FileManager mgr;
    private DisplayScreen screen;
    private final static int CREATOR = 1;
    private final static int VOIDED = 0;
    private List<CareCardInitialCoding> codingList=new ArrayList<CareCardInitialCoding>();
    private final static int DATE_CONFIRMED_POSITIVE_CONCEPT_ID=160554;
    private final static int DATE_ART_STARTED_CONCEPT_ID=159599;
    private final static int NEXT_OF_KIN_NAME_CONCEPT_ID=162729;
    private final static int NEXT_OF_KIN_PHONE_CONEPT_ID=164946;
    
    private final static int PHONE_NUMBER_CONCEPT_ID=159635;
    private final static int HIV_ENROLLMENT_FORM=23;
    public LamisEnrollmentDictionary(){
        mgr = new FileManager();
        loadMapFiles();
    }
    public List<String[]> readCSVData(String csvFile) {
        List<String[]> data = mgr.loadAllData(csvFile);
        return data;
    }
    public void setDisplayScreen(DisplayScreen screen) {
        this.screen = screen;
        mgr.setScreen(screen);
    }

    public int countRecords(String csvFile) {
        int count = mgr.countRecordsInCSV(csvFile, screen);
        return count;
    }
    public List<Obs> convertToObsList(HIVEnrollment hivEnrollment,int locationID){
        List<Obs> obsList=new ArrayList<Obs>();
        Obs obs=null;
        CareCardInitialCoding coding=null;
        
        //Next of Kin Name
        obs=createValueTextObs(hivEnrollment, NEXT_OF_KIN_NAME_CONCEPT_ID, hivEnrollment.getNextOfKinName(), locationID);
        obsList.add(obs);
        
        //Next of Kin Relationship
        coding=getCodingValue(20, hivEnrollment.getNextOfKinrelationship());
        if(coding!=null){
            obs=createCodedObs(hivEnrollment, coding.getNmrsQuestionConceptID(), coding.getNmrsAnswerConceptID(), locationID);
            obsList.add(obs);
        }
             
        //Date Confirmed Positive
        obs=createDateObs(hivEnrollment, DATE_CONFIRMED_POSITIVE_CONCEPT_ID, hivEnrollment.getDateConfirmedHIVTest(), locationID);
        obsList.add(obs);
        //Marrital Status
        coding=getCodingValue(10, hivEnrollment.getMaritalStatus());
        if(coding!=null){
            obs=createCodedObs(hivEnrollment, coding.getNmrsQuestionConceptID(), coding.getNmrsAnswerConceptID(), locationID);
            obsList.add(obs);
        }
        //Education Status
        coding=getCodingValue(11, hivEnrollment.getEducation());
        if(coding!=null){
            obs=createCodedObs(hivEnrollment, coding.getNmrsQuestionConceptID(), coding.getNmrsAnswerConceptID(), locationID);
            obsList.add(obs);
        }
        //Occupation
        coding=getCodingValue(12, hivEnrollment.getOccupation());
        if(coding!=null){
            obs=createCodedObs(hivEnrollment, coding.getNmrsQuestionConceptID(), coding.getNmrsAnswerConceptID(), locationID);
            obsList.add(obs);
        }
        //Care Entry Point
        coding=getCodingValue(21, hivEnrollment.getCareEntryPoint());
        if(coding!=null){
            obs=createCodedObs(hivEnrollment, coding.getNmrsQuestionConceptID(), coding.getNmrsAnswerConceptID(), locationID);
            obsList.add(obs);
        }
        //Phone number
        String phoneNumber=hivEnrollment.getPhone();
        if(!StringUtils.isEmpty(phoneNumber)){
            obs=createValueTextObs(hivEnrollment, PHONE_NUMBER_CONCEPT_ID, phoneNumber, locationID);
            obsList.add(obs);
        }
        
        return obsList;
    }
    public CareCardInitialCoding getCodingValue(int pos, String val){
        CareCardInitialCoding coding=null;
        for(CareCardInitialCoding ele: codingList){
            if(ele.getId()==pos && StringUtils.equalsIgnoreCase(ele.getLamisAnswer(), val)){
                coding=ele;
            }
        }
        return coding;
    }
    public List<HIVEnrollment> convertToHIVEnrollments(List<String[]> dataArrList){
        return Converter.convertToHIVEnrollmentList(dataArrList);
    }
    public Obs createDateObs(HIVEnrollment hIVEnrollment, int conceptID,Date valueDate, int locationID){
        Obs obs = new Obs();
        obs.setPatientID(hIVEnrollment.getPatientID());
        obs.setVisitDate(hIVEnrollment.getDateRegistration());
        obs.setFormID(HIV_ENROLLMENT_FORM);
        obs.setCreator(CREATOR);
        obs.setDateEntered(new Date());
        obs.setLocationID(locationID);
        obs.setVoided(VOIDED);
        obs.setConceptID(conceptID);
        obs.setValueDate(valueDate);
        obs.setUuid(Converter.generateUUID());
        obs.setProviderID(CREATOR);
        obs.setAllowed(true);
        return obs;
    }
    public Obs createValueTextObs(HIVEnrollment hIVEnrollment, int conceptID,String valueText, int locationID){
        Obs obs = new Obs();
        obs.setPatientID(hIVEnrollment.getPatientID());
        obs.setVisitDate(hIVEnrollment.getDateRegistration());
        obs.setFormID(HIV_ENROLLMENT_FORM);
        obs.setCreator(CREATOR);
        obs.setDateEntered(new Date());
        obs.setLocationID(locationID);
        obs.setVoided(VOIDED);
        obs.setConceptID(conceptID);
        obs.setValueText(valueText);
        obs.setUuid(Converter.generateUUID());
        obs.setProviderID(CREATOR);
        obs.setAllowed(true);
        return obs;
    }
    public Obs createCodedObs(HIVEnrollment hIVEnrollment, int conceptID, int valueCoded, int locationID) {
        Obs obs = new Obs();
        obs.setPatientID(hIVEnrollment.getPatientID());
        obs.setVisitDate(hIVEnrollment.getDateRegistration());
        obs.setFormID(HIV_ENROLLMENT_FORM);
        obs.setCreator(CREATOR);
        obs.setDateEntered(new Date());
        obs.setLocationID(locationID);
        obs.setVoided(VOIDED);
        obs.setConceptID(conceptID);
        obs.setValueCoded(valueCoded);
        obs.setUuid(Converter.generateUUID());
        obs.setProviderID(CREATOR);
        obs.setAllowed(true);
        return obs;
    }
     public void loadMapFiles() {
        List<String[]> dataArr = mgr.loadAllDataInFolder("map", "carecardinitial.csv");
        codingList=Converter.convertToCareCardInitialCodingList(dataArr);
    }
     public void closeAllResources() {
        mgr.closeAll();
    }
}
