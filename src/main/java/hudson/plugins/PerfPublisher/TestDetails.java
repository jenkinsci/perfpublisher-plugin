package hudson.plugins.PerfPublisher;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

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

import hudson.model.ModelObject;
import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.PerfPublisher.Report.Test;
import hudson.util.ChartUtil;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;
import hudson.util.ShiftedCategoryAxis;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;

public class TestDetails implements ModelObject {

	private final Test test;
	private final AbstractBuild<?, ?> _owner;

	public TestDetails(final AbstractBuild<?, ?> owner, Test test) {

		this.test = test;
		this._owner = owner;
	}

	public AbstractBuild<?, ?> getOwner() {
		return _owner;
	}

	public String getDisplayName() {
		return "Details of test " + test.getName();
	}

	public Test getTest() {
		return test;
	}

	public String getSuccessGraph() {
		String result = "";
		Object ob_builds = (Object)_owner.getProject().getBuilds();		
		List<Object> builds = (List<Object>) ob_builds;
		
		
		
		float taille_case = 100.0f/Math.min(builds.size(), 25);
		int total = 0;
		int indice_build = 0;
		
		for (int j=0; j<Math.min(builds.size(), 25); j++) {
			Object build = builds.get(j);
			String numberBuild = "#";
			String color = "white";
			AbstractBuild abstractBuild = (AbstractBuild) build;
						
			if (!abstractBuild.isBuilding()	&& abstractBuild.getResult().isBetterOrEqualTo(Result.SUCCESS)) {				
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
			
			numberBuild = "#"+abstractBuild.getNumber();
			total +=taille_case;
			
			if (indice_build==Math.min(builds.size(), 25)-1 && total<100) {
				taille_case +=100-total;
			}
			result+="<div id=\""+color+"\" style=\"width:"+taille_case+"%;\">"+numberBuild+"</div>";
			
		}
		return result;
	}
	
	public void doPerformanceGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		ChartUtil.generateGraph(request, response, createPerformanceGraph(),
				800, 250);
		
	}

	private JFreeChart createPerformanceGraph() {
		DataSetBuilder<String, NumberOnlyBuildLabel> builder = new DataSetBuilder<String, NumberOnlyBuildLabel>();

		for (Object build : _owner.getProject().getBuilds()) {
			AbstractBuild abstractBuild = (AbstractBuild) build;
			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(
							Result.SUCCESS)) {
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
	
	
	public void doExecutionTimeGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		ChartUtil.generateGraph(request, response, createExecutionTimeGraph(),
				800, 250);
	}

	private JFreeChart createExecutionTimeGraph() {
		DataSetBuilder<String, NumberOnlyBuildLabel> builder = new DataSetBuilder<String, NumberOnlyBuildLabel>();

		for (Object build : _owner.getProject().getBuilds()) {
			AbstractBuild abstractBuild = (AbstractBuild) build;
			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(
							Result.SUCCESS)) {
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
		DataSetBuilder<String, NumberOnlyBuildLabel> builder = new DataSetBuilder<String, NumberOnlyBuildLabel>();

		for (Object build : _owner.getProject().getBuilds()) {
			AbstractBuild abstractBuild = (AbstractBuild) build;
			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(
							Result.SUCCESS)) {
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

}
