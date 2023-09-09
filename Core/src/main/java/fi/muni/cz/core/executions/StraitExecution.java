package fi.muni.cz.core.executions;

import fi.muni.cz.core.ArgsParser;
import fi.muni.cz.core.analysis.ReliabilityAnalysis;

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public abstract class StraitExecution {

    public abstract void initializeAnalyses(ArgsParser configuration);

    public abstract void execute();

}
