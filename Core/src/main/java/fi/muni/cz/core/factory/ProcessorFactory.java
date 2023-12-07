package fi.muni.cz.core.factory;

import fi.muni.cz.core.ArgsParser;
import fi.muni.cz.dataprocessing.issuesprocessing.IssueProcessingAction;
import java.util.ArrayList;
import java.util.List;

/** @author Radoslav Micko, 445611@muni.cz */
public class ProcessorFactory {

  /**
   * Get all processors to run.
   *
   * @param parser parsed CommandLine.
   * @return list of Processors.
   */
  public static List<IssueProcessingAction> getProcessors(ArgsParser parser) {
    List<IssueProcessingAction> listOfProcessors = new ArrayList<>();

    // Implement for processors

    return listOfProcessors;
  }
}
