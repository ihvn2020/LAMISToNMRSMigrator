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
public class LamisRegimenMap {

    /**
     * @return the columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @param columnName the columnName to set
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @return the lamisVariableName
     */
    public String getLamisVariableName() {
        return lamisVariableName;
    }

    /**
     * @param lamisVariableName the lamisVariableName to set
     */
    public void setLamisVariableName(String lamisVariableName) {
        this.lamisVariableName = lamisVariableName;
    }

    /**
     * @return the lamisAnswerName
     */
    public String getLamisAnswerName() {
        return lamisAnswerName;
    }

    /**
     * @param lamisAnswerName the lamisAnswerName to set
     */
    public void setLamisAnswerName(String lamisAnswerName) {
        this.lamisAnswerName = lamisAnswerName;
    }

    /**
     * @return the lamisAnswerCode
     */
    public String getLamisAnswerCode() {
        return lamisAnswerCode;
    }

    /**
     * @param lamisAnswerCode the lamisAnswerCode to set
     */
    public void setLamisAnswerCode(String lamisAnswerCode) {
        this.lamisAnswerCode = lamisAnswerCode;
    }

    /**
     * @return the lamisQuestionCode
     */
    public String getLamisQuestionCode() {
        return lamisQuestionCode;
    }

    /**
     * @param lamisQuestionCode the lamisQuestionCode to set
     */
    public void setLamisQuestionCode(String lamisQuestionCode) {
        this.lamisQuestionCode = lamisQuestionCode;
    }

    /**
     * @return the nmrsConceptID
     */
    public int getNmrsConceptID() {
        return nmrsConceptID;
    }

    /**
     * @param nmrsConceptID the nmrsConceptID to set
     */
    public void setNmrsConceptID(int nmrsConceptID) {
        this.nmrsConceptID = nmrsConceptID;
    }

    /**
     * @return the nmrsAnswerConceptID
     */
    public int getNmrsAnswerConceptID() {
        return nmrsAnswerConceptID;
    }

    /**
     * @param nmrsAnswerConceptID the nmrsAnswerConceptID to set
     */
    public void setNmrsAnswerConceptID(int nmrsAnswerConceptID) {
        this.nmrsAnswerConceptID = nmrsAnswerConceptID;
    }
    private String columnName;
    private String lamisVariableName;
    private String lamisAnswerName;
    private String lamisAnswerCode;
    private String lamisQuestionCode;
    private int nmrsConceptID;
    private int nmrsAnswerConceptID;
}
