package fi.muni.cz.dataprocessing.issuesprocessing;

import fi.muni.cz.dataprovider.GeneralIssue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class FilterByTime implements Filter {

    private final Date startOfTesting;
    private final Date endOfTesting;
    private int issueAmountBefore;
    private int issueAmountAfter;
    
    /**
     * Initialize.
     * 
     * @param startOfTesting start of testing
     * @param endOfTesting end of testing
     */
    public FilterByTime(Date startOfTesting, Date endOfTesting) {
        this.startOfTesting = startOfTesting;
        this.endOfTesting = endOfTesting;
    }
    
    @Override
    public List<GeneralIssue> apply(List<GeneralIssue> list) {
        issueAmountBefore = list.size();
        List<GeneralIssue> filteredList = new ArrayList<>();
        for (GeneralIssue issue: list) {
            if (issue.getCreatedAt().after(startOfTesting) && issue.getCreatedAt().before(endOfTesting)) {
                filteredList.add(issue);
            }
        }
        issueAmountAfter = filteredList.size();
        return filteredList;
    }

    @Override
    public String infoAboutIssueProcessingAction() {
        return "FilterByTime start of testing = " + startOfTesting 
                + ", end of testing = " + endOfTesting;
    }

    @Override
    public String infoAboutApplicationResult(){
        return String.format("Removed %d issue reports", issueAmountBefore - issueAmountAfter);
    }

    @Override
    public String toString() {
        return "FilterByTime: " + startOfTesting + " - " + endOfTesting;
    }
}