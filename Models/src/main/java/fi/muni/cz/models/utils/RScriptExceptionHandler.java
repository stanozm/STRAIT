package fi.muni.cz.models.utils;

import fi.muni.cz.models.exception.ModelException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Utility class for handling R script execution and related exceptions. This class provides methods
 * for executing R scripts, processing their output, and handling errors in a standardized way.
 */
public class RScriptExceptionHandler {

  /**
   * Executes an R script with the provided parameters and returns the exit code.
   *
   * @param rScriptPath Path to the R script file
   * @param args Arguments to pass to the R script
   * @return The process exit code
   * @throws IOException If there's an error with process I/O
   * @throws InterruptedException If the process is interrupted
   * @throws ModelException If the R script execution fails
   */
  public static int executeRScript(String rScriptPath, String... args)
      throws IOException, InterruptedException, ModelException {
    // Execute and discard output
    String output = executeRScriptWithOutput(rScriptPath, args);
    return 0; // If we get here, execution was successful
  }

  /**
   * Executes an R script with the provided parameters and returns the console output.
   *
   * @param rScriptPath Path to the R script file
   * @param args Arguments to pass to the R script
   * @return The captured console output
   * @throws IOException If there's an error with process I/O
   * @throws InterruptedException If the process is interrupted
   * @throws ModelException If the R script execution fails
   */
  public static String executeRScriptWithOutput(String rScriptPath, String... args)
      throws IOException, InterruptedException, ModelException {
    // Build command array
    String[] command = new String[args.length + 2];
    command[0] = "Rscript";
    command[1] = rScriptPath;
    System.arraycopy(args, 0, command, 2, args.length);

    // Build the process
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.redirectErrorStream(true);

    // Start the process
    Process process = processBuilder.start();

    // Collect output
    StringBuilder output = new StringBuilder();
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line).append("\n");
      }
    }

    // Wait for completion
    int exitCode = process.waitFor();

    // Handle non-zero exit code
    if (exitCode != 0) {
      System.err.println("R script execution failed with exit code: " + exitCode);
      System.err.println("R script output:");
      System.err.println(output.toString());
      throw new ModelException("R script execution failed with exit code: " + exitCode);
    }

    return output.toString();
  }

  /**
   * Validates the results file created by the R script.
   *
   * @param resultsFile The file containing R script output
   * @param output Captured R script console output (for error reporting)
   * @throws ModelException If the results file is invalid
   */
  public static void validateResultsFile(File resultsFile, String output) throws ModelException {
    if (!resultsFile.exists() || resultsFile.length() == 0) {
      System.err.println("Results file is empty or does not exist");
      System.err.println("R script output:");
      System.err.println(output);
      throw new ModelException("Results file is empty or does not exist");
    }
  }

  /**
   * Validates the content of results file and ensures it has enough lines.
   *
   * @param resultLines Lines read from the results file
   * @param output Captured R script console output (for error reporting)
   * @throws ModelException If the results format is invalid
   */
  public static void validateResultLines(List<String> resultLines, String output)
      throws ModelException {
    if (resultLines.size() < 2) {
      System.err.println("Invalid results file format - insufficient lines");
      System.err.println("R script output:");
      System.err.println(output);
      throw new ModelException("Invalid results file format - insufficient lines");
    }
  }

  /**
   * Validates the values from the results file.
   *
   * @param values Values parsed from the results file
   * @param expectedCount Expected number of values
   * @param output Captured R script console output (for error reporting)
   * @throws ModelException If the values format is invalid
   */
  public static void validateValues(String[] values, int expectedCount, String output)
      throws ModelException {
    if (values.length < expectedCount) {
      System.err.println(
          "Invalid results format - expected "
              + expectedCount
              + " values but got "
              + values.length);
      System.err.println("R script output:");
      System.err.println(output);
      throw new ModelException(
          "Invalid results format - expected "
              + expectedCount
              + " values but got "
              + values.length);
    }
  }

  /**
   * Parses a parameter value from a string.
   *
   * @param value String value to parse
   * @param index Parameter index (for error reporting)
   * @param output Captured R script console output (for error reporting)
   * @return Parsed double value
   * @throws ModelException If parsing fails
   */
  public static double parseParameterValue(String value, int index, String output)
      throws ModelException {
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      System.err.println("Invalid parameter value at index " + index + ": " + value);
      System.err.println("R script output:");
      System.err.println(output);
      throw new ModelException("Invalid parameter value at index " + index + ": " + value);
    }
  }

  /**
   * Handles IO and Interrupted exceptions by logging and re-throwing as ModelException.
   *
   * @param e The exception to handle
   * @throws ModelException Always throws a ModelException with the appropriate message
   */
  public static void handleException(Exception e) throws ModelException {
    if (e instanceof IOException) {
      System.err.println("I/O error during R script execution: " + e.getMessage());
      e.printStackTrace();
      throw new ModelException("I/O error during R script execution: " + e.getMessage());
    } else if (e instanceof InterruptedException) {
      System.err.println("R script execution was interrupted: " + e.getMessage());
      e.printStackTrace();
      Thread.currentThread().interrupt(); // Restore interrupted status
      throw new ModelException("R script execution was interrupted: " + e.getMessage());
    } else {
      System.err.println("Error during R script execution: " + e.getMessage());
      e.printStackTrace();
      throw new ModelException("Error during R script execution: " + e.getMessage());
    }
  }
}
