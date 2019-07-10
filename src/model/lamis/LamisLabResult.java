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
public class LamisLabResult {

    /**
     * @return the laboratoryID
     */
    public int getLaboratoryID() {
        return laboratoryID;
    }

    /**
     * @param laboratoryID the laboratoryID to set
     */
    public void setLaboratoryID(int laboratoryID) {
        this.laboratoryID = laboratoryID;
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
     * @return the dateReported
     */
    public Date getDateReported() {
        return dateReported;
    }

    /**
     * @param dateReported the dateReported to set
     */
    public void setDateReported(Date dateReported) {
        this.dateReported = dateReported;
    }

    /**
     * @return the dateCollected
     */
    public Date getDateCollected() {
        return dateCollected;
    }

    /**
     * @param dateCollected the dateCollected to set
     */
    public void setDateCollected(Date dateCollected) {
        this.dateCollected = dateCollected;
    }

    /**
     * @return the labNo
     */
    public String getLabNo() {
        return labNo;
    }

    /**
     * @param labNo the labNo to set
     */
    public void setLabNo(String labNo) {
        this.labNo = labNo;
    }

    /**
     * @return the resultCoded
     */
    public String getResultCoded() {
        return resultCoded;
    }

    /**
     * @param resultCoded the resultCoded to set
     */
    public void setResultCoded(String resultCoded) {
        this.resultCoded = resultCoded;
    }

    /**
     * @return the resultAbsolute
     */
    public String getResultAbsolute() {
        return resultAbsolute;
    }

    /**
     * @param resultAbsolute the resultAbsolute to set
     */
    public void setResultAbsolute(String resultAbsolute) {
        this.resultAbsolute = resultAbsolute;
    }

    /**
     * @return the resultPercentage
     */
    public String getResultPercentage() {
        return resultPercentage;
    }

    /**
     * @param resultPercentage the resultPercentage to set
     */
    public void setResultPercentage(String resultPercentage) {
        this.resultPercentage = resultPercentage;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the labtestID
     */
    public int getLabtestID() {
        return labtestID;
    }

    /**
     * @param labtestID the labtestID to set
     */
    public void setLabtestID(int labtestID) {
        this.labtestID = labtestID;
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
     * @return the uploadTime
     */
    public Date getUploadTime() {
        return uploadTime;
    }

    /**
     * @param uploadTime the uploadTime to set
     */
    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
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
    private int laboratoryID;
    private int patientID;
    private int facilityID;
    private Date dateReported;
    private Date dateCollected;
    private String labNo;
    private String resultCoded;
    private String resultAbsolute;
    private String resultPercentage;
    private String comment;
    private int labtestID;
    private Date timestamp;
    private Date uploadTime;
    private int userID;
}
