package fi.muni.cz.models.leastsquaresolver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import fi.muni.cz.models.exception.RJriExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.util.Pair;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** @author Radoslav Micko, 445611@muni.cz */
public class LeastSquaresSolverNGTest {

  private final List<Pair<Integer, Integer>> listOfPairs = new ArrayList<>();

  @Mock private Rengine rEngine;
  @Mock private RJriExceptionHandler handler;

  private Solver GOSolver;
  private Solver GOSShapedSolver;
  private Solver duaneSolver;
  private Solver hossainDahiyaSolver;
  private Solver musaOkumotoSolver;
  private Solver weibullSolver;
  private Solver yamadaExponentialSolver;
  private Solver yamadaRaleighSolver;
  private Solver logLogisticSolver;

  @BeforeClass
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    // Create test data
    Pair<Integer, Integer> pair = new Pair<>(1, 1);
    listOfPairs.add(pair);

    // Create solver instances with mocked dependencies
    GOSolver = new GOLeastSquaresSolver(rEngine, handler);
    GOSShapedSolver = new GOSShapedLeastSquaresSolver(rEngine, handler);
    duaneSolver = new DuaneLeastSquaresSolver(rEngine, handler);
    hossainDahiyaSolver = new HossainDahiyaLeastSquaresSolver(rEngine, handler);
    musaOkumotoSolver = new MusaOkumotoLeastSquaresSolver(rEngine, handler);
    weibullSolver = new WeibullLeastSquaresSolver(rEngine, handler);
    yamadaExponentialSolver = new YamadaExponentialLeastSquaresSolver(rEngine, handler);
    yamadaRaleighSolver = new YamadaRaleighLeastSquaresSolver(rEngine, handler);
    logLogisticSolver = new LogLogisticLeastSquaresSolver(rEngine, handler);

    // Set up mock behavior for RJriExceptionHandler
    // These mocks will handle the R operations that would be performed by the handler
    // For models with 2 parameters
    REXP twoParamRexp = new REXP(new double[] {1.0, 1.0});
    // For models with 3 parameters
    REXP threeParamRexp = new REXP(new double[] {1.0, 1.0, 1.0});
    // For model statistics
    REXP statRexp = new REXP(new int[] {1});

    // Mock behavior for handler.safeEval
    when(handler.safeEval(any(Rengine.class), anyString(), anyString())).thenReturn(twoParamRexp);

    // Special cases for specific model function calls
    when(handler.safeEval(any(Rengine.class), contains("coef(modelGO)"), anyString()))
        .thenReturn(twoParamRexp);
    when(handler.safeEval(any(Rengine.class), contains("coef(modelGOS)"), anyString()))
        .thenReturn(twoParamRexp);
    when(handler.safeEval(any(Rengine.class), contains("coef(modelDuane)"), anyString()))
        .thenReturn(twoParamRexp);
    when(handler.safeEval(any(Rengine.class), contains("coef(modelMO)"), anyString()))
        .thenReturn(twoParamRexp);
    when(handler.safeEval(any(Rengine.class), contains("coef(modelGO2)"), anyString()))
        .thenReturn(twoParamRexp);
    when(handler.safeEval(any(Rengine.class), contains("coef(modelGOS2)"), anyString()))
        .thenReturn(twoParamRexp);
    when(handler.safeEval(any(Rengine.class), contains("coef(modelDuane2)"), anyString()))
        .thenReturn(twoParamRexp);
    when(handler.safeEval(any(Rengine.class), contains("coef(modelMO2)"), anyString()))
        .thenReturn(twoParamRexp);

    when(handler.safeEval(any(Rengine.class), contains("coef(modelHD)"), anyString()))
        .thenReturn(threeParamRexp);
    when(handler.safeEval(any(Rengine.class), contains("coef(modelWeibull)"), anyString()))
        .thenReturn(threeParamRexp);
    when(handler.safeEval(any(Rengine.class), contains("coef(modelLogLogistic)"), anyString()))
        .thenReturn(threeParamRexp);
    when(handler.safeEval(
            any(Rengine.class), contains("coef(modelYamadaExponential)"), anyString()))
        .thenReturn(threeParamRexp);
    when(handler.safeEval(any(Rengine.class), contains("coef(modelYamadaRaleigh)"), anyString()))
        .thenReturn(threeParamRexp);
    when(handler.safeEval(any(Rengine.class), contains("coef(modelHD2)"), anyString()))
        .thenReturn(threeParamRexp);
    when(handler.safeEval(any(Rengine.class), contains("coef(modelWeibull2)"), anyString()))
        .thenReturn(threeParamRexp);
    when(handler.safeEval(any(Rengine.class), contains("coef(modelLogLogistic2)"), anyString()))
        .thenReturn(threeParamRexp);
    when(handler.safeEval(
            any(Rengine.class), contains("coef(modelYamadaExponential2)"), anyString()))
        .thenReturn(threeParamRexp);
    when(handler.safeEval(any(Rengine.class), contains("coef(modelYamadaRaleigh2)"), anyString()))
        .thenReturn(threeParamRexp);

    when(handler.safeEval(any(Rengine.class), contains("glance"), anyString()))
        .thenReturn(statRexp);
    when(handler.safeEval(any(Rengine.class), contains("R2nls"), anyString())).thenReturn(statRexp);

    // Mock behavior for validateRexpResult - just do nothing
    // This is a void method so we use Mockito.doNothing(), but it's the default behavior anyway

    // Mock behavior for loadLibrary - just do nothing
    // We don't need to stub this since it's a void method and we want it to do nothing
  }

  @Test
  public void testOptimizeOnAllSolvers() {
    // Set up expected results
    double[] doubleArr = {1.0, 1.0};
    double[] double3Arr = {1.0, 1.0, 1.0};
    int[] intArr = {1, 1};
    int[] int3Arr = {1, 1, 1};

    // Test solvers with 2 parameters
    assertEquals(GOSolver.optimize(intArr, listOfPairs).getParameters(), doubleArr);
    assertEquals(GOSShapedSolver.optimize(intArr, listOfPairs).getParameters(), doubleArr);
    assertEquals(duaneSolver.optimize(intArr, listOfPairs).getParameters(), doubleArr);
    assertEquals(musaOkumotoSolver.optimize(intArr, listOfPairs).getParameters(), doubleArr);

    // Test solvers with 3 parameters
    assertEquals(hossainDahiyaSolver.optimize(int3Arr, listOfPairs).getParameters(), double3Arr);
    assertEquals(weibullSolver.optimize(int3Arr, listOfPairs).getParameters(), double3Arr);
    assertEquals(logLogisticSolver.optimize(int3Arr, listOfPairs).getParameters(), double3Arr);
    assertEquals(
        yamadaExponentialSolver.optimize(int3Arr, listOfPairs).getParameters(), double3Arr);
    assertEquals(yamadaRaleighSolver.optimize(int3Arr, listOfPairs).getParameters(), double3Arr);
  }
}
