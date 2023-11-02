package fi.muni.cz.dataprocessing.issuesprocessing;

import org.apache.commons.math3.util.Pair;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class MovingAverage {

    /**
     * Calculate a simple moving average over the given data.
     * Current element is the last element of the moving average window.
     * Float-type average is rounded to the nearest integer.
     *
     * @param inputData Input data
     * @param windowSize Size of the moving average window
     * @return Data to which the moving average has been applied to
     * */
    public static List<Pair<Integer, Integer>> calculateMovingAverage(
            List<Pair<Integer, Integer>> inputData,
            int windowSize
    ){

        List<Pair<Integer, Integer>> paddedData = padDataWithEdgeValues(inputData, windowSize - 1);

        List<Pair<Integer, Integer>> averagedValues = new ArrayList<>();
        for (int i = windowSize - 1; i < inputData.size(); i++) {
            List<Pair<Integer, Integer>> windowElements = paddedData.subList(i-windowSize / 2, i + windowSize / 2);
            float average = windowElements
                    .stream()
                    .map(Pair::getSecond)
                    .reduce(0, Integer::sum) / (float) windowSize;

            averagedValues.add(new Pair<>(inputData.get(i).getFirst(), Math.round(average)));
        }
        return averagedValues;
    }

    private static List<Pair<Integer, Integer>> padDataWithEdgeValues(
            List<Pair<Integer, Integer>> data,
            Integer padAmount
    ) {
        Pair<Integer, Integer> firstValue = data.get(0);
        Pair<Integer, Integer> lastValue = data.get(data.size() - 1);
        List<Pair<Integer, Integer>> paddedData = new ArrayList<>(data);
        for (int p = 0; p < padAmount; p++) {
            paddedData.add(lastValue);
            paddedData.add(0, firstValue);
        }
       return paddedData;
    }
}
