package hudson.plugins.PerfPublisher;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

class ParserXmlTest {

    @Test
    void testRejectsExternalEntityDeclarations()
            throws IOException, ParserConfigurationException {

        File xml = File.createTempFile("perfpublisher-xxe-", ".xml");

        try {
            try (FileWriter writer = new FileWriter(xml)) {
                writer.write(
                    "<?xml version=\"1.0\"?>\n" +
                    "<!DOCTYPE report [\n" +
                    "  <!ENTITY xxe SYSTEM \"file:///etc/passwd\">\n" +
                    "]>\n" +
                    "<report name=\"XXE Test\" categ=\"test\">\n" +
                    "  &xxe;\n" +
                    "</report>"
                );
            }

            ParserXml parser = new ParserXml(xml.toURI(), null);

            assertThrows(
                SAXException.class,
                () -> parser.parse(),
                "Parser should reject XML containing a DOCTYPE declaration."
            );

        } finally {
            if (xml.exists()) {
                xml.delete();
            }
        }
    }
}