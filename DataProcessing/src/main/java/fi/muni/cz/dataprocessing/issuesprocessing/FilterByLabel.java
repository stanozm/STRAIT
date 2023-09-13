package fi.muni.cz.dataprocessing.issuesprocessing;

import fi.muni.cz.dataprocessing.exception.DataProcessingException;
import fi.muni.cz.dataprovider.GeneralIssue;
import fi.muni.cz.dataprovider.RepositoryInformation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtering list of GeneralIssue by labels
 * 
 * @author Radoslav Micko, 445611@muni.cz
 */
public class FilterByLabel implements Filter, Serializable {

    private int issueAmountBefore;
    private int issueAmountAfter;

    private boolean negativeMode;
    private final List<String> filteringWords;

    /**
     * Initialize List of filteringWords.
     * 
     * @param filteringWords words for filter
     * @param negativeMode boolean.
     *                     Includes issues that match filter if false.
     *                     Includes issues that do not match filter if false.
     */
    public FilterByLabel(List<String> filteringWords, boolean negativeMode) {
        this.filteringWords = filteringWords;
        this.negativeMode = negativeMode;
    }
    
    @Override
    public List<GeneralIssue> apply(List<GeneralIssue> list, RepositoryInformation repositoryInformation) {
        issueAmountBefore = list.size();
        if (filteringWords.isEmpty()) {
            throw new DataProcessingException("No filtering words");
        }

        List<GeneralIssue> result = filterByLabels(allLabelsToLowerCase(list));
        issueAmountAfter = result.size();
        return result;
    }
    
    /**
     * Filter List of GeneralIssue.
     * 
     * @param list to be filtered
     * @return filtered list.
     */
    private List<GeneralIssue> filterByLabels(List<GeneralIssue> list) {
        List<GeneralIssue> filteredList = new ArrayList<>();
        for (GeneralIssue issue: list) {
            if (!negativeMode && checkLabelsForMatchWithFilteringWords(issue.getLabels())) {
                filteredList.add(issue);
            }

            if (negativeMode && !checkLabelsForMatchWithFilteringWords(issue.getLabels())) {
                filteredList.add(issue);
            }
        }
        return filteredList;
    }
    
    /**
     * Check labels for any match with List of filteringWords.
     * @param labels to check
     * @return true if any match, false otherwise
     */
    private boolean checkLabelsForMatchWithFilteringWords(List<String> labels) {
        for (String label: labels) {
            for (String filteringWord: filteringWords) {
                if (label.contains(filteringWord)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Make all labels to lower case
     * @param list to iterate over
     * @return List
     */
    private List<GeneralIssue> allLabelsToLowerCase(List<GeneralIssue> list) {
        IssuesProcessor toLowerCaseProcessor = new LabelsToLowerCaseProcessor();
        return toLowerCaseProcessor.apply(list, null);
    }

    @Override
    public String infoAboutIssueProcessingAction() {
        return "FilterByLabel used, with filtering words: " + filteringWords;
    }

    @Override
    public String infoAboutApplicationResult(){
        return String.format("Removed %d issue reports", issueAmountBefore - issueAmountAfter);
    }

    @Override
    public String toString() {
        return "FilterByLabel" + filteringWords.stream().map(str -> str + " ").collect(Collectors.joining());
    }
}
