/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dictionaries.lamis;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.DisplayScreen;
import model.Obs;
import model.lamis.LamisClinical;
import model.lamis.LamisClinicalCoding;
import model.lamis.LamisDrug;
import model.lamis.LamisDrugCoding;
import model.lamis.LamisRegimenMap;
import org.apache.commons.lang3.StringUtils;
import utils.Converter;
import utils.FileManager;

/**
 *
 * @author The Bright
 */
public class LamisDrugDictionary {

    private FileManager mgr;
    private DisplayScreen screen;
    private List<LamisDrugCoding> lamisDrugCodingList = new ArrayList<LamisDrugCoding>();
    private List<LamisRegimenMap> lamisRegimenCodingList = new ArrayList<LamisRegimenMap>();
    private final static int CREATOR = 1;
    private final static int VOIDED = 0;
    private final static Map<String, Integer> regimenLineMap = new HashMap<String, Integer>();
    private final static Map<String, Integer> treatmentTypeMap = new HashMap<String, Integer>();

    /* List of NMRS Concepts used in the migration */
    private final static int PHARMACY_FORM_ID = 27;
    private final static int TREATMENT_TYPE_CONCEPT_ID = 165945;
    private final static int REGIMEN_LINE_CONCEPT_ID = 165708;
    private final static int ADHERENCE_COUNSELING_OFFERING = 165832;
    private final static int VISIT_TYPE_CONCEPT_ID = 164181;
    private final static int NEXT_APPOINTMENT_DATE_CONCEPT_ID=5096;
    private final static int FOLLOWUP_VISIT_TYPE_CONCEPT_ID = 160530;
    private final static int PICKUP_REASON_CONCEPT_ID = 165774;
    private final static int DRUG_STRENGTH_CONCEPT_ID = 165725;
    private final static int DRUG_FREQUENCY_CONCEPT_ID = 165723;
    private final static int DRUG_DURATION_CONCEPT_ID = 159368;
    private final static int ORDERED_BY_CONCEPT_ID = 164987;
    private final static int ORDERED_BY_DATE_CONCEPT_ID = 164989;
    private final static int DRUG_QUANTITY_PRESCRIBED_CONCEPT_ID = 160856;
    private final static int DRUG_QUANTITY_DISPENSED_CONCEPT_ID = 1443;
    private final static int FREQUENCY_CONCEPT_OD = 160862;
    private final static int FREQUENCY_CONCEPT_BD = 160858;
    private final static int FREQUENCY_CONCEPT_2BD = 165721;
    private final static int FFREQUENCY_CONCEPT_3BD = 166056;
    private final static int FFREQUENCY_CONCEPT_4BD = 166057;
    private final static String ORDERED_BY = "1 - Super User";

    public LamisDrugDictionary() {
        mgr = new FileManager();
        loadDrugCodingMapFiles();
        loadRegimenMap();
        loadRegimenCodingMapFiles();
        loadTreatmentTypeMap();
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

    public List<LamisDrug> convertToLamisDrugList(List<String[]> data) {
        return Converter.converterToLamisDrugList(data);
    }

    public List<Obs> extractObsGroupForDrugs(LamisDrug drug, int locationID) {
        List<Obs> obsList = new ArrayList<Obs>();
        Obs obs = null;
        LamisDrugCoding drugCoding = getDrugCoding(drug.getRegimenDrugID());
        //System.out.println(drug.getVisitDate() + "Visit Date");
        if (drugCoding != null && drug.getVisitDate() != null && !drug.isExist()) {
            //Medication Grouping Concept
            //obs=createGroupingObs(drug, drugCoding.getGroupingConceptID(), locationID);
            //obsList.add(obs);
            //Drug Name
            obs = createCodedObs(drug, drugCoding.getOmrsQuestionConceptID(), drugCoding.getOmrsDrugConceptID(), drug.getObsGroupID(), locationID);
            obsList.add(obs);
            //Drug Strength
            obs = createCodedObs(drug, DRUG_STRENGTH_CONCEPT_ID, drugCoding.getStrengthConceptID(), drug.getObsGroupID(), locationID);
            obsList.add(obs);
            //Drug Frequency
            obs = createCodedObs(drug, DRUG_FREQUENCY_CONCEPT_ID, getFrequencyConcept(drug), drug.getObsGroupID(), locationID);
            obsList.add(obs);
            //Drug Duration
            obs = createNumericObs(drug, DRUG_DURATION_CONCEPT_ID, drug.getDuration(), drug.getObsGroupID(), locationID);
            obsList.add(obs);
            //Quantity of medication prescribed
            obs = createNumericObs(drug, DRUG_QUANTITY_PRESCRIBED_CONCEPT_ID, getQuantityPrescribed(drug), drug.getObsGroupID(), locationID);
            obsList.add(obs);
            //Quantity of medication dispensed
            obs = createNumericObs(drug, DRUG_QUANTITY_DISPENSED_CONCEPT_ID, getQuantityPrescribed(drug), drug.getObsGroupID(), locationID);
            obsList.add(obs);
            //Next Appointment Date
            obs=createDateObs(drug, NEXT_APPOINTMENT_DATE_CONCEPT_ID, drug.getNextAppointmentDate(), locationID);
            obsList.add(obs);

        }

        return obsList;
    }

    public List<Obs> extractOneTimeObsList(LamisDrug drug, int locationID) {
        List<Obs> obsList = new ArrayList<Obs>();
       
        Obs obs = null;
        String adherence = drug.getAdherence();
        int valueCoded = 0;
        
        if (StringUtils.isNotEmpty(adherence)) {
            if (StringUtils.equalsIgnoreCase(adherence, "0")) {
                obs = createCodedObs(drug, ADHERENCE_COUNSELING_OFFERING, 1066, locationID);
            } else if (StringUtils.equalsIgnoreCase(adherence, "1")) {
                obs = createCodedObs(drug, ADHERENCE_COUNSELING_OFFERING, 1065, locationID);
            }
            obsList.add(obs);
        }
        //Visit Type Obs
        obs = createCodedObs(drug, VISIT_TYPE_CONCEPT_ID, FOLLOWUP_VISIT_TYPE_CONCEPT_ID, locationID);
        obsList.add(obs);

        //Regimen Line
        String regimenTypeID = drug.getRegimenTypeID();
        if (StringUtils.isNotEmpty(regimenTypeID) && regimenLineMap.containsKey(regimenTypeID)) {
            valueCoded = regimenLineMap.get(regimenTypeID);
            obs = createCodedObs(drug, REGIMEN_LINE_CONCEPT_ID, valueCoded, locationID);
            obsList.add(obs);
            //Treatment Type
            if (treatmentTypeMap.containsKey(regimenTypeID)) {
                valueCoded = treatmentTypeMap.get(regimenTypeID);
                obs = createCodedObs(drug, TREATMENT_TYPE_CONCEPT_ID, valueCoded, locationID);
                obsList.add(obs);
            }

            //Current Regimen
            LamisRegimenMap regimenMap = getRegimenMap(drug.getRegimenTypeID(), drug.getRegimenID());
            if (regimenMap != null) {
                obs = createCodedObs(drug, regimenMap.getNmrsConceptID(), regimenMap.getNmrsAnswerConceptID(), locationID);
                obsList.add(obs);
            }
            //Ordered by 164987
            obs = createTextObs(drug, ORDERED_BY_CONCEPT_ID, ORDERED_BY, locationID);
            obsList.add(obs);

            //Ordered by date 164989
            obs = createDateObs(drug, ORDERED_BY_CONCEPT_ID, drug.getVisitDate(), locationID);
            obsList.add(obs);

        }
        
        return obsList;
    }

    private double getQuantityPrescribed(LamisDrug drug) {
        double drugQuantity = 0.0;
        double totalDosePerDay = drug.getMorningDose() + drug.getAfternoonDose() + drug.getEveningDose();
        double duration = drug.getDuration();
        drugQuantity = totalDosePerDay * duration;
        return drugQuantity;
    }

    private int getFrequencyConcept(LamisDrug drug) {
        int valueCoded = 5622;
        int oneCount = 0, twoCount = 0, threeCount = 0;
        int morningDose = (int) drug.getMorningDose();
        int afternoonDose = (int) drug.getAfternoonDose();
        int eveningDose = (int) drug.getEveningDose();
        String frequency = StringUtils.join(morningDose, afternoonDose, eveningDose);
        if (StringUtils.equalsIgnoreCase(frequency, "101") || StringUtils.equalsIgnoreCase(frequency, "202") || StringUtils.equalsIgnoreCase(frequency, "303") || StringUtils.equalsIgnoreCase(frequency, "404")) {
            valueCoded = FREQUENCY_CONCEPT_BD;
        } else if (StringUtils.equalsIgnoreCase(frequency, "002") || StringUtils.equalsIgnoreCase(frequency, "001") || StringUtils.equalsIgnoreCase(frequency, "100") || StringUtils.equalsIgnoreCase(frequency, "200") || StringUtils.equalsIgnoreCase(frequency, "300") || StringUtils.equalsIgnoreCase(frequency, "400")) {
            valueCoded = FREQUENCY_CONCEPT_OD;
        }

        return valueCoded;
    }

    public Obs createGroupingObs(LamisDrug drug, int conceptID, int locationID) {
        Obs obs = new Obs();
        obs.setPatientID(drug.getPatientID());
        obs.setVisitDate(drug.getVisitDate());
        obs.setFormID(PHARMACY_FORM_ID);
        obs.setCreator(CREATOR);
        obs.setDateEntered(drug.getTimestamp());
        obs.setLocationID(locationID);
        obs.setVoided(VOIDED);
        obs.setConceptID(conceptID);
        //obs.setValueCoded(valueCoded);
        obs.setUuid(Converter.generateUUID());
        obs.setProviderID(CREATOR);
        obs.setAllowed(true);
        return obs;
    }

    public Obs createCodedObs(LamisDrug drug, int conceptID, int valueCoded, int locationID) {
        Obs obs = new Obs();
        obs.setPatientID(drug.getPatientID());
        obs.setVisitDate(drug.getVisitDate());
        obs.setFormID(PHARMACY_FORM_ID);
        obs.setCreator(CREATOR);
        obs.setDateEntered(drug.getTimestamp());
        obs.setLocationID(locationID);
        obs.setVoided(VOIDED);
        obs.setConceptID(conceptID);
        obs.setValueCoded(valueCoded);
        obs.setUuid(Converter.generateUUID());
        obs.setProviderID(CREATOR);
        obs.setAllowed(true);
        return obs;
    }

    public Obs createCodedObs(LamisDrug drug, int conceptID, int valueCoded, int obsGroupingConceptID, int locationID) {
        Obs obs = new Obs();
        obs.setPatientID(drug.getPatientID());
        obs.setVisitDate(drug.getVisitDate());
        obs.setFormID(PHARMACY_FORM_ID);
        obs.setCreator(CREATOR);
        obs.setDateEntered(drug.getTimestamp());
        obs.setLocationID(locationID);
        obs.setVoided(VOIDED);
        obs.setConceptID(conceptID);
        obs.setValueCoded(valueCoded);
        obs.setUuid(Converter.generateUUID());
        obs.setObsGroupID(obsGroupingConceptID);
        obs.setProviderID(CREATOR);
        obs.setAllowed(true);
        return obs;
    }

    public Obs createNumericObs(LamisDrug drug, int conceptID, double valueNumeric, int obsGroupingConceptID, int locationID) {
        Obs obs = new Obs();
        obs.setPatientID(drug.getPatientID());
        obs.setVisitDate(drug.getVisitDate());
        obs.setFormID(PHARMACY_FORM_ID);
        obs.setCreator(CREATOR);
        obs.setDateEntered(drug.getTimestamp());
        obs.setLocationID(locationID);
        obs.setVoided(VOIDED);
        obs.setConceptID(conceptID);
        obs.setValueNumeric(valueNumeric);
        obs.setUuid(Converter.generateUUID());
        obs.setObsGroupID(obsGroupingConceptID);
        obs.setProviderID(CREATOR);
        obs.setAllowed(true);
        return obs;
    }

    public Obs createNumericObs(LamisDrug drug, int conceptID, double valueNumeric, int locationID) {
        Obs obs = new Obs();
        obs.setPatientID(drug.getPatientID());
        obs.setVisitDate(drug.getVisitDate());
        obs.setFormID(PHARMACY_FORM_ID);
        obs.setCreator(CREATOR);
        obs.setDateEntered(drug.getTimestamp());
        obs.setLocationID(locationID);
        obs.setVoided(VOIDED);
        obs.setConceptID(conceptID);
        obs.setValueNumeric(valueNumeric);
        obs.setUuid(Converter.generateUUID());
        //obs.setObsGroupID(obsGroupingConceptID);
        obs.setProviderID(CREATOR);
        obs.setAllowed(true);
        return obs;
    }

    public Obs createTextObs(LamisDrug drug, int conceptID, String valueText, int locationID) {
        Obs obs = new Obs();
        obs.setPatientID(drug.getPatientID());
        obs.setVisitDate(drug.getVisitDate());
        obs.setFormID(PHARMACY_FORM_ID);
        obs.setCreator(CREATOR);
        obs.setDateEntered(drug.getTimestamp());
        obs.setLocationID(locationID);
        obs.setVoided(VOIDED);
        obs.setConceptID(conceptID);
        obs.setValueText(valueText);
        obs.setUuid(Converter.generateUUID());
        //obs.setObsGroupID(obsGroupingConceptID);
        obs.setProviderID(CREATOR);
        obs.setAllowed(true);
        return obs;
    }

    public Obs createDateObs(LamisDrug drug, int conceptID, Date valueDate, int locationID) {
        Obs obs = new Obs();
        obs.setPatientID(drug.getPatientID());
        obs.setVisitDate(drug.getVisitDate());
        obs.setFormID(PHARMACY_FORM_ID);
        obs.setCreator(CREATOR);
        obs.setDateEntered(drug.getTimestamp());
        obs.setLocationID(locationID);
        obs.setVoided(VOIDED);
        obs.setConceptID(conceptID);
        obs.setValueDate(valueDate);
        obs.setUuid(Converter.generateUUID());
        //obs.setObsGroupID(obsGroupingConceptID);
        obs.setProviderID(CREATOR);
        obs.setAllowed(true);
        return obs;
    }

    private void loadRegimenMap() {
        regimenLineMap.put("1", 164506);
        regimenLineMap.put("2", 164513);
        regimenLineMap.put("3", 164507);
        regimenLineMap.put("4", 164514);
        regimenLineMap.put("14", 165702);
    }

    private void loadTreatmentTypeMap() {
        treatmentTypeMap.put("1", 165303);
        treatmentTypeMap.put("2", 165303);
        treatmentTypeMap.put("3", 165303);
        treatmentTypeMap.put("4", 165303);
        treatmentTypeMap.put("14", 165303);
        treatmentTypeMap.put("5", 165685);
        treatmentTypeMap.put("6", 165658);
        treatmentTypeMap.put("7", 165942);
        treatmentTypeMap.put("9", 165941);
    }

    private void loadDrugCodingMapFiles() {
        List<String[]> dataArr = mgr.loadAllDataInFolder("map", "drugcoding2.csv");
        lamisDrugCodingList = Converter.converterToLamisDrugCodingList(dataArr);
    }

    private void loadRegimenCodingMapFiles() {
        List<String[]> dataArr = mgr.loadAllDataInFolder("map", "Regimenmap.csv");
        lamisRegimenCodingList = Converter.converterToLamisRegimenCodingList(dataArr);
    }

    private LamisRegimenMap getRegimenMap(String regimenType, String regimenID) {
        LamisRegimenMap regimenMap = null;
        for (LamisRegimenMap rm : lamisRegimenCodingList) {
            if (StringUtils.equalsIgnoreCase(regimenType, rm.getLamisQuestionCode()) && StringUtils.equalsIgnoreCase(regimenID, rm.getLamisAnswerCode())) {
                regimenMap = rm;
            }
        }
        return regimenMap;
    }

    public LamisDrugCoding getDrugCoding(int drugID) {
        LamisDrugCoding drugCoding = null;
        for (LamisDrugCoding ldc : lamisDrugCodingList) {
            if (ldc.getItemID()==drugID) {
                //System.out.println(StringUtils.equalsIgnoreCase(StringUtils.trim(itemID), StringUtils.trim(ldc.getItemID()))+" output");
                drugCoding = ldc;
                break;

            }
        }
        return drugCoding;
    }
    public void closeAllResources() {
        mgr.closeAll();
    }

    public static void main(String[] arg) {
       LamisDrugDictionary dic=new LamisDrugDictionary();
       LamisDrugCoding drugCoding=dic.getDrugCoding(1);
       if(drugCoding!=null){
           System.out.println(drugCoding.getDrugName() + " "+ drugCoding.getItemID());
       }else{
           System.out.println(drugCoding+" val");
       }
       
    }
}
