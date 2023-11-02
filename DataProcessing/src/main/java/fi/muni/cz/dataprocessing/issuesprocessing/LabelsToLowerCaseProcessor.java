package fi.muni.cz.dataprocessing.issuesprocessing;

import fi.muni.cz.dataprovider.GeneralIssue;
import fi.muni.cz.dataprovider.RepositoryInformation;
import java.util.List;

/** @author Radoslav Micko, 445611@muni.cz */
public class LabelsToLowerCaseProcessor implements IssuesProcessor {

  @Override
  public List<GeneralIssue> apply(
      List<GeneralIssue> list, RepositoryInformation repositoryInformation) {
    for (GeneralIssue issue : list) {
      issue.allLabelsToLowerCase();
    }
    return list;
  }

  @Override
  public String infoAboutIssueProcessingAction() {
    return "LabelsToLowerCaseProcessor used to lowercase all lables of issues.";
  }

  @Override
  public String infoAboutApplicationResult() {
    return "Labels now use lowercase.";
  }

  @Override
  public String toString() {
    return "LabelsToLowerCase";
  }
}
