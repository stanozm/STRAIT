package fi.muni.cz.core.analysis.phases.dataprocessing;

import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.dto.DataPointCollection;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.dataprocessing.issuesprocessing.modeldata.CumulativeIssuesCounter;
import fi.muni.cz.dataprocessing.issuesprocessing.modeldata.TimeBetweenIssuesCounter;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesCollection;
import java.util.List;
import java.util.stream.Collectors;

public class TimeBetweenIssuesCalculationPhase implements ReliabilityAnalysisPhase {

    private TimeBetweenIssuesCounter timeBetweenIssuesCounter;
    private String timeUnit;

    public TimeBetweenIssuesCalculationPhase(String timeUnit){
        this.timeUnit = timeUnit;
        this.timeBetweenIssuesCounter = new TimeBetweenIssuesCounter(timeUnit);
    };


    @Override
    public ReliabilityAnalysisDto execute(ReliabilityAnalysisDto dto) {
        List<GeneralIssuesCollection> issueCollections = dto.getIssueReportSets();
        List<DataPointCollection> timesBetweenIssues = issueCollections.
                stream().
                map(this::calculateTimesBetweenIssues)
                .collect(Collectors.toList());

        dto.setTimeBetweenDefectsCollections(timesBetweenIssues);
        dto.setTimeBetweenDefectsUnit(timeUnit);

        return dto;
    }

   private DataPointCollection calculateTimesBetweenIssues(GeneralIssuesCollection issuesCollection){
        DataPointCollection dataPointCollection = new DataPointCollection();
        dataPointCollection.setName(issuesCollection.getSnapshotName());
        dataPointCollection.setDataPoints(
                timeBetweenIssuesCounter.countIssues(issuesCollection.getListOfGeneralIssues())
        );
        return dataPointCollection;
   };
}
