package fi.muni.cz.models.leastsquaresolver;

import fi.muni.cz.models.exception.ModelException;
import java.util.List;
import java.util.Locale;
import org.apache.commons.math3.util.Pair;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/** @author Andrej Mrazik, 456651@muni.cz */
public class PhamZhangLeastSquaresSolver extends SolverAbstract {
  private static final String MODEL_FUNCTION =
      "((c + a) * (1 - exp(-b * xvalues))"
          + " - (a * b * (exp(-alpha * xvalues) - exp(-b * xvalues)) / (b - alpha)))"
          + " / (1 + beta * exp(-b * xvalues))";
  private static final String MODEL_NAME = "modelPhamZhang";

  /**
   * Initialize Rengine.
   *
   * @param rEngine Rengine.
   */
  public PhamZhangLeastSquaresSolver(Rengine rEngine) {
    super(rEngine);
  }

  @Override
  public SolverResult optimize(int[] startParameters, List<Pair<Integer, Integer>> listOfData) {
    initializeOptimizationInR(listOfData);
    rEngine.eval(
        "modelPhamZhang2 <- nls2(yvalues ~ "
            + MODEL_FUNCTION
            + ", "
            + "start = data.frame("
            + "a = c(10, 1000),b = c(0.01, 10), c = 10, alpha = c(0.01, 10), beta = c(0.01, 10)), "
            + "algorithm = \"brute-force\", control = list(warnOnly = TRUE, maxiter = 100000))");
    REXP intermediate = rEngine.eval("coef(" + MODEL_NAME + "2)");
    if (intermediate == null) {
      throw new ModelException("Repository data not suitable for R evaluation (nls2).");
    }
    rEngine.eval(
        String.format(
            Locale.US,
            "modelPhamZhang <- nls(yvalues ~ "
                + MODEL_FUNCTION
                + ", "
                + "start = list(a = %.10f,b = %.10f, c = %.10f, alpha = %.10f, beta = %.10f), "
                + "lower = list(a = 0, b = 0, c = 0, alpha = 0, beta = 0), "
                + "control = list(warnOnly = TRUE, maxiter = 100000), "
                + "algorithm = \"port\")",
            intermediate.asDoubleArray()[0],
            intermediate.asDoubleArray()[1],
            intermediate.asDoubleArray()[2],
            intermediate.asDoubleArray()[3],
            intermediate.asDoubleArray()[4]));
    REXP result = rEngine.eval("coef(" + MODEL_NAME + ")");

    rEngine.eval("library(broom)");
    REXP aic = rEngine.eval(String.format("glance(%s)$AIC", MODEL_NAME));
    REXP bic = rEngine.eval(String.format("glance(%s)$BIC", MODEL_NAME));

    rEngine.eval("library(aomisc)");
    REXP pseudoRSquared = rEngine.eval(String.format("R2nls(%s)$PseudoR2", MODEL_NAME));

    rEngine.end();
    if (result == null || result.asDoubleArray().length < 5) {
      throw new ModelException("Repository data not suitable for R evaluation (nls).");
    }
    double[] d = result.asDoubleArray();

    SolverResult solverResult = new SolverResult();
    solverResult.setParameters(new double[] {d[0], d[1], d[2], d[3], d[4]});
    solverResult.setAic(aic.asDoubleArray()[0]);
    solverResult.setBic(bic.asDoubleArray()[0]);
    solverResult.setPseudoRSquared(pseudoRSquared.asDoubleArray()[0]);

    return solverResult;
  }
}
