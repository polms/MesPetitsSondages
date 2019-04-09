package fr.ensibs.sondages.sounder;

import java.util.ArrayList;

import fr.ensibs.sondages.questions.Question;
import net.jini.core.entry.Entry;

public class Sounder implements Entry {
	private int id;
	private String name;
	private ArrayList<Question> questions;
	
	public Sounder(int id, String name){
		this.id=id;
		this.name=name;
		this.questions = new ArrayList<Question>();
	}
	public String toString() {
		
		return id+" "+name+" "+questions.toString();
	}
	
	public ArrayList<Question> getQuestion() {
		return this.questions;
	}
	public void addQuestion(Question id) {
		questions.add(id);
	}
	public String getName() {
		return this.name;
	}
	public int getId() {
		return this.id;
	}
	
	
}
