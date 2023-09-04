package fi.muni.cz.dataprocessing.issuesprocessing;

import fi.muni.cz.dataprovider.GeneralIssue;
import fi.muni.cz.dataprovider.RepositoryInformation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Remove GeneralIssue objects that are invalid based on label or closing time
 *
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class FilterOutInvalidissues implements Filter {

    private static final List<String> FILTERING_WORDS = Arrays.asList(
            "invalid",
            "obosolete"
    );
    private static final FilterByLabel FILTER_BY_LABELS = new FilterByLabel(FILTERING_WORDS, true);

    private int issueAmountBefore;
    private int issueAmountAfter;

    @Override
    public List<GeneralIssue> apply(List<GeneralIssue> list, RepositoryInformation repositoryInformation) {
        issueAmountBefore = list.size();
        Set<GeneralIssue> filteredList = FILTER_BY_LABELS.apply(list, ).stream()
                .filter(issue -> issue.getCreatedAt() == null
                        || issue.getClosedAt() == null ||
                        (issue.getClosedAt().getTime() - issue.getCreatedAt().getTime()) / 1000.0 > 600)
                .collect(Collectors.toSet());

        issueAmountAfter = filteredList.size();
        return filteredList.stream()
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt())).collect(Collectors.toList());
    }

    @Override
    public String infoAboutIssueProcessingAction() {
        return "FilterOutInvalidIssues used to remove invalid issue reports";
    }

    @Override
    public String infoAboutApplicationResult(){
        return String.format("Removed %d issue reports", issueAmountBefore - issueAmountAfter);
    }

    @Override
    public String toString() {
        return "FilterOutInvalidIssues";
    }
}
