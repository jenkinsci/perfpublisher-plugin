package hudson.plugins.PerfPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Calculate width of columns
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class WidthCalculator {
    private final double minWidth;
    private final List<Double> widthList = new ArrayList<>();
    private double totalWidth = 0;
    private boolean calculated = false;

    public WidthCalculator(double minWidth) {
        this.minWidth = minWidth;
    }

    public void addWidth(double width) {
        if (calculated) throw new IllegalStateException();
        widthList.add(width);
        totalWidth += width;
    }

    public double getWidth(int n) {
        if (!calculated) {
            calculate();
            calculated = true;
        }

        return widthList.get(n);
    }

    private void calculate() {
        double unusedWidth = Double.isNaN(totalWidth) ? 100 : totalWidth, usedWidth = 0;

        for (ListIterator<Double> iterator = widthList.listIterator(); iterator.hasNext(); ) {
            double width = iterator.next();
            if (width <= minWidth) {
                usedWidth += minWidth;
                unusedWidth -= width;
                iterator.set(minWidth);
            }
        }

        for (ListIterator<Double> iterator = widthList.listIterator(); iterator.hasNext(); ) {
            double width = iterator.next();
            if (width > minWidth) {
                int newWidth = (int) Math.round((totalWidth - usedWidth) * width / unusedWidth);
                usedWidth += newWidth;
                unusedWidth -= width;
                iterator.set((double) newWidth);
            }
            else if (Double.isNaN(width)) {
                iterator.set(unusedWidth / widthList.size());
            }
        }
    }
}
