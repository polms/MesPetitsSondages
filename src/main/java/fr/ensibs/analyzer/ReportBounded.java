package fr.ensibs.analyzer;

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
		String s = "";
		for(int i=1; i<=this.answers.length; i++) {
			s += "\n" + i + " | " + this.answers[i-1];
		}
		return "\n ReportBounded :"
				+ "\n - nombre total de réponses = " + this.nbAnswers
				+ "\n - tableau = " + s
				+ "\n - moyenne = " + this.average;
	}

}
