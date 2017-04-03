package hudson.plugins.PerfPublisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hudson.model.Run;
import org.kohsuke.stapler.StaplerRequest;

import hudson.model.ModelObject;
import hudson.plugins.PerfPublisher.Report.ReportContainer;
import hudson.plugins.PerfPublisher.Report.Test;

/**
 * This class is dedicated to the report diff display
 * it prepares and generate the content of the diff
 *
 * @author gbossert
 * @see hudson.model.ModelObject
 */
public class ReportsDiff implements ModelObject {

    private final Run<?, ?> _owner;

    /**
     * The 2 reports to compare
     */
    private ReportContainer report1;
    private ReportContainer report2;
    private ReportContainer report3;
    /**
     * The 3 builds numbers
     */
    private int nb_build1;
    private int nb_build2;
    private int nb_build3;
    /**
     * Activate or not the short diff
     */
    private boolean shortDiff;

    /**
     * The stappler request
     */
    private String link;


    /**
     * The constructor
     *  @param request activate or not the short display
     * @param owner
     * @param nb_build1 number of the first build
     * @param report1   first report
     * @param nb_build2 number of the second build
     * @param report2   second report
     * @param nb_build3 number of the third build
     * @param report3   third report
     */
    public ReportsDiff(final Run<?, ?> owner, StaplerRequest request, int nb_build1, ReportContainer report1, int nb_build2, ReportContainer report2, int nb_build3, ReportContainer report3) {
        this._owner = owner;
        this.report1 = report1;
        this.report2 = report2;
        this.report3 = report3;

        this.nb_build1 = nb_build1;
        this.nb_build2 = nb_build2;
        this.nb_build3 = nb_build3;


        this.link = "?build1=" + this.nb_build1 + "&build2=" + this.nb_build2;
        if (this.nb_build3 == 0) {
            this.link += "&build3=none";
        } else {
            this.link += "&build3=" + this.nb_build3;
        }


        // Activate or not the short display
        this.shortDiff = false;

        if (request.getParameter("short") != null && request.getParameter("short").equals("yes")) {
            this.shortDiff = true;
            this.link += "&short=no";
        } else {
            this.link += "&short=yes";
        }

    }

    /**
     * Getter for the first build number
     *
     * @return the first build number
     */
    public String getBuild1Number() {
        return "" + nb_build1;
    }

    /**
     * Getter for the second build number
     *
     * @return the second build number
     */
    public String getBuild2Number() {
        return "" + nb_build2;
    }

    /**
     * Getter for the third build number
     *
     * @return the third build number
     */
    public String getBuild3Number() {
        return "" + nb_build3;
    }

    /**
     * Getter for the first report
     *
     * @return the first report
     */
    public ReportContainer getReport1() {
        return this.report1;
    }

    /**
     * Getter for the tests list of the first report
     *
     * @return all the tests from the first report
     */
    public List<Test> getReport1Tests() {
        return this.report1.getTests();
    }

    /**
     * Getter for the second report
     *
     * @return the second report
     */
    public ReportContainer getReport2() {
        return this.report2;
    }

    /**
     * Getter for the tests of the second report
     *
     * @return all the tests from the second report
     */
    public List<Test> getReport2Tests() {
        return this.report2.getTests();
    }

    /**
     * Getter for the third report
     *
     * @return the third report
     */
    public ReportContainer getReport3() {
        return this.report3;
    }

    public List<Test> getReport3Tests() {
        return this.report3.getTests();
    }


    public String getHtmlTestsDiff() {
        String style = "Threecolumn";
        if (nb_build3 == 0) {
            style = "Twocolumn";
        }

        StringBuilder strb = new StringBuilder();
        strb.append("<div class=\"line\">");
        strb.append("<div class=\"header\">Build number</div>");
        strb.append("<div class=\"" + style + "\"><b>Build " + this.nb_build1 + "</b></div>");
        strb.append("<div class=\"" + style + "\"><b>Build " + this.nb_build2 + "</b></div>");
        if (nb_build3 != 0) strb.append("<div class=\"" + style + "\"><b>Build " + this.nb_build3 + "</b></div>");
        strb.append("</div>");


        strb.append("<div class=\"line\">");
        strb.append("<div class=\"header\">Build date</div>");
        strb.append("<div class=\"" + style + "\">" + getBuildByNumber(_owner, nb_build1).getTimestampString2() + "</div>");
        strb.append("<div class=\"" + style + "\">" + getBuildByNumber(_owner, nb_build2).getTimestampString2() + "</div>");
        if (nb_build3 != 0)
            strb.append("<div class=\"" + style + "\">" + getBuildByNumber(_owner, nb_build3).getTimestampString2() + "</div>");
        strb.append("</div>");

        strb.append("<div class=\"line\">");
        strb.append("<div class=\"header\">Number of executed tests</div>");
        strb.append("<div class=\"" + style + "\">" + this.report1.getNumberOfExecutedTest() + "</div>");
        strb.append("<div class=\"" + style + "\">" + this.report2.getNumberOfExecutedTest() + "</div>");
        if (nb_build3 != 0)
            strb.append("<div class=\"" + style + "\">" + this.report3.getNumberOfExecutedTest() + "</div>");
        strb.append("</div>");

        strb.append("<div class=\"line\">");
        strb.append("<div class=\"header\">Number of not executed tests</div>");
        strb.append("<div class=\"" + style + "\">" + this.report1.getNumberOfNotExecutedTest() + "</div>");
        strb.append("<div class=\"" + style + "\">" + this.report2.getNumberOfNotExecutedTest() + "</div>");
        if (nb_build3 != 0)
            strb.append("<div class=\"" + style + "\">" + this.report3.getNumberOfNotExecutedTest() + "</div>");
        strb.append("</div>");

        strb.append("<div class=\"line\">");
        strb.append("<div class=\"header\">Number of succeeded tests</div>");
        strb.append("<div class=\"" + style + "\">" + this.report1.getNumberOfPassedTest() + "</div>");
        strb.append("<div class=\"" + style + "\">" + this.report2.getNumberOfPassedTest() + "</div>");
        if (nb_build3 != 0)
            strb.append("<div class=\"" + style + "\">" + this.report3.getNumberOfPassedTest() + "</div>");
        strb.append("</div>");

        strb.append("<div class=\"line\">");
        strb.append("<div class=\"header\">Number of failed tests</div>");
        strb.append("<div class=\"" + style + "\">" + this.report1.getNumberOfFailedTest() + "</div>");
        strb.append("<div class=\"" + style + "\">" + this.report2.getNumberOfFailedTest() + "</div>");
        if (nb_build3 != 0)
            strb.append("<div class=\"" + style + "\">" + this.report3.getNumberOfFailedTest() + "</div>");
        strb.append("</div>");

        strb.append("<div class=\"line\">");
        strb.append("<div class=\"header\">Compile time (Average/Number of measures)</div>");
        strb.append("<div class=\"" + style + "\">" + this.report1.getAverageOfCompileTime() + "/" + this.report1.getNumberOfCompileTimeTest() + "</div>");
        strb.append("<div class=\"" + style + "\">" + this.report2.getAverageOfCompileTime() + "/" + this.report2.getNumberOfCompileTimeTest() + "</div>");
        if (nb_build3 != 0)
            strb.append("<div class=\"" + style + "\">" + this.report3.getAverageOfCompileTime() + "/" + this.report3.getNumberOfCompileTimeTest() + "</div>");
        strb.append("</div>");

        strb.append("<div class=\"line\">");
        strb.append("<div class=\"header\">Execution time (Average/Number of measures)</div>");
        strb.append("<div class=\"" + style + "\">" + this.report1.getAverageOfExecutionTime() + "/" + this.report1.getNumberOfExecutionTimeTest() + "</div>");
        strb.append("<div class=\"" + style + "\">" + this.report2.getAverageOfExecutionTime() + "/" + this.report2.getNumberOfExecutionTimeTest() + "</div>");
        if (nb_build3 != 0)
            strb.append("<div class=\"" + style + "\">" + this.report3.getAverageOfExecutionTime() + "/" + this.report3.getNumberOfExecutionTimeTest() + "</div>");
        strb.append("</div>");

        strb.append("<div class=\"line\">");
        strb.append("<div class=\"header\">Performance (Average/Number of measures)</div>");
        strb.append("<div class=\"" + style + "\">" + this.report1.getAverageOfPerformance() + "/" + this.report1.getNumberOfPerformanceTest() + "</div>");
        strb.append("<div class=\"" + style + "\">" + this.report2.getAverageOfPerformance() + "/" + this.report2.getNumberOfPerformanceTest() + "</div>");
        if (nb_build3 != 0)
            strb.append("<div class=\"" + style + "\">" + this.report3.getAverageOfPerformance() + "/" + this.report3.getNumberOfPerformanceTest() + "</div>");
        strb.append("</div>");

        strb.append("<div class=\"line\">");
        strb.append("<div class=\"header\">Performance (Average/Number of measures)</div>");
        strb.append("<div class=\"" + style + "\">" + this.report1.getAverageOfPerformance() + "/" + this.report1.getNumberOfPerformanceTest() + "</div>");
        strb.append("<div class=\"" + style + "\">" + this.report2.getAverageOfPerformance() + "/" + this.report2.getNumberOfPerformanceTest() + "</div>");
        if (nb_build3 != 0)
            strb.append("<div class=\"" + style + "\">" + this.report3.getAverageOfPerformance() + "/" + this.report3.getNumberOfPerformanceTest() + "</div>");
        strb.append("<br style=\"clear:both;\"></div>&nbsp;");

        strb.append("<div class=\"legende\">");
        strb.append("<div id=\"grey\">NOT EXECUTED</div>");
        strb.append("<div id=\"yellow\">TIMED OUT</div>");
        strb.append("<div id=\"red\">FAILED</div>");
        strb.append("<div id=\"green\">SUCCESSFULL</div>");
        strb.append("<div id=\"white\">DOESN'T EXIST</div>");
        strb.append("<div id=\"link_display\">");
        if (this.shortDiff) {
            strb.append("<a href=\"" + this.link + "\">Display the full diff</a>");
        } else {
            strb.append("<a href=\"" + this.link + "\">Display the short diff</a>");
        }
        strb.append("</div>");

        strb.append("</div>");


        /*
          if short display :
          	Get all the tests executed in the three builds
          else
          	Get all the changed status tests in the three builds
         */
        ArrayList<Test> global_test = new ArrayList<Test>();
        global_test.addAll(this.report1.getTests());

        for (int i = 0; i < this.report2.getTests().size(); i++) {
            boolean insert = true;
            for (Test aGlobal_test : global_test) {
                if (aGlobal_test.getName().equals(this.report2.getTests().get(i).getName())) {
                    insert = false;
                }
            }
            if (insert) {
                global_test.add(this.report2.getTests().get(i));
            }
        }
        if (this.report3 != null) {
            for (int i = 0; i < this.report3.getTests().size(); i++) {
                boolean insert = true;
                for (Test aGlobal_test : global_test) {
                    if (aGlobal_test.getName().equals(this.report3.getTests().get(i).getName())) {
                        insert = false;
                    }
                }
                if (insert) {
                    global_test.add(this.report3.getTests().get(i));
                }
            }
        }
        Collections.sort(global_test);
        for (Test aGlobal_test : global_test) {


            String color1 = "#fff";
            String color2 = "#fff";
            String color3 = "#fff";
            String txt1 = "-";
            String txt2 = "-";
            String txt3 = "-";

            if (this.report1.getTestWithName(aGlobal_test.getName()) != null) {
                Test test1 = this.report1.getTestWithName(aGlobal_test.getName());
                txt1 = "<a href=\"../../../" + this.nb_build1 + "/PerfPublisher/testDetails." + test1.getNameForUrl() + "\">x</a>";
                if (!test1.isExecuted()) {
                    color1 = "grey";
                } else {
                    if (test1.isHasTimedOut()) {
                        color1 = "yellow";
                    } else {
                        if (test1.isSuccessfull()) {
                            color1 = "green";
                        } else {
                            color1 = "red";
                        }
                    }
                }
            }


            if (this.report2.getTestWithName(aGlobal_test.getName()) != null) {
                Test test2 = this.report2.getTestWithName(aGlobal_test.getName());
                txt2 = "<a href=\"../../../" + this.nb_build2 + "/PerfPublisher/testDetails." + test2.getNameForUrl() + "\">x</a>";
                if (!test2.isExecuted()) {
                    color2 = "grey";
                } else {
                    if (test2.isHasTimedOut()) {
                        color2 = "yellow";
                    } else {
                        if (test2.isSuccessfull()) {
                            color2 = "green";
                        } else {
                            color2 = "red";
                        }
                    }
                }
            }

            if (nb_build3 != 0) {
                if (this.report3.getTestWithName(aGlobal_test.getName()) != null) {
                    Test test3 = this.report3.getTestWithName(aGlobal_test.getName());
                    txt3 = "<a href=\"../../../" + this.nb_build3 + "/PerfPublisher/testDetails." + test3.getNameForUrl() + "\">x</a>";
                    if (!test3.isExecuted()) {
                        color3 = "grey";
                    } else {
                        if (test3.isHasTimedOut()) {
                            color3 = "yellow";
                        } else {
                            if (test3.isSuccessfull()) {
                                color3 = "green";
                            } else {
                                color3 = "red";
                            }
                        }
                    }
                }
                if (shortDiff && (color1 != color2 || color1 != color3 || color2 != color3)) {
                    strb.append("<div class=\"line\">");
                    strb.append("<div class=\"header\">" + aGlobal_test.getName() + "</div>");
                    strb.append("<div class=\"" + style + "\" style=\"background-color:" + color1 + "\"> " + txt1 + " </div>");
                    strb.append("<div class=\"" + style + "\" style=\"background-color:" + color2 + "\"> " + txt2 + " </div>");
                    strb.append("<div class=\"" + style + "\" style=\"background-color:" + color3 + "\"> " + txt3 + " </div>");
                    strb.append("</div>\n");
                } else if (!shortDiff) {
                    strb.append("<div class=\"line\">");
                    strb.append("<div class=\"header\">" + aGlobal_test.getName() + "</div>");
                    strb.append("<div class=\"" + style + "\" style=\"background-color:" + color1 + "\"> " + txt1 + " </div>");
                    strb.append("<div class=\"" + style + "\" style=\"background-color:" + color2 + "\"> " + txt2 + " </div>");
                    strb.append("<div class=\"" + style + "\" style=\"background-color:" + color3 + "\"> " + txt3 + " </div>");
                    strb.append("</div>\n");
                }
            } else {
                if (shortDiff && (color1 != color2)) {
                    strb.append("<div class=\"line\">");
                    strb.append("<div class=\"header\">" + aGlobal_test.getName() + "</div>");
                    strb.append("<div class=\"" + style + "\" style=\"background-color:" + color1 + "\"> " + txt1 + " </div>");
                    strb.append("<div class=\"" + style + "\" style=\"background-color:" + color2 + "\"> " + txt2 + " </div>");
                    strb.append("</div>\n");
                } else if (!shortDiff) {
                    strb.append("<div class=\"line\">");
                    strb.append("<div class=\"header\">" + aGlobal_test.getName() + "</div>");
                    strb.append("<div class=\"" + style + "\" style=\"background-color:" + color1 + "\"> " + txt1 + " </div>");
                    strb.append("<div class=\"" + style + "\" style=\"background-color:" + color2 + "\"> " + txt2 + " </div>");
                    strb.append("</div>\n");
                }
            }

        }


        return strb.toString();
    }

    private static Run getBuildByNumber(Run<?, ?> _owner, int nb_build) {
        return _owner.getParent().getBuildByNumber( nb_build );
    }

    public Run<?, ?> getOwner() {
        return _owner;
    }

    public String getDisplayName() {
        return "Reports diff.";
    }

}
