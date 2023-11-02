package fi.muni.cz.core.analysis.phases.dataprocessing;

import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.dto.DataPointCollection;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.dataprocessing.issuesprocessing.MovingAverage;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Valtteri Valtonen valtonenvaltteri@gmail.com
 */
public class MovingAveragePhase implements ReliabilityAnalysisPhase {

    @Override
    public ReliabilityAnalysisDto execute(ReliabilityAnalysisDto dto) {
        System.out.println("Calculating moving average");
        List<DataPointCollection> dataPointCollections = dto.getCumulativeIssueReportCollections();
        List<DataPointCollection> averagedPoints = dataPointCollections.
                stream().
                map(cumulativePoints -> calculateMovingAverage(cumulativePoints, getAverageWindowSize(dto)))
                .collect(Collectors.toList());

        dto.setCumulativeIssueReportCollections(averagedPoints);
        return dto;
    }

    private Integer getAverageWindowSize(ReliabilityAnalysisDto dto) {
        try{
            return Integer.valueOf(dto.getConfiguration().getOptionValueMovingAverage());
        } catch (NumberFormatException numberFormatException) {
            numberFormatException.printStackTrace();
            System.exit(1);
        }
        return 1;
    }

   private DataPointCollection calculateMovingAverage(DataPointCollection dataPointCollection, Integer windowSize){
        dataPointCollection.setDataPoints(
                MovingAverage.calculateMovingAverage(dataPointCollection.getDataPoints(), windowSize)
        );
        return dataPointCollection;
   }
}
