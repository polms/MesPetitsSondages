package fr.ensibs.sondages.questions;

import fr.ensibs.joram.Connector;
import fr.ensibs.joram.Helper;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

import javax.jms.*;
import java.rmi.RemoteException;
import java.util.ArrayList;


/**
 * A class to create and register Questions/Answers
 *
 * Questions are sent to a JMS Topic.
 * Answers are saved to the JavaSpace.
 */
public class Poll {
    private final JavaSpace javaSpace;
    private final Session session;
    private final Session listen;
    private MessageConsumer listenConsumer;
    private final Topic topic;
    private ArrayList<Question> questions;
    public static final String TOPIC_NAME = "QUESTIONS";

    /**
     * Constructor
     * @param javaSpace can be null
     */
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

    /**
     * Register a question.
     *
     * @param question text of the question
     * @param response_type the class used for the response. (Free/YesNo/Bounded)
     * @return the created question
     * @throws Exception thrown if the response_type is not correct
     */
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

    /**
     * Answer a question.
     *
     * If the question is no longer waiting for an answer, ignore the answer.
     * otherwise store it in the JavaSpace
     * @param answer the answer
     */
    public void answer(Answer answer) {
        if (this.javaSpace == null)
            return;
        if (! this.questions.removeIf(q -> q.getID().equals(answer.question_id)))
            return; // no questions for this answer. Ignore.
        try {
            this.javaSpace.write(answer, null, Lease.FOREVER);
        } catch (TransactionException | RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the questions yet to be answered
     *
     * @return a list of questions
     */
    public ArrayList<Question> getQuestions() {
        return this.questions;
    }
}
