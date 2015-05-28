package hudson.plugins.PerfPublisher;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Inheritor of Hudson's {@link hudson.util.ChartUtil}
 * Overrides {@link #generateGraph(StaplerRequest, StaplerResponse, JFreeChart, int, int)} method generating empty image
 * in case if chart dataset has no significant data
 *
 * @author Eugene Schava<eschava@gmail.com>
 */
public class ChartUtil extends hudson.util.ChartUtil {
    private static final BufferedImage EMPTY_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

    public static void generateGraph(StaplerRequest req, StaplerResponse rsp, final JFreeChart chart, final int defaultW, final int defaultH) throws IOException {
        boolean isEmpty = false;

        Plot plot = chart.getPlot();
        if (plot instanceof CategoryPlot) {
            CategoryPlot categoryPlot = (CategoryPlot) plot;
            CategoryDataset dataset = categoryPlot.getDataset();
            if (dataset instanceof DefaultCategoryDataset) {
                DefaultCategoryDataset categoryDataset = (DefaultCategoryDataset) dataset;
                int rows = categoryDataset.getRowCount();
                int cols = categoryDataset.getColumnCount();

                boolean nonEmptyFound = false;

                loop:
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        Number value = categoryDataset.getValue(i, j);
                        if (value != null && value.doubleValue() != 0 && !Double.isNaN(value.doubleValue()))
                        {
                            nonEmptyFound = true;
                            break loop;
                        }
                    }
                }

                isEmpty = !nonEmptyFound;
            }
        }

        if (!isEmpty)
            hudson.util.ChartUtil.generateGraph(req, rsp, chart, defaultW, defaultH);
        else
            generateEmptyImage(req, rsp);

    }

    private static void generateEmptyImage(StaplerRequest req, StaplerResponse rsp) throws IOException{
        if(!req.checkIfModified(-1, rsp)) {
            try {
                rsp.setContentType("image/png");
                ServletOutputStream os = rsp.getOutputStream();
                ImageIO.write(EMPTY_IMAGE, "PNG", os);
                os.close();
            } catch (Error error) {
                if(error.getMessage().contains("Probable fatal error:No fonts found")) {
                    rsp.sendRedirect2(req.getContextPath() + "/images/headless.png");
                    return;
                }

                throw error;
            } catch (HeadlessException e) {
                rsp.sendRedirect2(req.getContextPath() + "/images/headless.png");
            }
        }
    }
}
