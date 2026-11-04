package hudson.plugins.PerfPublisher;

import hudson.util.Graph;
import jakarta.servlet.ServletOutputStream;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;

public class ChartUtil extends hudson.util.ChartUtil {

    private static final BufferedImage EMPTY_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

    private static Graph createGraph(JFreeChart chart, int defaultW, int defaultH) {

        return new Graph(-1, defaultW, defaultH) {
            @Override
            protected JFreeChart createGraph() {
                return chart;
            }
        };
    }

    public static void generateGraph(
            StaplerRequest2 req, StaplerResponse2 rsp, JFreeChart chart, int defaultW, int defaultH)
            throws IOException {

        if (hasNoSignificantData(chart)) {
            generateEmptyImage(req, rsp);
            return;
        }

        createGraph(chart, defaultW, defaultH).doPng(req, rsp);
    }

    public static void generateClickableMap(
            StaplerRequest2 req, StaplerResponse2 rsp, JFreeChart chart, int defaultW, int defaultH)
            throws IOException {

        createGraph(chart, defaultW, defaultH).doMap(req, rsp);
    }

    private static boolean hasNoSignificantData(JFreeChart chart) {
        Plot plot = chart.getPlot();

        if (!(plot instanceof CategoryPlot categoryPlot)) {
            return false;
        }

        CategoryDataset dataset = categoryPlot.getDataset();

        if (!(dataset instanceof DefaultCategoryDataset categoryDataset)) {
            return false;
        }

        for (int row = 0; row < categoryDataset.getRowCount(); row++) {
            for (int column = 0; column < categoryDataset.getColumnCount(); column++) {

                Number value = categoryDataset.getValue(row, column);

                if (value != null && value.doubleValue() != 0 && !Double.isNaN(value.doubleValue())) {
                    return false;
                }
            }
        }

        return true;
    }

    private static void generateEmptyImage(StaplerRequest2 req, StaplerResponse2 rsp) throws IOException {

        if (!req.checkIfModified(-1, rsp)) {
            try {
                rsp.setContentType("image/png");

                try (ServletOutputStream output = rsp.getOutputStream()) {
                    ImageIO.write(EMPTY_IMAGE, "PNG", output);
                }
            } catch (Error error) {
                String message = error.getMessage();

                if (message != null && message.contains("Probable fatal error:No fonts found")) {
                    rsp.sendRedirect2(req.getContextPath() + "/images/headless.png");
                    return;
                }

                throw error;
            } catch (HeadlessException exception) {
                rsp.sendRedirect2(req.getContextPath() + "/images/headless.png");
            }
        }
    }
}
