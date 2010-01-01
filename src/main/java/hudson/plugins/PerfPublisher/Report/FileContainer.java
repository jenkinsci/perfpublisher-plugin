package hudson.plugins.PerfPublisher.Report;

import java.util.ArrayList;

public class FileContainer extends ResultContainer {

	
	public FileContainer() {
		reports = new ArrayList<Report>();
	}
	
	/**
	 * Adding a report into Container
	 * @param report
	 */
	public void addReport(Report report) {		
		reports.add(report);
	}
	
	
	
	public ArrayList<Report> getReports() {
		return reports;
	}	
	
	
	public int getNumberOfFiles(){
		return reports.size();
	}

	/**
	 * @return number of tests
	 */
	public int getNumberOfTest() {
		int result = 0; 
		for (int i=0; i<getNumberOfFiles(); i++) {
			result+=getReports().get(i).getNumberOfTest();
		}
		return result;
	}

	

	

}
