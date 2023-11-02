package fi.muni.cz.core.analysis.phases.datacollection;

import fi.muni.cz.dataprovider.GeneralIssue;
import fi.muni.cz.dataprovider.RepositoryInformation;
import java.util.List;

/** @author valtonenvaltteri@gmail.com */
public class DataCollectionUtil {

  /**
   * Get repository information object for case where only issue report CSV data is available.
   *
   * @param filePath Path of issue report csv file.
   * @param listOfGeneralIssues List of issues contained in the file.
   * @return RepositoryInformation object
   */
  public static RepositoryInformation getRepositoryInformationForFileEvaluation(
      String filePath, List<GeneralIssue> listOfGeneralIssues) {
    RepositoryInformation repositoryInformation = new RepositoryInformation();
    repositoryInformation.setName(filePath);
    repositoryInformation.setContributors(1);
    repositoryInformation.setDescription("CSV repository");
    repositoryInformation.setForks(0);
    repositoryInformation.setSize(0);
    repositoryInformation.setPushedAtFirst(listOfGeneralIssues.get(0).getCreatedAt());
    repositoryInformation.setPushedAt(
        listOfGeneralIssues.get(listOfGeneralIssues.size() - 1).getCreatedAt());
    return repositoryInformation;
  }
}
