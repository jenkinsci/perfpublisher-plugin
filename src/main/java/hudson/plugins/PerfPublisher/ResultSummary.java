package hudson.plugins.PerfPublisher;

import java.util.Date;

import hudson.plugins.PerfPublisher.Report.ReportContainer;

/**
 * Represents the result summary of the FindBugs parser. This summary will be
 * shown in the summary.jelly script of the FindBugs result action.
 *
 * @author Ulli Hafner
 */
public final class ResultSummary {
    /**
     * Returns the message to show as the result summary.
     *
     * @param result
     *            the result
     * @return the message
     */
    public static String createSummary(final ReportContainer result) {
        StringBuilder summary = new StringBuilder();
        int numberOfTests = result.getNumberOfTest();

        summary.append("Global Test : ");
        if (numberOfTests > 0) {
            summary.append("<a href=\"findbugsResult\">");
        }
        if (numberOfTests == 1) {
            summary.append("1 test");
        }
        else {
            summary.append(numberOfTests+" tests evaluated");
        }
        if (numberOfTests > 0) {
            summary.append("</a>");
        }
        summary.append(" ");
        return summary.toString();
    }

    

    /**
     * Instantiates a new result summary.
     */
    private ResultSummary() {
        // prevents instantiation
    }
}

