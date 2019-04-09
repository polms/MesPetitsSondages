package fr.ensibs.sondages.analyzer;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a report for a question
 * 
 * @author Maxime
 *
 */
public abstract class Report implements Serializable {
	
	/**
	 * The id of the question
	 */
	UUID id;
		
	/**
	 * The number of answers to the question
	 */
	int nbAnswers = 0;
	
	/**
	 * Constructor
	 * 
	 * @param id the question id
	 */
	public Report(UUID id) {
		this.id = id;
	}
		
	/**
	 * Increments by 1 the number of answers
	 */
	public void incrementNbAnswers() {
		nbAnswers++;
	}
	
	/**
	 * Gets the question id
	 * 
	 * @return the question id
	 */
	public UUID getId() {
		return this.id;
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
