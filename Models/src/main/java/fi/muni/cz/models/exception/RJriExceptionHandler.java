package fi.muni.cz.models.exception;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 * Exception handler for R JRI operations. Provides centralized exception handling for R code
 * execution.
 *
 * @author Andrej Mrazik, 456651@muni.cz
 */
public class RJriExceptionHandler {

  /**
   * Validates the result of an R expression evaluation.
   *
   * @param result The result of R expression evaluation
   * @param errorMessage The error message to throw when validation fails
   * @param requiredLength The minimum required length of the result array (if applicable)
   * @throws ModelException if the result is null or shorter than the required length
   */
  public void validateRexpResult(REXP result, String errorMessage, int requiredLength) {
    if (result == null) {
      throw new ModelException(errorMessage);
    }

    if (requiredLength > 0 && result.asDoubleArray().length < requiredLength) {
      throw new ModelException(errorMessage);
    }
  }

  /**
   * Safely evaluates an R expression and handles R-specific exceptions.
   *
   * @param rEngine The R engine instance
   * @param rExpression The R expression to evaluate
   * @param errorMessage Error message to use if evaluation fails
   * @return result of evaluation
   * @throws ModelException if R evaluation fails
   */
  public REXP safeEval(Rengine rEngine, String rExpression, String errorMessage) {
    if (rEngine == null) {
      throw new ModelException("R engine is not initialized");
    }

    REXP result = rEngine.eval(rExpression);
    if (result == null) {
      throw new ModelException(errorMessage);
    }
    return result;
  }

  /**
   * Loads an R library safely with exception handling.
   *
   * @param rEngine The R engine instance
   * @param libraryName The name of the library to load
   * @throws ModelException if the library cannot be loaded
   */
  public void loadLibrary(Rengine rEngine, String libraryName) {
    if (rEngine == null) {
      throw new ModelException("R engine is not initialized");
    }

    REXP loadResult =
        rEngine.eval("suppressWarnings(suppressMessages(require(" + libraryName + ")))");

    if (loadResult == null || loadResult.asBool().isFALSE()) {
      throw new ModelException("Failed to load R library: " + libraryName);
    }
  }

  /**
   * Captures and processes R warnings.
   *
   * @param rEngine The R engine instance
   * @return Array of warning messages
   */
  public String[] captureRWarnings(Rengine rEngine) {
    if (rEngine == null) {
      return new String[0];
    }

    REXP warnings = rEngine.eval("warnings()");
    if (warnings != null) {
      return warnings.asStringArray();
    }

    return new String[0];
  }
}
