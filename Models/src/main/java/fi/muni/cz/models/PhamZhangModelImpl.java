package fi.muni.cz.models;

import fi.muni.cz.models.leastsquaresolver.Solver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.util.Pair;

/** @author Andrej Mrazik, 456651@muni.cz */
public class PhamZhangModelImpl extends ModelAbstract {
  private final String firstParameter = "a";
  private final String secondParameter = "b";
  private final String thirdParameter = "c";
  private final String fourthParameter = "alpha";
  private final String fifthParameter = "beta";

  /**
   * Initialize model attributes.
   *
   * @param trainingData list of issues.
   * @param testData list of issues.
   * @param solver Solver to estimate model parameters.
   */
  public PhamZhangModelImpl(
      List<Pair<Integer, Integer>> trainingData,
      List<Pair<Integer, Integer>> testData,
      Solver solver) {
    super(trainingData, testData, solver);
  }

  @Override
  protected double getFunctionValue(Integer testPeriod) {
    double a = modelParameters.get(firstParameter);
    double b = modelParameters.get(secondParameter);
    double c = modelParameters.get(thirdParameter);
    double alpha = modelParameters.get(fourthParameter);
    double beta = modelParameters.get(fifthParameter);

    double expBTerm = Math.exp(-b * testPeriod);
    double expAlphaTerm = Math.exp(-alpha * testPeriod);

    double numeratorPart1 = (c + a) * (1 - expBTerm);
    double numeratorPart2 = (a * b * (expAlphaTerm - expBTerm)) / (b - alpha);
    double denominator = 1 + beta * expBTerm;

    return (numeratorPart1 - numeratorPart2) / denominator;
  }

  @Override
  protected void setParametersToMap(double[] params) {
    Map<String, Double> map = new HashMap<>();
    map.put(firstParameter, params[0]);
    map.put(secondParameter, params[1]);
    map.put(thirdParameter, params[2]);
    map.put(fourthParameter, params[3]);
    map.put(fifthParameter, params[4]);
    modelParameters = map;
  }

  @Override
  protected int[] getInitialParametersValue() {
    return new int[] {trainingIssueData.get(trainingIssueData.size() - 1).getSecond(), 1, 1, 1, 1};
  }

  @Override
  public String getTextFormOfTheFunction() {
    return "μ(t) = ((C+a)"
        + "∗(1−e<html><sup>−b∗t</sup></html>)"
        + "−(a∗b∗(e<html><sup>−alpha∗t</sup></html>−e<html><sup>−b∗t</sup></html>)/(b−alpha)))"
        + "/(1+beta∗e<html><sup>−b∗t</sup></html>)";
  }

  @Override
  public String toString() {
    return "Pham Zhang model";
  }

  @Override
  protected String getModelShortName() {
    return "PZ";
  }
}
