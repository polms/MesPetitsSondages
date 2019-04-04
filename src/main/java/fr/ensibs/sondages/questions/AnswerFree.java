package fr.ensibs.sondages.questions;

import java.util.UUID;

public class AnswerFree extends Answer {
    public String response;

    public AnswerFree() {super();}

    public AnswerFree(UUID id) {
        super(id);
    }
}
