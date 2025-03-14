package fi.muni.cz.models;

import fi.muni.cz.models.leastsquaresolver.Solver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.util.Pair;

/** @author Andrej Mrazik, 456651@muni.cz */
public class JinyongWangModelImpl extends ModelAbstract {
  private final String firstParameter = "a";
  private final String secondParameter = "theta";
  private final String thirdParameter = "omega";
  private final String fourthParameter = "beta";
  private final String fifthParameter = "d";

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

  /**
   * Calculate factorial with protection against overflow.
   *
   * @param num Integer for factorial calculation
   * @return Factorial as double
   */
  private double factorial(int num) {
    if (num == 0 || num == 1) {
      return 1.0;
    }
    if (num > 170) { // Protect against overflow, same limit as in R
      return Double.POSITIVE_INFINITY;
    }
    double result = 1.0;
    for (int i = 2; i <= num; i++) {
      result *= i;
    }
    return result;
  }

  /**
   * Calculate the sum term using logarithms for numerical stability. This matches the R
   * implementation of calc_sum_term_total.
   *
   * @param omega Parameter omega
   * @param t Time value
   * @param d Parameter d
   * @param n Maximum iteration
   * @return Sum of terms
   */
  private double calcSumTermTotal(double omega, double t, double d, int n) {
    double total = 0.0;

    for (int i = 0; i <= n; i++) {
      if (i > 170) break; // Same factorial limit as in R

      // Use logarithms for more stable calculation
      double logTerm =
          i * Math.log(omega) + (i + d) * Math.log(t) - Math.log(factorial(i)) - Math.log(i + d);
      double term = Math.exp(logTerm);

      if (Double.isFinite(term)) {
        total += term;
      }
    }

    return total;
  }

  @Override
  protected double getFunctionValue(Integer testPeriod) {
    double a = modelParameters.get(firstParameter);
    double theta = modelParameters.get(secondParameter);
    double omega = modelParameters.get(thirdParameter);
    double beta = modelParameters.get(fourthParameter);
    double d = modelParameters.get(fifthParameter);
    int n = 5;

    double t = testPeriod;

    // Check for overflow condition
    if (omega * t > 700) {
      return a; // Asymptotic behavior for high values
    }

    // Calculate sum term using the numerically stable method
    double sumTerm = calcSumTermTotal(omega, t, d, n);

    // Calculate components
    double denominator = theta + Math.exp(omega * t);
    double expTermExponent = beta * Math.pow(t, d) + omega * t;

    double expTerm;
    if (expTermExponent > 700) {
      expTerm = a; // Asymptotic behavior for high values
    } else {
      expTerm = Math.exp(expTermExponent);
    }

    // Calculate result with checks for validity
    double result = (a / denominator) * (expTerm - beta * d * sumTerm - 1);

    // Ensure result is non-negative
    return Math.max(0, result);
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
    return "μ(t) = a / (θ + e^(ωt)) * (e^(βt^d + ωt) - βd * sum_term - 1)";
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
