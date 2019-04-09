package fr.ensibs.sondages.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a report for a free question
 * 
 * @author Maxime
 *
 */
public class ReportFree extends Report {

	/**
	 * The list of answers to the question
	 */
	private List<String> list = new ArrayList<>();
	
	/**
	 * Constructor
	 * 
	 * @param id the question id
	 */
	public ReportFree(UUID id) {
		super(id);
	}
	
	/**
	 * Adds an answer to the list
	 * 
	 * @param answer the answer to add
	 */
	public void addAnswer(String answer) {
		this.list.add(answer);
	}
	
	/**
	 * Gets the list of answers
	 * 
	 * @return the list of answers
	 */
	public List<String> getAnswers() {
		return this.list;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for(String z : this.list) {
			s.append("\n").append(z);
		}
		return "\n ReportFree :"
				+ "\n - id question = " + this.id
				+ "\n - nombre total de réponses = " + this.nbAnswers
				+ "\n - liste des réponses = " + s;
	}

}
