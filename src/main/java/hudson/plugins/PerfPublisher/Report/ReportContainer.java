package hudson.plugins.PerfPublisher.Report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.FileAppender;

public class ReportContainer {

	ArrayList<Report> reports;
	ArrayList<String> files;
	ArrayList<Test> tests;
	ArrayList<Test> compileTimeTest;
	ArrayList<Test> performanceTest;
	ArrayList<Test> executionTimeTest;

	/**
	 * Analysis results
	 */
	// metrics
	private Map<String, String> metrics_name = new HashMap<String, String>();
	
	private Map<String, Double> bestValuePerMetrics = new HashMap<String, Double>();
	private Map<String, Double> worstValuePerMetrics = new HashMap<String, Double>();
	private Map<String, Double> averageValuePerMetrics = new HashMap<String, Double>();
	private Map<String, Integer> nbValuePerMetric = new HashMap<String, Integer>();
	
	
	private Test bestCompileTimeTest;
	private double averageCompileTime;
	private Test worstCompileTimeTest;
	private Test bestExecutionTimeTest;
	private double averageExecutionTime;
	private Test worstExecutionTimeTest;
	private Test bestPerformanceTest;
	private double averagePerformance;
	private Test worstPerformanceTest;

	private int numberOfTest;
	private int numberOfPassedTest;
	private int numberOfNotExecutedTest;
	private int numberOfFailedTest;

	public ReportContainer() {
		tests = new ArrayList<Test>();
		reports = new ArrayList<Report>();
		files = new ArrayList<String>();
		compileTimeTest = new ArrayList<Test>();
		performanceTest = new ArrayList<Test>();
		executionTimeTest = new ArrayList<Test>();
		/*
		 * this.bestCompileTimeTest = computeBestCompileTimeTest();
		 * this.averageCompileTime = computeAverageOfCompileTime();
		 * this.worstCompileTimeTest = computeWorstCompileTimeTest();
		 * this.bestExecutionTimeTest = computeBestExecutionTimeTest();
		 * this.averageExecutionTime = computeAverageOfExecutionTime();
		 * this.worstExecutionTimeTest = computeWorstExecutionTimeTest();
		 * this.bestPerformanceTest = computeBestPerformanceTest();
		 * this.averagePerformance = computeAverageOfPerformance();
		 * this.worstPerformanceTest = computeWorstPerformanceTest();
		 */
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

	/**
	 * Add a report into Container
	 * 
	 * @param report
	 *            report to add
	 * @param computeStats
	 *            if true : compute stats
	 */
	public void addReport(Report report, boolean computeStats) {
		
		if (computeStats) {
			addReport(report);
		} else {
			/**
			 * If report's category already exists, just add the tests if no,
			 * create report category
			 */
			if (getReportOfThisCategorie(report.getCategorie()) != null) {
				for (int j = 0; j < report.getNumberOfTest(); j++) {
					getReportOfThisCategorie(report.getCategorie()).addTest(
							report.getTests().get(j));
				}
			} else {
				reports.add(report);
			}
		}
	}

	/**
	 * Adding a report into Container and compute Stats
	 * 
	 * @param report
	 */
	public void addReport(Report report) {
		/**
		 * If report's category already exists, just add the tests if no, create
		 * report category
		 */
		if (getReportOfThisCategorie(report.getCategorie()) != null) {
			for (int j = 0; j < report.getNumberOfTest(); j++) {
				getReportOfThisCategorie(report.getCategorie()).addTest(
						report.getTests().get(j));
			}
		} else {
			reports.add(report);
		}
		computeStats();
	}

	public void computeStats() {
		computeGetTests();
		computeCompileTimeTest();
		computePerformanceTest();
		computeExecutionTimeTest();
		computeMetrics();
		this.numberOfTest = computeNumberOfTest();
		this.numberOfFailedTest = computeNumberOfFailedTest();
		this.numberOfNotExecutedTest = computeNumberOfNotExecutedTest();
		this.numberOfPassedTest = computeNumberOfPassedTest();
		this.bestCompileTimeTest = computeBestCompileTimeTest();
		this.averageCompileTime = computeAverageOfCompileTime();
		this.worstCompileTimeTest = computeWorstCompileTimeTest();
		this.bestExecutionTimeTest = computeBestExecutionTimeTest();
		this.averageExecutionTime = computeAverageOfExecutionTime();
		this.worstExecutionTimeTest = computeWorstExecutionTimeTest();
		this.bestPerformanceTest = computeBestPerformanceTest();
		this.averagePerformance = computeAverageOfPerformance();
		this.worstPerformanceTest = computeWorstPerformanceTest();

	}

	/**
	 * Generates the source file to generate diff solution
	 * 
	 * @return xmls sources
	 */
	@SuppressWarnings("unchecked")
	public String getXmlForDiff() {
		String date = new Date().toString();
		StringBuilder result = new StringBuilder();
		List<Test> tests = getTests();
		// Sort by test name
		Collections.sort(tests);
		for (int i = 0; i < tests.size(); i++) {
			// Add name of the test
			result.append(tests.get(i).getName() + " | ");
			// Add if the test is executed
			if (tests.get(i).isExecuted()) {
				result.append("YES | ");
			} else {
				result.append("NO | ");
			}
			// Add the success state of the test
			if (tests.get(i).isSuccessfull()) {
				result.append("YES ");
			} else {
				result.append("NO ");
			}
			result.append("\r\n");
		}
		return result.toString();
	}

	public ErrorMessageContainer getErrorMessageContainer() {
		ErrorMessageContainer result = new ErrorMessageContainer();
		for (int i = 0; i < getTests().size(); i++) {
			if (getTests().get(i).isExecuted()
					&& !getTests().get(i).isSuccessfull()) {
				if (getTests().get(i).getMessage() != null) {
					result.addErrorMessage(new ErrorMessage(getTests().get(i)
							.getMessage()), getTests().get(i));
				} else {
					result.addErrorMessage(new ErrorMessage(""), getTests()
							.get(i));
				}
			}
		}
		return result;
	}

	public ErrorMessageContainer getValidMessageContainer() {
		ErrorMessageContainer result = new ErrorMessageContainer();
		for (int i = 0; i < getTests().size(); i++) {
			if (getTests().get(i).isExecuted()
					&& getTests().get(i).isSuccessfull()) {
				if (getTests().get(i).getMessage() != null) {
					result.addErrorMessage(new ErrorMessage(getTests().get(i)
							.getMessage()), getTests().get(i));
				} else {
					result.addErrorMessage(new ErrorMessage(""), getTests()
							.get(i));
				}
			}
		}
		return result;
	}

	public ErrorMessageContainer getBrokenMessageContainer() {
		ErrorMessageContainer result = new ErrorMessageContainer();
		for (int i = 0; i < getTests().size(); i++) {
			if (!getTests().get(i).isExecuted()) {
				if (getTests().get(i).getMessage() != null) {
					result.addErrorMessage(new ErrorMessage(getTests().get(i)
							.getMessage()), getTests().get(i));
				} else {
					result.addErrorMessage(new ErrorMessage(""), getTests()
							.get(i));
				}
			}
		}
		return result;
	}

	public void computeGetTests() {
		this.tests = new ArrayList<Test>();
		this.tests.ensureCapacity(getNumberOfTest());
		for (int i = 0; i < getNumberOfReports(); i++) {
			this.tests.addAll(reports.get(i).getTests());
		}
	}

	public List<Test> getTests() {
		if (this.tests == null || this.tests.size() == 0) {
			computeGetTests();
		}
		return this.tests;
	}
	public Map<String,String> getMetricsName() {
		return this.metrics_name;
	}

	public ArrayList<Test> getExecutedTests() {
		ArrayList<Test> tests = new ArrayList<Test>();
		for (int i = 0; i < getNumberOfReports(); i++) {
			tests.addAll(reports.get(i).getExecutedTests());
		}
		return tests;
	}

	public ArrayList<Test> getNotExecutedTests() {
		ArrayList<Test> tests = new ArrayList<Test>();
		for (int i = 0; i < getNumberOfReports(); i++) {
			tests.addAll(reports.get(i).getNotExecutedTests());
		}
		return tests;
	}

	public int getNumberOfTest() {
		if (this.numberOfTest != 0) {
			return this.numberOfTest;
		} else {
			this.numberOfTest = computeNumberOfTest();
			return this.numberOfTest;
		}
	}

	public int computeNumberOfTest() {
		int result = 0;
		for (int i = 0; i < getNumberOfReports(); i++) {
			result += reports.get(i).getNumberOfTest();
		}
		return result;
	}

	public int getNumberOfExecutedTest() {
		int result = 0;
		for (int i = 0; i < getNumberOfReports(); i++) {
			result += reports.get(i).getNumberOfExecutedTest();
		}
		return result;
	}

	public int computeNumberOfNotExecutedTest() {
		return getNotExecutedTests().size();
	}

	public int getNumberOfNotExecutedTest() {
		if (numberOfNotExecutedTest == 0) {
			this.numberOfNotExecutedTest = computeNumberOfNotExecutedTest();
		}
		return this.numberOfNotExecutedTest;
	}

	public double getPercentOfExecutedTest() {
		double resultat = 0;
		resultat = ((double) getNumberOfExecutedTest() / getNumberOfTest()) * 100;
		return floor(resultat, 2);
	}

	public double getPercentOfNotExecutedTest() {
		double resultat = 0;
		resultat = 100 - getPercentOfExecutedTest();
		return floor(resultat, 2);
	}

	public boolean isPercentOfFailedTestLowFifteen() {
		return (getPercentOfFailedTest() < 15);
	}

	public boolean isPercentOfFailedTestSupFifteen() {
		return (getPercentOfFailedTest() >= 15);
	}

	public int getNumberOfPassedTest() {
		if (this.numberOfPassedTest != 0) {
			return this.numberOfPassedTest;
		} else {
			this.numberOfPassedTest = computeNumberOfPassedTest();
			return this.numberOfPassedTest;
		}
	}

	public int computeNumberOfPassedTest() {
		int result = 0;
		for (int i = 0; i < getNumberOfReports(); i++) {
			result += reports.get(i).getNumberofPassedTest();
		}
		return result;
	}

	public int getNumberOfFailedTest() {
		if (this.numberOfFailedTest == 0) {
			this.numberOfFailedTest = computeNumberOfFailedTest();
		}
		return this.numberOfFailedTest;
	}

	public int computeNumberOfFailedTest() {
		int result = 0;
		for (int i = 0; i < getNumberOfReports(); i++) {
			result += getReports().get(i).getNumberofFailedTest();
		}
		return result;
	}

	public double getPercentOfFailedTest() {
		double resultat = 0;
		resultat = ((double) getNumberOfFailedTest() / getNumberOfTrueFalseTest()) * 100;
		return floor(resultat, 2);
	}

	public double getPercentOfPassedTest() {
		double resultat = 0;
		resultat = ((double) getNumberOfPassedTest() / getNumberOfTrueFalseTest()) * 100;
		return floor(resultat, 2);
	}

	public double getNumberOfTrueFalseTest() {
		int result = 0;
		for (int i = 0; i < getNumberOfReports(); i++) {
			result += reports.get(i).getNumberOfSuccessTest();
		}
		return result;
	}

	/**
	 * METRICS STATISTICS
	 */
	public void setMetricsName(Map<String,String> metrics_name) {
		this.metrics_name = metrics_name;
	}
	public void computeMetrics() {
		for (int i = 0; i < getNumberOfReports(); i++) {
			for (int j = 0; j < getReports().get(i).getNumberOfExecutedTest(); j++) {
				if (getReports().get(i).getExecutedTests().get(j).getMetrics()
						.size() > 0) {
					Map<String, Double> metric = getReports().get(i)
							.getExecutedTests().get(j).getMetrics();
					for (String name : metric.keySet()) {
						double value = metric.get(name);
						// Compute best metric
						if (!bestValuePerMetrics.containsKey(name)) {
							bestValuePerMetrics.put(name, value);
						} else {
							if (bestValuePerMetrics.get(name) < value) {
								bestValuePerMetrics.put(name, value);
							}
						}
						// Compute worst metric
						if (!worstValuePerMetrics.containsKey(name)) {
							worstValuePerMetrics.put(name, value);
						} else {
							if (worstValuePerMetrics.get(name) > value) {
								worstValuePerMetrics.put(name, value);
							}
						}
						// Compute average metric
						if (!averageValuePerMetrics.containsKey(name)) {
							averageValuePerMetrics.put(name, value);
							getNbValuePerMetric().put(name, 1);
						} else {
							averageValuePerMetrics.put(name,
									averageValuePerMetrics.get(name) + value);
							getNbValuePerMetric().put(name, getNbValuePerMetric()
									.get(name) + 1);
						}
					}
				}
			}
		}
		for (String name : getNbValuePerMetric().keySet()) {
			averageValuePerMetrics.put(name, averageValuePerMetrics.get(name)
					/ getNbValuePerMetric().get(name));
		}
	}
	

	

	/**
	 * COMPILE TIME STATISTICS
	 */
	public void computeCompileTimeTest() {
		compileTimeTest = new ArrayList<Test>();
		for (int i = 0; i < getNumberOfReports(); i++) {
			for (int j = 0; j < getReports().get(i).getNumberOfExecutedTest(); j++) {
				if (getReports().get(i).getExecutedTests().get(j)
						.isCompileTime()
						&& getReports().get(i).getExecutedTests().get(j)
								.getCompileTime().isRelevant()) {
					compileTimeTest.add(getReports().get(i).getExecutedTests()
							.get(j));
				}
			}
		}
	}

	public ArrayList<Test> getCompileTimeTest() {
		if (compileTimeTest == null) {
			computeCompileTimeTest();
		}
		return compileTimeTest;
	}

	public int getNumberOfCompileTimeTest() {
		return getCompileTimeTest().size();
	}

	private Test computeBestCompileTimeTest() {
		Test result = new Test();
		for (int i = 0; i < getNumberOfCompileTimeTest(); i++) {
			if (result.getCompileTime().getMeasure() >= getCompileTimeTest()
					.get(i).getCompileTime().getMeasure()
					|| i == 0) {
				result = getCompileTimeTest().get(i);
			}
		}
		return result;
	}

	public Test getBestCompileTimeTest() {
		if (this.bestCompileTimeTest != null) {
			return this.bestCompileTimeTest;
		} else {
			this.bestCompileTimeTest = computeBestCompileTimeTest();
			return this.bestCompileTimeTest;
		}
	}

	public double getBestCompileTimeTestValue() {
		return getBestCompileTimeTest().getCompileTime().getMeasure();
	}

	public String getBestCompileTimeTestName() {
		return getBestCompileTimeTest().getName();
	}

	private Test computeWorstCompileTimeTest() {
		Test result = new Test();
		for (int i = 0; i < getNumberOfCompileTimeTest(); i++) {
			if (result.getCompileTime().getMeasure() < getCompileTimeTest()
					.get(i).getCompileTime().getMeasure()
					|| i == 0) {
				result = getCompileTimeTest().get(i);
			}
		}
		return result;
	}

	public Test getWorstCompileTimeTest() {
		if (this.worstCompileTimeTest != null) {
			return this.worstCompileTimeTest;
		} else {
			this.worstCompileTimeTest = computeWorstCompileTimeTest();
			return this.worstCompileTimeTest;
		}
	}

	public double getWorstCompileTimeTestValue() {
		return getWorstCompileTimeTest().getCompileTime().getMeasure();
	}

	public String getWorstCompileTimeTestName() {
		return getWorstCompileTimeTest().getName();
	}

	private double computeAverageOfCompileTime() {
		double result = 0.0;
		for (int i = 0; i < getNumberOfCompileTimeTest(); i++) {
			result += getCompileTimeTest().get(i).getCompileTime().getMeasure();
		}
		if (getNumberOfCompileTimeTest() != 0) {
			return floor(result / getNumberOfCompileTimeTest(), 2);
		} else {
			return 0;
		}
	}

	public double getAverageOfCompileTime() {
		if (this.averageCompileTime != 0.0) {
			return this.averageCompileTime;
		} else {
			this.averageCompileTime = computeAverageOfCompileTime();
			return this.averageCompileTime;
		}

	}

	/**
	 * PERFORMANCE STATISTICS
	 */
	public void computePerformanceTest() {
		performanceTest = new ArrayList<Test>();
		for (int i = 0; i < getNumberOfReports(); i++) {
			for (int j = 0; j < getReports().get(i).getNumberOfExecutedTest(); j++) {
				if (getReports().get(i).getExecutedTests().get(j)
						.isPerformance()
						&& getReports().get(i).getExecutedTests().get(j)
								.getPerformance().isRelevant()) {
					performanceTest.add(getReports().get(i).getExecutedTests()
							.get(j));
				}
			}
		}
	}

	public ArrayList<Test> getPerformanceTest() {
		if (performanceTest == null) {
			computePerformanceTest();
		}
		return performanceTest;
	}

	public int getNumberOfPerformanceTest() {
		return getPerformanceTest().size();
	}

	private Test computeBestPerformanceTest() {
		Test result = new Test();
		for (int i = 0; i < getNumberOfPerformanceTest(); i++) {
			if (result.getPerformance().getMeasure() <= getPerformanceTest()
					.get(i).getPerformance().getMeasure()
					|| i == 0) {
				result = getPerformanceTest().get(i);
			}
		}
		return result;
	}

	public Test getBestPerformanceTest() {
		if (this.bestPerformanceTest != null) {
			return this.bestPerformanceTest;
		} else {
			this.bestPerformanceTest = computeBestPerformanceTest();
			return this.bestPerformanceTest;
		}
	}

	public double getBestPerformanceTestValue() {
		return getBestPerformanceTest().getPerformance().getMeasure();
	}

	public String getBestPerformanceTestName() {
		return getBestPerformanceTest().getName();
	}

	private Test computeWorstPerformanceTest() {
		Test result = new Test();
		for (int i = 0; i < getNumberOfPerformanceTest(); i++) {
			if (result.getPerformance().getMeasure() > getPerformanceTest()
					.get(i).getPerformance().getMeasure()
					|| i == 0) {
				result = getPerformanceTest().get(i);
			}
		}
		return result;
	}

	public Test getWorstPerformanceTest() {
		if (this.worstPerformanceTest != null) {
			return this.worstPerformanceTest;
		} else {
			this.worstPerformanceTest = computeWorstPerformanceTest();
			return this.worstPerformanceTest;
		}
	}

	public double getWorstPerformanceTestValue() {
		return getWorstPerformanceTest().getPerformance().getMeasure();
	}

	public String getWorstPerformanceTestName() {
		return getWorstPerformanceTest().getName();
	}

	private double computeAverageOfPerformance() {
		double result = 0.0;
		for (int i = 0; i < getNumberOfPerformanceTest(); i++) {
			result += getPerformanceTest().get(i).getPerformance().getMeasure();
		}
		if (getNumberOfPerformanceTest() != 0) {
			return floor(result / getNumberOfPerformanceTest(), 2);
		} else {
			return 0;
		}
	}

	public double getAverageOfPerformance() {
		if (this.averagePerformance != 0.0) {
			return this.averagePerformance;
		} else {
			this.averagePerformance = computeAverageOfPerformance();
			return this.averagePerformance;
		}
	}

	/**
	 * EXECUTION TIME STATISTICS
	 */
	public void computeExecutionTimeTest() {
		executionTimeTest = new ArrayList<Test>();
		for (int i = 0; i < getNumberOfReports(); i++) {
			for (int j = 0; j < getReports().get(i).getNumberOfExecutedTest(); j++) {
				if (getReports().get(i).getExecutedTests().get(j)
						.isExecutionTime()
						&& getReports().get(i).getExecutedTests().get(j)
								.getExecutionTime().isRelevant()) {
					executionTimeTest.add(getReports().get(i)
							.getExecutedTests().get(j));
				}
			}
		}
	}

	public ArrayList<Test> getExecutionTimeTest() {
		if (executionTimeTest == null) {
			computeExecutionTimeTest();
		}
		return executionTimeTest;
	}

	public int getNumberOfExecutionTimeTest() {
		return getExecutionTimeTest().size();
	}

	private Test computeBestExecutionTimeTest() {
		Test result = new Test();
		for (int i = 0; i < getNumberOfExecutionTimeTest(); i++) {
			if (result.getExecutionTime().getMeasure() >= getExecutionTimeTest()
					.get(i).getExecutionTime().getMeasure()
					|| i == 0) {
				result = getExecutionTimeTest().get(i);
			}
		}
		return result;
	}

	public Test getBestExecutionTimeTest() {
		if (this.bestExecutionTimeTest != null) {
			return this.bestExecutionTimeTest;
		} else {
			this.bestExecutionTimeTest = computeBestExecutionTimeTest();
			return this.bestExecutionTimeTest;
		}
	}

	public double getBestExecutionTimeTestValue() {
		return getBestExecutionTimeTest().getExecutionTime().getMeasure();
	}

	public String getBestExecutionTimeTestName() {
		return getBestExecutionTimeTest().getName();
	}

	private Test computeWorstExecutionTimeTest() {
		Test result = new Test();
		for (int i = 0; i < getNumberOfExecutionTimeTest(); i++) {
			if (result.getExecutionTime().getMeasure() < getExecutionTimeTest()
					.get(i).getExecutionTime().getMeasure()
					|| i == 0) {
				result = getExecutionTimeTest().get(i);
			}
		}
		return result;
	}

	public Test getWorstExecutionTimeTest() {
		if (this.worstExecutionTimeTest != null) {
			return this.worstExecutionTimeTest;
		} else {
			this.worstExecutionTimeTest = computeWorstExecutionTimeTest();
			return this.worstExecutionTimeTest;
		}
	}

	public double getWorstExecutionTimeTestValue() {
		return getWorstExecutionTimeTest().getExecutionTime().getMeasure();
	}

	public String getWorstExecutionTimeTestName() {
		return getWorstExecutionTimeTest().getName();
	}

	private double computeAverageOfExecutionTime() {
		double result = 0.0;
		for (int i = 0; i < getNumberOfExecutionTimeTest(); i++) {
			result += getExecutionTimeTest().get(i).getExecutionTime()
					.getMeasure();
		}
		if (getNumberOfExecutionTimeTest() != 0) {
			return floor(result / getNumberOfExecutionTimeTest(), 2);
		} else {
			return 0;
		}
	}

	public double getAverageOfExecutionTime() {
		if (this.averageExecutionTime != 0.0) {
			return this.averageExecutionTime;
		} else {
			this.averageExecutionTime = computeAverageOfExecutionTime();
			return this.averageExecutionTime;
		}
	}
	
	/**
	 * METRICS
	 */
	public Map<String, Double> getBestValuePerMetrics() {
		return bestValuePerMetrics;
	}

	public Map<String, Double> getWorstValuePerMetrics() {
		return worstValuePerMetrics;
	}

	public Map<String, Double> getAverageValuePerMetrics() {
		return averageValuePerMetrics;
	}

	/**
	 * Return the report having the same category
	 * 
	 * @param category
	 *            to search for
	 * @return Report if it exists
	 */
	public Report getReportOfThisCategorie(String cat) {
		for (int i = 0; i < reports.size(); i++) {
			if (reports.get(i).getCategorie().equalsIgnoreCase(cat)) {
				return reports.get(i);
			}
		}
		return null;
	}

	

	/**
	 * Return the report having the same file
	 * 
	 * @param file
	 *            to search for
	 * @return Report if it exists
	 */
	public Report getReportOfThisFile(String cat) {
		for (int i = 0; i < reports.size(); i++) {
			if (reports.get(i).getFile().equals(cat)) {
				return reports.get(i);
			}
		}
		return null;
	}

	public ArrayList<String> getCategories() {
		ArrayList<String> categories = new ArrayList<String>();
		for (int i = 0; i < reports.size(); i++) {
			categories.add(reports.get(i).getCategorie());
		}
		return categories;
	}

	public ArrayList<Report> getReports() {
		return reports;
	}

	public ArrayList<String> getFiles() {
		return files;
	}

	public int getNumberOfFiles() {
		return files.size();
	}

	public void addFile(String file) {
		files.add(file);
	}

	public ArrayList<Test> getSuccessTests() {
		ArrayList<Test> tests = new ArrayList<Test>();
		for (int i = 0; i < getNumberOfReports(); i++) {
			for (int j = 0; j < getReports().get(i).getNumberOfExecutedTest(); j++) {
				if (getReports().get(i).getExecutedTests().get(j).isSuccess()) {
					tests.add(getReports().get(i).getExecutedTests().get(j));
				}
			}
		}
		return tests;
	}

	public int getNumberOfSuccessTests() {
		return getSuccessTests().size();
	}

	public int getNumberOfReports() {
		return reports.size();
	}

	public Test getTestWithName(String testName) {
		for (int i = 0; i < getNumberOfReports(); i++) {
			if (getReports().get(i).getTestWithName(testName) != null) {
				return getReports().get(i).getTestWithName(testName);
			}
		}
		return null;
	}

	public Map<String, Integer> getAllTargets() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		for (int i = 0; i < getExecutedTests().size(); i++) {
			Test currentTest = getExecutedTests().get(i);
			for (int j = 0; j < currentTest.getTargets().size(); j++) {
				if (result.containsKey(currentTest.getTargets().get(j)
						.getName())) {
					result.put(currentTest.getTargets().get(j).getName(),
							result.get(currentTest.getTargets().get(j)
									.getName()) + 1);
				} else {
					result.put(currentTest.getTargets().get(j).getName(), 1);
				}
			}
		}
		return result;
	}

	public int getNumberOfTargets() {
		return getAllTargets().size();
	}

	public String getTargetName(int i) {
		String result = "";
		if (i < 0 || i >= getNumberOfTargets())
			return result;
		int j = 0;
		for (Entry<String, Integer> currentEntry : getAllTargets().entrySet()) {
			if (i == j) {
				return currentEntry.getKey();
			}
			j++;
		}
		return result;
	}

	public int getTargetNumber(int i) {
		int result = 0;
		if (i < 0 || i >= getNumberOfTargets())
			return result;
		int j = 0;
		for (Entry<String, Integer> currentEntry : getAllTargets().entrySet()) {
			if (j == i) {
				return currentEntry.getValue();
			}
			j++;
		}
		return result;
	}

	public String getTargetsGraphic() {
		String result = "";
		double percent = 0;
		double size = 0;
		List<String> couleurs = new ArrayList<String>();
		// couleurs.add("#608ec2"); //bue
		// couleurs.add("#ef2929"); //red
		couleurs.add("#99CC33"); // green
		couleurs.add("#FF9933"); // orange
		// couleurs.add("#FF9999"); //pink
		// couleurs.add("#99FFCC"); //turquoise
		couleurs.add("#B88A00");
		couleurs.add("#CC33FF");

		int indice_couleur = 0;
		int indice_target = 0;
		int t_total = 0;
		for (Entry<String, Integer> currentEntry : getAllTargets().entrySet()) {
			String col = "";
			if (indice_couleur >= couleurs.size())
				indice_couleur = 0;

			col = couleurs.get(indice_couleur);
			percent = floor(currentEntry.getValue() * 100
					/ getNumberOfExecutedTest(), 2);
			size = floor(100 / getAllTargets().size(), 2);
			t_total += size;
			if (indice_target == getAllTargets().size() - 1) {
				size += 100 - t_total;
			}
			result += "<div id=\"red2\" style=\"background-color:" + col
					+ "; width:" + size + "%\">" + currentEntry.getKey() + " ("
					+ currentEntry.getValue() + ")</div>";

			indice_couleur++;
			indice_target++;
		}
		return result;
	}

	public void setNbValuePerMetric(Map<String, Integer> nbValuePerMetric) {
		this.nbValuePerMetric = nbValuePerMetric;
	}

	public Map<String, Integer> getNbValuePerMetric() {
		return nbValuePerMetric;
	}

}
