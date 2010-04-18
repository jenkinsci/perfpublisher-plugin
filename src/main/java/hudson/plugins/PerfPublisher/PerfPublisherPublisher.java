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
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.util.FormFieldValidator;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

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
	private String metrics;

	@DataBoundConstructor
	public PerfPublisherPublisher(String name, String threshold,
			String healthy, String unhealthy, String metrics) {
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
		this.metrics = metrics;
	}
	/**
	 * Return the metrics
	 * @return
	 */
	public String getMetrics() {
		return metrics;
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
	public void doValidateMetricsConfiguration(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
		new FormFieldValidator(req,rsp,true) {
	        protected void check() throws IOException, ServletException {
	            try {
	                ok("Success");
	            } catch (Exception e) {
	                error("Client error : "+e.getMessage());
	            }
	        }
	    }.process();
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
		 * Compute metrics parametring
		 */
		Map<String, String> list_metrics = new HashMap<String, String>();
		//Parse the field to understand the metrics
		//Format : name=xmlfield;
		if (metrics!=null && metrics.length()>0) {
			List<String> tmps = Arrays.asList(this.metrics.split(";"));
			for (String tmp : tmps) {
				List<String> f = Arrays.asList(tmp.split("="));
				if (f.size()==2 && f.get(0).trim().length()>0 && f.get(1).length()>0) {
					list_metrics.put(f.get(0).trim(), f.get(1).trim());
				}
			}
		}
		
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
			File workspace = new File(build.getWorkspace().toURI());
			
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
					logger, hl, list_metrics));
			
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

	public static final Descriptor<Publisher> DESCRIPTOR = new PerfPublisherDescriptor();
	/**
	 * Descriptor for the PerfPublisher plugin
	 * Must extends BuildStepDescriptor since issue HUDSON-5612
	 * @author gbossert
	 *
	 */
	public static final class PerfPublisherDescriptor extends BuildStepDescriptor<Publisher> {
		protected PerfPublisherDescriptor() {
			super(PerfPublisherPublisher.class);
		}

		public String getDisplayName() {
			return PerfPublisherPlugin.CONFIG_DISPLAY_NAME;
		}

		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}
	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}
}
