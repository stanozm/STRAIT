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
        List<Pair<Integer, Integer>> averagedValues = new ArrayList<>();
        for (int i = 0; i < inputData.size(); i++) {
            List<Pair<Integer, Integer>> windowElements = inputData.subList(Math.max(0, i-windowSize), i);
            float average = windowElements
                    .stream()
                    .map(Pair::getSecond)
                    .reduce(0, Integer::sum) / (float) windowSize;

            averagedValues.add(new Pair<>(inputData.get(i).getFirst(), Math.round(average)));
        }
        return averagedValues;
    }
}
