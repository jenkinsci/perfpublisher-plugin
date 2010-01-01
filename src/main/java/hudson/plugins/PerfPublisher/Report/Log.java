package hudson.plugins.PerfPublisher.Report;

public class Log {

	private String name;
	private String log;
	
	public Log(String name, String log) {
		super();
		this.name = name;
		this.log = log;
	}
	
	public Log(String name) {
		super();
		this.name = name;
		this.log = "";
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
	 * @return the log
	 */
	public String getLog() {
		return log.replaceAll("\n", "<br />");
	}

	/**
	 * @param log the log to set
	 */
	public void setLog(String log) {
		this.log = log;
	}
	
	
	
	
}

