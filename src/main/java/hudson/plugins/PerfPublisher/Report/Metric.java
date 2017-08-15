package hudson.plugins.PerfPublisher.Report;

public class Metric {

    private String unit;
    private double measure;
    private boolean relevant;

    public Metric() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @return the measure
     */
    public double getMeasure() {
        return measure;
    }

    /**
     * @param measure the measure to set
     */
    public void setMeasure(double measure) {
        this.measure = measure;
    }

    public boolean isRelevant() {
        return relevant;
    }

    public void setRelevant(boolean relevant) {
        this.relevant = relevant;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(measure);
        if (unit != null) {
            builder.append(" (").append(unit).append(")");
        }
        return builder.toString();
    }
}
