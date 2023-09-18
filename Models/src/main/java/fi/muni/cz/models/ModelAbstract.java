package fi.muni.cz.models;

import fi.muni.cz.models.leastsquaresolver.Solver;
import fi.muni.cz.models.leastsquaresolver.SolverResult;
import fi.muni.cz.models.testing.GoodnessOfFitTest;
import org.apache.commons.math3.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public abstract class ModelAbstract implements Model {

    protected Map<String, Double> modelParameters;
    protected List<Pair<Integer, Integer>> trainingIssueData;
    protected List<Pair<Integer, Integer>> testingIssueData;
    protected Map<String, String> goodnessOfFit;
    protected Map<String, String> predictiveAccuracy;
    protected GoodnessOfFitTest goodnessOfFitTest;
    protected Solver solver;

    private SolverResult solverResult;
    
    /**
     *
     * Initialize model attributes.
     * 
     * @param trainingIssueData     list of cumulative issue data which model is fit to.
     * @param testingIssueData      list of cumulative issue data used to calculate predictive accuracy
     * @param goodnessOfFitTest     Goodness of fit test to execute.
     * @param solver                Solver to estimate model parameters.
     */
    public ModelAbstract(
            List<Pair<Integer, Integer>> trainingIssueData,
            List<Pair<Integer, Integer>>testingIssueData,
            GoodnessOfFitTest goodnessOfFitTest, Solver solver
    ) {
        this.trainingIssueData = trainingIssueData;
        this.testingIssueData = testingIssueData;
        this.goodnessOfFitTest = goodnessOfFitTest;
        this.solver = solver;
    }
    
    @Override
    public void estimateModelData() {
        System.out.println("Evaluating " + getModelName());
        calculateModelParameters();
        calculateModelGoodnessOfFit();
        calculateModelPredictiveAccuracy();
    }

    @Override
    public List<Pair<Integer, Integer>> getIssuesPrediction(double howMuchToPredict) {
        return calculateEstimatedIssuesOccurance(howMuchToPredict);
    }
    
    /**
     * Calculate model parameters.
     */
    protected void calculateModelParameters() {
        solverResult = solver.optimize(getInitialParametersValue(), trainingIssueData);
        setParametersToMap(solverResult.getParameters());
    }
    
    private void calculateModelGoodnessOfFit() {

        Map<String, String> goodnessOfFitMap = new HashMap<>();
        goodnessOfFitMap.putAll(goodnessOfFitTest.executePerformanceTest(
                calculateEstimatedIssuesOccurance(0),
                trainingIssueData, getModelShortName()));

        goodnessOfFitMap.put("AIC from solver = ", String.valueOf(solverResult.getAic()));
        goodnessOfFitMap.put("BIC from solver = ", String.valueOf(solverResult.getBic()));
        goodnessOfFitMap.put("Pseudo Rsquared from solver = ", String.valueOf(solverResult.getPseudoRSquared()));

        goodnessOfFit = goodnessOfFitMap;
    }

    private void calculateModelPredictiveAccuracy() {
        List<Pair<Integer, Integer>> testSetEstimates = calculateEstimatesForDataSet(testingIssueData);
        predictiveAccuracy = goodnessOfFitTest.executePerformanceTest(
                testSetEstimates,
                testingIssueData,
                getModelShortName()
        );
    }
    
    /**
     * Calculate estimated and predicted issues.
     * 
     * @param howMuchToPredict count of time unites to predict to future.
     * @return Estimated issues occurance.
     */
    private List<Pair<Integer, Integer>> calculateEstimatedIssuesOccurance(double howMuchToPredict) {
        List<Pair<Integer, Integer>> listOfEstimatedIssues = calculateEstimatesForDataSet(trainingIssueData);
        int last = trainingIssueData.get(trainingIssueData.size() - 1).getFirst();
        for (int i = last + 1; i < last + howMuchToPredict; i++) {
            double estimation = getFunctionValue(i);
            Integer roundedEstimation = (int) estimation;
            listOfEstimatedIssues.add(new Pair<>(i, roundedEstimation));
        }
        return listOfEstimatedIssues;
    }

    private List<Pair<Integer, Integer>> calculateEstimatesForDataSet(List<Pair<Integer, Integer>> dataSet) {
        List<Pair<Integer, Integer>> listOfEstimatedIssues = new ArrayList<>();
        for (Pair<Integer, Integer> pair: dataSet) {
            double estimation = getFunctionValue(pair.getFirst());
            Integer roundedEstimation = (int) estimation;
            listOfEstimatedIssues.add(new Pair<>(pair.getFirst(),
                    roundedEstimation == 0 ? 1 : roundedEstimation));
        }
        return listOfEstimatedIssues;
    }
    
    /**
     * Get function value fo testPeriod. 
     * 
     * @param testPeriod    i-th test period.
     * @return              value of function.
     */
    protected abstract double getFunctionValue(Integer testPeriod);
    
    /**
     * Set estimated parameters of model with name to set.
     * 
     * @param params to be saved.
     */
    protected abstract void setParametersToMap(double[] params);

    /**
     * Get short name of Model.
     *
     * @return  short name of model.
     */
    protected abstract String getModelShortName();
    
    /**
     * Get initial estimation of model parameters.
     * 
     * @return initial parameters.
     */
    protected abstract int[] getInitialParametersValue();
    
    @Override
    public Map<String, String> getGoodnessOfFitData() {
        return goodnessOfFit;
    }

    @Override
    public Map<String, String> getPredictiveAccuracyData() {
        return predictiveAccuracy;
    }

    @Override
    public Map<String, Double> getModelParameters() {
        return modelParameters;
    }

    @Override
    public String getModelName() {
        return getModelShortName();
    }
}
