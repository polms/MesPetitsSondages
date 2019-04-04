package fr.ensibs.analyzer;

import java.rmi.RemoteException;
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
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

/**
 * Analyzes answers to questions and make reports
 * 
 * @author Maxime
 *
 */
public class Analyzer {
	
	/**
	 * The host name of the server
	 */
	private String hostName;
	
	/**
	 * The port number to connect to the server
	 */
	private int portNumber;
	
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
		System.out.println("Usage: java -jar target/analyzer-1.0.jar <server_host> <server_port>");
		System.out.println("Launch the analyzer");
		System.out.println("with:");
		System.out.println("<server_host> the name of the server host");
		System.out.println("<server_port> the number of the server port");
		System.exit(0);
	}

	/**
	 * Application entry point
	 * 
	 * @param args see usage
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 2 || args.equals("-h"))
			usage();
		
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		
		Analyzer instance = new Analyzer(host, port);
	    instance.run();
	}
	
	/**
	 * Constructor
	 *
	 * @param host the name of the server host
	 * @param port the number of the server port
	 */
	public Analyzer(String host, int port) {
		this.hostName = host;
		this.portNumber = port;
	}
	
	/**
	 * Launch the application process
	 * 
	 * @throws Exception 
	 */
	public void run() {
		
		RiverLookup rl;
		try {
			rl = new RiverLookup();
			this.space = rl.lookup(this.hostName, this.portNumber, JavaSpace.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Answer tmpFree = new AnswerFree();
		Answer tmpYesNo = new AnswerYesNo();
		Answer tmpBounded = new AnswerBounded();
		EntryListener listenerFree = new EntryListener(tmpFree, this);
		EntryListener listenerYesNo = new EntryListener(tmpYesNo, this);
		EntryListener listenerBounded = new EntryListener(tmpBounded, this);
		try {
			this.space.notify(tmpFree, null, listenerFree, Long.MAX_VALUE, null);
			this.space.notify(tmpYesNo, null, listenerYesNo, Long.MAX_VALUE, null);
			this.space.notify(tmpBounded, null, listenerBounded, Long.MAX_VALUE, null);
		} catch (RemoteException | TransactionException e) {
			e.printStackTrace();
		}
		
		createResponseQueue();
		
		while(true) {
			
		}
		
	}
	
	/**
	 * Create a queue listening to reports requests
	 */
	private void createResponseQueue() {
		System.setProperty("java.naming.factory.initial", "fr.dyade.aaa.jndi2.client.NamingContextFactory");
		System.setProperty("java.naming.factory.host", this.hostName);
		System.setProperty("java.naming.factory.port", String.valueOf(this.portNumber));
		
		Session session = Connector.getInstance().createSession();
		
		this.queue = Helper.getQueue(session, "response");
		
		Session sessionListener = Connector.getInstance().createSession();
		try {
			MessageConsumer consumer = sessionListener.createConsumer(this.queue);
			consumer.setMessageListener(new MessageListener() {
				@Override
				public void onMessage(Message message) {
					try {
						reply(session, message);
					} catch (JMSException e) {
						e.printStackTrace();
					}
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
	 * @param tmp the template of an answer
	 */
	public void readAnswer(Answer tmp) {
		Answer answer = null;
		try {
			answer = (Answer) this.space.readIfExists(tmp, null, Long.MAX_VALUE);
		} catch (RemoteException | UnusableEntryException | TransactionException | InterruptedException e) {
			e.printStackTrace();
		}
		if(answer != null) {
			if(answer instanceof AnswerFree) {
				analyzeFree((AnswerFree) answer);
			}
			else if(answer instanceof AnswerYesNo) {
				analyzeYesNo((AnswerYesNo) answer);
			}
			else if(answer instanceof AnswerBounded) {
				analyzeBounded((AnswerBounded) answer);
			}
		}	
	}
	
	/**
	 * Analyzes an AnswerFree and updates the report corresponding to the question
	 * 
	 * @param answer the answer to analyze
	 */
	private void analyzeFree(AnswerFree answer) {
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
	private void analyzeYesNo(AnswerYesNo answer) {
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
	 * Analyzes an AnswerBounded and updates the report corresponding to the question
	 * 
	 * @param answer the answer to analyze
	 */
	private void analyzeBounded(AnswerBounded answer) {
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
	class EntryListener implements RemoteEventListener {
		
		/**
		 * The analyzer
		 */
		private Analyzer analyzer;
		
		/**
		 * The template of the answer to listen
		 */
		private Answer answer;
		
		/**
		 * Constructor
		 * 
		 * @param answer the template to listen
		 * @param analyzer the analyzer
		 */
		public EntryListener(Answer answer, Analyzer analyzer) {
			this.analyzer = analyzer;
			this.answer = answer;
		}

		@Override
		public void notify(RemoteEvent event) throws UnknownEventException, RemoteException {
			this.analyzer.readAnswer(answer);
		}

	}

}
