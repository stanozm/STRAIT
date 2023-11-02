package fi.muni.cz.core.executions;

import fi.muni.cz.core.ArgsParser;
import fi.muni.cz.core.analysis.phases.datacollection.DataCollectionCacheMode;
import fi.muni.cz.core.factory.FilterFactory;
import fi.muni.cz.core.factory.ProcessorFactory;
import fi.muni.cz.dataprocessing.issuesprocessing.IssueProcessingStrategy;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** @author Valtteri Valtonen, valtonenvaltteri@gmail.com */
public abstract class StraitExecution {

  /**
   * Initialize the reliability analyses used in this execution.
   *
   * @param configuration Command line configuration object
   */
  public abstract void initializeAnalyses(ArgsParser configuration);

  /**
   * Execute this STRAIT execution.
   *
   * @param configuration Command line configuration
   */
  public abstract void execute(ArgsParser configuration);

  /**
   * Get correct execution for provided run configuration
   *
   * @param runConfiguration STRAIT run configuration
   * @return Strait execution corresponding to the run configuration
   */
  public static StraitExecution getExecutionForRunConfiguration(RunConfiguration runConfiguration) {
    switch (runConfiguration) {
      case LIST_ALL_SNAPSHOTS:
        return new ListSnapshotsExecution();
      case URL_AND_LIST_SNAPSHOTS:
        return new ListUrlSnapshotsExecution();
      case BATCH_AND_EVALUATE:
        return new BatchExecution();
      case URL_AND_EVALUATE:
        return new SingleUrlExecution();
      case SNAPSHOT_NAME_AND_EVALUATE:
        return new SingleSnapshotExecution();
      default:
        System.out.println("This kind of execution has not been implemented yet");
    }
    return null;
  }

  /**
   * Get cache mode for Github data collection from configuration
   *
   * @param configuration Configuration object
   * @return Cache mode
   */
  protected DataCollectionCacheMode getDataCollectionCacheModeFromConfiguration(
      ArgsParser configuration) {

    if (configuration.hasOptionNewSnapshot()) {
      return DataCollectionCacheMode.OVERWRITE_CACHE;
    }

    return DataCollectionCacheMode.CACHE;
  }

  protected IssueProcessingStrategy getStrategyFromConfiguration(ArgsParser configuration) {
    return new IssueProcessingStrategy(
        Stream.concat(
                FilterFactory.getFilters(configuration).stream(),
                ProcessorFactory.getProcessors(configuration).stream())
            .collect(Collectors.toList()),
        "");
  }
}
