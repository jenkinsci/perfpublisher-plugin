package hudson.plugins.PerfPublisher.Report;

import java.util.ArrayList;

public class ErrorMessage {

	private String message;
	private int numberOfTest;
	private ArrayList<Test> tests;
	
	public ErrorMessage() {
		// TODO Auto-generated constructor stub
		tests=new ArrayList<Test>();
	}
	
	public ErrorMessage(String description) {
		message = description;
		tests=new ArrayList<Test>();
		numberOfTest=1;
	}
	
	public void addTest(Test test) {
		tests.add(test);
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the numberOfTest
	 */
	public int getNumberOfTest() {
		return numberOfTest;
	}
	/**
	 * @param numberOfTest the numberOfTest to set
	 */
	public void setNumberOfTest(int numberOfTest) {
		this.numberOfTest = numberOfTest;
	}
	
	public ArrayList<Test> getTests() {
		return tests;
	}
	
	
	
	
}
