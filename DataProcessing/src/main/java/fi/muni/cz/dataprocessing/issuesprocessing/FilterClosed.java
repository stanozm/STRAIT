package fi.muni.cz.dataprocessing.issuesprocessing;

import fi.muni.cz.dataprovider.GeneralIssue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Filtering out opened issues from list of 
 * {@link fi.muni.cz.reliability.tool.dataprovider.GeneralIssue GeneralIssue} 
 * 
 * @author Radoslav Micko, 445611@muni.cz
 */
public class FilterClosed implements Filter, Serializable {

    private int issueAmountBefore;
    private int issueAmountAfter;

    @Override
    public List<GeneralIssue> apply(List<GeneralIssue> list) {
        issueAmountBefore = list.size();
        List<GeneralIssue> closedIssues = new ArrayList<>();
        for (GeneralIssue issue: list) {
            if (issue.getState().equals("closed")) {
                closedIssues.add(issue);
            }
        }
        issueAmountAfter = closedIssues.size();
        return closedIssues;
    }

    @Override
    public String infoAboutIssueProcessingAction() {
        return "FilterClosed used to filter out opened issues.";
    }

    @Override
    public String infoAboutApplicationResult(){
        return String.format("Removed %d issue reports", issueAmountBefore - issueAmountAfter);
    }
    @Override
    public String toString() {
        return "FilterClosed";
    }
}
