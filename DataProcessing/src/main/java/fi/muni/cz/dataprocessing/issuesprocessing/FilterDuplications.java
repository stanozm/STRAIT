package fi.muni.cz.dataprocessing.issuesprocessing;

import fi.muni.cz.dataprovider.GeneralIssue;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class FilterDuplications implements Filter {
    private int issueAmountBefore;
    private int issueAmountAfter;
    @Override
    public List<GeneralIssue> apply(List<GeneralIssue> list) {
        issueAmountBefore = list.size();
        List<GeneralIssue> filteredList = new ArrayList<>();
        for (GeneralIssue issue: allLabelsToLowerCase(list)) {
            if (issue.getLabels().stream()
                    .noneMatch(label ->
                            label.contains("duplicate")
                                    || label.contains("duplication")
                                    || label.contains("duplicated"))
            ) {
                filteredList.add(issue);
            }
        }
        issueAmountAfter = filteredList.size();
        return filteredList;
    }

    @Override
    public String infoAboutIssueProcessingAction() {
        return "FilterDuplications used to remove duplication issues.";
    }

    @Override
    public String infoAboutApplicationResult(){
        return String.format("Removed %d issue reports", issueAmountBefore - issueAmountAfter);
    }
    @Override
    public String toString() {
        return "FilterDuplications";
    }

    private List<GeneralIssue> allLabelsToLowerCase(List<GeneralIssue> list) {
        IssuesProcessor toLowerCaseProcessor = new LabelsToLowerCaseProcessor();
        return toLowerCaseProcessor.apply(list);
    }
}
