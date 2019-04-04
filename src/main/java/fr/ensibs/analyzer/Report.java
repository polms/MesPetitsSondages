package fr.ensibs.analyzer;

import java.io.Serializable;

public abstract class Report implements Serializable {
		
	private int nbAnswers = 0;
		
	public void incrementNbAnswers() {
		nbAnswers++;
	}
	
}
