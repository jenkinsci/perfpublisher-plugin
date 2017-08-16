package hudson.plugins.PerfPublisher;

import hudson.model.ModelObject;
import hudson.model.Run;
import hudson.plugins.PerfPublisher.Report.ReportContainer;

public class BrokenDetails implements ModelObject {

    private final ReportContainer report;
    private final Run<?, ?> _owner;

    public BrokenDetails(final Run<?, ?> owner, ReportContainer rep) {
        report = rep;
        this._owner = owner;
    }

    public Run<?, ?> getOwner() {
        return _owner;
    }

    public String getDisplayName() {
        return "Details of broken tests messages.";
    }

    public ReportContainer getReport() {
        return report;
    }


}
