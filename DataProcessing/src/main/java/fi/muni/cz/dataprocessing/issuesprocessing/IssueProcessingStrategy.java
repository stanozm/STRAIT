package fi.muni.cz.dataprocessing.issuesprocessing;

import fi.muni.cz.dataprovider.GeneralIssue;
import java.util.List;

/**
 * Class that represents an issue processing strategy.
 * Consists of several issue processing actions.
 *
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */

public class IssueProcessingStrategy {

    private final String name;
    private final List<IssueProcessingAction> issueProcessingActionList;

    /**
     * Constructor.
     * @param issueProcessingActions list of issue processing actions included in this strategy
     * @param strategyName name for the data processing strategy
     */
    public IssueProcessingStrategy(List<IssueProcessingAction> issueProcessingActions, String strategyName){
        name = strategyName;
        issueProcessingActionList = issueProcessingActions;
    }

    /**
     * Apply issue processing strategy on list of issue reports.
     * @param issues Target list of issues
     * @return List of issue reports that the data processing strategy has been applied to.
     */
    public List<GeneralIssue> apply(List<GeneralIssue> issues){
        List<GeneralIssue> issueList = issues;
        for(IssueProcessingAction action : issueProcessingActionList){
            issueList = checkListForEmpty(action.apply(issues));
        }
        return issueList;
    }

    private static List<GeneralIssue> checkListForEmpty(List<GeneralIssue> issueList) {
        if (issueList.isEmpty()) {
            System.out.println("[There are no issues after applying data processing action]");
            System.exit(1);
        }
        return issueList;
    }
}
