package fr.ensibs.sondages.questions;

import net.jini.core.entry.Entry;

import java.util.UUID;

public class DefaultQuestion implements Question, Entry {
    public UUID id;
    public String question;
    public Class<Answer> answer;

    public DefaultQuestion() {}

    public DefaultQuestion(String question, Class<Answer> answer) {
        this.id = UUID.randomUUID();
        this.question = question;
        this.answer = answer;
    }

    @Override
    public UUID getID() {
        return this.id;
    }

    @Override
    public String getQuestion() {
        return this.question;
    }

    @Override
    public Answer makeResponseForm() {
        Answer a = null;
        try {
            a = answer.getConstructor(UUID.class).newInstance(this.id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return a;
    }
}
