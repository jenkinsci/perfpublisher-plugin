package hudson.plugins.PerfPublisher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import hudson.Launcher;
import hudson.matrix.Combination;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixRun;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Cause;
import hudson.plugins.PerfPublisher.Report.MatrixTestReport;
import hudson.plugins.PerfPublisher.Report.ReportContainer;

public class PerfPublisherResultAggregator extends MatrixAggregator {

	MatrixTestReportAction result;
	
	public PerfPublisherResultAggregator(MatrixBuild build, Launcher launcher,
			BuildListener listener) {
		super(build, launcher, listener);
	}

	public boolean startBuild() throws InterruptedException, IOException {
		result = new MatrixTestReportAction(build);
		build.addAction(result);
		return true;
	}
	

	public boolean endRun(MatrixRun run) throws InterruptedException,
			IOException {
		Map<String, String> buildVariables = run.getBuildVariables();
				
		PerfPublisherBuildAction buildAction = run.getAction(PerfPublisherBuildAction.class);
		if (buildAction!=null) {
			result.addSubBuildResult(buildAction.getReports(), buildVariables);
		}
		return true;
	}
	
	public boolean endBuild() throws InterruptedException, IOException {
		result.computeStats();
		return true;
    }

}
