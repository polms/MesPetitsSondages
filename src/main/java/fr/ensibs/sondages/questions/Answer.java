package fr.ensibs.sondages.questions;

import net.jini.core.entry.Entry;

public abstract class Answer implements Entry {
    public Integer question_id;
    public Boolean no_responce;

    public Answer() {}

    public Answer(int question_id) {
        this.question_id = question_id;
    }
}
