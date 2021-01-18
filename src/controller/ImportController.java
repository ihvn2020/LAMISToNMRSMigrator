/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import daos.ImportDAO;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import model.ConnectionParameters;
import model.DisplayScreen;
import model.Location;

/**
 *
 * @author The Bright
 */
public class ImportController {

    private ImportDAO dao;
    private DisplayScreen screen;

    public ImportController() {
        dao = new ImportDAO();
    }

    public DefaultComboBoxModel getTemplateTypeModel() {
        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
        String[] import_types = {
            "Demographics",
            "Lab",
            "Clinical",
            "Pharmacy",
        };
        for (String ele : import_types) {
            comboBoxModel.addElement(ele);
        }
        return comboBoxModel;
    }

    public DefaultComboBoxModel getlocationComboModel() {
        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
        List<Location> locList = dao.getLocations();
        for (Location ele : locList) {
            comboBoxModel.addElement(ele);
        }
        return comboBoxModel;
    }

    public boolean connect(ConnectionParameters con) {
        return dao.connect(con);
    }

    public void closeConnection() {
        dao.closeConnections();
    }

    public void migrate(String csvFilePath, String exportType, int locationID) {
        if (exportType.equalsIgnoreCase("Lab")) {
            dao.migrateLabTests(csvFilePath, locationID);
        } else if (exportType.equalsIgnoreCase("Demographics")) {
            dao.migrateDemographics(csvFilePath, locationID);
            dao.migrateHIVEnrollments(csvFilePath, locationID);
            dao.migrateARTCommencement(csvFilePath, locationID);// Migrate ART Commencement
        } else if (exportType.equalsIgnoreCase("Clinical")) {
            dao.migrateClinicals(csvFilePath, locationID);
        }else if(exportType.equalsIgnoreCase("Pharmacy")){
            dao.migrateDrugs(csvFilePath, locationID);
        }
        // Add Migrate Exits
        
    }

    public void registerDisplay(DisplayScreen screen) {
        this.screen = screen;
        dao.registerDisplay(screen);
    }

    public void validate(String xmlPath, String importType) {

    }
}
