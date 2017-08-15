package hudson.plugins.PerfPublisher.matrixBuild;

import hudson.plugins.PerfPublisher.Report.ReportContainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class PerfPublisherMatrixSubBuild implements Comparable<PerfPublisherMatrixSubBuild> {

    /**
     * List of axes value for this subBuild
     */
    private Map<String, String> combinations;

    private ReportContainer report;

    public PerfPublisherMatrixSubBuild(Map<String, String> comb, ReportContainer report) {
        this.combinations = comb;
        this.report = report;
    }

    public boolean hasCombination(Map<String, String> combination) {
        Set<Entry<String, String>> entry = this.combinations.entrySet();
        for (Entry<String, String> axe : entry) {
            if (combination.containsKey(axe.getKey()) && combination.get(axe.getKey()).equals(axe.getValue())) {
                return true;
            }
        }
        return false;
    }

    public Map<String, String> getCombinations() {
        return combinations;
    }

    public String getStringCombinations() {
        StringBuilder strb = new StringBuilder();

        Set<Entry<String, String>> entry = this.combinations.entrySet();
        for (Entry<String, String> axe : entry) {
            strb.append(axe.getKey() + "=" + axe.getValue() + " ; ");
        }

        return strb.toString();
    }


    public ReportContainer getReport() {
        return report;
    }

    public List<String> getAxis() {
        List<String> result = new ArrayList<String>();
        Set<String> axis = this.combinations.keySet();
        Object[] arr = axis.toArray();
        for (Object anArr : arr) {
            result.add((String) anArr);
        }
        return result;
    }

    public List<String> getAxisValues(String axe) {
        List<String> result = new ArrayList<String>();
        Set<Entry<String, String>> entry = this.combinations.entrySet();
        for (Entry<String, String> a : entry) {
            if (a.getKey().equals(axe) && !result.contains(a.getValue())) {
                result.add(a.getValue());
            }
        }
        return result;
    }

    /**
     * Comparable function which offers a sorting solution
     */
    public int compareTo(PerfPublisherMatrixSubBuild subBuild) {
        return this.getStringCombinations().compareTo(subBuild.getStringCombinations());
    }


}
