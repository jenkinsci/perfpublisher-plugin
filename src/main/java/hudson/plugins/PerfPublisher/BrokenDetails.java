package hudson.plugins.PerfPublisher;

import java.awt.Color;
import java.io.IOException;

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
import hudson.plugins.PerfPublisher.Report.Report;
import hudson.plugins.PerfPublisher.Report.ReportContainer;
import hudson.plugins.PerfPublisher.Report.Test;
import hudson.util.ChartUtil;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;
import hudson.util.ShiftedCategoryAxis;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;

public class BrokenDetails implements ModelObject {

	private final ReportContainer report;
	private final AbstractBuild<?, ?> _owner;

	public BrokenDetails(final AbstractBuild<?, ?> owner, ReportContainer rep) {
		report = rep;
		this._owner = owner;
	}

	public AbstractBuild<?, ?> getOwner() {
		return _owner;
	}

	public String getDisplayName() {
		return "Details of broken tests messages.";
	}

	public ReportContainer getReport() {
		return report;
	}
	

}
