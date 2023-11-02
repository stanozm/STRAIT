package fi.muni.cz.models;

import fi.muni.cz.models.leastsquaresolver.Solver;
import org.apache.commons.math3.util.Pair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class MusaOkumotoModelImpl extends ModelAbstract {

    private final String firstParameter = "α";
    private final String secondParameter = "β";
    
    /**
     * Initialize model attributes.
     * 
     * @param trainingData          list of issues.
     * @param testData              list of issues.
     * @param solver                Solver to estimate model parameters.
     */
    public MusaOkumotoModelImpl(
            List<Pair<Integer, Integer>> trainingData,
            List<Pair<Integer, Integer>> testData,
            Solver solver) {
        super(trainingData, testData, solver);
    }
    
    @Override
    protected int[] getInitialParametersValue() {
        return new int[]{trainingIssueData.get(trainingIssueData.size() - 1).getSecond(), 1};
    }
    
    @Override
    protected double getFunctionValue(Integer testPeriod) {
        return modelParameters.get(firstParameter) *
                Math.log(modelParameters.get(secondParameter) * testPeriod + 1);        
    }
    
    @Override
    protected void setParametersToMap(double[] params) {
        Map<String, Double> map = new HashMap<>();
        map.put(firstParameter, params[0]);
        map.put(secondParameter, params[1]);
        modelParameters = map;
    }
    
    @Override
    public String getTextFormOfTheFunction() {
        return "μ(t) = α * ln(β * t + 1)";
    }
    
    @Override
    public String toString() {
        return "Musa-Okumoto model";
    }

    @Override
    protected String getModelShortName() {
        return "MO";
    }
}
