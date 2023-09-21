package fi.muni.cz.models;

import fi.muni.cz.models.leastsquaresolver.Solver;
import fi.muni.cz.models.leastsquaresolver.SolverResult;
import org.apache.commons.math3.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public abstract class ModelAbstract implements Model{

    protected Map<String, Double> modelParameters;
    protected List<Pair<Integer, Integer>> trainingIssueData;
    protected List<Pair<Integer, Integer>> testingIssueData;
    protected Map<String, String> goodnessOfFit;
    protected Map<String, String> predictiveAccuracy;
    protected Solver solver;

    private SolverResult solverResult;
    
    /**
     *
     * Initialize model attributes.
     * 
     * @param trainingIssueData     list of cumulative issue data which model is fit to.
     * @param testingIssueData      list of cumulative issue data used to calculate predictive accuracy
     * @param solver                Solver to estimate model parameters.
     */
    public ModelAbstract(
            List<Pair<Integer, Integer>> trainingIssueData,
            List<Pair<Integer, Integer>>testingIssueData,
            Solver solver
    ) {
        this.trainingIssueData = trainingIssueData;
        this.testingIssueData = testingIssueData;
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
        if(solverResult.getParameters() != null) {
            setParametersToMap(solverResult.getParameters());
        }
    }
    
    private void calculateModelGoodnessOfFit() {

        double alpha = 0.05;

        Map<String, String> goodnessOfFitMap = new HashMap<>();
        goodnessOfFitMap.put(getModelName() + " AIC = ", convertResultToString(solverResult.getAic()));
        goodnessOfFitMap.put(getModelName() + " BIC = ", convertResultToString(solverResult.getBic()));
        goodnessOfFitMap.put(getModelName() + " Pseudo R2 = ", convertResultToString(solverResult.getPseudoRSquared()));

        if(solverResult.getPseudoRSquared() == null) {
            goodnessOfFitMap.put(getModelName() + " Pseudo R2 NHR = ", "Ignored");
        }

        if(solverResult.getPseudoRSquared() != null) {
            goodnessOfFitMap.put(getModelName() + " Pseudo R2 NHR = ",
                    1 - solverResult.getPseudoRSquared() > alpha ? "REJECT" : "NOT REJECT"
            );
        }

        List<Pair<Integer, Integer>> trainingSetEstimates = solverResult.getParameters() != null
                ? calculateEstimatedIssuesOccurance(0) :
                new ArrayList<>();

        Double residualStandardError = solverResult.getParameters() != null ?
                calculateResidualStandardError(trainingIssueData, trainingSetEstimates)
                : null;

        Double meanSquaredError = solverResult.getParameters() != null ?
                calculateMeanSquaredError(trainingIssueData, trainingSetEstimates)
                : null;

        Double normalizedRootMeanSquaredError = solverResult.getParameters() != null ?
                calculateNormalizedRootMeanSquaredError(trainingIssueData, trainingSetEstimates)
                : null;

        Double predctiveAbility = solverResult.getParameters() != null ?
                calculateModelPredictiveAbility(trainingIssueData, trainingSetEstimates)
                : null;

        Double accuracyOfTheFinalPoint = solverResult.getParameters() != null ?
                calculateAccuracyOfTheFinalPoint(trainingIssueData, trainingSetEstimates)
                : null;

        goodnessOfFitMap.put(getModelName() + " RSE = ", convertResultToString(residualStandardError));
        goodnessOfFitMap.put(getModelName() + " MSE = ", convertResultToString(meanSquaredError));
        goodnessOfFitMap.put(getModelName() + " NRMSE = ", convertResultToString(normalizedRootMeanSquaredError));
        goodnessOfFitMap.put(getModelName() + " PA = ", convertResultToString(predctiveAbility));
        goodnessOfFitMap.put(getModelName() + " AOFP = ", convertResultToString(accuracyOfTheFinalPoint));

        goodnessOfFit = goodnessOfFitMap;
    }

    private void calculateModelPredictiveAccuracy() {
        Map<String, String> predictiveAccuracyMap = new HashMap<>();
        List<Pair<Integer, Integer>> testSetEstimates = solverResult.getParameters() != null ?
                calculateEstimatesForDataSet(testingIssueData) :
                new ArrayList<>();

        Double residualStandardError = solverResult.getParameters() != null ?
                calculateResidualStandardError(testingIssueData, testSetEstimates)
                : null;

        Double meanSquaredError = solverResult.getParameters() != null ?
                calculateMeanSquaredError(testingIssueData, testSetEstimates)
                : null;

        Double normalizedRootMeanSquaredError = solverResult.getParameters() != null ?
                calculateNormalizedRootMeanSquaredError(testingIssueData, testSetEstimates)
                : null;

        Double predctiveAbility = solverResult.getParameters() != null ?
                calculateModelPredictiveAbility(testingIssueData, testSetEstimates)
                : null;

        Double accuracyOfTheFinalPoint = solverResult.getParameters() != null ?
                calculateAccuracyOfTheFinalPoint(testingIssueData, testSetEstimates)
                : null;

        predictiveAccuracyMap.put(getModelName() + " Pred. Acc. RSE = ", convertResultToString(residualStandardError));
        predictiveAccuracyMap.put(getModelName() + " Pred. Acc. MSE = ", convertResultToString(meanSquaredError));
        predictiveAccuracyMap.put(getModelName() + " Pred. Acc. NRMSE = ",
                convertResultToString(normalizedRootMeanSquaredError)
        );
        predictiveAccuracyMap.put(getModelName() + " Pred. Acc. PA = ", convertResultToString(predctiveAbility));
        predictiveAccuracyMap.put(getModelName() + " Pred. Acc. AOFP = ",
                convertResultToString(accuracyOfTheFinalPoint)
        );

        predictiveAccuracy = predictiveAccuracyMap;
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

    private String convertResultToString(Double result) {

        if(result == null) {
            return "Ignored";
        }

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
