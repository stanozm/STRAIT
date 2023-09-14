package fi.muni.cz.core.analysis.phases.output.writers;

import org.apache.commons.math3.util.Pair;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class ModelResult implements Serializable {
    private Map<String, String> goodnessOfFitData;

    private Map<String, String> predictiveAccuracyData;
    private Map<String, Double> modelParameters;
    private List<Pair<Integer, Integer>> issuesPrediction;
    private String functionTextForm;
    private String modelName;

    public Map<String, String> getGoodnessOfFitData() {
        return goodnessOfFitData;
    }

    public void setGoodnessOfFitData(Map<String, String> goodnessOfFitData) {
        this.goodnessOfFitData = goodnessOfFitData;
    }

    public Map<String, Double> getModelParameters() {
        return modelParameters;
    }

    public void setModelParameters(Map<String, Double> modelParameters) {
        this.modelParameters = modelParameters;
    }

    public List<Pair<Integer, Integer>> getIssuesPrediction() {
        return issuesPrediction;
    }

    public void setIssuesPrediction(List<Pair<Integer, Integer>> issuesPrediction) {
        this.issuesPrediction = issuesPrediction;
    }

    public String getFunctionTextForm() {
        return functionTextForm;
    }

    public void setFunctionTextForm(String functionTextForm) {
        this.functionTextForm = functionTextForm;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Map<String, String> getPredictiveAccuracyData() {
        return predictiveAccuracyData;
    }

    public void setPredictiveAccuracyData(Map<String, String> predictiveAccuracyData) {
        this.predictiveAccuracyData = predictiveAccuracyData;
    }
}