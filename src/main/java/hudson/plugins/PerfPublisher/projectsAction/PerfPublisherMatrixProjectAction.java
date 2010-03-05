package hudson.plugins.PerfPublisher.projectsAction;

import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixProject;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.FreeStyleProject;
import hudson.model.Project;
import hudson.model.Result;
import hudson.plugins.PerfPublisher.AbstractPerfPublisherAction;
import hudson.plugins.PerfPublisher.MatrixTestReportAction;
import hudson.plugins.PerfPublisher.PerfPublisherBuildAction;
import hudson.plugins.PerfPublisher.PerfPublisherPlugin;
import hudson.plugins.PerfPublisher.Report.ReportContainer;
import hudson.plugins.PerfPublisher.matrixBuild.PerfPublisherMatrixBuild;
import hudson.plugins.PerfPublisher.matrixBuild.PerfPublisherMatrixSubBuild;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Action used for PerfPublisher report on matrix project level.
 * 
 * @see AbstractPerfPublisherAction
 * @author Georges Bossert
 */
public class PerfPublisherMatrixProjectAction extends
		AbstractPerfPublisherAction {

	/**
	 * The associated matrix project
	 */
	private final MatrixProject project;

	/**
	 * The maximum number of build to display
	 */
	private final int max_nb_build = 10;

	/**
	 * Constructor
	 * 
	 * @param project
	 *            the current matrix project
	 */
	public PerfPublisherMatrixProjectAction(MatrixProject project) {
		this.project = project;
	}

	/**
	 * Getter for the display name which it used by hudson for the link menu
	 * display
	 */
	public String getDisplayName() {
		return PerfPublisherPlugin.GENERAL_DISPLAY_NAME;
	}

	/**
	 * Getter of the current matrix project
	 * 
	 * @return the current matrix project
	 */
	public MatrixProject getProject() {
		return project;
	}

	/**
	 * 
	 * @return
	 */
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

	public String getHtmlArrayDisplay() {
		StringBuilder strb = new StringBuilder();
		/**
		 * Compute builds to display
		 */
		List<Integer> buildsNumber = computeBuildsToDisplay();

		/**
		 * Generate HTML Header Table
		 */
		strb.append("<table class=\"global_matrix_table\">\n");
		strb.append(generateHtmlArrayHeader(buildsNumber));

		/**
		 * Generate HTML Table Content
		 */
		strb.append(generateHtmlArrayContent(buildsNumber));
		strb.append("</table>");

		return strb.toString();
	}

	/**
	 * Compute the builds numbers we will integer in the html table
	 * 
	 * @return an ordered array containing all the builds numbers
	 */
	private List<Integer> computeBuildsToDisplay() {
		List<Integer> builds = new ArrayList<Integer>();
		for (MatrixBuild build : this.project.getBuilds()) {
			if (!build.isBuilding()
					&& build.getResult().isBetterOrEqualTo(Result.SUCCESS)
					&& build.getAction(MatrixTestReportAction.class) != null) {
				builds.add(build.getNumber());
			}
		}
		Collections.sort(builds);

		List<Integer> result = new ArrayList<Integer>();

		/**
		 * Only display the last ${max_nb_build} builds
		 */
		int start = 0;
		if (builds.size() > max_nb_build) {
			start = builds.size() - max_nb_build;
		}
		for (int i = builds.size() - 1; i >= start; i--) {
			result.add(builds.get(i));
		}

		Collections.sort(result);
		return result;
	}

	private String generateHtmlArrayHeader(List<Integer> builds) {
		StringBuilder strb = new StringBuilder();

		strb.append("<tr class=\"header\">");
		strb
				.append("<td style=\"width:170px;\">Statistics</td><td>Combination</td>");

		for (int i_build = builds.size() - 1; i_build >= 0; i_build--) {
			MatrixBuild matrixBuild = this.project.getBuildByNumber(builds
					.get(i_build));

			strb.append("<td");
			if (matrixBuild.getResult() == Result.SUCCESS) {
				strb.append(" class=\"blue\"");
			} else if (matrixBuild.getResult() == Result.ABORTED
					|| matrixBuild.getResult() == Result.FAILURE) {
				strb.append(" class=\"red\"");
			} else if (matrixBuild.getResult() == Result.UNSTABLE) {
				strb.append(" class=\"yellow\"");
			} else if (matrixBuild.getResult() == Result.NOT_BUILT) {
				strb.append(" class=\"grey\"");
			}

			strb.append("><small>build</small> " + matrixBuild.getNumber()
					+ "</td>");
		}
		strb.append("</tr>");

		return strb.toString();
	}

	/**
	 * Generate the html source code for the array content it must be called by
	 * the jelly
	 * 
	 * @return a string containing the html source code
	 */
	private String generateHtmlArrayContent(List<Integer> builds) {
		StringBuilder strb = new StringBuilder();

		Map<String, Map<String, Map<Integer,Float>>> values = getStaticticsValues(builds);
		Set<String> statsNames = values.keySet();
		Iterator<String> iteratorOnStatsNames = statsNames.iterator();
		while (iteratorOnStatsNames.hasNext()) {

			// Get the stats name
			String statsName = iteratorOnStatsNames.next();
			Map<String, Map<Integer,Float>> values2 = values.get(statsName);
			// Remove the classifying solution from the stats name. format :
			// {number - stats_name}
			statsName = statsName.substring(statsName.indexOf('-') + 2);

			strb.append("<tr");
			strb.append(" onMouseOver=\"this.style.backgroundColor='#FFCC66';\"");
			strb.append(" onMouseOut=\"this.style.backgroundColor='#FFFFFF';\"");
			strb.append(">\n");
			strb.append("<td rowspan=\"" + values2.size()
					+ "\"  class=\"statsRow\">\n");
			strb.append("<b>" + statsName + "</b>");
			strb.append("</td>\n");

			Set<String> combinations = values2.keySet();
			Iterator<String> iteratorOnCombinations = combinations.iterator();
			while (iteratorOnCombinations.hasNext()) {

				// Min and max local for this combination
				List<Integer> i_min_local = new ArrayList<Integer>();
				List<Integer> i_max_local = new ArrayList<Integer>();
				float min_local = 0, max_local = 0;

				// Get the combination name
				String combination = iteratorOnCombinations.next();
				Map<Integer,Float> buildValues = values2.get(combination);

				String tdClass = "";
				if (!iteratorOnCombinations.hasNext()) {
					tdClass = "class=\"combinationRow\" style=\"border-bottom:1px solid #000000;\"";
				} else {
					tdClass = "class=\"combinationRow\"";
				}
				strb.append("<td " + tdClass + ">" + combination + "</td>\n");

				// Compute the min and max
				for (int i_buildNumber= builds.size() - 1; i_buildNumber >= 0; i_buildNumber--) {
					if (buildValues.containsKey(builds.get(i_buildNumber))) {
						Float buildValue = buildValues.get(builds.get(i_buildNumber));
						if (i_buildNumber == builds.size() - 1) {
							i_max_local.add(builds.get(i_buildNumber));
							i_min_local.add(builds.get(i_buildNumber));
							max_local = min_local = buildValue;
						} else {
							if (buildValue < min_local) {
								min_local = buildValue;
								i_min_local = new ArrayList<Integer>();
								i_min_local.add(builds.get(i_buildNumber));
							} else if (buildValue > max_local) {
								max_local = buildValue;
								i_max_local = new ArrayList<Integer>();
								i_max_local.add(builds.get(i_buildNumber));
							} else if (buildValue == min_local) {
								i_min_local.add(builds.get(i_buildNumber));
							} else if (buildValue == max_local) {
								i_max_local.add(builds.get(i_buildNumber));
							}
						}
					}
				}
				// Generate the content of the table
				for (int i_buildNumber= builds.size() - 1; i_buildNumber >= 0; i_buildNumber--) {
					if (buildValues.containsKey(builds.get(i_buildNumber))) {
						Float buildValue = buildValues.get(builds.get(i_buildNumber));
						if (i_min_local.contains(builds.get(i_buildNumber))) {
							strb.append("<td " + tdClass + "><font color=\"red\">"
									+ buildValue + "</font></td>\n");
						} else if (i_max_local.contains(builds.get(i_buildNumber))) {
							strb.append("<td " + tdClass + "><font color=\"blue\">"
									+ buildValue + "</font></td>\n");
						} else {
							strb.append("<td " + tdClass + ">" + buildValue
									+ "</td>\n");
						}
					} else {
						strb.append("<td " + tdClass + ">-</td>\n");
					}
				}
				
				strb.append("</tr>\n");
				if (iteratorOnCombinations.hasNext()) {
					strb.append("<tr");
					strb
							.append(" onMouseOver=\"this.style.backgroundColor='#FFCC66';\"");
					strb
							.append(" onMouseOut=\"this.style.backgroundColor='#FFFFFF';\"");
					strb.append(">\n");
				}
			}
		}

		return strb.toString();
	}

	/**
	 * Format datas to optimize the html generation
	 * 
	 * @return the data formated Map<StatsName,Map<Combination,
	 *         List<StatsValue>>>
	 */
	private Map<String, Map<String, Map<Integer,Float>>> getStaticticsValues(
			List<Integer> builds) {
		// Data container
		Map<String, Map<String, Map<Integer,Float>>> values = new TreeMap<String, Map<String, Map<Integer, Float>>>();

		for (int i_build = 0; i_build < builds.size(); i_build++) {
			MatrixBuild build = this.project.getBuildByNumber(builds
					.get(i_build));
			// Get the report from this build
			MatrixTestReportAction reportAction = build
					.getAction(MatrixTestReportAction.class);
			// Get the matrix build report
			PerfPublisherMatrixBuild matrixBuild = reportAction
					.getMatrixBuild();
			if (matrixBuild != null) {
				List<PerfPublisherMatrixSubBuild> subBuilds = matrixBuild
						.getSubBuilds();
				for (int i_subBuild = 0; i_subBuild < subBuilds.size(); i_subBuild++) {
					// Get the report associated to a specific subBuild
					PerfPublisherMatrixSubBuild subBuild = subBuilds
							.get(i_subBuild);

					// Compute the number of test
					values = updateStatistics(values, "0 - Number of test",
							subBuild.getStringCombinations(), build.getNumber(), subBuild
									.getReport().getNumberOfTest() + 0f);

					// Compute the number of executed test
					values = updateStatistics(values,
							"1 - Number of executed test", subBuild
									.getStringCombinations(), build.getNumber(), subBuild
									.getReport().getNumberOfExecutedTest() + 0f);

					// Compute the number of not executed test
					values = updateStatistics(
							values,
							"2 - Number of not executed test",
							subBuild.getStringCombinations(),build.getNumber(), 
							subBuild.getReport().getNumberOfNotExecutedTest() + 0f);

					// Compute the number of successfull test
					values = updateStatistics(values,
							"3 - Number of passed test", subBuild
									.getStringCombinations(), build.getNumber(), subBuild
									.getReport().getNumberOfPassedTest() + 0f);

					// Compute the number of failed test
					values = updateStatistics(values,
							"4 - Number of failed test", subBuild
									.getStringCombinations(), build.getNumber(), subBuild
									.getReport().getNumberOfFailedTest() + 0f);

					// Compute the average of compile time
					values = updateStatistics(values,
							"5 - Average of compile time", subBuild
									.getStringCombinations(), build.getNumber(), (float) subBuild
									.getReport().getAverageOfCompileTime());

					// Compute the average of execution time
					values = updateStatistics(values,
							"6 - Average of execution time", subBuild
									.getStringCombinations(), build.getNumber(), (float) subBuild
									.getReport().getAverageOfExecutionTime());

					// Compute the average of performance
					values = updateStatistics(values,
							"7 - Average of performance", subBuild
									.getStringCombinations(), build.getNumber(), (float) subBuild
									.getReport().getAverageOfPerformance());

					// Compute the number of files
					values = updateStatistics(values, "8 - Number of files",
							subBuild.getStringCombinations(), build.getNumber(), subBuild
									.getReport().getNumberOfFiles() + 0f);

				}
			}
		}
		return values;
	}

	/**
	 * Insert into the map the current statistics values
	 * 
	 * @param values
	 *            the representation of datas
	 * @param statsName
	 *            statistic name like "Number of test"
	 * @param stringCombinations
	 *            combination which has generated the stats
	 * @param statsValue
	 *            statistic value associated to the statsName
	 * @return the current map added with the current stats value
	 */
	private Map<String, Map<String, Map<Integer,Float>>> updateStatistics(
			Map<String, Map<String, Map<Integer,Float>>> values, String statsName,
			String stringCombinations, int buildNumber, float statsValue) {

		if (values.containsKey(statsName)) {
			Map<String, Map<Integer,Float>> tmp2 = values.get(statsName);
			if (tmp2.containsKey(stringCombinations)) {
				Map<Integer,Float> tmp_float = tmp2.get(stringCombinations);
				tmp_float.put(buildNumber, statsValue);
				tmp2.put(stringCombinations, tmp_float);
				values.put(statsName, tmp2);
			} else {
				Map<Integer, Float> tmp_float = new HashMap<Integer, Float>();
				tmp_float.put(buildNumber, statsValue);
				tmp2.put(stringCombinations, tmp_float);
				values.put(statsName, tmp2);
			}
		} else {
			Map<Integer, Float> tmp_float = new HashMap<Integer, Float>();
			tmp_float.put(buildNumber, statsValue);
			Map<String, Map<Integer, Float>> tmp2 = new HashMap<String, Map<Integer, Float>>();
			tmp2.put(stringCombinations, tmp_float);
			values.put(statsName, tmp2);
		}

		return values;
	}

	public void doSuccessGraph(StaplerRequest request, StaplerResponse response)
			throws IOException {
		ChartUtil.generateGraph(request, response, createSuccessGraph(), 800,
				250);
	}

	public void doMiniSuccessGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		ChartUtil.generateGraph(request, response, createSuccessGraph(), 350,
				1000);
	}

	private JFreeChart createSuccessGraph() {

		DataSetBuilder<String, NumberOnlyBuildLabel> builder = new DataSetBuilder<String, NumberOnlyBuildLabel>();

		for (Object build : project.getBuilds()) {
			AbstractBuild abstractBuild = (AbstractBuild) build;
			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(
							Result.UNSTABLE)) {
				MatrixTestReportAction action = abstractBuild
						.getAction(MatrixTestReportAction.class);
				if (action != null && action.getMatrixBuild() != null
						&& action.getMatrixBuild().getSubBuilds() != null) {
					List<PerfPublisherMatrixSubBuild> subBuilds = action
							.getMatrixBuild().getSubBuilds();
					Collections.sort(subBuilds);
					for (int i = 0; i < subBuilds.size(); i++) {
						builder.add(subBuilds.get(i).getReport()
								.getNumberOfExecutedTest(), subBuilds.get(i)
								.getStringCombinations(),
								new NumberOnlyBuildLabel(abstractBuild));

					}
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

	private boolean shouldReloadGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		return shouldReloadGraph(request, response, project
				.getLastSuccessfulBuild());
	}
}
