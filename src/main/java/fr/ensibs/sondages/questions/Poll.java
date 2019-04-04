package fr.ensibs.sondages.questions;

import fr.ensibs.joram.Connector;
import net.jini.space.JavaSpace;

import javax.jms.Session;


public class Poll {

    private final JavaSpace javaSpace;
    private final Session session;


    public Poll(JavaSpace javaSpace) {
        this.javaSpace = javaSpace;
        this.session = Connector.getInstance().createSession();
    }

    public int ask(String question, Class<Answer> response_type) {
        Question q = new DefaultQuestion(question, response_type);
        //JMS
        return 0;
    }
}
