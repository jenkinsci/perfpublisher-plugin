package hudson.plugins.PerfPublisher;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;

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
import hudson.model.Run;
import hudson.model.Result;
import hudson.plugins.PerfPublisher.Report.Report;
import hudson.plugins.PerfPublisher.Report.ReportContainer;
import hudson.plugins.PerfPublisher.Report.Test;
import hudson.util.ChartUtil;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;
import hudson.util.ShiftedCategoryAxis;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;

public class DeletedTestsDetails implements ModelObject {

	private final TrendReport report;
	private final Run<?, ?> _owner;
	private final Map<String, String> metrics;

	public DeletedTestsDetails(final Run<?, ?> owner, TrendReport rep, Map<String, String> metrics) {
		report = rep;
		this._owner = owner;
		this.metrics = metrics;
	}
	
	public Map<String, String> getMetrics() {
		return metrics;
	}

	public Run<?, ?> getOwner() {
		return _owner;
	}

	public String getDisplayName() {
		return "Details of deleted tests.";
	}

	public TrendReport getReport() {
		return report;
	}

}
