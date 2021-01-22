/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daos;

import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import dictionaries.MasterDictionary;
import dictionaries.lamis.LamisARTCommencementDictionary;
import dictionaries.lamis.LamisClinicalDictionary;
import dictionaries.lamis.LamisDemographicsDictionary;
import dictionaries.lamis.LamisDrugDictionary;
import dictionaries.lamis.LamisEnrollmentDictionary;
import dictionaries.lamis.LamisLabDictionary;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import model.ConnectionParameters;
import model.DisplayScreen;
import model.Location;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import model.Demographics;
import model.Encounter;
import model.EncounterProvider;
import model.Obs;
import model.Provider;
import model.User;
import model.Visit;
import model.lamis.HIVEnrollment;
import model.lamis.LamisClinical;
import model.lamis.LamisDrug;
import model.lamis.LamisDrugCoding;
import model.lamis.LamisLabResult;
import org.apache.commons.lang3.StringUtils;
import utils.Converter;
import utils.FileManager;
import utils.Validator;

/**
 *
 * @author The Bright
 */
public class ImportDAO {

    private static final String OPENMRS_USER = "admin";
    private static final String OPENMRS_PASS = "Admin123";
    private DisplayScreen screen;
    private Connection connection;
    private final static String USERENCRYPTPASSWORD = "9caac041e4f0e1834c28e5b51c985881438450d7c6c0d302dc8a92247fa57eb1820fa817479df48f393e41ed5da0e13814b2d4a2667ba775ee03f4829701e858";
    private final static String USERENCRYPTSALT = "77eb9c12ad5335292bf6fae7f421a5983bc7ffcb02764ae2de6c758abdc536e4ac4a1b57e3609448eca9a39445742e906466b8f8d8950d1df8d09e4a151409dc";
    private final static int BIRTHDATEESTIMATED = 0;
    private final static int DEAD = 0;
    private final static int ADMIN_USER_ID = 1;
    private final static int USER_RETIRED = 0;
    private final static int PREFERRED = 1;
    private final static int HIV_CARE_PROGRAM = 1;
    private final static int PMTCT_PROGRAM = 2;
    private final static int HIV_EXPOSED_INFANT = 3;
    private final static int HIV_TESTING_SERVICES = 4;
    private final static int PEPFAR_IDENTIFIER = 4;
    private final static int OPENMRSID_IDENTIFIER = 1;
    private final static int HOSPID_IDENTIFIER = 5;
    private final static int SERVICE_PROVIDER_ENCOUNTER_ROLE = 3;
    private final static int DATAENTRY_PROVIDER_ENCOUNTER_ROLE = 1;
    private final static int VISIT_TYPE_ID = 1;
    private final static int VOIDED = 0;
    private final static int PHONE_ATTRIBUTE_TYPE_ID=8;
    private final static String ADDRESS_COUNTRY = "NIGERIA";
    private Set<Integer> providerIdSet = new HashSet<Integer>();
    private FileManager mgr;
    private MasterDictionary dictionary;
    private Map<Integer, User> usersMap = new HashMap<Integer, User>();
    Map<Integer, Provider> providerMap = new HashMap<Integer, Provider>();
    Map<Integer, Integer> encounterTypeIDMap = new HashMap<Integer, Integer>();
    private List<Integer> exemptFromDuplicateChecking = new ArrayList<Integer>();
    private int visitID;

    public ImportDAO() {
        //dictionary = new MasterDictionary();
        loadEncounterTypeIDMap();
    }

    public void registerDisplay(DisplayScreen screen) {
        this.screen = screen;
        //dictionary.setDisplayScreen(screen);
    }

    public boolean loadDriver() {
        boolean ans;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            ans = true;
            screen.updateStatus("Mysql jdbc driver loaded......");
        } catch (ClassNotFoundException e) {
            ans = false;
            screen.updateStatus(e.getMessage());
        } catch (InstantiationException ie) {
            System.err.println(ie.getMessage());
            ans = false;
            screen.updateStatus(ie.getMessage());
        } catch (IllegalAccessException iae) {
            System.err.println(iae.getMessage());
            screen.updateStatus(iae.getMessage());
            ans = false;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            screen.updateStatus(ex.getMessage());
            ans = false;
        }
        return ans;
    }

    public PreparedStatement prepareQuery(String query) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(query);
        } catch (SQLException e) {
            displayErrors(e);
            return ps;
        }
        return ps;
    }

    public void disableForeignKeyConstraint() {
        String query = "SET FOREIGN_KEY_CHECKS=0";
        PreparedStatement ps = prepareQuery(query);
        try {
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            processException(ex, query);
        }
    }

    private void processException(SQLException ex, String query) {
        screen.showError(ex.getMessage() + " " + query);
        displayErrors(ex);
    }

    public void loadProviderMap() {
        String sql_text = "select * from provider";
        Map<Integer, Provider> providerMap = new HashMap<Integer, Provider>();
        Provider provider = null;
        PreparedStatement ps = prepareQuery(sql_text);
        int count = 0;
        try {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                provider = new Provider();
                provider.setProviderID(rs.getInt("provider_id"));
                provider.setPersonID(rs.getInt("person_id"));
                provider.setProviderName(rs.getString("name"));
                provider.setIdentifier(rs.getString("identifier"));
                provider.setCreator(rs.getInt("creator"));
                provider.setDateCreated(rs.getDate("date_created"));
                provider.setRetired(rs.getInt("retired"));
                provider.setUuid(rs.getString("uuid"));
                provider.setProviderRoleID(rs.getInt("provider_role_id"));
                providerMap.put(provider.getPersonID(), provider);
                count++;
                screen.updateStatus("Loading provider..." + count);
            }
            cleanup(rs, ps);
        } catch (SQLException ex) {
            handleException(ex);
        }
        this.providerMap = providerMap;
    }

    public void loadUserMap() {
        String sql_text = "select * from users";
        Map<Integer, User> usersMap = new HashMap<Integer, User>();
        User user = null;
        PreparedStatement ps = prepareQuery(sql_text);
        int count = 0;
        try {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user = new User();
                user.setUser_id(rs.getInt("user_id"));
                user.setUserName(rs.getString("username"));
                user.setCreator(rs.getInt("creator"));
                user.setDateCreated(rs.getDate("date_created"));
                user.setPerson_id(rs.getInt("person_id"));
                usersMap.put(user.getUser_id(), user);
                count++;
                screen.updateStatus("Loading user..." + count);
            }
            cleanup(rs, ps);
        } catch (SQLException ex) {
            handleException(ex);
        }
        this.usersMap = usersMap;
    }

    public boolean connect(ConnectionParameters con) {
        boolean ans;
        Properties p = new Properties();
        p.setProperty("user", con.getUsername());
        p.setProperty("password", con.getPassword());
        p.setProperty("MaxPooledStatements", "200");
        p.setProperty("rewriteBatchedStatements", "true");
        try {
            //String conString = "jdbc:mysql://" + con.getHostIP()+ ":" + con.getPortNo()+ "/" + con.getDatabase() + "?user=" + con.getUsername() + "&password=" + con.getPassword();
            String conString = "jdbc:mysql://" + con.getHostIP() + ":" + con.getPortNo() + "/" + con.getDatabase();
            connection = DriverManager.getConnection(conString, p);
            ans = true;
            connection.setAutoCommit(false);
            disableForeignKeyConstraint();
        } catch (SQLException ex) {
            displayErrors(ex);
            ans = false;
        }
        return ans;
    }

    public void displayErrors(SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
        ex.printStackTrace();
    }

    public void closeConnections() {
        try {
            if (connection != null) {
                connection.commit();
                connection.close();
            }
        } catch (SQLException ex) {
            screen.showError(ex.getMessage());
        }
    }

    public void cleanup(ResultSet rs, Statement stmt) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException ex) {
            processException(ex, "");
        }
    }
    public void migrateARTCommencement(String csvFile,int locationID){
        int count = 0;
        int batch_count = 1;
        List<HIVEnrollment> hivEnrollmentList = null;
        LamisARTCommencementDictionary dictionary = new LamisARTCommencementDictionary();
        dictionary.setDisplayScreen(screen);
        int size = dictionary.countRecords(csvFile);
        screen.updateMinMaxProgress(0, size);
        List<String[]> dataArr = dictionary.readCSVData(csvFile);
        hivEnrollmentList = dictionary.convertToHIVEnrollments(dataArr);
        Set<Obs> obsListForMigration = new HashSet<Obs>();
        int num = 0;
        int obsListSize = 0;
        List<Obs> obsList = null;
        for (HIVEnrollment hIVEnrollment : hivEnrollmentList) {
            if (hIVEnrollment.getArtStartDate() != null) {
                obsList = dictionary.convertToObsList(hIVEnrollment, locationID);
                System.out.println(hIVEnrollment.getPatientID()+" size "+ obsList.size());
                obsListForMigration.addAll(obsList);
                num++;
                screen.updateProgress(num);
                if (num % 50 == 0) {
                    migrateMigrateForms(obsListForMigration, locationID);
                    screen.updateStatus(num + " ARTCommencement migrated of " + size);
                    obsListForMigration.clear();
                }
            }else{
                System.out.println("ARTStartDate is empty");
            }
        }
        if (!obsListForMigration.isEmpty()) {
            migrateMigrateForms(obsListForMigration, locationID);
        }
        try {
            commitConnection();
        } catch (SQLException ex) {
            handleException(ex);
        }
        dictionary.closeAllResources();
    }
    public void migrateDemographics(List<Demographics> demoList, int locationID) {
        savePersons(demoList);
        savePatients(demoList);
        savePatientIdentifier(demoList, locationID);
        savePatientProgram(demoList, locationID);
        savePersonNames(demoList);
        savePersonAddress(demoList);
        savePersonAttribute(demoList);
    }

    public void migrateDrugs(String csvFile, int locationID) {
        migrateRegimen(csvFile, locationID);
        migrateRegimenDrugs(csvFile, locationID);
    }

    public void savePatientIdentifier(List<Demographics> demoList, int locationID) {
        String sql_text = "insert into patient_identifier (patient_id,identifier,identifier_type,preferred,location_id,creator,date_created,voided,uuid) values (?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = prepareQuery(sql_text);
        try {
            for (Demographics demo : demoList) {
                if (StringUtils.isNotEmpty(demo.getPepfarID())) {
                    ps.setInt(1, demo.getPatientID());
                    ps.setString(2, demo.getPepfarID());
                    ps.setInt(3, PEPFAR_IDENTIFIER);
                    ps.setInt(4, PREFERRED);
                    ps.setInt(5, locationID);
                    ps.setInt(6, ADMIN_USER_ID);
                    ps.setDate(7, Converter.convertToSQLDate(demo.getDateCreated()));
                    ps.setInt(8, VOIDED);
                    ps.setString(9, Converter.generateUUID());
                    ps.addBatch();
                }
                if (StringUtils.isNotEmpty(demo.geteHNID())) {
                    ps.setInt(1, demo.getPatientID());
                    ps.setString(2, demo.geteHNID());
                    ps.setInt(3, OPENMRSID_IDENTIFIER);
                    ps.setInt(4, 0);
                    ps.setInt(5, locationID);
                    ps.setInt(6, demo.getCreatorID());
                    ps.setDate(7, Converter.convertToSQLDate(demo.getDateCreated()));
                    ps.setInt(8, VOIDED);
                    ps.setString(9, Converter.generateUUID());
                    ps.addBatch();
                }
                if (StringUtils.isNotEmpty(demo.getHospID())) {
                    ps.setInt(1, demo.getPatientID());
                    ps.setString(2, demo.getHospID());
                    ps.setInt(3, HOSPID_IDENTIFIER);
                    ps.setInt(4, 0);
                    ps.setInt(5, locationID);
                    ps.setInt(6, demo.getCreatorID());
                    ps.setDate(7, Converter.convertToSQLDate(demo.getDateCreated()));
                    ps.setInt(8, VOIDED);
                    ps.setString(9, Converter.generateUUID());
                    ps.addBatch();
                }

            }
            ps.executeBatch();
            ps.close();
        } catch (SQLException ex) {
            handleException(ex);
        }

    }

    public int createOrReturnVisitID(Obs obs, int locationID) {
        int id = 0;
        Visit vst = createVisitFromObs(obs, locationID);
        if (isExisting(vst)) {
            id = getVisitID(vst.getDateStarted(), locationID);
        } else {
            id = migrateVisit(vst);
        }
        return id;
    }

    public int createOrReturnEncounter(Obs obs, int locationID, int visitID) {
        int id = 0;
        Encounter enc = createEncounterFromObs(obs, locationID);
        enc.setVisitID(visitID);
        if (isExisting(enc)) {
            id = getEncounterID(obs.getPatientID(), obs.getFormID(), obs.getVisitDate());
        } else {
            id = migrateEncounter(enc, locationID);
        }
        return id;
    }

    public boolean isExisting(LamisDrug drug, int locationID, LamisDrugDictionary dictionary, LamisDrugCoding drugCoding) {
        boolean ans = false;
        String sql_text = "select * from obs where person_id=? and concept_id=? and value_coded=? and obs_datetime=? and voided=0";
        PreparedStatement ps = prepareQuery(sql_text);
        ResultSet rs = null;
        try {
            ps.setInt(1, drug.getPatientID());
            ps.setInt(2, drugCoding.getOmrsQuestionConceptID());
            ps.setInt(3, drugCoding.getOmrsDrugConceptID());
            ps.setDate(4, Converter.convertToSQLDate(drug.getVisitDate()));
            rs = ps.executeQuery();
            while (rs.next()) {
                ans = true;
            }
            cleanup(rs, ps);
        } catch (SQLException ex) {
            handleException(ex);
        }
        drug.setExist(ans);
        return ans;
    }

    public void preporcessDrug(LamisDrug drug, int locationID, LamisDrugDictionary dictionary) {
        LamisDrugCoding drugCoding = dictionary.getDrugCoding(drug.getRegimenDrugID());
        Obs obs = null;
        int obs_id = 0;
        if (drugCoding != null && drug.getVisitDate() != null) {
            if (!isExisting(drug, locationID, dictionary, drugCoding)) {
                //Medication Grouping Concept
                obs = dictionary.createGroupingObs(drug, drugCoding.getGroupingConceptID(), locationID);
                migrateEncounterAndVisitForObs(obs, locationID);
                obs_id = migrateObs(obs, locationID);
                drug.setObsGroupID(obs_id);
            } else {
                drug.setExist(true);
            }

        }
    }

    public void migrateEncounterAndVisitForObs(Obs obs, int locationID) {
        int visitID = createOrReturnVisitID(obs, locationID);
        int encounterID = createOrReturnEncounter(obs, locationID, visitID);
        obs.setEncounterID(encounterID);
    }

    public void migrateRegimenDrugs(String csvFile, int locationID) {
        int count = 0;
        LamisDrugDictionary drugDictionary = new LamisDrugDictionary();
        drugDictionary.setDisplayScreen(screen);
        int size = drugDictionary.countRecords(csvFile);
        screen.updateMinMaxProgress(0, size);
        List<String[]> dataArr = drugDictionary.readCSVData(csvFile);
        List<LamisDrug> lamisDrugList = drugDictionary.convertToLamisDrugList(dataArr);
        Set<Obs> obsListForMigration = new HashSet<Obs>();
        int num = 0;
        int obsListSize = 0;
        List<Obs> obsList = null;
        for (LamisDrug lamisDrug : lamisDrugList) {

            preporcessDrug(lamisDrug, locationID, drugDictionary);
            obsList = drugDictionary.extractObsGroupForDrugs(lamisDrug, locationID);
            //System.out.println(obsList.size() + " list size");
            obsListForMigration.addAll(obsList);
            num++;
            screen.updateProgress(num);
            if (num % 200 == 0) {
                migrateMigrateForms(obsListForMigration, locationID);
                screen.updateStatus(num + " Drug migrated of " + size);
                obsListForMigration.clear();
            }
        }
        if (!obsListForMigration.isEmpty()) {
            migrateMigrateForms(obsListForMigration, locationID);
        }

    }

    public void migrateRegimen(String csvFile, int locationID) {
        int count = 0;
        LamisDrugDictionary drugDictionary = new LamisDrugDictionary();
        drugDictionary.setDisplayScreen(screen);
        int size = drugDictionary.countRecords(csvFile);
        screen.updateMinMaxProgress(0, size);
        List<String[]> dataArr = drugDictionary.readCSVData(csvFile);
        List<LamisDrug> lamisDrugList = drugDictionary.convertToLamisDrugList(dataArr);
        Set<Obs> obsListForMigration = new HashSet<Obs>();
        int num = 0;
        int obsListSize = 0;
        List<Obs> obsList = null;
        for (LamisDrug lamisDrug : lamisDrugList) {
            obsList = drugDictionary.extractOneTimeObsList(lamisDrug, locationID);
            //obsList.removeAll(Collections.singletonList(null));
            // if (!obsList.isEmpty()) {
            obsListForMigration.addAll(obsList);
            num++;
            //}
            screen.updateProgress(num);
            if (num % 200 == 0) {
                migrateMigrateForms(obsListForMigration, locationID);
                screen.updateStatus(num + " Regimen migrated of " + size);
                obsListForMigration.clear();
            }
        }
        if (!obsListForMigration.isEmpty()) {
            migrateMigrateForms(obsListForMigration, locationID);
        }
    }

    public void migrateClinicals(String csvFile, int locationID) {
        int count = 0;
        LamisClinicalDictionary clinicalDictionary = new LamisClinicalDictionary();
        clinicalDictionary.setDisplayScreen(screen);
        int size = clinicalDictionary.countRecords(csvFile);
        screen.updateMinMaxProgress(0, size);
        List<String[]> dataArr = clinicalDictionary.readCSVData(csvFile);
        List<LamisClinical> clinicalList = clinicalDictionary.convertToLamisClinicalList(dataArr);
        Set<Obs> obsListForMigration = new HashSet<Obs>();
        //screen.updateMinMaxProgress(0, size);
        int num = 0;
        int obsListSize = 0;
        List<Obs> obsList = null;
        for (LamisClinical clinical : clinicalList) {
            obsList = clinicalDictionary.convertToObsList(clinical, locationID);
            obsListSize = obsList.size();
            System.out.println(" List size: " + obsListSize);
            obsListForMigration.addAll(obsList);
            num++;
            screen.updateProgress(num);
            if (num % 200 == 0) {
                migrateMigrateForms(obsListForMigration, locationID);
                screen.updateStatus(num + " CareCardFollowUp migrated of " + size);
                obsListForMigration.clear();
            }
        }
        if (!obsListForMigration.isEmpty()) {
            migrateMigrateForms(obsListForMigration, locationID);
        }
        try {
            commitConnection();
        } catch (SQLException ex) {
            handleException(ex);
        }
        clinicalDictionary.closeAllResources();

    }

    public void migrateHIVEnrollments(String csvFile, int locationID) {
        int count = 0;
        int batch_count = 1;
        List<HIVEnrollment> hivEnrollmentList = null;
        LamisEnrollmentDictionary dictionary = new LamisEnrollmentDictionary();
        dictionary.setDisplayScreen(screen);
        int size = dictionary.countRecords(csvFile);
        screen.updateMinMaxProgress(0, size);
        List<String[]> dataArr = dictionary.readCSVData(csvFile);
        hivEnrollmentList = dictionary.convertToHIVEnrollments(dataArr);
        Set<Obs> obsListForMigration = new HashSet<Obs>();
        int num = 0;
        int obsListSize = 0;
        List<Obs> obsList = null;
        for (HIVEnrollment hIVEnrollment : hivEnrollmentList) {
            if (hIVEnrollment.getDateRegistration() != null) {
                obsList = dictionary.convertToObsList(hIVEnrollment, locationID);
                obsListForMigration.addAll(obsList);
                num++;
                screen.updateProgress(num);
                if (num % 200 == 0) {
                    migrateMigrateForms(obsListForMigration, locationID);
                    screen.updateStatus(num + " InitialCareCard migrated of " + size);
                    obsListForMigration.clear();
                }
            }
        }
        if (!obsListForMigration.isEmpty()) {
            migrateMigrateForms(obsListForMigration, locationID);
        }
        try {
            commitConnection();
        } catch (SQLException ex) {
            handleException(ex);
        }
        dictionary.closeAllResources();
    }

    public void migrateDemographics(String csvFile, int locationID) {
        File file = new File(csvFile);
        int count = 0;
        int batch_count = 1;
        List<Demographics> demoList = new ArrayList<Demographics>();
        List<Demographics> demoListPassValidation = new ArrayList<Demographics>();
        //Demographics demo = null;
        LamisDemographicsDictionary dictionary = new LamisDemographicsDictionary();
        dictionary.setDisplayScreen(screen);
        dictionary.initializeDemographicErrorLog();
        int size = dictionary.countRecords(csvFile);
        screen.updateMinMaxProgress(0, size);
        List<String[]> dataArr = dictionary.readDemographicsCSV(csvFile);
        demoList = dictionary.convertToDemographicsList(dataArr, locationID);
        preprocessDemographics(demoList);
        for (Demographics demo : demoList) {
            if (demo != null && demo.getPatientID() != 1 && StringUtils.isNotEmpty(demo.getHospID()) && demo.getDateOfBirth() != null && !demo.isExist()) {
                demoListPassValidation.add(demo);
                count++;
                screen.updateProgress(count);
                screen.updateStatus(count + " of " + size + " patient created in batch: " + batch_count);
                if ((count % 500) == 0) {

                    migrateDemographics(demoListPassValidation, locationID);
                    demoListPassValidation.clear();
                    batch_count++;
                }
            } else {
                dictionary.log(demo);
            }
        }
        if (!demoListPassValidation.isEmpty()) {
            migrateDemographics(demoListPassValidation, locationID);
        }
        try {
            commitConnection();
        } catch (SQLException ex) {
            handleException(ex);
        }
        dictionary.closeAllResources();
    }

    public void savePatients(List<Demographics> demoList) {
        String sql_text = "insert into patient (patient_id,creator,date_created,voided) values (?,?,?,?)";
        PreparedStatement ps = prepareQuery(sql_text);
        try {
            for (Demographics demo : demoList) {
                if (demo.getDateOfBirth() != null) {
                    ps.setInt(1, demo.getPatientID());
                    ps.setInt(2, ADMIN_USER_ID);
                    ps.setDate(3, Converter.convertToSQLDate(demo.getDateCreated()));
                    ps.setInt(4, VOIDED);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            ps.close();

        } catch (SQLException ex) {
            handleException(ex);
        }

    }

    public void savePersons(List<Demographics> demoList) {
        String sql_text = "insert into person (person_id,gender,birthdate,dead,creator,date_created,voided,uuid) values(?,?,?,?,?,?,?,?)";
        PreparedStatement ps = prepareQuery(sql_text);
        try {
            for (Demographics demo : demoList) {
                ps.setInt(1, demo.getPatientID());
                ps.setString(2, demo.getGender());
                ps.setDate(3, Converter.convertToSQLDate(demo.getDateOfBirth()));
                ps.setInt(4, DEAD);
                ps.setInt(5, ADMIN_USER_ID);
                ps.setDate(6, Converter.convertToSQLDate(demo.getDateCreated()));
                ps.setInt(7, VOIDED);
                ps.setString(8, demo.getPatientUUID());
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();

        } catch (SQLException ex) {
            handleException(ex);
        }
    }

    public void savePatientProgram(List<Demographics> demoList, int locationID) {
        enrollPatientsHIVCare(demoList, locationID);
        enrollPatientsToPMTCT(demoList, locationID);
    }

    public void enrollPatientsHIVCare(List<Demographics> demoList, int locationID) {
        String sql_text = "insert into patient_program (patient_id,program_id,date_enrolled,location_id,creator,date_created,voided,uuid) values (?,?,?,?,?,?,?,?)";
        PreparedStatement ps = prepareQuery(sql_text);
        try {
            for (Demographics demo : demoList) {
                if (demo.getAdultEnrollmentDt() != null) {
                    ps.setInt(1, demo.getPatientID());
                    ps.setInt(2, HIV_CARE_PROGRAM);
                    ps.setDate(3, Converter.convertToSQLDate(demo.getAdultEnrollmentDt()));
                    ps.setInt(4, locationID);
                    ps.setInt(5, ADMIN_USER_ID);
                    ps.setDate(6, Converter.convertToSQLDate(demo.getDateCreated()));
                    ps.setInt(7, VOIDED);
                    ps.setString(8, Converter.generateUUID());
                    ps.addBatch();
                }

            }
            ps.executeBatch();
            ps.close();
        } catch (SQLException ex) {
            handleException(ex);
        }
    }

    public void enrollPatientsToPMTCT(List<Demographics> demoList, int locationID) {
        String sql_text = "insert into patient_program (patient_id,program_id,date_enrolled,location_id,creator,date_created,voided,uuid) values (?,?,?,?,?,?,?,?)";
        PreparedStatement ps = prepareQuery(sql_text);
        try {
            for (Demographics demo : demoList) {
                if (demo.getPmtctEnrollmentDt() != null) {
                    ps.setInt(1, demo.getPatientID());
                    ps.setInt(2, PMTCT_PROGRAM);
                    ps.setDate(3, Converter.convertToSQLDate(demo.getPmtctEnrollmentDt()));
                    ps.setInt(4, locationID);
                    ps.setInt(5, ADMIN_USER_ID);
                    ps.setDate(6, Converter.convertToSQLDate(demo.getDateCreated()));
                    ps.setInt(7, VOIDED);
                    ps.setString(8, Converter.generateUUID());
                    ps.addBatch();
                }

            }
            ps.executeBatch();
            ps.close();
        } catch (SQLException ex) {
            handleException(ex);
        }
    }

    public void savePersonNames(List<Demographics> demoList) {
        String sql_text = "insert into person_name (preferred,given_name,family_name,creator,date_created,voided,uuid,person_id) values(?,?,?,?,?,?,?,?)";
        PreparedStatement ps = prepareQuery(sql_text);
        try {
            for (Demographics demo : demoList) {
                ps.setInt(1, PREFERRED);
                ps.setString(2, demo.getFirstName());
                ps.setString(3, demo.getLastName());
                ps.setInt(4, ADMIN_USER_ID);
                ps.setDate(5, Converter.convertToSQLDate(demo.getDateCreated()));
                ps.setInt(6, VOIDED);
                ps.setString(7, Converter.generateUUID());
                ps.setInt(8, demo.getPatientID());
                ps.addBatch();
            }

            ps.executeBatch();
            ps.close();
        } catch (SQLException ex) {
            handleException(ex);
        }
    }
    public void savePersonAttribute(List<Demographics> demoList){
        String sql_text="insert into person_attribute"
                + " (person_id,value,person_attribute_type_id,creator,date_created,voided,uuid)"
                + " values(?,?,?,?,?,?,?)";
        PreparedStatement ps=prepareQuery(sql_text);
        try{
            for(Demographics demo: demoList){
                ps.setInt(1, demo.getPatientID());
                ps.setString(2,demo.getPhone_number());
                ps.setInt(3, PHONE_ATTRIBUTE_TYPE_ID);
                ps.setInt(4,ADMIN_USER_ID);
                ps.setDate(5, Converter.convertToSQLDate(demo.getDateCreated()));
                ps.setInt(6,VOIDED);
                ps.setString(7, Converter.generateUUID());
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
        }catch(SQLException ex){
            handleException(ex);
        }
    }
    public void savePersonAddress(List<Demographics> demoList) {
        String sql_text = "insert into person_address "
                + "(person_id,preferred,address1,address2,"
                + "city_village,state_province,country,"
                + "creator,date_created,voided,uuid) "
                + "values(?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = prepareQuery(sql_text);
        try {
            for (Demographics demo : demoList) {
                ps.setInt(1, demo.getPatientID());
                ps.setInt(2, PREFERRED);
                ps.setString(3, demo.getAddress1());
                ps.setString(4, demo.getAddress2());
                ps.setString(5, demo.getAddress_lga());
                ps.setString(6, demo.getAddress_state());
                ps.setString(7, ADDRESS_COUNTRY);
                ps.setInt(8, ADMIN_USER_ID);
                ps.setDate(9, Converter.convertToSQLDate(demo.getDateCreated()));
                ps.setInt(10, VOIDED);
                ps.setString(11, Converter.generateUUID());
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
        } catch (SQLException ex) {
            handleException(ex);
        }

    }

    public void preprocessDemographics(List<Demographics> demoList) {
        int count = 0;
        for (Demographics demo : demoList) {
            if (isPatientExisting(demo.getPatientID())) {
                demo.setExist(true);
                count++;
                screen.updateStatus("Checking if exist: " + count);
            }
        }
    }

    public boolean isPatientExisting(int personID) {
        boolean ans = false;
        String sql_text = "select * from person where person_id=?";
        PreparedStatement ps = prepareQuery(sql_text);
        ResultSet rs = null;
        try {
            ps.setInt(1, personID);
            rs = ps.executeQuery();
            if (rs.next()) {
                ans = true;
            }
            cleanup(rs, ps);
        } catch (SQLException ex) {
            handleException(ex);
        }
        return ans;

    }

    public void handleException(Exception ex) {
        screen.updateStatus(ex.getMessage());
        ex.printStackTrace();
    }

    public boolean isProvider(int person_id) throws SQLException {
        boolean ans = false;
        String sql_text = "select user_id from users where person_id=?";
        PreparedStatement ps = prepareQuery(sql_text);
        ps.setInt(1, person_id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            ans = true;
        }
        cleanup(rs, ps);
        return ans;
    }

    public void migrateLabTests(String csvFileName, int locationID) {
        LamisLabDictionary dictionary = new LamisLabDictionary();
        dictionary.setDisplayScreen(screen);
        int count = dictionary.countRecords(csvFileName);
        List<String[]> dataArr = dictionary.readCSVData(csvFileName);
        List<LamisLabResult> labResultList = dictionary.convertToLamisLabResult(dataArr);
        Set<Obs> obsListForMigration = new HashSet<Obs>();
        screen.updateMinMaxProgress(0, count);
        int num = 0;
        for (LamisLabResult labResult : labResultList) {
            obsListForMigration.addAll(dictionary.convertToObs(labResult, locationID));
            num++;
            screen.updateProgress(num);
            if (num % 200 == 0) {
                migrateMigrateForms(obsListForMigration, locationID);
                screen.updateStatus(num + " Lab test migrated of " + count);
                obsListForMigration.clear();
            }
        }
         try {
            commitConnection();
        } catch (SQLException ex) {
            handleException(ex);
        }
        dictionary.closeAllResources();

    }

    public void savePersonName(List<User> userList) {
        String sql_text = "insert into person_name (preferred,person_id,given_name,family_name,creator,date_created,voided,uuid) values (?,?,?,?,?,?,?,?)";
        PreparedStatement ps = prepareQuery(sql_text);
        try {
            for (User usr : userList) {
                ps.setInt(1, PREFERRED);
                ps.setInt(2, usr.getPerson_id());
                ps.setString(3, usr.getFirstName());
                ps.setString(4, usr.getLastName());
                ps.setInt(5, ADMIN_USER_ID);
                ps.setDate(6, Converter.convertToSQLDate(new Date()));
                ps.setInt(7, VOIDED);
                ps.setString(8, Converter.generateUUID());
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
        } catch (SQLException ex) {
            handleException(ex);
        }

    }

    public void savePersonUsers(List<User> prsList) {
        String sql_text = "insert into person (person_id,gender,birthdate_estimated,dead,creator,date_created,voided,uuid) values (?,?,?,?,?,?,?,?)";
        PreparedStatement ps = prepareQuery(sql_text);
        try {
            for (User usr : prsList) {
                ps.setInt(1, usr.getPerson_id());
                ps.setString(2, usr.getGender());
                ps.setInt(3, BIRTHDATEESTIMATED);
                ps.setInt(4, DEAD);
                ps.setInt(5, ADMIN_USER_ID);
                ps.setDate(6, Converter.convertToSQLDate(new Date()));
                ps.setInt(7, VOIDED);
                ps.setString(8, Converter.generateUUID());
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
        } catch (SQLException ex) {
            handleException(ex);
        }
    }

    public void saveUsers(List<User> usrList) {
        String sql_text = "insert into users (user_id,system_id,username,password,salt,creator,date_created,person_id,retired,uuid) values (?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = prepareQuery(sql_text);
        try {
            for (User usr : usrList) {
                ps.setInt(1, usr.getUser_id());
                ps.setString(2, Converter.generateUserSystemID(usr));
                ps.setString(3, usr.getUserName());
                ps.setString(4, USERENCRYPTPASSWORD);
                ps.setString(5, USERENCRYPTSALT);
                ps.setInt(6, ADMIN_USER_ID);
                ps.setDate(7, Converter.convertToSQLDate(new Date()));
                ps.setInt(8, usr.getPerson_id());
                ps.setInt(9, USER_RETIRED);
                ps.setString(10, Converter.generateUUID());
                ps.addBatch();

            }
            ps.executeBatch();
            ps.close();
        } catch (SQLException ex) {
            handleException(ex);
        }
    }

    public boolean existsInList(List<User> usrList, int person_id) {
        boolean ans = false;
        for (User usr : usrList) {
            if (usr.getPerson_id() == person_id) {
                ans = true;
            }
        }
        return ans;
    }

    public void commitConnection() throws SQLException {
        if (connection != null) {
            connection.commit();
        }
    }

    public Set<Visit> createVisitSet(List<Obs> obsList, int locationID) {
        Set<Visit> visitSet = new HashSet<Visit>();
        Visit vst = null;
        for (Obs obs : obsList) {
            vst = createVisitFromObs(obs, locationID);
            visitSet.add(vst);
        }
        return visitSet;
    }

    public Set<Provider> createProviderList(List<Obs> obsList, int locationID) {
        Set<Provider> providerList = new HashSet<Provider>();
        Provider provider = null;
        User usr = null;
        for (Obs obs : obsList) {
            usr = usersMap.get(obs.getProviderID());
            provider = createProviderFromUser(usr);
            providerList.add(provider);
        }
        return providerList;
    }

    public Set<Encounter> createEncounterSet(List<Obs> obsList, int locationID) {
        Set<Encounter> encounterSet = new HashSet<Encounter>();
        Encounter enc = null;
        for (Obs obs : obsList) {
            enc = createEncounterFromObs(obs, locationID);
            encounterSet.add(enc);
        }
        return encounterSet;
    }

    /*public Encounter createEncounterOneObs(Obs obs,int locationID){
        Encounter enc = null;
        enc = createEncounterFromObs(obs, locationID);
        return enc;
    }*/
    //Create two EncounterProvider per 
    public EncounterProvider createEncounterProvider(Obs obs) {
        EncounterProvider encPro = new EncounterProvider();
        encPro.setEncounterID(obs.getEncounterID());
        //Go to the provider table with the person_id from obs.getProvider()
        //and retrieve the provider id 
        encPro.setProviderID(obs.getProviderID());
        encPro.setCreator(obs.getCreator());
        encPro.setVoided(VOIDED);
        encPro.setUuid(Converter.generateUUID());
        return encPro;

    }

    public Set<EncounterProvider> createEncounterProvider(List<Obs> obsList) {
        Set<EncounterProvider> encProSet = new HashSet<EncounterProvider>();
        EncounterProvider encPro = null;
        for (Obs obs : obsList) {
            encPro = createEncounterProvider(obs);
            encProSet.add(encPro);
        }
        return encProSet;
    }

    public Set<EncounterProvider> createEncounterProvider(Encounter enc) {
        Set<EncounterProvider> encProSet = new HashSet<EncounterProvider>();
        EncounterProvider dataEntryProviderEP = new EncounterProvider();
        EncounterProvider serviceProviderEP = new EncounterProvider();
        // Creating the serviceProvider EncounterProvider object
        serviceProviderEP.setEncounterID(enc.getEncounterID());
        Provider serviceProvider = providerMap.get(enc.getProviderID());
        int serviceProviderID = 1;
        if (serviceProvider != null) {
            serviceProviderID = serviceProvider.getProviderID();
        }
        serviceProviderEP.setProviderID(serviceProviderID);
        serviceProviderEP.setEncounterRoleID(SERVICE_PROVIDER_ENCOUNTER_ROLE);
        serviceProviderEP.setCreator(enc.getCreator());
        serviceProviderEP.setDateCreated(enc.getDateCreated());
        serviceProviderEP.setVoided(VOIDED);
        serviceProviderEP.setUuid(Converter.generateUUID());
        // Creating the dataEntryProvider EncounterProvider object
        dataEntryProviderEP.setEncounterID(enc.getEncounterID());
        User dataEntryUser = usersMap.get(enc.getCreator());
        int dataEntryUserID = 1;
        int dataEntryProviderID = 1;
        if (dataEntryUser != null) {
            dataEntryUserID = dataEntryUser.getPerson_id();
            Provider dataEntryProvider = providerMap.get(dataEntryUser.getUser_id());
            if (dataEntryProvider != null) {
                dataEntryProviderID = dataEntryProvider.getProviderID();
            }
        }
        dataEntryProviderEP.setProviderID(dataEntryProviderID);
        dataEntryProviderEP.setEncounterRoleID(DATAENTRY_PROVIDER_ENCOUNTER_ROLE);
        dataEntryProviderEP.setCreator(enc.getCreator());
        dataEntryProviderEP.setDateCreated(enc.getDateCreated());
        dataEntryProviderEP.setVoided(VOIDED);
        dataEntryProviderEP.setUuid(Converter.generateUUID());
        //Add the 2 EncounterProvider objects to a set and return set
        encProSet.add(serviceProviderEP);
        encProSet.add(dataEntryProviderEP);
        return encProSet;
    }

    public int getLastVisitID() throws SQLException {
        String sql_text = "select max(visit_id) id from visit where voided=0";
        PreparedStatement ps = prepareQuery(sql_text);
        ResultSet rs = ps.executeQuery();
        int last_id = 0;
        while (rs.next()) {
            last_id = rs.getInt("id");
        }
        return last_id;
    }

    public Set<EncounterProvider> createEncounterProvider(Set<Encounter> encSet) {
        Set<EncounterProvider> encProSet = new HashSet<EncounterProvider>();
        for (Encounter enc : encSet) {
            encProSet.addAll(createEncounterProvider(enc));
        }
        return encProSet;
    }

    public void migrateVisitSet(List<Obs> obsList, int locationID) {
        Set<Visit> visitSet = createVisitSet(obsList, locationID);
        preprocessVisits(visitSet);
        migrateVisits(visitSet);

    }

    public void migrateEncounterSet(List<Obs> obsList, int locationID) {
        Set<Encounter> encounterSet = createEncounterSet(obsList, locationID);
        preprocessEncounters(encounterSet);
        migrateEncounter(encounterSet, locationID);
    }

    public void migrateEncounterProviderSet(List<Obs> obsList, int locationID) {
        Set<Encounter> encounterSet = createEncounterSet(obsList, locationID);
        Set<EncounterProvider> providerSet = createEncounterProvider(encounterSet);
        preprocessEncounterProviders(providerSet);
        migrateEncounterProvider(providerSet);
    }

    public void migrateMigrateForms(Set<Obs> obsSet, int locationID) {
        List<Obs> obsList = new ArrayList<Obs>(obsSet);
        Set<Visit> visitSet = createVisitSet(obsList, locationID);

        Set<Encounter> encounterSet = createEncounterSet(obsList, locationID);

        //Set<EncounterProvider> providerSet = createEncounterProvider(encounterSet);
        preprocessVisits(visitSet);
        migrateVisits(visitSet);
        preprocessEncounters(encounterSet);
        migrateEncounter(encounterSet, locationID);
        postProcessEncounters(encounterSet);
        Set<EncounterProvider> providerSet = createEncounterProvider(encounterSet);
        preprocessEncounterProviders(providerSet);
        migrateEncounterProvider(providerSet);
        preprocessObsList(obsList);
        migrateObs(obsList, locationID);
    }

    public boolean isExistingVisit(Date startDate, int patientID) {
        boolean ans = false;
        String sql_text = "select visit_id from visit where patient_id=? and date_started=? and voided=0";
        PreparedStatement ps = prepareQuery(sql_text);
        ResultSet rs = null;
        try {
            ps.setInt(1, patientID);
            ps.setDate(2, Converter.convertToSQLDate(startDate));
            rs = ps.executeQuery();
            while (rs.next()) {
                ans = true;
            }
            cleanup(rs, ps);
        } catch (SQLException ex) {
            handleException(ex);
        } finally {
            cleanup(rs, ps);
        }
        return ans;
    }

    public int getEncounterID(int patientID, int formID, Date visitDate) {
        String sql_text = "select encounter_id from encounter where patient_id=? and encounter_datetime=? and form_id=? and voided=0";
        PreparedStatement ps = prepareQuery(sql_text);
        ResultSet rs = null;
        int encounterID = 0;
        try {
            ps.setInt(1, patientID);
            ps.setDate(2, Converter.convertToSQLDate(visitDate));
            ps.setInt(3, formID);
            rs = ps.executeQuery();
            while (rs.next()) {
                encounterID = rs.getInt("encounter_id");
            }
            cleanup(rs, ps);
        } catch (SQLException ex) {
            handleException(ex);
        }
        return encounterID;
    }

    public void postProcessEncounters(Set<Encounter> encounterSet) {
        for (Encounter enc : encounterSet) {
            setEncounterID(enc);
        }
    }

    public void setEncounterID(Encounter encounter) {
        int encounterID = getEncounterID(encounter.getPatientID(), encounter.getFormID(), encounter.getEncounterDatetime());
        encounter.setEncounterID(encounterID);
    }

    public boolean isExisting(Visit visit) {
        String sql_text = "select visit_id from visit where patient_id=? and date_started=? and voided=0";
        PreparedStatement ps = prepareQuery(sql_text);
        ResultSet rs = null;
        boolean ans = false;
        try {
            ps.setInt(1, visit.getPatientID());
            ps.setDate(2, Converter.convertToSQLDate(visit.getDateStarted()));
            rs = ps.executeQuery();
            while (rs.next()) {
                ans = true;
            }
            cleanup(rs, ps);
        } catch (SQLException ex) {
            handleException(ex);
        } finally {
            cleanup(rs, ps);
        }
        return ans;
    }

    public void preprocessVisits(Set<Visit> visitSet) {
        System.out.println("Processing visits...");
        if (!visitSet.isEmpty()) {
            for (Visit visit : visitSet) {
                if (isExisting(visit)) {
                    visit.setExists(true);
                }
            }
        }
        //return visitSet;
    }

    public boolean isExisting(Encounter encounter) {
        String sql_text = "select encounter_id from encounter where patient_id=? and form_id=? and encounter_datetime=? and voided=0";
        PreparedStatement ps = prepareQuery(sql_text);
        ResultSet rs = null;
        boolean ans = false;
        try {
            ps.setInt(1, encounter.getPatientID());
            ps.setInt(2, encounter.getFormID());
            ps.setDate(3, Converter.convertToSQLDate(encounter.getEncounterDatetime()));
            rs = ps.executeQuery();
            while (rs.next()) {
                ans = true;
            }
            cleanup(rs, ps);
        } catch (SQLException ex) {
            handleException(ex);
        } finally {
            cleanup(rs, ps);
        }
        return ans;
    }

    private void loadEncounterTypeIDMap() {
        encounterTypeIDMap.put(1, 7);
        encounterTypeIDMap.put(2, 3);
        encounterTypeIDMap.put(3, 2);
        encounterTypeIDMap.put(4, 1);
        encounterTypeIDMap.put(7, 5);
        encounterTypeIDMap.put(8, 18);
        encounterTypeIDMap.put(10, 2);
        encounterTypeIDMap.put(13, 15);
        encounterTypeIDMap.put(14, 12);
        encounterTypeIDMap.put(15, 16);
        encounterTypeIDMap.put(16, 10);
        encounterTypeIDMap.put(19, 9);
        encounterTypeIDMap.put(20, 8);
        encounterTypeIDMap.put(21, 11);
        encounterTypeIDMap.put(22, 8);
        encounterTypeIDMap.put(23, 14);
        encounterTypeIDMap.put(27, 13);
        encounterTypeIDMap.put(28, 17);
        encounterTypeIDMap.put(30, 6);
        encounterTypeIDMap.put(40, 19);
        encounterTypeIDMap.put(46, 7);
        encounterTypeIDMap.put(47, 6);
        encounterTypeIDMap.put(50, 2);
        encounterTypeIDMap.put(51, 21);
        encounterTypeIDMap.put(52, 22);
        encounterTypeIDMap.put(53, 23);
        encounterTypeIDMap.put(56,25);
    }

    public int getVisitID(Date visitDate, int patientID) {
        int visitID = 0;
        String sql_text = "select visit_id from visit where patient_id=? and date_started=? and voided=0";
        PreparedStatement ps = prepareQuery(sql_text);
        ResultSet rs = null;
        try {
            ps.setInt(1, patientID);
            ps.setDate(2, Converter.convertToSQLDate(visitDate));
            rs = ps.executeQuery();
            while (rs.next()) {
                visitID = rs.getInt("visit_id");
            }
            cleanup(rs, ps);
        } catch (SQLException ex) {
            handleException(ex);
        } finally {
            cleanup(rs, ps);
        }
        return visitID;
    }

    public void preprocessEncounters(Set<Encounter> encounterSet) {
        int visitID = 0;
        System.out.println("Preprocess Encounters...");
        for (Encounter encounter : encounterSet) {
            encounter.setEncounterType(encounterTypeIDMap.get(encounter.getFormID()));
            if (isExisting(encounter)) {
                encounter.setExists(true);
                System.out.println("Encounter existing");
            } else {
                System.out.println("Encounter not existing");
                encounter.setExists(false);
            }
            if (isExistingVisit(encounter.getEncounterDatetime(), encounter.getPatientID())) {
                visitID = getVisitID(encounter.getEncounterDatetime(), encounter.getPatientID());
                encounter.setVisitID(visitID);
                System.out.println("Encounter id is: " + encounter.getEncounterID() + " Visit id is " + visitID);
            } else {
                System.out.println("Visit not existing");
            }

            //check if encounter exists
            //if encounter exists update the exists flag
            //Map Encounter to NMRS equivalent (Change FormID and EncounterType) 
            //Update the VisitID for encounter
        }
        //return encounterSet;
    }

    public void preprocessEncounterProviders(Set<EncounterProvider> encounterProviders) {
        System.out.println("Preprocess EncounterProvider...");
        for (EncounterProvider encounterProvider : encounterProviders) {
            if (isExisting(encounterProvider)) {
                encounterProvider.setExists(true);
            } else {
                encounterProvider.setExists(false);
            }
        }
        // return encounterProviders;
    }

    public boolean isExisting(EncounterProvider encounterProvider) {
        boolean ans = false;
        String sql_text = "select encounter_id from encounter_provider where encounter_id=? and provider_id=? and encounter_role_id=? and voided=0";
        PreparedStatement ps = prepareQuery(sql_text);
        ResultSet rs = null;
        try {
            ps.setInt(1, encounterProvider.getEncounterID());
            ps.setInt(2, encounterProvider.getProviderID());
            ps.setInt(3, encounterProvider.getEncounterRoleID());
            rs = ps.executeQuery();
            while (rs.next()) {
                ans = true;
            }
            cleanup(rs, ps);
        } catch (SQLException ex) {
            handleException(ex);
        } finally {
            cleanup(rs, ps);
        }
        return ans;
    }

    public void setEncounterID(Obs obs) {
        int encounterID = getEncounterID(obs.getPatientID(), obs.getFormID(), obs.getVisitDate());
        obs.setEncounterID(encounterID);
    }

    public void preprocessObsList(List<Obs> obsList) {
        //List<Obs> mappedObs = new ArrayList<Obs>();
        for (Obs obs : obsList) {

            if (obs.getConceptID() == 0) {
                obs.setAllowed(false);
            } else {
                obs.setAllowed(true);
            }
            setEncounterID(obs);
            if (obs.getObsGroupID() != 0) {
                if (isCodedObsGroupExisting(obs)) {
                    obs.setExist(true);
                } else {
                    obs.setExist(false);
                }
            } else {
                if (isExisting(obs)) {
                    obs.setExist(true);
                } else {
                    obs.setExist(false);
                }
            }

        }
        System.out.println("Preprocess Obs...");
        //return mappedObs;
    }

    public boolean isExisting(Obs obs) {
        boolean ans = false;
        String sql_text = "select obs_id from obs where person_id=? and concept_id=? and obs_datetime=? and voided=0";
        PreparedStatement ps = prepareQuery(sql_text);
        ResultSet rs = null;
        try {
            ps.setInt(1, obs.getPatientID());
            ps.setInt(2, obs.getConceptID());
            ps.setDate(3, Converter.convertToSQLDate(obs.getVisitDate()));
            rs = ps.executeQuery();
            while (rs.next()) {
                ans = true;
            }
            cleanup(rs, ps);
        } catch (SQLException ex) {
            handleException(ex);
        } finally {
            cleanup(rs, ps);
        }
        return ans;
    }

    private boolean isCodedObsGroupExisting(Obs obs) {
        boolean ans = false;
        String sql_text = "select obs_id from obs where person_id=? and concept_id=? and obs_datetime=? and value_coded=?  and obs_group_id=? and voided=0";
        PreparedStatement ps = prepareQuery(sql_text);
        ResultSet rs = null;
        try {
            ps.setInt(1, obs.getPatientID());
            ps.setInt(2, obs.getConceptID());
            ps.setDate(3, Converter.convertToSQLDate(obs.getVisitDate()));
            ps.setInt(4, obs.getValueCoded());
            ps.setInt(5, obs.getObsGroupID());
            rs = ps.executeQuery();
            while (rs.next()) {
                ans = true;
            }
            cleanup(rs, ps);
        } catch (SQLException ex) {
            handleException(ex);
        } finally {
            cleanup(rs, ps);
        }
        return ans;
    }

    public void handlePS(int pos, int val, PreparedStatement ps) throws SQLException {
        if (val == 0) {
            ps.setNull(pos, java.sql.Types.INTEGER);
        } else {
            ps.setInt(pos, val);
        }
    }
    
    public void handlePS(int pos, double val, PreparedStatement ps) throws SQLException {
        if (val == 0.0) {
            ps.setNull(pos, java.sql.Types.DOUBLE);
        } else {
            ps.setDouble(pos, val);
        }
    }

    public int migrateObs(Obs obs, int locationID) {
        int obs_id = 0;

        String sql_text = "insert into obs (person_id,concept_id,encounter_id,"
                + "obs_datetime,location_id,"
                + "creator,"
                + "date_created,voided,uuid) "
                + "values ('" + obs.getPatientID()
                + "','"
                + obs.getConceptID()
                + "','"
                + obs.getEncounterID()
                + "','"
                + Converter.convertToSQLDate(obs.getVisitDate())
                + "','"
                + locationID + "','" + obs.getCreator() + "','" + Converter.convertToSQLDate(obs.getDateEntered()) + "','" + obs.getVoided() + "','" + obs.getUuid() + "')";
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            obs_id = stmt.executeUpdate(sql_text, Statement.RETURN_GENERATED_KEYS);
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                obs_id = rs.getInt(1);
            }
            cleanup(rs, stmt);
        } catch (SQLException ex) {
            handleException(ex);
        } finally {
            cleanup(rs, stmt);

        }
        return obs_id;
    }

    public void migrateObs(List<Obs> obsList, int locationID) {
        String sql_text = "insert into obs (person_id,concept_id,encounter_id,"
                + "obs_datetime,location_id,obs_group_id,"
                + "value_coded,value_datetime,"
                + "value_numeric,value_text,creator,"
                + "date_created,voided,uuid) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        System.out.println("Migrate Obs...");
        PreparedStatement ps = prepareQuery(sql_text);
        try {
            for (Obs obs : obsList) {
                if (!obs.isExist() && obs.isAllowed()) {
                    // ps.setInt(1, obs.getObsID());
                    ps.setInt(1, obs.getPatientID());
                    ps.setInt(2, obs.getConceptID());
                    ps.setInt(3, obs.getEncounterID());
                    ps.setDate(4, Converter.convertToSQLDate(obs.getVisitDate()));
                    ps.setInt(5, locationID);
                    //handlePS(6, obs.getObsGroupID(), ps);
                    handlePS(6, obs.getObsGroupID(), ps);
                    handlePS(7, obs.getValueCoded(), ps);
                    ps.setDate(8, Converter.convertToSQLDate(obs.getValueDate()));
                    handlePS(9, obs.getValueNumeric(), ps);
                    ps.setString(10, obs.getValueText());
                    ps.setInt(11, obs.getCreator());
                    ps.setDate(12, Converter.convertToSQLDate(obs.getDateEntered()));
                    ps.setInt(13, obs.getVoided());
                    ps.setString(14, obs.getUuid());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            ps.close();
            //commitConnection();
        } catch (SQLException ex) {
            handleException(ex);
        }
    }

    public Encounter createEncounterFromObs(Obs obs, int locationID) {
        Encounter enc = new Encounter();
        enc.setEncounterID(obs.getEncounterID());
        //enc.setEncounterType(clinicalDictionary.convertEncounterType(obs.getFormID()));
        enc.setPatientID(obs.getPatientID());
        enc.setLocationID(locationID);
        enc.setFormID(obs.getFormID());
        enc.setEncounterDatetime(obs.getVisitDate());
        enc.setCreator(obs.getCreator());
        enc.setDateCreated(obs.getDateEntered());
        enc.setVoided(obs.getVoided());
        enc.setProviderID((obs.getProviderID()));
        // Visit ID not set
        enc.setUuid(Converter.generateUUID());
        return enc;
    }

    public Visit createVisitFromObs(Obs obs, int locationID) {
        Visit vst = new Visit();
        vst.setPatientID(obs.getPatientID());
        // Set Visit Type ID
        vst.setDateStarted(obs.getVisitDate());
        vst.setVisitTypeID(VISIT_TYPE_ID);
        vst.setDateStopped(obs.getVisitDate());
        vst.setLocationID(locationID);
        vst.setCreator(obs.getCreator());
        vst.setDateCreated(obs.getDateEntered());
        vst.setVoided(obs.getVoided());
        vst.setUuid(Converter.generateUUID());
        return vst;
    }

    public List<Provider> createProvidersFromUsers(List<User> usrList) {
        List<Provider> providerList = new ArrayList<Provider>();
        Provider provider = null;
        for (User usr : usrList) {
            provider = createProviderFromUser(usr);
            providerList.add(provider);
        }
        return providerList;
    }

    public Provider createProviderFromUser(User usr) {
        Provider provider = new Provider();
        provider.setPersonID(usr.getPerson_id());
        provider.setIdentifier(usr.getUserName());
        provider.setCreator(usr.getCreator());
        provider.setDateCreated(usr.getDateCreated());
        provider.setRetired(usr.getRetired());
        //provider.setProviderRoleID(PREFERRED);
        provider.setUuid(Converter.generateUUID());
        return provider;
    }

    public int migrateVisit(Visit vst) {
        String sql_text = "insert into visit (patient_id,visit_type_id,"
                + "      date_started,date_stopped,location_id,creator,"
                + "      date_created,voided,uuid) values (?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        int visitID = 0;
        try {
            ps = connection.prepareStatement(sql_text, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, vst.getPatientID());
            ps.setInt(2, vst.getVisitTypeID());
            ps.setDate(3, Converter.convertToSQLDate(vst.getDateStarted()));
            ps.setDate(4, Converter.convertToSQLDate(vst.getDateStopped()));
            ps.setInt(5, vst.getLocationID());
            ps.setInt(6, vst.getCreator());
            ps.setDate(7, Converter.convertToSQLDate(vst.getDateCreated()));
            ps.setInt(8, vst.getVoided());
            ps.setString(9, vst.getUuid());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                visitID = rs.getInt(1);
            }
            cleanup(rs, ps);
        } catch (SQLException ex) {
            handleException(ex);
        } finally {
            cleanup(rs, ps);
        }
        return visitID;

    }

    public void migrateVisits(Set<Visit> visitList) {
        System.out.println("Migrating visits...");
        String sql_text = "insert into visit (patient_id,visit_type_id,"
                + "      date_started,date_stopped,location_id,creator,"
                + "      date_created,voided,uuid) values (?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = prepareQuery(sql_text);
        try {
            if (!visitList.isEmpty()) {
                for (Visit vst : visitList) {
                    if (!vst.isExists()) {
                        ps.setInt(1, vst.getPatientID());
                        ps.setInt(2, vst.getVisitTypeID());
                        ps.setDate(3, Converter.convertToSQLDate(vst.getDateStarted()));
                        ps.setDate(4, Converter.convertToSQLDate(vst.getDateStopped()));
                        ps.setInt(5, vst.getLocationID());
                        ps.setInt(6, vst.getCreator());
                        ps.setDate(7, Converter.convertToSQLDate(vst.getDateCreated()));
                        ps.setInt(8, vst.getVoided());
                        ps.setString(9, vst.getUuid());
                        ps.addBatch();
                    }
                }
            }
            ps.executeBatch();
            ps.close();
            //commitConnection();
        } catch (SQLException ex) {
            handleException(ex);
        }
    }

    public void migrateEncounterProvider(Set<EncounterProvider> encProviderList) {
        String sql_text = "insert into encounter_provider "
                + "(encounter_id,provider_id,encounter_role_id,creator,date_created,voided,uuid) values (?,?,?,?,?,?,?)";
        PreparedStatement ps = prepareQuery(sql_text);
        System.out.println("Migrating EncounterProvider...");
        try {
            for (EncounterProvider encounterProvider : encProviderList) {
                if (!encounterProvider.isExists()) {
                    ps.setInt(1, encounterProvider.getEncounterID());
                    ps.setInt(2, encounterProvider.getProviderID());
                    ps.setInt(3, encounterProvider.getEncounterRoleID());
                    ps.setInt(4, encounterProvider.getCreator());
                    ps.setDate(5, Converter.convertToSQLDate(encounterProvider.getDateCreated()));
                    ps.setInt(6, encounterProvider.getVoided());
                    ps.setString(7, encounterProvider.getUuid());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            ps.close();
            commitConnection();
        } catch (SQLException ex) {
            handleException(ex);
        }
    }

    public int migrateEncounter(Encounter encounter, int locationID) {
        int encounterID = 0;
        String sql_text = "insert into encounter (encounter_id,encounter_type,patient_id,"
                + "location_id,form_id,encounter_datetime,"
                + "creator,date_created,voided,visit_id,uuid) values (?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(sql_text, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, encounter.getEncounterID());
            ps.setInt(2, encounter.getEncounterType());
            ps.setInt(3, encounter.getPatientID());
            ps.setInt(4, locationID);
            ps.setInt(5, encounter.getFormID());
            ps.setDate(6, Converter.convertToSQLDate(encounter.getEncounterDatetime()));
            ps.setInt(7, encounter.getCreator());
            ps.setDate(8, Converter.convertToSQLDate(encounter.getDateCreated()));
            ps.setInt(9, VOIDED);
            ps.setInt(10, encounter.getVisitID());
            ps.setString(11, encounter.getUuid());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                encounterID = rs.getInt(1);
            }
            cleanup(rs, ps);
        } catch (SQLException ex) {
            handleException(ex);
        } finally {
            cleanup(rs, ps);
        }
        return encounterID;
    }

    public void migrateEncounter(Set<Encounter> encList, int locationID) {
        String sql_text = "insert into encounter (encounter_id,encounter_type,patient_id,"
                + "location_id,form_id,encounter_datetime,"
                + "creator,date_created,voided,visit_id,uuid) values (?,?,?,?,?,?,?,?,?,?,?)";
        System.out.println("Migrating Encounters...");
        PreparedStatement ps = prepareQuery(sql_text);
        try {
            for (Encounter encounter : encList) {
                if (!encounter.isExists()) {
                    ps.setInt(1, encounter.getEncounterID());
                    ps.setInt(2, encounter.getEncounterType());
                    ps.setInt(3, encounter.getPatientID());
                    ps.setInt(4, locationID);
                    ps.setInt(5, encounter.getFormID());
                    ps.setDate(6, Converter.convertToSQLDate(encounter.getEncounterDatetime()));
                    ps.setInt(7, encounter.getCreator());
                    ps.setDate(8, Converter.convertToSQLDate(encounter.getDateCreated()));
                    ps.setInt(9, VOIDED);
                    ps.setInt(10, encounter.getVisitID());
                    ps.setString(11, encounter.getUuid());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            ps.close();
            //commitConnection();
        } catch (SQLException ex) {
            handleException(ex);
        }
    }

    public void migrateProviders(List<Provider> providerList) {
        String sql_text = "insert into provider (person_id,identifier,"
                + "creator,date_created,retired,uuid,provider_role_id) values (?,?,?,?,?,?,?)";
        PreparedStatement ps = prepareQuery(sql_text);
        try {
            for (Provider prv : providerList) {
                ps.setInt(1, prv.getPersonID());
                ps.setString(2, prv.getIdentifier());
                ps.setInt(3, prv.getCreator());
                ps.setDate(4, Converter.convertToSQLDate(prv.getDateCreated()));
                ps.setInt(5, prv.getRetired());
                ps.setString(6, prv.getUuid());
                ps.setInt(7, prv.getProviderRoleID());
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
        } catch (SQLException ex) {
            handleException(ex);
        }
    }

    public void migrateUsers(List<User> usrList) {

        savePersonUsers(usrList);
        savePersonName(usrList);
        saveUsers(usrList);
        saveProvider(usrList);
    }

    public boolean isExisting(User usr) {
        String sql_text = "";
        return false;
    }

    public void preprocessUsers(List<User> usrList) {

    }

    public void saveProvider(List<User> usrList) {
        String sql_text = "insert into provider (person_id,identifier,creator,date_created,retired,uuid) values (?,?,?,?,?,?)";
        PreparedStatement ps = prepareQuery(sql_text);
        try {
            for (User usr : usrList) {
                ps.setInt(1, usr.getPerson_id());
                ps.setString(2, Converter.generateUserSystemID(usr));
                ps.setInt(3, usr.getCreator());
                ps.setDate(4, Converter.convertToSQLDate(usr.getDateCreated()));
                ps.setInt(5, 0);
                ps.setString(6, Converter.generateUUID());
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
        } catch (SQLException ex) {
            handleException(ex);
        }
    }

    public List<Location> getLocations() {
        List<Location> locationList = new ArrayList<Location>();
        try {
            String query = "select location_id, TRIM(UPPER(name)) location_name from location where parent_location is null order by location_name desc";
            PreparedStatement ps = prepareQuery(query);
            ResultSet result = ps.executeQuery();
            Location loc = null;
            String locationName = null;
            int location_id = 0;
            while (result.next()) {
                location_id = result.getInt("location_id");
                locationName = result.getString("location_name");
                loc = new Location();
                loc.setLocationID(location_id);
                loc.setLocationName(locationName);
                locationList.add(loc);
            }
            result.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return locationList;
    }
}
