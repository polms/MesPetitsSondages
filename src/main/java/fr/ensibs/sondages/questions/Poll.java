package fr.ensibs.sondages.questions;

import fr.ensibs.joram.Connector;
import fr.ensibs.joram.Helper;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

import javax.jms.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;


public class Poll {

    private final JavaSpace javaSpace;
    private final Session session;
    private final Session listen;
    private MessageConsumer listenConsumer;
    private final Topic topic;
    private ArrayList<Question> questions;
    public static final String TOPIC_NAME = "QUESTIONS";

    public Poll(JavaSpace javaSpace) {
        this.javaSpace = javaSpace;
        this.questions = new ArrayList<>();
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
                    questions.add(q);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

     public Question ask(String question, Class response_type) throws Exception {
        if (! Answer.class.isAssignableFrom(response_type))
            throw new Exception("Response type is not an answer");
        Question q = null;
        if (response_type == AnswerFree.class)
            q = new DefaultQuestion<AnswerFree>(question, response_type);
        else if (response_type == AnswerYesNo.class)
            q = new DefaultQuestion<AnswerYesNo>(question, response_type);
        else if (response_type == AnswerBounded.class)
            q = new DefaultQuestion<AnswerBounded>(question, response_type);
        else
            throw new Exception("Response type not in use");
        try {
            MessageProducer producer =this.session.createProducer(this.topic);
            producer.send(this.session.createObjectMessage(q));
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return q;
    }

    public void answer(Answer answer) {
        try {
            this.javaSpace.write(answer, null, Lease.FOREVER);
            this.questions.removeIf(q -> q.getID().equals(answer.question_id));
        } catch (TransactionException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Question> getQuestions() {
        return this.questions;
    }
}
