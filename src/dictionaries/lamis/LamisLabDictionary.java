/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dictionaries.lamis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.ConceptMap;
import model.Demographics;
import model.DisplayScreen;
import model.Obs;
import model.lamis.LamisLabCoding;
import model.lamis.LamisLabResult;
import org.apache.commons.lang3.StringUtils;
import utils.Converter;
import utils.FileManager;

/**
 *
 * @author The Bright
 */
public class LamisLabDictionary {

    private FileManager mgr;
    private DisplayScreen screen;
    private List<LamisLabCoding> lamisLabCodingList = new ArrayList<LamisLabCoding>();

    private final static int DATE_LAB_REPORTED_CONCEPT = 165414;
    private final static int DATE_SAMPLE_COLLECTED_CONCEPT = 159951;
    private final static int DATE_ORDERED_CONCEPT = 164989;
    private final static int LAB_NUMBER_CONCEPT = 164409;
    private final static int COMMENT_CONCEPT = 164980;
    private final static int NMRS_LAB_FORM_ID = 21;
    private final static String REPORTED_BY_NAME = "1 - eHealth Developer";
    private final static int REPORTED_BY_CONCEPT_ID = 164982;
    private final static int NMRS_PROVIDER_ID = 1;
    private final static int NMRS_CREATOR_ID = 1;
    private Map<String, Integer> codedValueMap = new HashMap<String, Integer>();
    private final static int VOIDED = 0;

    public LamisLabDictionary() {
        mgr = new FileManager();
        loadMapFiles();
        loadCodedValueMap();
    }

    public void loadMapFiles() {
        List<String[]> dataArr = mgr.loadAllDataInFolder("map", "LABCODING.csv");
        lamisLabCodingList = Converter.convertToLamisLabCoding(dataArr);
    }

    public void loadCodedValueMap() {
        codedValueMap.put("Routine Monitoring", 161236);
        codedValueMap.put("Baseline", 162080);
        codedValueMap.put("Targeted Monitoring", 5622);
    }

    public void setDisplayScreen(DisplayScreen screen) {
        this.screen = screen;
        mgr.setScreen(screen);
    }

    public int countRecords(String csvFile) {
        int count = mgr.countRecordsInCSV(csvFile, screen);
        return count;
    }

    public List<String[]> readCSVData(String csvFile) {
        List<String[]> data = mgr.loadAllData(csvFile);
        return data;
    }

    public List<LamisLabResult> convertToLamisLabResult(List<String[]> data) {
        return Converter.convertToLamisLabResult(data);
    }

    public Obs createDateReportedObs(LamisLabResult labResult, int locationID) {
        Obs dateReportedObs =null;
        if (labResult.getDateReported() != null) {
            dateReportedObs = new Obs();
            dateReportedObs.setPatientID(labResult.getPatientID());
            dateReportedObs.setVisitDate(labResult.getDateCollected());
            dateReportedObs.setConceptID(DATE_LAB_REPORTED_CONCEPT);
            dateReportedObs.setValueDate(labResult.getDateReported());
            dateReportedObs.setDateEntered(labResult.getTimestamp());
            dateReportedObs.setLocationID(locationID);
            dateReportedObs.setFormID(NMRS_LAB_FORM_ID);
            dateReportedObs.setUuid(Converter.generateUUID());
            dateReportedObs.setProviderID(NMRS_PROVIDER_ID);
            dateReportedObs.setCreator(NMRS_CREATOR_ID);
            dateReportedObs.setVoided(VOIDED);
        }
        return dateReportedObs;
    }

    public Obs createDateSampleCollectedObs(LamisLabResult labResult, int locationID) {
        Obs dateSampleCollectObs = null;
        if (labResult.getDateCollected() != null) {
            dateSampleCollectObs = new Obs();
            dateSampleCollectObs.setPatientID(labResult.getPatientID());
            dateSampleCollectObs.setVisitDate(labResult.getDateCollected());
            dateSampleCollectObs.setConceptID(DATE_SAMPLE_COLLECTED_CONCEPT);
            dateSampleCollectObs.setValueDate(labResult.getDateCollected());
            dateSampleCollectObs.setDateEntered(labResult.getTimestamp());
            dateSampleCollectObs.setLocationID(locationID);
            dateSampleCollectObs.setFormID(NMRS_LAB_FORM_ID);
            dateSampleCollectObs.setUuid(Converter.generateUUID());
            dateSampleCollectObs.setProviderID(NMRS_PROVIDER_ID);
            dateSampleCollectObs.setCreator(NMRS_CREATOR_ID);
            dateSampleCollectObs.setVoided(VOIDED);
        }
        return dateSampleCollectObs;
    }

    public Obs createReportedByObs(LamisLabResult labResult, int locationID) {
        Obs reportedByObs = new Obs();
        reportedByObs.setPatientID(labResult.getPatientID());
        reportedByObs.setVisitDate(labResult.getDateCollected());
        reportedByObs.setConceptID(REPORTED_BY_CONCEPT_ID);
        reportedByObs.setValueText(REPORTED_BY_NAME);
        reportedByObs.setDateEntered(labResult.getTimestamp());
        reportedByObs.setLocationID(locationID);
        reportedByObs.setFormID(NMRS_LAB_FORM_ID);
        reportedByObs.setUuid(Converter.generateUUID());
        reportedByObs.setProviderID(NMRS_PROVIDER_ID);
        reportedByObs.setCreator(NMRS_CREATOR_ID);
        reportedByObs.setVoided(VOIDED);
        return reportedByObs;
    }

    public Obs createLabNumberObs(LamisLabResult labResult, int locationID) {
        Obs labNoObs = null;
        if (!StringUtils.isEmpty(labResult.getLabNo())) {
            labNoObs = new Obs();
            labNoObs.setPatientID(labResult.getPatientID());
            labNoObs.setVisitDate(labResult.getDateCollected());
            labNoObs.setConceptID(LAB_NUMBER_CONCEPT);
            labNoObs.setValueText(labResult.getLabNo());
            labNoObs.setDateEntered(labResult.getTimestamp());
            labNoObs.setLocationID(locationID);
            labNoObs.setFormID(NMRS_LAB_FORM_ID);
            labNoObs.setUuid(Converter.generateUUID());
            labNoObs.setProviderID(NMRS_PROVIDER_ID);
            labNoObs.setCreator(NMRS_CREATOR_ID);
            labNoObs.setVoided(VOIDED);
        }
        return labNoObs;
    }

    public Obs createDateOrderedObs(LamisLabResult labResult, int locationID) {
        Obs dateOrderedObs = null;
        if (labResult.getDateCollected() != null) {
            dateOrderedObs = new Obs();
            dateOrderedObs.setPatientID(labResult.getPatientID());
            dateOrderedObs.setVisitDate(labResult.getDateCollected());
            dateOrderedObs.setConceptID(DATE_ORDERED_CONCEPT);
            dateOrderedObs.setValueDate(labResult.getDateCollected());
            dateOrderedObs.setDateEntered(labResult.getTimestamp());
            dateOrderedObs.setLocationID(locationID);
            dateOrderedObs.setFormID(NMRS_LAB_FORM_ID);
            dateOrderedObs.setUuid(Converter.generateUUID());
            dateOrderedObs.setProviderID(NMRS_PROVIDER_ID);
            dateOrderedObs.setCreator(NMRS_CREATOR_ID);
            dateOrderedObs.setVoided(VOIDED);
        }
        return dateOrderedObs;
    }

    /*
        Ensure commment field contains something
        before calling this method
     */
    public Obs createIndicationObs(LamisLabResult labResult, int locationID) {
        Obs indicationForViralLoadTestObs = null;
        if (StringUtils.isNotEmpty(labResult.getComment()) && codedValueMap.containsKey(labResult.getComment())) {
            indicationForViralLoadTestObs = new Obs();
            indicationForViralLoadTestObs.setPatientID(labResult.getPatientID());
            indicationForViralLoadTestObs.setVisitDate(labResult.getDateCollected());
            indicationForViralLoadTestObs.setConceptID(COMMENT_CONCEPT);
            indicationForViralLoadTestObs.setValueCoded(getValueCoded(labResult.getComment()));
            indicationForViralLoadTestObs.setDateEntered(labResult.getTimestamp());
            indicationForViralLoadTestObs.setLocationID(locationID);
            indicationForViralLoadTestObs.setFormID(NMRS_LAB_FORM_ID);
            indicationForViralLoadTestObs.setUuid(Converter.generateUUID());
            indicationForViralLoadTestObs.setProviderID(NMRS_PROVIDER_ID);
            indicationForViralLoadTestObs.setCreator(NMRS_CREATOR_ID);
            indicationForViralLoadTestObs.setVoided(VOIDED);
        }
        return indicationForViralLoadTestObs;
    }

    public int getValueCoded(String lamisText) {
        return codedValueMap.get(lamisText);
    }

    public int getLabTestConceptID(LamisLabResult labResult) {
        int conceptID = 0;
        int testID = 0;
        for (LamisLabCoding labCoding : lamisLabCodingList) {
            if (labCoding.getLabTestID() == labResult.getLabtestID()) {
                if (StringUtils.isNotEmpty(labResult.getResultAbsolute())) {
                    conceptID = labCoding.getOpenMRSConceptAbsolute();
                } else if (StringUtils.isNotEmpty(labResult.getResultPercentage())) {
                    conceptID = labCoding.getOpenMRSConceptPercentage();
                }
            }
        }
        return conceptID;
    }

    public LamisLabCoding getCodingObj(int testID) {
        LamisLabCoding labCode = null;
        for (LamisLabCoding labCoding : lamisLabCodingList) {
            if (labCoding.getLabTestID() == testID) {
                labCode = labCoding;
            }
        }
        return labCode;
    }

    public int getCodedLabTestAnswer(LamisLabResult labResult) {
        int valueCoded = 0;
        int testID = labResult.getLabtestID();
        for (LamisLabCoding labCoding : lamisLabCodingList) {
            if (testID == labCoding.getLabTestID()) {
                if (labCoding.getDataType().equalsIgnoreCase("CODED")) {
                    if (labResult.getResultAbsolute().equalsIgnoreCase("-")) {
                        valueCoded = labCoding.getNegativeConceptID();
                    } else if (labResult.getResultAbsolute().equalsIgnoreCase("+")) {
                        valueCoded = labCoding.getPositiveConceptID();
                    }
                }
            }
        }
        return valueCoded;

    }

    public Obs createLabTestObs(LamisLabResult labResult, int locationID) {
        Obs labTestObs = null;
        LamisLabCoding labCode = getCodingObj(labResult.getLabtestID());
        if (labCode != null) {
            labTestObs = new Obs();
            labTestObs.setPatientID(labResult.getPatientID());
            labTestObs.setVisitDate(labResult.getDateCollected());
            labTestObs.setConceptID(getLabTestConceptID(labResult));
            //labTestObs.setValueDate(labResult.getDateReported());
            labTestObs.setDateEntered(labResult.getTimestamp());
            if (labCode.getDataType().equalsIgnoreCase("CODED")) {
                labTestObs.setValueCoded(getCodedLabTestAnswer(labResult));
            } else if (labCode.getDataType().equalsIgnoreCase("NUMERIC")) {
                if (StringUtils.isNotEmpty(labResult.getResultAbsolute()) && Converter.isValidDouble(labResult.getResultAbsolute())) {
                    labTestObs.setValueNumeric(Converter.convertToDouble(labResult.getResultAbsolute()));
                } else if (StringUtils.isNoneEmpty(labResult.getResultPercentage()) && Converter.isValidDouble(labResult.getResultAbsolute())) {
                    labTestObs.setValueNumeric(Converter.convertToDouble(labResult.getResultPercentage()));
                }
            }
            labTestObs.setLocationID(locationID);
            labTestObs.setFormID(NMRS_LAB_FORM_ID);
            labTestObs.setUuid(Converter.generateUUID());
            labTestObs.setProviderID(NMRS_PROVIDER_ID);
            labTestObs.setCreator(NMRS_CREATOR_ID);
            labTestObs.setVoided(VOIDED);
        }

        return labTestObs;
    }

    public List<Obs> convertToObs(LamisLabResult labResult, int locationID) {
        List<Obs> obsList = new ArrayList<Obs>();
        Obs dateOrderedObs, dateReportedObs, dateSampleCollectedObs,labNoObs, reportedByObs, indicationObs = null, labTestObs;
        dateOrderedObs = createDateOrderedObs(labResult, locationID);
        dateReportedObs = createDateReportedObs(labResult, locationID);
        dateSampleCollectedObs = createDateSampleCollectedObs(labResult, locationID);
        reportedByObs = createReportedByObs(labResult, locationID);
        labNoObs=createLabNumberObs(labResult, locationID);
        if (StringUtils.isNotEmpty(labResult.getComment())) {
            indicationObs = createIndicationObs(labResult, locationID);
        }
        labTestObs = createLabTestObs(labResult, locationID);
        obsList.add(labTestObs);
        obsList.add(dateOrderedObs);
        obsList.add(dateSampleCollectedObs);
        obsList.add(reportedByObs);
        obsList.add(dateReportedObs);
        obsList.add(labNoObs);
        if (indicationObs != null) {
            obsList.add(indicationObs);
        }
        return obsList;
    }

    public void closeAllResources() {
        mgr.closeAll();
    }

}
