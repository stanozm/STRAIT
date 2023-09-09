package fi.muni.cz.core.analysis.phases.datacollection;

import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.configuration.DataSource;
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

public class SnapshotDataCollectionPhase implements ReliabilityAnalysisPhase {

    private String snapshotName;
    private GeneralIssuesSnapshotDao localDataAccess;
    private List<GeneralIssuesCollection> issueReportCollections;

    public SnapshotDataCollectionPhase(
            GeneralIssuesSnapshotDao localDataAccess,
            String snapshotName
    ){
        this.issueReportCollections = Collections.synchronizedList(new ArrayList<>());
        this.localDataAccess = localDataAccess;
        this.snapshotName = snapshotName;
    }

    public ReliabilityAnalysisDto execute(ReliabilityAnalysisDto dto){

        collectDataFromSnapshot();

        dto.setIssueReportSets(issueReportCollections);

        return dto;
    }

    private void collectDataFromSnapshot(){
        GeneralIssuesCollection issuesCollection = localDataAccess.getSnapshotByName(snapshotName);
        if(issuesCollection == null){
            System.out.println("No such snapshot '" + snapshotName + "' in database.");
            System.exit(1);
        }
        issueReportCollections.add(localDataAccess.getSnapshotByName(snapshotName));
    }

}

