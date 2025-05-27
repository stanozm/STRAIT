package fi.muni.cz.models.leastsquaresolver;

import fi.muni.cz.models.exception.RJriExceptionHandler;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 * Li model least squares solver implementation.
 *
 * @author Andrej Mrazik, 456651@muni.cz
 */
public class LiLeastSquaresSolver extends SolverAbstract {
  private static final String MODEL_FUNCTION =
      "a * (1 - exp(-n * ((1 / (1 + phi * exp(-v * xvalues))) - (1 / (1 + phi)))))";
  private static final String MODEL_NAME = "modelLi";
  private static final int PARAMETER_COUNT = 4;
  private static final List<String> PARAMETER_NAMES = Arrays.asList("a", "n", "phi", "v");

  /**
   * Initialize Rengine and exception handler.
   *
   * @param rEngine Rengine.
   * @param handler RJriExceptionHandler.
   */
  public LiLeastSquaresSolver(Rengine rEngine, RJriExceptionHandler handler) {
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
    return "a = c(10, 100000), n = c(0.0001, 1000), phi = c(0.0001, 1000), v = c(0.0001, 1000)";
  }

  @Override
  protected String buildStartParametersList(REXP intermediate) {
    double[] params = intermediate.asDoubleArray();
    return String.format(
        Locale.US,
        "a = %.10f, n = %.10f, phi = %.10f, v = %.10f",
        params[0],
        params[1],
        params[2],
        params[3]);
  }
}
