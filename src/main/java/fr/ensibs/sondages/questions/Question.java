package fr.ensibs.sondages.questions;

import java.io.Serializable;
import java.util.UUID;

/**
 * A question
 */
public interface Question extends Serializable {
    /**
     * Get the unique identifier of the question
     * @return the uuid
     */
    UUID getID();

    /**
     * Get the question text
     * @return the question text
     */
    String getQuestion();

    /**
     * Create a response form asking the user the answer for the question.
     * @return a filled answer to be sent.
     */
    Answer makeResponseForm();
}
