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
public class LamisClinicalCoding {

    /**
     * @return the variableName
     */
    public String getVariableName() {
        return variableName;
    }

    /**
     * @param variableName the variableName to set
     */
    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    /**
     * @return the variablePosition
     */
    public int getVariablePosition() {
        return variablePosition;
    }

    /**
     * @param variablePosition the variablePosition to set
     */
    public void setVariablePosition(int variablePosition) {
        this.variablePosition = variablePosition;
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
     * @return the lamisAnswer
     */
    public String getLamisAnswer() {
        return lamisAnswer;
    }

    /**
     * @param lamisAnswer the lamisAnswer to set
     */
    public void setLamisAnswer(String lamisAnswer) {
        this.lamisAnswer = lamisAnswer;
    }

    /**
     * @return the OMRSConceptID
     */
    public int getOMRSConceptID() {
        return OMRSConceptID;
    }

    /**
     * @param OMRSConceptID the OMRSConceptID to set
     */
    public void setOMRSConceptID(int OMRSConceptID) {
        this.OMRSConceptID = OMRSConceptID;
    }

    /**
     * @return the OMRSAnswerID
     */
    public int getOMRSAnswerID() {
        return OMRSAnswerID;
    }

    /**
     * @param OMRSAnswerID the OMRSAnswerID to set
     */
    public void setOMRSAnswerID(int OMRSAnswerID) {
        this.OMRSAnswerID = OMRSAnswerID;
    }
    private String variableName;
    private int variablePosition;
    private String dataType;
    private String lamisAnswer;
    private int OMRSConceptID;
    private int OMRSAnswerID;

}
