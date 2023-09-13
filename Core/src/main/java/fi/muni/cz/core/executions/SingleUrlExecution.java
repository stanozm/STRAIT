package fi.muni.cz.core.executions;

import static fi.muni.cz.dataprocessing.issuesprocessing.modeldata.IssuesCounter.HOURS;
import static fi.muni.cz.dataprocessing.issuesprocessing.modeldata.IssuesCounter.WEEKS;

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
import fi.muni.cz.core.dto.DataSource;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.core.factory.FilterFactory;
import fi.muni.cz.core.factory.ModelFactory;
import fi.muni.cz.core.factory.ProcessorFactory;
import fi.muni.cz.dataprocessing.issuesprocessing.IssueProcessingStrategy;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesSnapshotDao;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesSnapshotDaoImpl;
import fi.muni.cz.dataprovider.GitHubGeneralIssueDataProvider;
import fi.muni.cz.dataprovider.GitHubRepositoryInformationDataProvider;
import fi.muni.cz.dataprovider.authenticationdata.GitHubAuthenticationDataProvider;
import org.eclipse.egit.github.core.client.GitHubClient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Valtteri Valtonen valtonenvaltteri@gmail.com
 */
public class SingleUrlExecution extends StraitExecution {

    private ReliabilityAnalysis analysis;
    private GitHubGeneralIssueDataProvider githubIssueDataProvider;
    private GitHubRepositoryInformationDataProvider githubRepositoryDataProvider;
    private GeneralIssuesSnapshotDao dao;


    /**
     * Create new single url execution.
     */
    public SingleUrlExecution() {
        GitHubClient gitHubClient = new GitHubAuthenticationDataProvider().getGitHubClientWithCreditials();
        this.githubIssueDataProvider = new GitHubGeneralIssueDataProvider(gitHubClient);
        this.githubRepositoryDataProvider = new GitHubRepositoryInformationDataProvider(gitHubClient);
        this.dao = new GeneralIssuesSnapshotDaoImpl();

    }

    @Override
    public void initializeAnalyses(ArgsParser configuration) {

        String periodOfTestingValue = configuration.getOptionValuePeriodOfTesting() != null
                ? configuration.getOptionValuePeriodOfTesting() :
                WEEKS;

        String timeBetweenIssuesUnitValue = configuration.getOptionValueTimeBetweenIssuesUnit() != null
                ? configuration.getOptionValueTimeBetweenIssuesUnit() :
                HOURS;

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
                new CumulativeIssueAmountCalculationPhase(periodOfTestingValue)
        );

        analysisPhases.add(new TimeBetweenIssuesCalculationPhase(timeBetweenIssuesUnitValue));

        analysisPhases.add(new TrendTestPhase());

        analysisPhases.add(new ModelFittingAndGoodnessOfFitTestPhase(ModelFactory.getREngine()));

        analysisPhases.add(new HtmlReportOutputPhase());

        ReliabilityAnalysis reliabilityAnalysis = new ReliabilityAnalysis(analysisPhases);

        this.analysis = reliabilityAnalysis;

    }

    @Override
    public void execute(ArgsParser configuration) {
        System.out.println("Executing STRAIT in single URL analysis mode");
        analysis.performAnalysis(new ReliabilityAnalysisDto(configuration));
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
