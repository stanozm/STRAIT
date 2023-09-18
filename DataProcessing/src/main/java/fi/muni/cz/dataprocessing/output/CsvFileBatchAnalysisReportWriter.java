package fi.muni.cz.dataprocessing.output;

import static fi.muni.cz.dataprocessing.output.CsvUtil.CSV_FILE_SUFFIX;
import static fi.muni.cz.dataprocessing.output.CsvUtil.NEW_LINE_SEPARATOR;
import static fi.muni.cz.dataprocessing.output.CsvUtil.eliminateSeparatorAndCheckNullValue;
import static fi.muni.cz.dataprocessing.output.CsvUtil.writeElementWithDelimiter;

import fi.muni.cz.dataprocessing.exception.DataProcessingException;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class CsvFileBatchAnalysisReportWriter implements BatchOutputWriter {
    private static final String FILE_HEADER = "Project name,Trend test,Total issues,Defects used for fitting,";

    @Override
    public void writeBatchOutputDataToFile(List<List<OutputData>> list, String fileName) {
        String filePath = "./output/" + fileName + CSV_FILE_SUFFIX;

        List<List<OutputData>> notEmptyResults =
                list.stream()
                .filter(l -> !l.isEmpty())
                .collect(Collectors.toList());

        if(notEmptyResults.isEmpty()){
            System.out.println("Will not write batch report because there is no output data available");
            return;
        }

        File file = new File(filePath);
        System.out.println("Writing batch result report to path " + filePath);
        try (FileWriter fileWriter = new FileWriter(file)){

            List<String> goodnessOfFitKeys = new ArrayList<>(
                    notEmptyResults.get(0).get(0)
                            .getGoodnessOfFit().keySet()
            ).stream().sorted().collect(Collectors.toList());

            List<String> predictiveAccuracyKeys = new ArrayList<>(
                    notEmptyResults.get(0).get(0)
                            .getPredictiveAccuracy().keySet()
            ).stream().sorted().collect(Collectors.toList());

            List<String> issueProcessingStrategyResultKeys = new ArrayList<>(
                    notEmptyResults.get(0).get(0)
                            .getIssueProcessingActionResults().keySet()
            ).stream().sorted().collect(Collectors.toList());

            String modelResultHeaders = notEmptyResults.get(0).stream().flatMap(outputData -> {
                String modelName = outputData.getModelName();
                List<String> gofHeaders = goodnessOfFitKeys.stream().map(testName -> modelName + " " +  testName + ",")
                        .collect(Collectors.toList());
                List<String> predictiveAccuracyHeaders = predictiveAccuracyKeys.stream().map(
                        testName -> modelName + " " + "predictive accuracy" + " " +  testName + ","
                        ).collect(Collectors.toList());
                return Stream.concat(gofHeaders.stream(), predictiveAccuracyHeaders.stream());
            }).collect(Collectors.joining());

            String issueProcessingStrategyResultHeaders = issueProcessingStrategyResultKeys
                    .stream()
                    .map(str -> str + ",")
                    .collect(Collectors.joining());

            String extendedHeader = FILE_HEADER + modelResultHeaders + issueProcessingStrategyResultHeaders;


            fileWriter.append(extendedHeader);
            fileWriter.append(NEW_LINE_SEPARATOR);
            for (List<OutputData> outputs : notEmptyResults) {
                writeElementWithDelimiter(
                        eliminateSeparatorAndCheckNullValue(outputs.get(0).getRepositoryName()), fileWriter);
                writeElementWithDelimiter(
                        eliminateSeparatorAndCheckNullValue(outputs.get(0).isExistTrend()), fileWriter);
                writeElementWithDelimiter(
                        eliminateSeparatorAndCheckNullValue(outputs.get(0).getInitialNumberOfIssues()), fileWriter);
                writeElementWithDelimiter(
                        eliminateSeparatorAndCheckNullValue(outputs.get(0).getTotalNumberOfDefects()), fileWriter);

                for (OutputData output : outputs) {
                    for (String testName : goodnessOfFitKeys){
                        writeElementWithDelimiter(eliminateSeparatorAndCheckNullValue(
                                output.getGoodnessOfFit().getOrDefault(testName, "N/A")), fileWriter);
                    }

                    for (String testName : predictiveAccuracyKeys){
                        writeElementWithDelimiter(eliminateSeparatorAndCheckNullValue(
                                output.getPredictiveAccuracy().getOrDefault(testName, "N/A")), fileWriter);
                    }

                }

                for(String resultName : issueProcessingStrategyResultKeys){
                    writeElementWithDelimiter(eliminateSeparatorAndCheckNullValue(
                            outputs.get(0).getIssueProcessingActionResults().get(resultName)), fileWriter);
                }

                fileWriter.append(NEW_LINE_SEPARATOR);
            }
        } catch (Exception ex) {
            System.out.println("Batch report writing failed.");
            throw new DataProcessingException("Error while creating csv file.", ex);
        }
    }

}
