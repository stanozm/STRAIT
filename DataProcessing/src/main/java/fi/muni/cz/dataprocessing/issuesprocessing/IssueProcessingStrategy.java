package fi.muni.cz.dataprocessing.issuesprocessing;

import fi.muni.cz.dataprovider.GeneralIssue;
import fi.muni.cz.dataprovider.RepositoryInformation;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class that represents an issue processing strategy. Consists of several issue processing actions.
 *
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class IssueProcessingStrategy {

  private final String name;
  private final List<IssueProcessingAction> issueProcessingActionList;

  /**
   * Constructor.
   *
   * @param issueProcessingActions list of issue processing actions included in this strategy
   * @param strategyName name for the data processing strategy
   */
  public IssueProcessingStrategy(
      List<IssueProcessingAction> issueProcessingActions, String strategyName) {
    name = strategyName;
    issueProcessingActionList = issueProcessingActions;
  }

  /**
   * Apply issue processing strategy on list of issue reports.
   *
   * @param issues Target list of issues
   * @param repoInfo Repository information
   * @return List of issue reports that the data processing strategy has been applied to.
   */
  public List<GeneralIssue> apply(List<GeneralIssue> issues, RepositoryInformation repoInfo) {
    List<GeneralIssue> issueList = issues;
    for (IssueProcessingAction action : issueProcessingActionList) {
      issueList = action.apply(issueList, repoInfo);
    }
    return issueList;
  }

  /**
   * Get info about issue processing action results. Should be used after strategy has been applied
   * on data.
   *
   * @return Issue processing strategy action results.
   */
  public Map<String, String> getIssueProcessingActionResults() {
    return issueProcessingActionList.stream()
        .collect(
            Collectors.toMap(
                IssueProcessingAction::toString,
                IssueProcessingAction::infoAboutApplicationResult));
  }

  private static List<GeneralIssue> checkListForEmpty(List<GeneralIssue> issueList) {
    if (issueList.isEmpty()) {
      System.out.println("[There are no issues after applying data processing action]");
      System.exit(1);
    }
    return issueList;
  }
}
