package fi.muni.cz.models;

import org.apache.commons.math3.util.Pair;
import java.util.List;
import java.util.Map;

/**
 * Represents interface for software reliability models
 * 
 * @author Radoslav Micko, 445611@muni.cz
 */
public interface Model {
    
    /**
     * Calculate all parameters need for models functions and execute all data test.
     */
    void estimateModelData();

    /**
     * Get data of executed goodness of fit (GOF) test.
     * 
     * @return  Map with test output data.
     */
    Map<String, String> getGoodnessOfFitData();

    /**
     * Get predictive accuracy data.
     *
     * @return  Map with test output data.
     */
    Map<String, String> getPredictiveAccuracyData();
    
    /**
     * Get parameters of model.
     * 
     * @return  Map with parameters of model.
     */
    Map<String, Double> getModelParameters();
    
    /**
     * Get list of expected / predicted issues occurance.
     * 
     * @param howMuchToPredict  How much time units predict to future.
     * @return                  list of expected / predicted issues.
     */
    List<Pair<Integer, Integer>> getIssuesPrediction(double howMuchToPredict);
    
    /**
     * Get text form of the model function.
     * 
     * @return String form.
     */
    String getTextFormOfTheFunction();

    /**
     * Get model name in string form
     * @return Model name string
     */
    String getModelName();
}