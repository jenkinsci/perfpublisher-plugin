package hudson.plugins.PerfPublisher;

import hudson.model.ModelObject;
import hudson.model.Run;
import hudson.plugins.PerfPublisher.Report.Report;
import hudson.plugins.PerfPublisher.Report.ReportContainer;
import java.util.Map;

public class CategoryDetails implements ModelObject {

    private final ReportContainer report;
    private final Run<?, ?> _owner;
    private final Map<String, String> metrics;

    public CategoryDetails(final Run<?, ?> owner, Report rep, Map<String, String> metrics) {

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
