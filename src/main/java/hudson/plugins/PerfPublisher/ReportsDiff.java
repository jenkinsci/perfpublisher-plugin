package hudson.plugins.PerfPublisher;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

public class ReportsDiff implements ModelObject {

	private final AbstractBuild<?, ?> _owner;

	private ReportContainer report1;
	private ReportContainer report2;
	private ReportContainer report3;
	
	private int nb_build1;
	private int nb_build2;
	private int nb_build3;
	
	public ReportsDiff(final AbstractBuild<?, ?> owner, int nb_build1, ReportContainer report1, int nb_build2, ReportContainer report2, int nb_build3, ReportContainer report3) {
		this._owner = owner;
		this.report1 = report1;
		this.report2 = report2;
		this.report3 = report3;
		
		this.nb_build1 = nb_build1;
		this.nb_build2 = nb_build2;
		this.nb_build3 = nb_build3;
		
	}
	public String getBuild1Number() {
		return ""+nb_build1;
	}
	public String getBuild2Number() {
		return ""+nb_build2;
	}
	public String getBuild3Number() {
		return ""+nb_build3;
	}

	public ReportContainer getReport1() {
		return this.report1;
	}
	public List<Test> getReport1Tests() {
		return this.report1.getTests();
	}
	public ReportContainer getReport2() {
		return this.report2;
	}
	public List<Test> getReport2Tests() {
		return this.report2.getTests();
	}
	public ReportContainer getReport3() {
		return this.report3;
	}
	public List<Test> getReport3Tests() {
		return this.report3.getTests();
	}
	
	public String getHtmlTestsDiff() {
		String style="Threecolumn";
		if (nb_build3==0) {
			style="Twocolumn";
		}
		
		StringBuilder strb = new StringBuilder();
		strb.append("<div class=\"line\">");
		strb.append("<div class=\"header\">Build number</div>");
		strb.append("<div class=\""+style+"\"><b>Build "+this.nb_build1+"</b></div>");
		strb.append("<div class=\""+style+"\"><b>Build "+this.nb_build2+"</b></div>");
		if (nb_build3!=0) strb.append("<div class=\""+style+"\"><b>Build "+this.nb_build3+"</b></div>");
		strb.append("</div>");
		
		
		
		strb.append("<div class=\"line\">");
		strb.append("<div class=\"header\">Build date</div>");
		strb.append("<div class=\""+style+"\">"+this._owner.getProject().getBuildByNumber(nb_build1).getTimestampString2()+"</div>");
		strb.append("<div class=\""+style+"\">"+this._owner.getProject().getBuildByNumber(nb_build2).getTimestampString2()+"</div>");
		if (nb_build3!=0) strb.append("<div class=\""+style+"\">"+this._owner.getProject().getBuildByNumber(nb_build3).getTimestampString2()+"</div>");
		strb.append("</div>");
		
		strb.append("<div class=\"line\">");
		strb.append("<div class=\"header\">Number of executed tests</div>");
		strb.append("<div class=\""+style+"\">"+this.report1.getNumberOfExecutedTest()+"</div>");
		strb.append("<div class=\""+style+"\">"+this.report2.getNumberOfExecutedTest()+"</div>");
		if (nb_build3!=0) strb.append("<div class=\""+style+"\">"+this.report3.getNumberOfExecutedTest()+"</div>");
		strb.append("</div>");
		
		strb.append("<div class=\"line\">");
		strb.append("<div class=\"header\">Number of not executed tests</div>");
		strb.append("<div class=\""+style+"\">"+this.report1.getNumberOfNotExecutedTest()+"</div>");
		strb.append("<div class=\""+style+"\">"+this.report2.getNumberOfNotExecutedTest()+"</div>");
		if (nb_build3!=0) strb.append("<div class=\""+style+"\">"+this.report3.getNumberOfNotExecutedTest()+"</div>");
		strb.append("</div>");
		
		strb.append("<div class=\"line\">");
		strb.append("<div class=\"header\">Number of succeeded tests</div>");
		strb.append("<div class=\""+style+"\">"+this.report1.getNumberOfPassedTest()+"</div>");
		strb.append("<div class=\""+style+"\">"+this.report2.getNumberOfPassedTest()+"</div>");
		if (nb_build3!=0) strb.append("<div class=\""+style+"\">"+this.report3.getNumberOfPassedTest()+"</div>");
		strb.append("</div>");
		
		strb.append("<div class=\"line\">");
		strb.append("<div class=\"header\">Number of failed tests</div>");
		strb.append("<div class=\""+style+"\">"+this.report1.getNumberOfFailedTest()+"</div>");
		strb.append("<div class=\""+style+"\">"+this.report2.getNumberOfFailedTest()+"</div>");
		if (nb_build3!=0) strb.append("<div class=\""+style+"\">"+this.report3.getNumberOfFailedTest()+"</div>");
		strb.append("</div>");
		
		strb.append("<div class=\"line\">");
		strb.append("<div class=\"header\">Compile time (Average/Number of measures)</div>");
		strb.append("<div class=\""+style+"\">"+this.report1.getAverageOfCompileTime()+"/"+this.report1.getNumberOfCompileTimeTest()+"</div>");
		strb.append("<div class=\""+style+"\">"+this.report2.getAverageOfCompileTime()+"/"+this.report2.getNumberOfCompileTimeTest()+"</div>");
		if (nb_build3!=0) strb.append("<div class=\""+style+"\">"+this.report3.getAverageOfCompileTime()+"/"+this.report3.getNumberOfCompileTimeTest()+"</div>");
		strb.append("</div>");
		
		strb.append("<div class=\"line\">");
		strb.append("<div class=\"header\">Execution time (Average/Number of measures)</div>");
		strb.append("<div class=\""+style+"\">"+this.report1.getAverageOfExecutionTime()+"/"+this.report1.getNumberOfExecutionTimeTest()+"</div>");
		strb.append("<div class=\""+style+"\">"+this.report2.getAverageOfExecutionTime()+"/"+this.report2.getNumberOfExecutionTimeTest()+"</div>");
		if (nb_build3!=0) strb.append("<div class=\""+style+"\">"+this.report3.getAverageOfExecutionTime()+"/"+this.report3.getNumberOfExecutionTimeTest()+"</div>");
		strb.append("</div>");
		
		strb.append("<div class=\"line\">");
		strb.append("<div class=\"header\">Performance (Average/Number of measures)</div>");
		strb.append("<div class=\""+style+"\">"+this.report1.getAverageOfPerformance()+"/"+this.report1.getNumberOfPerformanceTest()+"</div>");
		strb.append("<div class=\""+style+"\">"+this.report2.getAverageOfPerformance()+"/"+this.report2.getNumberOfPerformanceTest()+"</div>");
		if (nb_build3!=0) strb.append("<div class=\""+style+"\">"+this.report3.getAverageOfPerformance()+"/"+this.report3.getNumberOfPerformanceTest()+"</div>");
		strb.append("</div>");
		
		strb.append("<div class=\"line\">");
		strb.append("<div class=\"header\">Performance (Average/Number of measures)</div>");
		strb.append("<div class=\""+style+"\">"+this.report1.getAverageOfPerformance()+"/"+this.report1.getNumberOfPerformanceTest()+"</div>");
		strb.append("<div class=\""+style+"\">"+this.report2.getAverageOfPerformance()+"/"+this.report2.getNumberOfPerformanceTest()+"</div>");
		if (nb_build3!=0) strb.append("<div class=\""+style+"\">"+this.report3.getAverageOfPerformance()+"/"+this.report3.getNumberOfPerformanceTest()+"</div>");
		strb.append("<br style=\"clear:both;\"></div>&nbsp;");
		
		strb.append("<div class=\"legende\">");
		strb.append("<div id=\"grey\">NOT EXECUTED</div>");
		strb.append("<div id=\"yellow\">TIMED OUT</div>");
		strb.append("<div id=\"red\">FAILED</div>");
		strb.append("<div id=\"green\">SUCCESSFULL</div>");
		strb.append("<div id=\"white\">DOESN'T EXIST</div>");
		strb.append("</div>");
		
		
		/**
		 * Get all the tests executed in the three builds
		 */
		ArrayList<Test> global_test = new ArrayList<Test>();
		global_test.addAll(this.report1.getTests());
		
		for (int i=0; i<this.report2.getTests().size(); i++) {
			boolean insert = true;
			for (int j=0; j<global_test.size(); j++) {
				if (global_test.get(j).getName().equals(this.report2.getTests().get(i).getName())) {
					insert = false;
				}
			}
			if (insert) {
				global_test.add(this.report2.getTests().get(i));
			}
		}
		if (this.report3!=null) {
			for (int i=0; i<this.report3.getTests().size(); i++) {
				boolean insert = true;
				for (int j=0; j<global_test.size(); j++) {
					if (global_test.get(j).getName().equals(this.report3.getTests().get(i).getName())) {
						insert = false;
					}
				}
				if (insert) {
					global_test.add(this.report3.getTests().get(i));
				}
			}
		}
		Collections.sort(global_test);
		for (int i=0; i<global_test.size(); i++) {
			strb.append("<div class=\"line\">");
			strb.append("<div class=\"header\">"+global_test.get(i).getName()+"</div>");
			
			String color1="#fff";
			String color2="#fff";
			String color3="#fff";
			String txt1="-";
			String txt2="-";
			String txt3="-";
			
			if (this.report1.getTestWithName(global_test.get(i).getName()) != null) {
				txt1="x";
				Test test1 = this.report1.getTestWithName(global_test.get(i).getName());
				if (!test1.isExecuted()) {
					color1="grey";
				} else {
					if (test1.isHasTimedOut()) {
						color1 = "yellow";
					} else {
						if (test1.isSuccessfull()) {
							color1="green";
						} else { color1="red"; }
					}
				}
			}
			strb.append("<div class=\""+style+"\" style=\"background-color:"+color1+"\"> "+txt1+" </div>");
			
			if (this.report2.getTestWithName(global_test.get(i).getName()) != null) {
				txt2="x";
				Test test2 = this.report2.getTestWithName(global_test.get(i).getName());
				if (!test2.isExecuted()) {
					color2="grey";
				} else {
					if (test2.isHasTimedOut()) {
						color2 = "yellow";
					} else {
						if (test2.isSuccessfull()) {
							color2="green";
						} else { color2="red"; }
					}
				}
			}
			strb.append("<div class=\""+style+"\" style=\"background-color:"+color2+"\"> "+txt2+" </div>");
			if (nb_build3!=0) {
				if (this.report3.getTestWithName(global_test.get(i).getName()) != null) {
					txt3="x";
					Test test3 = this.report3.getTestWithName(global_test.get(i).getName());
					if (!test3.isExecuted()) {
						color3="grey";
					} else {
						if (test3.isHasTimedOut()) {
							color3 = "yellow";
						} else {
							if (test3.isSuccessfull()) {
								color3="green";
							} else { color3="red"; }
						}
					}
				}
				strb.append("<div class=\""+style+"\" style=\"background-color:"+color3+"\"> "+txt3+" </div>");
			}
			strb.append("</div>\n");
		}
		
		
		
		return strb.toString();
	}
	
	public AbstractBuild<?, ?> getOwner() {
		return _owner;
	}

	public String getDisplayName() {
		return "Reports diff.";
	}

}
