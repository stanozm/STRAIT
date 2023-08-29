package fi.muni.cz.core;

import fi.muni.cz.core.configuration.DataSource;
import fi.muni.cz.core.dto.IssueReportSet;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesCollection;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesSnapshotDao;
import fi.muni.cz.dataprovider.GitHubGeneralIssueDataProvider;
import fi.muni.cz.dataprovider.GitHubRepositoryInformationDataProvider;
import fi.muni.cz.dataprovider.RepositoryInformation;
import org.apache.derby.client.am.DateTime;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataCollectionPhase implements ReliabilityAnalysisPhase{

    private List<DataSource> dataSources;
    private List<GeneralIssuesCollection> issueReportCollections;
    private GitHubGeneralIssueDataProvider githubIssueDataProvider;
    private GitHubRepositoryInformationDataProvider githubRepositoryDataProvider;

    private GeneralIssuesSnapshotDao localDataAccess;

    public DataCollectionPhase(
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
    }

    public ReliabilityAnalysisDto perform(ReliabilityAnalysisDto dto){
        return dto;
    }

    private void collectIssuesAndMetadataFromGithub(DataSource gitDataSource) {
        String url = gitDataSource.getLocation();
        GeneralIssuesCollection issueCollection = new GeneralIssuesCollection();

        RepositoryInformation repoInfo = githubRepositoryDataProvider.getRepositoryInformation(url);

        issueCollection.setListOfGeneralIssues(githubIssueDataProvider.getIssuesByUrl(url));
        issueCollection.setSnapshotName(url);
        issueCollection.setUrl(url);
        issueCollection.setCreatedAt(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        issueCollection.setUserName("Github");
        issueCollection.setRepositoryInformation(repoInfo);

        issueReportCollections.add(issueCollection);
    }

    private void collectIssuesFromDatabase(DataSource dataSource) {
        GeneralIssuesCollection issuesCollection = localDataAccess.getSnapshotByName(dataSource.getLocation());
        issueReportCollections.add(issuesCollection);
    }

    private void saveIssuesToDatabase(GeneralIssuesCollection issueReportSet) {
        localDataAccess.save(issueReportSet);
    }

}

