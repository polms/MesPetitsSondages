package fr.ensibs.sondages.sounder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import fr.ensibs.joram.Connector;
import fr.ensibs.joram.Helper;
import fr.ensibs.sondages.questions.Question;

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
		this.sounders = new ArrayList<>();
		this.queue = Helper.getQueue(session, "request");
		
	}
	
	
	@Override
	public void createSounder(String name) {
		Sounder s= new Sounder(this.sounders.size(), name);
		sounders.add(s);
	}

	@Override
	public void addQuestion(int idsounder, Question q) {
		for (Sounder sounder : sounders) {
			// on trouve le sondeur
			if (sounder.getId() == idsounder) {
				sounder.addQuestion(q);


			}
		}

	}


	@Override
	public void Save() {
		
			
			try {
				File f = new File("sounders.ser");
				ObjectOutputStream oos= new ObjectOutputStream(new FileOutputStream(f));
				for (Sounder sounder : sounders) {
					oos.writeObject(sounder);
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
		for (Sounder sounder : sounders) {
			if (sounder.getId() == id) {
				return sounder;
			}
		}
		return null;
	}
	public Integer getId(String name) {
		for (Sounder sounder : sounders) {
			if (sounder.getName().equals(name)) {
				return sounder.getId();

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
				for (Question question1 : q) {
					if (question1.getID().equals(question)) {
						ObjectMessage message = this.session.createObjectMessage(question);
						message.setJMSReplyTo(this.queue);
						producer.send(message);
					}
				}
			}
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public boolean exist(String name) {
		for (Sounder sounder : sounders) {
			if (sounder.getName().equals(name)) {
				return true;
			}

		}
		return false;
	}
	

}
