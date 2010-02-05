package hudson.plugins.PerfPublisher.Report;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Model class representing one PerfPublisher Rapport.
 *
 * @author Georges Bossert
 */

public class Report {

	private String categorie;
	private String name;
	private String startDate;
	private String startDateFormat;
	private String startTime;
	private String startTimeFormat;
	private String endDate;
	private String endDateFormat;
	private String endTime;
	private String endTimeFormat;
	private String file;

	
	private ArrayList<Test> tests;
	private ArrayList<Test> executedTests;

	public Report() {
		tests = new ArrayList<Test>();
		executedTests = new ArrayList<Test>();
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndDateFormat() {
		return endDateFormat;
	}

	public void setEndDateFormat(String endDateFormat) {
		this.endDateFormat = endDateFormat;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getEndTimeFormat() {
		return endTimeFormat;
	}

	public void setEndTimeFormat(String endTimeFormat) {
		this.endTimeFormat = endTimeFormat;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	

	/**
	 * @return the tests
	 */
	public ArrayList<Test> getTests() {
		return tests;
	}

	/**
	 * @param tests the tests to set
	 */
	public void setTests(ArrayList<Test> tests) {
		for (int i=0; i<tests.size(); i++) {
			if (tests.get(i).isExecuted()) {
				this.executedTests.add(tests.get(i));
			}
		}		
		this.tests = tests;
	}
	/**
	 * @param test the test to add in the report
	 */
	public void addTest(Test test) {
		this.tests.add(test);
		if (test.isExecuted()) {
			this.executedTests.add(test);
		}
	}

	/**
	 * @return the categorie
	 */
	public String getCategorie() {
		return categorie;
	}

	/**
	 * @param categorie the categorie to set
	 */
	public void setCategorie(String categorie) {
		this.categorie = categorie;
		
	}

	/**
	 * @return the startDateFormat
	 */
	public String getStartDateFormat() {
		return startDateFormat;
	}

	/**
	 * @param startDateFormat the startDateFormat to set
	 */
	public void setStartDateFormat(String startDateFormat) {
		this.startDateFormat = startDateFormat;
	}

	/**
	 * @return the startTimeFormat
	 */
	public String getStartTimeFormat() {
		return startTimeFormat;
	}

	/**
	 * @param startTimeFormat the startTimeFormat to set
	 */
	public void setStartTimeFormat(String startTimeFormat) {
		this.startTimeFormat = startTimeFormat;
	}
	/**
	 * @return number of tests
	 */
	public int getNumberOfTest() {
		int result = 0;
		for (int i=0; i<tests.size(); i++) {
				result ++;
		}
		return result;
	}
	/**
	 * Getter for all the executed test
	 * @return an ArrayList<Test> containing all the executed test
	 */
	public ArrayList<Test> getExecutedTests() {
		if (this.executedTests==null || this.executedTests.size()==0) {
			//Verify there is no executed test
			//Support backward compatibility
			this.executedTests = new ArrayList<Test>();
			for (int i=0; i<this.tests.size(); i++) {
				if (this.tests.get(i).isExecuted()) {
					this.executedTests.add(tests.get(i));
				}
			}
		}
		return this.executedTests;
	}
	public ArrayList<Test> getNotExecutedTests() {
		ArrayList<Test> result = new ArrayList<Test>();
		for (int i=0; i<tests.size(); i++) {
			if (!tests.get(i).isExecuted()) {
				result.add(tests.get(i));
			}	
		}
		return result;
	}
	public int getNumberOfNotExecutedTest() {
		return getNotExecutedTests().size();
	}
	
	
	/**
	 * @return number of executed tests
	 */
	public int getNumberOfExecutedTest() {
		return getExecutedTests().size();
	}
	/**
	 * @return Percent of executed tests
	 */
	public double getPercentOfExecutedTest() {
		double resultat = 0;
		resultat = ((double)getNumberOfExecutedTest()/getNumberOfTest())*100;
		
		return floor(resultat, 2);
	}	
	/**
	 * @return the number of not executed test
	 */
	public int getNumberofNotExecutedTest() {		
		return getNumberOfTest()-getNumberOfExecutedTest();
	}
	/**
	 * @return percent of not executed Test
	 */
	public double getPercentOfNotExecutedTest() {
		return 100-getPercentOfExecutedTest();
	}
	
	/**
	 * @return number of passed test
	 */
	public int getNumberofPassedTest() {
		int result = 0;
		for (int i=0; i<getNumberOfExecutedTest(); i++) {
			if (getExecutedTests().get(i).isSuccessfull()) {
				result ++;
			}
		}
		return result;
	}
	/**
	 * @return percent of passed test
	 */
	public double getPercentofPassedTest() {
		double resultat = 0;
		resultat = ((double)getNumberofPassedTest()/getNumberOfExecutedTest())*100;
		return floor(resultat, 2);
	}
	
	
	/**
	 * @return number of failed test
	 */
	public int getNumberofFailedTest() {
		int result = 0;
		for (int i=0; i<getNumberOfExecutedTest(); i++) {
			if (!getExecutedTests().get(i).isSuccessfull()) {
				result ++;
			}
		}
		return result;
	}
	/**
	 * @return percent of failed tests
	 */
	public double getPercentofFailedTest() {
		return 100 - getPercentofPassedTest();
	}
	/**
	 * @return arrayList of test having a compile time define
	 */
	public ArrayList<Test> getTestsWithCompileTime() {
		ArrayList<Test> result = new ArrayList<Test>();
		for (int i=0; i<getNumberOfExecutedTest(); i++) {
			if (getExecutedTests().get(i).isCompileTime()) {
				result.add(getExecutedTests().get(i));
			}
		}
		return result;
	}
	/**
	 * @return number of tests having compile times results
	 */
	public int getNumberOfcompileTime() {
		return getTestsWithCompileTime().size();
	}
	/**
	 * @return average value of all compile time
	 */
	public double getAverageOfcompileTime() {
		double result = 0;
		for (int i=0; i<getNumberOfcompileTime(); i++) {
			result +=getTestsWithCompileTime().get(i).getCompileTime().getMeasure();
		}
		result = result / getNumberOfcompileTime();
		
		return result;
	}
	
	public Test getBestCompileTimeTest() {
		Test test = new Test();
		double result = 0.0;
		for (int i=0; i<getNumberOfcompileTime(); i++) {			
			if (i==0) {
				result = getTestsWithCompileTime().get(i).getCompileTime().getMeasure();
				test = getTestsWithCompileTime().get(i);
			}
			else if (result > getTestsWithCompileTime().get(i).getCompileTime().getMeasure()) {
				result = getTestsWithCompileTime().get(i).getCompileTime().getMeasure();
				test = getTestsWithCompileTime().get(i);
			}
		}		
		return test;
	}
	
	public CompileTime getBestCompileTime() {
		return getBestCompileTimeTest().getCompileTime();
	}
	
	/**
	 * @return name of the value having the lower compile time value
	 */
	public double getBestCompileTimeTestValue() {
		return getBestCompileTimeTest().getCompileTime().getMeasure();
	}
	/**
	 * @return name of the test having the lower compile time value
	 */
	public String getBestCompileTimeTestName() {
		return getBestCompileTimeTest().getName();
	}
	
	/**
	 * @return name of the test having the highest compile time value
	 */
	public String getWorstCompileTimeTestName() {
		String result = "";
		double temp = 0.0;
		for (int i=0; i<getNumberOfcompileTime(); i++) {			
			if (i==0) {
				temp = getTestsWithCompileTime().get(i).getCompileTime().getMeasure();
				result = getTestsWithCompileTime().get(i).getName();
			}
			else if (temp < getTestsWithCompileTime().get(i).getCompileTime().getMeasure()) {
				temp = getTestsWithCompileTime().get(i).getCompileTime().getMeasure();
				result = getTestsWithCompileTime().get(i).getName();
			}
		}
		return result;
	}
	/**
	 * @return name of the value having the highest compile time value
	 */
	public double getWorstCompileTimeTestValue() {
		double result = 0.0;
		for (int i=0; i<getNumberOfcompileTime(); i++) {			
			if (i==0) {
				result = getTestsWithCompileTime().get(i).getCompileTime().getMeasure();
			}
			else if (result < getTestsWithCompileTime().get(i).getCompileTime().getMeasure()) {
				result = getTestsWithCompileTime().get(i).getCompileTime().getMeasure();
			}
		}
		return result;
	}
	
	/**
	 * @return inferior standard deviation of the compile time average
	 */
	public double getInfStandardDeviationOfCompileTimeAverage() {
		double result = 0.0f;
		for (int i=0; i<getNumberOfcompileTime(); i++) {
			if (result < getAverageOfcompileTime()-getTestsWithCompileTime().get(i).getCompileTime().getMeasure()) {
				result = getAverageOfcompileTime()-getTestsWithCompileTime().get(i).getCompileTime().getMeasure();
			}
		}
		return result;
	}
	/**
	 * @return superior standard deviation of the compile time average
	 */
	public double getSupStandardDeviationOfCompileTimeAverage() {
		double result = 0.0f;
		for (int i=0; i<getNumberOfcompileTime(); i++) {
			if (result < getTestsWithCompileTime().get(i).getCompileTime().getMeasure()-getAverageOfcompileTime()) {
				result = getTestsWithCompileTime().get(i).getCompileTime().getMeasure()-getAverageOfcompileTime();
			}
		}
		return result;
	}
	/**
	 * @return arrayList of test having a execution time define
	 */
	public ArrayList<Test> getTestsWithExecutionTime() {
		ArrayList<Test> result = new ArrayList<Test>();
		for (int i=0; i<getNumberOfExecutedTest(); i++) {
			if (getExecutedTests().get(i).isExecutionTime()) {
				result.add(getExecutedTests().get(i));
			}
		}
		return result;
	}
	/**
	 * @return number of tests having execution times results
	 */
	public int getNumberOfExecutionTime() {
		return getTestsWithExecutionTime().size();
	}
	/**
	 * @return average value of all execution time
	 */
	public double getAverageOfExecutionTime() {
		double result = 0;
		for (int i=0; i<getNumberOfExecutionTime(); i++) {
			result +=getTestsWithExecutionTime().get(i).getExecutionTime().getMeasure();
		}
		result = result / getNumberOfExecutionTime();
		
		return result;
	}
	/**
	 * @return name of the test having the highest execution time value
	 */
	public String getWorstExecutionTimeTestName() {
		String result = "";
		double temp = 0.0;
		for (int i=0; i<getNumberOfExecutionTime(); i++) {			
			if (i==0) {
				temp = getTestsWithExecutionTime().get(i).getExecutionTime().getMeasure();
				result = getTestsWithExecutionTime().get(i).getName();
			}
			else if (temp < getTestsWithExecutionTime().get(i).getExecutionTime().getMeasure()) {
				temp = getTestsWithExecutionTime().get(i).getExecutionTime().getMeasure();
				result = getTestsWithExecutionTime().get(i).getName();
			}
		}
		return result;
	}
	/**
	 * @return name of the value having the highest execution time value
	 */
	public double getWorstExecutionTimeTestValue() {
		double result = 0.0;
		for (int i=0; i<getNumberOfExecutionTime(); i++) {			
			if (i==0) {
				result = getTestsWithExecutionTime().get(i).getExecutionTime().getMeasure();
			}
			else if (result < getTestsWithExecutionTime().get(i).getExecutionTime().getMeasure()) {
				result = getTestsWithExecutionTime().get(i).getExecutionTime().getMeasure();
			}
		}
		return result;
	}
	/**
	 * @return name of the test having the lowest execution time value
	 */
	public String getBestExecutionTimeTestName() {
		String result = "";
		double temp = 0.0;
		for (int i=0; i<getNumberOfExecutionTime(); i++) {			
			if (i==0) {
				temp = getTestsWithExecutionTime().get(i).getExecutionTime().getMeasure();
				result = getTestsWithExecutionTime().get(i).getName();
			}
			else if (temp > getTestsWithExecutionTime().get(i).getExecutionTime().getMeasure()) {
				temp = getTestsWithExecutionTime().get(i).getExecutionTime().getMeasure();
				result = getTestsWithExecutionTime().get(i).getName();
			}
		}
		return result;
	}
	/**
	 * @return name of the value having the lowest execution time value
	 */
	public double getBestExecutionTimeTestValue() {
		double result = 0.0;
		for (int i=0; i<getNumberOfExecutionTime(); i++) {			
			if (i==0) {
				result = getTestsWithExecutionTime().get(i).getExecutionTime().getMeasure();
			}
			else if (result > getTestsWithExecutionTime().get(i).getExecutionTime().getMeasure()) {
				result = getTestsWithExecutionTime().get(i).getExecutionTime().getMeasure();
			}
		}
		return result;
	}
	
	/**
	 * @return inferior standard deviation of the execution time average
	 */
	public double getInfStandardDeviationOfExecutionTimeAverage() {
		double result = 0.0f;
		for (int i=0; i<getNumberOfExecutionTime(); i++) {
			if (result < getAverageOfExecutionTime()-getTestsWithExecutionTime().get(i).getExecutionTime().getMeasure()) {
				result = getAverageOfExecutionTime()-getTestsWithExecutionTime().get(i).getExecutionTime().getMeasure();
			}
		}
		return result;
	}
	/**
	 * @return superior standard deviation of the execution time average
	 */
	public double getSupStandardDeviationOfExecutionTimeAverage() {
		double result = 0.0f;
		for (int i=0; i<getNumberOfExecutionTime(); i++) {
			if (result < getTestsWithExecutionTime().get(i).getExecutionTime().getMeasure()-getAverageOfExecutionTime()) {
				result = getTestsWithExecutionTime().get(i).getExecutionTime().getMeasure()-getAverageOfExecutionTime();
			}
		}
		return result;
	}
	
	/**
	 * @return arrayList of test having a Performance define
	 */
	public ArrayList<Test> getTestsWithPerformance() {
		ArrayList<Test> result = new ArrayList<Test>();
		for (int i=0; i<getNumberOfExecutedTest(); i++) {
			if (getExecutedTests().get(i).isPerformance()) {
				result.add(getExecutedTests().get(i));
			}
		}
		return result;
	}
	/**
	 * @return number of tests having Performance results
	 */
	public int getNumberOfPerformance() {
		return getTestsWithPerformance().size();
	}
	
	/**
	 * @return arrayList of test having a Performance define
	 */
	public ArrayList<Test> getTestsWithSuccess() {
		ArrayList<Test> result = new ArrayList<Test>();
		for (int i=0; i<getNumberOfExecutedTest(); i++) {
			if (getExecutedTests().get(i).isSuccess()) {
				result.add(getExecutedTests().get(i));
			}
		}
		return result;
	}
	/**
	 * @return number of tests having Performance results
	 */
	public int getNumberOfSuccess() {
		return getTestsWithSuccess().size();
	}
	
	/**
	 * @return average value of all Performance
	 */
	public double getAverageOfPerformance() {
		double result = 0;
		for (int i=0; i<getNumberOfPerformance(); i++) {
			result +=getTestsWithPerformance().get(i).getPerformance().getMeasure();
		}
		result = result / getNumberOfPerformance();
		
		return result;
	}
	/**
	 * @return name of the test having the highest performance value
	 */
	public String getBestPerformanceTestName() {
		String result = "";
		double temp = 0.0;
		for (int i=0; i<getNumberOfPerformance(); i++) {			
			if (i==0) {
				temp = getTestsWithPerformance().get(i).getPerformance().getMeasure();
				result = getTestsWithPerformance().get(i).getName();
			}
			else if (temp < getTestsWithPerformance().get(i).getPerformance().getMeasure()) {
				temp = getTestsWithPerformance().get(i).getPerformance().getMeasure();
				result = getTestsWithPerformance().get(i).getName();
			}
		}
		return result;
	}
	/**
	 * @return name of the value having the highest execution time value
	 */
	public double getBestPerformanceTestValue() {
		double result = 0.0;
		for (int i=0; i<getNumberOfPerformance(); i++) {			
			if (i==0) {
				result = getTestsWithPerformance().get(i).getPerformance().getMeasure();
			}
			else if (result < getTestsWithPerformance().get(i).getPerformance().getMeasure()) {
				result = getTestsWithPerformance().get(i).getPerformance().getMeasure();
			}
		}
		return result;
	}
	/**
	 * @return name of the test having the lowest performance value
	 */
	public String getWorstPerformanceTestName() {
		String result = "";
		double temp = 0.0;
		for (int i=0; i<getNumberOfPerformance(); i++) {			
			if (i==0) {
				temp = getTestsWithPerformance().get(i).getPerformance().getMeasure();
				result = getTestsWithPerformance().get(i).getName();
			}
			else if (temp > getTestsWithPerformance().get(i).getPerformance().getMeasure()) {
				temp = getTestsWithPerformance().get(i).getPerformance().getMeasure();
				result = getTestsWithPerformance().get(i).getName();
			}
		}
		return result;
	}
	/**
	 * @return name of the value having the lowest execution time value
	 */
	public double getWorstPerformanceTestValue() {
		double result = 0.0;
		for (int i=0; i<getNumberOfPerformance(); i++) {			
			if (i==0) {
				result = getTestsWithPerformance().get(i).getPerformance().getMeasure();
			}
			else if (result > getTestsWithPerformance().get(i).getPerformance().getMeasure()) {
				result = getTestsWithPerformance().get(i).getPerformance().getMeasure();
			}
		}
		return result;
	}
	
	
	/**
	 * @return inferior standard deviation of the Performance average
	 */
	public double getInfStandardDeviationOfPerformanceAverage() {
		double result = 0.0f;
		for (int i=0; i<getNumberOfPerformance(); i++) {
			if (result < getAverageOfPerformance()-getTestsWithPerformance().get(i).getPerformance().getMeasure()) {
				result = getAverageOfPerformance()-getTestsWithPerformance().get(i).getPerformance().getMeasure();
			}
		}
		return result;
	}
	/**
	 * @return superior standard deviation of the Performance average
	 */
	public double getSupStandardDeviationOfPerformanceAverage() {
		double result = 0.0f;
		for (int i=0; i<getNumberOfPerformance(); i++) {
			if (result < getTestsWithPerformance().get(i).getPerformance().getMeasure()-getAverageOfPerformance()) {
				result = getTestsWithPerformance().get(i).getPerformance().getMeasure()-getAverageOfPerformance();
			}
		}
		return result;
	}
	
	/**
	 * Round a double with n decimals
	 * @param a value to convert
	 * @param n Number of decimals
	 * @return the rounded number
	 */
	public static double floor(double a, int n) {
		double p = Math.pow(10.0, n);
		return Math.floor((a*p)+0.5) / p;
	}

	
	public int getDuration() {
		int resultat=0;
		GregorianCalendar calendar_endDate = new GregorianCalendar();
		
		calendar_endDate.set(
				Integer.parseInt(getEndDate().substring(0, 4)), 
				Integer.parseInt(getEndDate().substring(4, 6)), 
				Integer.parseInt(getEndDate().substring(6, 8)), 
				Integer.parseInt(getEndDate().substring(0, 2)), 
				Integer.parseInt(getEndDate().substring(2, 4)), 
				Integer.parseInt(getEndDate().substring(4, 6)));
		
		
		GregorianCalendar calendar_startDate = new GregorianCalendar();
		
		calendar_startDate.set(
				Integer.parseInt(getStartDate().substring(0, 4)), 
				Integer.parseInt(getStartDate().substring(4, 6)), 
				Integer.parseInt(getStartDate().substring(6, 8)), 
				Integer.parseInt(getStartDate().substring(0, 2)), 
				Integer.parseInt(getStartDate().substring(2, 4)), 
				Integer.parseInt(getStartDate().substring(4, 6)));
		return resultat;
	}
	
	/**
	 * @return the string definition of the report
	 */
	public String toString() {
		String resultat=null;
		resultat = "Categorie : "+categorie;
		resultat += "\nName : "+name;
		resultat += "\nStart date ("+startDateFormat+") : "+startDate;
		resultat += "\nStart time ("+startTimeFormat+") : "+startTime;
		resultat += "\n----------------------------------------------";	
		for (int i=0; i<tests.size(); i++) {
			resultat += "\n----------------------------------------------";
			resultat += "\nTest name : "+tests.get(i).getName();
			for (int j=0; j<tests.get(i).getCommandLine().size(); j++) {
				resultat += "\nCommand line : "+tests.get(i).getCommandLine().get(j).getCommand() + " ("+tests.get(i).getCommandLine().get(j).getTime()+")";
			}
			
			resultat += "\nState success : "+tests.get(i).getSuccess().getState();			
		}		
		return resultat;
	}

	
	
	public int getNumberOfSuccessTest() {
		return getTestsWithSuccess().size();
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}

	public Test getTestWithName(String testName) {
		for (int i=0; i<getNumberOfTest(); i++) {
			if (getTests().get(i).getName().equals(testName)) {
				return getTests().get(i);
			}
		}
		return null;
	}
}
