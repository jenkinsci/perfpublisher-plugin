package hudson.plugins.PerfPublisher;



import hudson.model.HealthReport;
import hudson.plugins.PerfPublisher.Report.ReportContainer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.data.category.CategoryDataset;

/**
 * Creates a health report for integer values based on healthy and unhealthy
 * thresholds.
 *
 * @see HealthReport
 * @author Ulli Hafner
 */
public class HealthReportBuilder implements Serializable {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = 5191317904662711835L;
    /** Health descriptor. */
    private final HealthDescriptor healthDescriptor;

    /**
     * Creates a new instance of {@link HealthReportBuilder}.
     *
     * @param healthDescriptor
     *            health descriptor
     */
    public HealthReportBuilder(final HealthDescriptor healthDescriptor) {
        this.healthDescriptor = healthDescriptor;
    }

    /**
     * Computes the healthiness of a build based on the specified results.
     * Reports a health of 100% when the specified counter is less than
     * {@link #healthy}. Reports a health of 0% when the specified counter is
     * greater than {@link #unHealthy}. The computation takes only annotations
     * of the specified severity into account.
     *
     * @param result
     *            annotations of the current build
     * @return the healthiness of a build
     */
    public HealthReport computeHealth(HealthDescriptor healthDescriptor, final ReportContainer result) {
        	double percentOfFailedTest = result.getPercentOfFailedTest();
        	int numberOfFailedTest = result.getNumberOfFailedTest();
        	
            int percentage;
            if (percentOfFailedTest < healthDescriptor.getMinHealth()) {
                percentage = 100;
            }
            else if (percentOfFailedTest > healthDescriptor.getMaxHealth()) {
                percentage = 0;
            }
            else {
            	int div = (healthDescriptor.getMaxHealth() - healthDescriptor.getMinHealth());
            	if (div != 0) {
            		percentage = 100 - (((int)percentOfFailedTest - healthDescriptor.getMinHealth()) * 100 / div);
            	} else {
            		percentage = 0;
            	}
            }
            return new HealthReport(percentage, "PerfPublisher : "+numberOfFailedTest+" ("+percentOfFailedTest+"%) failed tests were reported");
    }

    /**
     * Returns whether this health report build is enabled, i.e. at least one of
     * the health or failed thresholds are provided.
     *
     * @return <code>true</code> if health or failed thresholds are provided
     */
    public boolean isEnabled() {
        return healthDescriptor.isHealthAnalyse();
    }

    

}

