package fi.muni.cz.models.leastsquaresolver;

import fi.muni.cz.models.exception.RJriExceptionHandler;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 * Wang model least squares solver implementation.
 *
 * @author Andrej Mrazik, 456651@muni.cz
 */
public class WangLeastSquaresSolver extends SolverAbstract {
  private static final String MODEL_FUNCTION =
      "a * (1 - exp(-b * xvalues)) / (1 + beta * exp(1 - exp(-b * xvalues)))";
  private static final String MODEL_NAME = "modelWang";
  private static final int PARAMETER_COUNT = 3;
  private static final List<String> PARAMETER_NAMES = Arrays.asList("a", "b", "beta");

  /**
   * Initialize Rengine and exception handler.
   *
   * @param rEngine Rengine.
   * @param handler RJriExceptionHandler.
   */
  public WangLeastSquaresSolver(Rengine rEngine, RJriExceptionHandler handler) {
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
    return "a = c(10, 100000), b = c(0.0001, 1000), beta = c(0.0001, 1000)";
  }

  @Override
  protected String buildStartParametersList(REXP intermediate) {
    double[] params = intermediate.asDoubleArray();
    return String.format(
        Locale.US, "a = %.10f, b = %.10f, beta = %.10f", params[0], params[1], params[2]);
  }
}
