package hudson.plugins.PerfPublisher.Report;

import java.io.IOException;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.PerfPublisher.HealthPublisher;
import hudson.plugins.PerfPublisher.PerfPublisherPlugin;
import hudson.tasks.BuildStepMonitor;


public class PerfPublisherAggregatedTestResultAction extends HealthPublisher {

	public String getIconFileName() {
	      return PerfPublisherPlugin.ICON_FILE_NAME;
	   }

	   public String getDisplayName() {
	      return "Aggregated Test Report";
	   }
	
	public BuildStepMonitor getRequiredMonitorService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
			throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		
	}

}
