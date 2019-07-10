/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.lamis;

/**
 *
 * @author The Bright
 */
public class LamisLabCoding {

    /**
     * @return the positiveConceptID
     */
    public int getPositiveConceptID() {
        return positiveConceptID;
    }

    /**
     * @param positiveConceptID the positiveConceptID to set
     */
    public void setPositiveConceptID(int positiveConceptID) {
        this.positiveConceptID = positiveConceptID;
    }

    /**
     * @return the negativeConceptID
     */
    public int getNegativeConceptID() {
        return negativeConceptID;
    }

    /**
     * @param negativeConceptID the negativeConceptID to set
     */
    public void setNegativeConceptID(int negativeConceptID) {
        this.negativeConceptID = negativeConceptID;
    }

    /**
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the labTestID
     */
    public int getLabTestID() {
        return labTestID;
    }

    /**
     * @param labTestID the labTestID to set
     */
    public void setLabTestID(int labTestID) {
        this.labTestID = labTestID;
    }

    /**
     * @return the labTestCategoryID
     */
    public int getLabTestCategoryID() {
        return labTestCategoryID;
    }

    /**
     * @param labTestCategoryID the labTestCategoryID to set
     */
    public void setLabTestCategoryID(int labTestCategoryID) {
        this.labTestCategoryID = labTestCategoryID;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the measureAbsolute
     */
    public String getMeasureAbsolute() {
        return measureAbsolute;
    }

    /**
     * @param measureAbsolute the measureAbsolute to set
     */
    public void setMeasureAbsolute(String measureAbsolute) {
        this.measureAbsolute = measureAbsolute;
    }

    /**
     * @return the measurePercentage
     */
    public String getMeasurePercentage() {
        return measurePercentage;
    }

    /**
     * @param measurePercentage the measurePercentage to set
     */
    public void setMeasurePercentage(String measurePercentage) {
        this.measurePercentage = measurePercentage;
    }

    /**
     * @return the openMRSConceptAbsolute
     */
    public int getOpenMRSConceptAbsolute() {
        return openMRSConceptAbsolute;
    }

    /**
     * @param openMRSConceptAbsolute the openMRSConceptAbsolute to set
     */
    public void setOpenMRSConceptAbsolute(int openMRSConceptAbsolute) {
        this.openMRSConceptAbsolute = openMRSConceptAbsolute;
    }

    /**
     * @return the openMRSConceptPercentage
     */
    public int getOpenMRSConceptPercentage() {
        return openMRSConceptPercentage;
    }

    /**
     * @param openMRSConceptPercentage the openMRSConceptPercentage to set
     */
    public void setOpenMRSConceptPercentage(int openMRSConceptPercentage) {
        this.openMRSConceptPercentage = openMRSConceptPercentage;
    }
    private int labTestID;
    private int labTestCategoryID;
    private String description;
    private String measureAbsolute;
    private String measurePercentage;
    private int openMRSConceptAbsolute;
    private int openMRSConceptPercentage;
    //1-Numeric, 2-Coded, 3-Text
    private String dataType;
    private int positiveConceptID;
    private int negativeConceptID;
}
