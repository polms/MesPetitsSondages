package fr.ensibs.sondages;

import net.jini.core.entry.Entry;

public abstract class Response implements Entry {
    public Integer question_id;
    public Boolean no_responce;
}
