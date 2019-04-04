package fr.ensibs.analyzer;

import java.util.ArrayList;
import java.util.List;

public class ReportFree extends Report {
	
	private List<String> list = new ArrayList<>();
	
	public void addAnswer(String answer) {
		this.list.add(answer);
	}
	
	public List<String> getAnswers() {
		return this.list;
	}

}
