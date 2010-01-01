package hudson.plugins.PerfPublisher.Report;

public class Compiler {

	private String name;
	private String version;
	private String path;
	
	
	
	public Compiler() {
		
	}
	public Compiler(String name, String version, String path) {
		super();
		this.name = name;
		this.version = version;
		this.path = path;
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
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}



	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}



	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}



	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
}
