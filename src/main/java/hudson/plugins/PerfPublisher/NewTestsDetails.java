package hudson.plugins.PerfPublisher;

import java.util.Map;

import hudson.model.Run;

import hudson.model.ModelObject;

public class NewTestsDetails implements ModelObject {

    private final TrendReport report;
    private final Run<?, ?> _owner;
    private final Map<String, String> metrics;

    public NewTestsDetails(final Run<?, ?> owner, TrendReport rep, Map<String, String> metrics) {
        report = rep;
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
        return "Details of new tests.";
    }

    public TrendReport getReport() {
        return report;
    }

}
