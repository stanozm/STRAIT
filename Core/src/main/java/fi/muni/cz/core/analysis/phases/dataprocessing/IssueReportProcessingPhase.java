package fi.muni.cz.core.analysis.phases.dataprocessing;

import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.dataprocessing.issuesprocessing.IssueProcessingStrategy;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Valtteri Valtonen valtonenvaltteri@gmail.com
 */
public class IssueReportProcessingPhase implements ReliabilityAnalysisPhase {

    private IssueProcessingStrategy issueProcessingStrategy;
    private List<Map<String, String>> issueProcessingResults;
    private List<GeneralIssuesCollection> processedIssues;

    /**
     * Create new issue report processing phase
     * @param issueProcessingStrategy the issue report processing strategy that is to be used
     */
    public IssueReportProcessingPhase(IssueProcessingStrategy issueProcessingStrategy){
        this.issueProcessingStrategy = issueProcessingStrategy;
        this.issueProcessingResults = new ArrayList<>();
        this.processedIssues = new ArrayList<>();
    };


    @Override
    public ReliabilityAnalysisDto execute(ReliabilityAnalysisDto dto) {
        List<GeneralIssuesCollection> issueReportSets = dto.getIssueReportSets();

        dto.setIssueReportAmountBeforeProcessing(calculateTotalIssuesInDto(dto));

        issueReportSets.forEach(issuesCollection -> {
            processedIssues.add(applyIssueProcessingStrategyToIssueCollection(issuesCollection));
            issueProcessingResults.add(issueProcessingStrategy.getIssueProcessingActionResults());
        });

        dto.setIssueReportSets(processedIssues);
        dto.setIssueReportAmountAfterProcessing(calculateTotalIssuesInDto(dto));

        dto.setIssueProcessingResults(issueProcessingResults);

        return dto;
    }

    private int calculateTotalIssuesInDto(ReliabilityAnalysisDto dto) {
        return (int) dto.getIssueReportSets()
                .stream()
                .flatMap(issueReportSet -> issueReportSet.getListOfGeneralIssues().stream()).count();
    }

    private GeneralIssuesCollection applyIssueProcessingStrategyToIssueCollection(
            GeneralIssuesCollection issuesCollection
    ){
        issuesCollection.setListOfGeneralIssues(
                issueProcessingStrategy.apply(issuesCollection.getListOfGeneralIssues())
        );
        return issuesCollection;
    }


}
