package fi.muni.cz.core.factory;

import fi.muni.cz.core.ArgsParser;
import fi.muni.cz.core.exception.InvalidInputException;
import fi.muni.cz.models.*;
import fi.muni.cz.models.leastsquaresolver.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.util.Pair;
import org.rosuda.JRI.Rengine;

/** @author Radoslav Micko, 445611@muni.cz */
public class ModelFactory {

  public static final String GOEL_OKUMOTO = "go";
  public static final String GOEL_OKUMOTO_SSHAPED = "gos";
  public static final String MUSA_OKUMOTO = "mo";
  public static final String DUANE = "du";
  public static final String HOSSAIN_DAHIYA = "hd";
  public static final String WEIBULL = "we";
  public static final String YAMADA_EXPONENTIAL = "ye";
  public static final String YAMADA_RALEIGH = "yr";
  public static final String LOG_LOGISITC = "ll";
  public static final String PHAM_NORDMANN_ZHANG = "pnz";
  public static final String PHAM_ZHANG = "pz";
  public static final String WANG = "wa";
  public static final String LI = "li";
  public static final String JINYONG_WANG = "jw";
  public static final String EMPTY_MODEL = "em";

  public static final String SOLVER_LEAST_SQUARES = "ls";
  public static final String SOLVER_MAXIMUM_LIKELIHOOD = "ml";

  private static Rengine rengine;

  public static void setREngine(Rengine rEngine) {
    rengine = rEngine;
  }

  public static Rengine getREngine() {
    return rengine;
  }

  /**
   * Get all Model to run.
   *
   * @param trainingData cumulative data.
   * @param testData cumulative data.
   * @param parser parsed CommandLine.
   * @return list of Models.
   * @throws InvalidInputException when there is no such model from cmdl.
   */
  public static List<Model> getModels(
      List<Pair<Integer, Integer>> trainingData,
      List<Pair<Integer, Integer>> testData,
      ArgsParser parser)
      throws InvalidInputException {
    List<Model> models = new ArrayList<>();
    if (parser.hasOptionModels()) {
      for (String modelArg : parser.getOptionValuesModels()) {
        models.add(ModelFactory.getModel(trainingData, testData, modelArg, parser));
      }
    } else {
      models.add(ModelFactory.getModel(trainingData, testData, ModelFactory.GOEL_OKUMOTO, parser));
      models.add(
          ModelFactory.getModel(trainingData, testData, ModelFactory.GOEL_OKUMOTO_SSHAPED, parser));
      models.add(
          ModelFactory.getModel(trainingData, testData, ModelFactory.HOSSAIN_DAHIYA, parser));
      models.add(ModelFactory.getModel(trainingData, testData, ModelFactory.MUSA_OKUMOTO, parser));
      models.add(ModelFactory.getModel(trainingData, testData, ModelFactory.DUANE, parser));
      models.add(ModelFactory.getModel(trainingData, testData, ModelFactory.WEIBULL, parser));
      models.add(
          ModelFactory.getModel(trainingData, testData, ModelFactory.YAMADA_EXPONENTIAL, parser));
      models.add(
          ModelFactory.getModel(trainingData, testData, ModelFactory.YAMADA_RALEIGH, parser));
      models.add(ModelFactory.getModel(trainingData, testData, ModelFactory.LOG_LOGISITC, parser));
      models.add(
          ModelFactory.getModel(trainingData, testData, ModelFactory.PHAM_NORDMANN_ZHANG, parser));
      models.add(ModelFactory.getModel(trainingData, testData, ModelFactory.PHAM_ZHANG, parser));
      models.add(ModelFactory.getModel(trainingData, testData, ModelFactory.WANG, parser));
      models.add(ModelFactory.getModel(trainingData, testData, ModelFactory.LI, parser));
      models.add(ModelFactory.getModel(trainingData, testData, ModelFactory.JINYONG_WANG, parser));
    }
    return models;
  }

  /**
   * Get Model for string value.
   *
   * @param cumulativeTrainingData cumulative data.
   * @param goodnes-of-fit.
   * @param modelArg represnetation of model.
   * @return Model
   * @throws InvalidInputException when there is no such implmented model.
   */
  private static Model getModel(
      List<Pair<Integer, Integer>> cumulativeTrainingData,
      List<Pair<Integer, Integer>> cumulativeTestData,
      String modelArg,
      ArgsParser parser)
      throws InvalidInputException {
    switch (modelArg) {
      case GOEL_OKUMOTO:
        return new GOModelImpl(
            cumulativeTrainingData,
            cumulativeTestData,
            getSolverBySolverArgument(parser, GOLeastSquaresSolver.class));
      case GOEL_OKUMOTO_SSHAPED:
        return new GOSShapedModelImpl(
            cumulativeTrainingData,
            cumulativeTestData,
            getSolverBySolverArgument(parser, GOSShapedLeastSquaresSolver.class));
      case MUSA_OKUMOTO:
        return new MusaOkumotoModelImpl(
            cumulativeTrainingData,
            cumulativeTestData,
            getSolverBySolverArgument(parser, MusaOkumotoLeastSquaresSolver.class));
      case DUANE:
        return new DuaneModelImpl(
            cumulativeTrainingData,
            cumulativeTestData,
            getSolverBySolverArgument(parser, DuaneLeastSquaresSolver.class));
      case HOSSAIN_DAHIYA:
        return new HossainDahiyaModelImpl(
            cumulativeTrainingData,
            cumulativeTestData,
            getSolverBySolverArgument(parser, HossainDahiyaLeastSquaresSolver.class));
      case WEIBULL:
        return new WeibullModelImpl(
            cumulativeTrainingData,
            cumulativeTestData,
            getSolverBySolverArgument(parser, WeibullLeastSquaresSolver.class));
      case YAMADA_EXPONENTIAL:
        return new YamadaExponentialModelImpl(
            cumulativeTrainingData,
            cumulativeTestData,
            getSolverBySolverArgument(parser, YamadaExponentialLeastSquaresSolver.class));
      case YAMADA_RALEIGH:
        return new YamadaRaleighModelImpl(
            cumulativeTrainingData,
            cumulativeTestData,
            getSolverBySolverArgument(parser, YamadaRaleighLeastSquaresSolver.class));
      case LOG_LOGISITC:
        return new LogLogisticModelImpl(
            cumulativeTrainingData,
            cumulativeTestData,
            getSolverBySolverArgument(parser, LogLogisticLeastSquaresSolver.class));
      case PHAM_NORDMANN_ZHANG:
        return new PhamNordmannZhangModelImpl(
            cumulativeTrainingData,
            cumulativeTestData,
            getSolverBySolverArgument(parser, PhamNordmannZhangLeastSquaresSolver.class));
      case PHAM_ZHANG:
        return new PhamZhangModelImpl(
            cumulativeTrainingData,
            cumulativeTestData,
            getSolverBySolverArgument(parser, PhamZhangLeastSquaresSolver.class));
      case WANG:
        return new WangModelImpl(
            cumulativeTrainingData,
            cumulativeTestData,
            getSolverBySolverArgument(parser, WangLeastSquaresSolver.class));
      case LI:
        return new LiModelImpl(
            cumulativeTrainingData,
            cumulativeTestData,
            getSolverBySolverArgument(parser, LiLeastSquaresSolver.class));
      case JINYONG_WANG:
        return new JinyongWangModelImpl(
            cumulativeTrainingData,
            cumulativeTestData,
            getSolverBySolverArgument(parser, JinyongWangLeastSquaresSolver.class));
      case EMPTY_MODEL:
        return new EmptyModelImpl(
            cumulativeTrainingData,
            cumulativeTestData,
            getSolverBySolverArgument(parser, EmptyLeastSquaresSolver.class));
      default:
        throw new InvalidInputException(
            Arrays.asList("No such model implemented: '" + modelArg + "'"));
    }
  }

  private static <T> T getSolverBySolverArgument(ArgsParser parser, Class<T> solverClass)
      throws InvalidInputException {
    try {
      if (parser.hasOptionSolver()) {
        switch (parser.getOptionValueSolver()) {
          case SOLVER_LEAST_SQUARES:
            return solverClass.getDeclaredConstructor(Rengine.class).newInstance(rengine);
          case SOLVER_MAXIMUM_LIKELIHOOD:
            // To be implemented
            throw new InvalidInputException(
                Arrays.asList(
                    "No such solver implemented: '" + parser.getOptionValueSolver() + "'"));
          default:
            throw new InvalidInputException(
                Arrays.asList(
                    "No such solver implemented: '" + parser.getOptionValueSolver() + "'"));
        }
      } else {
        return solverClass.getDeclaredConstructor(Rengine.class).newInstance(rengine);
      }
    } catch (ReflectiveOperationException ex) {
      throw new IllegalArgumentException(ex);
    }
  }
}
