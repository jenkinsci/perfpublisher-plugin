package hudson.plugins.PerfPublisher.Report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Model class representing one Test result.
 *
 * @author Georges Bossert
 */
public class Test implements java.lang.Comparable<Test> {
  
	private String name;
	private String description;
	private ArrayList<Target> targets;
	private String message;
	private boolean executed;
	
	private ArrayList<Source> sources;
	private DataSet dataSetIn;
	private DataSet dataSetOut;
	
	private ArrayList<CommandLine> commandLine;
	private ArrayList<Param> parameters;
	
	private Success success;
	private CompileTime compileTime;
	private ExecutionTime executionTime;
	private Performance performance;
	
	private List<Log> logs;
	
	private Platform plateforme;
	
	private boolean isPerformance;
	

	private boolean isCompileTime;
	private boolean isExecutionTime;
	private boolean isSuccess;
	
	private Map<String, Double> metrics;
	
	public Test() {
		targets = new ArrayList<Target>();
		sources = new ArrayList<Source>();
		commandLine = new ArrayList<CommandLine>();
		parameters = new ArrayList<Param>();
		success = new Success();
		compileTime = new CompileTime();
		executionTime = new ExecutionTime();
		performance = new Performance();
		logs = new ArrayList<Log>();
		metrics = new HashMap<String, Double>();

	}

	/**
	 * @param name
	 * @param executed
	 * @param sources
	 * @param dataSetIn
	 * @param dataSetOut
	 * @param commandLine
	 * @param parameters
	 * @param success
	 * @param compileTime
	 * @param executionTime
	 * @param performance
	 */
	public Test(String name, String message, String description, boolean executed, ArrayList<Source> sources,
			DataSet dataSetIn, DataSet dataSetOut,
			ArrayList<CommandLine> commandLine, ArrayList<Param> parameters,
			Success success, CompileTime compileTime,
			ExecutionTime executionTime, Performance performance) {
		this.name = name;
		this.message=message;
		this.description=description;
		this.executed = executed;
		this.sources = sources;
		this.dataSetIn = dataSetIn;
		this.dataSetOut = dataSetOut;
		this.commandLine = commandLine;
		this.parameters = parameters;
		this.success = success;
		this.compileTime = compileTime;
		this.executionTime = executionTime;
		this.performance = performance;	
		this.targets= new ArrayList<Target>();
	}
	
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	public String getShortedMessage() {
		if (message == null || message.isEmpty()) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		String tmp  = message;
		
		
		while (tmp.length()>90) {
			result.append(tmp.substring(0,90));
			result.append("<br />");
			tmp = tmp.substring(90);
		}
		result.append(tmp);
		
		return result.toString();
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	public String getAdaptedName() {
		if (name==null || name.isEmpty()) {
			return name;
		}
		StringBuilder result = new StringBuilder();
		if (name.length()>60) {
			result.append(name.substring(0,27));
			result.append("[...]");
			result.append(name.substring(name.length()-27, name.length()));
			result.append("<span>");
			result.append(name);
			result.append("</span>");			
		} else {
			result.append(name);
		}
		return result.toString();
	}
	
	public String getNameForUrl() {
		String result = name.replace("/", "..");
		return result;
	}
	public static String ResolveTestNameInUrl(String name) {
		String result = name.replace("..", "/");
		return result;
	}

	/**
	 * @return the targets
	 */
	public ArrayList<Target> getTargets() {
		return targets;
	}
	/**
	 * @param targets the targets to set
	 */
	public void setTargets(ArrayList<Target> targets) {
		this.targets = targets;
	}

	public void addTarget(Target target) {
		this.targets.add(target);
	}
	public String getHtmlDefinitionOfTargets(){
		StringBuilder strb = new StringBuilder();
		strb.append("<small>");
		for (int i=0; i<targets.size(); i++) {
			strb.append("<b>"+targets.get(i).getName()+"</b>");
			if (i<targets.size()-1) {
				strb.append("-");
			}
		}
		strb.append("</small>");
		return strb.toString();
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the executed
	 */
	public boolean isExecuted() {
		return executed;
	}
	

	/**
	 * @return the logs
	 */
	public List<Log> getLogs() {
		return logs;
	}

	/**
	 * @param logs the logs to set
	 */
	public void setLogs(List<Log> logs) {
		this.logs = logs;
	}
	
	public void addLog(Log log) {
		this.logs.add(log);
	}

	/**
	 * @param executed the executed to set
	 */
	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	/**
	 * @return the sources
	 */
	public ArrayList<Source> getSources() {
		return sources;
	}
	/**
	 * @param source the source to add
	 */
	public void addSource(Source source) {
		sources.add(source);
	}
	
	/**
	 * @param sources the sources to set
	 */
	public void setSources(ArrayList<Source> sources) {
		this.sources = sources;
	}

	/**
	 * @return the dataSetIn
	 */
	public DataSet getDataSetIn() {
		return dataSetIn;
	}
	
	/**
	 * @param dataSetIn the dataSetIn to set
	 */
	public void setDataSetIn(DataSet dataSetIn) {
		this.dataSetIn = dataSetIn;
	}

	/**
	 * @return the dataSetOut
	 */
	public DataSet getDataSetOut() {
		return dataSetOut;
	}

	/**
	 * @param dataSetOut the dataSetOut to set
	 */
	public void setDataSetOut(DataSet dataSetOut) {
		this.dataSetOut = dataSetOut;
	}
	
	/**
	 * @return the commandLine
	 */
	public ArrayList<CommandLine> getCommandLine() {
		return commandLine;
	}
	/**
	 * @param cmdLine the CommandLine to add
	 */
	public void addCommandLine(CommandLine cmdLine) {
		this.commandLine.add(cmdLine);
	}
	
	/**
	 * @param commandLine the commandLine to set
	 */
	public void setCommandLine(ArrayList<CommandLine> commandLine) {
		this.commandLine = commandLine;
	}

	/**
	 * @return the parameters
	 */
	public ArrayList<Param> getParameters() {
		return parameters;
	}
	/**
	 * @param param add a parameter
	 */
	public void addParameter(Param param) {
		this.parameters.add(param);
	}
	
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(ArrayList<Param> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the success
	 */
	public Success getSuccess() {
		return success;
	}

	/**
	 * @param success the success to set
	 */
	public void setSuccess(Success success) {
		this.success = success;
	}

	/**
	 * @return the compileTime
	 */
	public CompileTime getCompileTime() {
		return compileTime;
	}
	
	/**
	 * @param compileTime the compileTime to set
	 */
	public void setCompileTime(CompileTime compileTime) {
		this.compileTime = compileTime;
	}

	/**
	 * @return the executionTime
	 */
	public ExecutionTime getExecutionTime() {
		return executionTime;
	}

	/**
	 * @param executionTime the executionTime to set
	 */
	public void setExecutionTime(ExecutionTime executionTime) {
		this.executionTime = executionTime;
	}

	/**
	 * @return the performance
	 */
	public Performance getPerformance() {
		return performance;
	}

	/**
	 * @param performance the performance to set
	 */
	public void setPerformance(Performance performance) {
		this.performance = performance;
	}
	
	public boolean isSuccessfull() {
		return success.isPassed();
	}
	public boolean isHasTimedOut() {
		return success.isHasTimedOut();
	}
	public boolean isHasNotTimedOutButHasFailed() {
		return (!success.isHasTimedOut() && !success.isPassed());
	}

	/**
	 * @return the isPerformance
	 */
	public boolean isPerformance() {
		return isPerformance;
	}

	/**
	 * @param isPerformance the isPerformance to set
	 */
	public void setIsPerformance(boolean isPerformance) {
		this.isPerformance = isPerformance;
	}

	/**
	 * @return the isCompileTime
	 */
	public boolean isCompileTime() {
		return isCompileTime;
	}

	/**
	 * @param isCompileTime the isCompileTime to set
	 */
	public void setIsCompileTime(boolean isCompileTime) {
		this.isCompileTime = isCompileTime;
	}

	/**
	 * @return the isExecutionTion
	 */
	public boolean isExecutionTime() {
		return isExecutionTime;
	}

	/**
	 * @param isExecutionTion the isExecutionTion to set
	 */
	public void setIsExecutionTime(boolean isExecutionTime) {
		this.isExecutionTime = isExecutionTime;
	}

	/**
	 * @return the isSuccess
	 */
	public boolean isSuccess() {
		return isSuccess;
	}

	/**
	 * @param isSuccess the isSuccess to set
	 */
	public void setIsSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	/**
	 * @return the plateforme
	 */
	public Platform getPlateforme() {
		return plateforme;
	}

	/**
	 * @param plateforme the plateforme to set
	 */
	public void setPlateforme(Platform plateforme) {
		this.plateforme = plateforme;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Map<String, Double> getMetrics() {
		return metrics;
	}

	public void setMetrics(Map<String, Double> metrics) {
		this.metrics = metrics;
	}
	
	
	
	@Override
	public String toString() {
		StringBuffer strb = new StringBuffer();
		strb.append("Test "+getName());
		strb.append("\n Description of the platform :"+plateforme.getName());
		strb.append("\n Os Name :"+plateforme.getOsName());
		strb.append("\n---------------------------------------");
		if (isCompileTime) {
			strb.append("\nCompile Time : "+compileTime.getMeasure()+" "+compileTime.getUnit());
		}
		if (isExecutionTime) {
			strb.append("\nExecution Time : "+executionTime.getMeasure()+" "+executionTime.getUnit());	
		}
		if (isPerformance) {
			strb.append("\nPerformance : "+performance.getMeasure()+" "+performance.getUnit());	
		}
		if (isSuccess) {
			strb.append("\nSuccess State : "+success.getState()+" %");
		}
		if (metrics.size()>0) {
			strb.append("\nYour metrics : ");
			for (String metric_name : this.metrics.keySet()) {
				strb.append("\n"+metric_name+" : "+this.metrics.get(metric_name));
			}
		}
		
		return strb.toString();
	}

	public int compareTo(Test test) {
		return this.name.compareTo(test.getName());
	}

	

	
	
	
	
}
