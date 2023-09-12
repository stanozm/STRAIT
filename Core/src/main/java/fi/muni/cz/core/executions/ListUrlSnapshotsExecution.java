package fi.muni.cz.core.executions;

import fi.muni.cz.core.ArgsParser;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesCollection;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesSnapshotDao;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesSnapshotDaoImpl;
import fi.muni.cz.dataprovider.utils.GitHubUrlParser;
import fi.muni.cz.dataprovider.utils.ParsedUrlData;
import java.util.List;

/**
 * @author Valtteri Valtonen valtonenvaltteri@gmail.com
 */
public class ListUrlSnapshotsExecution extends StraitExecution{

    private GeneralIssuesSnapshotDao dao;
    private ArgsParser configuration;

    private GitHubUrlParser urlParser;

    @Override
    public void initializeAnalyses(ArgsParser configuration) {
        this.dao = new GeneralIssuesSnapshotDaoImpl();
        this.configuration = configuration;
        this.urlParser = new GitHubUrlParser();
    }

    @Override
    public void execute(ArgsParser configuration) {
        listUrlSnapshots();
    }

    private void listUrlSnapshots() {

        ParsedUrlData githubUrlData = urlParser.parseUrlAndCheck(configuration.getOptionValueUrl());

        List<GeneralIssuesCollection> listFromDB =
                dao.getAllSnapshotsForUserAndRepository(githubUrlData.getUserName(),
                    githubUrlData.getRepositoryName());
        for (GeneralIssuesCollection snap: listFromDB) {
            System.out.println(snap);
        }
    }



}
