package fi.muni.cz.core.analysis.phases.datacollection;

import static
        fi.muni.cz.core.analysis.phases.datacollection.DataCollectionUtil.getRepositoryInformationForFileEvaluation;

import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.dto.DataSource;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesCollection;
import fi.muni.cz.dataprovider.BugzillaGeneralIssueDataProvider;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/** @author Valtteri Valtonen valtonenvaltteri@gmail.com */
public class BugzillaDataCollectionPhase implements ReliabilityAnalysisPhase {

  private List<DataSource> dataSources;
  private BugzillaGeneralIssueDataProvider bugzillaIssueDataProvider;
  private List<GeneralIssuesCollection> issueReportCollections;

  /**
   * Create new Jira data collection phase
   *
   * @param dataSources List of Jira data sources to be used for this collection phase
   */
  public BugzillaDataCollectionPhase(List<DataSource> dataSources) {
    this.dataSources = dataSources;
    this.bugzillaIssueDataProvider = new BugzillaGeneralIssueDataProvider();
    this.issueReportCollections = new ArrayList<>();
  }

  /**
   * Execute this reliability analysis phase
   *
   * @param dto Reliability analysis dto
   * @return Updated reliability analysis dto
   */
  public ReliabilityAnalysisDto execute(ReliabilityAnalysisDto dto) {

    System.out.println("Collecting data from Bugzilla");

    collectData();

    dto.setIssueReportSets(issueReportCollections);
    dto.addRepositoryInformationData(
        issueReportCollections.get(0).getRepositoryInformation(),
        issueReportCollections.get(0).getUrl(),
        issueReportCollections.get(0).getUserName());

    return dto;
  }

  private void collectData() {
    dataSources.forEach(
        dataSource -> {
          System.out.println("On data source " + dataSource.getLocation());
          issueReportCollections.add(collectIssuesAndMetadataFromBugzilla(dataSource));
        });
  }

  private GeneralIssuesCollection collectIssuesAndMetadataFromBugzilla(
      DataSource bugzillaDataSource) {
    String url = bugzillaDataSource.getLocation();
    GeneralIssuesCollection issueCollection = new GeneralIssuesCollection();

    issueCollection.setListOfGeneralIssues(bugzillaIssueDataProvider.getIssuesByUrl(url));

    issueCollection.setSnapshotName(url);
    issueCollection.setUrl(url);
    issueCollection.setCreatedAt(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
    issueCollection.setUserName("Bugzilla");

    issueCollection.setRepositoryInformation(
        getRepositoryInformationForFileEvaluation(url, issueCollection.getListOfGeneralIssues()));

    return issueCollection;
  }
}
