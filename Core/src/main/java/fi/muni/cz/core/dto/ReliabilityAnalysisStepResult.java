package fi.muni.cz.core.dto;

import java.util.List;
import java.util.Map;

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class ReliabilityAnalysisStepResult {
    private String type;
    private List<?> result;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<?> getResult() {
        return result;
    }

    public void setResult(List<?> result) {
        this.result = result;
    }
}
