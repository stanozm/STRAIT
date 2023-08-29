package fi.muni.cz.models.testing;

import org.apache.commons.math3.util.Pair;
import java.util.List;
import java.util.Map;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public interface GoodnessOfFitTest {
    
    /**
     * Execute the goodness of fit (GOF) test.
     * 
     * @param expectedIssues    Estimated list of data from model.
     * @param observedIssues    Real observed data.
     * @param modelName         Name of model.
     * @return                  Map with estimated goodness of fit data.
     */
    Map<String, String> executeGoodnessOfFitTest(List<Pair<Integer, Integer>> expectedIssues, 
            List<Pair<Integer, Integer>> observedIssues, String modelName);
}
