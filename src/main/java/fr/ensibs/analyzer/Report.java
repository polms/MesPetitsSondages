package fr.ensibs.analyzer;

import java.io.Serializable;

public abstract class Report implements Serializable {
	
	private String question;
	
	private int nbAnswers;
	
	private int nbNoAnswers;
	
	public Report() {}
	
	public Report(String question) {
		this.question = question;
	}

}
