package fr.ensibs.analyzer;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import fr.ensibs.river.RiverLookup;
import fr.ensibs.sondages.questions.Answer;
import fr.ensibs.sondages.questions.AnswerBounded;
import fr.ensibs.sondages.questions.AnswerFree;
import fr.ensibs.sondages.questions.AnswerYesNo;
import net.jini.space.JavaSpace;

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
	 * The Map associating an id with a report
	 */
	private Map<Integer, Report> list = new HashMap<>();
		
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
	public void run() throws Exception {
		
		RiverLookup rl = new RiverLookup();
		this.space = rl.lookup(this.hostName, this.portNumber, JavaSpace.class);
		
		Answer tmpFree = new AnswerFree();
		Answer tmpYesNo = new AnswerYesNo();
		Answer tmpBounded = new AnswerBounded();
		EntryListener listenerFree = new EntryListener(tmpFree);
		EntryListener listenerYesNo = new EntryListener(tmpYesNo);
		EntryListener listenerBounded = new EntryListener(tmpBounded);
		this.space.notify(tmpFree, null, listenerFree, Long.MAX_VALUE, null);
		this.space.notify(tmpYesNo, null, listenerYesNo, Long.MAX_VALUE, null);
		this.space.notify(tmpBounded, null, listenerBounded, Long.MAX_VALUE, null);
		while(true) {
			
		}
		
	}
	
	public void analyzeFree() {
		
	}
	
	public void analyzeYesNo() {
		
	}
	
	public void analyzeBounded() {
	
}

}
