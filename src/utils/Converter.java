/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import dictionaries.lamis.LamisLabDictionary;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import model.ConceptMap;
import model.Demographics;
import model.MapConfig;
import model.Obs;
import model.User;
import model.lamis.CareCardInitialCoding;
import model.lamis.HIVEnrollment;
import model.lamis.LamisClinical;
import model.lamis.LamisClinicalCoding;
import model.lamis.LamisDrug;
import model.lamis.LamisDrugCoding;
import model.lamis.LamisLabCoding;
import model.lamis.LamisLabResult;
import model.lamis.LamisRegimenMap;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author openmrsdev
 */
public class Converter {

    private static SimpleDateFormat formatter;
    private static DecimalFormat df = new DecimalFormat();
    private final static String USERSYSTEMIDPREFIX = "IHVN";
    private final static int ADMIN_USER=1;

    public static Date stringToDate(String dateString) {
        Date date = null;
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setLenient(false);
        if (dateString == null) {
            return date;
        }
        if (dateString.isEmpty()) {
            return date;
        }
        try {
            date = formatter.parse(dateString);
        } catch (ParseException ex) {
            date = null;

            return date;
        }
        return date;
    }

    public static java.sql.Date convertToSQLDate(java.util.Date olddate) {
        java.sql.Date sqlDate = null;
        if (olddate != null) {
            sqlDate = new java.sql.Date(olddate.getTime());
        }
        return sqlDate;
    }

    public static String generateUserSystemID(User usr) {
        String id = null;
        id = StringUtils.trim(USERSYSTEMIDPREFIX + "-" + usr.getUser_id() + "-" + usr.getPerson_id());
        return id;
    }

    public static String formatDateDDMMYYYY(Date date) {
        String dateString = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            dateString = df.format(date);
        }
        return dateString;

    }

    public static String formatDDMONYYYYHHMMSS(Date date) {
        String dateString = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            dateString = df.format(date);
        }
        return dateString;
    }

    public static String formatDateYYYYMMDD(Date date) {
        String dateString = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            dateString = df.format(date);
        }
        return dateString;

    }

    public static String formatDDMONYYYY(Date date) {
        String dateString = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            dateString = df.format(date);
        }
        return dateString;

    }

    public static String formatHHMMSSDDMONYYYYNoSlash(Date date) {
        String dateString = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat("hhmmssddMMMyyyy");
            dateString = df.format(date);
        }
        return dateString;
    }

    public static String codeGender(String gender) {
        String sex = "";
        if (gender.equalsIgnoreCase("Male")) {
            sex = "M";
        } else if (gender.equalsIgnoreCase("Female")) {
            sex = "F";
        } else if (gender.equalsIgnoreCase("M")) {
            sex = "M";
        } else if (gender.equalsIgnoreCase("F")) {
            sex = "F";
        }
        return sex;
    }

    public static List<Demographics> createDemographicsList(List<String[]> data) {
        List<Demographics> demoTempList = new ArrayList<Demographics>();
        Demographics demo;
        for (String[] ele : data) {
            demo = convertTodemographics(ele);
            demoTempList.add(demo);
        }
        return demoTempList;
    }

    public static int convertEventToInt(XMLEvent xmlEvent) {
        int val = 0;
        Characters eventCharacters = null;
        if (xmlEvent instanceof Characters) {
            eventCharacters = (Characters) xmlEvent;
            val = Converter.convertToInt(eventCharacters.getData());
        }
        return val;
    }

    public static double convertEventToDouble(XMLEvent xmlEvent) {
        double val = 0;
        Characters eventCharacters = null;
        if (xmlEvent instanceof Characters) {
            eventCharacters = (Characters) xmlEvent;
            val = Converter.convertToDouble(eventCharacters.getData());
        }
        return val;
    }

    public static boolean convertEventToBoolean(XMLEvent xmlEvent) {
        boolean ans = false;
        Characters eventCharacters = null;
        if (xmlEvent instanceof Characters) {
            eventCharacters = (Characters) xmlEvent;
            ans = Converter.convertToBoolean(eventCharacters.getData());
        }
        return ans;
    }

    public static String convertEventToString(XMLEvent xmlEvent) {
        String val = null;
        Characters eventCharacters = null;
        if (xmlEvent instanceof Characters) {
            eventCharacters = (Characters) xmlEvent;
            val = eventCharacters.getData();
        }
        return val;
    }

    public static Date convertEventToDate(XMLEvent xmlEvent) {
        Date dateVal = null;
        Characters eventCharacters = null;
        if (xmlEvent instanceof Characters) {
            eventCharacters = (Characters) xmlEvent;
            dateVal = Converter.stringToDate(eventCharacters.getData());
        }
        return dateVal;
    }

    public static Demographics convertToLamisDemographics(String[] data, int locationID) {
        Demographics demo = new Demographics();
        demo.setPatientID(Converter.convertToInt(data[0]));
        demo.setPatientUUID(Converter.generateUUID());
        demo.setPepfarID(StringUtils.leftPad(data[3],5,"0"));
        demo.setHospID(data[2]);
        demo.setFirstName(Converter.UnscrambleCharacters(data[5]));
        demo.setLastName(Converter.UnscrambleCharacters(data[4]));
        if(!StringUtils.isEmpty(data[30])){
            demo.setAdultEnrollmentDt(Converter.stringToDate(data[30]));
        }else{
            demo.setAdultEnrollmentDt(Converter.stringToDate(data[23]));
        }
        
        demo.setDateOfBirth(Converter.stringToDate(data[7]));
        demo.setGender(Converter.codeGender(data[6]));
        demo.setAddress1(Converter.UnscrambleCharacters(data[13]));
        demo.setAddress_state(data[15]);
        demo.setAddress_lga(data[16]);
        demo.setPhone_number(Converter.UnscrambleNumbers(data[14]));
        demo.setCreatorID(ADMIN_USER);
        demo.setDateCreated(new Date());
        demo.setLocationID(locationID);
        return demo;
    }

    /*
    private String pepfarID;0
    private String hospID;1
    private String eHNID;2
    private String otherID;3
    private String firstName;4
    private String lastName;5
    private String middleName;6
    private Date adultEnrollmentDt;7
    private Date pmtctEnrollmentDt;8
    private Date dateOfBirth;9
    private String gender;10
    private String address1;11
    private String address2;12
    private String address_lga;13
    private String address_state;14
    private int creatorID;15
    private Date dateCreated;16
    private int locationID;17
    private Date enrollDate;18
    private Date artStartDate;19
    private String errorString;20
     */
    public static Demographics convertTodemographics(String[] ele) {
        Demographics demo = new Demographics();
        demo.setPatientID(convertToInt(ele[0]));
        demo.setPatientUUID(ele[1]);
        demo.setPepfarID(ele[2]);
        demo.setHospID(ele[3]);
        demo.seteHNID(ele[4]);
        demo.setOtherID(ele[5]);
        demo.setFirstName(ele[6]);
        demo.setLastName(ele[7]);
        demo.setMiddleName(ele[8]);
        demo.setAdultEnrollmentDt(stringToDate(ele[9]));
        demo.setPepEnrollmentDt(stringToDate(ele[10]));
        demo.setPmtctEnrollmentDt(stringToDate(ele[11]));
        demo.setHeiEnrollmentDt(stringToDate(ele[12]));
        demo.setPepEnrollmentDt(stringToDate(ele[13]));
        demo.setDateOfBirth(stringToDate(ele[14]));
        demo.setAge(Integer.parseInt(ele[15]));
        demo.setGender(ele[16]);
        demo.setAddress1(ele[17]);
        demo.setAddress2(ele[18]);
        demo.setAddress_lga(ele[19]);
        demo.setAddress_state(ele[20]);
        //demo.setCreatorID(Integer.parseInt(ele[21]));
        int creator = convertToInt(ele[15]);
        if (creator != 0) {
            demo.setCreatorID(creator);
            demo.setDateCreated(stringToDate(ele[16]));
        }
        demo.setDateChanged(stringToDate(ele[17]));
        int locationID = convertToInt(ele[18]);
        demo.setLocationID(locationID);
        demo.setCreatorName(ele[19]);
        demo.setLocationName(ele[20]);

        //demo.setArtStartDate(stringToDate(ele[18]));
        demo.addError(ele[21]);
        return demo;
    }

    public Obs createObs(int patientID, String pepfarID, int locationID, Date visitDate, int conceptID, int providerID, int formID, int encounterID, int creator, Date dateCreated, double valueNumeric, Date valueDate, String valueText, int valueCoded) {
        Obs obs = new Obs();
        obs.setPatientID(patientID);
        obs.setPepfarID(pepfarID);
        obs.setLocationID(locationID);
        obs.setVisitDate(visitDate);
        obs.setConceptID(conceptID);
        obs.setProviderID(providerID);
        obs.setFormID(formID);
        obs.setEncounterID(encounterID);
        obs.setCreator(creator);
        obs.setDateEntered(dateCreated);
        obs.setValueCoded(valueCoded);
        obs.setValueDate(valueDate);
        obs.setValueNumeric(valueNumeric);
        obs.setValueText(valueText);
        return obs;
    }

    public static int convertToInt(String val) {
        int id = 0;
        if (!val.isEmpty()) {
            try {
                id = Integer.parseInt(StringUtils.replacePattern(StringUtils.trim(val), "[^0-9]", ""));
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
        return id;
    }

    public static double convertToDouble(String txt) {
        double val = 0.0;
        if (!txt.isEmpty() && isValidDouble(txt)) {
            try {
                val = Double.parseDouble(txt);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                //System.out.println(nfe.getMessage());
            }
        }
        return val;
    }

    public static boolean isValidDouble(String numStr) {
        boolean ans = true;
        double num = 0;
        if (!numStr.isEmpty()) {
            try {
                num = Double.parseDouble(numStr);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                ans = false;
            }
        }
        return ans;
    }

    public static boolean isValidInteger(String numStr) {
        boolean ans = true;
        int num = 0;
        if (!numStr.isEmpty()) {
            try {
                num = Integer.parseInt(numStr);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                ans = false;
            }
        }
        return ans;
    }

    public static String generateUUID() {
        UUID uid;
        uid = UUID.randomUUID();
        String uidStr = String.valueOf(uid);
        return uidStr;
    }

    public static boolean convertToBoolean2(String ele) {
        boolean ans = true;
        int num = convertToInt(ele);
        if (num == 0) {
            ans = false;
        }
        return ans;
    }

    public static boolean convertToBoolean(String ele) {
        boolean ans = false;
        if (ele != null) {
            ans = Boolean.valueOf(ele);
        }
        return ans;
    }

    public static MapConfig convertToMapConfig(String[] data) {
        MapConfig mapConfig = new MapConfig();
        mapConfig.setFormID(convertToInt(data[0]));
        mapConfig.setCsvFileName(data[1]);
        mapConfig.setMigrate(Converter.convertToBoolean(data[2]));
        return mapConfig;

    }

    public static Map<Integer, MapConfig> convertToMapConfigMap(List<String[]> dataArr) {
        MapConfig config = null;
        Map<Integer, MapConfig> configMap = new HashMap<Integer, MapConfig>();
        for (String[] ele : dataArr) {
            config = convertToMapConfig(ele);
            configMap.put(config.getFormID(), config);
        }
        return configMap;
    }

    /*
    private int omrsFormID;
    private boolean Allowed;
    private int omrsConceptID;
    private int isAnswerToWhichOMRSConcept;
    private int nmrsFormID;
    private int nmrsConceptID;
    private int isAnswerToWhichNMRSConcept;
     */
    public static ConceptMap convertToConceptMap(String[] data) {
        ConceptMap conceptMap = new ConceptMap();
        conceptMap.setOmrsFormID(convertToInt(StringUtils.trim(data[0])));
        conceptMap.setOmrsConceptID(convertToInt(StringUtils.trim(data[1])));
        conceptMap.setOmrsConceptName(StringUtils.trim(data[2]));
        conceptMap.setOmrsConceptDataType(StringUtils.trim(data[3]));
        conceptMap.setOmrsQuestionConcept(convertToInt(StringUtils.trim(data[4])));
        conceptMap.setNmrsFormID(convertToInt(StringUtils.trim(data[5])));
        conceptMap.setNmrsConceptID(convertToInt(StringUtils.trim(data[6])));
        conceptMap.setNmrsConceptName(StringUtils.trim(data[7]));
        conceptMap.setNmrsConceptDataType(StringUtils.trim(data[8]));
        conceptMap.setNmrsQuestionConcept(convertToInt(StringUtils.trim(data[9])));
        return conceptMap;
    }

    public static List<ConceptMap> convertToConceptMapList(List<String[]> dataArr) {
        List<ConceptMap> conceptMapList = new ArrayList<ConceptMap>();
        for (String[] ele : dataArr) {
            conceptMapList.add(convertToConceptMap(ele));
        }
        return conceptMapList;
    }
     public static String UnscrambleNumbers(String str)
        {
            if (str == null) return "";
            str = str.replace("^", "1");
            str = str.replace("~", "2");
            str = str.replace("`", "3");
            str = str.replace("*", "5");
            str = str.replace("$", "6");
            str = str.replace("#", "7");
            str = str.replace("@", "8");
            str = str.replace("!", "9");
            return str;
        }
      public static String UnscrambleCharacters(String str)
        {
            if (str == null) return "";
            str = str.replace("^", "a");
            str = str.replace("~", "b");
            str = str.replace("`", "c");
            str = str.replace("*", "e");
            str = str.replace("$", "f");
            str = str.replace("#", "g");
            str = str.replace("@", "h");
            str = str.replace("!", "i");
            str = str.replace("%", "j");
            str = str.replace("|", "k");
            str = str.replace("}", "n");
            str = str.replace("{", "o");
            return str.toUpperCase();
        }
    public static List<CareCardInitialCoding> convertToCareCardInitialCodingList(List<String[]> dataArrList) {
        List<CareCardInitialCoding> codingList = new ArrayList<CareCardInitialCoding>();
        CareCardInitialCoding coding = null;
        for (String[] ele : dataArrList) {
            coding = convertToCareCardInitialCoding(ele);
            codingList.add(coding);
        }
        return codingList;
    }

    public static CareCardInitialCoding convertToCareCardInitialCoding(String[] dataArr) {
        CareCardInitialCoding coding = new CareCardInitialCoding();
        coding.setId(Converter.convertToInt(dataArr[0]));
        coding.setLamisQuestion(dataArr[1]);
        coding.setLamisAnswer(dataArr[2]);
        coding.setNmrsQuestionConceptID(Converter.convertToInt(dataArr[3]));
        coding.setNmrsAnswerConceptID(Converter.convertToInt(dataArr[4]));
        return coding;
    }

    public static List<LamisLabCoding> convertToLamisLabCoding(List<String[]> dataArr) {
        List<LamisLabCoding> lamisLabCodeList = new ArrayList<LamisLabCoding>();
        LamisLabCoding lamisLabCode = null;
        for (String[] data : dataArr) {
            lamisLabCode = convertToLamisConceptMap(data);
            lamisLabCodeList.add(lamisLabCode);
        }
        return lamisLabCodeList;
    }

    public static List<HIVEnrollment> convertToHIVEnrollmentList(List<String[]> dataArrList) {
        List<HIVEnrollment> hivEnrollmentList = new ArrayList<HIVEnrollment>();
        HIVEnrollment hivEnrollment=null;
        for (String[] ele : dataArrList) {
           hivEnrollment=convertToHIVEnrollment(ele);
           hivEnrollmentList.add(hivEnrollment);
        }
        return hivEnrollmentList;
    }
    
    public static HIVEnrollment convertToHIVEnrollment(String[] dataArr) {
        HIVEnrollment hivEnrollment = new HIVEnrollment();
        hivEnrollment.setPatientID(Converter.convertToInt(dataArr[0]));
        hivEnrollment.setFacilityID(Converter.convertToInt(dataArr[1]));
        //hivEnrollment.setFacilityName(dataArr[1]);
        hivEnrollment.setState(dataArr[15]);
        hivEnrollment.setLga(dataArr[16]);
        
        hivEnrollment.setHospNo(dataArr[2]);
        hivEnrollment.setUniqueID(StringUtils.leftPad(dataArr[3], 5, "0"));
        hivEnrollment.setSurname(Converter.UnscrambleCharacters(dataArr[4]));
        hivEnrollment.setOtherNames(Converter.UnscrambleCharacters(dataArr[5]));
        hivEnrollment.setDateOfBirth(Converter.stringToDate(dataArr[7]));
        hivEnrollment.setAge(Converter.convertToInt(dataArr[8]));
        hivEnrollment.setAgeUnit(dataArr[9]);
        hivEnrollment.setGender(dataArr[6]);
        hivEnrollment.setMaritalStatus(dataArr[10]);
        hivEnrollment.setEducation(dataArr[11]);
        hivEnrollment.setOccupation(dataArr[12]);
        hivEnrollment.setStateOfResidence(dataArr[15]);
        hivEnrollment.setLgaOfResidence(dataArr[16]);
        hivEnrollment.setAddress(Converter.UnscrambleCharacters(dataArr[13]));
        hivEnrollment.setPhone(Converter.UnscrambleNumbers(dataArr[14]));
        hivEnrollment.setCareEntryPoint(dataArr[21]);
        //if(StringUtils.isEmpty(dataArr[21])){
            hivEnrollment.setDateConfirmedHIVTest(Converter.stringToDate(dataArr[23]));
        //}else{
            //hivEnrollment.setDateConfirmedHIVTest(Converter.stringToDate(dataArr[21]));
        //}
        //hivEnrollment.setDateConfirmedHIVTest(Converter.stringToDate(dataArr[21]));
        hivEnrollment.setDateRegistration(Converter.stringToDate(dataArr[30]));
        hivEnrollment.setStatusAtRegistration(dataArr[31]);
        hivEnrollment.setCurrentStatus(dataArr[36]);
        hivEnrollment.setDateCurrentStatus(Converter.stringToDate(dataArr[37]));
        hivEnrollment.setArtStartDate(Converter.stringToDate(dataArr[35]));
        //hivEnrollment.setBaselineCD4(Converter.convertToDouble(dataArr[27]));
        //hivEnrollment.setBaselineCD4Percent(Converter.convertToDouble(dataArr[28]));
        //hivEnrollment.setSystolicBP(Converter.convertToDouble(dataArr[29]));
        //hivEnrollment.setDiastolicBP(Converter.convertToDouble(dataArr[30]));
        //hivEnrollment.setBaselineClinicStage(dataArr[31]);
        //hivEnrollment.setBaselineFunctionalStatus(dataArr[32]);
        //hivEnrollment.setBaselineWeight(Converter.convertToDouble(dataArr[33]));
        //hivEnrollment.setBaselineHeight(Converter.convertToDouble(dataArr[34]));
        //hivEnrollment.setFirstRegimenLine(dataArr[35]);
        //hivEnrollment.setFirstRegimen(dataArr[36]);
        //hivEnrollment.setFirstNRTI(dataArr[37]);
        //hivEnrollment.setFirstNNRTI(dataArr[38]);
        hivEnrollment.setCurrentRegimenLine(dataArr[38]);
        hivEnrollment.setCurrentRegimen(dataArr[39]);
        //hivEnrollment.setCurrentNRTI(dataArr[41]);
        //hivEnrollment.setCurrentNNRTI(dataArr[42]);
        //hivEnrollment.setDateSubstitutedOrSwitched(Converter.stringToDate(dataArr[43]));
        hivEnrollment.setDateOfLastRefill(Converter.stringToDate(dataArr[48]));
        hivEnrollment.setLastRefillDurationDays(Converter.convertToDouble(dataArr[50]));
        hivEnrollment.setDateOfNextRefill(Converter.stringToDate(dataArr[49]));
        hivEnrollment.setLastClinicStage(dataArr[40]);
        hivEnrollment.setDateOfLastClinic(Converter.stringToDate(dataArr[52]));
        hivEnrollment.setDateOfNextClinic(Converter.stringToDate(dataArr[53]));
        hivEnrollment.setLastCD4(Converter.convertToDouble(dataArr[42]));
        hivEnrollment.setLastCD4p(Converter.convertToDouble(dataArr[43]));
        hivEnrollment.setDateOfLastCD4(Converter.stringToDate(dataArr[44]));
        hivEnrollment.setLastViralLoad(Converter.convertToDouble(dataArr[41]));
        hivEnrollment.setDateOfLastViralLoad(Converter.stringToDate(dataArr[45]));
        hivEnrollment.setViralLoadDueDate(Converter.stringToDate(dataArr[46]));
        hivEnrollment.setViralLoadType(dataArr[47]);
        //hivEnrollment.setSmsConsent(dataArr[57]);
        hivEnrollment.setNextOfKinName(Converter.UnscrambleCharacters(dataArr[17]));
        hivEnrollment.setNextOfKinAddress(Converter.UnscrambleCharacters(dataArr[18]));
        hivEnrollment.setNextOfKinPhone(Converter.UnscrambleNumbers(dataArr[19]));
        hivEnrollment.setNextOfKinrelationship(dataArr[20]);
        return hivEnrollment;
    }

    /*
       LABORATORY_ID	0
PATIENT_ID	1
FACILITY_ID	2
DATE_REPORTED	3
DATE_COLLECTED	4
LABNO	5
RESULTAB	6
RESULTPC	7
COMMENT	8
LABTEST_ID	9
TIME_STAMP	10
UPLOADED	11
TIME_UPLOADED	12
USER_ID	13

     */
    public static LamisLabResult convertToLamisLabResult(String[] data) {
        LamisLabResult labResult = new LamisLabResult();
        labResult.setLaboratoryID(convertToInt(data[0]));
        labResult.setPatientID(convertToInt(data[1]));
        labResult.setFacilityID(convertToInt(data[2]));
        labResult.setDateReported(stringToDate(data[3]));
        if (StringUtils.isNotEmpty(data[4])) {
            labResult.setDateCollected(stringToDate(data[4]));
        } else {
            labResult.setDateCollected(stringToDate(data[3]));
        }
        labResult.setLabNo(data[5]);
        String resultAbs = "";
        if (StringUtils.equalsIgnoreCase(data[6], "TND")) {
            resultAbs = "20";
        } else if (StringUtils.equalsIgnoreCase(data[6], "rpt")) {
            resultAbs = "";
        } else if (StringUtils.equalsIgnoreCase(data[6], "<20")) {
            resultAbs = "20";
        } else if (StringUtils.containsIgnoreCase(data[6], "<")) {
            resultAbs = "20";
        } else {
            resultAbs = data[6];
        }
        labResult.setResultAbsolute(resultAbs);
        labResult.setResultPercentage(data[7]);
        labResult.setComment(data[8]);
        labResult.setLabtestID(convertToInt(data[9]));
        labResult.setTimestamp(stringToDate(data[10]));
        labResult.setUploadTime(stringToDate(data[12]));
        labResult.setUserID(convertToInt(data[13]));
        return labResult;
    }

    /*
    CLINIC_ID	0
PATIENT_ID	1
FACILITY_ID	2
DATE_VISIT	3
CLINIC_STAGE	4
FUNC_STATUS	5
TB_STATUS	6
VIRAL_LOAD	7
CD4	8
CD4P	9
REGIMENTYPE	10
REGIMEN	11
BODY_WEIGHT	12
HEIGHT	13
WAIST	14
BP	15
PREGNANT	16
LMP	17
BREASTFEEDING	18
OI_SCREENED	19
OI_IDS	20
ADR_SCREENED	21
ADR_IDS	22
ADHERENCE_LEVEL	23
ADHERE_IDS	24
COMMENCE	25
NEXT_APPOINTMENT	26
NOTES	27
TIME_STAMP	28
UPLOADED	29
TIME_UPLOADED	30
USER_ID	31

     */
    public static LamisClinical convertToLamisClinical(String[] data) {
        LamisClinical clinical = new LamisClinical();
        clinical.setClinicalID(Converter.convertToInt(data[0]));
        clinical.setPatientID(Converter.convertToInt(data[1]));
        clinical.setFacilityID(Converter.convertToInt(data[2]));
        clinical.setVisitDate(Converter.stringToDate(data[3]));
        clinical.setClinicalStage(data[4]);
        clinical.setFunctionalStatus(data[5]);
        clinical.setTbStatus(data[6]);
        clinical.setViralLoad(Converter.convertToDouble(data[7]));
        clinical.setCd4Count(Converter.convertToDouble(data[8]));
        clinical.setCd4Percent(Converter.convertToDouble(data[9]));
        clinical.setRegimenType(data[10]);
        clinical.setRegimen(data[11]);
        clinical.setWeight(Converter.convertToDouble(data[12]));
        clinical.setHeight(Converter.convertToDouble(data[13]));
        clinical.setWaist(Converter.convertToDouble(data[14]));
        clinical.setBp(data[15]);
        clinical.setPregnant(data[16]);
        clinical.setLMP(Converter.stringToDate(data[17]));
        clinical.setBreastFeeding(data[18]);
        clinical.setOiScreen(data[19]);
        clinical.setStiID(data[20]);
        clinical.setStiTreated(data[21]);
        clinical.setOiID(data[22]);
        clinical.setAdverseDrugScreened(data[23]);
        clinical.setAdverseDrugReactionID(data[24]);
        clinical.setAdherenceLevel(data[25]);
        clinical.setAdherenceID(data[26]);
        clinical.setCommence(data[27]);
        clinical.setNextAppointmentDate(Converter.stringToDate(data[28]));
        clinical.setNote(data[29]);
        clinical.setTimestamp(Converter.stringToDate(data[30]));
        clinical.setUploaded(data[31]);
        clinical.setTimeUploaded(Converter.stringToDate(data[32]));
        clinical.setUserID(Converter.convertToInt(data[33]));
        return clinical;
    }

    public static List<LamisDrug> converterToLamisDrugList(List<String[]> dataArr) {
        List<LamisDrug> lamisDrugList = new ArrayList<LamisDrug>();
        LamisDrug drug = null;
        for (String[] data : dataArr) {
            drug = convertToLamisDrug(data);
            lamisDrugList.add(drug);
        }
        return lamisDrugList;
    }

    public static List<LamisDrugCoding> converterToLamisDrugCodingList(List<String[]> dataArr) {
        List<LamisDrugCoding> lamisCodingList = new ArrayList<LamisDrugCoding>();
        LamisDrugCoding drugCoding = null;
        for (String[] data : dataArr) {
            drugCoding = convertToLamisDrugCoding(data);
            lamisCodingList.add(drugCoding);
        }
        return lamisCodingList;
    }

    public static List<LamisRegimenMap> converterToLamisRegimenCodingList(List<String[]> dataArr) {
        List<LamisRegimenMap> lamisRegimenCodingList = new ArrayList<LamisRegimenMap>();
        LamisRegimenMap regimenMap = null;
        for (String[] data : dataArr) {
            regimenMap = convertToLamisRegimenCoding(data);
            lamisRegimenCodingList.add(regimenMap);
            //lamisCodingList.add(drugCoding);
        }
        return lamisRegimenCodingList;
    }

    public static LamisRegimenMap convertToLamisRegimenCoding(String[] data) {
        LamisRegimenMap regimenMap = new LamisRegimenMap();
        regimenMap.setColumnName(data[0]);
        regimenMap.setLamisVariableName(data[1]);
        regimenMap.setLamisAnswerName(data[2]);
        regimenMap.setLamisAnswerCode(data[3]);
        regimenMap.setLamisQuestionCode(data[4]);
        regimenMap.setNmrsConceptID(Converter.convertToInt(data[5]));
        regimenMap.setNmrsAnswerConceptID(Converter.convertToInt(data[6]));
        return regimenMap;
    }

    public static String trim(String val) {
        return StringUtils.trim(val);
    }

    public static LamisDrugCoding convertToLamisDrugCoding(String[] data) {

        LamisDrugCoding drugCoding = new LamisDrugCoding();
        drugCoding.setDrugID(Converter.convertToInt(data[0]));
        drugCoding.setAbbreviation(trim(data[1]));
        drugCoding.setDrugName(trim(data[2]));
        drugCoding.setPacksize(trim(data[3]));
        drugCoding.setDrugStrength(trim(data[4]));
        drugCoding.setDoseForm(trim(data[5]));
        drugCoding.setMorning(trim(data[6]));
        drugCoding.setAfternoon(trim(data[7]));
        drugCoding.setEvening(trim(data[8]));
        drugCoding.setItemID(Converter.convertToInt(data[9]));
        drugCoding.setOmrsQuestionConceptID(Converter.convertToInt(data[10]));
        drugCoding.setOmrsDrugConceptID(Converter.convertToInt(data[11]));
        drugCoding.setStrengthConceptID(Converter.convertToInt(data[12]));
        drugCoding.setGroupingConceptID(Converter.convertToInt(data[13]));
        return drugCoding;
    }

    public static LamisDrug convertToLamisDrug(String[] data) {
        LamisDrug drug = new LamisDrug();
        drug.setPharmacyID(Converter.convertToInt(data[0]));
        drug.setPatientID(Converter.convertToInt(data[1]));
        drug.setFacilityID(Converter.convertToInt(data[2]));
        //System.out.print(data[3]);
        drug.setVisitDate(Converter.stringToDate(data[3]));
        drug.setDuration(Converter.convertToInt(data[4]));
        drug.setMorningDose(Converter.convertToDouble(data[5]));
        drug.setAfternoonDose(Converter.convertToDouble(data[6]));
        drug.setEveningDose(Converter.convertToDouble(data[7]));
        drug.setAdrScreened(data[8]);
        drug.setAdrID(data[9]);
        drug.setPrescriptionError(data[10]);
        drug.setAdherence(data[11]);
        drug.setNextAppointmentDate(Converter.stringToDate(data[12]));
        drug.setRegimenTypeID(trim(data[13]));
        drug.setRegimenID(trim(data[14]));
        drug.setRegimenDrugID(Converter.convertToInt(data[15]));
        drug.setTimestamp(Converter.stringToDate(data[16]));
        drug.setUploaded(data[17]);
        drug.setTimeUploaded(Converter.stringToDate(data[18]));
        drug.setUserID(Converter.convertToInt(data[19]));
        drug.setDmocType(data[21]);
        return drug;
    }

    public static List<LamisClinical> converterToLamisClinicalList(List<String[]> dataList) {
        List<LamisClinical> clinicalList = new ArrayList<LamisClinical>();
        LamisClinical clinical = null;
        for (String[] data : dataList) {
            clinical = convertToLamisClinical(data);
            clinicalList.add(clinical);
        }
        return clinicalList;
    }

    /*
    VariableName
    VariablePosition
    DataType
    LamisAnswer
    OMRSConceptID
    OMRSAnswerID

     */
    public static LamisClinicalCoding convertToLamisClinicalCoding(String[] data) {
        LamisClinicalCoding clinicalCoding = new LamisClinicalCoding();
        clinicalCoding.setVariableName(data[0]);
        clinicalCoding.setVariablePosition(Converter.convertToInt(data[1]));
        clinicalCoding.setDataType(data[2]);
        clinicalCoding.setLamisAnswer(data[3]);
        clinicalCoding.setOMRSConceptID(Converter.convertToInt(data[4]));
        clinicalCoding.setOMRSAnswerID(Converter.convertToInt(data[5]));
        return clinicalCoding;
    }

    public static List<LamisClinicalCoding> convertToLamisClinicalCodingList(List<String[]> dataArr) {
        LamisClinicalCoding clinicalCoding = null;
        List<LamisClinicalCoding> clinicalCodingList = new ArrayList<LamisClinicalCoding>();
        for (String[] data : dataArr) {
            clinicalCoding = convertToLamisClinicalCoding(data);
            clinicalCodingList.add(clinicalCoding);
        }
        return clinicalCodingList;
    }

    public static List<LamisLabResult> convertToLamisLabResult(List<String[]> dataArr) {
        List<LamisLabResult> labResultList = new ArrayList<LamisLabResult>();
        LamisLabResult labResult = null;
        for (String[] data : dataArr) {
            //if (Converter.isValidDouble(data[6])) {
            labResult = convertToLamisLabResult(data);
            //}
            labResultList.add(labResult);
        }
        return labResultList;
    }

    public static LamisLabCoding convertToLamisConceptMap(String[] data) {
        LamisLabCoding llc = new LamisLabCoding();
        llc.setLabTestID(convertToInt(data[0]));
        llc.setLabTestCategoryID(convertToInt(data[1]));
        llc.setDescription(data[2]);
        llc.setMeasureAbsolute(data[3]);
        llc.setMeasurePercentage(data[4]);
        llc.setOpenMRSConceptAbsolute(convertToInt(data[5]));
        llc.setOpenMRSConceptPercentage(convertToInt(data[6]));
        llc.setPositiveConceptID(convertToInt(data[7]));
        llc.setNegativeConceptID(convertToInt(data[8]));
        llc.setDataType(data[9]);
        return llc;
    }
    

}
