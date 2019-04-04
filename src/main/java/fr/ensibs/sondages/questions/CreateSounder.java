package fr.ensibs.sondages.questions;

import java.util.UUID;

public interface CreateSounder {
	
	/**
	 * methode used to create sounders
	 * @param name name of the sounder
	 */
	public void createSounder(String name);
	
	/**
	 * method used to link a sounder with a question
	 * @param idsounder th id of the sounder
	 * @param q the question
	 */
	public void addQuestion(int idsounder,Question q);
	/**
	 * method used to print informations in file
	 */
	public void Save();
	
	/**
	 * method used to load data from file
	 */
	public void Load();

	/**
	 * method used ti adk the answer of the question
	 */
	public void askAnswer(int id, UUID question);
}
