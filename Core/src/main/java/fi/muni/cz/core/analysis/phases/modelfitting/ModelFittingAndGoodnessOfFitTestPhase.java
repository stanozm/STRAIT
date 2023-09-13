package fi.muni.cz.core.analysis.phases.modelfitting;

import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.analysis.phases.output.writers.ModelResult;
import fi.muni.cz.core.dto.DataPointCollection;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.core.exception.InvalidInputException;
import fi.muni.cz.core.factory.ModelFactory;
import fi.muni.cz.models.Model;
import fi.muni.cz.models.testing.ModelPerformanceTest;
import org.rosuda.JRI.Rengine;
import java.util.ArrayList;
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

        List<List<ModelResult>> modelResults = new ArrayList<>();

        for(DataPointCollection dataPointCollection : dto.getCumulativeIssueReportCollections()){
            try {
                List<ModelResult> currentCollectionResults = new ArrayList<>();
                List<Model> models = getModels(dto, dataPointCollection);
                models.forEach(model -> {
                    currentCollectionResults.add(performModelEstimationAndGoodnessofFitTest(model));
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

    private List<Model> getModels(ReliabilityAnalysisDto dto, DataPointCollection dataPoints)
            throws InvalidInputException {
        return ModelFactory.getModels(
                dataPoints.getDataPoints(),
                new ModelPerformanceTest(rEngine),
                dto.getConfiguration()
        );
    }

    private ModelResult performModelEstimationAndGoodnessofFitTest(
            Model model
    ) {

        model.estimateModelData();

        ModelResult modelResult = new ModelResult();
        modelResult.setModelParameters(model.getModelParameters());
        modelResult.setIssuesPrediction(model.getIssuesPrediction(0.0));
        modelResult.setGoodnessOfFitData(model.getGoodnessOfFitData());
        modelResult.setFunctionTextForm(model.getTextFormOfTheFunction());
        modelResult.setModelName(model.getModelName());

        return modelResult;
    }


}
