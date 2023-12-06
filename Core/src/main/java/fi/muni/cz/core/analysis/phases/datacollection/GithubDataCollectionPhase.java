package fi.muni.cz.core.analysis.phases.datacollection;

import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.dto.DataSource;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesCollection;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesSnapshotDao;
import fi.muni.cz.dataprovider.GitHubGeneralIssueDataProvider;
import fi.muni.cz.dataprovider.GitHubRepositoryInformationDataProvider;
import fi.muni.cz.dataprovider.RepositoryInformation;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** @author Valtteri Valtonen valtonenvaltteri@gmail.com */
public class GithubDataCollectionPhase implements ReliabilityAnalysisPhase {

  private List<DataSource> dataSources;
  private GitHubGeneralIssueDataProvider githubIssueDataProvider;
  private GitHubRepositoryInformationDataProvider githubRepositoryDataProvider;
  private GeneralIssuesSnapshotDao localDataAccess;

  private DatabaseUsageMode dbMode;

  private List<GeneralIssuesCollection> issueReportCollections;

  /**
   * Create new GitHub data collection phase
   *
   * @param dataSources List of GitHub data sources to be used for this collection phase
   * @param dbMode Cache mode
   * @param githubIssueDataProvider Github issue data provider
   * @param githubRepositoryDataProvider Github repository data provider
   * @param localDataAccess Object that provides database access
   */
  public GithubDataCollectionPhase(
      List<DataSource> dataSources,
      DatabaseUsageMode dbMode,
      GitHubGeneralIssueDataProvider githubIssueDataProvider,
      GitHubRepositoryInformationDataProvider githubRepositoryDataProvider,
      GeneralIssuesSnapshotDao localDataAccess) {
    this.dataSources = dataSources;
    this.issueReportCollections = Collections.synchronizedList(new ArrayList<>());

    this.githubIssueDataProvider = githubIssueDataProvider;
    this.githubRepositoryDataProvider = githubRepositoryDataProvider;

    this.localDataAccess = localDataAccess;

    this.dbMode = dbMode;
  }

  /**
   * Execute this reliability analysis phase
   *
   * @param dto Reliability analysis dto
   * @return Updated reliability analysis dto
   */
  public ReliabilityAnalysisDto execute(ReliabilityAnalysisDto dto) {

    System.out.println("Collecting data from Github");

    if (dbMode.equals(DatabaseUsageMode.DO_NOT_USE_DATABASE)) {
      collectDataWithoutCaching();
    }

    if (dbMode.equals(DatabaseUsageMode.USE_DATABASE_IF_SNAPSHOT_AVAILABLE)) {
      collectDataWithCaching();
    }

    if (dbMode.equals(DatabaseUsageMode.USE_DATABASE_AND_OVERWRITE_SNAPSHOTS)) {
      collectDataAndOverwriteCache();
    }

    dto.setIssueReportSets(issueReportCollections);
    dto.addRepositoryInformationData(
        issueReportCollections.get(0).getRepositoryInformation(),
        issueReportCollections.get(0).getUrl(),
        issueReportCollections.get(0).getUserName());

    return dto;
  }

  private void collectDataWithoutCaching() {
    dataSources.forEach(
        dataSource -> {
          System.out.println("On data source " + dataSource.getLocation());
          issueReportCollections.add(collectIssuesAndMetadataFromGithub(dataSource));
        });
  }

  private void collectDataWithCaching() {
    dataSources.forEach(
        dataSource -> {
          System.out.println("On data source " + dataSource.getLocation());
          GeneralIssuesCollection oldIssuesCollection = collectIssuesFromDatabase(dataSource);
          if (oldIssuesCollection != null) {
            issueReportCollections.add(oldIssuesCollection);
            return;
          }

          GeneralIssuesCollection newIssuesCollection =
              collectIssuesAndMetadataFromGithub(dataSource);
          saveIssuesToDatabase(newIssuesCollection);
          issueReportCollections.add(newIssuesCollection);
        });
  }

  private void collectDataAndOverwriteCache() {
    dataSources.forEach(
        dataSource -> {
          System.out.println("On data source " + dataSource.getLocation());
          GeneralIssuesCollection newIssuesCollection =
              collectIssuesAndMetadataFromGithub(dataSource);
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
    System.out.println("Attempting issues collection from database");
    return localDataAccess.getSnapshotByName(dataSource.getLocation());
  }

  private void saveIssuesToDatabase(GeneralIssuesCollection issueReportSet) {
    localDataAccess.save(issueReportSet);
  }

  private void overwriteIssuesInDatabase(GeneralIssuesCollection issueReportSet) {
    GeneralIssuesCollection oldIssuesCollection =
        localDataAccess.getSnapshotByName(issueReportSet.getSnapshotName());
    if (oldIssuesCollection != null) {
      localDataAccess.deleteSnapshot(oldIssuesCollection);
      localDataAccess.save(issueReportSet);
    }
  }
}
