package fi.muni.cz.dataprocessing.issuesprocessing;

import org.apache.commons.math3.util.Pair;
import java.util.ArrayList;
import java.util.List;

public class MovingAverage {
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
