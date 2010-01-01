package hudson.plugins.PerfPublisher;

import hudson.model.AbstractBuild;
import hudson.plugins.PerfPublisher.Report.ReportContainer;

public class DownloadDiff {
	private final ReportContainer report;
	private final AbstractBuild<?, ?> _owner;

	public DownloadDiff(final AbstractBuild<?, ?> owner, ReportContainer rep) {
		report = rep;
		this._owner = owner;
	}

	public AbstractBuild<?, ?> getOwner() {
		return _owner;
	}

	public String getDisplayName() {
		return "Details of error.";
	}

	public ReportContainer getReport() {
		return report;
	}
}
