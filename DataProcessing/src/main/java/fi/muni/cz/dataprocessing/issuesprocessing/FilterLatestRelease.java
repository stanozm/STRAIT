package fi.muni.cz.dataprocessing.issuesprocessing;

import fi.muni.cz.dataprovider.GeneralIssue;
import fi.muni.cz.dataprovider.Release;
import fi.muni.cz.dataprovider.RepositoryInformation;
import java.util.ArrayList;

import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Remove GeneralIssue objects that are invalid based on label or closing time
 *
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class FilterLatestRelease implements Filter {

    private int issueAmountBefore;
    private int issueAmountAfter;

    @Override
    public List<GeneralIssue> apply(List<GeneralIssue> list, RepositoryInformation repositoryInformation) {
        issueAmountBefore = list.size();
        List<Release> releases = repositoryInformation != null ?
                repositoryInformation.getListOfReleases()
                        .stream()
                        .sorted(Comparator.comparing(Release::getPublishedAt))
                        .collect(Collectors.toList())
                : new ArrayList<>();

        if(releases.size() < 2){
            issueAmountAfter = list.size();
            return list;
        }

        Date timePeriodStart = releases.get(releases.size() - 2).getPublishedAt();
        Date timePeriodEnd = releases.get(releases.size() - 1).getPublishedAt();

        Set<GeneralIssue> filteredList = new HashSet<>(new FilterByTime(timePeriodStart, timePeriodEnd)
                .apply(list, repositoryInformation));


        issueAmountAfter = filteredList.size();
        return filteredList.stream()
                .sorted(Comparator.comparing(GeneralIssue::getCreatedAt)).collect(Collectors.toList());
    }

    @Override
    public String infoAboutIssueProcessingAction() {
        return "FilterLatestRelease used to remove issue reports outside latest release";
    }

    @Override
    public String infoAboutApplicationResult(){
        return String.format("Removed %d issue reports", issueAmountBefore - issueAmountAfter);
    }

    @Override
    public String toString() {
        return "FilterLatestRelease";
    }
}
