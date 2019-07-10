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
public class CareCardInitialCoding {

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the lamisQuestion
     */
    public String getLamisQuestion() {
        return lamisQuestion;
    }

    /**
     * @param lamisQuestion the lamisQuestion to set
     */
    public void setLamisQuestion(String lamisQuestion) {
        this.lamisQuestion = lamisQuestion;
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
     * @return the nmrsQuestionConceptID
     */
    public int getNmrsQuestionConceptID() {
        return nmrsQuestionConceptID;
    }

    /**
     * @param nmrsQuestionConceptID the nmrsQuestionConceptID to set
     */
    public void setNmrsQuestionConceptID(int nmrsQuestionConceptID) {
        this.nmrsQuestionConceptID = nmrsQuestionConceptID;
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
    private int id;
    private String lamisQuestion;
    private String lamisAnswer;
    private int nmrsQuestionConceptID;
    private int nmrsAnswerConceptID;
}
