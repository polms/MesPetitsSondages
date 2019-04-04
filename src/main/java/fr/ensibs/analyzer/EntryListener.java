package fr.ensibs.analyzer;

import java.rmi.RemoteException;

import fr.ensibs.sondages.questions.Answer;
import fr.ensibs.sondages.questions.AnswerBounded;
import fr.ensibs.sondages.questions.AnswerFree;
import fr.ensibs.sondages.questions.AnswerYesNo;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;

public class EntryListener implements RemoteEventListener {
	
	private Analyzer analyzer;
	
	private Answer answer;
	
	public EntryListener(Answer answer) {
		this.analyzer = analyzer;
		this.answer = answer;
	}

	@Override
	public void notify(RemoteEvent event) throws UnknownEventException, RemoteException {
		if(answer instanceof AnswerFree) {
			this.analyzer.analyzeFree();
		}
		else if(answer instanceof AnswerYesNo) {
			this.analyzer.analyzeYesNo();
		}
		else if(answer instanceof AnswerBounded) {
			this.analyzer.analyzeBounded();
		}
	}

}
