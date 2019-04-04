package fr.ensibs.sondages.questions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import fr.ensibs.joram.Connector;
import fr.ensibs.joram.Helper;
import net.jini.space.JavaSpace;

public class CreateSounderImpl implements CreateSounder {


	private ArrayList<Sounder> sounders;
	private Session session;
	private Connector connector;
	private Queue queue;
	
	
	//------------Constructeur vide-------
	public CreateSounderImpl(String host, int port) {
		System.setProperty("java.naming.factory.initial", "fr.dyade.aaa.jndi2.client.NamingContextFactory");
		System.setProperty("java.naming.factory.host", host);
		System.setProperty("java.naming.factory.port", String.valueOf(port));
		
		this.connector=Connector.getInstance();
		this.session = connector.createSession();
		this.sounders = new ArrayList<Sounder>();
		Helper help = new Helper();
		this.queue = help.getQueue(session, "request");
		
	}
	
	
	@Override
	public void createSounder(String name) {
		Sounder s= new Sounder(this.sounders.size(), name);
		sounders.add(s);
	}

	@Override
	public void addQuestion(int idsounder, Question q) {
		for (int i=0; i<sounders.size();i++) {
			// on trouve le sondeur
			if (sounders.get(i).getId()==idsounder) {
				this.sounders.get(i).addQuestion(q);
				
				
			}
		}

	}


	@Override
	public void Save() {
		
			
			try {
				File f = new File("sounders.ser");
				ObjectOutputStream oos= new ObjectOutputStream(new FileOutputStream(f));
				for(int i=0; i<sounders.size();i++) {
					oos.writeObject(this.sounders.get(i));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
	}


	@Override
	public void Load() {
		
		try {
			File f = new File("sounders.ser");
			ObjectInputStream ois=  new ObjectInputStream(new FileInputStream(f)) ;
			while(ois.available()>0) {
				Sounder s= (Sounder) ois.readObject();
				this.sounders.add(s);
			}
			
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public Sounder getSounder(int id) {
		for (int i=0; i<sounders.size(); i++) {
			if(this.sounders.get(i).getId()==id) {
				return this.sounders.get(i);
			}
		}
		return null;
	}
	public Integer getId(String name) {
		for (int i=0; i<sounders.size(); i++) {
			if(this.sounders.get(i).getName().equals(name)) {
				return this.sounders.get(i).getId();
				
			}
		}
		return null;
	}
	@Override
	public void askAnswer(int id, UUID question) {
		
		try {
			MessageProducer producer = this.session.createProducer(Helper.getQueue(session, "response"));
			Sounder sound = getSounder(id);
			if(sound!= null) {
				ArrayList<Question> q = sound.getQuestion();
				// compteur de questions
				for(int j=0; j<q.size();j++) {
					if (q.get(j).getID().equals(question)) {
						ObjectMessage message = this.session.createObjectMessage(question);
					}
				}
			}
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
