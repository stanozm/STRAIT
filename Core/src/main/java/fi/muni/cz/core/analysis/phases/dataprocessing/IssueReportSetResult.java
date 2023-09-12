package fi.muni.cz.core.analysis.phases.dataprocessing;

import java.util.Map;

/**
 * @author Valtteri Valtonen valtonenvaltteri@gmail.com
 */
public class IssueReportSetResult {
    private Map<String, String> result;

    public Map<String, String> getResult() {
        return result;
    }

    public void setResult(Map<String, String> result) {
        this.result = result;
    }
}
