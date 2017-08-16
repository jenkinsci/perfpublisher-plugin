package hudson.plugins.PerfPublisher;

import java.util.List;

import hudson.model.Run;

import hudson.model.ModelObject;
import hudson.plugins.PerfPublisher.Report.Test;

public class ChangedStatusTestsDetails implements ModelObject {

    private final TrendReport report;
    private final Run<?, ?> _owner;

    public ChangedStatusTestsDetails(final Run<?, ?> owner, TrendReport rep) {
        report = rep;
        this._owner = owner;
    }

    public Run<?, ?> getOwner() {
        return _owner;
    }

    public String getDisplayName() {
        return "Details of status changed tests.";
    }

    public TrendReport getReport() {
        return report;
    }

    public String getPageContent() {
        StringBuilder stb = new StringBuilder();
        List<Test> tests = report.getExecutionStatusChangedTests();
        if (tests.size() > 0) {

            stb.append("<table border=\"1px\" class=\"pane sortable\">");
            stb.append("<tr>");
            stb.append("<td class= \"pane-header\" title=\"Message\">Tests</td>");
            stb.append("<td class=\"pane-header\" title=\"Number of tests\">Execution status modification</td>");
            stb.append("</tr>");


            for (Test t : tests) {
                stb.append("<tr><td><a class=\"info_bulle\" href=\"../testDetails." + t.getNameForUrl() + "\">" + t.getName() + "</a></td>");
                if (t.isExecuted()) {
                    stb.append("<td>Not executed <img border=\"0\" src=\"/plugin/perfpublisher/icons/bullet_go.png\" /> <b>Executed</b></td>");
                } else {
                    stb.append("<td>Executed <img border=\"0\" src=\"/plugin/perfpublisher/icons/bullet_go.png\" /> <b>Not executed</b></td>");
                }
                stb.append("</tr>");
            }


            stb.append("</table>");
        } else {
            stb.append("<b>None of the tests has changed of execution status.</b><br />");
        }

        tests = report.getSuccessStatusChangedTests();
        if (tests.size() > 0) {

            stb.append("<table border=\"1px\" class=\"pane sortable\">");
            stb.append("<tr>");
            stb.append("<td class= \"pane-header\" title=\"Message\">Tests</td>");
            stb.append("<td class=\"pane-header\" title=\"Number of tests\">Success status modification</td>");
            stb.append("</tr>");


            for (Test t : tests) {
                stb.append("<tr><td><a class=\"info_bulle\" href=\"../testDetails." + t.getNameForUrl() + "\">" + t.getName() + "</a></td>");
                if (t.isSuccessfull()) {
                    stb.append("<td>Failed <img border=\"0\" src=\"/plugin/perfpublisher/icons/bullet_go.png\" /> <b>Successfull</b></td>");
                } else {
                    stb.append("<td>Successfull <img border=\"0\" src=\"/plugin/perfpublisher/icons/bullet_go.png\" /> <b>Failed</b></td>");
                }
                stb.append("</tr>");
            }


            stb.append("</table>");
        } else {
            stb.append("<b>None of the tests has changed of success status.</b><br />");
        }


        return stb.toString();
    }


}
