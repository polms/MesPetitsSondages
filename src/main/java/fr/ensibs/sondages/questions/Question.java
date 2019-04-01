package fr.ensibs.sondages.questions;

public interface Question {
    public int getID();
    public String getQuestion();
    public Answer makeResponseForm();
}
