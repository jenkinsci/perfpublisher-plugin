package hudson.plugins.PerfPublisher;

import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixProject;
import hudson.model.Action;
import hudson.plugins.PerfPublisher.Report.ReportContainer;
import hudson.plugins.PerfPublisher.matrixBuild.PerfPublisherMatrixBuild;
import hudson.plugins.PerfPublisher.matrixBuild.PerfPublisherMatrixSubBuild;

import java.util.*;
import java.util.Map.Entry;

public class MatrixTestReportAction extends AbstractPerfPublisherAction
		implements Action {

	PerfPublisherMatrixBuild matrixbuild;
	private int numberOfExecutedTest;
	private int numberOfTest;
	private int numberOfPassedTest;
	private int numberOfNotExecutedTest;
	private int numberOfTrueFalseTest;
	private int numberOfFailedTest;
    private int numberOfSuccessTest;
    private int numberOfUnstableTest;
	private MatrixBuild build;
	private MatrixProject project;

	public MatrixTestReportAction(MatrixBuild build) {
		this.matrixbuild = new PerfPublisherMatrixBuild(build.getNumber());
		this.build = build;
		this.project = build.getProject();
	}

	public MatrixBuild getBuild() {
		return build;
	}

	public MatrixProject getProject() {
		return project;
	}

	public String getDisplayName() {
		return PerfPublisherPlugin.MATRIX_BUILD_DISPLAY_NAME;
	}

	public String getSearchUrl() {
		return PerfPublisherPlugin.URL;
	}

	public String getIconFileName() {
		return PerfPublisherPlugin.ICON_FILE_NAME;
	}

	public String getUrlName() {
		return PerfPublisherPlugin.URL;
	}

	/**
	 * Add a subBuild result
	 * @param report
	 * @param buildVariables
	 */
	public void addSubBuildResult(ReportContainer report,
			Map<String, String> buildVariables) {
		Map<String, String> buildVars = new TreeMap<String, String>();
		buildVars.putAll(buildVariables);
		this.matrixbuild.addSubBuild(new PerfPublisherMatrixSubBuild(buildVars, report));
		computeStats();
	}
	public PerfPublisherMatrixBuild getMatrixBuild() {
		return this.matrixbuild;
	}

	public void computeStats() {
		numberOfExecutedTest = 0;
		numberOfFailedTest = 0;
		numberOfTest = 0;
		numberOfPassedTest = 0;
		numberOfTrueFalseTest = 0;
        numberOfSuccessTest = 0;
        numberOfUnstableTest = 0;
		
		List<PerfPublisherMatrixSubBuild> subBuilds = this.matrixbuild.getSubBuilds();
        for (PerfPublisherMatrixSubBuild subBuild : subBuilds) {
            ReportContainer report = subBuild.getReport();
            this.numberOfExecutedTest += report.getNumberOfExecutedTest();
            this.numberOfFailedTest += report.getNumberOfFailedTest();
            this.numberOfTest += report.getNumberOfTest();
            this.numberOfPassedTest += report.getNumberOfPassedTest();
            this.numberOfTrueFalseTest += report.getNumberOfTrueFalseTest();
            this.numberOfSuccessTest += report.getNumberOfSuccessTest();
            this.numberOfUnstableTest += report.getNumberOfUnstableTest();
        }
	}
	
	/**
	 * @return Summary HTML
	 */
	public String getSummary() {
        StringBuilder strbuilder = new StringBuilder();
        strbuilder.append("<div class=\"progress-container\">");
        double percentOfFailedTest = floor(100.0 * numberOfFailedTest / numberOfTrueFalseTest, 2);
        double percentOfSuccessTest = floor(100.0 * numberOfSuccessTest / numberOfTrueFalseTest, 2);
        double percentOfUnstableTest = floor(100.0 * numberOfUnstableTest / numberOfTrueFalseTest, 2);

        boolean showPct = percentOfFailedTest >= 15 || percentOfUnstableTest >= 15;
        boolean showUnstable = numberOfUnstableTest > 0;

        WidthCalculator widthCalculator = new WidthCalculator(15);
        widthCalculator.addWidth(percentOfFailedTest);
        if (showUnstable)
            widthCalculator.addWidth(percentOfUnstableTest);
        widthCalculator.addWidth(percentOfSuccessTest);

        int i = 0;
        appendSummary(strbuilder, "red", widthCalculator.getWidth(i++), percentOfFailedTest, numberOfFailedTest, showPct);
        if (showUnstable)
            appendSummary(strbuilder, "yellow", widthCalculator.getWidth(i++), percentOfUnstableTest, numberOfUnstableTest, showPct);
        appendSummary(strbuilder, "blue", widthCalculator.getWidth(i), percentOfSuccessTest, numberOfSuccessTest, showPct);
        strbuilder.append("</div>");
		return strbuilder.toString();
	}

    private void appendSummary(StringBuilder builder, String id, double width, double pct, int number, boolean showPct) {
  		builder.append("<div id=\"").append(id).append("\" style=\"width:").append(width).append("%;\">");
  		if (showPct)
  			builder.append(pct).append("% (").append(number).append(")");
  		else
  			builder.append(number);
  		builder.append("</div>");
  	}

	/**
	 * Round a double with n decimals
	 * 
	 * @param a
	 *            value to convert
	 * @param n
	 *            Number of decimals
	 * @return the rounded number
	 */
	public static double floor(double a, int n) {
		double p = Math.pow(10.0, n);
		return Math.floor((a * p) + 0.5) / p;
	}
	public String getDetailSummary() {
		StringBuilder strbuilder = new StringBuilder();
		
		strbuilder.append("Number of executed tests : <b>"+this.getNumberOfExecutedTest()+".</b>");
		strbuilder.append("<br />");
		
		return strbuilder.toString();
	}
	
//	public String getRegression() {
//		
//		StringBuilder strb = new StringBuilder();
//		List<Test> regressions = new ArrayList<Test>();
//		if (this.getTrendReport()!=null) {
//		List<Test> tmpTests = this.getTrendReport().getSuccessStatusChangedTests();
//		for (int i=0; i<tmpTests.size(); i++) {
//			if (!tmpTests.get(i).isSuccessfull()) {
//				regressions.add(tmpTests.get(i));
//			}
//		}
//		if (!regressions.isEmpty()) {
//			strb.append("<div class=\"warning_regression\">");
//			strb.append("This build has discovered "+regressions.size()+" regression(s).");
//			strb.append("</div>");
//		}
//		}
//		
//		return strb.toString();
//	}

	public String getHtmlArrayDisplay() {
		StringBuilder strb = new StringBuilder();
		/**
		 * Generate HTML Header Table
		 */
		strb.append("<table class=\"matrix_table\">\n");
		strb.append(generateHtmlArrayHeader());
		
		/**
		 * Generate HTML Table Content
		 */		
		strb.append(generateHtmlArrayContent());
		strb.append("</table>");
		
		return strb.toString();
	}
	
	private String generateHtmlArrayContent() {
		StringBuilder strb = new StringBuilder();
		List<PerfPublisherMatrixSubBuild> subBuilds = this.matrixbuild.getSubBuilds();
		Collections.sort(subBuilds);
		Map<String, List<Integer>> values = new HashMap<String, List<Integer>>();
		for (int i=0; i<subBuilds.size(); i++) {
			if (values.containsKey("Nb tests")) {
				List<Integer> nbs = values.get("Nb tests");
				nbs.add(subBuilds.get(i).getReport().getNumberOfTest());
			} else {
				List<Integer> nbs = new ArrayList<Integer>();
				nbs.add(subBuilds.get(i).getReport().getNumberOfTest());
				values.put("Nb tests", nbs);
			}
			if (values.containsKey("Nb Executed tests")) {
				List<Integer> nbs = values.get("Nb Executed tests");
				nbs.add(subBuilds.get(i).getReport().getNumberOfExecutedTest());
			} else {
				List<Integer> nbs = new ArrayList<Integer>();
				nbs.add(subBuilds.get(i).getReport().getNumberOfExecutedTest());
				values.put("Nb Executed tests", nbs);
			}
			if (values.containsKey("Nb not executed tests")) {
				List<Integer> nbs = values.get("Nb not executed tests");
				nbs.add(subBuilds.get(i).getReport().getNumberOfNotExecutedTest());
			} else {
				List<Integer> nbs = new ArrayList<Integer>();
				nbs.add(subBuilds.get(i).getReport().getNumberOfNotExecutedTest());
				values.put("Nb not executed tests", nbs);
			}
			if (values.containsKey("Succeeded tests")) {
				List<Integer> nbs = values.get("Succeeded tests");
				nbs.add(subBuilds.get(i).getReport().getNumberOfPassedTest());
			} else {
				List<Integer> nbs = new ArrayList<Integer>();
				nbs.add(subBuilds.get(i).getReport().getNumberOfPassedTest());
				values.put("Succeeded tests", nbs);
			}
			if (values.containsKey("Failed tests")) {
				List<Integer> nbs = values.get("Failed tests");
				nbs.add(subBuilds.get(i).getReport().getNumberOfFailedTest());
			} else {
				List<Integer> nbs = new ArrayList<Integer>();
				nbs.add(subBuilds.get(i).getReport().getNumberOfFailedTest());
				values.put("Failed tests", nbs);
			}
			if (values.containsKey("Avg Compile time")) {
				List<Integer> nbs = values.get("Avg Compile time");
				nbs.add((int)subBuilds.get(i).getReport().getAverageOfCompileTime());
			} else {
				List<Integer> nbs = new ArrayList<Integer>();
				nbs.add((int)subBuilds.get(i).getReport().getAverageOfCompileTime());
				values.put("Avg Compile time", nbs);
			}
			if (values.containsKey("Avg Execution time")) {
				List<Integer> nbs = values.get("Avg Execution time");
				nbs.add((int)subBuilds.get(i).getReport().getAverageOfExecutionTime());
			} else {
				List<Integer> nbs = new ArrayList<Integer>();
				nbs.add((int)subBuilds.get(i).getReport().getAverageOfExecutionTime());
				values.put("Avg Execution time", nbs);
			}
			if (values.containsKey("Avg Performance")) {
				List<Integer> nbs = values.get("Avg Performance");
				nbs.add((int)subBuilds.get(i).getReport().getAverageOfPerformance());
			} else {
				List<Integer> nbs = new ArrayList<Integer>();
				nbs.add((int)subBuilds.get(i).getReport().getAverageOfPerformance());
				values.put("Avg Performance", nbs);
			}
		}
		strb.append(generateHtmlContentRow(values));
		return strb.toString();
	}
	private String generateHtmlContentRow(Map<String, List<Integer>> values) {
		StringBuilder strb = new StringBuilder();
		Set<Entry<String, List<Integer>>> entry = values.entrySet();
		Iterator<Entry<String, List<Integer>>>  iterator = entry.iterator();
		int zebra = 2;
		
		while (iterator.hasNext()) {
			Entry<String, List<Integer>> axe = iterator.next();
			strb.append("<tr class=\"zebra"+zebra+"\">\n");
			strb.append("<td class=\"header\"><small>"+axe.getKey()+"</small></td>\n");
			
			List<Integer> i_min = new ArrayList<Integer>();
			int tmp_min = 0;
			List<Integer> i_max = new ArrayList<Integer>();
			int tmp_max = 0;
		
			
			for (int i=0; i<axe.getValue().size(); i++) {
				/**
				 * Compute min and max
				 * !! I Know this should be improve
				 */
				for (int j=0; j<axe.getValue().size(); j++) {
					if (j==0) {
						tmp_min = axe.getValue().get(j);
						tmp_max = axe.getValue().get(j);
						i_min.add(j);
						i_max.add(j);
					} else {
						if (tmp_min>axe.getValue().get(j)) {
							tmp_min = axe.getValue().get(j);
							i_min = new ArrayList<Integer>();
							i_min.add(j);
						}
						if (tmp_max<axe.getValue().get(j)) {
							tmp_max = axe.getValue().get(j);
							i_max = new ArrayList<Integer>();
							i_max.add(j);
						}
						if (tmp_min == axe.getValue().get(j)) {
							i_min.add(j);
						}
						if (tmp_max == axe.getValue().get(j)) {
							i_max.add(j);
						}
					}
				}
				
				
				if (i_min.contains(i)) {
					strb.append("<td class=\"min\">"+axe.getValue().get(i)+"</td>");
				} else if (i_max.contains(i)) {
					strb.append("<td class=\"max\">"+axe.getValue().get(i)+"</td>");
				} else {
					strb.append("<td>"+axe.getValue().get(i)+"</td>");
				}
				
				
			}
			strb.append("</tr>\n");
			if (zebra==1) { zebra = 2; } else { zebra = 1; }
		}
		return strb.toString();
	}

	private String generateHtmlArrayHeader() {
		StringBuilder strb = new StringBuilder();
		
//		/**
//		 * First a row which contains as cols as combinations
//		 */
//		strb.append("<thead>\n");
//		strb.append("<tr>\n");
//		
//		strb.append("<th>Sub-Builds</hd>\n");
//		for (int i=0; i<this.matrixbuild.getNbCombinations(); i++) {
//			strb.append("<th>"+i+"</th>\n");
//		}
//		strb.append("</tr>\n");
//		strb.append("</thead>\n");
		
		/**
		 * Rows for each axis
		 */
		int factor = 1;
		int colspan = this.matrixbuild.getNbCombinations();
		for (int i_row=0; i_row<this.matrixbuild.getNbAxis(); i_row++) {
			strb.append("<tr>");
			String axis = this.matrixbuild.getAxis().get(i_row);
			/**
			 * Add the row description
			 */
			strb.append("<td class=\"axe\">"+axis+"</td>\n");
			
			/**
			 * Separate the current row with axis values cols
			 */
			List<String> values = matrixbuild.getAxisValues(axis);
			Collections.sort(values);
			
			colspan = colspan/values.size();
			/**
			 * repeat axis cols on the same row
			 */
			for (int i_factor=0; i_factor<factor; i_factor++) {
				for (int i_value=0; i_value<values.size(); i_value++) {	
					if (colspan > 1) {
						strb.append("<td COLSPAN="+colspan+" class=\"axe_value\">"+values.get(i_value)+"</td>");	
					} else {
						strb.append("<td class=\"axe_value\">"+values.get(i_value)+"</td>\n");
					}					
				}
			}
			factor *= values.size();
			strb.append("</tr>\n");
		}
		return strb.toString();
	}

	public int getNumberOfExecutedTest() {
		return numberOfExecutedTest;
	}

}
