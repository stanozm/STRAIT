package fi.muni.cz.dataprocessing.persistence;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/** @author Radoslav Micko, 445611@muni.cz */
public class GeneralIssuesSnapshotDaoImpl implements GeneralIssuesSnapshotDao {

  private SessionFactory sessionFactory;
  private Session session;

  /** Initialize session factory. */
  public GeneralIssuesSnapshotDaoImpl() {
    getSessionFactory();
  }

  @Override
  public List<GeneralIssuesCollection> getAllSnapshotsForUserAndRepository(
      String user, String repository) {
    beginTransaction();
    Query query =
        session.createQuery(
            "FROM GeneralIssuesCollection " + "WHERE userName = ? AND repositoryName = ?");
    List<GeneralIssuesCollection> result = query.setString(0, user).setString(1, repository).list();
    endTransaction();
    return result;
  }

  @Override
  public GeneralIssuesCollection getSnapshotByName(String name) {
    beginTransaction();
    Query query = session.createQuery("FROM GeneralIssuesCollection WHERE snapshotName = ?");
    List<GeneralIssuesCollection> result = query.setString(0, name).list();
    GeneralIssuesCollection snapshot;
    if (result.isEmpty()) {
      snapshot = null;
    } else {
      snapshot = result.get(0);
    }
    endTransaction();
    return snapshot;
  }

  @Override
  public void save(GeneralIssuesCollection snapshot) {
    beginTransaction();
    session.merge(snapshot);
    endTransaction();
  }

  @Override
  public List<GeneralIssuesCollection> getAllSnapshots() {
    beginTransaction();
    Query query = session.createQuery("FROM GeneralIssuesCollection");
    List<GeneralIssuesCollection> list = query.list();
    endTransaction();
    return list;
  }

  @Override
  public void deleteSnapshot(GeneralIssuesCollection snapshot) {
    GeneralIssuesCollection idSnapshot = new GeneralIssuesCollection();
    idSnapshot.setId(snapshot.getId());
    beginTransaction();
    session.delete(idSnapshot);
    endTransaction();
  }

  private void beginTransaction() {
    session = sessionFactory.openSession();
    session.beginTransaction();
  }

  private void endTransaction() {
    session.getTransaction().commit();
    session.flush();
  }

  private void getSessionFactory() {
    Configuration configuration = new Configuration();
    configuration.configure();
    ServiceRegistry serviceRegistry =
        new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
    sessionFactory = configuration.buildSessionFactory(serviceRegistry);
  }
}
