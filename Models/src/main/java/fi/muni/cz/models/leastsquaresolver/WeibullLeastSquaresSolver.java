package fi.muni.cz.models.leastsquaresolver;

import fi.muni.cz.models.exception.ModelException;
import java.util.List;
import java.util.Locale;

import org.apache.commons.math3.util.Pair;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class WeibullLeastSquaresSolver extends SolverAbstract {

    private static final String MODEL_FUNCTION = "a * (1 - exp(-b * (xvalues ^ c)))";

    /**
     * Initialize Rengine.
     * @param rEngine Rengine.
     */
    public WeibullLeastSquaresSolver(Rengine rEngine) {
        super(rEngine);
    }

    @Override
    public double[] optimize(int[] startParameters, List<Pair<Integer, Integer>> listOfData) {
        initializeOptimizationInR(listOfData);
        rEngine.eval("modelWeibull2 <- nls2(yvalues ~ " + MODEL_FUNCTION + ", " +
                "start = data.frame(a = c(100, 10000),b = c(0.00001, 1), c = c(1, 10)), " +
                "algorithm = \"brute-force\", control = list(warnOnly = TRUE, maxiter = 100000))");
        REXP intermediate = rEngine.eval("coef(modelWeibull2)");
        if (intermediate == null) {
            throw new ModelException("Repository data not suitable for R evaluation.");
        }
        rEngine.eval(String.format(Locale.US, "modelWeibull <- nls(yvalues ~ " + MODEL_FUNCTION + ", "
                + "start = list(a = %.10f,b = %.10f,c = %.10f), "
                + "control = list(warnOnly = TRUE, maxiter = 100000), "
                + "lower = list(a = 0,b = 0,c = 0), algorithm = \"port\")",
                intermediate.asDoubleArray()[0], intermediate.asDoubleArray()[1], intermediate.asDoubleArray()[2]));
        REXP result = rEngine.eval("coef(modelWeibull)");
        rEngine.end();
        if (result == null || result.asDoubleArray().length < 3) {
            throw new ModelException("Repository data not suitable for R evaluation.");
        }
        double[] d = result.asDoubleArray();
        return new double[]{d[0], d[1], d[2]};
    }
}