package fi.muni.cz.models;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import fi.muni.cz.models.leastsquaresolver.Solver;
import fi.muni.cz.models.leastsquaresolver.SolverResult;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.util.Pair;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** @author Radoslav Micko, 445611@muni.cz */
public class ModelNGTest {

  private static Integer ROUNDING_DECIMAL_AMOUNT = 3;
  private List<Pair<Integer, Integer>> listOfPairs = new ArrayList<>();

  @Mock private Solver solver;

  @InjectMocks private Model GOModel = new GOModelImpl(listOfPairs, listOfPairs, solver);

  @InjectMocks
  private Model GOSShapedModel = new GOSShapedModelImpl(listOfPairs, listOfPairs, solver);

  @InjectMocks private Model DuaneModel = new DuaneModelImpl(listOfPairs, listOfPairs, solver);

  @InjectMocks
  private Model HossainDahiyaModel = new HossainDahiyaModelImpl(listOfPairs, listOfPairs, solver);

  @InjectMocks
  private Model MusaOkumotoModel = new MusaOkumotoModelImpl(listOfPairs, listOfPairs, solver);

  @BeforeClass
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    Pair<Integer, Integer> pair = new Pair<>(1, 1);
    listOfPairs.add(pair);
  }

  private void prepareMocksForTwoParams() {
    Map<String, String> map = new LinkedHashMap<>();
    map.put("a", "a");
    SolverResult solverResult = new SolverResult();
    solverResult.setParameters(new double[]{1, 1});
    solverResult.setAic(1.0);
    solverResult.setBic(1.0);
    when(solver.optimize(any(int[].class), any(List.class))).thenReturn(solverResult);
  }

  private void prepareMocksForThreeParams() {
    Map<String, String> map = new LinkedHashMap<>();
    map.put("a", "a");
    SolverResult solverResult = new SolverResult();
    solverResult.setParameters(new double[]{1.0, 1.0, 1.0});
    solverResult.setAic(1.0);
    solverResult.setBic(1.0);
    when(solver.optimize(any(int[].class), any(List.class))).thenReturn(solverResult);
  }

  @Test
  public void testGOModel() {
    prepareMocksForTwoParams();

    GOModel.estimateModelData(ROUNDING_DECIMAL_AMOUNT);
    assertEquals(GOModel.getModelParameters().get("a"), Double.valueOf(1));
    assertEquals(GOModel.getModelParameters().get("b"), Double.valueOf(1));
  }

  @Test
  public void testGOSShapedModel() {
    prepareMocksForTwoParams();

    GOSShapedModel.estimateModelData(ROUNDING_DECIMAL_AMOUNT);
    assertEquals(GOSShapedModel.getModelParameters().get("a"), Double.valueOf(1));
    assertEquals(GOSShapedModel.getModelParameters().get("b"), Double.valueOf(1));
  }

  @Test
  public void testDuaneModel() {
    prepareMocksForTwoParams();

    DuaneModel.estimateModelData(ROUNDING_DECIMAL_AMOUNT);
    assertEquals(DuaneModel.getModelParameters().get("α"), Double.valueOf(1));
    assertEquals(DuaneModel.getModelParameters().get("β"), Double.valueOf(1));
  }

  @Test
  public void testMusaOkumotoModel() {
    prepareMocksForTwoParams();

    MusaOkumotoModel.estimateModelData(ROUNDING_DECIMAL_AMOUNT);
    assertEquals(MusaOkumotoModel.getModelParameters().get("α"), Double.valueOf(1));
    assertEquals(MusaOkumotoModel.getModelParameters().get("β"), Double.valueOf(1));
  }

  @Test
  public void testHossainDahiyaModel() {
    prepareMocksForThreeParams();

    HossainDahiyaModel.estimateModelData(ROUNDING_DECIMAL_AMOUNT);
    assertEquals(HossainDahiyaModel.getModelParameters().get("a"), Double.valueOf(1));
    assertEquals(HossainDahiyaModel.getModelParameters().get("b"), Double.valueOf(1));
    assertEquals(HossainDahiyaModel.getModelParameters().get("c"), Double.valueOf(1));
  }
}
