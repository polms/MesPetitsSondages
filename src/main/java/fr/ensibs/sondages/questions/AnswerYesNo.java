package fr.ensibs.sondages.questions;

import java.util.UUID;

public class AnswerYesNo extends Answer {
    public Boolean response;

    public AnswerYesNo() {super();}

    public AnswerYesNo(UUID id) {
        super(id);
    }
}
