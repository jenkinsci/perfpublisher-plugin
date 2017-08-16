package hudson.plugins.PerfPublisher;

import hudson.model.Run;

import hudson.model.ModelObject;
import hudson.plugins.PerfPublisher.Report.ReportContainer;

public class ErrorsDetails implements ModelObject {

    private final ReportContainer report;
    private final Run<?, ?> _owner;

    public ErrorsDetails(final Run<?, ?> owner, ReportContainer rep) {
        report = rep;
        this._owner = owner;
    }

    public Run<?, ?> getOwner() {
        return _owner;
    }

    public String getDisplayName() {
        return "Details of error.";
    }

    public ReportContainer getReport() {
        return report;
    }

}
