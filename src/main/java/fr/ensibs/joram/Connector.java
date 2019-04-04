package fr.ensibs.joram;

import org.objectweb.joram.client.jms.ConnectionFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

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

    public static Connector getInstance() {
        if (instance == null)
            instance = new Connector();

        return instance;
    }

    public Session createSession() {
        try {
            return this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }

}
