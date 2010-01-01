package hudson.plugins.PerfPublisher.Report;

public class Target {
	String name;
	Boolean threaded;
	
	public Target() {
		this.name="";
		this.threaded=false;
	}	
	public Target(String name, Boolean threaded) {
		this.name=name;
		this.threaded=threaded;
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
	 * @return the threaded
	 */
	public Boolean isThreaded() {
		return threaded;
	}
	/**
	 * @param threaded the threaded to set
	 */
	public void setThreaded(Boolean threaded) {
		this.threaded = threaded;
	}
	public String getStringThreaded() {
		if (this.threaded) {
			return "threaded";
		} else {
			return "not threaded";
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Target) {
			return false;
		} else {
			Target tar = (Target) obj;
			if (tar.getName().equalsIgnoreCase(this.getName())) {
				return true;
			} else {
				return false;
			}
		}
	}

	
	
}
