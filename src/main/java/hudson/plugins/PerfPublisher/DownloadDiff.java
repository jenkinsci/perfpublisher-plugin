package hudson.plugins.PerfPublisher;

import hudson.model.Run;
import hudson.plugins.PerfPublisher.Report.ReportContainer;

public class DownloadDiff {
    private final ReportContainer report;
    private final Run<?, ?> _owner;

    public DownloadDiff(final Run<?, ?> owner, ReportContainer rep) {
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
