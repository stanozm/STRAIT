package fi.muni.cz.models.leastsquaresolver;

import fi.muni.cz.models.exception.ModelException;
import java.util.List;
import java.util.Locale;
import org.apache.commons.math3.util.Pair;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/** @author Andrej Mrazik, 456651@muni.cz */
public class LiLeastSquaresSolver extends SolverAbstract {
  private static final String MODEL_FUNCTION =
      "a * (1 - exp(-n * ((1 / (1 + phi * exp(-v * xvalues))) - (1 / (1 + phi)))))";
  private static final String MODEL_NAME = "modelLi";

  /**
   * Initialize Rengine.
   *
   * @param rEngine Rengine.
   */
  public LiLeastSquaresSolver(Rengine rEngine) {
    super(rEngine);
  }

  @Override
  public SolverResult optimize(int[] startParameters, List<Pair<Integer, Integer>> listOfData) {
    initializeOptimizationInR(listOfData);
    rEngine.eval(
        "modelLi2 <- nls2(yvalues ~ "
            + MODEL_FUNCTION
            + ", "
            + "start = data.frame(a = c(10, 100000),n = c(0.0001, 1000), phi = c(0.0001, 1000), v = c(0.0001, 1000)), "
            + "algorithm = \"brute-force\", control = list(warnOnly = TRUE, maxiter = 100000))");
    REXP intermediate = rEngine.eval("coef(" + MODEL_NAME + "2)");
    if (intermediate == null) {
      throw new ModelException("Repository data not suitable for R evaluation.");
    }
    rEngine.eval(
        String.format(
            Locale.US,
            "modelLi <- nls(yvalues ~ "
                + MODEL_FUNCTION
                + ", "
                + "start = list(a = %.10f, n = %.10f, phi = %.10f, v = %.10f), "
                + "lower = list(a = 0, n = 0, phi = 0, v = 0), "
                + "control = list(warnOnly = TRUE, maxiter = 100000), "
                + "algorithm = \"port\")",
            intermediate.asDoubleArray()[0],
            intermediate.asDoubleArray()[1],
            intermediate.asDoubleArray()[2],
            intermediate.asDoubleArray()[3]));
    REXP result = rEngine.eval("coef(" + MODEL_NAME + ")");

    rEngine.eval("library(broom)");
    REXP aic = rEngine.eval(String.format("glance(%s)$AIC", MODEL_NAME));
    REXP bic = rEngine.eval(String.format("glance(%s)$BIC", MODEL_NAME));

    rEngine.eval("library(aomisc)");
    REXP pseudoRSquared = rEngine.eval(String.format("R2nls(%s)$PseudoR2", MODEL_NAME));

    rEngine.end();
    if (result == null || result.asDoubleArray().length < 4) {
      return new SolverResult();
    }
    double[] d = result.asDoubleArray();

    SolverResult solverResult = new SolverResult();
    solverResult.setParameters(new double[] {d[0], d[1], d[2], d[3]});
    solverResult.setAic(aic.asDoubleArray()[0]);
    solverResult.setBic(bic.asDoubleArray()[0]);
    solverResult.setPseudoRSquared(pseudoRSquared.asDoubleArray()[0]);

    return solverResult;
  }
}
