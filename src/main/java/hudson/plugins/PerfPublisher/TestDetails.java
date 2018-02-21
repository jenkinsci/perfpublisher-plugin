package hudson.plugins.PerfPublisher;

import hudson.model.ModelObject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.plugins.PerfPublisher.Report.Metric;
import hudson.plugins.PerfPublisher.Report.Test;
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
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDetails implements ModelObject {

	private final Test test;
	private final Run<?, ?> _owner;
	private final Map<String, String> metrics;

	public TestDetails(final Run<?, ?> owner, Test test, Map<String, String> metrics) {

		this.test = test;
		this._owner = owner;
		this.metrics = metrics;
	}

	public Run<?, ?> getOwner() {
		return _owner;
	}

	public String getDisplayName() {
		return "Details of test " + test.getName();
	}
	
	public Map<String, String> getMetrics() {
	  return metrics;
	}
	
	public Map<String, String> getMetricsReversed() {
	  Map<String, String> returnMap = new HashMap<String, String>();
	  for (String key : metrics.keySet()) {
	    returnMap.put(metrics.get(key), key);
	  }
	  return returnMap;
	}

	public Test getTest() {
		return test;
	}

	public List<SuccessGraphBuild> getSuccessGraph() {
		Object ob_builds = _owner.getParent().getBuilds();
		List<Object> builds = (List<Object>) ob_builds;

		int buildsCount = Math.min(builds.size(), 25);
		List<SuccessGraphBuild> result = new ArrayList<SuccessGraphBuild>(buildsCount);

		float taille_case = 100.0f/ buildsCount;
		int total = 0;
		int indice_build = 0;
		
		for (int j = 0; j< buildsCount; j++) {
			Object build = builds.get(j);
			String color = "white";
			Run<?,?> abstractBuild = (Run<?,?>) build;
						
			if (!abstractBuild.isBuilding()	&& abstractBuild.getResult().isBetterOrEqualTo(Result.FAILURE)) {
				PerfPublisherBuildAction action = abstractBuild.getAction(PerfPublisherBuildAction.class);
				
				if (action!=null && action.getReport() != null) {
					Test prev_test = action.getReports().getTestWithName(this.test.getName());
					if (prev_test!=null) {
						
						if (!prev_test.isExecuted()) {
							color="orange";
						} else {
							if (prev_test.isSuccessfull() && prev_test.isExecuted()) {
								color="blue";
							} else {
								color="red";
							}
						}						
					} else {
						color = "grey";
					}
				}
			}
			
			total +=taille_case;
			
			if (indice_build== buildsCount-1 && total<100) {
				taille_case +=100-total;
			}
			result.add(new SuccessGraphBuild(color, taille_case, abstractBuild.getNumber()));

		}
		return result;
	}
	
	public void doPerformanceGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		ChartUtil.generateGraph(request, response, createPerformanceGraph(),
				800, 250);
		
	}

	private JFreeChart createPerformanceGraph() {
		DataSetBuilder<String, NumberOnlyBuildLabel> builder = new DataSetBuilder<>();

		for (Object build : _owner.getParent().getBuilds()) {
			Run<?,?> abstractBuild = (Run<?,?>) build;
			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(Result.UNSTABLE)) {
				PerfPublisherBuildAction action = abstractBuild
						.getAction(PerfPublisherBuildAction.class);
				if (action!=null && action.getReport() != null) {
					for (int i = 0; i < action.getReports().getNumberOfTest(); i++) {
						if (action.getReports().getTests().get(i).getName()
								.equals(test.getName()) && action.getReports().getTests().get(i).isPerformance()) {
							builder.add(action.getReports().getTests().get(i)
									.getPerformance().getMeasure(),
									"Performance", new NumberOnlyBuildLabel(
											abstractBuild));
						}
					}
				}
			}
		}
		JFreeChart chart = ChartFactory.createLineChart3D(
				"Evolution of Performance", "Build", "Performance", builder
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
		renderer.setSeriesPaint(0, ColorPalette.BLUE);
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// crop extra space around the graph
		plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

		return chart;
	}
	
	public void doMetrics(StaplerRequest request,
			StaplerResponse response) throws IOException {
        ChartUtil.generateGraph(request, response, createMetricGraph(request.getRestOfPath().substring(1)), 800, 250);
	}
	
	private JFreeChart createMetricGraph(String metric) {
		DataSetBuilder<String, NumberOnlyBuildLabel> builder = new DataSetBuilder<>();
		String unit = null;
		for (Object build : _owner.getParent().getBuilds()) {
			Run<?,?> abstractBuild = (Run<?,?>) build;
			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(Result.UNSTABLE)) {
				PerfPublisherBuildAction action = abstractBuild
						.getAction(PerfPublisherBuildAction.class);
				if (action!=null && action.getReports() != null) {
					for (int i = 0; i < action.getReports().getNumberOfTest(); i++) {
						if (action.getReports().getTests().get(i).getName()
								.equals(test.getName()) && action.getReports().getTests().get(i).getMetrics().containsKey(metric)) {
							Object metricByName = action.getReports().getTests().get(i).getMetrics().get(metric); // Object to avoid CCE for old persisted data
							if (metricByName instanceof Metric)
								unit = ((Metric) metricByName).getUnit();
							double measure = metricByName instanceof Metric ? ((Metric)metricByName).getMeasure() : ((Number)metricByName).doubleValue();
							builder.add(measure,
									getMetricsReversed().get(metric) , new NumberOnlyBuildLabel(
											abstractBuild));
						}
					}
				}
			}
		}
    
		JFreeChart chart = ChartFactory.createLineChart3D(
				getMetricsReversed().get(metric), "Build", unit == null || unit.isEmpty() ? "unit" : unit,
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
		renderer.setSeriesPaint(0, ColorPalette.BLUE);
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// crop extra space around the graph
		plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

		return chart;
	}

  public void doExecutionTimeGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		ChartUtil.generateGraph(request, response, createExecutionTimeGraph(),
				800, 250);
	}

	private JFreeChart createExecutionTimeGraph() {
		DataSetBuilder<String, NumberOnlyBuildLabel> builder = new DataSetBuilder<>();

		for (Object build : _owner.getParent().getBuilds()) {
			Run<?,?> abstractBuild = (Run<?,?>) build;
			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(Result.UNSTABLE)) {
				PerfPublisherBuildAction action = abstractBuild
						.getAction(PerfPublisherBuildAction.class);
				if (action!=null && action.getReport() != null) {
					for (int i = 0; i < action.getReports().getNumberOfTest(); i++) {
						if (action.getReports().getTests().get(i).getName()
								.equals(test.getName()) && action.getReports().getTests().get(i).isExecutionTime()) {
							builder.add(action.getReports().getTests().get(i)
									.getExecutionTime().getMeasure(),
									"Execution Time", new NumberOnlyBuildLabel(
											abstractBuild));
						}
					}
				}
			}
		}
		JFreeChart chart = ChartFactory.createLineChart3D(
				"Evolution of Execution Time", "Build", "Execution time", builder
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
		renderer.setSeriesPaint(0, ColorPalette.BLUE);
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// crop extra space around the graph
		plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

		return chart;
	}
	
	
	public void doCompileTimeGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		ChartUtil.generateGraph(request, response, createCompileTimeGraph(),
				800, 250);
	}

	private JFreeChart createCompileTimeGraph() {
		DataSetBuilder<String, NumberOnlyBuildLabel> builder = new DataSetBuilder<>();

		for (Object build : _owner.getParent().getBuilds()) {
			Run<?,?> abstractBuild = (Run<?,?>) build;
			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(Result.FAILURE)) {
				PerfPublisherBuildAction action = abstractBuild
						.getAction(PerfPublisherBuildAction.class);
				if (action!=null && action.getReport() != null) {
					for (int i = 0; i < action.getReports().getNumberOfTest(); i++) {
						if (action.getReports().getTests().get(i).getName()
								.equals(test.getName()) && action.getReports().getTests().get(i).isCompileTime()) {
							builder.add(action.getReports().getTests().get(i)
									.getCompileTime().getMeasure(),
									"Compile Time", new NumberOnlyBuildLabel(
											abstractBuild));
						}
					}
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
		renderer.setSeriesPaint(0, ColorPalette.BLUE);
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// crop extra space around the graph
		plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

		return chart;
	}

	public static class SuccessGraphBuild {
		private String color;
		private float width;
		private int number;

		public SuccessGraphBuild(String color, float width, int number) {
			this.color = color;
			this.width = width;
			this.number = number;
		}

		public String getColor() {
			return color;
		}

		public float getWidth() {
			return width;
		}

		public int getNumber() {
			return number;
		}
	}
}
