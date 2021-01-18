/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dictionaries.lamis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.DisplayScreen;
import model.Obs;
import model.lamis.CareCardInitialCoding;
import model.lamis.HIVEnrollment;
import org.apache.commons.lang3.StringUtils;
import utils.Converter;
import utils.FileManager;

/**
 *
 * @author The Bright
 */
public class LamisARTCommencementDictionary {

    private FileManager mgr;
    private DisplayScreen screen;
    private final static int CREATOR = 1;
    private final static int VOIDED = 0;
    private List<CareCardInitialCoding> codingList = new ArrayList<CareCardInitialCoding>();
    private final static int DATE_CONFIRMED_POSITIVE_CONCEPT_ID = 160554;
    private final static int DATE_ART_STARTED_CONCEPT_ID = 159599;
    private final static int BASELINE_CD4_CONCEPT_ID = 164429;
    private final static int BASELINE_WEIGHT_CONCEPT_ID = 165582;
    private final static int BASELINE_HEIGHT_CONCEPT_ID = 165581;
    private final static int PHONE_NUMBER_CONCEPT_ID = 159635;
    private final static int ARTCOMMENCEMENT_FORM = 56;

    public LamisARTCommencementDictionary() {
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

    public List<Obs> convertToObsList(HIVEnrollment hivEnrollment, int locationID) {
        List<Obs> obsList = new ArrayList<Obs>();
        Obs obs = null;
        CareCardInitialCoding coding = null;
        int regimenQuestionConceptID = 0;

        //Date ART Started
        obs = createDateObs(hivEnrollment, DATE_ART_STARTED_CONCEPT_ID, hivEnrollment.getArtStartDate(), locationID);
        obsList.add(obs);
        //Baseline CD4
        obs = createNumericObs(hivEnrollment, BASELINE_CD4_CONCEPT_ID, hivEnrollment.getBaselineCD4(), locationID);
        obsList.add(obs);
        //Baseline Weight
        obs = createNumericObs(hivEnrollment, BASELINE_WEIGHT_CONCEPT_ID, hivEnrollment.getBaselineWeight(), locationID);
        obsList.add(obs);

        //Baseline Height
        obs = createNumericObs(hivEnrollment, BASELINE_HEIGHT_CONCEPT_ID, hivEnrollment.getBaselineHeight(), locationID);
        obsList.add(obs);

        //Baseline Clinical Stagging
        coding = getCodingValue(31, hivEnrollment.getBaselineClinicStage());
        if (coding != null) {
            obs = createCodedObs(hivEnrollment, coding.getNmrsQuestionConceptID(), coding.getNmrsAnswerConceptID(), locationID);
            obsList.add(obs);
        }
        //Functional Status
        coding = getCodingValue(32, hivEnrollment.getBaselineFunctionalStatus());
        if (coding != null) {
            obs = createCodedObs(hivEnrollment, coding.getNmrsQuestionConceptID(), coding.getNmrsAnswerConceptID(), locationID);
            obsList.add(obs);

        }

        //First Regimen Line
        coding = getCodingValue(35, hivEnrollment.getFirstRegimenLine());
        if (coding != null) {
            obs = createCodedObs(hivEnrollment, coding.getNmrsQuestionConceptID(), coding.getNmrsAnswerConceptID(), locationID);
            obsList.add(obs);
            //regimenQuestionConceptID = coding.getNmrsAnswerConceptID();
        }
        coding=mapRegimen(36, hivEnrollment);
        if(coding!=null){
            obs=createCodedObs(hivEnrollment,coding.getNmrsQuestionConceptID(), coding.getNmrsAnswerConceptID(), locationID);
            obsList.add(obs);
        }

        return obsList;
    }
    
    public CareCardInitialCoding mapRegimen(int pos,HIVEnrollment hivEnrollment){
        CareCardInitialCoding coding=null;
        for (CareCardInitialCoding ele : codingList) {
            if (
                    ele.getId() == pos 
                    && 
                    StringUtils.equalsIgnoreCase(hivEnrollment.getFirstRegimenLine(), ele.getLamisQuestion())
                    &&
                    StringUtils.equalsIgnoreCase(hivEnrollment.getFirstRegimen(), ele.getLamisAnswer())
                    ) {
                coding = ele;
            }
        }
        return coding;
    }
    public CareCardInitialCoding getCodingValue(int pos, String val) {
        CareCardInitialCoding coding = null;
        for (CareCardInitialCoding ele : codingList) {
            if (ele.getId() == pos && StringUtils.equalsIgnoreCase(ele.getLamisAnswer(), val)) {
                coding = ele;
            }
        }
        return coding;
    }

    public List<HIVEnrollment> convertToHIVEnrollments(List<String[]> dataArrList) {
        return Converter.convertToHIVEnrollmentList(dataArrList);
    }

    public Obs createCodedObs(HIVEnrollment hIVEnrollment, int conceptID, int valueCoded, int locationID) {
        Obs obs = new Obs();
        obs.setPatientID(hIVEnrollment.getPatientID());
        obs.setVisitDate(hIVEnrollment.getArtStartDate());
        obs.setFormID(ARTCOMMENCEMENT_FORM);
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

    public Obs createDateObs(HIVEnrollment hIVEnrollment, int conceptID, Date valueDate, int locationID) {
        Obs obs = new Obs();
        obs.setPatientID(hIVEnrollment.getPatientID());
        obs.setVisitDate(hIVEnrollment.getArtStartDate());
        obs.setFormID(ARTCOMMENCEMENT_FORM);
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

    public Obs createValueTextObs(HIVEnrollment hIVEnrollment, int conceptID, String valueText, int locationID) {
        Obs obs = new Obs();
        obs.setPatientID(hIVEnrollment.getPatientID());
        obs.setVisitDate(hIVEnrollment.getDateRegistration());
        obs.setFormID(ARTCOMMENCEMENT_FORM);
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

    public void closeAllResources() {
        mgr.closeAll();
    }

    public Obs createNumericObs(HIVEnrollment hIVEnrollment, int conceptID, double valueNumeric, int locationID) {
        Obs obs = new Obs();
        obs.setPatientID(hIVEnrollment.getPatientID());
        obs.setVisitDate(hIVEnrollment.getArtStartDate());
        obs.setFormID(ARTCOMMENCEMENT_FORM);
        obs.setCreator(CREATOR);
        obs.setDateEntered(new Date());
        obs.setLocationID(locationID);
        obs.setVoided(VOIDED);
        obs.setConceptID(conceptID);
        obs.setValueNumeric(valueNumeric);
        obs.setUuid(Converter.generateUUID());
        obs.setProviderID(CREATOR);
        obs.setAllowed(true);
        return obs;
    }

    public void loadMapFiles() {
        List<String[]> dataArr = mgr.loadAllDataInFolder("map", "carecardinitial.csv");
        codingList = Converter.convertToCareCardInitialCodingList(dataArr);
    }
}
