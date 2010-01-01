package hudson.plugins.PerfPublisher.Report;

public class Hardware {

	private boolean hwa;
	private String name;
	
	public Hardware() {
		
	}
	
	public Hardware(boolean hwa, String name) {
		super();
		this.hwa = hwa;
		this.name = name;
	}
	
	
	
	/**
	 * @return the hwa
	 */
	public boolean isHwa() {
		return hwa;
	}
	
	/**
	 * @param hwa the hwa to set
	 */
	public void setHwa(boolean hwa) {
		this.hwa = hwa;
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
	
}
