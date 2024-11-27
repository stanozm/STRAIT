package fi.muni.cz.models;

import fi.muni.cz.models.leastsquaresolver.Solver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.util.Pair;

/** @author Andrej Mrazik, 456651@muni.cz */
public class JinyongWangModelImpl extends ModelAbstract {
  private final String firstParameter = "a";
  private final String secondParameter = "d";
  private final String thirdParameter = "n";
  private final String fourthParameter = "beta";
  private final String fifthParameter = "omega";
  private final String sixthParameter = "theta";

  /**
   * Initialize model attributes.
   *
   * @param trainingData list of issues.
   * @param testData list of issues.
   * @param solver Solver to estimate model parameters.
   */
  public JinyongWangModelImpl(
      List<Pair<Integer, Integer>> trainingData,
      List<Pair<Integer, Integer>> testData,
      Solver solver) {
    super(trainingData, testData, solver);
  }

  private double factorial(int num) {
    if (num == 0 || num == 1) {
      return 1.0;
    }
    double result = 1.0;
    for (int i = 2; i <= num; i++) {
      result *= i;
    }
    return result;
  }

  @Override
  protected double getFunctionValue(Integer testPeriod) {
    double a = modelParameters.get(firstParameter);
    double d = modelParameters.get(secondParameter);
    double n = modelParameters.get(thirdParameter);
    double beta = modelParameters.get(fourthParameter);
    double omega = modelParameters.get(fifthParameter);
    double theta = modelParameters.get(sixthParameter);

    double expOmegaT = Math.exp(omega * testPeriod);
    double expBetaOmega = Math.exp(beta * Math.pow(testPeriod, d) + omega * testPeriod);

    double summation = 0.0;
    for (int i = 0; i <= n; i++) {
      double term = (Math.pow(omega, i) * Math.pow(testPeriod, i + d)) / (factorial(i) * (i + d));
      summation += term;
    }

    return (a / (theta + expOmegaT)) * (expBetaOmega - beta * d * summation - 1);
  }

  @Override
  protected void setParametersToMap(double[] params) {
    Map<String, Double> map = new HashMap<>();
    map.put(firstParameter, params[0]);
    map.put(secondParameter, params[1]);
    map.put(thirdParameter, params[2]);
    map.put(fourthParameter, params[3]);
    map.put(fifthParameter, params[4]);
    map.put(sixthParameter, params[5]);
    modelParameters = map;
  }

  @Override
  protected int[] getInitialParametersValue() {
    return new int[] {
      trainingIssueData.get(trainingIssueData.size() - 1).getSecond(), 1, 1, 1, 1, 1
    };
  }

  @Override
  public String getTextFormOfTheFunction() {
    //    return "μ(t) = a / (theta + e<html><sup>omega*t</sup></html>) "
    //        + "* (e<html><sup>beta*t<html><sup>d</sup></html> + omega*t</sup></html> - beta * d *
    // "
    //        + "<html>&sum;</html><sub>i=0</sub><sup>n</sup> ((omega<html><sup>i</sup></html> "
    //        + "* t<html><sup>(i+d)</sup></html>) / (i! * (i + d))) - 1)";
    return "μ(t) = toBeImplemented";
  }

  @Override
  public String toString() {
    return "JinyongWang model";
  }

  @Override
  protected String getModelShortName() {
    return "JW";
  }
}
