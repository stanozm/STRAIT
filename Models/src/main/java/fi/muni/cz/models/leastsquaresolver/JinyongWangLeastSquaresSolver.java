package fi.muni.cz.models.leastsquaresolver;

import fi.muni.cz.models.exception.ModelException;
import java.util.List;
import java.util.Locale;
import org.apache.commons.math3.util.Pair;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/** @author Andrej Mrazik, 456651@muni.cz */
public class JinyongWangLeastSquaresSolver extends SolverAbstract {
  private static final String MODEL_FUNCTION =
      "a / (theta + exp(omega * xvalues)) *"
          + "  (exp(beta * xvalues^d + omega * xvalues) -"
          + "   beta * d * "
          + "sum(sapply(0:n, function(i) ((omega^i * xvalues^(i + d)) / (factorial(i) * (i + d)))) - 1))";
  private static final String MODEL_NAME = "modelJinyongWang";

  /**
   * Initialize Rengine.
   *
   * @param rEngine Rengine.
   */
  public JinyongWangLeastSquaresSolver(Rengine rEngine) {
    super(rEngine);
  }

  @Override
  public SolverResult optimize(int[] startParameters, List<Pair<Integer, Integer>> listOfData) {
    initializeOptimizationInR(listOfData);
    rEngine.eval(
        "modelJinyongWang2 <- nls2(yvalues ~ "
            + MODEL_FUNCTION
            + ", "
            + "start = data.frame("
            + "a = c(100, 10000), d = c(0.01, 10), n = 3, beta = c(0.0001,1), "
            + "omega = c(0.01, 1), theta = c(0.1, 10000)), "
            + "algorithm = \"brute-force\", control = list(warnOnly = TRUE, maxiter = 100000))");
    REXP intermediate = rEngine.eval("coef(" + MODEL_NAME + "2)");
    if (intermediate == null) {
      throw new ModelException("Repository data not suitable for R evaluation.");
    }
    rEngine.eval(
        String.format(
            Locale.US,
            "modelJinyongWang <- nls(yvalues ~ "
                + MODEL_FUNCTION
                + ", "
                + "start = list(a = %.10f,d = %.10f, n = %.10f, beta = %.10f, omega = %.10f, theta = %.10f), "
                + "lower = list(a = 0, d = 0, n = 0, beta = 0, omega = 0, theta = 0), "
                + "control = list(warnOnly = TRUE, maxiter = 100000), "
                + "algorithm = \"port\")",
            intermediate.asDoubleArray()[0],
            intermediate.asDoubleArray()[1],
            intermediate.asDoubleArray()[2],
            intermediate.asDoubleArray()[3],
            intermediate.asDoubleArray()[4],
            intermediate.asDoubleArray()[5]));
    REXP result = rEngine.eval("coef(" + MODEL_NAME + ")");

    rEngine.eval("library(broom)");
    REXP aic = rEngine.eval(String.format("glance(%s)$AIC", MODEL_NAME));
    REXP bic = rEngine.eval(String.format("glance(%s)$BIC", MODEL_NAME));

    rEngine.eval("library(aomisc)");
    REXP pseudoRSquared = rEngine.eval(String.format("R2nls(%s)$PseudoR2", MODEL_NAME));

    rEngine.end();
    if (result == null || result.asDoubleArray().length < 6) {
      return new SolverResult();
    }
    double[] d = result.asDoubleArray();

    SolverResult solverResult = new SolverResult();
    solverResult.setParameters(new double[] {d[0], d[1], d[2], d[3], d[4], d[5]});
    solverResult.setAic(aic.asDoubleArray()[0]);
    solverResult.setBic(bic.asDoubleArray()[0]);
    solverResult.setPseudoRSquared(pseudoRSquared.asDoubleArray()[0]);

    return solverResult;
  }
}
