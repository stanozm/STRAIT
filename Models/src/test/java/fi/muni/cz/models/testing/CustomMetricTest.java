package fi.muni.cz.models.testing;

import fi.muni.cz.dataprocessing.issuesprocessing.modeldata.IssuesCounter;
import fi.muni.cz.dataprovider.GeneralIssue;
import org.apache.commons.math3.util.Pair;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.*;

import static fi.muni.cz.models.testing.CustomMetrics.calculateAccuracyOfTheFinalPoint;
import static fi.muni.cz.models.testing.CustomMetrics.calculateNormalizedRootMeanSquaredError;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class CustomMetricTest {

    List<Pair<Integer, Integer>> observedData = new ArrayList<>();
    List<Pair<Integer, Integer>> predictedData = new ArrayList<>();

    @BeforeClass
    public void initialize(){
        observedData.add(Pair.create(1, 1));
        observedData.add(Pair.create(2, 1));
        observedData.add(Pair.create(3, 2));

        predictedData.add(Pair.create(1, 0));
        predictedData.add(Pair.create(2, 0));
        predictedData.add(Pair.create(3, 1));
    }
    
    @Test
    public void testAofp() {
        assertEquals(calculateAccuracyOfTheFinalPoint(observedData, predictedData), 0.5);
    }

    @Test
    public void testNrmse() {
        assertEquals(calculateNormalizedRootMeanSquaredError(observedData, predictedData), 0.5);
    }

}
