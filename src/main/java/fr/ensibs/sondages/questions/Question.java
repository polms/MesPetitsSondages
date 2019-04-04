package fr.ensibs.sondages.questions;

import java.util.UUID;

public interface Question {
    public UUID getID();
    public String getQuestion();
    public Answer makeResponseForm();
}
