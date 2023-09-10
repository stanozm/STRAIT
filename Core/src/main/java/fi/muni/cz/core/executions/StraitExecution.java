package fi.muni.cz.core.executions;

import fi.muni.cz.core.ArgsParser;

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public abstract class StraitExecution {

    public abstract void initializeAnalyses(ArgsParser configuration);

    public abstract void execute();

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

}
