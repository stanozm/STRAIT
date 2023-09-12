package fi.muni.cz.core.analysis.phases.datacollection;

import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesCollection;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesSnapshotDao;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Valtteri Valtonen valtonenvaltteri@gmail.com
 */
public class SnapshotDataCollectionPhase implements ReliabilityAnalysisPhase {

    private String snapshotName;
    private GeneralIssuesSnapshotDao localDataAccess;
    private List<GeneralIssuesCollection> issueReportCollections;

    /**
     * Create new snapshot data collection phase
     * @param localDataAccess Database access object
     * @param snapshotName snapshot name
     */
    public SnapshotDataCollectionPhase(
            GeneralIssuesSnapshotDao localDataAccess,
            String snapshotName
    ){
        this.issueReportCollections = Collections.synchronizedList(new ArrayList<>());
        this.localDataAccess = localDataAccess;
        this.snapshotName = snapshotName;
    }

    /**
     * Execute this reliability analysis phase
     * @param dto Reliability analysis dto
     * @return Updated reliability analysis dto
     */
    public ReliabilityAnalysisDto execute(ReliabilityAnalysisDto dto){

        collectDataFromSnapshot();

        dto.setIssueReportSets(issueReportCollections);
        dto.addRepositoryInformationData(
                issueReportCollections.get(0).getRepositoryInformation(),
                issueReportCollections.get(0).getUrl(),
                issueReportCollections.get(0).getUserName()
        );

        return dto;
    }

    private void collectDataFromSnapshot(){
        System.out.println("On snapshot " + snapshotName);
        GeneralIssuesCollection issuesCollection = localDataAccess.getSnapshotByName(snapshotName);
        if(issuesCollection == null){
            System.out.println("No such snapshot '" + snapshotName + "' in database.");
            System.exit(1);
        }
        issueReportCollections.add(localDataAccess.getSnapshotByName(snapshotName));
    }

}

