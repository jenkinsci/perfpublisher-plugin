package hudson.plugins.PerfPublisher;

import hudson.Launcher;
import hudson.matrix.MatrixAggregatable;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixConfiguration;
import hudson.matrix.MatrixProject;
import hudson.model.*;
import hudson.plugins.PerfPublisher.Report.ReportContainer;
import hudson.plugins.PerfPublisher.projectsAction.PerfPublisherFreestyleProjectAction;
import hudson.plugins.PerfPublisher.projectsAction.PerfPublisherMatrixConfigurationAction;
import hudson.plugins.PerfPublisher.projectsAction.PerfPublisherMatrixProjectAction;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * The publisher creates the results we want from the PerfPublisher execution.
 * 
 * @author Georges Bossert
 */
public class PerfPublisherPublisher extends HealthPublisher implements MatrixAggregatable {

	private String name;
	private String threshold;
	private String healthy;
	private String unhealthy;

	@DataBoundConstructor
	public PerfPublisherPublisher(String name, String threshold,
			String healthy, String unhealthy) {
		this.name = name;
		if (threshold != "") {
			this.threshold = threshold;
		} else {
			this.threshold = "0";
		}
		if (healthy != "") {
			this.healthy = healthy;
		} else {
			this.healthy = "0";
		}
		if (unhealthy != "") {
			this.unhealthy = unhealthy;
		} else {
			this.unhealthy = "0";
		}
	}

	/**
	 * @return the healthy
	 */
	public String getHealthy() {
		return healthy;
	}

	/**
	 * @return the unhealthy
	 */
	public String getUnhealthy() {
		return unhealthy;
	}

	public String getThreshold() {
		return threshold;
	}

	public String getName() {
		return name;
	}

	public Descriptor<Publisher> getDescriptor() {
		return DESCRIPTOR;
	}
	public MatrixAggregator createAggregator(final MatrixBuild matrixBuild, Launcher launcher, BuildListener listener) {
		return new PerfPublisherResultAggregator(matrixBuild, launcher, listener);
	}

	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {

		PrintStream logger = listener.getLogger();

		/**
		 * Compute the HealthDescription
		 */
		HealthDescriptor hl = new HealthDescriptor();
		try {
			hl.setMaxHealth(Integer.parseInt(unhealthy));
		} catch (java.lang.NumberFormatException e) {
			hl.setMaxHealth(0);
		}
		try {
			hl.setMinHealth(Integer.parseInt(healthy));
		} catch (java.lang.NumberFormatException e) {
			hl.setMinHealth(0);
		}
		try {
			hl.setUnstableHealth(Integer.parseInt(threshold));
		} catch (java.lang.NumberFormatException e) {
			hl.setUnstableHealth(0);
		}

		/**
		 * Define if we must parse multiple file by searching for , in the name
		 * var
		 */
		String[] files = name.split(",");
		if (files.length > 1) {
			logger.println("[CapsAnalysis] Multiple reports detected.");
		}
		ArrayList<String> filesToParse = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			FileSet fileSet = new FileSet();
			File workspace = new File(build.getProject().getWorkspace().toString());
			fileSet.setDir(workspace);
			fileSet.setIncludes(files[i].trim());
			Project antProject = new Project();
			fileSet.setProject(antProject);
			String[] tmp_files = fileSet.getDirectoryScanner(antProject).getIncludedFiles();
			for (int j=0; j<tmp_files.length; j++) {
				if (build.getProject().getWorkspace().child(tmp_files[j]).exists()) {
					filesToParse.add(tmp_files[j]);
				} else {
					logger.println("[CapsAnalysis] Impossible to analyse report " + tmp_files[j] + " file not found!");
					build.setResult(Result.UNSTABLE);
				}
			}
		}

		try {
			build.addAction(new PerfPublisherBuildAction(build, filesToParse,
					logger, hl));
			
		} catch (PerfPublisherParseException gpe) {
			logger
					.println("[CapsAnalysis] generating reports analysis failed!");
			build.setResult(Result.UNSTABLE);
		}
		return true;
	}

	public Action getProjectAction(AbstractProject project) {
		if (project instanceof MatrixProject) {
			return new PerfPublisherMatrixProjectAction((MatrixProject)project);
		}else if (project instanceof MatrixConfiguration) {
			return new PerfPublisherMatrixConfigurationAction((MatrixConfiguration) project);
		}else if (project instanceof FreeStyleProject) {
			return new PerfPublisherFreestyleProjectAction((FreeStyleProject)project);
		}
		return null;
	}

	public static final Descriptor<Publisher> DESCRIPTOR = new DescriptorImpl();

	public static final class DescriptorImpl extends Descriptor<Publisher> {
		protected DescriptorImpl() {
			super(PerfPublisherPublisher.class);
		}

		public String getDisplayName() {
			return PerfPublisherPlugin.CONFIG_DISPLAY_NAME;
		}
	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}
}
