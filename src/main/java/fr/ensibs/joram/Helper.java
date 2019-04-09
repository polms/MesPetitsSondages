package fr.ensibs.joram;


import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Helper {
	
    public static Topic getTopic(Session sess, String topic) {
        Context context = null;
        try {
            context = new InitialContext();
        } catch (NamingException e) {
            e.printStackTrace();
        }
        Topic t = null;
        try {
            t = (Topic) context.lookup(topic);
        } catch (NamingException ignored) {}

        try {
            if (t == null) {
                t = sess.createTopic(topic);
                context.bind(topic, t);
            }
        } catch (JMSException | NamingException e) {
            e.printStackTrace();
        }
        return t;
    }
    
    public static Queue getQueue(Session sess, String queue) {
        Context context = null;
        try {
            context = new InitialContext();
        } catch (NamingException e) {
            e.printStackTrace();
        }
        Queue q = null;
        try {
            q = (Queue) context.lookup(queue);
        } catch (NamingException ignored) {}

        try {
            if (q == null) {
                q = sess.createQueue(queue);
                context.bind(queue, q);
            }
        } catch (JMSException | NamingException e) {
            e.printStackTrace();
        }
        return q;
    }
}
