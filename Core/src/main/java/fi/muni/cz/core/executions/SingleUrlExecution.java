package fi.muni.cz.core.executions;

import fi.muni.cz.core.ArgsParser;
import fi.muni.cz.core.analysis.ReliabilityAnalysis;
import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.analysis.phases.datacollection.DataCollectionCacheMode;
import fi.muni.cz.core.analysis.phases.datacollection.GithubDataCollectionPhase;
import fi.muni.cz.core.analysis.phases.dataprocessing.CumulativeIssueAmountCalculationPhase;
import fi.muni.cz.core.analysis.phases.dataprocessing.IssueReportProcessingPhase;
import fi.muni.cz.core.analysis.phases.dataprocessing.TimeBetweenIssuesCalculationPhase;
import fi.muni.cz.core.analysis.phases.modelfitting.ModelFittingAndGoodnessOfFitTestPhase;
import fi.muni.cz.core.analysis.phases.modelfitting.TrendTestPhase;
import fi.muni.cz.core.analysis.phases.output.HtmlReportOutputPhase;
import fi.muni.cz.core.configuration.DataSource;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.core.factory.FilterFactory;
import fi.muni.cz.core.factory.ProcessorFactory;
import fi.muni.cz.dataprocessing.issuesprocessing.IssueProcessingStrategy;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesSnapshotDao;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesSnapshotDaoImpl;
import fi.muni.cz.dataprovider.GitHubGeneralIssueDataProvider;
import fi.muni.cz.dataprovider.GitHubRepositoryInformationDataProvider;
import org.eclipse.egit.github.core.client.GitHubClient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SingleUrlExecution extends StraitExecution {

    private ReliabilityAnalysis analysis;
    private GitHubGeneralIssueDataProvider githubIssueDataProvider;
    private GitHubRepositoryInformationDataProvider githubRepositoryDataProvider;
    private GeneralIssuesSnapshotDao dao;



    public SingleUrlExecution() {
        this.githubIssueDataProvider = new GitHubGeneralIssueDataProvider(new GitHubClient());
        this.githubRepositoryDataProvider = new GitHubRepositoryInformationDataProvider(new GitHubClient());
        this.dao = new GeneralIssuesSnapshotDaoImpl();

    }

    @Override
    public void initializeAnalyses(ArgsParser configuration) {

        List<ReliabilityAnalysisPhase> analysisPhases = new ArrayList<>();

        analysisPhases.add(new GithubDataCollectionPhase(
                getDataSourceFromConfiguration(configuration),
                DataCollectionCacheMode.CACHE,
                githubIssueDataProvider,
                githubRepositoryDataProvider,
                dao
        ));

        analysisPhases.add(new IssueReportProcessingPhase(getStrategyFromConfiguration(configuration)));

        analysisPhases.add(
                new CumulativeIssueAmountCalculationPhase(configuration.getOptionValuePeriodOfTesting())
        );

        analysisPhases.add(new TimeBetweenIssuesCalculationPhase(configuration.getOptionValueTimeBetweenIssuesUnit()));

        analysisPhases.add(new TrendTestPhase());

        analysisPhases.add(new ModelFittingAndGoodnessOfFitTestPhase());

        analysisPhases.add(new HtmlReportOutputPhase());

        ReliabilityAnalysis reliabilityAnalysis = new ReliabilityAnalysis(configuration);
        reliabilityAnalysis.setPhases(analysisPhases);

        this.analysis = reliabilityAnalysis;

    }

    @Override
    public void execute() {
        analysis.performAnalysis(new ReliabilityAnalysisDto());
    }


    private List<DataSource> getDataSourceFromConfiguration(ArgsParser configuration) {
        DataSource dataSource = new DataSource();
        dataSource.setType("github");
        dataSource.setLocation(configuration.getOptionValueUrl());

        List<DataSource> sourceList = new ArrayList<>();
        sourceList.add(dataSource);
        return sourceList;
    }

    private IssueProcessingStrategy getStrategyFromConfiguration(ArgsParser configuration) {
        return new IssueProcessingStrategy(
                Stream.concat(
                        FilterFactory.getFilters(configuration).stream(),
                        ProcessorFactory.getProcessors(configuration).stream()).collect(
                        Collectors.toList()),
                "");
    }
}
