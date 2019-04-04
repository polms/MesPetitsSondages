package fr.ensibs.joram;

import org.objectweb.joram.client.jms.ConnectionFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Factory to connect to JMS and create Sessions
 */
public class Connector {
    private Context context;
    private Connection connection;
    private static Connector instance = null;

    private Connector()  {
        try {
            this.context = new InitialContext();
            ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
            this.connection = factory.createConnection();
            this.connection.start();
        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the instance of the Factory.
     * Create one if it is not yet initialised
     * @return the instance
     */
    public static Connector getInstance() {
        if (instance == null)
            instance = new Connector();

        return instance;
    }

    /**
     * Create a new session.
     * In auto acknowledge mode.
     * @return the session
     */
    public Session createSession() {
        try {
            return this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }

}
