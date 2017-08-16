package hudson.plugins.PerfPublisher;

import hudson.model.Run;

import hudson.model.ModelObject;
import hudson.plugins.PerfPublisher.Report.ReportContainer;

public class ValidDetails implements ModelObject {

    private final ReportContainer report;
    private final Run<?, ?> _owner;

    public ValidDetails(final Run<?, ?> owner, ReportContainer rep) {
        report = rep;
        this._owner = owner;
    }

    public Run<?, ?> getOwner() {
        return _owner;
    }

    public String getDisplayName() {
        return "Details of valid messages.";
    }

    public ReportContainer getReport() {
        return report;
    }

}
