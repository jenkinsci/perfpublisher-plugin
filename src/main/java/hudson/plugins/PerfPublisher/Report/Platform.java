package hudson.plugins.PerfPublisher.Report;

import java.util.ArrayList;


/**
 * Model class representing one PerfPublisher Rapport.
 *
 * @author Georges Bossert
 */

public class Platform {
	
	
	private String name;
	private boolean remote;
	private boolean capspool;
	
	private String osType;
	private String osName;
	private String osVersion;
	private String osDistribution;
	
	private ArrayList<Hardware> hardwares;
	private ArrayList<Compiler> compilers;
	private Processor processor;
	
	
	public Platform() {
		hardwares = new ArrayList<Hardware>();
		compilers = new ArrayList<Compiler>();
		processor = new Processor();
		
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
	 * @return the remote
	 */
	public boolean isRemote() {
		return remote;
	}

	/**
	 * @return the hardwares
	 */
	public ArrayList<Hardware> getHardwares() {
		return hardwares;
	}

	/**
	 * @param hard the hardware to add
	 */
	public void addHardware(Hardware hard) {
		hardwares.add(hard);
	}

	/**
	 * @param hardwares the hardwares to set
	 */
	public void setHardwares(ArrayList<Hardware> hardwares) {
		this.hardwares = hardwares;
	}





	/**
	 * @return the compilers
	 */
	public ArrayList<Compiler> getCompilers() {
		return compilers;
	}
	/**
	 * @param compiler the compiler to add
	 */
	public void addCompiler(Compiler compiler) {
		compilers.add(compiler);
	}





	/**
	 * @param compilers the compilers to set
	 */
	public void setCompilers(ArrayList<Compiler> compilers) {
		this.compilers = compilers;
	}





	/**
	 * @return the processor
	 */
	public Processor getProcessor() {
		return processor;
	}





	/**
	 * @param processor the processor to set
	 */
	public void setProcessor(Processor processor) {
		this.processor = processor;
	}





	/**
	 * @param remote the remote to set
	 */
	public void setRemote(boolean remote) {
		this.remote = remote;
	}

	/**
	 * @return the capspool
	 */
	public boolean isCapspool() {
		return capspool;
	}

	/**
	 * @param capspool the capspool to set
	 */
	public void setCapspool(boolean capspool) {
		this.capspool = capspool;
	}

	/**
	 * @return the osType
	 */
	public String getOsType() {
		return osType;
	}

	/**
	 * @param osType the osType to set
	 */
	public void setOsType(String osType) {
		this.osType = osType;
	}

	/**
	 * @return the osName
	 */
	public String getOsName() {
		return osName;
	}

	/**
	 * @param osName the osName to set
	 */
	public void setOsName(String osName) {
		this.osName = osName;
	}

	/**
	 * @return the osVersion
	 */
	public String getOsVersion() {
		return osVersion;
	}

	/**
	 * @param osVersion the osVersion to set
	 */
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	/**
	 * @return the osDistribution
	 */
	public String getOsDistribution() {
		return osDistribution;
	}

	/**
	 * @param osDistribution the osDistribution to set
	 */
	public void setOsDistribution(String osDistribution) {
		this.osDistribution = osDistribution;
	}
	
	
	
}
