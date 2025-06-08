package fi.muni.cz.models.leastsquaresolver;

import fi.muni.cz.models.exception.RJriExceptionHandler;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 * Pham-Zhang model least squares solver implementation.
 *
 * @author Andrej Mrazik, 456651@muni.cz
 */
public class PhamZhangLeastSquaresSolver extends SolverAbstract {
  private static final String MODEL_FUNCTION =
      "(a * (1 - exp(-b * xvalues))"
          + " - (a * b * (exp(-alpha * xvalues) - exp(-b * xvalues)) / (b - alpha)))"
          + " / (1 + beta * exp(-b * xvalues))";
  private static final String MODEL_NAME = "modelPhamZhang";
  private static final int PARAMETER_COUNT = 4;
  private static final List<String> PARAMETER_NAMES = Arrays.asList("a", "b", "alpha", "beta");

  /**
   * Initialize Rengine and exception handler.
   *
   * @param rEngine Rengine.
   * @param handler RJriExceptionHandler.
   */
  public PhamZhangLeastSquaresSolver(Rengine rEngine, RJriExceptionHandler handler) {
    super(rEngine, handler);
  }

  @Override
  protected String getModelFunction() {
    return MODEL_FUNCTION;
  }

  @Override
  protected String getModelName() {
    return MODEL_NAME;
  }

  @Override
  protected int getParameterCount() {
    return PARAMETER_COUNT;
  }

  @Override
  protected List<String> getParameterNames() {
    return PARAMETER_NAMES;
  }

  @Override
  protected String buildStartParametersDataFrame() {
    return "a = c(100, 100000), "
        + "b = c(0.0001, 1), "
        + "alpha = c(0.01, 100), "
        + "beta = c(0.01, 100)";
  }

  @Override
  protected String buildStartParametersList(REXP intermediate) {
    double[] params = intermediate.asDoubleArray();
    return String.format(
        Locale.US,
        "a = %.10f, b = %.10f, alpha = %.10f, beta = %.10f",
        params[0],
        params[1],
        params[2],
        params[3]);
  }
}
