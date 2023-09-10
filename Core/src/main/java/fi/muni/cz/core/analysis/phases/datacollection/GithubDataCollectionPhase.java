package fi.muni.cz.core.analysis.phases.datacollection;

import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.dto.DataSource;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesCollection;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesSnapshotDao;
import fi.muni.cz.dataprovider.GitHubGeneralIssueDataProvider;
import fi.muni.cz.dataprovider.GitHubRepositoryInformationDataProvider;
import fi.muni.cz.dataprovider.RepositoryInformation;
import fi.muni.cz.dataprovider.utils.GitHubUrlParser;
import fi.muni.cz.dataprovider.utils.UrlParser;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GithubDataCollectionPhase implements ReliabilityAnalysisPhase {

    private List<DataSource> dataSources;
    private GitHubGeneralIssueDataProvider githubIssueDataProvider;
    private GitHubRepositoryInformationDataProvider githubRepositoryDataProvider;
    private GeneralIssuesSnapshotDao localDataAccess;

    private DataCollectionCacheMode cacheMode;

    private List<GeneralIssuesCollection> issueReportCollections;

    public GithubDataCollectionPhase(
            List<DataSource> dataSources,
            DataCollectionCacheMode cacheMode,
            GitHubGeneralIssueDataProvider githubIssueDataProvider,
            GitHubRepositoryInformationDataProvider githubRepositoryDataProvider,
            GeneralIssuesSnapshotDao localDataAccess
    ){
        this.dataSources = dataSources;
        this.issueReportCollections = Collections.synchronizedList(new ArrayList<>());

        this.githubIssueDataProvider = githubIssueDataProvider;
        this.githubRepositoryDataProvider = githubRepositoryDataProvider;

        this.localDataAccess = localDataAccess;

        this.cacheMode = cacheMode;
    }

    public ReliabilityAnalysisDto execute(ReliabilityAnalysisDto dto){

        if(cacheMode.equals(DataCollectionCacheMode.NO_CACHE)){
            collectDataWithoutCaching();
        }

        if(cacheMode.equals(DataCollectionCacheMode.CACHE)){
            collectDataWithCaching();
        }

        if(cacheMode.equals(DataCollectionCacheMode.OVERWRITE_CACHE)){
            collectDataAndOverwriteCache();
        }

        dto.setIssueReportSets(issueReportCollections);
        dto.addRepositoryInformationData(
                issueReportCollections.get(0).getRepositoryInformation(),
                dataSources.get(0).getLocation(),
                "Github"
        );

        return dto;
    }

    public void collectDataWithoutCaching(){
        dataSources.forEach(dataSource -> {
            issueReportCollections.add(collectIssuesAndMetadataFromGithub(dataSource));
        });
    }

    public void collectDataWithCaching(){
        dataSources.forEach(dataSource -> {
            GeneralIssuesCollection oldIssuesCollection = collectIssuesFromDatabase(dataSource);
            if(oldIssuesCollection != null){
                issueReportCollections.add(oldIssuesCollection);
                return;
            }

            GeneralIssuesCollection newIssuesCollection = collectIssuesAndMetadataFromGithub(dataSource);
            saveIssuesToDatabase(newIssuesCollection);
            issueReportCollections.add(newIssuesCollection);
        });
    }

    public void collectDataAndOverwriteCache(){
        dataSources.forEach(dataSource -> {
            GeneralIssuesCollection newIssuesCollection = collectIssuesAndMetadataFromGithub(dataSource);
            issueReportCollections.add(newIssuesCollection);
            overwriteIssuesInDatabase(newIssuesCollection);
        });
    }



    private GeneralIssuesCollection collectIssuesAndMetadataFromGithub(DataSource gitDataSource) {
        String url = gitDataSource.getLocation();
        GeneralIssuesCollection issueCollection = new GeneralIssuesCollection();

        RepositoryInformation repoInfo = githubRepositoryDataProvider.getRepositoryInformation(url);

        issueCollection.setListOfGeneralIssues(githubIssueDataProvider.getIssuesByUrl(url));
        issueCollection.setSnapshotName(url);
        issueCollection.setUrl(url);
        issueCollection.setCreatedAt(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        issueCollection.setUserName("Github");
        issueCollection.setRepositoryInformation(repoInfo);

        return issueCollection;
    }

    private GeneralIssuesCollection collectIssuesFromDatabase(DataSource dataSource) {
        GeneralIssuesCollection issuesCollection = localDataAccess.getSnapshotByName(dataSource.getLocation());
        return issuesCollection;
    }

    private void saveIssuesToDatabase(GeneralIssuesCollection issueReportSet) {
        localDataAccess.save(issueReportSet);
    }

    private void overwriteIssuesInDatabase(GeneralIssuesCollection issueReportSet) {
        GeneralIssuesCollection oldIssuesCollection =
                localDataAccess.getSnapshotByName(issueReportSet.getSnapshotName());
        if(oldIssuesCollection != null){
            localDataAccess.deleteSnapshot(oldIssuesCollection);
            localDataAccess.save(issueReportSet);
        }
    }

}

