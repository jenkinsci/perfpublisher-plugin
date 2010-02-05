package hudson.plugins.PerfPublisher.Report;

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

}
