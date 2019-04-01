package fr.ensibs.sondages.questions;

import net.jini.core.entry.Entry;

public class DefaultQuestion implements Question, Entry {
    public Integer id;
    public String question;
    public Class<Answer> answer;

    public DefaultQuestion() {}

    public DefaultQuestion(int id, String question, Class<Answer> answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    @Override
    public int getID() {
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
            a = answer.getConstructor(Integer.class).newInstance(this.id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return a;
    }
}
