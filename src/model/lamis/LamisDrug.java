/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.lamis;

import java.util.Date;

/**
 *
 * @author The Bright
 */
public class LamisDrug {

    /**
     * @return the exist
     */
    public boolean isExist() {
        return exist;
    }

    /**
     * @param exist the exist to set
     */
    public void setExist(boolean exist) {
        this.exist = exist;
    }

    /**
     * @return the obsGroupID
     */
    public int getObsGroupID() {
        return obsGroupID;
    }

    /**
     * @param obsGroupID the obsGroupID to set
     */
    public void setObsGroupID(int obsGroupID) {
        this.obsGroupID = obsGroupID;
    }
   
    private int pharmacyID;
    private int patientID;
    private int facilityID;
    private Date visitDate;
    private int durationDays;
    private double morningDose;
    private double afternoonDose;
    private double eveningDose;
    private String adrScreened;//Yes/No
    private String adrID;
    private String prescriptionError;
    private String adherence;
    private Date nextAppointmentDate;
    private String regimenTypeID;
    private String regimenID;
    private int regimenDrugID;
    private Date timestamp;
    private String uploaded;
    private Date timeUploaded;
    private int userID;
    private int obsGroupID;
    private boolean exist;

    /**
     * @return the pharmacyID
     */
    public int getPharmacyID() {
        return pharmacyID;
    }

    /**
     * @param pharmacyID the pharmacyID to set
     */
    public void setPharmacyID(int pharmacyID) {
        this.pharmacyID = pharmacyID;
    }

    /**
     * @return the patientID
     */
    public int getPatientID() {
        return patientID;
    }

    /**
     * @param patientID the patientID to set
     */
    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    /**
     * @return the facilityID
     */
    public int getFacilityID() {
        return facilityID;
    }

    /**
     * @param facilityID the facilityID to set
     */
    public void setFacilityID(int facilityID) {
        this.facilityID = facilityID;
    }

    /**
     * @return the visitDate
     */
    public Date getVisitDate() {
        return visitDate;
    }

    /**
     * @param visitDate the visitDate to set
     */
    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    /**
     * @return the durationDays
     */
    public int getDuration() {
        return durationDays;
    }

    /**
     * @param duration the durationDays to set
     */
    public void setDuration(int duration) {
        this.durationDays = duration;
    }

    /**
     * @return the morningDose
     */
    public double getMorningDose() {
        return morningDose;
    }

    /**
     * @param morningDose the morningDose to set
     */
    public void setMorningDose(double morningDose) {
        this.morningDose = morningDose;
    }

    /**
     * @return the afternoonDose
     */
    public double getAfternoonDose() {
        return afternoonDose;
    }

    /**
     * @param afternoonDose the afternoonDose to set
     */
    public void setAfternoonDose(double afternoonDose) {
        this.afternoonDose = afternoonDose;
    }

    /**
     * @return the eveningDose
     */
    public double getEveningDose() {
        return eveningDose;
    }

    /**
     * @param eveningDose the eveningDose to set
     */
    public void setEveningDose(double eveningDose) {
        this.eveningDose = eveningDose;
    }

    /**
     * @return the adrScreened
     */
    public String getAdrScreened() {
        return adrScreened;
    }

    /**
     * @param adrScreened the adrScreened to set
     */
    public void setAdrScreened(String adrScreened) {
        this.adrScreened = adrScreened;
    }

    /**
     * @return the adrID
     */
    public String getAdrID() {
        return adrID;
    }

    /**
     * @param adrID the adrID to set
     */
    public void setAdrID(String adrID) {
        this.adrID = adrID;
    }

    /**
     * @return the prescriptionError
     */
    public String getPrescriptionError() {
        return prescriptionError;
    }

    /**
     * @param prescriptionError the prescriptionError to set
     */
    public void setPrescriptionError(String prescriptionError) {
        this.prescriptionError = prescriptionError;
    }

    /**
     * @return the adherence
     */
    public String getAdherence() {
        return adherence;
    }

    /**
     * @param adherence the adherence to set
     */
    public void setAdherence(String adherence) {
        this.adherence = adherence;
    }

    /**
     * @return the nextAppointmentDate
     */
    public Date getNextAppointmentDate() {
        return nextAppointmentDate;
    }

    /**
     * @param nextAppointmentDate the nextAppointmentDate to set
     */
    public void setNextAppointmentDate(Date nextAppointmentDate) {
        this.nextAppointmentDate = nextAppointmentDate;
    }

    /**
     * @return the regimenTypeID
     */
    public String getRegimenTypeID() {
        return regimenTypeID;
    }

    /**
     * @param regimenTypeID the regimenTypeID to set
     */
    public void setRegimenTypeID(String regimenTypeID) {
        this.regimenTypeID = regimenTypeID;
    }

    /**
     * @return the regimenID
     */
    public String getRegimenID() {
        return regimenID;
    }

    /**
     * @param regimenID the regimenID to set
     */
    public void setRegimenID(String regimenID) {
        this.regimenID = regimenID;
    }

    /**
     * @return the regimenDrugID
     */
    public int getRegimenDrugID() {
        return regimenDrugID;
    }

    /**
     * @param regimenDrugID the regimenDrugID to set
     */
    public void setRegimenDrugID(int regimenDrugID) {
        this.regimenDrugID = regimenDrugID;
    }

    /**
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the uploaded
     */
    public String getUploaded() {
        return uploaded;
    }

    /**
     * @param uploaded the uploaded to set
     */
    public void setUploaded(String uploaded) {
        this.uploaded = uploaded;
    }

    /**
     * @return the timeUploaded
     */
    public Date getTimeUploaded() {
        return timeUploaded;
    }

    /**
     * @param timeUploaded the timeUploaded to set
     */
    public void setTimeUploaded(Date timeUploaded) {
        this.timeUploaded = timeUploaded;
    }

    /**
     * @return the userID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }
    
    
}
