
package hudson.plugins.PerfPublisher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

public class ParserXmlTest extends TestCase {

    public void testRejectsExternalEntityDeclarations()
            throws IOException, ParserConfigurationException {

        File xml = File.createTempFile("perfpublisher-xxe-", ".xml");

        try {
            FileWriter writer = new FileWriter(xml);
            try {
                writer.write(
                    "<?xml version=\"1.0\"?>\n" +
                    "<!DOCTYPE report [\n" +
                    "  <!ENTITY xxe SYSTEM \"file:///etc/passwd\">\n" +
                    "]>\n" +
                    "<report name=\"XXE Test\" categ=\"test\">\n" +
                    "  &xxe;\n" +
                    "</report>"
                );
            } finally {
                writer.close();
            }

            ParserXml parser = new ParserXml(xml.toURI(), null);

            try {
                parser.parse();
                fail("Parser should reject XML containing a DOCTYPE declaration.");
            } catch (SAXException expected) {
                // Expected: DOCTYPE declarations are disabled.
            }

        } finally {
            if (xml.exists()) {
                xml.delete();
            }
        }
    }
}
