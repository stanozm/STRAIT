package fi.muni.cz.dataprocessing.issuesprocessing;

import fi.muni.cz.dataprovider.GeneralIssue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filtering list of GeneralIssue that are defects
 *
 * @author Radoslav Micko, 445611@muni.cz
 */
public class FilterDefects implements Filter {

    private static final List<String> FILTERING_WORDS = Arrays.asList("bug","error","fail","fault","defect");
    private static final List<String> NEGATIVE_FILTERING_WORDS = Arrays.asList("not-a-bug", "not a bug", "not a defect");
    private static final FilterByLabel FILTER_BY_LABELS = new FilterByLabel(FILTERING_WORDS, false);

    private static final FilterByLabel NEGATIVE_FILTER_BY_LABELS = new FilterByLabel(
            NEGATIVE_FILTERING_WORDS,
            true
    );

    private int issueAmountBefore;
    private int issueAmountAfter;

    @Override
    public List<GeneralIssue> apply(List<GeneralIssue> list) {
        issueAmountBefore = list.size();
        Set<GeneralIssue> filteredList = new HashSet<>(NEGATIVE_FILTER_BY_LABELS.apply(FILTER_BY_LABELS.apply(list)));

        for (GeneralIssue issue: list) {
            if (issue.getBody() == null) {
                continue;
            }
            if (FILTERING_WORDS.stream().anyMatch(filteringWord -> issue.getBody().contains(filteringWord))) {
                filteredList.add(issue);
            }
        }

        issueAmountAfter = filteredList.size();
        return filteredList.stream()
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt())).collect(Collectors.toList());
    }

    @Override
    public String infoAboutIssueProcessingAction() {
        return "FilterDefects used to get only defects.";
    }

    @Override
    public String infoAboutApplicationResult(){
        return String.format("Removed %d issue reports", issueAmountBefore - issueAmountAfter);
    }

    @Override
    public String toString() {
        return "FilterDefects";
    }
}
