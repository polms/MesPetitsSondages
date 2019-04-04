package fr.ensibs.sondages.questions;

import java.util.UUID;

public class AnswerBounded extends Answer {
    public Integer response;

    public AnswerBounded() {super();}

    public AnswerBounded(UUID id) {
        super(id);
    }
}
