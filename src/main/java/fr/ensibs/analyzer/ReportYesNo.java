package fr.ensibs.analyzer;

public class ReportYesNo extends Report {

	private int nbYes = 0;
	
	private int nbNo = 0;
	
	public void incrementNbYes() {
		nbYes++;
	}
	
	public void incrementNbNo() {
		nbNo++;
	}
	
	public int getYesAnswers() {
		return this.nbYes;
	}
	
	public int getNoAnswers() {
		return this.nbNo;
	}

}
