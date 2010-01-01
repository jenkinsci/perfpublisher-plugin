	package hudson.plugins.PerfPublisher;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;
import hudson.model.Result;
import hudson.model.HealthReportingAction;
import hudson.plugins.PerfPublisher.Report.FileContainer;
import hudson.plugins.PerfPublisher.Report.Report;
import hudson.plugins.PerfPublisher.Report.ReportContainer;
import hudson.plugins.PerfPublisher.Report.Test;
import hudson.util.ChartUtil;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.util.Rotation;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

/**
 * Action used for PerfPublisher report on build level.
 * 
 * @author Georges Bossert
 */
public class PerfPublisherBuildAction extends AbstractPerfPublisherAction
		implements HealthReportingAction {

	private final AbstractBuild<?, ?> build;
	private Report report;
	private ReportContainer reports = new ReportContainer();
	private FileContainer reportsFiles = new FileContainer();
	/** Parameters for the health report. */
	private final HealthDescriptor healthDescriptor;
	
	/** Pre-Loaded stats */
	private int numberOfTest=-1;
	private int numberOfExecutedTest=-1;
	private double percentOfExecutedTest=-1;
	private int numberOfNotExecutedTest=-1;
	private double percentOfNotExecutedTest=-1;
	private int numberOfPassedTest=-1;
	private double percentOfPassedTest=-1;
	private int numberOfFailedTest=-1;
	private double percentOfFailedTest=-1;
	private int numberOfCompileTimeTest=-1;
	private double averageOfCompileTime=-1;
	private int numberOfExecutionTimeTest=-1;
	private double averageOfExecutionTime=-1;
	private int numberOfPerformanceTest=-1;
	private double averageOfPerformance=-1;
	private int numberOfNewTests=-1;
	private double percentOfNewTests=-1;
	private int numberOfDeletedTests=-1;
	private double percentOfDeletedTests=-1;
	private List<Test> executedTests;
	private TrendReport trendReport;
	private int numberOfSuccessStatusChangedTests = -1;
	private int numberOfExecutionStatusChangedTests = -1;
	private double percentOfSuccessStatusChangedTests = -1;
	private double percentOfExecutionStatusChangedTests = -1;
	
	/**
	 * Returns the build as owner of this action.
	 * 
	 * @return the owner
	 */
	public final AbstractBuild<?, ?> getOwner() {
		return build;
	}

	public PerfPublisherBuildAction(AbstractBuild<?, ?> build,
			ArrayList<String> files, PrintStream logger,
			HealthDescriptor healthDescriptor) {
		this.build = build;
		this.executedTests = new ArrayList<Test>();
		/**
		 * Compute the healthDescription
		 */
		this.healthDescriptor = healthDescriptor;

		for (int i = 0; i < files.size(); i++) {
			String current_report = files.get(i);
			InputStream is;
			try {
				is = build.getProject().getWorkspace().child(current_report)
						.read();
				logger.println("[CapsAnalysis] Parsing du Report : "
						+ current_report);
				ReportReader rs = new ReportReader(is, logger);
				report = rs.getReport();
				report.setFile(current_report);
				reports.addReport(report, false);
				reports.addFile(current_report);
			} catch (IOException e) {
				logger.println("[CapsAnalysis] Impossible to analyse report "
						+ current_report + ", file can't be read.");
				build.setResult(Result.UNSTABLE);
			}
			if (healthDescriptor.getUnstableHealth() > 0
					&& reports.getNumberOfFailedTest() > healthDescriptor
							.getUnstableHealth()) {
				build.setResult(Result.UNSTABLE);
			}
		}
		/**
		 * Compute Reports Stats
		 */
		logger.println("[CapsAnalysis] Compute global statistics...");
		reports.computeStats();

		logger
				.println("[CapsAnalysis] [--------------------------------------------------]");
		logger.println("[CapsAnalysis] Number of parsed files : "
				+ reports.getNumberOfFiles());
		logger.println("[CapsAnalysis] Number of reports : "
				+ reports.getNumberOfReports());
		logger.println("[CapsAnalysis] Number of test : "
				+ reports.getNumberOfTest());
		logger.println("[CapsAnalysis] Number of executed test : "
				+ reports.getNumberOfExecutedTest());
		logger.println("[CapsAnalysis] Number of not executed test : "
				+ reports.getNumberOfNotExecutedTest());
		logger.println("[CapsAnalysis] Number of passed test : "
				+ reports.getNumberOfPassedTest());
		logger.println("[CapsAnalysis] Number of failed test : "
				+ reports.getNumberOfFailedTest());

		for (int i = 0; i < reports.getCategories().size(); i++) {
			logger
					.println("[CapsAnalysis] ---------------------------------------------------");
			logger.println("[CapsAnalysis] Category : "
					+ reports.getCategories().get(i));
			logger.println("[CapsAnalysis]  - Number of test : "
					+ reports.getReportOfThisCategorie(
							reports.getCategories().get(i)).getNumberOfTest());
		}

		for (int j = 0; j < reports.getNumberOfTest(); j++) {
			logger.println("[CapsAnalysis] "
					+ reports.getTests().get(j).getName());
		}

		logger
				.println("[CapsAnalysis] ---------------------------------------------------");
		logger.println("[CapsAnalysis] Analysis :");
		logger
				.println("[CapsAnalysis] ---------------------------------------------------");
		logger.println("[CapsAnalysis] Performance :");
		logger.println("[CapsAnalysis]  - Worst Perfomance : "
				+ getReports().getWorstPerformanceTestValue());
		logger.println("[CapsAnalysis]  - Best Perfomance : "
				+ getReports().getBestPerformanceTestValue());
		logger.println("[CapsAnalysis]  - Average Perfomance : "
				+ getReports().getAverageOfPerformance());
		logger.println("[CapsAnalysis] Execution Time :");
		logger.println("[CapsAnalysis]  - Worst Execution Time : "
				+ getReports().getWorstExecutionTimeTestValue());
		logger.println("[CapsAnalysis]  - Best Execution Time : "
				+ getReports().getBestExecutionTimeTestValue());
		logger.println("[CapsAnalysis]  - Average Execution Time : "
				+ getReports().getAverageOfExecutionTime());
		logger.println("[CapsAnalysis] Compile Time :");
		logger.println("[CapsAnalysis]  - Worst Compile Time : "
				+ getReports().getWorstCompileTimeTestValue());
		logger.println("[CapsAnalysis]  - Best Compile Time : "
				+ getReports().getBestCompileTimeTestValue());
		logger.println("[CapsAnalysis]  - Average Compile Time : "
				+ getReports().getAverageOfCompileTime());
		logger
				.println("[CapsAnalysis] [--------------------------------------------------]");
	}
	
	public List<Test> getExecutedTests() {
		if (this.executedTests==null || this.executedTests.size()==0) {
			this.executedTests = this.reports.getExecutedTests();
		}
		return this.executedTests;
	}

	public AbstractBuild<?, ?> getBuild() {
		return build;
	}

	public Report getReport() {
		return report;
	}

	public ReportContainer getReports() {
		return reports;
	}

	public FileContainer getFiles() {
		return reportsFiles;
	}
	/**
	 * @return List of builds in html options format
	 */
	public String getHtmlListOfBuildsInOptions() {
		StringBuilder strbuilder = new StringBuilder();
		for (Object build : this.build.getProject().getBuilds()) {
			AbstractBuild abstractBuild = (AbstractBuild) build;
			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(
							Result.UNSTABLE)) {
				PerfPublisherBuildAction ac = abstractBuild.getAction(PerfPublisherBuildAction.class);
				if (ac!=null) {
				strbuilder.append("<option value=\""+abstractBuild.getNumber()+"\"");
				strbuilder.append(">"+abstractBuild.getNumber()+"</option>\n");
				}
			}
		}
		
		return strbuilder.toString();
	}
	public String getHtmlListOfBuildsInOptionsWithSelected() {
		StringBuilder strbuilder = new StringBuilder();
		for (Object build : this.build.getProject().getBuilds()) {
			AbstractBuild abstractBuild = (AbstractBuild) build;
			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(
							Result.UNSTABLE)) {
				PerfPublisherBuildAction ac = abstractBuild.getAction(PerfPublisherBuildAction.class);
				if (ac!=null) {
				strbuilder.append("<option value=\""+abstractBuild.getNumber()+"\"");
				if (abstractBuild.getNumber() == this.build.getNumber()) {
					strbuilder.append(" selected");
				}
				strbuilder.append(">"+abstractBuild.getNumber()+"</option>\n");
				}
			}
		}
		
		return strbuilder.toString();
	}

	/**
	 * @return Summary HTML
	 */
	public String getSummary() {
		StringBuilder strbuilder = new StringBuilder();
		strbuilder.append("<div class=\"progress-container\">");
		int tmp1 = this.getNumberOfFailedTest();
		int tmp2 = this.getNumberOfPassedTest();
		double tmp3 = this.getPercentOfFailedTest();
		double tmp4 = this.getPercentOfPassedTest();
		
		if (tmp3<15) {
			strbuilder.append("<div id=\"red\" style=\"width:15%;\">"+tmp1+"</div>");
			strbuilder.append("<div id=\"blue\" style=\"width:85%;\">"+tmp2+"</div>");
		} else {
			strbuilder.append("<div id=\"red\" style=\"width:"+tmp3+"%;\">"+tmp3+"% ("+tmp1+")</div>");
			strbuilder.append("<div id=\"blue\" style=\"width:"+tmp4+"%;\">"+tmp4+"% ("+tmp2+")</div>");
		}
		strbuilder.append("</div>");
		return strbuilder.toString();
	}
	public String getDetailSummary() {
		StringBuilder strbuilder = new StringBuilder();
		
		strbuilder.append("Number of parsed files : <b>"+reports.getNumberOfFiles()+".</b>");
		strbuilder.append("<br />");
		strbuilder.append("Number of executed tests : <b>"+this.getNumberOfExecutedTest()+".</b>");
		strbuilder.append("<br />");
		
		return strbuilder.toString();
	}
	
	public String getRegression() {
		
		StringBuilder strb = new StringBuilder();
		List<Test> regressions = new ArrayList<Test>();
		List<Test> tmpTests = this.getTrendReport().getSuccessStatusChangedTests();
		for (int i=0; i<tmpTests.size(); i++) {
			System.out.println("test = "+tmpTests.get(i).getName()+" -> "+tmpTests.get(i).getSuccess().isPassed());
			if (!tmpTests.get(i).isSuccessfull()) {
				regressions.add(tmpTests.get(i));
			}
		}
		if (!regressions.isEmpty()) {
			strb.append("<div class=\"warning_regression\">");
			strb.append("This build has discovered "+regressions.size()+" regression(s).");
			strb.append("</div>");
		}
		
		
		return strb.toString();
	}
	

	/**
	 * @return the associated trend report
	 */
	public TrendReport getTrendReport() {
		if (this.trendReport == null) {
			this.trendReport = computeTrendReport();
		}
		return this.trendReport;
	}
	private TrendReport computeTrendReport() {
		Object ob = build.getPreviousNotFailedBuild();
		AbstractBuild build = (AbstractBuild) ob;
		if (build != null) {
			PerfPublisherBuildAction ac = build
					.getAction(PerfPublisherBuildAction.class);
			if (ac != null) {
				return new TrendReport(reports, ac.getReports());
			}
		}
		return null;
	}

	public void doTestGraph(StaplerRequest request, StaplerResponse response)
			throws IOException {

		if (shouldReloadGraph(request, response, build)) {
			ChartUtil.generateGraph(request, response, createTestGraph(), 800,
					400);
		}
	}

	private JFreeChart createTestGraph() {
		DefaultStatisticalCategoryDataset timeDS = new DefaultStatisticalCategoryDataset();
		DataSetBuilder<String, Comparable> lengthDS = new DataSetBuilder<String, Comparable>();

		for (Test test : report.getTests()) {
			timeDS.add(test.getSuccess().getState(), test.getCompileTime()
					.getMeasure(), "Report success state", test.getName());
			lengthDS.add(test.getExecutionTime().getMeasure(),
					"Report execution time", test.getName());
		}

		final CategoryAxis xAxis = new CategoryAxis("Test name");
		xAxis.setLowerMargin(0.01);
		xAxis.setUpperMargin(0.01);
		xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		xAxis.setMaximumCategoryLabelLines(3);

		final ValueAxis timeAxis = new NumberAxis("Time (ms)");
		final ValueAxis lengthAxis = new NumberAxis("Length (bytes)");

		final BarRenderer timeRenderer = new StatisticalBarRenderer();
		timeRenderer.setSeriesPaint(2, ColorPalette.RED);
		timeRenderer.setSeriesPaint(1, ColorPalette.YELLOW);
		timeRenderer.setSeriesPaint(0, ColorPalette.BLUE);
		timeRenderer.setItemMargin(0.0);

		final CategoryPlot plot = new CategoryPlot(timeDS, xAxis, timeAxis,
				timeRenderer);
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(null);
		plot.setForegroundAlpha(0.8f);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.black);

		final CategoryItemRenderer lengthRenderer = new LineAndShapeRenderer();
		plot.setRangeAxis(1, lengthAxis);
		plot.setDataset(1, lengthDS.build());
		plot.mapDatasetToRangeAxis(1, 1);
		plot.setRenderer(1, lengthRenderer);

		JFreeChart chart = new JFreeChart("Test time", plot);
		chart.setBackgroundPaint(Color.WHITE);

		return chart;
	}

	/**
	 * Returns the dynamic result
	 * 
	 * @param link
	 *            the link to identify the sub page to show
	 * @param request
	 *            Stapler request
	 * @param response
	 *            Stapler response
	 * @return the dynamic result of the analysis.
	 */
	public Object getDynamic(final String link, final StaplerRequest request,
			final StaplerResponse response) {

		Object resultat = null;
		if (link.startsWith("testDetails.")) {
			String testName = StringUtils.substringAfter(link, "testDetails.");
			resultat = new TestDetails(getOwner(), reports.getTestWithName(Test
					.ResolveTestNameInUrl(testName)));
		} else if (link.startsWith("categoryDetails.")) {
			int indiceCat = Integer.parseInt(StringUtils.substringAfter(link,
					"categoryDetails."));
			resultat = new CategoryDetails(getOwner(), reports
					.getReportOfThisCategorie(reports.getCategories().get(
							indiceCat)));
		} else if (link.startsWith("filesDetails.")) {
			int indiceFil = Integer.parseInt(StringUtils.substringAfter(link,
					"filesDetails."));
			resultat = new FilesDetails(getOwner(), reports
					.getReportOfThisFile(reports.getFiles().get(indiceFil)));
		} else if (link.startsWith("diff")) {
			resultat = new DownloadDiff(getOwner(), reports);
		} else if (link.startsWith("errorsDetails.")) {
			String message = StringUtils.substringAfter(link, "errorsDetails.");
			if (message.equals("all")) {
				resultat = new ErrorsDetails(getOwner(), reports);
			} else {
				resultat = new ErrorsDetails(getOwner(), reports);
			}
		} else if (link.startsWith("validDetails.")) {
			String message = StringUtils.substringAfter(link, "validDetails.");
			if (message.equals("all")) {
				resultat = new ValidDetails(getOwner(), reports);
			} else {
				resultat = new ValidDetails(getOwner(), reports);
			}
		} else if (link.startsWith("brokenDetails.")) {
			String message = StringUtils.substringAfter(link, "brokenDetails.");
			if (message.equals("all")) {
				resultat = new BrokenDetails(getOwner(), reports);
			} else {
				resultat = new BrokenDetails(getOwner(), reports);
			}
		} else if (link.startsWith("newTestsDetails.")) {
			String message = StringUtils.substringAfter(link,
					"newTestsDetails.");
			if (message.equals("all")) {
				resultat = new NewTestsDetails(getOwner(), getTrendReport());
			} else {
				resultat = new NewTestsDetails(getOwner(), getTrendReport());
			}
		} else if (link.startsWith("deletedTestsDetails.")) {
			String message = StringUtils.substringAfter(link,
					"deletedTestsDetails.");
			if (message.equals("all")) {
				resultat = new DeletedTestsDetails(getOwner(), getTrendReport());
			} else {
				resultat = new DeletedTestsDetails(getOwner(), getTrendReport());
			}
		} else if (link.startsWith("statusChangedTestsDetails.")) {
			String message = StringUtils.substringAfter(link,
					"statusChangedTestsDetails.");
			if (message.equals("all")) {
				resultat = new ChangedStatusTestsDetails(getOwner(),
						getTrendReport());
			} else {
				resultat = new ChangedStatusTestsDetails(getOwner(),
						getTrendReport());
			}
		}
		 else if (link.startsWith("reportsDiff")) {			
			 if (request.getParameter("build1")!=null && request.getParameter("build2")!=null && request.getParameter("build3")!=null) {
				
				int nb_build1 = 0;
				int nb_build2 = 0;
				int nb_build3 = 0;
				AbstractBuild build1 = null;
				AbstractBuild build2 = null;
				AbstractBuild build3 = null;
				ReportContainer report1=null, report2=null, report3=null;
				if (!request.getParameter("build1").equals("none")) {
					nb_build1 = Integer.parseInt(request.getParameter("build1"));
					build1 = build.getProject().getBuildByNumber(nb_build1);
					PerfPublisherBuildAction ac = build1.getAction(PerfPublisherBuildAction.class);
					if (ac != null) {
						 report1 = ac.getReports();
					}
				}
				if (!request.getParameter("build2").equals("none")) {
					nb_build2 = Integer.parseInt(request.getParameter("build2"));
					build2 = build.getProject().getBuildByNumber(nb_build2);
					PerfPublisherBuildAction ac2 = build2.getAction(PerfPublisherBuildAction.class);
					if (ac2 != null) {
						report2 = ac2.getReports();
					}
				}
				if (!request.getParameter("build3").equals("none")) {
					nb_build3 = Integer.parseInt(request.getParameter("build3"));
					build3 = build.getProject().getBuildByNumber(nb_build3);
					PerfPublisherBuildAction ac3 = build3.getAction(PerfPublisherBuildAction.class);
					if (ac3 != null) {
						report3 = ac3.getReports();
					}
				}				
				resultat = new ReportsDiff(getOwner(), nb_build1,report1, nb_build2,report2, nb_build3, report3);
				return resultat;
			}
		}
		return resultat;
	}

	/**
	 * Returns the healthDescriptor.
	 * 
	 * @return the healthDescriptor
	 */
	public HealthDescriptor getHealthDescriptor() {
		return healthDescriptor;
	}

	/**
	 * Returns the associated health report builder.
	 * 
	 * @return the associated health report builder
	 */
	public final HealthReportBuilder getHealthReportBuilder() {
		return new HealthReportBuilder(getHealthDescriptor());
	}

	public HealthReport getBuildHealth() {
		return getHealthReportBuilder().computeHealth(healthDescriptor,
				getReports());
	}

	/**
	 * @return the reportsFiles
	 */
	public FileContainer getReportsFiles() {
		return reportsFiles;
	}

	/**
	 * @return the numberOfTest
	 */
	public int getNumberOfTest() {
		if (this.numberOfTest==-1 || this.numberOfTest==0) {
			this.numberOfTest = this.getReports().getNumberOfTest();
		} 
		return this.numberOfTest;
	}

	/**
	 * @return the numberOfExecutedTest
	 */
	public int getNumberOfExecutedTest() {
		if (this.numberOfExecutedTest==-1 || this.numberOfExecutedTest==0) {
			this.numberOfExecutedTest = this.getReports().getNumberOfExecutedTest();
		}
		return this.numberOfExecutedTest;
	}

	/**
	 * @return the percentOfExecutedTest
	 */
	public double getPercentOfExecutedTest() {
		if (this.percentOfExecutedTest == -1 || this.percentOfExecutedTest==0) {
			this.percentOfExecutedTest = this.getReports().getPercentOfExecutedTest();
		}
		return this.percentOfExecutedTest;
	}

	/**
	 * @return the numberOfNotExecutedTest
	 */
	public int getNumberOfNotExecutedTest() {
		if (this.numberOfNotExecutedTest==-1 || this.numberOfNotExecutedTest==0) {
			this.numberOfNotExecutedTest = this.getReports().getNumberOfNotExecutedTest();
		}
		return this.numberOfNotExecutedTest;
	}

	/**
	 * @return the percentOfNotExecutedTest
	 */
	public double getPercentOfNotExecutedTest() {
		if (this.percentOfNotExecutedTest==-1 || this.percentOfNotExecutedTest==0) {
			this.percentOfNotExecutedTest = this.getReports().getPercentOfNotExecutedTest();
		}
		return this.percentOfNotExecutedTest;
	}

	/**
	 * @return the numberOfPassedTest
	 */
	public int getNumberOfPassedTest() {
		if (this.numberOfPassedTest==-1 || this.numberOfPassedTest==0) {
			this.numberOfPassedTest = this.getReports().getNumberOfPassedTest();
		}
		return this.numberOfPassedTest;
	}

	/**
	 * @return the percentOfPassedTest
	 */
	public double getPercentOfPassedTest() {
		if (this.percentOfPassedTest==-1 || this.percentOfPassedTest==0) {
			this.percentOfPassedTest = this.getReports().getPercentOfPassedTest();
		}
		return this.percentOfPassedTest;
	}

	/**
	 * @return the numberOfFailedTest
	 */
	public int getNumberOfFailedTest() {
		if (this.numberOfFailedTest==-1 || this.numberOfFailedTest==0) {
			this.numberOfFailedTest = this.getReports().getNumberOfFailedTest();
		}
		return this.numberOfFailedTest;
	}

	/**
	 * @return the percentOfFailedTest
	 */
	public double getPercentOfFailedTest() {
		if (this.percentOfFailedTest==-1 || this.percentOfFailedTest==0) {
			this.percentOfFailedTest = this.getReports().getPercentOfFailedTest();
		}
		return this.percentOfFailedTest;
	}

	/**
	 * @return the numberOfCompileTimeTest
	 */
	public int getNumberOfCompileTimeTest() {
		if (this.numberOfCompileTimeTest==-1 || this.numberOfCompileTimeTest==0) {
			this.numberOfCompileTimeTest = this.getReports().getNumberOfCompileTimeTest();
		}
		return this.numberOfCompileTimeTest;
	}

	/**
	 * @return the averageOfCompileTime
	 */
	public double getAverageOfCompileTime() {
		if (this.averageOfCompileTime==-1 || this.averageOfCompileTime==0) {
			this.averageOfCompileTime = this.getReports().getAverageOfCompileTime();
		}
		return this.averageOfCompileTime;
	}

	/**
	 * @return the numberOfExecutionTimeTest
	 */
	public int getNumberOfExecutionTimeTest() {
		if (this.numberOfExecutionTimeTest==-1 || this.numberOfExecutionTimeTest==0) {
			this.numberOfExecutionTimeTest = this.getReports().getNumberOfExecutionTimeTest();
		}
		return this.numberOfExecutionTimeTest;
	}

	/**
	 * @return the averageOfExecutionTime
	 */
	public double getAverageOfExecutionTime() {
		if (this.averageOfExecutionTime==-1 || this.averageOfExecutionTime==0) {
			this.averageOfExecutionTime = this.getReports().getAverageOfExecutionTime();
		}
		return this.averageOfExecutionTime;
	}

	/**
	 * @return the numberOfPerformanceTest
	 */
	public int getNumberOfPerformanceTest() {
		if (this.numberOfPerformanceTest==-1 || this.numberOfPerformanceTest==0)  {
			this.numberOfPerformanceTest = this.getReports().getNumberOfPerformanceTest();
		}
		return this.numberOfPerformanceTest;
	}

	/**
	 * @return the averageOfPerformance
	 */
	public double getAverageOfPerformance() {
		if (this.averageOfPerformance==-1 || this.averageOfPerformance==0) {
			this.averageOfPerformance = this.getReports().getAverageOfPerformance();
		}
		return this.averageOfPerformance;
	}

	/**
	 * @return the numberOfNewTests
	 */
	public int getNumberOfNewTests() {
		if (this.numberOfNewTests==-1 || this.numberOfNewTests==0) {
			this.numberOfNewTests = this.getTrendReport().getNumberOfNewTests();
		}
		return this.numberOfNewTests;
	}

	/**
	 * @return the percentOfNewTests
	 */
	public double getPercentOfNewTests() {
		if (this.percentOfNewTests==-1 || this.percentOfNewTests==0) {
			this.percentOfNewTests = this.getTrendReport().getPercentOfNewTests();
		}
		return this.percentOfNewTests;
	}

	/**
	 * @return the numberOfDeletedTests
	 */
	public int getNumberOfDeletedTests() {
		if (this.numberOfDeletedTests==-1 || this.numberOfDeletedTests==0) {
			this.numberOfDeletedTests = this.getTrendReport().getNumberOfDeletedTests();
		}
		return this.numberOfDeletedTests;
	}

	/**
	 * @return the percentOfDeletedTests
	 */
	public double getPercentOfDeletedTests() {
		if (this.percentOfDeletedTests==-1 || this.percentOfDeletedTests==0) {
			this.percentOfDeletedTests = this.getTrendReport().getPercentOfDeletedTests();
		}
		return this.percentOfDeletedTests;
	}

	/**
	 * @return the numberOfStatusChangedTests
	 */
	public int getNumberOfSuccessStatusChangedTests() {
		if (this.numberOfSuccessStatusChangedTests  == -1 || this.numberOfSuccessStatusChangedTests == 0) {
			this.numberOfSuccessStatusChangedTests = this.getTrendReport().getNumberOfSuccessStatusChangedTests();
		}
		return this.numberOfSuccessStatusChangedTests;
	}
	public double getPercentOfSuccessStatusChangedTests() {
		if (this.percentOfSuccessStatusChangedTests == -1 || this.percentOfSuccessStatusChangedTests  == 0) {
			this.percentOfSuccessStatusChangedTests = this.getTrendReport().getPercentOfSuccessStatusChangedTests();
		}
		return this.percentOfSuccessStatusChangedTests;
	}
	
	public int getNumberOfExecutionStatusChangedTests() {
		if (this.numberOfExecutionStatusChangedTests  == -1 || this.numberOfExecutionStatusChangedTests == 0) {
			this.numberOfExecutionStatusChangedTests = this.getTrendReport().getNumberOfExecutionStatusChangedTests();
		}
		return this.numberOfExecutionStatusChangedTests;
	}
	public double getPercentOfExecutionStatusChangedTests() {
		if (this.percentOfExecutionStatusChangedTests == -1 || this.percentOfExecutionStatusChangedTests  == 0) {
			this.percentOfExecutionStatusChangedTests = this.getTrendReport().getPercentOfExecutionStatusChangedTests();
		}
		return this.percentOfExecutionStatusChangedTests ;
	}
	
	

}
