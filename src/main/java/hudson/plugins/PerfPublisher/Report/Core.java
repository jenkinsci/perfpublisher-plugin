package hudson.plugins.PerfPublisher.Report;

public class Core {

	private int proc;
	private int coreid;
	private int physid;
	
	public Core() {
		
	}
	
	public Core(int proc, int coreid, int physid) {
		super();
		this.proc = proc;
		this.coreid = coreid;
		this.physid = physid;
	}
	/**
	 * @return the proc
	 */
	public int getProc() {
		return proc;
	}
	/**
	 * @param proc the proc to set
	 */
	public void setProc(int proc) {
		this.proc = proc;
	}
	/**
	 * @return the coreid
	 */
	public int getCoreid() {
		return coreid;
	}
	/**
	 * @param coreid the coreid to set
	 */
	public void setCoreid(int coreid) {
		this.coreid = coreid;
	}
	/**
	 * @return the physid
	 */
	public int getPhysid() {
		return physid;
	}
	/**
	 * @param physid the physid to set
	 */
	public void setPhysid(int physid) {
		this.physid = physid;
	}
	
	
}
