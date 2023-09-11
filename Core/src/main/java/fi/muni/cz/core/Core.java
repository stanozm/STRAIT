package fi.muni.cz.core;

import static fi.muni.cz.dataprocessing.issuesprocessing.MovingAverage.calculateMovingAverage;
import static java.lang.Math.round;

import fi.muni.cz.core.configuration.BatchAnalysisConfiguration;
import fi.muni.cz.core.configuration.DataSource;
import fi.muni.cz.core.exception.InvalidInputException;
import fi.muni.cz.core.factory.FilterFactory;
import fi.muni.cz.core.factory.IssuesWriterFactory;
import fi.muni.cz.core.factory.ModelFactory;
import fi.muni.cz.core.factory.OutputWriterFactory;
import fi.muni.cz.core.factory.ProcessorFactory;
import fi.muni.cz.dataprocessing.issuesprocessing.IssueProcessingStrategy;
import fi.muni.cz.dataprocessing.issuesprocessing.modeldata.CumulativeIssuesCounter;
import fi.muni.cz.dataprocessing.issuesprocessing.modeldata.IssuesCounter;
import fi.muni.cz.dataprocessing.issuesprocessing.modeldata.TimeBetweenIssuesCounter;
import fi.muni.cz.dataprocessing.output.CsvFileBatchAnalysisReportWriter;
import fi.muni.cz.dataprocessing.output.OutputData;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesSnapshot;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesSnapshotDaoImpl;
import fi.muni.cz.dataprovider.BugzillaGeneralIssueDataProvider;
import fi.muni.cz.dataprovider.GeneralIssue;
import fi.muni.cz.dataprovider.GeneralIssueDataProvider;
import fi.muni.cz.dataprovider.GitHubGeneralIssueDataProvider;
import fi.muni.cz.dataprovider.GitHubRepositoryInformationDataProvider;
import fi.muni.cz.dataprovider.JiraGeneralIssueDataProvider;
import fi.muni.cz.dataprovider.Release;
import fi.muni.cz.dataprovider.RepositoryInformation;
import fi.muni.cz.dataprovider.RepositoryInformationDataProvider;
import fi.muni.cz.dataprovider.authenticationdata.GitHubAuthenticationDataProvider;
import fi.muni.cz.dataprovider.utils.GitHubUrlParser;
import fi.muni.cz.dataprovider.utils.ParsedUrlData;
import fi.muni.cz.dataprovider.utils.UrlParser;
import fi.muni.cz.models.Model;
import fi.muni.cz.models.exception.ModelException;
import fi.muni.cz.models.testing.ChiSquareGoodnessOfFitTest;
import fi.muni.cz.models.testing.GoodnessOfFitTest;
import fi.muni.cz.models.testing.LaplaceTrendTest;
import fi.muni.cz.models.testing.TrendTest;
import org.apache.commons.math3.util.Pair;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.rosuda.JRI.Rengine;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class Core {

    private static final ArgsParser PARSER = new ArgsParser();
    private static final GitHubClient CLIENT = new GitHubAuthenticationDataProvider().getGitHubClientWithCreditials();
    private static final GeneralIssueDataProvider GITHUB_ISSUES_DATA_PROVIDER =
            new GitHubGeneralIssueDataProvider(CLIENT);
    private static final GeneralIssueDataProvider JIRA_ISSUES_DATA_PROVIDER = new JiraGeneralIssueDataProvider();
    private static final GeneralIssueDataProvider BUGZILLA_ISSUES_DATA_PROVIDER =
            new BugzillaGeneralIssueDataProvider();
    private static final RepositoryInformationDataProvider REPOSITORY_DATA_PROVIDER =
            new GitHubRepositoryInformationDataProvider(CLIENT);
    private static ParsedUrlData parsedUrlData;
    private static final GeneralIssuesSnapshotDaoImpl DAO = new GeneralIssuesSnapshotDaoImpl();
    private static final Rengine RENGINE = new Rengine(new String[] {"--vanilla"}, false, null);

    /**
     * Main method, takes command line arguments.
     * 
     * @param args  command line arguments
     */
    public static void main(String[] args) {
        try {
            PARSER.parse(args);
            run();
        } catch (InvalidInputException e) {
            PARSER.printHelp();
            System.out.println(e.causes());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void run() throws InvalidInputException {
        System.out.println("Working...");
        Instant start = Instant.now();
        ModelFactory.setREngine(RENGINE);
        switch (PARSER.getRunConfiguration()) {
            case LIST_ALL_SNAPSHOTS:
               doListAllSnapshots();
               break;
            case HELP:
                PARSER.printHelp();
                break;
            case BATCH_AND_EVALUATE:
                doBatchAnalysis();
                break;
            case URL_AND_SAVE:
                checkGithubUrl(PARSER.getOptionValueUrl());
                doSaveToFileFromUrl();
                System.out.println("Saved to file.");
                break;
            case URL_AND_LIST_SNAPSHOTS:
                checkGithubUrl(PARSER.getOptionValueUrl());
                doListSnapshotsForUrl();
                break;
            case URL_AND_EVALUATE:
                checkGithubUrl(PARSER.getOptionValueUrl());
                doEvaluateForGithubUrl();
                System.out.println("Evaluated to file.");
                break;
            case SNAPSHOT_NAME_AND_SAVE:
                doSaveToFileFromSnapshot();
                System.out.println("Saved.");
                break;
            case SNAPSHOT_NAME_AND_EVALUATE:
                doEvaluateForSnapshot();
                System.out.println("Evaluated.");
                break;
            case SNAPSHOT_NAME_AND_LIST_SNAPSHOTS:
                PARSER.printHelp();
                System.out.println("[Can't combine '-sn' with '-sl']");
                break;
            case NOT_SUPPORTED:
                PARSER.printHelp();
                System.out.println("[Missing option: '-e' / '-s']");
                break;
            default:
                PARSER.printHelp();
                System.out.println("[Missing option: '-e' / '-s' / '-sl']");
        }
        System.out.println("Done! Duration - " + Duration.between(start, Instant.now()).toMinutes() + "min");
        System.exit(0);
    }

    private static void doBatchAnalysis() throws InvalidInputException {
        System.out.println("Starting batch processing");
        BatchAnalysisConfiguration batchConfiguration = getBatchAnalysisConfiguration();
        List<List<OutputData>> outputs = new ArrayList<>();
        for(DataSource dataSource : batchConfiguration.getDataSources()){
            if(dataSource.getType().equals("github")){
                checkGithubUrl(dataSource.getLocation());
                outputs.add(doEvaluateForGithubUrl());
            }
            if(dataSource.getType().equals("jira")){
                checkJiraPath(dataSource.getLocation());
                outputs.add(doEvaluateForJiraPath());
            }
            if(dataSource.getType().equals("bugzilla")){
                checkBugzillaPath(dataSource.getLocation());
                outputs.add(doEvaluateForBugzillaPath());
            }
        }
        System.out.println("Writing batch report");
        new CsvFileBatchAnalysisReportWriter().writeBatchOutputDataToFile(outputs, "batchAnalysisReport");
    }

    private static BatchAnalysisConfiguration getBatchAnalysisConfiguration() {
        List<String> errors = Collections.EMPTY_LIST;
        String filePath = PARSER.getOptionValueBatchConfigurationFile();
        BatchAnalysisConfiguration batchConfiguration =
                PARSER.parseBatchAnalysisConfigurationFromFile(filePath, errors);
        return batchConfiguration;
    }


    private static List<OutputData> doEvaluateForJiraPath() throws InvalidInputException {
        String url = parsedUrlData.getUrl();
        System.out.println("On Jira CSV file - " + url);

        List<GeneralIssue> listOfGeneralIssues = JIRA_ISSUES_DATA_PROVIDER.getIssuesByUrl(url);

        RepositoryInformation repositoryInformation = getRepositoryInformationForFileEvaluation(
                url,
                listOfGeneralIssues
        );

        return doEvaluate(listOfGeneralIssues, repositoryInformation);
    }

    private static void checkJiraPath(String jiraPath) {
        ParsedUrlData urlData = new ParsedUrlData(jiraPath, "Jira", jiraPath);
        parsedUrlData = urlData;
    }

    private static List<OutputData> doEvaluateForBugzillaPath() throws InvalidInputException {
        String url = parsedUrlData.getUrl();
        System.out.println("On Bugzilla CSV file - " + url);
        List<GeneralIssue> listOfGeneralIssues = BUGZILLA_ISSUES_DATA_PROVIDER.getIssuesByUrl(url);

        RepositoryInformation repositoryInformation = getRepositoryInformationForFileEvaluation(
                url,
                listOfGeneralIssues
        );

        return doEvaluate(listOfGeneralIssues, repositoryInformation);
    }

    private static void checkBugzillaPath(String bugzillaPath) {
        ParsedUrlData urlData = new ParsedUrlData(bugzillaPath, "Bugzilla", bugzillaPath);
        parsedUrlData = urlData;
    }

    private static RepositoryInformation getRepositoryInformationForFileEvaluation(
            String filePath,
            List<GeneralIssue> listOfGeneralIssues
    ) {
        RepositoryInformation repositoryInformation = new RepositoryInformation();
        repositoryInformation.setName(filePath);
        repositoryInformation.setContributors(1);
        repositoryInformation.setDescription("CSV repository");
        repositoryInformation.setForks(0);
        repositoryInformation.setSize(0);
        repositoryInformation.setPushedAtFirst(listOfGeneralIssues.get(0).getCreatedAt());
        repositoryInformation.setPushedAt(listOfGeneralIssues.get(listOfGeneralIssues.size()-1).getCreatedAt());
        return repositoryInformation;
    }


    private static List<OutputData> doEvaluateForGithubUrl() throws InvalidInputException {
        System.out.println("On repository - " + parsedUrlData.getUrl());

        String snapshotName = parsedUrlData.getRepositoryName() + " " + parsedUrlData.getUrl();

        List<GeneralIssue> listOfGeneralIssues = new ArrayList<>();
        RepositoryInformation repositoryInformation = new RepositoryInformation();

        if(!PARSER.hasOptionNewSnapshot()) {
            GeneralIssuesSnapshot oldSnapshot = DAO.getSnapshotByName(snapshotName);

            listOfGeneralIssues = oldSnapshot != null ?
                    oldSnapshot.getListOfGeneralIssues() :
                    GITHUB_ISSUES_DATA_PROVIDER.getIssuesByUrl(parsedUrlData.getUrl());

            repositoryInformation = oldSnapshot != null ?
                    oldSnapshot.getRepositoryInformation() :
                    REPOSITORY_DATA_PROVIDER.getRepositoryInformation(parsedUrlData.getUrl());
        }

        if (PARSER.hasOptionNewSnapshot()) {
            GeneralIssuesSnapshot oldSnapshot = DAO.getSnapshotByName(snapshotName);
            boolean doOverWrite = PARSER.getOptionValueNewSnapshot().equals("overwrite");

            if (oldSnapshot != null && doOverWrite) {
                System.out.println("Deleting old snapshot...");
                DAO.deleteSnapshot(oldSnapshot);
            }

            if (oldSnapshot == null || doOverWrite){
                listOfGeneralIssues = GITHUB_ISSUES_DATA_PROVIDER.getIssuesByUrl(parsedUrlData.getUrl());
                repositoryInformation = REPOSITORY_DATA_PROVIDER
                        .getRepositoryInformation(parsedUrlData.getUrl());

                System.out.println("Preparing new snapshot... " + listOfGeneralIssues.size());
                prepareGeneralIssuesSnapshotAndSave(listOfGeneralIssues, repositoryInformation, snapshotName);
            }

            if (oldSnapshot != null && !doOverWrite) {
                listOfGeneralIssues = oldSnapshot.getListOfGeneralIssues();
                repositoryInformation = oldSnapshot.getRepositoryInformation();
            }
        }
        return doEvaluate(listOfGeneralIssues, repositoryInformation);
    }
    
    private static void prepareGeneralIssuesSnapshotAndSave(
            List<GeneralIssue> listOfGeneralIssues,
            RepositoryInformation repositoryInformation,
            String snapshotName
    ) {
        DAO.save(new GeneralIssuesSnapshot.GeneralIssuesSnapshotBuilder()
                    .setCreatedAt(new Date())
                    .setListOfGeneralIssues(listOfGeneralIssues)
                    .setRepositoryName(repositoryInformation.getName())
                    .setUrl(parsedUrlData.getUrl())
                    .setUserName(parsedUrlData.getUserName())
                    .setSnapshotName(snapshotName)
                    .setRepositoryInformation(repositoryInformation)
                    .build());
    }
    
    private static void doEvaluateForSnapshot() throws InvalidInputException {
        GeneralIssuesSnapshot snapshot = DAO.getSnapshotByName(PARSER.getOptionValueSnapshotName());
        if (snapshot == null) {
            System.out.println("No such snapshot '" + PARSER.getOptionValueSnapshotName() + "' in database.");
            System.exit(1);
        }
        checkGithubUrl(snapshot.getUrl());
        System.out.println("On repository - " + snapshot.getUrl());
        doEvaluate(snapshot.getListOfGeneralIssues(), snapshot.getRepositoryInformation());
    }

    private static String getPeriodOfTesting() {
        if (PARSER.hasOptionPeriodOfTestiong()) {
            return PARSER.getOptionValuePeriodOfTesting();
        }
        return IssuesCounter.WEEKS;
    }
    
    private static String getTimeBetweenIssuesUnit() {
        if (PARSER.hasOptionTimeBetweenIssuesUnit()) {
            return PARSER.getOptionValueTimeBetweenIssuesUnit();
        }
        return IssuesCounter.HOURS;
    }
    
    private static List<Pair<Integer, Integer>> getTimeBetweenIssuesList(List<GeneralIssue> listOfGeneralIssues) {
        return new TimeBetweenIssuesCounter(getTimeBetweenIssuesUnit())
                        .countIssues(listOfGeneralIssues);
    }
    
    private static List<Pair<Integer, Integer>> getCumulativeIssuesList(List<GeneralIssue> listOfGeneralIssues) {
        List<Pair<Integer, Integer>> cumulativeIssues =
                new CumulativeIssuesCounter(getPeriodOfTesting())
                .countIssues(listOfGeneralIssues);
        return PARSER.hasOptionMovingAverage() ?
                calculateMovingAverage(cumulativeIssues, Integer.parseInt(PARSER.getOptionValueMovingAverage()))
                :
                cumulativeIssues;
    }
    
    private static List<Model> runModels(
            List<Pair<Integer, Integer>> trainingData,
            List<Pair<Integer, Integer>> testData,
            GoodnessOfFitTest goodnessOfFitTest
    ) throws InvalidInputException {
        List<Model> models = ModelFactory.getModels(trainingData, testData, goodnessOfFitTest, PARSER);
        List<Model> modelsToRemove = new ArrayList<>();

        if (trainingData.isEmpty()
                || trainingData.get(trainingData.size() - 1).getSecond() < 1) {
           return new ArrayList<>();
        }

        if (testData.isEmpty()
                || testData.get(testData.size() - 1).getSecond() < 1) {
            return new ArrayList<>();
        }

        models.parallelStream().forEach(model -> {
            try {
                System.out.println("Evaluating - " + model.toString());
                model.estimateModelData();
            } catch (ModelException ex) {
                System.out.println("Ignored model - " + model.toString());
                modelsToRemove.add(model);
            }
        });

        models.removeAll(modelsToRemove);
        return models;
    }
    
    private static GoodnessOfFitTest getGoodnessOfFitTest() {
        return new ChiSquareGoodnessOfFitTest(RENGINE);
    }
    
    private static TrendTest runTrendTest(List<GeneralIssue> listOfGeneralIssues) {
        TrendTest trendTest = new LaplaceTrendTest(getTimeBetweenIssuesUnit());
        trendTest.executeTrendTest(listOfGeneralIssues);
        return trendTest;
    }
    
    private static int getLengthOfPrediction() {
        if (PARSER.hasOptionPredict()) {
            try {
                return Integer.parseInt(PARSER.getOptionValuePredict());
            } catch (NumberFormatException e) {
                System.out.println("[Argument of option '-p' is not a number]");
                System.exit(1);
            }
        }
        return 0;
    }
    
    private static void writeOutput(List<OutputData> outputDataList) throws InvalidInputException {
        OutputWriterFactory.getIssuesWriter(PARSER)
                .writeOutputDataToFile(outputDataList,
                        PARSER.getOptionValueEvaluation() == null
                                ? parsedUrlData.getRepositoryName() : PARSER.getOptionValueEvaluation());
    }
    
    private static List<OutputData> prepareOutputData(
            int initialNumberOfIssues,
            List<GeneralIssue> listOfGeneralIssues,
            List<Pair<Integer, Integer>> completeData,
            List<Pair<Integer, Integer>> trainingData,
            List<Pair<Integer, Integer>> testData,
            TrendTest trendTest,
            RepositoryInformation repositoryInformation,
            IssueProcessingStrategy issueProcessingStrategy)
            throws InvalidInputException {
        List<OutputData> outputDataList = new ArrayList<>();
        OutputData outputData;
        for (Model model: runModels(trainingData, testData, getGoodnessOfFitTest())) {
            outputData = new OutputData.OutputDataBuilder()
                    .setCreatedAt(new Date())
                    .setRepositoryName(parsedUrlData.getRepositoryName())
                    .setUrl(parsedUrlData.getUrl().toString())
                    .setUserName(parsedUrlData.getUserName())
                    .setTotalNumberOfDefects(completeData.get(completeData.size() - 1).getSecond())
                    .setCumulativeDefects(completeData)
                    .setTimeBetweenDefects(getTimeBetweenIssuesList(listOfGeneralIssues))
                    .setTrend(trendTest.getTrendValue())
                    .setExistTrend(trendTest.getResult())
                    .setModelParameters(model.getModelParameters())
                    .setGoodnessOfFit(model.getGoodnessOfFitData())
                    .setPredictiveAccuracy(model.getPredictiveAccuracyData())
                    .setEstimatedIssuesPrediction(model.getIssuesPrediction(
                            testData.size() + (double) getLengthOfPrediction())
                    )
                    .setModelName(model.toString())
                    .setModelFunction(model.getTextFormOfTheFunction())
                    .setInitialNumberOfIssues(initialNumberOfIssues)
                    .setFiltersUsed(FilterFactory.getFiltersRanWithInfoAsList(PARSER))
                    .setProcessorsUsed(ProcessorFactory.getProcessorsRanWithInfoAsList(PARSER))
                    .setIssueProcessingActionResults(issueProcessingStrategy.getIssueProcessingActionResults())
                    .setTestingPeriodsUnit(getPeriodOfTesting())
                    .setTimeBetweenDefectsUnit(getTimeBetweenIssuesUnit())
                    .setSolver(getSolver())
                    .setRepositoryContributors(repositoryInformation.getContributors())
                    .setRepositoryDescription(repositoryInformation.getDescription())
                    .setRepositoryForks(repositoryInformation.getForks())
                    .setRepositoryLastPushedAt(repositoryInformation.getPushedAt())
                    .setRepositoryFirstPushedAt(repositoryInformation.getPushedAtFirst())
                    .setRepositorySize(repositoryInformation.getSize())
                    .setRepositoryWatchers(repositoryInformation.getWatchers())
                    .setDevelopmentDays(getDaysBetween(repositoryInformation))
                    .setReleases(repositoryInformation.getListOfReleases()
                            .stream().map(Release::toDto).collect(Collectors.toList()))
                    .build();
            outputDataList.add(outputData);
        }

        if (outputDataList.isEmpty()) {
            outputData = getOutputDataForNoModels(initialNumberOfIssues, listOfGeneralIssues,
                    trainingData, trendTest, repositoryInformation, issueProcessingStrategy);
            outputDataList.add(outputData);
        }

        return outputDataList;
    }

    private static OutputData getOutputDataForNoModels(int initialNumberOfIssues,
                                                       List<GeneralIssue> listOfGeneralIssues,
                                                       List<Pair<Integer, Integer>> countedWeeksWithTotal,
                                                       TrendTest trendTest,
                                                       RepositoryInformation repositoryInformation,
                                                       IssueProcessingStrategy issueProcessingStrategy) {
        return new OutputData.OutputDataBuilder()
                .setCreatedAt(new Date())
                .setRepositoryName(parsedUrlData.getRepositoryName())
                .setUrl(parsedUrlData.getUrl().toString())
                .setUserName(parsedUrlData.getUserName())
                .setTotalNumberOfDefects(countedWeeksWithTotal.isEmpty() ? 0 :
                        countedWeeksWithTotal.get(countedWeeksWithTotal.size() - 1).getSecond())
                .setCumulativeDefects(countedWeeksWithTotal)
                .setTimeBetweenDefects(getTimeBetweenIssuesList(listOfGeneralIssues))
                .setTrend(trendTest.getTrendValue())
                .setExistTrend(trendTest.getResult())
                .setInitialNumberOfIssues(initialNumberOfIssues)
                .setFiltersUsed(FilterFactory.getFiltersRanWithInfoAsList(PARSER))
                .setProcessorsUsed(ProcessorFactory.getProcessorsRanWithInfoAsList(PARSER))
                .setIssueProcessingActionResults(issueProcessingStrategy.getIssueProcessingActionResults())
                .setTestingPeriodsUnit(getPeriodOfTesting())
                .setTimeBetweenDefectsUnit(getTimeBetweenIssuesUnit())
                .setSolver(getSolver())
                .setRepositoryContributors(repositoryInformation.getContributors())
                .setRepositoryDescription(repositoryInformation.getDescription())
                .setRepositoryForks(repositoryInformation.getForks())
                .setRepositoryLastPushedAt(repositoryInformation.getPushedAt())
                .setRepositoryFirstPushedAt(repositoryInformation.getPushedAtFirst())
                .setRepositorySize(repositoryInformation.getSize())
                .setRepositoryWatchers(repositoryInformation.getWatchers())
                .setDevelopmentDays(getDaysBetween(repositoryInformation))
                .setReleases(repositoryInformation.getListOfReleases()
                        .stream().map(Release::toDto).collect(Collectors.toList()))
                .build();
    }

    private static String getSolver() {
        if (PARSER.hasOptionSolver()) {
            if (PARSER.getOptionValueSolver().equals(ModelFactory.SOLVER_LEAST_SQUARES)) {
                return "Least Squares";
            } else if (PARSER.getOptionValueSolver().equals(ModelFactory.SOLVER_MAXIMUM_LIKELIHOOD)) {
                return "Maximum Likelihood";
            }
        }
        return "Least Squares";
    }
    
    private static List<OutputData> doEvaluate(List<GeneralIssue> listOfGeneralIssues,
                                               RepositoryInformation repositoryInformation)
            throws InvalidInputException {
        System.out.println("Evaluating ...");

        IssueProcessingStrategy issueProcessingStrategy = getStrategyFromParser();

        List<GeneralIssue> filteredAndProcessedList = issueProcessingStrategy.apply(listOfGeneralIssues,
                repositoryInformation
        );

        if(filteredAndProcessedList.isEmpty()){
            List<OutputData> outputList = new ArrayList<>();
            System.out.println("No issues left for analysis. Skipping project.");
            return outputList;
        }

        List<Pair<Integer, Integer>> completeData  = getCumulativeIssuesList(filteredAndProcessedList);
        float trainingDataPortion = 0.66f;
        int trainingDataEndIndex = Math.round(trainingDataPortion * completeData.size());

        List<Pair<Integer, Integer>> trainingData = completeData.subList(0, trainingDataEndIndex);
        List<Pair<Integer, Integer>> testData = completeData.subList(trainingDataEndIndex, completeData.size());


        TrendTest trendTest = runTrendTest(filteredAndProcessedList); 
        List<OutputData> outputDataList = 
                prepareOutputData(listOfGeneralIssues.size(), 
                        filteredAndProcessedList,
                        completeData,
                        trainingData,
                        testData,
                        trendTest,
                        repositoryInformation,
                        issueProcessingStrategy);
        writeOutput(outputDataList);
        return outputDataList;
    }

    private static IssueProcessingStrategy getStrategyFromParser(){
        return new IssueProcessingStrategy(
                Stream.concat(
                        FilterFactory.getFilters(PARSER).stream(),
                        ProcessorFactory.getProcessors(PARSER).stream()).collect(
                        Collectors.toList()),
                "");
    }

    private static void doSaveToFileFromUrl() throws InvalidInputException {
        List<GeneralIssue> listOfInitialIssues = GITHUB_ISSUES_DATA_PROVIDER.
                getIssuesByUrl(PARSER.getOptionValueUrl());
        doSaveToFile(listOfInitialIssues, parsedUrlData.getRepositoryName());
    }
    
    private static void doSaveToFileFromSnapshot() throws InvalidInputException {
        String snapshotName = PARSER.getOptionValueSnapshotName();
        doSaveToFile(DAO.getSnapshotByName(snapshotName).getListOfGeneralIssues(), snapshotName);
    }
    
    private static void doSaveToFile(List<GeneralIssue> listOfInitialIssues, String fileName) 
            throws InvalidInputException {
        IssuesWriterFactory
                .getIssuesWriter(PARSER)
                .writeToFile(
                        getStrategyFromParser().apply(listOfInitialIssues, null), fileName
                );
    }

    private static void doListAllSnapshots() {
        List<GeneralIssuesSnapshot> listFromDB = DAO.getAllSnapshots();
        if (listFromDB.isEmpty()) {
            System.out.println("No snapshots in Database.");
        } else {
            for (GeneralIssuesSnapshot snap: listFromDB) {
                System.out.println(snap);
            }
        }
    }

    private static UrlParser getUrlParser() {
        return new GitHubUrlParser();
    }
    
    private static void checkGithubUrl(String url) {
        UrlParser urlParser = getUrlParser();
        parsedUrlData = urlParser.parseUrlAndCheck(url);
    }

    private static void doListSnapshotsForUrl() {
        List<GeneralIssuesSnapshot> listFromDB = DAO.
                getAllSnapshotsForUserAndRepository(parsedUrlData.getUserName(), 
                        parsedUrlData.getRepositoryName());
        for (GeneralIssuesSnapshot snap: listFromDB) {
            System.out.println(snap);
        }
    }

    private static long getDaysBetween(RepositoryInformation repositoryInformation) {
        return TimeUnit.DAYS.convert(Math.abs(repositoryInformation.getPushedAt().getTime()
                - repositoryInformation.getPushedAtFirst().getTime()), TimeUnit.MILLISECONDS);
    }
}
