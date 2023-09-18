package fi.muni.cz.core.factory;

import fi.muni.cz.core.ArgsParser;
import fi.muni.cz.core.exception.InvalidInputException;
import fi.muni.cz.models.DuaneModelImpl;
import fi.muni.cz.models.EmptyModelImpl;
import fi.muni.cz.models.GOModelImpl;
import fi.muni.cz.models.GOSShapedModelImpl;
import fi.muni.cz.models.HossainDahiyaModelImpl;
import fi.muni.cz.models.LogLogisticModelImpl;
import fi.muni.cz.models.Model;
import fi.muni.cz.models.MusaOkumotoModelImpl;
import fi.muni.cz.models.WeibullModelImpl;
import fi.muni.cz.models.YamadaExponentialModelImpl;
import fi.muni.cz.models.YamadaRaleighModelImpl;
import fi.muni.cz.models.leastsquaresolver.DuaneLeastSquaresSolver;
import fi.muni.cz.models.leastsquaresolver.EmptyLeastSquaresSolver;
import fi.muni.cz.models.leastsquaresolver.GOLeastSquaresSolver;
import fi.muni.cz.models.leastsquaresolver.GOSShapedLeastSquaresSolver;
import fi.muni.cz.models.leastsquaresolver.HossainDahiyaLeastSquaresSolver;
import fi.muni.cz.models.leastsquaresolver.LogLogisticLeastSquaresSolver;
import fi.muni.cz.models.leastsquaresolver.MusaOkumotoLeastSquaresSolver;
import fi.muni.cz.models.leastsquaresolver.WeibullLeastSquaresSolver;
import fi.muni.cz.models.leastsquaresolver.YamadaExponentialLeastSquaresSolver;
import fi.muni.cz.models.leastsquaresolver.YamadaRaleighLeastSquaresSolver;
import fi.muni.cz.models.testing.GoodnessOfFitTest;
import org.apache.commons.math3.util.Pair;
import org.rosuda.JRI.Rengine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class ModelFactory {
    
    public static final String  GOEL_OKUMOTO = "go";
    public static final String  GOEL_OKUMOTO_SSHAPED = "gos";
    public static final String  MUSA_OKUMOTO = "mo";
    public static final String  DUANE = "du";
    public static final String  HOSSAIN_DAHIYA = "hd";
    public static final String  WEIBULL = "we";
    public static final String  YAMADA_EXPONENTIAL = "ye";
    public static final String  YAMADA_RALEIGH = "yr";
    public static final String  LOG_LOGISITC = "ll";
    public static final String EMPTY_MODEL = "em";

    public static final String SOLVER_LEAST_SQUARES = "ls";
    public static final String SOLVER_MAXIMUM_LIKELIHOOD = "ml";
        
    private static Rengine rengine;

    public static void setREngine(Rengine rEngine) {
        rengine = rEngine;
    }

    /**
     * Get all Model to run.
     * 
     * @param trainingData          cumulative data.
     * @param testData              cumulative data.
     * @param goodnessOfFitTest     goodnes-of-fit.
     * @param parser                parsed CommandLine.
     * @return                      list of Models.
     * @throws InvalidInputException when there is no such model from cmdl.
     */
    public static List<Model> getModels(
            List<Pair<Integer, Integer>> trainingData,
            List<Pair<Integer, Integer>> testData,
            GoodnessOfFitTest goodnessOfFitTest, ArgsParser parser) throws InvalidInputException {
        List<Model> models = new ArrayList<>(); 
        if (parser.hasOptionModels()) {
            for (String modelArg: parser.getOptionValuesModels()) {
                models.add(ModelFactory.getModel(trainingData, testData, goodnessOfFitTest, modelArg, parser));
            }
        } else {
            models.add(ModelFactory.getModel(trainingData, testData, goodnessOfFitTest,
                    ModelFactory.GOEL_OKUMOTO, parser));
            models.add(ModelFactory.getModel(trainingData, testData, goodnessOfFitTest,
                    ModelFactory.GOEL_OKUMOTO_SSHAPED, parser));
            models.add(ModelFactory.getModel(trainingData, testData, goodnessOfFitTest,
                    ModelFactory.HOSSAIN_DAHIYA, parser));
            models.add(ModelFactory.getModel(trainingData, testData, goodnessOfFitTest,
                    ModelFactory.MUSA_OKUMOTO, parser));
            models.add(ModelFactory.getModel(trainingData, testData, goodnessOfFitTest,
                    ModelFactory.DUANE, parser));
            models.add(ModelFactory.getModel(trainingData, testData, goodnessOfFitTest,
                    ModelFactory.WEIBULL, parser));
            models.add(ModelFactory.getModel(trainingData, testData, goodnessOfFitTest,
                    ModelFactory.YAMADA_EXPONENTIAL, parser));
            models.add(ModelFactory.getModel(trainingData, testData, goodnessOfFitTest,
                    ModelFactory.YAMADA_RALEIGH, parser));
            models.add(ModelFactory.getModel(trainingData, testData, goodnessOfFitTest,
                    ModelFactory.LOG_LOGISITC, parser));
        }
        return models;
    }
    
    /**
     * Get Model for string value.
     * 
     * @param cumulativeTrainingData cumulative data.
     * @param goodnessOfFitTest     goodnes-of-fit.
     * @param modelArg              represnetation of model.
     * @return Model
     * @throws InvalidInputException when there is no such implmented model.
     */
    private static Model getModel(
            List<Pair<Integer, Integer>> cumulativeTrainingData,
            List<Pair<Integer, Integer>> cumulativeTestData,
            GoodnessOfFitTest goodnessOfFitTest, String modelArg, ArgsParser parser) throws InvalidInputException {
        switch (modelArg) {
            case GOEL_OKUMOTO:
                return new GOModelImpl(cumulativeTrainingData, cumulativeTestData, goodnessOfFitTest,
                        getSolverBySolverArgument(parser, GOLeastSquaresSolver.class));
            case GOEL_OKUMOTO_SSHAPED:
                return new GOSShapedModelImpl(cumulativeTrainingData, cumulativeTestData, goodnessOfFitTest,
                        getSolverBySolverArgument(parser, GOSShapedLeastSquaresSolver.class));
            case MUSA_OKUMOTO:
                return new MusaOkumotoModelImpl(cumulativeTrainingData,  cumulativeTestData, goodnessOfFitTest,
                        getSolverBySolverArgument(parser, MusaOkumotoLeastSquaresSolver.class));
            case DUANE:
                return new DuaneModelImpl(cumulativeTrainingData, cumulativeTestData, goodnessOfFitTest,
                        getSolverBySolverArgument(parser, DuaneLeastSquaresSolver.class));
            case HOSSAIN_DAHIYA:
                return new HossainDahiyaModelImpl(cumulativeTrainingData, cumulativeTestData, goodnessOfFitTest,
                        getSolverBySolverArgument(parser, HossainDahiyaLeastSquaresSolver.class));
            case WEIBULL:
                return new WeibullModelImpl(cumulativeTrainingData, cumulativeTestData, goodnessOfFitTest,
                        getSolverBySolverArgument(parser, WeibullLeastSquaresSolver.class));
            case YAMADA_EXPONENTIAL:
                return new YamadaExponentialModelImpl(cumulativeTrainingData, cumulativeTestData, goodnessOfFitTest,
                        getSolverBySolverArgument(parser, YamadaExponentialLeastSquaresSolver.class));
            case YAMADA_RALEIGH:
                return new YamadaRaleighModelImpl(cumulativeTrainingData, cumulativeTestData, goodnessOfFitTest,
                        getSolverBySolverArgument(parser, YamadaRaleighLeastSquaresSolver.class));
            case LOG_LOGISITC:
                return new LogLogisticModelImpl(cumulativeTrainingData, cumulativeTestData, goodnessOfFitTest,
                        getSolverBySolverArgument(parser, LogLogisticLeastSquaresSolver.class));
            case EMPTY_MODEL:
                return new EmptyModelImpl(cumulativeTrainingData, cumulativeTestData, goodnessOfFitTest,
                        getSolverBySolverArgument(parser, EmptyLeastSquaresSolver.class));
            default:
                throw new InvalidInputException(Arrays.asList("No such model implemented: '" + modelArg + "'")); 
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
                        throw new InvalidInputException(Arrays.asList("No such solver implemented: '"
                                + parser.getOptionValueSolver() + "'"));
                    default:
                        throw new InvalidInputException(Arrays.asList("No such solver implemented: '"
                                + parser.getOptionValueSolver() + "'"));
                }
            } else {
                return solverClass.getDeclaredConstructor(Rengine.class).newInstance(rengine);
            }
        } catch (ReflectiveOperationException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
