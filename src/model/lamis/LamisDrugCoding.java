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
public class LamisDrugCoding {

    /**
     * @return the drugStrength
     */
    public String getDrugStrength() {
        return drugStrength;
    }

    /**
     * @param drugStrength the drugStrength to set
     */
    public void setDrugStrength(String drugStrength) {
        this.drugStrength = drugStrength;
    }

    /**
     * @return the groupingConceptID
     */
    public int getGroupingConceptID() {
        return groupingConceptID;
    }

    /**
     * @param groupingConceptID the groupingConceptID to set
     */
    public void setGroupingConceptID(int groupingConceptID) {
        this.groupingConceptID = groupingConceptID;
    }

    /**
     * @return the drugID
     */
    public int getDrugID() {
        return drugID;
    }

    /**
     * @param drugID the drugID to set
     */
    public void setDrugID(int drugID) {
        this.drugID = drugID;
    }

    /**
     * @return the abbreviation
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * @param abbreviation the abbreviation to set
     */
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**
     * @return the drugName
     */
    public String getDrugName() {
        return drugName;
    }

    /**
     * @param drugName the drugName to set
     */
    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    /**
     * @return the packsize
     */
    public String getPacksize() {
        return packsize;
    }

    /**
     * @param packsize the packsize to set
     */
    public void setPacksize(String packsize) {
        this.packsize = packsize;
    }

    /**
     * @return the doseForm
     */
    public String getDoseForm() {
        return doseForm;
    }

    /**
     * @param doseForm the doseForm to set
     */
    public void setDoseForm(String doseForm) {
        this.doseForm = doseForm;
    }

    /**
     * @return the morning
     */
    public String getMorning() {
        return morning;
    }

    /**
     * @param morning the morning to set
     */
    public void setMorning(String morning) {
        this.morning = morning;
    }

    /**
     * @return the afternoon
     */
    public String getAfternoon() {
        return afternoon;
    }

    /**
     * @param afternoon the afternoon to set
     */
    public void setAfternoon(String afternoon) {
        this.afternoon = afternoon;
    }

    /**
     * @return the evening
     */
    public String getEvening() {
        return evening;
    }

    /**
     * @param evening the evening to set
     */
    public void setEvening(String evening) {
        this.evening = evening;
    }

    /**
     * @return the itemID
     */
    public int getItemID() {
        return itemID;
    }

    /**
     * @param itemID the itemID to set
     */
    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    /**
     * @return the omrsQuestionConceptID
     */
    public int getOmrsQuestionConceptID() {
        return omrsQuestionConceptID;
    }

    /**
     * @param omrsQuestionConceptID the omrsQuestionConceptID to set
     */
    public void setOmrsQuestionConceptID(int omrsQuestionConceptID) {
        this.omrsQuestionConceptID = omrsQuestionConceptID;
    }

    /**
     * @return the omrsDrugConceptID
     */
    public int getOmrsDrugConceptID() {
        return omrsDrugConceptID;
    }

    /**
     * @param omrsDrugConceptID the omrsDrugConceptID to set
     */
    public void setOmrsDrugConceptID(int omrsDrugConceptID) {
        this.omrsDrugConceptID = omrsDrugConceptID;
    }

    /**
     * @return the strengthConceptID
     */
    public int getStrengthConceptID() {
        return strengthConceptID;
    }

    /**
     * @param strengthConceptID the strengthConceptID to set
     */
    public void setStrengthConceptID(int strengthConceptID) {
        this.strengthConceptID = strengthConceptID;
    }
   
    private int drugID;
    private String abbreviation;
    private String drugName;
    private String drugStrength;
    private String packsize;
    private String doseForm;
    private String morning;
    private String afternoon;
    private String evening;
    private int itemID;
    private int omrsQuestionConceptID;
    private int omrsDrugConceptID;
    private int strengthConceptID;
    private int groupingConceptID;
    

}
