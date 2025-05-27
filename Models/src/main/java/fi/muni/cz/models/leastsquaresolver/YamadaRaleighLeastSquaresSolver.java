package fi.muni.cz.models.leastsquaresolver;

import fi.muni.cz.models.exception.RJriExceptionHandler;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 * Yamada Raleigh model least squares solver implementation.
 *
 * @author Radoslav Micko, 445611@muni.cz
 */
public class YamadaRaleighLeastSquaresSolver extends SolverAbstract {
  private static final String MODEL_FUNCTION = "a*(1 - exp(-b*(1-exp(-c*(xvalues^2)/2))))";
  private static final String MODEL_NAME = "modelYamadaRaleigh";
  private static final int PARAMETER_COUNT = 3;
  private static final List<String> PARAMETER_NAMES = Arrays.asList("a", "b", "c");

  /**
   * Initialize Rengine and exception handler.
   *
   * @param rEngine Rengine.
   * @param handler RJriExceptionHandler.
   */
  public YamadaRaleighLeastSquaresSolver(Rengine rEngine, RJriExceptionHandler handler) {
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
    return "a = c(1, 10000), b = c(0.00001, 100), c = c(0.00001, 200)";
  }

  @Override
  protected String buildStartParametersList(REXP intermediate) {
    double[] params = intermediate.asDoubleArray();
    return String.format(
        Locale.US, "a = %.10f, b = %.10f, c = %.10f", params[0], params[1], params[2]);
  }
}
