package hudson.plugins.PerfPublisher.Report;

import java.util.ArrayList;

public class ErrorMessageContainer {

    private ArrayList<ErrorMessage> messages;

    public ErrorMessageContainer() {
        messages = new ArrayList<ErrorMessage>();
    }

    public void addErrorMessage(ErrorMessage error, Test t) {
        boolean done = false;
        for (ErrorMessage message : messages) {
            if (message.getMessage().equalsIgnoreCase(error.getMessage())) {
                message.addTest(t);
                message.setNumberOfTest(message.getNumberOfTest() + 1);
                done = true;
            }
        }
        if (!done) {
            error.addTest(t);
            messages.add(error);
        }
    }

    public ArrayList<ErrorMessage> getErrorMessages() {
        return messages;
    }


}
