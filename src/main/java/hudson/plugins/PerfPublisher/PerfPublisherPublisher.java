package hudson.plugins.PerfPublisher;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.matrix.*;
import hudson.model.*;
import hudson.plugins.PerfPublisher.projectsAction.PerfPublisherFreestyleProjectAction;
import hudson.plugins.PerfPublisher.projectsAction.PerfPublisherMatrixConfigurationAction;
import hudson.plugins.PerfPublisher.projectsAction.PerfPublisherMatrixProjectAction;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.util.FormFieldValidator;

import org.jenkinsci.remoting.RoleChecker;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

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
  public PerfPublisherPublisher(String name, String threshold, String healthy, String unhealthy, String metrics) {
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

  private Map<String, String> parseMetrics(String metricsString) { 
    Map<String, String> list_metrics = new LinkedHashMap<String, String>();
    //Parse the field to understand the metrics
    //Format : name=xmlfield;
    if (metricsString != null && metricsString.length() > 0) {
      List<String> tmps = Arrays.asList(metricsString.split(";"));
      for (String tmp : tmps) {
        List<String> f = Arrays.asList(tmp.split("="));
        if (f.size()==2 && f.get(0).trim().length()>0 && f.get(1).length()>0) {
          list_metrics.put(f.get(0).trim(), f.get(1).trim());
        }
      }
    }
    return list_metrics;
  }

  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

    PrintStream logger = listener.getLogger();
    /**
     * Compute metrics parametring
     */
    Map<String, String> list_metrics = parseMetrics(metrics);

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
      hl.setUnstableHealth(-1);
    }

    /**
     * Define if we must parse multiple file by searching for , in the name
     * var
     */
    String[] files = name.split(",");
    if (files.length > 1) {
      logger.println("[CapsAnalysis] Multiple reports detected.");
    }
    ArrayList<FilePath> filesToParse = new ArrayList<FilePath>();
	
    final FilePath[] moduleRoots = build.getModuleRoots();
    final boolean multipleModuleRoots = moduleRoots != null && moduleRoots.length > 1;
    final FilePath moduleRoot = multipleModuleRoots ? build.getWorkspace() : build.getModuleRoot();
    final File buildCoberturaDir = build.getRootDir();
    FilePath buildTarget = new FilePath(buildCoberturaDir);
		
    for (int i = 0; i < files.length; i++) {
      FilePath[] reports = new FilePath[0];
      try {
        reports = moduleRoot.act(new ParseReportCallable(files[i]));
      } catch (IOException e) {
        Util.displayIOException(e, listener);
        e.printStackTrace(listener.fatalError("Unable to find coverage results"));
        build.setResult(Result.FAILURE);
      }
      
      for (int j = 0; j < reports.length; j++) {
        logger.println("[CapsAnalysis] FilePath Found and copied to master: " + reports[j].getRemote());
        final FilePath targetPath = new FilePath(buildTarget, reports[j].getName());
        try {
          reports[j].copyTo(targetPath);
          filesToParse.add(targetPath);
        } catch (IOException e) {
          Util.displayIOException(e, listener);
          e.printStackTrace(listener.fatalError("Unable to copy coverage from " + reports[j] + " to " + buildTarget));
          build.setResult(Result.FAILURE);
        }
      }
    }

    try {
      build.addAction(new PerfPublisherBuildAction(build, filesToParse, logger, hl, list_metrics));
    } catch (PerfPublisherParseException gpe) {
      logger.println("[CapsAnalysis] generating reports analysis failed!");
      build.setResult(Result.UNSTABLE);
    }
    return true;
  }

  public Action getProjectAction(AbstractProject project) {
    Map<String, String> list_metrics = parseMetrics(metrics);
    if (project instanceof MatrixProject) {
      return new PerfPublisherMatrixProjectAction((MatrixProject)project, list_metrics);
    }else if (project instanceof MatrixConfiguration) {
      return new PerfPublisherMatrixConfigurationAction((MatrixConfiguration) project);
    }else if (project instanceof FreeStyleProject) {
      return new PerfPublisherFreestyleProjectAction((FreeStyleProject)project, list_metrics);
    }
    return null;
  }

  @Extension
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
	
  public static class ParseReportCallable implements FilePath.FileCallable<FilePath[]> {
    
    private static final long serialVersionUID = 1L;
    private final String reportFilePath;
    
    public ParseReportCallable(String reportFilePath) {
      this.reportFilePath = reportFilePath;
    }

    public FilePath[] invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
            
      FilePath[] r = new FilePath(f).list(reportFilePath);
      return r;
    }

	@Override
	public void checkRoles(RoleChecker checker) throws SecurityException {
		// Do nothing.
	}
  }
}
