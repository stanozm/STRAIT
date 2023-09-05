package fi.muni.cz.core.analysis.phases.dataprocessing;

import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.dto.DataPointCollection;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.core.dto.ReliabilityAnalysisStepResult;
import fi.muni.cz.dataprocessing.issuesprocessing.IssueProcessingStrategy;
import fi.muni.cz.dataprocessing.issuesprocessing.modeldata.CumulativeIssuesCounter;
import fi.muni.cz.dataprocessing.issuesprocessing.modeldata.IssuesCounter;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CumulativeIssueAmountCalculationPhase implements ReliabilityAnalysisPhase {

    private CumulativeIssuesCounter cumulativeCounter;

    public CumulativeIssueAmountCalculationPhase(String timeUnit){
        this.cumulativeCounter = new CumulativeIssuesCounter(timeUnit);
    };


    @Override
    public ReliabilityAnalysisDto execute(ReliabilityAnalysisDto dto) {
        List<GeneralIssuesCollection> issueCollections = dto.getIssueReportSets();
        List<DataPointCollection> cumulativeAmounts = issueCollections.
                stream().
                map(this::calculateCumulativeIssueAmounts)
                .collect(Collectors.toList());

        dto.setCumulativeIssueReportCollections(cumulativeAmounts);

        return dto;
    }

   private DataPointCollection calculateCumulativeIssueAmounts(GeneralIssuesCollection issuesCollection){
        DataPointCollection dataPointCollection = new DataPointCollection();
        dataPointCollection.setName(issuesCollection.getSnapshotName());
        dataPointCollection.setDataPoints(
                cumulativeCounter.countIssues(issuesCollection.getListOfGeneralIssues())
        );
        return dataPointCollection;
   };
}
