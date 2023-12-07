package fi.muni.cz.dataprocessing.issuesprocessing;

import fi.muni.cz.dataprovider.GeneralIssue;
import fi.muni.cz.dataprovider.RepositoryInformation;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Remove GeneralIssue objects that are related to test code
 *
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class FilterOutTestRelatedIssues implements Filter {

  private static final List<String> FILTERING_WORDS =
      Arrays.asList("test, tests, testing, test-failures");
  private static final FilterByLabel FILTER_BY_LABELS = new FilterByLabel(FILTERING_WORDS, true);

  private int issueAmountBefore;
  private int issueAmountAfter;

  @Override
  public List<GeneralIssue> apply(
      List<GeneralIssue> list, RepositoryInformation repositoryInformation) {
    issueAmountBefore = list.size();
    Set<GeneralIssue> filteredList = new HashSet<>(FILTER_BY_LABELS.apply(list, null));
    issueAmountAfter = filteredList.size();
    return filteredList.stream()
        .sorted(Comparator.comparing(GeneralIssue::getCreatedAt))
        .collect(Collectors.toList());
  }

  @Override
  public String infoAboutIssueProcessingAction() {
    return "FilterOutTestIssues used to remove issue reports related to test code";
  }

  @Override
  public String infoAboutApplicationResult() {
    return String.format("Removed %d issue reports", issueAmountBefore - issueAmountAfter);
  }

  @Override
  public String toString() {
    return "FilterTestRelatedIssues";
  }
}
