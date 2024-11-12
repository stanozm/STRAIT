package fi.muni.cz.models;

import fi.muni.cz.models.leastsquaresolver.Solver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.util.Pair;

/** @author Andrej Mrazik, 456651@muni.cz */
public class WangModelImpl extends ModelAbstract {
  private final String firstParameter = "a";
  private final String secondParameter = "b";
  private final String thirdParameter = "β";

  /**
   * Initialize model attributes.
   *
   * @param trainingData list of issues.
   * @param testData list of issues.
   * @param solver Solver to estimate model parameters.
   */
  public WangModelImpl(
      List<Pair<Integer, Integer>> trainingData,
      List<Pair<Integer, Integer>> testData,
      Solver solver) {
    super(trainingData, testData, solver);
  }

  @Override
  protected double getFunctionValue(Integer testPeriod) {
    double a = modelParameters.get(firstParameter);
    double b = modelParameters.get(secondParameter);
    double beta = modelParameters.get(thirdParameter);

    double expBTerm = Math.exp(-b * testPeriod);
    double expTerm = Math.exp(1 - expBTerm);

    return a * (1 - expBTerm) / (1 + beta * expTerm);
  }

  @Override
  protected void setParametersToMap(double[] params) {
    Map<String, Double> map = new HashMap<>();
    map.put(firstParameter, params[0]);
    map.put(secondParameter, params[1]);
    map.put(thirdParameter, params[2]);
    modelParameters = map;
  }

  @Override
  protected int[] getInitialParametersValue() {
    return new int[] {trainingIssueData.get(trainingIssueData.size() - 1).getSecond(), 1, 1};
  }

  @Override
  public String getTextFormOfTheFunction() {
    return "μ(t) = a<html><sup>d</sup></html>∗(1−e<html><sup>−b∗t</sup></html>)/"
        + "(1+beta∗e<html><sup>1−e<−b∗t></sup></html>)";
  }

  @Override
  public String toString() {
    return "Wang model";
  }

  @Override
  protected String getModelShortName() {
    return "WA";
  }
}
