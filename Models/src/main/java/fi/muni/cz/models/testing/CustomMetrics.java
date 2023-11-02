package fi.muni.cz.models.testing;

import org.apache.commons.math3.util.Pair;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class CustomMetrics {


    /**
     * Calculate value for the software reliability growth model predictive ability metric.
     * This metric describes how long it takes for the model predictions to converge within 10 percent
     * of the final observed issue amount value.
     * @param observedData Observed actual values
     * @param modelPredictedData Model predicted values
     * @return predictive ability as a double
     */
    public static Double calculateModelPredictiveAbility(
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

    /**
     * Calculate the accuracy of the final point metric for the reliability model fit.
     * @param observedData Observed actual values
     * @param modelPredictedData Model predicted values
     * @return Accuracy of final point as a double
     */
    public static Double calculateAccuracyOfTheFinalPoint(
            List<Pair<Integer, Integer>> observedData,
            List<Pair<Integer, Integer>> modelPredictedData
    ) {

        int finalObservedIssues = observedData.get(observedData.size() - 1).getSecond();
        int finalEstimatedIssues = modelPredictedData.get(observedData.size() - 1).getSecond();


        return Math.abs((finalObservedIssues - finalEstimatedIssues) / (double) finalObservedIssues);

    }

    /**
     * Calculate the residual standard error for the reliability model fit.
     * @param observedData Observed actual values
     * @param modelPredictedData Model predicted values
     * @return Residual standard error as a double
     */
    public static  Double calculateResidualStandardError(
            List<Pair<Integer, Integer>> observedData,
            List<Pair<Integer, Integer>> modelPredictedData
    ) {

        BigInteger squareSumOfResiduals = calculateSumOfSquaresOfDataPointValues(
                calculateResidualsForDataPoints(observedData, modelPredictedData)
        );

        return Math.sqrt(squareSumOfResiduals.divide(BigInteger.valueOf(observedData.size())).doubleValue());

    }

    /**
     * Calculate the mean squared error metric for the reliability model fit.
     * @param observedData Observed actual values
     * @param modelPredictedData Model predicted values
     * @return Mean squared error as a double
     */
    public static Double calculateMeanSquaredError(
            List<Pair<Integer, Integer>> observedData,
            List<Pair<Integer, Integer>> modelPredictedData
    ) {

        BigInteger squareSumOfResiduals = calculateSumOfSquaresOfDataPointValues(
                calculateResidualsForDataPoints(observedData, modelPredictedData)
        );

        double meanCoefficient = 1.0 / observedData.size();

        return squareSumOfResiduals.doubleValue() * meanCoefficient;

    }

    /**
     * Calculate the normalized root mean squared error for the reliability model fit.
     * @param observedData Observed actual values
     * @param modelPredictedData Model predicted values
     * @return NRMSE as a double
     */
    public static Double calculateNormalizedRootMeanSquaredError(
            List<Pair<Integer, Integer>> observedData,
            List<Pair<Integer, Integer>> modelPredictedData
    ) {

        Double meanSquaredError = calculateMeanSquaredError(observedData, modelPredictedData);
        Double rootMeanSquaredError = Math.sqrt(meanSquaredError);

        Integer maximumObservedValue = observedData.get(observedData.size() - 1).getSecond();

        return rootMeanSquaredError/maximumObservedValue;

    }

    private static List<Pair<Integer, Integer>> calculateResidualsForDataPoints(
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


    private static BigInteger calculateSumOfSquaresOfDataPointValues(
            List<Pair<Integer, Integer>> dataPoints
    ) {
        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < dataPoints.size(); i++) {
            Pair<Integer, Integer> currentPoint = dataPoints.get(i);
            BigInteger pointValue = BigInteger.valueOf(currentPoint.getValue());
            result = result.add(pointValue.pow(2));
        }
        return result;
    }


}
