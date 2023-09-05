package fi.muni.cz.core.analysis.phases.dataprocessing;

import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.dto.IssueReportSet;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.core.dto.ReliabilityAnalysisStepResult;
import fi.muni.cz.dataprocessing.issuesprocessing.IssueProcessingStrategy;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IssueReportProcessingPhase implements ReliabilityAnalysisPhase {

    private IssueProcessingStrategy issueProcessingStrategy;
    private List<Map<String, String>> issueProcessingResults;
    private List<GeneralIssuesCollection> processedIssues;

    public IssueReportProcessingPhase(IssueProcessingStrategy issueProcessingStrategy){
        this.issueProcessingStrategy = issueProcessingStrategy;
        this.issueProcessingResults = new ArrayList<>();
    };


    @Override
    public ReliabilityAnalysisDto execute(ReliabilityAnalysisDto dto) {
        List<GeneralIssuesCollection> issueReportSets = dto.getIssueReportSets();
        issueReportSets.forEach(issuesCollection -> {
            processedIssues.add(applyIssueProcessingStrategyToIssueCollection(issuesCollection));
            issueProcessingResults.add(issueProcessingStrategy.getIssueProcessingActionResults());
        });

        dto.setIssueReportSets(processedIssues);

        List<ReliabilityAnalysisStepResult> resultList = dto.getAnalysisStepResults();
        resultList.add(getReliabilityAnalysisStepResult(issueProcessingResults));
        dto.setAnalysisStepResults(resultList);

        return dto;
    }

    private GeneralIssuesCollection applyIssueProcessingStrategyToIssueCollection(
            GeneralIssuesCollection issuesCollection
    ){
        issuesCollection.setListOfGeneralIssues(
                issueProcessingStrategy.apply(issuesCollection.getListOfGeneralIssues())
        );
        return issuesCollection;
    }

    private ReliabilityAnalysisStepResult getReliabilityAnalysisStepResult(List<Map<String, String>> results){
        ReliabilityAnalysisStepResult result = new ReliabilityAnalysisStepResult();
        List<IssueReportSetResult> issueSetResults = new ArrayList<>();
        results.forEach(stringStringMap -> {
            IssueReportSetResult singleResult = new IssueReportSetResult();
            singleResult.setResult(stringStringMap);
            issueSetResults.add(singleResult);
        });

        result.setType("IssueReportProcessing");
        result.setResult(issueSetResults);
        
        return result;
    }

}
