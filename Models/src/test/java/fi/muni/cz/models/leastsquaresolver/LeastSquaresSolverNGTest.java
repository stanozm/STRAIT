package fi.muni.cz.models.leastsquaresolver;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.util.Pair;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
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

  @InjectMocks private Solver GOSolver = new GOLeastSquaresSolver(rEngine);
  @InjectMocks private Solver GOSShapedSolver = new GOSShapedLeastSquaresSolver(rEngine);
  @InjectMocks private Solver duaneSolver = new DuaneLeastSquaresSolver(rEngine);
  @InjectMocks private Solver hossainDahiyaSolver = new HossainDahiyaLeastSquaresSolver(rEngine);
  @InjectMocks private Solver musaOkumotoSolver = new MusaOkumotoLeastSquaresSolver(rEngine);
  @InjectMocks private Solver weibullSolver = new WeibullLeastSquaresSolver(rEngine);

  @InjectMocks
  private Solver yamadaExponentialSolver = new YamadaExponentialLeastSquaresSolver(rEngine);

  @InjectMocks private Solver yamadaRaleighSolver = new YamadaRaleighLeastSquaresSolver(rEngine);
  @InjectMocks private Solver logLogisticSolver = new LogLogisticLeastSquaresSolver(rEngine);

  @BeforeClass
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    Pair<Integer, Integer> pair = new Pair<>(1, 1);
    listOfPairs.add(pair);
  }

  @Test
  public void testOptimizeOnAllSolvers() {
    double[] doubleArr = {1.0, 1.0};
    when(rEngine.eval("coef(modelGO)")).thenReturn(new REXP(doubleArr));
    when(rEngine.eval("coef(modelGOS)")).thenReturn(new REXP(doubleArr));
    when(rEngine.eval("coef(modelDuane)")).thenReturn(new REXP(doubleArr));
    when(rEngine.eval("coef(modelMO)")).thenReturn(new REXP(doubleArr));
    when(rEngine.eval("coef(modelGO2)")).thenReturn(new REXP(doubleArr));
    when(rEngine.eval("coef(modelGOS2)")).thenReturn(new REXP(doubleArr));
    when(rEngine.eval("coef(modelDuane2)")).thenReturn(new REXP(doubleArr));
    when(rEngine.eval("coef(modelMO2)")).thenReturn(new REXP(doubleArr));

    when(rEngine.eval(contains("glance"))).thenReturn(new REXP(new int[]{1}));
    when(rEngine.eval(contains("R2nls"))).thenReturn(new REXP(new int[]{1}));

    int[] intArr = {1, 1};

    assertEquals(GOSolver.optimize(intArr, listOfPairs).getParameters(), doubleArr);
    assertEquals(GOSShapedSolver.optimize(intArr, listOfPairs).getParameters(), doubleArr);
    assertEquals(duaneSolver.optimize(intArr, listOfPairs).getParameters(), doubleArr);
    assertEquals(musaOkumotoSolver.optimize(intArr, listOfPairs).getParameters(), doubleArr);

    int[] int3Arr = {1, 1, 1};
    double[] double3Arr = {1.0, 1.0, 1.0};
    when(rEngine.eval("coef(modelHD)")).thenReturn(new REXP(double3Arr));
    when(rEngine.eval("coef(modelWeibull)")).thenReturn(new REXP(double3Arr));
    when(rEngine.eval("coef(modelLogLogistic)")).thenReturn(new REXP(double3Arr));
    when(rEngine.eval("coef(modelYamadaExponential)")).thenReturn(new REXP(double3Arr));
    when(rEngine.eval("coef(modelYamadaRaleigh)")).thenReturn(new REXP(double3Arr));
    when(rEngine.eval("coef(modelHD2)")).thenReturn(new REXP(double3Arr));
    when(rEngine.eval("coef(modelWeibull2)")).thenReturn(new REXP(double3Arr));
    when(rEngine.eval("coef(modelLogLogistic2)")).thenReturn(new REXP(double3Arr));
    when(rEngine.eval("coef(modelYamadaExponential2)")).thenReturn(new REXP(double3Arr));
    when(rEngine.eval("coef(modelYamadaRaleigh2)")).thenReturn(new REXP(double3Arr));
    assertEquals(hossainDahiyaSolver.optimize(int3Arr, listOfPairs).getParameters(), double3Arr);
    assertEquals(weibullSolver.optimize(int3Arr, listOfPairs).getParameters(), double3Arr);
    assertEquals(logLogisticSolver.optimize(int3Arr, listOfPairs).getParameters(), double3Arr);
    assertEquals(
        yamadaExponentialSolver.optimize(int3Arr, listOfPairs).getParameters(), double3Arr);
    assertEquals(yamadaRaleighSolver.optimize(int3Arr, listOfPairs).getParameters(), double3Arr);
  }
}
