package fi.muni.cz.models;

import fi.muni.cz.models.leastsquaresolver.Solver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.util.Pair;

/** @author Andrej Mrazik, 456651@muni.cz */
public class PhamNordmannZhangModelImpl extends ModelAbstract {
  private final String firstParameter = "a";
  private final String secondParameter = "b";
  private final String thirdParameter = "α";
  private final String fourthParameter = "β";

  /**
   * Initialize model attributes.
   *
   * @param trainingData list of issues.
   * @param testData list of issues.
   * @param solver Solver to estimate model parameters.
   */
  public PhamNordmannZhangModelImpl(
      List<Pair<Integer, Integer>> trainingData,
      List<Pair<Integer, Integer>> testData,
      Solver solver) {
    super(trainingData, testData, solver);
  }

  @Override
  protected double getFunctionValue(Integer testPeriod) {
    double a = modelParameters.get(firstParameter);
    double b = modelParameters.get(secondParameter);
    double alpha = modelParameters.get(thirdParameter);
    double beta = modelParameters.get(fourthParameter);

    double expTerm = Math.exp(-b * testPeriod);
    double firstPart = a / (1 + beta * expTerm);
    double secondPart = (1 - expTerm) * (1 - alpha / b) + (alpha * testPeriod);

    return firstPart * secondPart;
  }

  @Override
  protected void setParametersToMap(double[] params) {
    Map<String, Double> map = new HashMap<>();
    map.put(firstParameter, params[0]);
    map.put(secondParameter, params[1]);
    map.put(thirdParameter, params[2]);
    map.put(fourthParameter, params[3]);
    modelParameters = map;
  }

  @Override
  protected int[] getInitialParametersValue() {
    return new int[] {trainingIssueData.get(trainingIssueData.size() - 1).getSecond(), 1, 1, 1};
  }

  @Override
  public String getTextFormOfTheFunction() {
    return "μ(t) = (a / (1 + β*e<html><sup>-b*t</sup></html>)) * (1 - e<html><sup>-b*t</sup></html>) * "
        + "(1 - α/b) + (α*t)";
  }

  @Override
  public String toString() {
    return "Pham Nordmann Zhang model";
  }

  @Override
  protected String getModelShortName() {
    return "PNZ";
  }
}
