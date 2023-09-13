package fi.muni.cz.dataprocessing.issuesprocessing;

import fi.muni.cz.dataprovider.GeneralIssue;
import fi.muni.cz.dataprovider.RepositoryInformation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Remove GeneralIssue objects that have a stated low criticality level
 *
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class FilterOutIssuesWithLowCriticality implements Filter {

    private static final List<String> FILTERING_WORDS = Arrays.asList(
            "priority:low",
            "low priority",
            "priority:medium",
            "medium priority"
    );
    private static final FilterByLabel FILTER_BY_LABELS = new FilterByLabel(FILTERING_WORDS, true);

    private int issueAmountBefore;
    private int issueAmountAfter;

    @Override
    public List<GeneralIssue> apply(List<GeneralIssue> list, RepositoryInformation repositoryInformation) {
        issueAmountBefore = list.size();
        Set<GeneralIssue> filteredList = new HashSet<>(FILTER_BY_LABELS.apply(list, null));
        issueAmountAfter = filteredList.size();
        return filteredList.stream()
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt())).collect(Collectors.toList());
    }

    @Override
    public String infoAboutIssueProcessingAction() {
        return "FilterOutIssuesWithLowCriticality used to remove issue reports " +
                "which have a stated low criticality level";
    }

    @Override
    public String infoAboutApplicationResult(){
        return String.format("Removed %d issue reports", issueAmountBefore - issueAmountAfter);
    }

    @Override
    public String toString() {
        return "FilterOutIssuesWithLowCriticality";
    }
}
