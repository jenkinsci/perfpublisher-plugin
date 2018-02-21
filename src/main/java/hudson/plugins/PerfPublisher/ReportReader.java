package hudson.plugins.PerfPublisher;

import hudson.plugins.PerfPublisher.Report.Report;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.Collection;

/**
 * Reads all the generated reports
 * 
 * @author Georges Bossert
 * 
 */

public class ReportReader {

	private Report report;
	
	private transient final PrintStream hudsonConsoleWriter;

	/**
	 * Construct a result reader for PerfPublisher out log files.
	 * 
	 * @param is
	 *            The input stream giving the out log file.
	 * @param logger
	 *            Logger to print messages to.
	 * @throws PerfPublisherParseException
	 *             Thrown if the parsing fails.
	 */
	public ReportReader(URI is, PrintStream logger, Collection<String> metrics) {
		hudsonConsoleWriter = logger;
		parse(is, metrics);
	}

	private void parse(URI is, Collection<String> metrics) {
		if (is == null) {
			throw new PerfPublisherParseException("Empty input stream");
		}
		if (report == null) {
			report = new Report();
		}		
		try {
			ParserXml parseur = new ParserXml(is, metrics);
			parseur.parse();
			report = parseur.result();
		} catch (IOException | ParserConfigurationException | SAXException e) {
			String errMsg = "[PerfPublisher] Problem parsing Performance report file";
	        hudsonConsoleWriter.println(errMsg + ": " + e.getMessage());
	        e.printStackTrace(hudsonConsoleWriter);
	        throw new PerfPublisherParseException(errMsg, e);
		}
	}
	
	/**
	 * Get the reports
	 */
	public Report getReport(){
		return report;
	}
}
