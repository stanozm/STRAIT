package fi.muni.cz.models;

import fi.muni.cz.models.leastsquaresolver.Solver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.util.Pair;

/** @author Radoslav Micko, 445611@muni.cz */
public class GOSShapedModelImpl extends ModelAbstract {

  private final String firstParameter = "a";
  private final String secondParameter = "b";

  /**
   * Initialize model attributes.
   *
   * @param trainingData list of issues.
   * @param testData list of issues.
   * @param solver Solver to estimate model parameters.
   */
  public GOSShapedModelImpl(
      List<Pair<Integer, Integer>> trainingData,
      List<Pair<Integer, Integer>> testData,
      Solver solver) {
    super(trainingData, testData, solver);
  }

  @Override
  protected int[] getInitialParametersValue() {
    return new int[] {trainingIssueData.get(trainingIssueData.size() - 1).getSecond(), 1};
  }

  @Override
  protected double getFunctionValue(Integer testPeriod) {
    return modelParameters.get(firstParameter)
        * (1
            - (1 + modelParameters.get(secondParameter) * testPeriod)
                * Math.exp(-modelParameters.get(secondParameter) * testPeriod));
  }

  @Override
  protected void setParametersToMap(double[] params) {
    Map<String, Double> map = new HashMap<>();
    map.put(firstParameter, params[0]);
    map.put(secondParameter, params[1]);
    modelParameters = map;
  }

  @Override
  public String getTextFormOfTheFunction() {
    return "μ(t) = a * (1 - (1 + b*t) * e<html><sup>-b*t</sup></html>)";
  }

  @Override
  public String toString() {
    return "Goel-Okumoto S-Shaped model";
  }

  @Override
  protected String getModelShortName() {
    return "GOS";
  }
}
