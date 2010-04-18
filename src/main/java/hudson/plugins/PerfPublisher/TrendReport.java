package hudson.plugins.PerfPublisher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import hudson.plugins.PerfPublisher.Report.FileContainer;
import hudson.plugins.PerfPublisher.Report.Report;
import hudson.plugins.PerfPublisher.Report.ReportContainer;
import hudson.plugins.PerfPublisher.Report.ResultContainer;
import hudson.plugins.PerfPublisher.Report.Test;

/**
 * Class representation of trend between two reports
 * 
 * @author Lapeluche
 * 
 */
public class TrendReport {

	private ReportContainer actualResult;
	private ReportContainer oldResult;

	public TrendReport(ReportContainer actualReport, ReportContainer oldReport) {
		this.actualResult = actualReport;
		this.oldResult = oldReport;
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
	 * @return the actual report
	 */
	public ReportContainer getActualReportContainer() {
		return actualResult;
	}

	/**
	 * @param actualReport
	 *            the actual report
	 */
	public void setActualReportContainer(ReportContainer actualReportContainer) {
		this.actualResult = actualReportContainer;
	}

	/**
	 * @return the old report
	 */
	public ReportContainer getOldReport() {
		return oldResult;
	}

	/**
	 * @param oldReport
	 *            the old report
	 */
	public void setOldReport(ReportContainer oldReportContainer) {
		this.oldResult = oldReportContainer;
	}

	/**
	 * @return true if the number of test has increased
	 */
	public boolean isNumberOfTestHasIncrease() {
		return (actualResult.getNumberOfTest() > oldResult.getNumberOfTest());
	}

	/**
	 * @return true if the number of test has decrease
	 */
	public boolean isNumberOfTestHasDecrease() {
		return (actualResult.getNumberOfTest() < oldResult.getNumberOfTest());
	}

	/**
	 * @return true if the number of test is stable
	 */
	public boolean isNumberOfTestStable() {
		return (actualResult.getNumberOfTest() == oldResult.getNumberOfTest());
	}

	/**
	 * @return true if the percent of passed test has increase
	 */
	public boolean isPercentOfPassedTestHasIncrease() {
		return (actualResult.getPercentOfPassedTest() > oldResult
				.getPercentOfPassedTest());
	}

	/**
	 * @return true if the percent of passed test has decrease
	 */
	public boolean isPercentOfPassedTestHasDecrease() {
		return (actualResult.getPercentOfPassedTest() < oldResult
				.getPercentOfPassedTest());
	}

	/**
	 * @return true if the percent of passed test is stable
	 */
	public boolean isPercentOfPassedTestStable() {
		return (actualResult.getPercentOfPassedTest() == oldResult
				.getPercentOfPassedTest());
	}

	/**
	 * @return true if the number of executed test has increased
	 */
	public boolean isNumberOfExecutedTestHasIncreased() {
		return (actualResult.getNumberOfExecutedTest() > oldResult
				.getNumberOfExecutedTest());
	}

	/**
	 * @return true if the number of executed test has decrease
	 */
	public boolean isNumberOfExecutedTestHasDecrease() {
		return (actualResult.getNumberOfExecutedTest() < oldResult
				.getNumberOfExecutedTest());
	}

	/**
	 * @return true if the number of executed test is stable
	 */
	public boolean isNumberOfExecutedTestStable() {
		return (actualResult.getNumberOfExecutedTest() == oldResult
				.getNumberOfExecutedTest());
	}

	/**
	 * @return true if the percent of executed test has increased
	 */
	public boolean isPercentOfExecutedTestHasIncreased() {
		return (actualResult.getPercentOfExecutedTest() > oldResult
				.getPercentOfExecutedTest());
	}

	/**
	 * @return true if the number of not executed test has increased
	 */
	public boolean isNumberOfNotExecutedTestHasIncreased() {
		return (actualResult.getNumberOfNotExecutedTest() > oldResult
				.getNumberOfNotExecutedTest());
	}

	/**
	 * @return true if the number of not executed test has decrease
	 */
	public boolean isNumberOfNotExecutedTestHasDecrease() {
		return (actualResult.getNumberOfNotExecutedTest() < oldResult
				.getNumberOfNotExecutedTest());
	}

	/**
	 * @return true if the number of not executed test is stable
	 */
	public boolean isNumberOfNotExecutedTestStable() {
		return (actualResult.getNumberOfNotExecutedTest() == oldResult
				.getNumberOfNotExecutedTest());
	}

	/**
	 * @return true if the percent of not executed test has increased
	 */
	public boolean isPercentOfNotExecutedTestHasIncreased() {
		return (actualResult.getPercentOfNotExecutedTest() > oldResult
				.getPercentOfNotExecutedTest());
	}

	/**
	 * @return true if the percent of passed test has increase
	 */
	public boolean isPercentOfFailedTestHasIncrease() {
		return (actualResult.getPercentOfFailedTest() > oldResult
				.getPercentOfFailedTest());
	}

	/**
	 * @return true if the percent of passed test has decrease
	 */
	public boolean isPercentOfFailedTestHasDecrease() {
		return (actualResult.getPercentOfFailedTest() < oldResult
				.getPercentOfFailedTest());
	}

	/**
	 * @return true if the percent of passed test is stable
	 */
	public boolean isPercentOfFailedTestStable() {
		return (actualResult.getPercentOfFailedTest() == oldResult
				.getPercentOfFailedTest());
	}

	/**
	 * @return true if the average compile time has increase
	 */
	public boolean isAverageOfCompileTimeHasIncrease() {
		return (actualResult.getAverageOfCompileTime() > oldResult
				.getAverageOfCompileTime());
	}

	/**
	 * @return true if the average compile time has decrease
	 */
	public boolean isAverageOfCompileTimeHasDecrease() {
		return (actualResult.getAverageOfCompileTime() < oldResult
				.getAverageOfCompileTime());
	}

	/**
	 * @return true if the average compile time is stable
	 */
	public boolean isAverageOfCompileTimeStable() {
		return (actualResult.getAverageOfCompileTime() == oldResult
				.getAverageOfCompileTime());
	}

	/**
	 * @return true if the average execution time has increase
	 */
	public boolean isAverageOfExecutionTimeHasIncrease() {
		return (actualResult.getAverageOfExecutionTime() > oldResult
				.getAverageOfExecutionTime());
	}

	/**
	 * @return true if the average execution time has decrease
	 */
	public boolean isAverageOfExecutionTimeHasDecrease() {
		return (actualResult.getAverageOfExecutionTime() < oldResult
				.getAverageOfExecutionTime());
	}

	/**
	 * @return true if the average execution time is stable
	 */
	public boolean isAverageOfExecutionTimeStable() {
		return (actualResult.getAverageOfExecutionTime() == oldResult
				.getAverageOfExecutionTime());
	}

	/**
	 * @return true if the average performance has increase
	 */
	public boolean isAverageOfPerformanceHasIncrease() {
		return (actualResult.getAverageOfPerformance() > oldResult
				.getAverageOfPerformance());
	}

	/**
	 * @return true if the average performance has decrease
	 */
	public boolean isAverageOfPerformanceHasDecrease() {
		return (actualResult.getAverageOfPerformance() < oldResult
				.getAverageOfPerformance());
	}

	/**
	 * @return true if the average performance is stable
	 */
	public boolean isAverageOfPerformanceStable() {
		return (actualResult.getAverageOfPerformance() == oldResult
				.getAverageOfPerformance());
	}

	public ArrayList<Test> getNewTests() {
		ArrayList<Test> result = new ArrayList<Test>();
		for (int i = 0; i < actualResult.getTests().size(); i++) {
			if (oldResult.getTestWithName(actualResult.getTests().get(i)
					.getName()) == null) {
				result.add(actualResult.getTests().get(i));
			}
		}
		return result;
	}

	public int getNumberOfNewTests() {
		return getNewTests().size();
	}

	public double getPercentOfNewTests() {
		double resultat = 0;
		resultat = (double) getNumberOfNewTests()
				/ (double) actualResult.getNumberOfTest() * 100.0;
		return floor(resultat, 2);
	}

	public int getNumberOfDeletedTests() {
		return getDeletedTests().size();
	}

	public ArrayList<Test> getDeletedTests() {
		ArrayList<Test> result = new ArrayList<Test>();
		for (int i = 0; i < oldResult.getTests().size(); i++) {
			if (actualResult.getTestWithName(oldResult.getTests().get(i)
					.getName()) == null) {
				result.add(oldResult.getTests().get(i));
			}
		}
		return result;
	}

	public double getPercentOfDeletedTests() {
		double resultat = 0;
		resultat = (double) getNumberOfDeletedTests()
				/ (double) actualResult.getNumberOfTest() * 100.0;
		return floor(resultat, 2);
	}

	
	
	
	
	
	

	public List<Test> getExecutionStatusChangedTests() {
		List<Test> result = new ArrayList<Test>();
		for (int i = 0; i < actualResult.getTests().size(); i++) {
			Test currentTest = actualResult.getTests().get(i);
			Test oldTest = oldResult.getTestWithName(currentTest.getName());
			if (oldTest != null) {
				if (oldTest.isExecuted() != currentTest.isExecuted()) {
					result.add(currentTest);
				} 
			}
		}
		return result;
	}
	public int getNumberOfExecutionStatusChangedTests() {
		return this.getExecutionStatusChangedTests().size();
	}

	public double getPercentOfExecutionStatusChangedTests() {
		double resultat = 0;
		resultat = (double) getNumberOfExecutionStatusChangedTests()
				/ (double) actualResult.getNumberOfTest() * 100.0;
		return floor(resultat, 2);
	}
	public List<Test> getSuccessStatusChangedTests() {
		List<Test> result = new ArrayList<Test>();
		for (int i = 0; i < actualResult.getTests().size(); i++) {
			Test currentTest = actualResult.getTests().get(i);
			Test oldTest = oldResult.getTestWithName(currentTest.getName());
			if (oldTest != null) {
				if (oldTest.isSuccessfull() != currentTest.isSuccessfull()) {
					result.add(currentTest);
				} 
			}
		}
		return result;
	}
	public int getNumberOfSuccessStatusChangedTests() {
		return this.getSuccessStatusChangedTests().size();
	}

	public double getPercentOfSuccessStatusChangedTests() {
		double resultat = 0;
		resultat = (double) getNumberOfSuccessStatusChangedTests()
				/ (double) actualResult.getNumberOfTest() * 100.0;
		return floor(resultat, 2);
	}

	public boolean containsMetrics(String metricName) {
		Map<String,String> old_metrics = this.oldResult.getMetricsName();
		if (null == old_metrics) {
			return false;
		}
		
		return (this.oldResult.getAverageValuePerMetrics().get(metricName)!=null);
	}

	public boolean isAverageOfMetricValueHasIncreased(String metricName) {
		if (!containsMetrics(metricName)) {
			return false;
		}
		return (this.actualResult.getAverageValuePerMetrics().get(metricName) > this.oldResult.getAverageValuePerMetrics().get(metricName));
	}
	
	public boolean isAverageOfMetricValueHasDecreased(String metricName) {
		if (!containsMetrics(metricName)) {
			return false;
		}
		return (this.actualResult.getAverageValuePerMetrics().get(metricName) < this.oldResult.getAverageValuePerMetrics().get(metricName));
	}

}
