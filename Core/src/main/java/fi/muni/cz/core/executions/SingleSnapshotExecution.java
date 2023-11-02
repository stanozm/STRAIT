package fi.muni.cz.core.executions;

import static fi.muni.cz.dataprocessing.issuesprocessing.modeldata.IssuesCounter.HOURS;
import static fi.muni.cz.dataprocessing.issuesprocessing.modeldata.IssuesCounter.WEEKS;

import fi.muni.cz.core.ArgsParser;
import fi.muni.cz.core.analysis.ReliabilityAnalysis;
import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.analysis.phases.datacollection.SnapshotDataCollectionPhase;
import fi.muni.cz.core.analysis.phases.dataprocessing.CumulativeIssueAmountCalculationPhase;
import fi.muni.cz.core.analysis.phases.dataprocessing.IssueReportProcessingPhase;
import fi.muni.cz.core.analysis.phases.dataprocessing.MovingAveragePhase;
import fi.muni.cz.core.analysis.phases.dataprocessing.TimeBetweenIssuesCalculationPhase;
import fi.muni.cz.core.analysis.phases.modelfitting.ModelFittingAndGoodnessOfFitTestPhase;
import fi.muni.cz.core.analysis.phases.modelfitting.TrendTestPhase;
import fi.muni.cz.core.analysis.phases.output.HtmlReportOutputPhase;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.core.factory.ModelFactory;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesSnapshotDao;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesSnapshotDaoImpl;
import java.util.ArrayList;
import java.util.List;

/** @author Valtteri Valtonen valtonenvaltteri@gmail.com */
public class SingleSnapshotExecution extends StraitExecution {

  private ReliabilityAnalysis analysis;
  private GeneralIssuesSnapshotDao dao;

  /** Create new single snapshot execution. */
  public SingleSnapshotExecution() {
    this.dao = new GeneralIssuesSnapshotDaoImpl();
  }

  @Override
  public void initializeAnalyses(ArgsParser configuration) {

    String periodOfTestingValue =
        configuration.getOptionValuePeriodOfTesting() != null
            ? configuration.getOptionValuePeriodOfTesting()
            : WEEKS;

    String timeBetweenIssuesUnitValue =
        configuration.getOptionValueTimeBetweenIssuesUnit() != null
            ? configuration.getOptionValueTimeBetweenIssuesUnit()
            : HOURS;

    List<ReliabilityAnalysisPhase> analysisPhases = new ArrayList<>();

    analysisPhases.add(
        new SnapshotDataCollectionPhase(dao, configuration.getOptionValueSnapshotName()));

    analysisPhases.add(new IssueReportProcessingPhase(getStrategyFromConfiguration(configuration)));

    analysisPhases.add(new CumulativeIssueAmountCalculationPhase(periodOfTestingValue));

    if (configuration.hasOptionMovingAverage()) {
      analysisPhases.add(new MovingAveragePhase());
    }

    analysisPhases.add(new TimeBetweenIssuesCalculationPhase(timeBetweenIssuesUnitValue));

    analysisPhases.add(new TrendTestPhase());

    analysisPhases.add(new ModelFittingAndGoodnessOfFitTestPhase(ModelFactory.getREngine()));

    analysisPhases.add(new HtmlReportOutputPhase());

    ReliabilityAnalysis reliabilityAnalysis = new ReliabilityAnalysis(analysisPhases);

    this.analysis = reliabilityAnalysis;
  }

  @Override
  public void execute(ArgsParser configuration) {
    System.out.println("Executing STRAIT in single snapshot analysis mode");
    analysis.performAnalysis(new ReliabilityAnalysisDto(configuration));
  }
}
