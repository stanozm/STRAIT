package fi.muni.cz.models;

import fi.muni.cz.models.leastsquaresolver.Solver;
import org.apache.commons.math3.util.Pair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class HossainDahiyaModelImpl extends ModelAbstract {
    
    private final String firstParameter = "a";
    private final String secondParameter = "b";
    private final String thirdParameter = "c";
    
    /**
     * Initialize model attributes.
     * 
     * @param trainingData          list of issues.
     * @param testData              list of issues.
     * @param solver                Solver to estimate model parameters.
     */
    public HossainDahiyaModelImpl(
            List<Pair<Integer, Integer>> trainingData,
            List<Pair<Integer, Integer>> testData,
            Solver solver) {
        super(trainingData, testData, solver);
    }
    
    @Override
    protected int[] getInitialParametersValue() {
        return new int[]{trainingIssueData.get(trainingIssueData.size() - 1).getSecond(), 1, 1};
    }
    
    @Override
    protected double getFunctionValue(Integer testPeriod) {
        return modelParameters.get(firstParameter) *
                (1 - Math.exp(- modelParameters.get(secondParameter) * testPeriod)) 
                / (1 + modelParameters.get(thirdParameter) 
                * Math.exp(- modelParameters.get(secondParameter) * testPeriod));         
    }
    
    @Override
    protected void setParametersToMap(double[] params) {
        Map<String, Double> map = new HashMap<>();
        map.put(firstParameter, params[0]);
        map.put(secondParameter, params[1]);
        map.put(thirdParameter, params[2]);
        modelParameters = map;
    }
    
    @Override
    public String getTextFormOfTheFunction() {
        return "μ(t) = a * (1 - e<html><sup>-b*t</sup></html>) "
                + "/ (1 + c*e<html><sup>-b*t</sup></html>)";
    }
    
    @Override
    public String toString() {
        return "Hossain-Dahiya model";
    }

    @Override
    protected String getModelShortName() {
        return "HD";
    }
}
