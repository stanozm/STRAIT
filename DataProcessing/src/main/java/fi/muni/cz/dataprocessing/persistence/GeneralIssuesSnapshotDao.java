package fi.muni.cz.dataprocessing.persistence;

import fi.muni.cz.dataprocessing.exception.DataProcessingException;
import java.util.List;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public interface GeneralIssuesSnapshotDao {
    
    /**
     * Save new snapshot to Database.
     * 
     * @param snapshot to save.
     */
    void save(GeneralIssuesSnapshot snapshot);
    
    /**
     * Get all saved snapshots.
     * 
     * @return list of GeneralIssuesSnapshot.
     */
    List<GeneralIssuesSnapshot> getAllSnapshots();
    
    /**
     * Get snapshot by name.
     * 
     * @param name  of snapshot.
     * @throws DataProcessingException  When there is no such snapshot.
     * @return GeneralIssuesSnapshot.
     */
    GeneralIssuesSnapshot getSnapshotByName(String name) throws DataProcessingException;

    /**
     * Delete snapshot by name.
     *
     * @param snapshot snapshot.
     * @throws DataProcessingException  When there is no such snapshot.
     */
    void deleteSnapshot(GeneralIssuesSnapshot snapshot) throws DataProcessingException;
    
    /**
     * Get snapshot by user and repositry name.
     * 
     * @param user          user name.
     * @param repository    repository name.
     * @return      list of  GeneralIssuesSnapshot for repository.
     */
    List<GeneralIssuesSnapshot> getAllSnapshotsForUserAndRepository(String user, String repository);
}
