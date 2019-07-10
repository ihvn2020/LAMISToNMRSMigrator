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
public class LamisClinical {

    /**
     * @return the clinicalID
     */
    public int getClinicalID() {
        return clinicalID;
    }

    /**
     * @param clinicalID the clinicalID to set
     */
    public void setClinicalID(int clinicalID) {
        this.clinicalID = clinicalID;
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
     * @return the clinicalStage
     */
    public String getClinicalStage() {
        return clinicalStage;
    }

    /**
     * @param clinicalStage the clinicalStage to set
     */
    public void setClinicalStage(String clinicalStage) {
        this.clinicalStage = clinicalStage;
    }

    /**
     * @return the functionalStatus
     */
    public String getFunctionalStatus() {
        return functionalStatus;
    }

    /**
     * @param functionalStatus the functionalStatus to set
     */
    public void setFunctionalStatus(String functionalStatus) {
        this.functionalStatus = functionalStatus;
    }

    /**
     * @return the tbStatus
     */
    public String getTbStatus() {
        return tbStatus;
    }

    /**
     * @param tbStatus the tbStatus to set
     */
    public void setTbStatus(String tbStatus) {
        this.tbStatus = tbStatus;
    }

    /**
     * @return the viralLoad
     */
    public double getViralLoad() {
        return viralLoad;
    }

    /**
     * @param viralLoad the viralLoad to set
     */
    public void setViralLoad(double viralLoad) {
        this.viralLoad = viralLoad;
    }

    /**
     * @return the cd4Count
     */
    public double getCd4Count() {
        return cd4Count;
    }

    /**
     * @param cd4Count the cd4Count to set
     */
    public void setCd4Count(double cd4Count) {
        this.cd4Count = cd4Count;
    }

    /**
     * @return the cd4Percent
     */
    public double getCd4Percent() {
        return cd4Percent;
    }

    /**
     * @param cd4Percent the cd4Percent to set
     */
    public void setCd4Percent(double cd4Percent) {
        this.cd4Percent = cd4Percent;
    }

    /**
     * @return the regimenType
     */
    public String getRegimenType() {
        return regimenType;
    }

    /**
     * @param regimenType the regimenType to set
     */
    public void setRegimenType(String regimenType) {
        this.regimenType = regimenType;
    }

    /**
     * @return the regimen
     */
    public String getRegimen() {
        return regimen;
    }

    /**
     * @param regimen the regimen to set
     */
    public void setRegimen(String regimen) {
        this.regimen = regimen;
    }

    /**
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * @return the waist
     */
    public double getWaist() {
        return waist;
    }

    /**
     * @param waist the waist to set
     */
    public void setWaist(double waist) {
        this.waist = waist;
    }

    /**
     * @return the bp
     */
    public String getBp() {
        return bp;
    }

    /**
     * @param bp the bp to set
     */
    public void setBp(String bp) {
        this.bp = bp;
    }

    /**
     * @return the pregnant
     */
    public String getPregnant() {
        return pregnant;
    }

    /**
     * @param pregnant the pregnant to set
     */
    public void setPregnant(String pregnant) {
        this.pregnant = pregnant;
    }

    /**
     * @return the LMP
     */
    public Date getLMP() {
        return LMP;
    }

    /**
     * @param LMP the LMP to set
     */
    public void setLMP(Date LMP) {
        this.LMP = LMP;
    }

    /**
     * @return the breastFeeding
     */
    public String getBreastFeeding() {
        return breastFeeding;
    }

    /**
     * @param breastFeeding the breastFeeding to set
     */
    public void setBreastFeeding(String breastFeeding) {
        this.breastFeeding = breastFeeding;
    }

    /**
     * @return the oiScreen
     */
    public String getOiScreen() {
        return oiScreen;
    }

    /**
     * @param oiScreen the oiScreen to set
     */
    public void setOiScreen(String oiScreen) {
        this.oiScreen = oiScreen;
    }

    /**
     * @return the oiID
     */
    public String getOiID() {
        return oiID;
    }

    /**
     * @param oiID the oiID to set
     */
    public void setOiID(String oiID) {
        this.oiID = oiID;
    }

    /**
     * @return the adverseDrugScreened
     */
    public String getAdverseDrugScreened() {
        return adverseDrugScreened;
    }

    /**
     * @param adverseDrugScreened the adverseDrugScreened to set
     */
    public void setAdverseDrugScreened(String adverseDrugScreened) {
        this.adverseDrugScreened = adverseDrugScreened;
    }

    /**
     * @return the adverseDrugReactionID
     */
    public String getAdverseDrugReactionID() {
        return adverseDrugReactionID;
    }

    /**
     * @param adverseDrugReactionID the adverseDrugReactionID to set
     */
    public void setAdverseDrugReactionID(String adverseDrugReactionID) {
        this.adverseDrugReactionID = adverseDrugReactionID;
    }

    /**
     * @return the adherenceLevel
     */
    public String getAdherenceLevel() {
        return adherenceLevel;
    }

    /**
     * @param adherenceLevel the adherenceLevel to set
     */
    public void setAdherenceLevel(String adherenceLevel) {
        this.adherenceLevel = adherenceLevel;
    }

    /**
     * @return the adherenceID
     */
    public String getAdherenceID() {
        return adherenceID;
    }

    /**
     * @param adherenceID the adherenceID to set
     */
    public void setAdherenceID(String adherenceID) {
        this.adherenceID = adherenceID;
    }

    /**
     * @return the commence
     */
    public String getCommence() {
        return commence;
    }

    /**
     * @param commence the commence to set
     */
    public void setCommence(String commence) {
        this.commence = commence;
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
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note the note to set
     */
    public void setNote(String note) {
        this.note = note;
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
    /*
    CLINIC_ID,PATIENT_ID,FACILITY_ID,DATE_VISIT,CLINIC_STAGE
    FUNC_STATUS,TB_STATUS,VIRAL_LOAD,CD4,CD4P,REGIMENTYPE,
    REGIMEN,BODY_WEIGHT,HEIGHT,WAIST,BP,PREGNANT,LMP,BREASTFEEDING
    OI_SCREENED,OI_IDS,ADR_SCREENED,ADR_IDS,ADHERENCE_LEVEL,ADHERE_IDS
    COMMENCE,NEXT_APPOINTMENT,NOTES,TIME_STAMP,UPLOADED,TIME_UPLOADED,USER_ID
    */
    private int clinicalID;
    private int patientID;
    private int facilityID;
    private Date visitDate;
    private String clinicalStage;
    private String functionalStatus;
    private String tbStatus;
    private double viralLoad;
    private double cd4Count;
    private double cd4Percent;
    private String regimenType;
    private String regimen;
    private double weight;
    private double height;
    private double waist;
    private String bp;
    private String pregnant;
    private Date LMP;
    private String breastFeeding;
    private String oiScreen;
    private String oiID;
    private String adverseDrugScreened;
    private String adverseDrugReactionID;
    private String adherenceLevel;
    private String adherenceID;
    private String commence;
    private Date nextAppointmentDate;
    private String note;
    private Date timestamp;
    private String uploaded;
    private Date timeUploaded;
    private int userID;

    
}
