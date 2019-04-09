package fr.ensibs.sondages.sounder;

import fr.ensibs.sondages.questions.Question;

import java.util.UUID;

public interface CreateSounder {
	
	/**
	 * method used to create sounder
	 * @param name name of the sounder
	 */
	void createSounder(String name);
	
	/**
	 * method used to link a sounder with a question
	 * @param idsounder the id of the sounder
	 * @param q the question
	 */
	void addQuestion(int idsounder, Question q);
	/**
	 * method used to print informations in file
	 */
	void Save();
	
	/**
	 * method used to load data from file
	 */
	void Load();

	/**
	 * method used to ask the answer of the question
	 */
	void askAnswer(int id, UUID question);
}
