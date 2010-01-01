package hudson.plugins.PerfPublisher.Report;

import java.util.ArrayList;

public class ErrorMessageContainer {

	private ArrayList<ErrorMessage> messages;
	
	public ErrorMessageContainer() {
		messages = new ArrayList<ErrorMessage>();
	}
	
	public void addErrorMessage(ErrorMessage error, Test t) {
		boolean done = false;
		for(int i=0; i<messages.size(); i++) {
			if (messages.get(i).getMessage().equalsIgnoreCase(error.getMessage())) {
				messages.get(i).addTest(t);
				messages.get(i).setNumberOfTest(messages.get(i).getNumberOfTest()+1);
				done = true;
			}
		}
		if (done == false) {
			error.addTest(t);
			messages.add(error);
		}
	}
	public ArrayList<ErrorMessage> getErrorMessages() {
		return messages;
	}
	
	
}
