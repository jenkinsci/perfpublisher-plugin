package hudson.plugins.PerfPublisher.Report;

import java.util.ArrayList;

public class Processor {

	private String procArch;
	private String procFreq;
	private String procFreqUnit;
	
	private ArrayList<Core> cores;

	public Processor() {
		cores = new ArrayList<Core>();
	}
	
	
	public Processor(String procArch, String procFreq, String procFreqUnit,
			ArrayList<Core> cores) {
		super();
		this.procArch = procArch;
		this.procFreq = procFreq;
		this.procFreqUnit = procFreqUnit;
		this.cores = cores;
	}

	/**
	 * @return the procArch
	 */
	public String getProcArch() {
		return procArch;
	}

	/**
	 * @param procArch the procArch to set
	 */
	public void setProcArch(String procArch) {
		this.procArch = procArch;
	}

	/**
	 * @return the procFreq
	 */
	public String getProcFreq() {
		return procFreq;
	}

	/**
	 * @param procFreq the procFreq to set
	 */
	public void setProcFreq(String procFreq) {
		this.procFreq = procFreq;
	}

	/**
	 * @return the procFreqUnit
	 */
	public String getProcFreqUnit() {
		return procFreqUnit;
	}

	/**
	 * @param procFreqUnit the procFreqUnit to set
	 */
	public void setProcFreqUnit(String procFreqUnit) {
		this.procFreqUnit = procFreqUnit;
	}

	/**
	 * @return the cores
	 */
	public ArrayList<Core> getCores() {
		return cores;
	}

	/**
	 * @param cores the cores to set
	 */
	public void setCores(ArrayList<Core> cores) {
		this.cores = cores;
	}
	/**
	 * @param core the core to add
	 */
	public void addCore(Core core) {
		cores.add(core);
	}
	
	
}
