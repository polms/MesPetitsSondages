package fr.ensibs.sondages.questions;

import fr.ensibs.joram.Connector;
import fr.ensibs.joram.Helper;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

import javax.jms.*;
import java.rmi.RemoteException;


public class Poll {

    private final JavaSpace javaSpace;
    private final Session session;
    private final Session listen;
    private MessageConsumer listenConsumer;
    private final Topic topic;
    public static final String TOPIC_NAME = "QUESTIONS";

    public Poll(JavaSpace javaSpace) {
        this.javaSpace = javaSpace;
        Connector connector = Connector.getInstance();
        this.session = connector.createSession();
        this.listen  = connector.createSession();
        this.topic = Helper.getTopic(this.session, TOPIC_NAME);

        try {
            this.listenConsumer = this.listen.createConsumer(this.topic);
            this.listenConsumer.setMessageListener(message -> {
                try {
                    Question q = message.getBody(Question.class);
                    System.out.println("New Question ("+q.getID()+"):"+q.getQuestion());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public int ask(String question, Class response_type) throws Exception {
        if (! Answer.class.isAssignableFrom(response_type))
            throw new Exception("Response type is not an answer");
        Question q = new DefaultQuestion(question, response_type);
        try {
            MessageProducer producer =this.session.createProducer(this.topic);
            producer.send(this.session.createObjectMessage(q));
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void answer(Answer answer) {
        try {
            this.javaSpace.write(answer, null, Lease.FOREVER);
        } catch (TransactionException | RemoteException e) {
            e.printStackTrace();
        }
    }
}
