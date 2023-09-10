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
import fi.muni.cz.core.analysis.phases.output.writers.CsvFileBatchAnalysisReportWriter;
import fi.muni.cz.core.dto.BatchAnalysisConfiguration;
import fi.muni.cz.core.dto.DataSource;
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

public class BatchExecution extends StraitExecution {

    private List<ReliabilityAnalysis> analyses;
    private List<ReliabilityAnalysisDto> analysisData;
    private GitHubGeneralIssueDataProvider githubIssueDataProvider;
    private GitHubRepositoryInformationDataProvider githubRepositoryDataProvider;
    private GeneralIssuesSnapshotDao dao;
    private CsvFileBatchAnalysisReportWriter fileWriter;



    public BatchExecution() {
        this.githubIssueDataProvider = new GitHubGeneralIssueDataProvider(new GitHubClient());
        this.githubRepositoryDataProvider = new GitHubRepositoryInformationDataProvider(new GitHubClient());
        this.dao = new GeneralIssuesSnapshotDaoImpl();
        this.analyses = new ArrayList<>();
        this.analysisData = new ArrayList<>();
        this.fileWriter = new CsvFileBatchAnalysisReportWriter();
    }

    @Override
    public void initializeAnalyses(ArgsParser configuration) {

        List<DataSource> dataSources = getDataSourcesFromConfiguration(configuration);
        for(DataSource dataSource : dataSources) {
            analyses.add(getAnalysisBasedOnConfiguration(configuration, dataSource));
        }

    }

    @Override
    public void execute() {

        for(int i = 0; i<analyses.size(); i++) {
            ReliabilityAnalysis analysis = analyses.get(i);
            ReliabilityAnalysisDto dto = new ReliabilityAnalysisDto();

            analysisData.add(analysis.performAnalysis(dto));
        }

        fileWriter.writeBatchOutputDataToFile(analysisData);
    }

    private ReliabilityAnalysis getAnalysisBasedOnConfiguration(ArgsParser configuration, DataSource dataSource) {
        List<ReliabilityAnalysisPhase> analysisPhases = new ArrayList<>();

        List<DataSource> dataSources = new ArrayList<>();
        dataSources.add(dataSource);

        analysisPhases.add(new GithubDataCollectionPhase(
                dataSources,
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

        return reliabilityAnalysis;
    }

    private List<DataSource> getDataSourcesFromConfiguration(ArgsParser configuration) {

        List<String> errors = new ArrayList();
        BatchAnalysisConfiguration batchConfiguration = configuration.parseBatchAnalysisConfigurationFromFile(
                configuration.getOptionValueBatchConfigurationFile(),
                errors
        );

        return batchConfiguration.getDataSources();
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
