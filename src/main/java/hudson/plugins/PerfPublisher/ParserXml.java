/**
 * Hudson PerfPublisher plugin
 *
 * @author Georges Bossert <gbossert@gmail.com>
 */
package hudson.plugins.PerfPublisher;

import hudson.plugins.PerfPublisher.Report.CommandLine;
import hudson.plugins.PerfPublisher.Report.CompileTime;
import hudson.plugins.PerfPublisher.Report.Compiler;
import hudson.plugins.PerfPublisher.Report.Core;
import hudson.plugins.PerfPublisher.Report.DataSet;
import hudson.plugins.PerfPublisher.Report.ExecutionTime;
import hudson.plugins.PerfPublisher.Report.Hardware;
import hudson.plugins.PerfPublisher.Report.Log;
import hudson.plugins.PerfPublisher.Report.Param;
import hudson.plugins.PerfPublisher.Report.Performance;
import hudson.plugins.PerfPublisher.Report.Platform;
import hudson.plugins.PerfPublisher.Report.Processor;
import hudson.plugins.PerfPublisher.Report.Report;
import hudson.plugins.PerfPublisher.Report.Source;
import hudson.plugins.PerfPublisher.Report.Success;
import hudson.plugins.PerfPublisher.Report.Target;
import hudson.plugins.PerfPublisher.Report.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ParserXml {

	static class Analyse extends DefaultHandler {
		private Report report;
		private Collection<String> metrics_name;
		private Map<String, Double> metrics;
		
		private StringBuffer buffer;
		
		private Platform tmp_platform;
		
		private Test tmp_test;
		
		private Source tmp_source;
		
		private DataSet tmp_dataset;
		
		private CommandLine tmp_cmdline;
		
		private Param tmp_param;
		
		private Success tmp_success;
		
		private CompileTime tmp_compiletime;
		
		private ExecutionTime tmp_executiontime;
		
		private Performance tmp_performance;
		
		private Processor tmp_processor;
		
		private Core tmp_core;
		
		private Hardware tmp_hardware;
		
		private Log tmp_log;
		
		private Target tmp_target;
		
		private hudson.plugins.PerfPublisher.Report.Compiler tmp_compiler;
		
		
		/**
		 * FLAGS
		 */
		private boolean f_report	= false;
		private boolean f_start		= false;	
		private boolean f_start_date	= false;
		private boolean f_start_time	= false;
		private boolean f_platform	= false;
		private boolean f_os	= false;
		private boolean f_os_type = false;
		private boolean f_os_name = false;
		private boolean f_os_version = false;
		private boolean f_os_distribution = false;
		private boolean f_processor = false;
		private boolean f_frequency = false;
		private boolean f_core = false;
		private boolean f_hardware = false;
		private boolean f_compiler = false;
		private boolean f_test	= false;
		private boolean f_description = false;
		private boolean f_targets = false;
		private boolean f_target = false;
		private boolean f_errorlog = false;
		private boolean f_source = false;
		private boolean f_dataset = false;
		private boolean f_commandline = false;
		private boolean f_param = false;
		private boolean f_result = false;
		private boolean f_success = false;
		private boolean f_compiletime = false;
		private boolean f_executiontime = false;
		private boolean f_metrics = false;
		private boolean f_performance = false;
		private boolean f_end = false;
		private boolean f_end_date = false;
		private boolean f_end_time = false;
		private boolean f_log = false;
		
		
		/**
		 * Analyse
		 */
		public Analyse(Collection<String> metrics) {
			super();
			report = new Report();
			tmp_platform = new Platform();
			this.metrics_name = metrics;			
		}

		/**
		 * @param ch,
		 *            start, length
		 */
		@Override
		public void characters(final char[] ch, final int start,
				final int length) throws SAXException {

			final String lecture = new String(ch, start, length);
			if (buffer != null) {
				buffer.append(lecture);
			}
		}

		/**
		 * 
		 */
		@Override
		public void endDocument() throws SAXException {
			resultat = report;
		}

		/**
		 * @param uri,
		 *            localName, qName
		 */
		@Override
		public void endElement(final String uri, final String localName,
				final String qName) throws SAXException {

			if (qName.equals("report") && f_report) {
				buffer = new StringBuffer();
				f_report = false;
			} else if (qName.equals("start") && f_report && f_start) {
				f_start = false;
				buffer = new StringBuffer();
			} else if (qName.equals("date") && f_report && f_start && f_start_date) {
				f_start_date = false;
				buffer = new StringBuffer();
			} else if (qName.equals("time") && f_report && f_start && f_start_time) {
				f_start_time = false;
				buffer = new StringBuffer();
			} else if (qName.equals("test") && f_report && f_test) {
				f_test = false;
				report.addTest(tmp_test);
				buffer = new StringBuffer();
			} else if (qName.equals("description") && f_report && f_test && f_description) {
				f_description = false;
				tmp_test.setDescription(buffer.toString());
				buffer = new StringBuffer();
			}  else if (qName.equals("targets") && f_report && f_test && f_targets) {
				f_targets = false;
				buffer = new StringBuffer();
			}  else if (qName.equals("target") && f_report && f_test && f_targets && f_target) {
				f_target=false;
				tmp_target.setName(buffer.toString().toUpperCase());
				tmp_test.addTarget(tmp_target);
				buffer = new StringBuffer();
			} else if (qName.equals("platform") && f_report && f_test && f_platform) {
				f_platform = false;
				tmp_test.setPlateforme(tmp_platform);
				buffer = new StringBuffer();
			} else if (qName.equals("os") && f_report && f_test && f_platform && f_os) {
				f_os = false;
				buffer = new StringBuffer();
			} else if (qName.equals("type") && f_report && f_test && f_platform && f_os && f_os_type) {
				f_os_type = false;
				tmp_platform.setOsType(buffer.toString());
				buffer = new StringBuffer();
			} else if (qName.equals("name") && f_report && f_test && f_platform && f_os && f_os_name) {
				f_os_name = false;
				tmp_platform.setOsName(buffer.toString());
				buffer = new StringBuffer();
			} else if (qName.equals("version") && f_report && f_test && f_platform && f_os && f_os_version) {
				f_os_version = false;
				tmp_platform.setOsVersion(buffer.toString());
				buffer = new StringBuffer();
			} else if (qName.equals("distribution") && f_report && f_test && f_platform && f_os && f_os_distribution) {
				f_os_distribution = false;
				tmp_platform.setOsDistribution(buffer.toString());
				buffer = new StringBuffer();				
			} else if (qName.equals("processor") && f_report && f_test && f_platform && f_processor) {
				f_processor = false;
				tmp_platform.setProcessor(tmp_processor);
				buffer = new StringBuffer();
			} else if (qName.equals("frequency") && f_report && f_test && f_platform && f_processor && f_frequency) {
				f_frequency = false;
				buffer = new StringBuffer();
			} else if (qName.equals("core") && f_report && f_test && f_platform && f_processor && f_core) {
				f_core = false;
				tmp_processor.addCore(tmp_core);
				buffer = new StringBuffer();
			} else if (qName.equals("hardware") && f_report && f_test && f_platform && f_hardware) {
				f_hardware = false;
				tmp_hardware.setName(buffer.toString());			
				tmp_platform.addHardware(tmp_hardware);
				buffer = new StringBuffer();
			}  else if (qName.equals("compiler") && f_report && f_test && f_platform && f_compiler) {
				f_compiler = false;				
				tmp_platform.addCompiler(tmp_compiler);
				buffer = new StringBuffer();
			} else if (qName.equals("source") && f_report && f_test && f_source) {
				f_source = false;	
				tmp_source.setSource(buffer.toString());
				tmp_test.addSource(tmp_source);	
				buffer = new StringBuffer();
			} else if (qName.equals("dataset") && f_report && f_test && f_dataset) {
				f_dataset = false;			
				buffer = new StringBuffer();
			} else if (qName.equals("commandline") && f_report && f_test && f_commandline) {
				tmp_cmdline.setCommand(buffer.toString());
				tmp_test.addCommandLine(tmp_cmdline);
				f_commandline = false;
				buffer = new StringBuffer();
			} else if (qName.equals("param") && f_report && f_test && f_param) {
				f_param = false;
				buffer = new StringBuffer();
			} else if (qName.equals("result") && f_report && f_test && f_result) {
				f_result = false;
				buffer = new StringBuffer();
			} else if (qName.equals("log") && f_report && f_test && f_result && f_log) {
				f_log = false;
				tmp_log.setLog(buffer.toString());
				tmp_test.addLog(tmp_log);
				buffer = new StringBuffer();
			}
			else if (qName.equals("success") && f_report && f_test && f_result && f_success) {
				f_success = false;
				tmp_test.setSuccess(tmp_success);
				buffer = new StringBuffer();
			} else if (qName.equals("compiletime") && f_report && f_test && f_result && f_compiletime) {
				f_compiletime = false;
				tmp_test.setCompileTime(tmp_compiletime);
				buffer = new StringBuffer();
			} else if (qName.equals("executiontime") && f_report && f_test && f_result && f_executiontime) {
				f_executiontime = false;
				tmp_test.setExecutionTime(tmp_executiontime);
				buffer = new StringBuffer();
			} else if (qName.equals("performance") && f_report && f_test && f_result && f_performance) {
				f_performance = false;
				tmp_test.setPerformance(tmp_performance);
				buffer = new StringBuffer();
			} else if (qName.equals("metrics") && f_report && f_test && f_result && f_metrics) {
				f_metrics = false;
				tmp_test.setMetrics(metrics);
				buffer = new StringBuffer();
			}else if (qName.equals("errorlog") && f_report && f_test && f_result && f_errorlog) {
				f_errorlog = false;
				if (!buffer.toString().equals("") && buffer.toString().length()>1) {
					tmp_test.setMessage(buffer.toString());
				}
				buffer = new StringBuffer();
			} else if (qName.equals("end") && f_report && f_end) {
				f_end = false;
				buffer = new StringBuffer();
			} else if (qName.equals("end") && f_report && f_end) {
				f_end = false;
				buffer = new StringBuffer();
			} else if (qName.equals("date") && f_report && f_end && f_end_date) {
				f_end_date = false;
				buffer = new StringBuffer();
			} else if (qName.equals("time") && f_report && f_end && f_end_time) {
				f_end_time = false;
				buffer = new StringBuffer();
			}			
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
		 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement(final String uri, final String localName,
				final String qName, final Attributes attributes)
				throws SAXException {
			if (qName.equals("report")) {
				report = new Report();
				report.setName(attributes.getValue("name"));
				report.setCategorie(attributes.getValue("categ"));
				buffer = new StringBuffer();
				f_report = true;
			} else if (qName.equals("start") && f_report) {
				f_start = true;
				buffer = new StringBuffer();
			} else if (qName.equals("date") && f_report && f_start) {
				f_start_date = true;
				report.setStartDate(attributes.getValue("val"));
				report.setStartDateFormat(attributes.getValue("format"));
				buffer = new StringBuffer();
			} else if (qName.equals("time") && f_report && f_start) {
				f_start_time = true;
				report.setStartTime(attributes.getValue("val"));
				report.setStartTimeFormat(attributes.getValue("format"));
				buffer = new StringBuffer();				
			}  else if (qName.equals("test") && f_report) {
				f_test = true;
				tmp_test = new Test();
				tmp_test.setName(attributes.getValue("name"));
				if (attributes.getValue("executed").equals("yes")) {
					tmp_test.setExecuted(true);
				} else { tmp_test.setExecuted(false); }					
				buffer = new StringBuffer();
			} else if (qName.equals("description") && f_report && f_test) {
				f_description = true;				
				buffer = new StringBuffer();
			} else if (qName.equals("targets") && f_report && f_test) {
				f_targets = true;				
				buffer = new StringBuffer();
			} else if (qName.equals("target") && f_report && f_test && f_targets) {
				f_target = true;			
				tmp_target = new Target();
				tmp_target.setThreaded(Boolean.parseBoolean(attributes.getValue("threaded")));
				buffer = new StringBuffer();
			} else if (qName.equals("platform") && f_report && f_test) {
				f_platform = true;
				tmp_platform = new Platform();
				tmp_platform.setName(attributes.getValue("name"));
				tmp_platform.setRemote(Boolean.parseBoolean(attributes.getValue("capspool")));
				buffer = new StringBuffer();
			} else if (qName.equals("os") && f_report && f_test && f_platform) {
				f_os = true;
				buffer = new StringBuffer();
			} else if (qName.equals("type") && f_report && f_test && f_platform && f_os) {
				f_os_type = true;
				buffer = new StringBuffer();
			} else if (qName.equals("name") && f_report && f_test && f_platform && f_os) {
				f_os_name = true;
				buffer = new StringBuffer();
			} else if (qName.equals("version") && f_report && f_test && f_platform && f_os) {
				f_os_version = true;
				buffer = new StringBuffer();
			} else if (qName.equals("distribution") && f_report && f_test && f_platform && f_os) {
				f_os_distribution = true;
				buffer = new StringBuffer();				
			} else if (qName.equals("processor") && f_report && f_test && f_platform) {
				f_processor = true;
				tmp_processor = new Processor();
				tmp_processor.setProcArch(attributes.getValue("arch"));
				buffer = new StringBuffer();
			} else if (qName.equals("frequency") && f_report && f_test && f_platform && f_processor) {
				f_frequency = true;
				tmp_processor.setProcFreq(attributes.getValue("cpufreq"));
				tmp_processor.setProcFreqUnit(attributes.getValue("unit"));
				buffer = new StringBuffer();
			} else if (qName.equals("core") && f_report && f_test && f_platform && f_processor) {
				f_core = true;
				tmp_core = new Core();
				tmp_core.setCoreid(Integer.parseInt(attributes.getValue("coreid")));
				tmp_core.setProc(Integer.parseInt(attributes.getValue("proc")));
				tmp_core.setPhysid(Integer.parseInt(attributes.getValue("physid")));
				buffer = new StringBuffer();
			} else if (qName.equals("hardware") && f_report && f_test && f_platform) {
				f_hardware = true;
				tmp_hardware = new Hardware();
				if (attributes.getValue("hwa")!=null && attributes.getValue("hwa").equals("yes")) {
					tmp_hardware.setHwa(true);
				} else { tmp_hardware.setHwa(false); }				
				buffer = new StringBuffer();
			}  else if (qName.equals("compiler") && f_report && f_test && f_platform) {
				f_compiler = true;	
				tmp_compiler = new Compiler();
				tmp_compiler.setName(attributes.getValue("name"));
				tmp_compiler.setVersion(attributes.getValue("version"));
				tmp_compiler.setPath(attributes.getValue("path"));
				
				buffer = new StringBuffer();
			
			} else if (qName.equals("source") && f_report && f_test) {
				f_source = true;
				tmp_source = new Source();
				tmp_source.setComment(attributes.getValue("comment"));
				tmp_source.setLanguage(attributes.getValue("language"));
				tmp_source.setPath(attributes.getValue("path"));
							
				buffer = new StringBuffer();
			} else if (qName.equals("dataset") && f_report && f_test) {
				f_dataset = true;
				tmp_dataset = new DataSet();
				tmp_dataset.setPath(attributes.getValue("path"));
				tmp_dataset.setType(attributes.getValue("type"));
				if (tmp_dataset.getType().equals("input")) {
					tmp_test.setDataSetIn(tmp_dataset);
				} else if (tmp_dataset.getType().equals("output")) {
					tmp_test.setDataSetOut(tmp_dataset);
				}				
				buffer = new StringBuffer();
			} else if (qName.equals("commandline") && f_report && f_test) {
				f_commandline = true;
				tmp_cmdline = new CommandLine();
				tmp_cmdline.setTime(attributes.getValue("time"));
				buffer = new StringBuffer();
			} else if (qName.equals("param") && f_report && f_test) {
				f_param = true;
				tmp_param = new Param();
				tmp_param.setName(attributes.getValue("name"));
				buffer = new StringBuffer();
			} else if (qName.equals("result") && f_report && f_test) {
				f_result = true;
				buffer = new StringBuffer();
			} else if (qName.equals("success") && f_report && f_test && f_result) {
				f_success = true;
				tmp_success = new Success();
				if (attributes.getValue("passed").equals("yes")) {
					tmp_success.setPassed(true);
				} else { tmp_success.setPassed(false); }
				tmp_success.setState(Float.parseFloat(attributes.getValue("state")));
				tmp_success.setHasTimedOut(Boolean.parseBoolean(attributes.getValue("hasTimedOut")));
				buffer = new StringBuffer();
				tmp_test.setIsSuccess(true);
			} else if (qName.equals("compiletime") && f_report && f_test && f_result) {
				f_compiletime = true;
				tmp_compiletime = new CompileTime();
				tmp_compiletime.setUnit(attributes.getValue("unit"));
				tmp_compiletime.setMeasure(Double.parseDouble(attributes.getValue("mesure")));
				tmp_compiletime.setRelevant(Boolean.parseBoolean(attributes.getValue("isRelevant")));
								
				tmp_test.setIsCompileTime(true);
				buffer = new StringBuffer();
			} else if (qName.equals("executiontime") && f_report && f_test && f_result) {
				f_executiontime = true;
				tmp_executiontime = new ExecutionTime();
				tmp_executiontime.setUnit(attributes.getValue("unit"));
				tmp_executiontime.setMeasure(Double.parseDouble(attributes.getValue("mesure")));
				tmp_executiontime.setRelevant(Boolean.parseBoolean(attributes.getValue("isRelevant")));
				tmp_test.setIsExecutionTime(true);
				buffer = new StringBuffer();
			} else if (qName.equals("performance") && f_report && f_test && f_result) {
				f_performance = true;
				tmp_performance = new Performance();
				tmp_performance.setUnit(attributes.getValue("unit"));
				tmp_performance.setMeasure(Double.parseDouble(attributes.getValue("mesure")));
				tmp_performance.setRelevant(Boolean.parseBoolean(attributes.getValue("isRelevant")));
				tmp_test.setIsPerformance(true);
				buffer = new StringBuffer();
			} else if (qName.equals("metrics") && f_report && f_test && f_result) {
				f_metrics = true;
				this.metrics = new HashMap<String, Double>();
				buffer = new StringBuffer();
			} else if (this.metrics_name.contains(qName) && f_report && f_test && f_result && f_metrics) {
				//We have discovered a metric
				//It should have attribute value
				if (attributes.getValue("mesure")!=null) {
					double value = Double.parseDouble(attributes.getValue("mesure"));
					this.metrics.put(qName, value);
					buffer = new StringBuffer();
				}
			} else if (qName.equals("errorlog") && f_report && f_test && f_result) {
				f_errorlog = true;
				buffer = new StringBuffer();
			} else if (qName.equals("log") && f_report && f_test && f_result) { 
				f_log = true;
				tmp_log = new Log(attributes.getValue("name"));
				buffer = new StringBuffer();	
			} else if (qName.equals("end") && f_report) {
				f_end = true;
				buffer = new StringBuffer();
			} else if (qName.equals("date") && f_report && f_end) {
				f_end_date = true;
				report.setEndDate(attributes.getValue("val"));
				report.setEndDateFormat(attributes.getValue("format"));
				buffer = new StringBuffer();
			} else if (qName.equals("time") && f_report && f_end && f_end_time) {
				f_end_time = false;
				report.setEndTime(attributes.getValue("val"));
				report.setEndTimeFormat(attributes.getValue("format"));
				buffer = new StringBuffer();
			} else {
				buffer = new StringBuffer();
			}
		}
	}

	// Attribute
	private static Report resultat;
	private static URI xml_path;
	private static Collection<String> metrics;
	/**
	 * @param xml URI Path to the xml file
	 */
	public ParserXml(final URI xml, Collection<String> metrics) {
		this.resultat = new Report();
		this.xml_path = xml;
		this.metrics = metrics;
	}

	/**
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parse() throws ParserConfigurationException, SAXException,
			IOException {

		final SAXParserFactory fabrique = SAXParserFactory.newInstance();
		final SAXParser parseur = fabrique.newSAXParser();
		final DefaultHandler gestionnaire = new Analyse(metrics);
		parseur.parse(new File(xml_path), gestionnaire);
	}

	/**
	 * @return HashMap<String, Rule>
	 */
	public Report result() {

		return resultat;
	}
}
