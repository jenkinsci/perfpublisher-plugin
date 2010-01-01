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
import hudson.plugins.PerfPublisher.Report.Report;
import hudson.plugins.PerfPublisher.Report.ReportContainer;
import hudson.plugins.PerfPublisher.Report.Test;
import hudson.util.ChartUtil;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;
import hudson.util.ShiftedCategoryAxis;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;

public class ChangedStatusTestsDetails implements ModelObject {

	private final TrendReport report;
	private final AbstractBuild<?, ?> _owner;

	public ChangedStatusTestsDetails(final AbstractBuild<?, ?> owner, TrendReport rep) {
		report = rep;
		this._owner = owner;
	}

	public AbstractBuild<?, ?> getOwner() {
		return _owner;
	}

	public String getDisplayName() {
		return "Details of status changed tests.";
	}

	public TrendReport getReport() {
		return report;
	}
	
	public String getPageContent() {
		StringBuilder stb = new StringBuilder();
		List<Test> tests = report.getExecutionStatusChangedTests();
		if (tests.size()>0) {
		
			stb.append("<table border=\"1px\" class=\"pane sortable\">");
	    	stb.append("<tr>");
	 		stb.append("<td class= \"pane-header\" title=\"Message\">Tests</td>");
	 		stb.append("<td class=\"pane-header\" title=\"Number of tests\">Execution status modification</td>");
	 		stb.append("</tr>");
	 		
	 		
	 		for (int i=0; i<tests.size(); i++) {
	 			Test t = tests.get(i);
	 			stb.append("<tr><td><a class=\"info_bulle\" href=\"../testDetails."+t.getNameForUrl()+"\">"+t.getName()+"</a></td>");
	 			if (t.isExecuted()){
	 				stb.append("<td>Not executed <img border=\"0\" src=\"/plugin/PerfPublisher/icons/bullet_go.png\" /> <b>Executed</b></td>");
	 			}else {
	 				stb.append("<td>Executed <img border=\"0\" src=\"/plugin/PerfPublisher/icons/bullet_go.png\" /> <b>Not executed</b></td>");
	 			}
	 			stb.append("</tr>");
	 		}
	     		
	 		                
	 		stb.append("</table>");
		} else {
			stb.append("<b>None of the tests has changed of execution status.</b><br />");
		}
 		
		tests = report.getSuccessStatusChangedTests();
		if (tests.size()>0) {
		
	 		stb.append("<table border=\"1px\" class=\"pane sortable\">");
	    	stb.append("<tr>");
	 		stb.append("<td class= \"pane-header\" title=\"Message\">Tests</td>");
	 		stb.append("<td class=\"pane-header\" title=\"Number of tests\">Success status modification</td>");
	 		stb.append("</tr>");
	 		
	 		
	 		for (int i=0; i<tests.size(); i++) {
	 			Test t = tests.get(i);
	 			stb.append("<tr><td><a class=\"info_bulle\" href=\"../testDetails."+t.getNameForUrl()+"\">"+t.getName()+"</a></td>");
	 			if (t.isSuccessfull()){
	 				stb.append("<td>Failed <img border=\"0\" src=\"/plugin/PerfPublisher/icons/bullet_go.png\" /> <b>Successfull</b></td>");
	 			}else {
	 				stb.append("<td>Successfull <img border=\"0\" src=\"/plugin/PerfPublisher/icons/bullet_go.png\" /> <b>Failed</b></td>");
	 			}
	 			stb.append("</tr>");
	 		}
	     		
	 		                
	 		stb.append("</table>");
		} else {
			stb.append("<b>None of the tests has changed of success status.</b><br />");
		}
		
		
		return stb.toString();
	}
	
	

}
