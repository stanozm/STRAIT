package fi.muni.cz.core.analysis.phases.modelfitting;

import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.dto.DataPointCollection;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.core.exception.InvalidInputException;
import fi.muni.cz.core.factory.ModelFactory;
import fi.muni.cz.dataprocessing.issuesprocessing.modeldata.TimeBetweenIssuesCounter;
import fi.muni.cz.dataprocessing.output.ModelResult;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesCollection;
import fi.muni.cz.models.Model;
import fi.muni.cz.models.ModelAbstract;
import fi.muni.cz.models.leastsquaresolver.Solver;
import fi.muni.cz.models.testing.ChiSquareGoodnessOfFitTest;
import fi.muni.cz.models.testing.GoodnessOfFitTest;
import org.apache.commons.math3.util.Pair;
import org.rosuda.JRI.Rengine;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelFittingAndGoodnessOfFitTestPhase implements ReliabilityAnalysisPhase {

    private Rengine rEngine;
    public ModelFittingAndGoodnessOfFitTestPhase(){
        this.rEngine = new Rengine(new String[] {"--vanilla"}, false, null);
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

        return dto;
    }

    private List<Model> getModels(ReliabilityAnalysisDto dto, DataPointCollection dataPoints)
            throws InvalidInputException {
        return ModelFactory.getModels(
                dataPoints.getDataPoints(),
                new ChiSquareGoodnessOfFitTest(rEngine),
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

        return modelResult;
    }


}
