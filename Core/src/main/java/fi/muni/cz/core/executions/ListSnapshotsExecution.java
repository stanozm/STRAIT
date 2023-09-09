package fi.muni.cz.core.executions;

import fi.muni.cz.core.ArgsParser;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesCollection;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesSnapshotDao;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesSnapshotDaoImpl;
import java.util.List;

public class ListSnapshotsExecution extends StraitExecution{

    private GeneralIssuesSnapshotDao dao;

    @Override
    public void initializeAnalyses(ArgsParser configuration) {
        this.dao = new GeneralIssuesSnapshotDaoImpl();
    }

    @Override
    public void execute() {
        listAllSnapshots();
    }

    private void listAllSnapshots() {
        List<GeneralIssuesCollection> listFromDB = dao.getAllSnapshots();
        if (listFromDB.isEmpty()) {
            System.out.println("No snapshots in Database.");
        } else {
            for (GeneralIssuesCollection snap: listFromDB) {
                System.out.println(snap);
            }
        }
    }



}
