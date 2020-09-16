package hudson.plugins.PerfPublisher;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.matrix.MatrixAggregatable;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;
import hudson.model.*;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.util.FormFieldValidator;
import org.jenkinsci.Symbol;
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
  private String unstableThreshold;
  private String healthy;
  private String unhealthy;
  private String metrics;
  private boolean parseAllMetrics;

  @DataBoundConstructor
  public PerfPublisherPublisher(String name, String threshold, String unstableThreshold, String healthy, String unhealthy, String metrics, boolean parseAllMetrics) {
    this.name = name;
    this.threshold = threshold;
    this.unstableThreshold = unstableThreshold;
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
    this.parseAllMetrics = parseAllMetrics;
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

  public String getUnstableThreshold() {
    return unstableThreshold;
  }

  public boolean isParseAllMetrics() {
    return parseAllMetrics;
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

  public BuildStepDescriptor<Publisher> getDescriptor() {
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

  public void perform(Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException {
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
      hl.setUnstableFailedHealth(Integer.parseInt(threshold));
    } catch (java.lang.NumberFormatException e) {
      hl.setUnstableFailedHealth(-1);
    }

    try {
      hl.setUnstableUnstableHealth(Integer.parseInt(unstableThreshold));
    } catch (java.lang.NumberFormatException e) {
      hl.setUnstableUnstableHealth(-1);
    }

    /**
     * Define if we must parse multiple file by searching for , in the name
     * var
     */
    String[] files = name.split(",");
    if (files.length > 1) {
      logger.println("[PerfPublisher] Multiple reports detected.");
    }
    ArrayList<FilePath> filesToParse = new ArrayList<FilePath>();
	
    final FilePath moduleRoot = workspace;
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
        try {
          logger.println("[PerfPublisher] FilePath found and copied to master: " + reports[j].getRemote());
          final String targetFileName = moduleRoot.toURI().relativize(reports[j].toURI()).toString();
          final FilePath targetPath = new FilePath(buildTarget, targetFileName);
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
      build.addAction(new PerfPublisherBuildAction(build, filesToParse, logger, hl, list_metrics, parseAllMetrics));
    } catch (PerfPublisherParseException gpe) {
      logger.println("[PerfPublisher] generating reports analysis failed!");
      build.setResult(Result.UNSTABLE);
    }
  }

  @Extension
  public static final BuildStepDescriptor<Publisher> DESCRIPTOR = new PerfPublisherDescriptor();
  /**
   * Descriptor for the PerfPublisher plugin
   * Must extends BuildStepDescriptor since issue HUDSON-5612
   * @author gbossert
   *
   */
  @Symbol("perfpublisher")
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
    return BuildStepMonitor.NONE;
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
