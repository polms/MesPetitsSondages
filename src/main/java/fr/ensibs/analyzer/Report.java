package fr.ensibs.analyzer;

import java.io.Serializable;

/**
 * Represents a report for a question
 * 
 * @author Maxime
 *
 */
public abstract class Report implements Serializable {
		
	/**
	 * The number of answers to the question
	 */
	private int nbAnswers = 0;
		
	/**
	 * Increments by 1 the number of answers
	 */
	public void incrementNbAnswers() {
		nbAnswers++;
	}
	
	/**
	 * Gets the number of answers
	 * 
	 * @return the number of answers
	 */
	public int getNbAnswers() {
		return this.nbAnswers;
	}
	
}
