package fi.muni.cz.core.analysis.phases.modelfitting;

import static fi.muni.cz.dataprocessing.issuesprocessing.modeldata.IssuesCounter.HOURS;

import fi.muni.cz.core.ArgsParser;
import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.dataprovider.GeneralIssue;
import fi.muni.cz.models.testing.LaplaceTrendTest;
import fi.muni.cz.models.testing.TrendTest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Valtteri Valtonen valtonenvaltteri@gmail.com
 */
public class TrendTestPhase implements ReliabilityAnalysisPhase {

    @Override
    public ReliabilityAnalysisDto execute(ReliabilityAnalysisDto dto) {

        TrendTest trendTest = new LaplaceTrendTest(getTimeUnit(dto));

        List<TrendTestResult> trendTestResults = new ArrayList<>();

        dto.getIssueReportSets().stream().forEach(issuesCollection -> {
            TrendTestResult result = getTrendTestResult(trendTest, issuesCollection.getListOfGeneralIssues());
            trendTestResults.add(result);
        });

        dto.setTrendTestResults(trendTestResults);

        return dto;
    }

   private String getTimeUnit(ReliabilityAnalysisDto dto){
       ArgsParser configuration = dto.getConfiguration();
        if(configuration.hasOptionTimeBetweenIssuesUnit()){
            return configuration.getOptionValueTimeBetweenIssuesUnit();
        }
        return HOURS;
   }

   private TrendTestResult getTrendTestResult(TrendTest test, List<GeneralIssue> issueList){

        test.executeTrendTest(issueList);

        TrendTestResult trendTestResult = new TrendTestResult();
        trendTestResult.setTrendFound(test.getResult());
        trendTestResult.setTrendValue(test.getTrendValue());
        return trendTestResult;

   }


}
