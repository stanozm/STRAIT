package fi.muni.cz.core;

import fi.muni.cz.core.exception.InvalidInputException;
import fi.muni.cz.core.executions.RunConfiguration;
import fi.muni.cz.core.executions.StraitExecution;
import fi.muni.cz.core.factory.ModelFactory;
import java.time.Duration;
import java.time.Instant;
import org.rosuda.JRI.Rengine;

/** @author Radoslav Micko, 445611@muni.cz */
public class Core {

  private static final ArgsParser PARSER = new ArgsParser();

  static final Rengine RENGINE =
      Rengine.getMainEngine() != null
          ? Rengine.getMainEngine()
          : new Rengine(new String[] {"--vanilla"}, false, null);

  /**
   * Main method, takes command line arguments.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    try {
      PARSER.parse(args);
      run();
    } catch (InvalidInputException e) {
      PARSER.printHelp();
      System.out.println(e.causes());
      e.printStackTrace();
      System.exit(1);
    } catch (Exception e) {
      System.out.println(e);
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static void run() throws InvalidInputException {
    System.out.println("Working...");
    Instant start = Instant.now();

    ModelFactory.setREngine(RENGINE);

    RunConfiguration runConfiguration = PARSER.getRunConfiguration();

    switch (runConfiguration) {
      case SNAPSHOT_NAME_AND_LIST_SNAPSHOTS:
        PARSER.printHelp();
        System.out.println("[Can't combine '-sn' with '-sl']");
        return;
      case NOT_SUPPORTED:
        PARSER.printHelp();
        System.out.println("[Missing option: '-e' / '-s']");
        return;
      default:
        break;
    }

    StraitExecution execution = StraitExecution.getExecutionForRunConfiguration(runConfiguration);

    if (execution == null) {
      System.out.println("Execution for this run configuration was not found");
      System.exit(1);
    }

    execution.initializeAnalyses(PARSER);
    execution.execute(PARSER);

    System.out.println(
        "Done! Duration - " + Duration.between(start, Instant.now()).toMinutes() + "min");
    System.exit(0);
  }
}
