/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dictionaries.lamis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import model.DisplayScreen;
import model.Obs;
import model.lamis.LamisClinical;
import model.lamis.LamisClinicalCoding;
import model.lamis.LamisLabCoding;
import model.lamis.LamisLabResult;
import model.lamis.LamisRegimenMap;
import org.apache.commons.lang3.StringUtils;
import utils.Converter;
import utils.FileManager;

/**
 *
 * @author The Bright
 */
public class LamisClinicalDictionary {

    private FileManager mgr;
    private DisplayScreen screen;
    private List<LamisClinicalCoding> lamisLabCodingList = new ArrayList<LamisClinicalCoding>();
    private List<LamisRegimenMap> lamisRegimenCodingList = new ArrayList<LamisRegimenMap>();
    private final static int CREATOR = 1;
    private final static int CLINICAL_FORM_ID = 14;
    private final static int VOIDED = 0;

    public LamisClinicalDictionary() {
        mgr = new FileManager();
        loadMapFiles();
        loadRegimenCodingMapFiles();
    }

    public void loadMapFiles() {
        List<String[]> dataArr = mgr.loadAllDataInFolder("map", "CLINICALCODING.csv");
        lamisLabCodingList = Converter.convertToLamisClinicalCodingList(dataArr);
    }
     private void loadRegimenCodingMapFiles() {
        List<String[]> dataArr = mgr.loadAllDataInFolder("map", "Regimenmap.csv");
        lamisRegimenCodingList = Converter.converterToLamisRegimenCodingList(dataArr);
    }

    public LamisClinicalCoding getAnswerConceptForCodedTypeID(int pos, String answer) {
        LamisClinicalCoding code = null;
        for (LamisClinicalCoding clinicalCoding : lamisLabCodingList) {
            if (clinicalCoding.getDataType().equalsIgnoreCase("CODED") && clinicalCoding.getVariablePosition() == pos && clinicalCoding.getLamisAnswer().equalsIgnoreCase(answer)) {
                code = clinicalCoding;
            }
        }
        return code;
    }

    public LamisClinicalCoding getQuestionConceptForNonCodedTypeID(int pos) {
        LamisClinicalCoding code = null;
        for (LamisClinicalCoding clinicalCoding : lamisLabCodingList) {
            if (clinicalCoding.getVariablePosition() == pos) {
                code = clinicalCoding;
            }
        }
        return code;
    }

    public int countRecords(String csvFile) {
        int count = mgr.countRecordsInCSV(csvFile, screen);
        return count;
    }

    public void setDisplayScreen(DisplayScreen screen) {
        this.screen = screen;
        mgr.setScreen(screen);
    }

    public List<String[]> readCSVData(String csvFile) {
        List<String[]> data = mgr.loadAllData(csvFile);
        return data;
    }

    public List<LamisClinical> convertToLamisClinicalList(List<String[]> data) {
        return Converter.converterToLamisClinicalList(data);
    }

   
    public List<Obs> convertToObsList(LamisClinical clinical, int locationID) {
        List<Obs> obsList = new ArrayList<Obs>();
        Obs obs = null;
        LamisClinicalCoding clinicalCoding = null;
        String whoStagging = clinical.getClinicalStage();
        int CLINICAL_STAGGING_POS = 4;
        clinicalCoding = getAnswerConceptForCodedTypeID(CLINICAL_STAGGING_POS, whoStagging);
        if (StringUtils.isNotEmpty(whoStagging) && clinicalCoding != null) {
            obs = getCodedObsFromClinical(CLINICAL_STAGGING_POS, clinical, whoStagging, locationID);
            obsList.add(obs);
        }
        String functionalStatus = clinical.getFunctionalStatus();
        int FUNCTIONAL_STATUS_POS = 5;
        clinicalCoding = getAnswerConceptForCodedTypeID(FUNCTIONAL_STATUS_POS, functionalStatus);
        if (StringUtils.isNotEmpty(functionalStatus) && clinicalCoding != null) {
            obs = getCodedObsFromClinical(FUNCTIONAL_STATUS_POS, clinical, functionalStatus, locationID);
            obsList.add(obs);

        }
        String tbStatus=clinical.getTbStatus();
        int TB_STATUS_POSITION=6;
        clinicalCoding=getAnswerConceptForCodedTypeID(TB_STATUS_POSITION, tbStatus);
        if(StringUtils.isNotEmpty(tbStatus) && clinicalCoding!=null){
            obs=getCodedObsFromClinical(TB_STATUS_POSITION, clinical, tbStatus, locationID);
            obsList.add(obs);
        }
        String regimenType = clinical.getRegimenType();
        int REGIMEN_TYPE_POS = 10;
        LamisRegimenMap regimenMap=null;
        clinicalCoding = getAnswerConceptForCodedTypeID(REGIMEN_TYPE_POS, regimenType);
        if (StringUtils.isNotEmpty(regimenType) && clinicalCoding != null) {
            obs = getCodedObsFromClinical(REGIMEN_TYPE_POS, clinical, regimenType, locationID);//RegimenType
            obsList.add(obs);
            String regimen = clinical.getRegimen();
            regimenMap=getRegimenCode(regimen);
            if (StringUtils.isNotEmpty(regimen) && regimenMap != null) {
                obs = new Obs();
                obs.setPatientID(clinical.getPatientID());
                obs.setConceptID(regimenMap.getNmrsConceptID());
                obs.setVisitDate(clinical.getVisitDate());
                obs.setFormID(CLINICAL_FORM_ID);
                obs.setUuid(Converter.generateUUID());
                obs.setLocationID(locationID);
                obs.setDateEntered(clinical.getTimestamp());
                obs.setProviderID(CREATOR);
                obs.setVoided(VOIDED);
                obs.setValueCoded(regimenMap.getNmrsAnswerConceptID());
                obs.setCreator(CREATOR);
                obsList.add(obs);
            }

        }
        int BODY_WEIGHT_POS = 12;
        clinicalCoding = getQuestionConceptForNonCodedTypeID(BODY_WEIGHT_POS);
        if (clinical.getWeight() != 0.0 && clinicalCoding!=null) {
                obs=getNumericObsFromClinical(BODY_WEIGHT_POS, clinical, clinical.getWeight(), locationID);
                obsList.add(obs);
        }

        int BODY_HEIGHT_POS = 13;
        clinicalCoding = getQuestionConceptForNonCodedTypeID(BODY_HEIGHT_POS);
        if (clinical.getHeight() != 0.0 && clinicalCoding != null) {
            //Convert Height to cm
            obs = getNumericObsFromClinical(BODY_HEIGHT_POS, clinical, clinical.getHeight()*100, locationID);
            obsList.add(obs);
        }
        String bp = clinical.getBp();
        int systolicBPConcept = 5085;
        int dystolicBPConcept = 5086;
        if (StringUtils.isNotEmpty(bp)) {
            String[] data = StringUtils.split(bp, "/");
            if (data.length == 2) {
                obs = new Obs(); // Systolic Obs
                obs.setConceptID(systolicBPConcept);
                obs.setPatientID(clinical.getPatientID());
                obs.setFormID(CLINICAL_FORM_ID);
                obs.setVisitDate(clinical.getVisitDate());
                obs.setVoided(VOIDED);
                obs.setCreator(CREATOR);
                obs.setProviderID(CREATOR);
                obs.setUuid(Converter.generateUUID());
                obs.setValueNumeric(Converter.convertToDouble(data[0]));
                obs.setDateEntered(clinical.getTimestamp());
                obs.setLocationID(locationID);
                obsList.add(obs);

                obs = new Obs();// Dystolic BP
                obs.setConceptID(dystolicBPConcept);
                obs.setPatientID(clinical.getPatientID());
                obs.setFormID(CLINICAL_FORM_ID);
                obs.setVisitDate(clinical.getVisitDate());
                obs.setVoided(VOIDED);
                obs.setCreator(CREATOR);
                obs.setProviderID(CREATOR);
                obs.setUuid(Converter.generateUUID());
                obs.setValueNumeric(Converter.convertToDouble(data[1]));
                obs.setDateEntered(clinical.getTimestamp());
                obs.setLocationID(locationID);
                obsList.add(obs);

            }
        }
        int PREGNANCY_STATUS_POS = 16;
        String pregnancyStatus = clinical.getPregnant();
        clinicalCoding = getAnswerConceptForCodedTypeID(PREGNANCY_STATUS_POS, pregnancyStatus);
        if (clinicalCoding != null && StringUtils.isNotEmpty(pregnancyStatus)) {
            obs = getCodedObsFromClinical(PREGNANCY_STATUS_POS, clinical, pregnancyStatus, locationID);
            obsList.add(obs);
        }
        int LMP_POSITION = 17;
        Date LMP = clinical.getLMP();
        clinicalCoding = getQuestionConceptForNonCodedTypeID(LMP_POSITION);
        if (clinicalCoding != null && LMP != null) {
            obs = getDateObsFromClinical(LMP_POSITION, clinical, LMP, locationID);
            obsList.add(obs);
        }
        int BREASTFEEDING_POSITION = 18;
        String breastfeedingStatus = clinical.getBreastFeeding();
        clinicalCoding = getAnswerConceptForCodedTypeID(BREASTFEEDING_POSITION, breastfeedingStatus);
        if (clinicalCoding != null && StringUtils.isNotEmpty(breastfeedingStatus)) {
            obs = getCodedObsFromClinical(BREASTFEEDING_POSITION, clinical, breastfeedingStatus, locationID);
            obsList.add(obs);
        }
        int OI_POSITION = 22;
        String oiID = clinical.getOiID();
        clinicalCoding = getAnswerConceptForCodedTypeID(OI_POSITION, oiID);
        if (StringUtils.isNotEmpty(oiID) && clinicalCoding != null) {
            obs = getCodedObsFromClinical(OI_POSITION, clinical, oiID, locationID);
            obsList.add(obs);
        }
        int ADVERSE_DRUG_REACTION_POS=24;
        String adrID=clinical.getAdverseDrugReactionID();
        if(StringUtils.isNotEmpty(adrID)){
            obs=getCodedObsFromClinical(ADVERSE_DRUG_REACTION_POS, clinical, adrID, locationID);
            obsList.add(obs);
        }
        int ADHERENCE_LEVEL_POS=25;
        String adherenceLevel=clinical.getAdherenceLevel();
        if(StringUtils.isNotEmpty(adherenceLevel)){
            obs=getCodedObsFromClinical(ADHERENCE_LEVEL_POS, clinical, adherenceLevel, locationID);
            obsList.add(obs);
        }
        int NEXT_APPOINTMENT_DATE_POS=28;
        Date nextAppointmentDate=clinical.getNextAppointmentDate();
        if(nextAppointmentDate!=null){
            obs=getDateObsFromClinical(NEXT_APPOINTMENT_DATE_POS, clinical, nextAppointmentDate, locationID);
            obsList.add(obs);
        }
        obsList.removeAll(Collections.singletonList(null));
        return obsList;
    }

    public Obs getNumericObsFromClinical(int pos, LamisClinical clinical, double variable, int locationID) {
        Obs obs = null;
        LamisClinicalCoding clinicalCoding = getQuestionConceptForNonCodedTypeID(pos);
        if (clinicalCoding != null) {
            obs = new Obs();
            obs.setPatientID(clinical.getPatientID());
            obs.setFormID(CLINICAL_FORM_ID);
            obs.setConceptID(clinicalCoding.getOMRSConceptID());
            obs.setVisitDate(clinical.getVisitDate());
            obs.setVoided(VOIDED);
            obs.setCreator(CREATOR);
            obs.setProviderID(CREATOR);
            obs.setUuid(Converter.generateUUID());
            obs.setValueNumeric(variable);
            obs.setDateEntered(clinical.getTimestamp());
            obs.setLocationID(locationID);
        }
        return obs;
    }

    public Obs getCodedObsFromClinical(int pos, LamisClinical clinical, String variable, int locationID) {
        Obs obs = null;
        LamisClinicalCoding clinicalCoding = getAnswerConceptForCodedTypeID(pos, variable);
        if (clinicalCoding != null) {
            obs = new Obs();//OI
            obs.setPatientID(clinical.getPatientID());
            obs.setFormID(CLINICAL_FORM_ID);
            obs.setVisitDate(clinical.getVisitDate());
            obs.setConceptID(clinicalCoding.getOMRSConceptID());
            obs.setValueCoded(clinicalCoding.getOMRSAnswerID());
            obs.setCreator(CREATOR);
            obs.setProviderID(CREATOR);
            obs.setDateEntered(clinical.getTimestamp());
            obs.setLocationID(locationID);
            obs.setUuid(Converter.generateUUID());
            obs.setVoided(VOIDED);
        }
        return obs;
    }

    public Obs getDateObsFromClinical(int pos, LamisClinical clinical, Date variableValue, int locationID) {
        Obs obs = null;
        LamisClinicalCoding clinicalCoding = getQuestionConceptForNonCodedTypeID(pos);
        if (clinicalCoding != null) {
            obs = new Obs();//OI
            obs.setPatientID(clinical.getPatientID());
            obs.setFormID(CLINICAL_FORM_ID);
            obs.setVisitDate(clinical.getVisitDate());
            obs.setConceptID(clinicalCoding.getOMRSConceptID());
            obs.setValueDate(variableValue);
            obs.setCreator(CREATOR);
            obs.setProviderID(CREATOR);
            obs.setDateEntered(clinical.getTimestamp());
            obs.setLocationID(locationID);
            obs.setUuid(Converter.generateUUID());
            obs.setVoided(VOIDED);
        }
        return obs;
    }

    public boolean containsALL(String regimenOpenMRS, String regimenText) {
        boolean ans = true;
        String[] data = StringUtils.split(StringUtils.upperCase(regimenOpenMRS), "-");
        if (!StringUtils.containsAny(StringUtils.upperCase(regimenText), data)) {
            ans = false;
        }
        return ans;
    }

    public LamisRegimenMap getRegimenCode(String regimenText) {
        //int valueCoded = 0;
        LamisRegimenMap regimenMap = null;
        for (LamisRegimenMap coding : lamisRegimenCodingList) {
            if (StringUtils.equalsIgnoreCase(coding.getLamisAnswerName(),regimenText)) {
                regimenMap = coding;
                //valueCoded = code.getOMRSAnswerID();
            }
        }
        return regimenMap;
    }

}
