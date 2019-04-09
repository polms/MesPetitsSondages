package fr.ensibs.sondages.analyzer;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.MarshalledObject;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import fr.ensibs.joram.Connector;
import fr.ensibs.joram.Helper;
import fr.ensibs.river.RiverLookup;
import fr.ensibs.sondages.questions.Answer;
import fr.ensibs.sondages.questions.AnswerBounded;
import fr.ensibs.sondages.questions.AnswerFree;
import fr.ensibs.sondages.questions.AnswerYesNo;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.transaction.TransactionException;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace;

/**
 * Analyzes answers to questions and make reports
 * 
 * @author Maxime
 *
 */
public class Analyzer {
	
	/**
	 * The host name of the javaspace server
	 */
	private String spaceHostName;
	
	/**
	 * The port number to connect to the javaspace server
	 */
	private int spacePortNumber;
	
	/**
	 * The host name of the jms server
	 */
	private String jmsHostName;
	
	/**
	 * The port number to connect to the jms server
	 */
	private String jmsPortNumber;
	
	/**
	 * The JavaSpace containing the data
	 */
	private JavaSpace space;
	
	/**
	 * The response queue listening for requests
	 */
	private Queue queue;
	
	/**
	 * The Map associating an question id with a report
	 */
	private Map<UUID, Report> list = new HashMap<>();
		
	/**
	 * Print a usage message and exit
	 */
	private static void usage()
	{
		System.out.println("Usage: java -jar target/analyzer-1.0.jar <js_server_host> <js_server_port> <jms_server_host> <jms_server_port>");
		System.out.println("Launch the analyzer");
		System.out.println("with:");
		System.out.println("<js_server_host> the name of the javaspace server host");
		System.out.println("<js_server_port> the number of the javaspace server port");
		System.out.println("<jms_server_host> the name of the jms server host");
		System.out.println("<jms_server_port> the number of the jms server port");
		System.exit(0);
	}

	/**
	 * Application entry point
	 * 
	 * @param args see usage
	 */
	public static void main(String[] args) {
		if (args.length != 4 || args[0].equals("-h"))
			usage();
		
		String spaceHost = args[0];
		int spacePort = Integer.parseInt(args[1]);
		String jmsHost = args[2];
		String jmsPort = args[3];
		
		Analyzer instance = new Analyzer(spaceHost, spacePort, jmsHost, jmsPort);
	    instance.run();
	}
	
	/**
	 * Constructor
	 *
	 * @param host the name of the server host
	 * @param port the number of the server port
	 */
	public Analyzer(String spaceHost, int spacePort, String jmsHost, String jmsPort) {
		this.spaceHostName = spaceHost;
		this.spacePortNumber = spacePort;
		this.jmsHostName = jmsHost;
		this.jmsPortNumber = jmsPort;
	}
	
	/**
	 * Launch the application process
	 * 
	 * @throws Exception 
	 */
	@SuppressWarnings("JavaDoc")
	public void run() {
		
		RiverLookup rl;
		try {
			rl = new RiverLookup();
			this.space = rl.lookup(this.spaceHostName, this.spacePortNumber, JavaSpace.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Exporter myDefaultExporter =
				new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
						new BasicILFactory(), false, true);

		RemoteEventListener msgListener = null;
		try {
			msgListener = (RemoteEventListener) myDefaultExporter.export(new EntryListener());
			System.out.println("Remote listener exported");
		} catch (ExportException e) {
			e.printStackTrace();
		}



		Answer tmpFree = new AnswerFree();
		Answer tmpYesNo = new AnswerYesNo();
		Answer tmpBounded = new AnswerBounded();
		try {
			this.space.notify(tmpFree, null, msgListener, Long.MAX_VALUE, new MarshalledObject<>(tmpFree));
			this.space.notify(tmpYesNo, null, msgListener, Long.MAX_VALUE, new MarshalledObject<>(tmpYesNo));
			this.space.notify(tmpBounded, null, msgListener, Long.MAX_VALUE, new MarshalledObject<>(tmpBounded));
		} catch (TransactionException | IOException e) {
			e.printStackTrace();
		}
		System.out.println("Notifiers set");

		createResponseQueue();
		System.out.println("Response queue set");
	}
	
	/**
	 * Create a queue listening to reports requests
	 */
	private void createResponseQueue() {
		System.setProperty("java.naming.factory.initial", "fr.dyade.aaa.jndi2.client.NamingContextFactory");
		System.setProperty("java.naming.factory.host", this.jmsHostName);
		System.setProperty("java.naming.factory.port", this.jmsPortNumber);
		
		Session session = Connector.getInstance().createSession();
		
		this.queue = Helper.getQueue(session, "response");
		
		Session sessionListener = Connector.getInstance().createSession();
		try {
			MessageConsumer consumer = sessionListener.createConsumer(this.queue);
			consumer.setMessageListener(message -> {
				try {
					reply(session, message);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			});
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reply to a request with the asked report
	 * 
	 * @param session the queue session
	 * @param msg the received message
	 * @throws JMSException
	 */
	@SuppressWarnings("JavaDoc")
	private void reply(Session session, Message msg) throws JMSException {
		ObjectMessage message = (ObjectMessage) msg;
		UUID uuid = (UUID) message.getObject();
		Report report = this.list.get(uuid);
		ObjectMessage response = session.createObjectMessage(report);
		MessageProducer producer = session.createProducer(message.getJMSReplyTo());
		producer.send(response);
	}
	
	/**
	 * Read an answer from the JavaSpace
	 * 
	 * @param answer the template of an answer
	 */
	public void readAnswer(Answer answer) {
		if(answer != null) {
			if(answer instanceof AnswerFree) {
				analyze((AnswerFree) answer);
			}
			else if(answer instanceof AnswerYesNo) {
				analyze((AnswerYesNo) answer);
			}
			else if(answer instanceof AnswerBounded) {
				analyze((AnswerBounded) answer);
			} else {
				analyze(answer);
			}
		}	
	}
	
	/**
	 * Analyzes an AnswerFree and updates the report corresponding to the question
	 * 
	 * @param answer the answer to analyze
	 */
	private void analyze(AnswerFree answer) {
		UUID id = answer.question_id;
		ReportFree report;
		if(this.list.containsKey(id)) {
			report = (ReportFree) this.list.get(id);
		}
		else {
			report = new ReportFree(id);
			this.list.put(id, report);
		}
		report.incrementNbAnswers();
		report.addAnswer(answer.response);
	}
	
	/**
	 * Analyzes an AnswerYesNo and updates the report corresponding to the question
	 * 
	 * @param answer the answer to analyze
	 */
	private void analyze(AnswerYesNo answer) {
		UUID id = answer.question_id;
		ReportYesNo report;
		if(this.list.containsKey(id)) {
			report = (ReportYesNo) this.list.get(id);
		}
		else {
			report = new ReportYesNo(id);
			this.list.put(id, report);
		}
		report.incrementNbAnswers();
		if(answer.response)
			report.incrementNbYes();
		else
			report.incrementNbNo();
	}

	/**
	 *	Called if you try to analyse an unhandled Answer type
	 *
	 * @param answer the answer to analyze
	 */
	private void analyze(Answer answer) {
		System.out.println("Answer type not handled ("+answer.getClass()+") ");
	}

	/**
	 * Analyzes an AnswerBounded and updates the report corresponding to the question
	 * 
	 * @param answer the answer to analyze
	 */
	private void analyze(AnswerBounded answer) {
		UUID id = answer.question_id;
		ReportBounded report;
		if(this.list.containsKey(id)) {
			report = (ReportBounded) this.list.get(id);
		}
		else {
			report = new ReportBounded(id);
			this.list.put(id, report);
		}
		report.incrementNbAnswers();
		report.addAnswer(answer.response);
	}
	
	/**
	 * Listener notified when an answer is written on the JavaSpace
	 * 
	 * @author Maxime
	 *
	 */
	class EntryListener implements RemoteEventListener, Serializable {
		@Override
		public void notify(RemoteEvent event) {
			try {
				Answer answer_type = (Answer) event.getRegistrationObject().get();
				Entry entry = Analyzer.this.space.read(answer_type, null, Long.MAX_VALUE);
				Answer answer = (Answer) entry;
				Analyzer.this.readAnswer(answer);

			} catch (IOException | TransactionException | UnusableEntryException | InterruptedException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
