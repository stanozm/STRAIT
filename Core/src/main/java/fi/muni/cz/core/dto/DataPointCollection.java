package fi.muni.cz.core.dto;

import org.apache.commons.math3.util.Pair;
import java.util.List;

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class DataPointCollection {
    private String name;
    private List<Pair<Integer, Integer>> dataPoints;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Pair<Integer, Integer>> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<Pair<Integer, Integer>> dataPoints) {
        this.dataPoints = dataPoints;
    }
}
