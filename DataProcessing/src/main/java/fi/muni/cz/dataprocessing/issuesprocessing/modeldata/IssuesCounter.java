package fi.muni.cz.dataprocessing.issuesprocessing.modeldata;

import fi.muni.cz.dataprovider.GeneralIssue;
import org.apache.commons.math3.util.Pair;
import java.util.List;

/**
 * Prepare data for model as needed.
 * 
 * @author Radoslav Micko, 445611@muni.cz
 */
public interface IssuesCounter {
    
    String SECONDS = "Seconds";
    String MINUTES = "Minutes";
    String HOURS = "Hours";
    String DAYS = "Days";
    String WEEKS = "Weeks";
    String MONTHS = "Months";
    String YEARS = "Years";
    
    
    /**
     * Count issues.
     *
     * @param issues Issues to count.
     * @return List of counted pairs.
     */
    List<Pair<Integer, Integer>> countIssues(List<GeneralIssue> issues);
}