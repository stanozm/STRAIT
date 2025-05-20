package fi.muni.cz.models.leastsquaresolver;

import static fi.muni.cz.models.exception.RScriptExceptionHandler.handleException;

import fi.muni.cz.models.exception.ModelException;
import fi.muni.cz.models.exception.RJriExceptionHandler;
import fi.muni.cz.models.exception.RScriptExceptionHandler;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.apache.commons.math3.util.Pair;
import org.rosuda.JRI.Rengine;

/** @author Andrej Mrazik, 456651@muni.cz */
public class JinyongWangLeastSquaresSolver implements Solver {
  private static final String MODEL_NAME = "jinyongWangModel";
  private static final String R_SCRIPT_RESOURCE = "/scripts/jinyong_wang_model.R"; // Resource path

  /**
   * Initialize Rengine and exception handler.
   *
   * @param rEngine Rengine.
   * @param handler RJriExceptionHandler.
   */
  public JinyongWangLeastSquaresSolver(Rengine rEngine, RJriExceptionHandler handler) {}

  @Override
  public SolverResult optimize(int[] startParameters, List<Pair<Integer, Integer>> listOfData) {
    File rScriptFile = null;
    File dataFile = null;
    File resultsFile = null;

    try {
      // Extract and prepare files
      rScriptFile = extractRScriptToTempFile();
      dataFile = createDataFile(listOfData);
      resultsFile = File.createTempFile("results", ".csv");
      resultsFile.deleteOnExit();

      // Execute the R script
      String scriptOutput = executeRScript(rScriptFile, dataFile, resultsFile);

      // Process the results
      return processResults(resultsFile, scriptOutput);

    } catch (IOException | InterruptedException e) {
      handleException(e);
      return null; // Will not reach here due to exception being thrown
    }
  }

  /**
   * Extracts the R script from resources to a temporary file.
   *
   * @return A File object for the extracted R script
   * @throws IOException If file operations fail
   * @throws ModelException If the R script resource cannot be found
   */
  private File extractRScriptToTempFile() throws IOException, ModelException {
    try (InputStream inputStream = getClass().getResourceAsStream(R_SCRIPT_RESOURCE)) {
      if (inputStream == null) {
        throw new ModelException("R script not found in resources: " + R_SCRIPT_RESOURCE);
      }

      File rScriptFile = File.createTempFile("jinyong_wang_model", ".R");
      Files.copy(inputStream, rScriptFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      rScriptFile.deleteOnExit();
      return rScriptFile;
    }
  }

  /**
   * Creates a CSV file with the data.
   *
   * @param listOfData The data to write
   * @return A File object for the created CSV file
   * @throws IOException If file writing fails
   */
  private File createDataFile(List<Pair<Integer, Integer>> listOfData) throws IOException {
    File dataFile = File.createTempFile("data", ".csv");
    dataFile.deleteOnExit();

    try (FileWriter writer = new FileWriter(dataFile)) {
      writer.write("x,y\n"); // Header
      for (Pair<Integer, Integer> pair : listOfData) {
        writer.write(pair.getFirst() + "," + pair.getSecond() + "\n");
      }
    }

    return dataFile;
  }

  /**
   * Executes the R script with the provided input and output files.
   *
   * @param rScriptFile The R script file
   * @param dataFile The input data file
   * @param resultsFile The output results file
   * @return The captured console output (for error reporting)
   * @throws IOException If process I/O fails
   * @throws InterruptedException If the process is interrupted
   * @throws ModelException If the R script execution fails
   */
  private String executeRScript(File rScriptFile, File dataFile, File resultsFile)
      throws IOException, InterruptedException, ModelException {
    // Use the RScriptExceptionHandler to execute the R script
    String[] args = {dataFile.getAbsolutePath(), resultsFile.getAbsolutePath()};

    return RScriptExceptionHandler.executeRScriptWithOutput(rScriptFile.getAbsolutePath(), args);
  }

  /**
   * Processes the results from the R script execution.
   *
   * @param resultsFile The file containing the results
   * @param scriptOutput The captured console output (for error reporting)
   * @return A SolverResult object with the model parameters and statistics
   * @throws IOException If file reading fails
   * @throws ModelException If results processing fails
   */
  private SolverResult processResults(File resultsFile, String scriptOutput)
      throws IOException, ModelException {
    // Validate results file
    RScriptExceptionHandler.validateResultsFile(resultsFile, scriptOutput);

    // Read results from the output file
    List<String> resultLines = Files.readAllLines(Paths.get(resultsFile.getAbsolutePath()));
    RScriptExceptionHandler.validateResultLines(resultLines, scriptOutput);

    // Parse the results (skip header line)
    String[] values = resultLines.get(1).split(",");
    RScriptExceptionHandler.validateValues(values, 8, scriptOutput);

    return createSolverResult(values, scriptOutput);
  }

  /**
   * Creates a SolverResult object from the parsed values.
   *
   * @param values Array of string values to parse
   * @param scriptOutput The captured console output (for error reporting)
   * @return A SolverResult object
   * @throws ModelException If parsing fails
   */
  private SolverResult createSolverResult(String[] values, String scriptOutput)
      throws ModelException {
    // Extract parameters
    double[] params = new double[5];
    for (int i = 0; i < 5; i++) {
      params[i] = RScriptExceptionHandler.parseParameterValue(values[i], i, scriptOutput);
    }

    // Extract statistics
    double aic = RScriptExceptionHandler.parseParameterValue(values[5], 5, scriptOutput);
    double bic = RScriptExceptionHandler.parseParameterValue(values[6], 6, scriptOutput);
    double rSquared = RScriptExceptionHandler.parseParameterValue(values[7], 7, scriptOutput);

    // Create the solver result
    SolverResult solverResult = new SolverResult();
    solverResult.setParameters(params);
    solverResult.setAic(aic);
    solverResult.setBic(bic);
    solverResult.setPseudoRSquared(rSquared);

    return solverResult;
  }
}
