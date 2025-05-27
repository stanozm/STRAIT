package fi.muni.cz.models.leastsquaresolver;

import fi.muni.cz.models.exception.ModelException;
import fi.muni.cz.models.exception.RJriExceptionHandler;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.apache.commons.math3.util.Pair;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 * Abstract implementation of the Solver interface with common functionality.
 *
 * @author Radoslav Micko, 445611@muni.cz
 * @author Andrej Mrazik, 456651@muni.cz
 */
public abstract class SolverAbstract implements Solver {

  protected Rengine rEngine;
  protected RJriExceptionHandler handler;

  /**
   * Initialize Rengine and exception handler.
   *
   * @param rEngine Rengine.
   * @param handler handler.
   */
  public SolverAbstract(Rengine rEngine, RJriExceptionHandler handler) {
    this.rEngine = rEngine;
    this.handler = handler;
  }

  /**
   * Gets the model function string.
   *
   * @return Model function string for R evaluation
   */
  protected abstract String getModelFunction();

  /**
   * Gets the model name.
   *
   * @return Model name for R references
   */
  protected abstract String getModelName();

  /**
   * Gets the number of parameters for this model.
   *
   * @return Number of parameters
   */
  protected abstract int getParameterCount();

  /**
   * Builds the start parameters data frame for the nls2 function.
   *
   * @return String representing the start parameters
   */
  protected abstract String buildStartParametersDataFrame();

  /**
   * Builds the start parameters list for the nls function.
   *
   * @param intermediate Intermediate result from nls2
   * @return Formatted string with start parameters
   */
  protected abstract String buildStartParametersList(REXP intermediate);

  /**
   * Create string of elements separated with comma.
   *
   * @param list list of integers to separate.
   * @return string of elements separated with comma.
   */
  protected String getPreparedListWithCommas(List<Integer> list) {
    return list.stream().map(Object::toString).collect(Collectors.joining(","));
  }

  /**
   * Pull out first element of list.
   *
   * @param listOfData list
   * @return List of first elements.
   */
  protected List<Integer> getListOfFirstFromPair(List<Pair<Integer, Integer>> listOfData) {
    return listOfData.stream().map(Pair::getFirst).collect(Collectors.toList());
  }

  /**
   * Pull out second element of list.
   *
   * @param listOfData list
   * @return List of second elements.
   */
  protected List<Integer> getListOfSecondFromPair(List<Pair<Integer, Integer>> listOfData) {
    return listOfData.stream().map(Pair::getSecond).collect(Collectors.toList());
  }

  /**
   * Initialize optimization in R by loading required libraries and setting data.
   *
   * @param listOfData Data points for optimization
   */
  protected void initializeOptimizationInR(List<Pair<Integer, Integer>> listOfData) {
    // Load required libraries with error handling
    handler.loadLibrary(rEngine, "nls2");

    // Set x and y values safely
    handler.safeEval(
        rEngine,
        String.format(
            "xvalues = c(%s)", getPreparedListWithCommas(getListOfFirstFromPair(listOfData))),
        "Failed to set x values in R for model - " + getModelName());

    handler.safeEval(
        rEngine,
        String.format(
            "yvalues = c(%s)", getPreparedListWithCommas(getListOfSecondFromPair(listOfData))),
        "Failed to set y values in R for model - " + getModelName());
  }

  /** Executes the nls2 brute force algorithm. */
  protected REXP executeNls2() {
    String modelName = getModelName();
    String modelFunction = getModelFunction();

    // Construct and evaluate nls2 expression with exception handling
    String nls2Expression =
        modelName
            + "2 <- nls2(yvalues ~ "
            + modelFunction
            + ", "
            + "start = data.frame("
            + buildStartParametersDataFrame()
            + "), "
            + "algorithm = \"brute-force\", control = list(warnOnly = TRUE, maxiter = 100000))";

    try {
      // Evaluate nls2 expression
      handler.safeEval(
          rEngine, nls2Expression, "Failed to execute nls2 for model - " + getModelName());

      // Get coefficients from the model
      REXP intermediate =
          handler.safeEval(
              rEngine,
              "coef(" + modelName + "2)",
              "Repository data not suitable for R evaluation (nls2) for model - " + getModelName());

      return intermediate;
    } catch (ModelException e) {
      // Log R warnings if available
      String[] warnings = handler.captureRWarnings(rEngine);
      if (warnings.length > 0) {
        System.err.println("R warnings during nls2 execution:");
        for (String warning : warnings) {
          System.err.println(" - " + warning);
        }
      }
      throw e;
    }
  }

  /**
   * Executes the nls port algorithm with optimized start parameters.
   *
   * @param intermediate Result from nls2 used for start parameters
   */
  protected REXP executeNls(REXP intermediate) {
    String modelName = getModelName();
    String modelFunction = getModelFunction();

    // Construct nls expression
    String nlsExpression =
        String.format(
            Locale.US,
            modelName
                + " <- nls(yvalues ~ "
                + modelFunction
                + ", "
                + "start = list("
                + buildStartParametersList(intermediate)
                + "), "
                + "lower = list("
                + buildLowerBoundsList()
                + "), "
                + "control = list(warnOnly = TRUE, maxiter = 100000), "
                + "algorithm = \"port\")");

    try {
      // Evaluate nls expression with exception handling
      handler.safeEval(rEngine, nlsExpression, getModelName() + ": Failed to execute nls model");

      // Get coefficients from the model
      REXP result =
          handler.safeEval(
              rEngine,
              "coef(" + modelName + ")",
              "Failed to extract coefficients from model - " + getModelName());

      return result;
    } catch (ModelException e) {
      // Log R warnings if available
      String[] warnings = handler.captureRWarnings(rEngine);
      if (warnings.length > 0) {
        System.err.println("R warnings during nls execution:");
        for (String warning : warnings) {
          System.err.println(" - " + warning);
        }
      }
      throw e;
    }
  }

  /**
   * Build the lower bounds list for all parameters (defaults to 0).
   *
   * @return Formatted string with lower bounds
   */
  protected String buildLowerBoundsList() {
    // Default implementation sets all parameters to have lower bound of 0
    return getParameterNames().stream()
        .map(param -> param + " = 0")
        .collect(Collectors.joining(", "));
  }

  /**
   * Get all parameter names for this model.
   *
   * @return List of parameter names
   */
  protected abstract List<String> getParameterNames();

  /**
   * Extract model statistics (AIC, BIC, R-squared).
   *
   * @return Array of [AIC, BIC, PseudoR2] values
   */
  protected double[] extractModelStatistics() {
    String modelName = getModelName();

    try {
      // Load required libraries with error handling
      handler.loadLibrary(rEngine, "broom");

      // Extract AIC and BIC
      REXP aic =
          handler.safeEval(
              rEngine,
              String.format("glance(%s)$AIC", modelName),
              "Failed to extract AIC statistic for model - " + modelName);

      REXP bic =
          handler.safeEval(
              rEngine,
              String.format("glance(%s)$BIC", modelName),
              "Failed to extract BIC statistic for model - " + modelName);

      // Load aomisc library and extract R-squared
      handler.loadLibrary(rEngine, "statforbiology");

      REXP pseudoRSquared =
          handler.safeEval(
              rEngine,
              String.format("R2nls(%s)$PseudoR2", modelName),
              "Failed to extract Pseudo R-squared statistic for model - " + modelName);

      return new double[] {
        aic.asDoubleArray()[0], bic.asDoubleArray()[0], pseudoRSquared.asDoubleArray()[0]
      };
    } catch (ModelException e) {
      // If statistics extraction fails, return default values
      System.err.println("Warning: Failed to extract model statistics: " + e.getMessage());
      return new double[] {0.0, 0.0, 0.0};
    }
  }

  @Override
  public SolverResult optimize(int[] startParameters, List<Pair<Integer, Integer>> listOfData) {
    initializeOptimizationInR(listOfData);

    try {
      // Step 1: Execute nls2 to get initial estimates
      REXP intermediate = executeNls2();

      // Step 2: Execute nls with optimized start parameters
      REXP result = executeNls(intermediate);

      // Step 3: Validate the final result
      int requiredParams = getParameterCount();
      handler.validateRexpResult(
          result, "Repository data not suitable for R evaluation (nls).", requiredParams);

      // Step 4: Extract statistics
      double[] stats = extractModelStatistics();

      // Step 5: Build the solver result
      double[] params = result.asDoubleArray();
      SolverResult solverResult = new SolverResult();
      solverResult.setParameters(params);
      solverResult.setAic(stats[0]);
      solverResult.setBic(stats[1]);
      solverResult.setPseudoRSquared(stats[2]);

      return solverResult;
    } catch (ModelException e) {
      // Let ModelException propagate up
      throw e;
    } catch (Exception e) {
      // For any other exceptions, return empty result
      return new SolverResult();
    } finally {
      rEngine.end();
    }
  }
}
