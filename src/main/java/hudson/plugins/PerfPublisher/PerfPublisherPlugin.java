package hudson.plugins.PerfPublisher;

import java.util.Date;

import hudson.Plugin;
import hudson.tasks.BuildStep;

/**
 * Entry point for the PerfPublisher plugin.
 * 
 * @author Georges Bossert
 */
public class PerfPublisherPlugin extends Plugin {

	static final String ICON_FILE_NAME = "graph.gif";
	static final String DISPLAY_NAME = "Global test report";
	static final String URL = "PerfPublisher";

	@Override
	public void start() throws Exception {
		BuildStep.PUBLISHERS.addRecorder(PerfPublisherPublisher.DESCRIPTOR);

	}
}
