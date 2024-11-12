package fi.muni.cz.models;

import fi.muni.cz.models.leastsquaresolver.Solver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.util.Pair;

/** @author Andrej Mrazik, 456651@muni.cz */
public class LiModelImpl extends ModelAbstract {
  private final String firstParameter = "a";
  private final String secondParameter = "v";
  private final String thirdParameter = "N";
  private final String fourthParameter = "ϕ";

  /**
   * Initialize model attributes.
   *
   * @param trainingData list of issues.
   * @param testData list of issues.
   * @param solver Solver to estimate model parameters.
   */
  public LiModelImpl(
      List<Pair<Integer, Integer>> trainingData,
      List<Pair<Integer, Integer>> testData,
      Solver solver) {
    super(trainingData, testData, solver);
  }

  @Override
  protected double getFunctionValue(Integer testPeriod) {
    double a = modelParameters.get(firstParameter);
    double v = modelParameters.get(secondParameter);
    double n = modelParameters.get(thirdParameter);
    double phi = modelParameters.get(fourthParameter);

    double expVTerm = Math.exp(-v * testPeriod);
    double firstTerm = 1 / (1 + phi * expVTerm);
    double secondTerm = 1 / (1 + phi);

    return a * (1 - Math.exp(-n * (firstTerm - secondTerm)));
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
    return "μ(t) = a∗(1−e<html><sup>−N∗(1/(1+phi∗e<html><sup>−v∗t</sup></html>)−1/(1+phi))</sup></html>)";
  }

  @Override
  public String toString() {
    return "Li model";
  }

  @Override
  protected String getModelShortName() {
    return "LI";
  }
}
