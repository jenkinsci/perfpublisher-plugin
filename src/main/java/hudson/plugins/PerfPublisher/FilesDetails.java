package hudson.plugins.PerfPublisher;

import java.util.Map;

import hudson.model.Run;

import hudson.model.ModelObject;
import hudson.plugins.PerfPublisher.Report.Report;
import hudson.plugins.PerfPublisher.Report.ReportContainer;

public class FilesDetails implements ModelObject {

    private final ReportContainer report;
    private final Run<?, ?> _owner;
    private final Map<String, String> metrics;

    public FilesDetails(final Run<?, ?> owner, Report rep, Map<String, String> metrics) {

        report = new ReportContainer();
        report.addReport(rep);
        this._owner = owner;
        this.metrics = metrics;
    }

    public Map<String, String> getMetrics() {
        return metrics;
    }

    public Run<?, ?> getOwner() {
        return _owner;
    }

    public String getDisplayName() {
        return "Details...";
    }

    public ReportContainer getReport() {
        return report;
    }

}
