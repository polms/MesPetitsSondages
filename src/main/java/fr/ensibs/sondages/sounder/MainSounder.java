package fr.ensibs.sondages.sounder;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;

import fr.ensibs.joram.Connector;
import fr.ensibs.joram.Helper;
import fr.ensibs.sondages.questions.*;

public class MainSounder {
	
	private static String port;
	private static String host;
	private CreateSounderImpl createsounder;
	private Poll poll;
	
	private static void usage()
	{
		System.out.println("Usage:sounder <jms_host> <jms_port>");

		System.exit(0);
	}
	public static void main(String[] args) {
		
		if (args.length != 2 || args.equals("-h"))
			usage();
		
		host = args[0];
		port = args[1];
		System.setProperty("java.naming.factory.initial", "fr.dyade.aaa.jndi2.client.NamingContextFactory");
		System.setProperty("java.naming.factory.host", host);
		System.setProperty("java.naming.factory.port", port);
		MainSounder instance = new MainSounder(host, port);
	    try {
			instance.run();
		} catch (Exception e) {
			System.out.println("can't run");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public MainSounder(String host, String port) {
		this.host = host;
		this.port = port;
		this.createsounder=new CreateSounderImpl(this.host, this.port);
		this.createsounder.Load();
		this.poll=new Poll(null);
	}

	
	public void run() throws Exception {
		
		   System.out.println("Enter commands:"
	                + "\n CREATE*<name>                                             to create a new questioner"
	                + "\n CREATEQUESTION*<question>*<name>*<Free/YesNo/Bounded>          to ask a new question"
	                + "\n LISTQUESTION*<name>                                       to obtain all the question of the user"
	                + "\n GETANSWER*<name>*<numquestion>                            to obtain an answer"
	                );
		   
	        Scanner scanner = new Scanner(System.in);
	        String line = scanner.nextLine();
	        while (!line.toLowerCase().equals("quit")) {
	            String[] command = line.split("\\*+");
	            switch (command[0].toUpperCase()) {
	                case "CREATE":{
	                	
	                	if(!this.createsounder.exist(command[1])){
	                		this.createsounder.createSounder(command[1]);
		                    System.out.println("Utilisateur crée");
	                	}
	                	else {System.out.println("Ce nom est déjà utilisé");}
	                    
	                    }
	                    
	                    break;
	                case "CREATEQUESTION": {
	                	
                		if(this.createsounder.exist(command[2])) {
                			
                			switch(command[3].toUpperCase()){
                			case"FREE":{
                				Question q= this.poll.ask(command[1], AnswerFree.class);
                				int id = this.createsounder.getId(command[2]);
                    			this.createsounder.addQuestion(id, q);
                				
                			}
                			break;
                			case"BOUNDED":{
                				Question q= this.poll.ask(command[1], AnswerBounded.class);
                				int id = this.createsounder.getId(command[2]);
                    			this.createsounder.addQuestion(id, q);
                				
                			}	
                			break;
                			case"YESNO":{
                				Question q= this.poll.ask(command[1], AnswerYesNo.class);
                				int id = this.createsounder.getId(command[2]);
                    			this.createsounder.addQuestion(id, q);
                				
                			}
                			break;
                			default:
        	                    System.err.println("Unknown type: "+command[4]);
                			
                    		
                			
                			}
                		
                		}
                	
	                		
	                	
	                }
	                    break;
	                case "LISTQUESTION": {
	                	if (this.createsounder.exist(command[1])) {
	                		 ArrayList<Question> question =this.createsounder.getSounder(this.createsounder.getId(command[1])).getQuestion();
	 	                    System.out.println("questions:");

	 	                    for(int i=0; i<question.size();i++) {
	 	                    	System.out.println(i+"- "+ question.get(i).getQuestion());
	 	                    }

	                		
	                	}
	                   
	                }
	                    break;
	                case "GETANSWER": {

	                	if(this.createsounder.exist(command[1])&& this.createsounder.getSounder(this.createsounder.getId(command[1])).getQuestion().size()>Integer.parseInt(command[2])){
	                		//demande    1) nom     2)idquestion
	                		Sounder sondeur= this.createsounder.getSounder(this.createsounder.getId(command[1]));
	                		
	                		UUID id = sondeur.getQuestion().get(Integer.parseInt(command[2])).getID();
	                		this.createsounder.askAnswer(sondeur.getId(), id);
	                		
	                
	                	}     

	                }
	                    break;
	                
	                default:
	                    System.err.println("Unknown command: \"" + command[0] + "\"");
	            }
	            line = scanner.nextLine();
	        }
	        this.createsounder.Save();
	        System.out.println("Exit");
	        System.exit(0);
			
		
		}

}
