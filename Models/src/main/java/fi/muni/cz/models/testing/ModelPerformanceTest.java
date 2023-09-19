package fi.muni.cz.models.testing;

import org.apache.commons.math3.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class ModelPerformanceTest implements GoodnessOfFitTest {
    
    @Override
    public Map<String, String> executePerformanceTest(List<Pair<Integer, Integer>> expectedIssues,
                                                      List<Pair<Integer, Integer>> observedIssues, String modelName) {
        Map<String, String> result = new HashMap<>();
        result.put("RSE = ", roundResultToThreeDecimals(
                calculateResidualStandardError(observedIssues, expectedIssues)));
        result.put("MSE = ", roundResultToThreeDecimals(
                calculateMeanSquaredError(observedIssues, expectedIssues)));
        result.put("NRMSE = ", roundResultToThreeDecimals(
                calculateNormalizedRootMeanSquaredError(observedIssues, expectedIssues)));
        result.put("PA = ", roundResultToThreeDecimals(
                calculateModelPredictiveAbility(observedIssues, expectedIssues)));
        result.put("AOFP = ", roundResultToThreeDecimals(
                calculateAccuracyOfTheFinalPoint(observedIssues, expectedIssues)));
        return result;
    }

    private Double calculateModelPredictiveAbility(
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

        return convergenceTime / (double)finalTime;

    }

    private Double calculateAccuracyOfTheFinalPoint(
            List<Pair<Integer, Integer>> observedData,
            List<Pair<Integer, Integer>> modelPredictedData
    ) {

        int finalObservedIssues = observedData.get(observedData.size() - 1).getSecond();
        int finalEstimatedIssues = modelPredictedData.get(observedData.size() - 1).getSecond();

        Double finalPointAccuracy =
                Math.abs((finalObservedIssues - finalEstimatedIssues) / (double) finalObservedIssues);


        return finalPointAccuracy;

    }

    private Double calculateResidualStandardError(
            List<Pair<Integer, Integer>> observedData,
            List<Pair<Integer, Integer>> modelPredictedData
    ) {

        Integer squareSumOfResiduals = calculateSumOfSquaresOfDataPointValues(
                calculateResidualsForDataPoints(observedData, modelPredictedData)
        );

        Double rse = Math.sqrt(squareSumOfResiduals / (observedData.size()));

        return rse;

    }

    private Double calculateMeanSquaredError(
            List<Pair<Integer, Integer>> observedData,
            List<Pair<Integer, Integer>> modelPredictedData
    ) {

        Integer squareSumOfResiduals = calculateSumOfSquaresOfDataPointValues(
                calculateResidualsForDataPoints(observedData, modelPredictedData)
        );

        Double meanCoefficient = 1.0 / observedData.size();

        return meanCoefficient * squareSumOfResiduals;

    }

    private Double calculateNormalizedRootMeanSquaredError(
            List<Pair<Integer, Integer>> observedData,
            List<Pair<Integer, Integer>> modelPredictedData
    ) {

        Double meanSquaredError = calculateMeanSquaredError(observedData, modelPredictedData);
        Double rootMeanSquaredError = Math.sqrt(meanSquaredError);

        Integer maximumObservedValue = observedData.get(observedData.size() - 1).getSecond();

        return rootMeanSquaredError/maximumObservedValue;

    }

    private String roundResultToThreeDecimals(Double result) {
        return String.format(Locale.US, "%.3f", result);
    }

    private List<Pair<Integer, Integer>> calculateResidualsForDataPoints(
            List<Pair<Integer, Integer>> observedData,
            List<Pair<Integer, Integer>> modelPredictedData
    ) {

        List<Pair<Integer, Integer>> result = new ArrayList<>();
        for (int i = 0; i < observedData.size(); i++) {
            Pair<Integer, Integer> observed = observedData.get(i);
            Pair<Integer, Integer> predicted = modelPredictedData.get(i);

            Pair<Integer, Integer> residual = Pair.create(
                    observed.getFirst(),
                    observed.getSecond() - predicted.getSecond()
            );
            result.add(residual);
        }
        return result;
    }

    private Integer calculateSumOfSquaresOfDataPointValues(
            List<Pair<Integer, Integer>> dataPoints
    ) {
        Integer result = 0;
        for (int i = 0; i < dataPoints.size(); i++) {
            Pair<Integer, Integer> currentPoint = dataPoints.get(i);
            Integer pointValue = currentPoint.getValue();
             result += pointValue * pointValue;
        }
        return result;
    }

}
