package hudson.plugins.PerfPublisher.matrixBuild;

import hudson.plugins.PerfPublisher.Report.ReportContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * This class encapsulates all the sub-builds results in it.
 * 
 * @author gbossert
 *
 */
public class PerfPublisherMatrixBuild {
	/**
	 * Number of the matrix build
	 */
	private int nbBuild;
	/**
	 * A list of all the sub-builds
	 */
	private List<PerfPublisherMatrixSubBuild> children;
	/**
	 * Create a new matrix build based on its build number
	 * @param nbBuild number of the build
	 */
	public PerfPublisherMatrixBuild(int nbBuild) {
		this.nbBuild = nbBuild;
		children = new ArrayList<PerfPublisherMatrixSubBuild>();
	}
	/**
	 * Getter for the list of sub-builds
	 * @return the list of sub-builds
	 */
	public List<PerfPublisherMatrixSubBuild> getSubBuilds() {
		return this.children;
	}
	/**
	 * Add a sub-build to this matrix
	 * @param child the sub-build to add
	 */
	public void addSubBuild(PerfPublisherMatrixSubBuild child) {		
		children.add(child);
	}
	/**
	 * Return the appropriate report for a list of combination
	 * @param combination List of combination
	 * @return the tests report
	 */
	public ReportContainer getReportForCombination(Map<String,String> combination) {
		ReportContainer result = null;
		for (int i=0; i<children.size(); i++) {
			if (children.get(i).hasCombination(combination)) {
				return children.get(i).getReport();
			}
		}
		return result;
	}
	/**
	 * Return all the different axis of this Matrix Build
	 * @return a list of all the axis
	 */
	public List<String> getAxis() {
		List<String> result = new ArrayList<String>();
		for (int i=0; i<children.size(); i++) {
			for (int j=0; j<children.get(i).getAxis().size(); j++) {
				if (!result.contains(children.get(i).getAxis().get(j))) {
					result.add(children.get(i).getAxis().get(j));
				}
			}
		}		
		return result;
	}

	public List<String> getAxisValues(String axe) {
		List<String> result = new ArrayList<String>();
		for (int i=0; i<children.size(); i++) {
			for (int j=0; j<children.get(i).getAxisValues(axe).size(); j++) {
				if (!result.contains(children.get(i).getAxisValues(axe).get(j))) {
					result.add(children.get(i).getAxisValues(axe).get(j));
				}
			}
		}
		return result;
	}

	public int getNbCombinations() {
		return this.children.size();
	}

	public int getNbAxis() {
		return this.getAxis().size();
	}
	
	
}
