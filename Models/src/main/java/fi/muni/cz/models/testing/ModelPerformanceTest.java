package fi.muni.cz.models.testing;

import org.apache.commons.math3.util.Pair;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class ModelPerformanceTest implements GoodnessOfFitTest {

    private static final double ALPHA = 0.05;
    private Rengine rEngine;

    /**
     *  Default constructor to initialize attributes.
     *
     * @param rEngine engine for R chisq.test
     */
    public ModelPerformanceTest(Rengine rEngine) {
        this.rEngine = rEngine;
    }
    
    @Override
    public Map<String, String> executeGoodnessOfFitTest(List<Pair<Integer, Integer>> expectedIssues, 
            List<Pair<Integer, Integer>> observedIssues, String modelName) {
        Map<String, String> result =  calculateTestResults(getPreparedListWithCommas(expectedIssues),
                getPreparedListWithCommas(observedIssues), modelName);
        result.put("Predictive ability = ", calculateModelPredictiveAbility(observedIssues, expectedIssues));
        result.put("Accuracy of the final point = ", calculateAccuracyOfTheFinalPoint(observedIssues, expectedIssues));
        return result;
    }
    
    private Map<String, String> calculateTestResults(String expected, String observe, String modelName) {
        Map<String, String> chiSquareMap = new LinkedHashMap<>();

        rEngine.eval("library(broom)");
        rEngine.eval(String.format("expected%s = c(%s)", modelName, expected));
        rEngine.eval(String.format("observed%s = c(%s)", modelName, observe));
        rEngine.eval(String.format("test%s <- lm(expected%s ~ observed%s)",
                modelName, modelName, modelName));
        REXP rSquared = rEngine.eval(String.format("glance(test%s)$r.squared", modelName));
        REXP aic = rEngine.eval(String.format("glance(test%s)$AIC", modelName));
        REXP bic = rEngine.eval(String.format("glance(test%s)$BIC", modelName));
        REXP rse = rEngine.eval(String.format("glance(test%s)$sigma", modelName));

        chiSquareMap.put("R-Squared = ", String.format(Locale.US, "%.3f", rSquared.asDoubleArray()[0]));
        chiSquareMap.put("R-Squared null hypothesis rejection = ",
                1 - rSquared.asDoubleArray()[0] > ALPHA ? "REJECT" : "NOT REJECT");
        chiSquareMap.put("Null hypothesis = ", "No significant difference between observed and expected values");
        chiSquareMap.put("AIC (Akaike information criterion) = ",
                String.format(Locale.US, "%.3f",aic.asDoubleArray()[0]));
        chiSquareMap.put("BIC (Bayesian Information Criterion) = ",
                String.format(Locale.US, "%.3f",bic.asDoubleArray()[0]));
        chiSquareMap.put("RSE (Residual Standard Error) = ", String.format(Locale.US, "%.3f",rse.asDoubleArray()[0]));
        return chiSquareMap;
    }

    private String calculateModelPredictiveAbility(
            List<Pair<Integer, Integer>> observedData,
            List<Pair<Integer, Integer>> modelPredictedData
    ) {

        int finalObservedIssues = observedData.get(observedData.size() - 1).getSecond();
        float rangeLowerBound = 0.9f * finalObservedIssues;
        float rangeUpperBound = 1.1f * finalObservedIssues;

        Pair<Integer, Integer> cumulativeResultWithinBounds = modelPredictedData.get(modelPredictedData.size() - 1);
        int resultIndex = modelPredictedData.size() - 1;
        boolean withinBounds = true;
        while (withinBounds && resultIndex > 0){

            Pair<Integer, Integer> currentResult = modelPredictedData.get(resultIndex);

            boolean isWithinBounds =
                    rangeLowerBound <= currentResult.getSecond() &&
                            rangeUpperBound >= currentResult.getSecond();

            if(isWithinBounds){
                cumulativeResultWithinBounds = currentResult;
                resultIndex = resultIndex - 1;
            }

            if(!isWithinBounds){
                withinBounds = false;
            }
        }

        int finalTime = observedData.get(observedData.size() - 1).getFirst();
        int convergenceTime = cumulativeResultWithinBounds.getFirst();

        return String.format(Locale.US, "%.3f", convergenceTime / (float)finalTime);

    }

    private String calculateAccuracyOfTheFinalPoint(
            List<Pair<Integer, Integer>> observedData,
            List<Pair<Integer, Integer>> modelPredictedData
    ) {

        int finalObservedIssues = observedData.get(observedData.size() - 1).getSecond();
        int finalEstimatedIssues = modelPredictedData.get(observedData.size() - 1).getSecond();

        float finalPointAccuracy = Math.abs((finalObservedIssues - finalEstimatedIssues) / (float) finalObservedIssues);


        return String.format(Locale.US, "%.3f", finalPointAccuracy);

    }

    private String getPreparedListWithCommas(List<Pair<Integer, Integer>> list) {
        return list.stream().map(value -> value.getSecond().toString()).collect(Collectors.joining(","));
    }
}
