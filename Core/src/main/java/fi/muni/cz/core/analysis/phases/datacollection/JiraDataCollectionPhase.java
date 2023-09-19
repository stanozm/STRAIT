package fi.muni.cz.core.analysis.phases.datacollection;

import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.dto.DataSource;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesCollection;
import fi.muni.cz.dataprovider.GeneralIssue;
import fi.muni.cz.dataprovider.JiraGeneralIssueDataProvider;
import fi.muni.cz.dataprovider.RepositoryInformation;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Valtteri Valtonen valtonenvaltteri@gmail.com
 */
public class JiraDataCollectionPhase implements ReliabilityAnalysisPhase {

    private List<DataSource> dataSources;
    private JiraGeneralIssueDataProvider jiraIssueDataProvider;
    private List<GeneralIssuesCollection> issueReportCollections;

    /**
     * Create new Jira data collection phase
     * @param dataSources List of Jira data sources to be used for this collection phase
     */

    public JiraDataCollectionPhase(
            List<DataSource> dataSources
    ){
        this.dataSources = dataSources;
        this.jiraIssueDataProvider = new JiraGeneralIssueDataProvider();
        this.issueReportCollections = new ArrayList<>();
    }

    /**
     * Execute this reliability analysis phase
     * @param dto Reliability analysis dto
     * @return Updated reliability analysis dto
     */
    public ReliabilityAnalysisDto execute(ReliabilityAnalysisDto dto){

        System.out.println("Collecting data from Jira");

        collectData();

        dto.setIssueReportSets(issueReportCollections);
        dto.addRepositoryInformationData(
                issueReportCollections.get(0).getRepositoryInformation(),
                issueReportCollections.get(0).getUrl(),
                issueReportCollections.get(0).getUserName()
        );

        return dto;
    }

    private void collectData(){
        dataSources.forEach(dataSource -> {
            System.out.println("On data source " + dataSource.getLocation());
            issueReportCollections.add(collectIssuesAndMetadataFromJira(dataSource));
        });
    }

    private GeneralIssuesCollection collectIssuesAndMetadataFromJira(DataSource jiraDataSource) {
        String url = jiraDataSource.getLocation();
        GeneralIssuesCollection issueCollection = new GeneralIssuesCollection();

        issueCollection.setListOfGeneralIssues(jiraIssueDataProvider.getIssuesByUrl(url));

        issueCollection.setSnapshotName(url);
        issueCollection.setUrl(url);
        issueCollection.setCreatedAt(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        issueCollection.setUserName("Jira");

        issueCollection.setRepositoryInformation(getRepositoryInformationForFileEvaluation(
                url, issueCollection.getListOfGeneralIssues())
        );

        return issueCollection;
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

}
