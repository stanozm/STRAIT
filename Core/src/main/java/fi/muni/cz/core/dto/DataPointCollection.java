package fi.muni.cz.core.dto;

import java.util.List;
import org.apache.commons.math3.util.Pair;

/** @author Valtteri Valtonen, valtonenvaltteri@gmail.com */
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
