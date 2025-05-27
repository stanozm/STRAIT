package fi.muni.cz.models.leastsquaresolver;

import fi.muni.cz.models.exception.ModelException;
import fi.muni.cz.models.exception.RJriExceptionHandler;
import java.util.Collections;
import java.util.List;
import org.apache.commons.math3.util.Pair;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 * Empty model placeholder for special cases.
 *
 * @author Radoslav Micko, 445611@muni.cz
 */
public class EmptyLeastSquaresSolver extends SolverAbstract {

  /**
   * Initialize Rengine and exception handler.
   *
   * @param rEngine Rengine.
   * @param handler RJriExceptionHandler.
   */
  public EmptyLeastSquaresSolver(Rengine rEngine, RJriExceptionHandler handler) {
    super(rEngine, handler);
  }

  @Override
  protected String getModelFunction() {
    return "";
  }

  @Override
  protected String getModelName() {
    return "empty";
  }

  @Override
  protected int getParameterCount() {
    return 0;
  }

  @Override
  protected List<String> getParameterNames() {
    return Collections.emptyList();
  }

  @Override
  protected String buildStartParametersDataFrame() {
    return "";
  }

  @Override
  protected String buildStartParametersList(REXP intermediate) {
    return "";
  }

  @Override
  public SolverResult optimize(int[] startParameters, List<Pair<Integer, Integer>> listOfData) {
    throw new ModelException("Empty model");
  }
}
