package fi.muni.cz.dataprocessing.issuesprocessing;

import fi.muni.cz.dataprovider.GeneralIssue;
import fi.muni.cz.dataprovider.Release;
import fi.muni.cz.dataprovider.RepositoryInformation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Remove GeneralIssue objects that have a creation date before the publishing date of the first
 * release
 *
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class FilterOutIssuesBeforeFirstRelease implements Filter {

  private int issueAmountBefore;
  private int issueAmountAfter;

  @Override
  public List<GeneralIssue> apply(
      List<GeneralIssue> list, RepositoryInformation repositoryInformation) {
    issueAmountBefore = list.size();
    List<Release> releases =
        repositoryInformation != null
            ? repositoryInformation.getListOfReleases().stream()
                .sorted(Comparator.comparing(Release::getPublishedAt))
                .toList()
            : new ArrayList<>();

    if (releases.isEmpty()) {
      issueAmountAfter = list.size();
      return list;
    }

    Date firstReleasePublishDate = releases.get(0).getPublishedAt();
    Date now = new Date();

    Set<GeneralIssue> filteredList =
        new HashSet<>(
            new FilterByTime(firstReleasePublishDate, now).apply(list, repositoryInformation));

    issueAmountAfter = filteredList.size();
    return filteredList.stream()
        .sorted(Comparator.comparing(GeneralIssue::getCreatedAt))
        .collect(Collectors.toList());
  }

  @Override
  public String infoAboutIssueProcessingAction() {
    return "FilterOutIssuesBeforeFirstRelease used to remove issue reports before publish date of first release";
  }

  @Override
  public String infoAboutApplicationResult() {
    return String.format("Removed %d issue reports", issueAmountBefore - issueAmountAfter);
  }

  @Override
  public String toString() {
    return "FilterOutIssuesBeforeFirstRelease";
  }
}
