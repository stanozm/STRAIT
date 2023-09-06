package fi.muni.cz.dataprocessing.output;

import fi.muni.cz.dataprovider.ReleaseDTO;
import org.apache.commons.math3.util.Pair;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class ModelResult implements Serializable {
    private Map<String, String> goodnessOfFitData;
    private Map<String, Double> modelParameters;
    private List<Pair<Integer, Integer>> issuesPrediction;
    private String functionTextForm;

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
}