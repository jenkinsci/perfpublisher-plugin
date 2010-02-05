package hudson.plugins.PerfPublisher.projectsAction;

import hudson.model.AbstractBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Project;
import hudson.model.Result;
import hudson.plugins.PerfPublisher.AbstractPerfPublisherAction;
import hudson.plugins.PerfPublisher.PerfPublisherBuildAction;
import hudson.plugins.PerfPublisher.PerfPublisherPlugin;
import hudson.plugins.PerfPublisher.Report.ReportContainer;
import hudson.util.ChartUtil;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;
import hudson.util.ShiftedCategoryAxis;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.StrokeSample;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.io.IOException;
import java.util.Date;

/**
 * Action used for PerfPublisher report on project level.
 * 
 * @author Georges Bossert
 */
public class PerfPublisherFreestyleProjectAction extends AbstractPerfPublisherAction {

	private final Project project;

	public PerfPublisherFreestyleProjectAction(FreeStyleProject project) {
		this.project = project;
	}
	
	public String getDisplayName() {
		return PerfPublisherPlugin.GENERAL_DISPLAY_NAME;
	}

	public Project getProject() {
		return project;
	}

	public ReportContainer getReports() {
		Object ob = getProject().getLastSuccessfulBuild();
		AbstractBuild build = (AbstractBuild) ob;
		if (build != null) {
			PerfPublisherBuildAction ac = build
					.getAction(PerfPublisherBuildAction.class);
			if (ac != null) {
				return ac.getReports();
			}
		}
		return null;
	}

	public PerfPublisherBuildAction getActionByBuildNumber(int number) {
		return project.getBuildByNumber(number).getAction(
				PerfPublisherBuildAction.class);
	}

	
	
	public void doTestsGraph(StaplerRequest request, StaplerResponse response)
			throws IOException {
		ChartUtil
				.generateGraph(request, response, createTestsGraph(), 800, 250);
	}
    public final void doMiniTestsGraphMap(final StaplerRequest request, final StaplerResponse response) throws IOException {
        ChartUtil.generateClickableMap(request, response, createTestsGraph(), 800, 250);
    }
	
	public void doMiniTestsGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {

		ChartUtil
				.generateGraph(request, response, createTestsGraph(), 350, 200);
	}

	public void doCompileTimeGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		ChartUtil.generateGraph(request, response, createCompileTimeGraph(),
				800, 250);
	}

	public void doMiniCompileTimeGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		ChartUtil.generateGraph(request, response, createCompileTimeGraph(),
				350, 200);
	}

	public void doSuccessGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		ChartUtil.generateGraph(request, response, createSuccessGraph(),
				800, 250);
	}

	public void doMiniSuccessGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		ChartUtil.generateGraph(request, response, createSuccessGraph(),
				350, 200);
	}
	public void doExecutionTimeGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		ChartUtil.generateGraph(request, response, createExecutionTimeGraph(),
				800, 250);
	}

	public void doMiniExecutionTimeGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		ChartUtil.generateGraph(request, response, createExecutionTimeGraph(),
				350, 200);
	}

	public void doPerformanceGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		ChartUtil.generateGraph(request, response, createPerformanceGraph(),
				800, 250);
	}

	public void doMiniPerformanceGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		ChartUtil.generateGraph(request, response, createPerformanceGraph(),
				350, 200);
	}

	public void doStdDevGraph(StaplerRequest request, StaplerResponse response)
			throws IOException {

		if (shouldReloadGraph(request, response)) {
			ChartUtil.generateGraph(request, response, createStdDevGraph(),
					800, 150);
		}
	}

	public void doMeanRespLengthGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {

		if (shouldReloadGraph(request, response)) {
			ChartUtil.generateGraph(request, response,
					createMeanRespLengthGraph(), 800, 150);
		}
	}
	
	
	
	private JFreeChart createSuccessGraph() {

		DataSetBuilder<String, NumberOnlyBuildLabel> builder = new DataSetBuilder<String, NumberOnlyBuildLabel>();

		for (Object build : project.getBuilds()) {
			AbstractBuild abstractBuild = (AbstractBuild) build;
			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(
							Result.UNSTABLE)) {
				PerfPublisherBuildAction action = abstractBuild
						.getAction(PerfPublisherBuildAction.class);
				if (action!=null && action.getReports() != null) {
					builder.add(action.getReports()
							.getNumberOfFailedTest(), "Failed test",
							new NumberOnlyBuildLabel(abstractBuild));
					builder.add(action.getReports().getNumberOfPassedTest(),
							"Passed test", new NumberOnlyBuildLabel(
									abstractBuild));
					builder.add(action.getReports().getNumberOfNotExecutedTest(),
							"Broken test", new NumberOnlyBuildLabel(
									abstractBuild));
					
				}
			}
		}

		JFreeChart chart = ChartFactory.createStackedAreaChart(
				"Evolution of tests success", "Build", "Number of test",
				builder.build(), PlotOrientation.VERTICAL, true, true, false);

		chart.setBackgroundPaint(Color.WHITE);

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(null);
		plot.setForegroundAlpha(0.8f);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.black);

		CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
		plot.setDomainAxis(domainAxis);
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
		domainAxis.setCategoryMargin(0.0);

		CategoryItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(2, ColorPalette.BLUE);
		renderer.setSeriesPaint(1, ColorPalette.RED);
		renderer.setSeriesPaint(0, ColorPalette.YELLOW);

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// crop extra space around the graph
		plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

		return chart;
	}

	private JFreeChart createPerformanceGraph() {
		DataSetBuilder<String, NumberOnlyBuildLabel> builder = new DataSetBuilder<String, NumberOnlyBuildLabel>();
		for (Object build : project.getBuilds()) {
			AbstractBuild abstractBuild = (AbstractBuild) build;
			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(
							Result.UNSTABLE)) {
				PerfPublisherBuildAction action = abstractBuild
						.getAction(PerfPublisherBuildAction.class);
				if (action!=null && action.getReports() != null) {
					builder.add(action.getReports().getWorstPerformanceTestValue(),
							"Worst Performance", new NumberOnlyBuildLabel(
									abstractBuild));
					builder.add(action.getReports().getAverageOfPerformance(),
							"Average Performance", new NumberOnlyBuildLabel(
									abstractBuild));
					builder.add(action.getReports().getBestPerformanceTestValue(), "Best Performance",
							new NumberOnlyBuildLabel(abstractBuild));
				}
			}
		}

		JFreeChart chart = ChartFactory.createLineChart3D(
				"Evolution of Performances", "Build", "GFlops",
				builder.build(), PlotOrientation.VERTICAL, true, true, false);

		chart.setBackgroundPaint(Color.WHITE);

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(null);
		plot.setForegroundAlpha(0.4f);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.black);

		CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
		plot.setDomainAxis(domainAxis);
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
		domainAxis.setCategoryMargin(0.0);

		CategoryItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(2, ColorPalette.RED);
		renderer.setSeriesPaint(1, ColorPalette.BLUE);
		renderer.setSeriesPaint(0, ColorPalette.GREY);
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// crop extra space around the graph
		plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

		return chart;
	}

	private JFreeChart createExecutionTimeGraph() {
		DataSetBuilder<String, NumberOnlyBuildLabel> builder = new DataSetBuilder<String, NumberOnlyBuildLabel>();

		for (Object build : project.getBuilds()) {
			AbstractBuild abstractBuild = (AbstractBuild) build;
			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(
							Result.UNSTABLE)) {
				PerfPublisherBuildAction action = abstractBuild
						.getAction(PerfPublisherBuildAction.class);
				if (action!=null && action.getReports() != null) {
					builder.add(action.getReports()
							.getWorstExecutionTimeTestValue(),
							"Worst Execution Time", new NumberOnlyBuildLabel(
									abstractBuild));
					builder.add(
							action.getReports().getAverageOfExecutionTime(),
							"Average Execution Time", new NumberOnlyBuildLabel(
									abstractBuild));
					builder.add(action.getReports()
							.getBestExecutionTimeTestValue(),
							"Best Execution Time", new NumberOnlyBuildLabel(
									abstractBuild));
				}

			}
		}

		JFreeChart chart = ChartFactory.createLineChart3D(
				"Evolution of Execution Time", "Build", "Execution time",
				builder.build(), PlotOrientation.VERTICAL, true, true, false);

		chart.setBackgroundPaint(Color.WHITE);

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(null);
		plot.setForegroundAlpha(0.4f);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.black);

		CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
		plot.setDomainAxis(domainAxis);
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
		domainAxis.setCategoryMargin(0.0);

		CategoryItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(2, ColorPalette.RED);
		renderer.setSeriesPaint(1, ColorPalette.BLUE);
		renderer.setSeriesPaint(0, ColorPalette.GREY);
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// crop extra space around the graph
		plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

		return chart;
	}

	private JFreeChart createCompileTimeGraph() {
		DataSetBuilder<String, NumberOnlyBuildLabel> builder = new DataSetBuilder<String, NumberOnlyBuildLabel>();

		for (Object build : project.getBuilds()) {
			AbstractBuild abstractBuild = (AbstractBuild) build;
			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(
							Result.UNSTABLE)) {
				PerfPublisherBuildAction action = abstractBuild
						.getAction(PerfPublisherBuildAction.class);
				if (action!=null && action.getReports() != null) {
					builder.add(action.getReports()
							.getWorstCompileTimeTestValue(),
							"Worst Compile Time", new NumberOnlyBuildLabel(
									abstractBuild));
					builder.add(action.getReports().getAverageOfCompileTime(),
							"Average Compile Time", new NumberOnlyBuildLabel(
									abstractBuild));
					builder.add(action.getReports()
							.getBestCompileTimeTestValue(),
							"Best Compile Time", new NumberOnlyBuildLabel(
									abstractBuild));
				}

			}
		}

		JFreeChart chart = ChartFactory.createLineChart3D(
				"Evolution of Compile Time", "Build", "Compile time", builder
						.build(), PlotOrientation.VERTICAL, true, true, false);

		chart.setBackgroundPaint(Color.WHITE);

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(null);
		plot.setForegroundAlpha(0.4f);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.black);

		CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
		plot.setDomainAxis(domainAxis);
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
		domainAxis.setCategoryMargin(0.0);

		CategoryItemRenderer renderer = plot.getRenderer();		
		renderer.setSeriesPaint(2, ColorPalette.RED);
		renderer.setSeriesPaint(1, ColorPalette.BLUE);
		renderer.setSeriesPaint(0, ColorPalette.GREY);
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// crop extra space around the graph
		plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

		return chart;
	}

	private JFreeChart createTestsGraph() {

		DataSetBuilder<String, NumberOnlyBuildLabel> builder = new DataSetBuilder<String, NumberOnlyBuildLabel>();

		for (Object build : project.getBuilds()) {
			AbstractBuild abstractBuild = (AbstractBuild) build;
			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(
							Result.UNSTABLE)) {
				PerfPublisherBuildAction action = abstractBuild
						.getAction(PerfPublisherBuildAction.class);
				if (action!=null && action.getReports() != null) {
					builder.add(action.getReports()
							.getNumberOfNotExecutedTest(), "Not executed test",
							new NumberOnlyBuildLabel(abstractBuild));
					builder.add(action.getReports().getNumberOfExecutedTest(),
							"Executed test", new NumberOnlyBuildLabel(
									abstractBuild));
					
				}
			}
		}

		JFreeChart chart = ChartFactory.createStackedAreaChart(
				"Evolution of tests executions", "Build", "Number of test",
				builder.build(), PlotOrientation.VERTICAL, true, true, false);

		chart.setBackgroundPaint(Color.WHITE);

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(null);
		plot.setForegroundAlpha(0.8f);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.black);

		CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
		plot.setDomainAxis(domainAxis);
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
		domainAxis.setCategoryMargin(0.0);

		CategoryItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(1, ColorPalette.RED);
		renderer.setSeriesPaint(0, ColorPalette.BLUE);

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// crop extra space around the graph
		plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

		return chart;
	}

	private JFreeChart createStdDevGraph() {
		return createNumberBuildGraph("Standart time deviation", "Time (ms)");
	}

	private JFreeChart createMeanRespLengthGraph() {
		return createNumberBuildGraph("Mean respond time", "Length (bytes)");
	}

	private JFreeChart createNumberBuildGraph(String valueName, String unitName) {
		DataSetBuilder<String, NumberOnlyBuildLabel> builder = new DataSetBuilder<String, NumberOnlyBuildLabel>();

		for (Object build : project.getBuilds()) {
			AbstractBuild abstractBuild = (AbstractBuild) build;
			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(
							Result.UNSTABLE)) {
				PerfPublisherBuildAction action = abstractBuild
						.getAction(PerfPublisherBuildAction.class);
				builder.add(1, valueName, new NumberOnlyBuildLabel(
						abstractBuild));
			}
		}

		JFreeChart chart = ChartFactory.createStackedAreaChart(valueName
				+ " Trend", "Build", unitName, builder.build(),
				PlotOrientation.VERTICAL, false, false, false);

		chart.setBackgroundPaint(Color.WHITE);

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(null);
		plot.setForegroundAlpha(0.8f);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.black);

		CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
		plot.setDomainAxis(domainAxis);
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
		domainAxis.setCategoryMargin(0.0);

		CategoryItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(2, ColorPalette.RED);
		renderer.setSeriesPaint(1, ColorPalette.YELLOW);
		renderer.setSeriesPaint(0, ColorPalette.BLUE);

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// crop extra space around the graph
		plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

		return chart;
	}

	private boolean shouldReloadGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		return shouldReloadGraph(request, response, project
				.getLastSuccessfulBuild());
	}
}
