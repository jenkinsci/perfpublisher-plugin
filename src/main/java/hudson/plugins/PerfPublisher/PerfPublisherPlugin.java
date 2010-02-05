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

	public static final String ICON_FILE_NAME = "graph.gif";
	public static final String DISPLAY_NAME = "Global test report";
	public static final String GENERAL_DISPLAY_NAME = "Global test report";
	public static final String BUILD_DISPLAY_NAME = "Build test report";
	public static final String CONFIG_DISPLAY_NAME = "Activate PerfPublisher for this project";
	public static final String MATRIX_BUILD_DISPLAY_NAME = "Matrix build test report";
	public static final String MATRIX_CONFIGURATION_DISPLAY_NAME = "Matrix configuration test report";
	
	public static final String URL = "PerfPublisher";
	

	@Override
	public void start() throws Exception {
		BuildStep.PUBLISHERS.addRecorder(PerfPublisherPublisher.DESCRIPTOR);
	}
}
