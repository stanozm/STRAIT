package fi.muni.cz.core.analysis.phases.dataprocessing;

import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.dto.DataPointCollection;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.dataprocessing.issuesprocessing.modeldata.CumulativeIssuesCounter;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesCollection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Valtteri Valtonen valtonenvaltteri@gmail.com
 */
public class CumulativeIssueAmountCalculationPhase implements ReliabilityAnalysisPhase {

    private CumulativeIssuesCounter cumulativeCounter;
    private String timeUnit;

    /**
     * Create new cumulative issue amount calculation phase
     * @param timeUnit Time unit used in the calculation. For instance DAYS.
     */
    public CumulativeIssueAmountCalculationPhase(String timeUnit){
        this.cumulativeCounter = new CumulativeIssuesCounter(timeUnit);
        this.timeUnit = timeUnit;
    };


    @Override
    public ReliabilityAnalysisDto execute(ReliabilityAnalysisDto dto) {
        System.out.println("Calculating cumulative issue amounts");
        List<GeneralIssuesCollection> issueCollections = dto.getIssueReportSets();
        List<DataPointCollection> cumulativeAmounts = issueCollections.
                stream().
                map(issuesCollection -> calculateCumulativeIssueAmounts(
                        issuesCollection, dto.getTestingPeriodStartDate(), dto.getTestingPeriodEndDate()
                ))
                .collect(Collectors.toList());

        dto.setCumulativeIssueReportCollections(cumulativeAmounts);
        dto.setCumulativeTimePeriodUnit(timeUnit);

        return dto;
    }

   private DataPointCollection calculateCumulativeIssueAmounts(
           GeneralIssuesCollection issuesCollection,
           Date startOfTesting,
           Date endOfTesting
   ){
        DataPointCollection dataPointCollection = new DataPointCollection();
        dataPointCollection.setName(issuesCollection.getSnapshotName());
        dataPointCollection.setDataPoints(
                cumulativeCounter.countIssues(issuesCollection.getListOfGeneralIssues(), startOfTesting, endOfTesting)
        );
        return dataPointCollection;
   };
}
