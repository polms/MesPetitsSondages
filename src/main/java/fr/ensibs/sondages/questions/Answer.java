package fr.ensibs.sondages.questions;

import net.jini.core.entry.Entry;

import java.util.UUID;

public abstract class Answer implements Entry {
    public UUID question_id;

    public Answer() {}

    public Answer(UUID question_id) {
        this.question_id = question_id;
    }
}
