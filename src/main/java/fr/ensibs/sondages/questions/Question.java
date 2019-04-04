package fr.ensibs.sondages.questions;

import java.io.Serializable;
import java.util.UUID;

public interface Question extends Serializable {
    public UUID getID();
    public String getQuestion();
    public Answer makeResponseForm();
}
