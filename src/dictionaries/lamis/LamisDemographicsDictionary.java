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
import utils.Converter;
import utils.FileManager;

/**
 *
 * @author The Bright
 */
public class LamisDemographicsDictionary {

    private FileManager mgr;
    private DisplayScreen screen;

    public LamisDemographicsDictionary() {
        mgr = new FileManager();
    }

    public void setDisplayScreen(DisplayScreen screen) {
        this.screen = screen;
        mgr.setScreen(screen);
    }

    /*
        This method reads all csv file in a List<String[]>
     */
    public List<String[]> readDemographicsCSV(String csvFile) {
        List<String[]> data = mgr.loadAllData(csvFile);
        return data;
    }

    public List<Demographics> convertToDemographicsList(List<String[]> dataArr, int locationID) {
        List<Demographics> demoList = new ArrayList<Demographics>();
        Demographics demo = null;
        for (String[] data : dataArr) {
            demo = Converter.convertToLamisDemographics(data, locationID);
            demoList.add(demo);
        }
        return demoList;
    }

    public void initializeDemographicErrorLog() {
        String[] errorDemoHeader = new String[]{
            "person_source_pk",
            "person_uuid",
            "pepfar_id",
            "hosp_id",
            "ehnid",
            "other_id",
            "first_name",
            "last_name",
            "middle_name",
            "adult_enrollment_dt",
            "pead_enrollment_dt",
            "pmtct_enrollment_dt",
            "hei_enrollment_dt",
            "pep_enrollment_dt",
            "dob",
            "age",
            "gender",
            "address1",
            "address2",
            "address_lga",
            "address_state",
            "creator_id",
            "date_created",
            "location_id",
            "creator",
            "location",
            "date_changed"
        };
        Date currentDate = new Date();
        String errorDemoFile = "ErrorDemographics" + Converter.formatHHMMSSDDMONYYYYNoSlash(currentDate) + ".csv";
        mgr.initializeWriter(errorDemoFile);
        mgr.writeHeaders(errorDemoHeader);
    }

    public int countRecords(String csvFile) {
        int count = mgr.countRecordsInCSV(csvFile, screen);
        return count;
    }

    public void log(Demographics demo) {
        mgr.writeCSV(demo);
    }

    public void closeAllResources() {
        mgr.closeAll();
    }

}
