package hudson.plugins.PerfPublisher;

import hudson.Launcher;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixRun;
import hudson.model.BuildListener;
import java.io.IOException;
import java.util.Map;

public class PerfPublisherResultAggregator extends MatrixAggregator {

    MatrixTestReportAction result;

    public PerfPublisherResultAggregator(MatrixBuild build, Launcher launcher, BuildListener listener) {
        super(build, launcher, listener);
    }

    public boolean startBuild() throws InterruptedException, IOException {
        result = new MatrixTestReportAction(build);
        build.addAction(result);
        return true;
    }

    public boolean endRun(MatrixRun run) throws InterruptedException, IOException {
        Map<String, String> buildVariables = run.getBuildVariables();

        PerfPublisherBuildAction buildAction = run.getAction(PerfPublisherBuildAction.class);
        if (buildAction != null) {
            result.addSubBuildResult(buildAction.getReports(), buildVariables);
        }
        return true;
    }

    public boolean endBuild() throws InterruptedException, IOException {
        result.computeStats();
        return true;
    }
}
