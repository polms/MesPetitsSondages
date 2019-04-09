package fr.ensibs.sondages.analyzer;

import java.util.UUID;

/**
 * Represents a report for a bounded question
 * 
 * @author Maxime
 *
 */
public class ReportBounded extends Report {

	/**
	 * The array with the number of each answer between 1 and 5
	 */
	private int[] answers = new int[5];
	
	/**
	 * The average of the answers
	 */
	private int average;
	
	/**
	 * Constructor
	 * 
	 * @param id the question id
	 */
	public ReportBounded(UUID id) {
		super(id);
	}
	
	/**
	 * Adds an answer to the array
	 * 
	 * @param answer
	 */
	public void addAnswer(int answer) {
		answers[answer]++;
		average(answer);
	}
	
	/**
	 * Calcutes the average
	 * 
	 * @param answer
	 */
	private void average(int answer) {
		this.average = Math.round(((average * (this.nbAnswers-1)) + answer) / this.nbAnswers);
	}
	
	/**
	 * Gets the array of answers
	 * 
	 * @return the array of answers
	 */
	public int[] getAnswers() {
		return this.answers;
	}
	
	/**
	 * Gets the average
	 * 
	 * @return the average
	 */
	public int getAverage() {
		return this.average;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for(int i=1; i<=this.answers.length; i++) {
			s.append("\n").append(i).append(" | ").append(this.answers[i - 1]);
		}
		return "\n ReportBounded :"
				+ "\n - id question = " + this.id
				+ "\n - nombre total de réponses = " + this.nbAnswers
				+ "\n - tableau = " + s
				+ "\n - moyenne = " + this.average;
	}

}
