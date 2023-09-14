package fi.muni.cz.core.analysis.phases.modelfitting;

import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.analysis.phases.output.writers.ModelResult;
import fi.muni.cz.core.dto.DataPointCollection;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.core.exception.InvalidInputException;
import fi.muni.cz.core.factory.ModelFactory;
import fi.muni.cz.models.Model;
import fi.muni.cz.models.testing.ModelPerformanceTest;
import org.apache.commons.math3.util.Pair;
import org.rosuda.JRI.Rengine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Valtteri Valtonen valtonenvaltteri@gmail.com
 */
public class ModelFittingAndGoodnessOfFitTestPhase implements ReliabilityAnalysisPhase {

    private Rengine rEngine;

    /**
     * Create new model fitting and goodness of fit test phase
     * @param rEngine R engine
     */
    public ModelFittingAndGoodnessOfFitTestPhase(Rengine rEngine){
        this.rEngine = rEngine;
    };


    @Override
    public ReliabilityAnalysisDto execute(ReliabilityAnalysisDto dto) {

        System.out.println("Fitting models and analyzing performance");

        List<List<ModelResult>> modelResults = new ArrayList<>();

        for(DataPointCollection dataPointCollection : dto.getCumulativeIssueReportCollections()){

            float trainingDataPortion = 0.66f;
            int trainingDataEndIndex = Math.round(trainingDataPortion * dataPointCollection.getDataPoints().size());

            List<Pair<Integer, Integer>> trainingData = dataPointCollection
                    .getDataPoints()
                    .subList(0, trainingDataEndIndex);

            List<Pair<Integer, Integer>> testData = dataPointCollection
                    .getDataPoints()
                    .subList(
                            trainingDataEndIndex, dataPointCollection.getDataPoints()
                            .size()
                    );


            try {
                List<ModelResult> currentCollectionResults = Collections.synchronizedList(new ArrayList<>());
                List<Model> models = getModels(dto, trainingData, testData);
                models.parallelStream().forEach(model -> {
                    currentCollectionResults.add(performModelEstimationAndGoodnessofFitTest(model, testData.size()));
                });
                modelResults.add(currentCollectionResults);
            } catch(InvalidInputException e) {
                e.printStackTrace();
            }
        }

        dto.setModelResults(modelResults);
        dto.setSolver("Least Squares solver");

        return dto;
    }

    private List<Model> getModels(
            ReliabilityAnalysisDto dto,
            List<Pair<Integer, Integer>> trainingData,
            List<Pair<Integer, Integer>> testData)
            throws InvalidInputException {

        return ModelFactory.getModels(
                trainingData,
                testData,
                new ModelPerformanceTest(rEngine),
                dto.getConfiguration()
        );
    }

    private ModelResult performModelEstimationAndGoodnessofFitTest(
            Model model,
            Integer testDataSize
    ) {

        model.estimateModelData();

        ModelResult modelResult = new ModelResult();
        modelResult.setModelParameters(model.getModelParameters());
        modelResult.setIssuesPrediction(model.getIssuesPrediction(testDataSize));
        modelResult.setGoodnessOfFitData(model.getGoodnessOfFitData());
        modelResult.setPredictiveAccuracyData(model.getPredictiveAccuracyData());
        modelResult.setFunctionTextForm(model.getTextFormOfTheFunction());
        modelResult.setModelName(model.getModelName());

        return modelResult;
    }


}
