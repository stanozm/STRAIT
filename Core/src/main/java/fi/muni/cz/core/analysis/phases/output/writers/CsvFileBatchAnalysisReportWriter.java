package fi.muni.cz.core.analysis.phases.output.writers;

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

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class CsvFileBatchAnalysisReportWriter implements BatchOutputWriter {
    private static final String FILE_HEADER = "Project name,Trend test,Total issues,Defects used for fitting,";

    @Override
    public void writeBatchOutputDataToFile(List<List<ModelResult>> list, String fileName) {
        String filePath = "./output/" + fileName + CSV_FILE_SUFFIX;
        File file = new File(filePath);
        System.out.println("Writing batch result report to path " + filePath);
        try (FileWriter fileWriter = new FileWriter(file)){
            if(list.isEmpty() || list.get(0).isEmpty()){
                System.out.println("Batch report writing failed.");
                throw new DataProcessingException("No output data with which to write batch report");
            }

            List<String> goodnessOfFitKeys = new ArrayList<>(
                    list.get(0).get(0)
                            .getGoodnessOfFit().keySet()
            ).stream().sorted().collect(Collectors.toList());

            List<String> issueProcessingStrategyResultKeys = new ArrayList<>(
                    list.get(0).get(0)
                            .getIssueProcessingActionResults().keySet()
            ).stream().sorted().collect(Collectors.toList());

            String modelResultHeaders = list.get(0).stream().flatMap(outputData -> {
                String modelName = outputData.getModelName();
                List<String> headers = goodnessOfFitKeys.stream().map(testName -> modelName + " " +  testName + ",")
                        .collect(Collectors.toList());
                return headers.stream();
            }).collect(Collectors.joining());

            String issueProcessingStrategyResultHeaders = issueProcessingStrategyResultKeys
                    .stream()
                    .map(str -> str + ",")
                    .collect(Collectors.joining());

            String extendedHeader = FILE_HEADER + modelResultHeaders + issueProcessingStrategyResultHeaders;


            fileWriter.append(extendedHeader);
            fileWriter.append(NEW_LINE_SEPARATOR);
            for (List<ModelResult> outputs : list) {
                writeElementWithDelimiter(
                        eliminateSeparatorAndCheckNullValue(outputs.get(0).getRepositoryName()), fileWriter);
                writeElementWithDelimiter(
                        eliminateSeparatorAndCheckNullValue(outputs.get(0).isExistTrend()), fileWriter);
                writeElementWithDelimiter(
                        eliminateSeparatorAndCheckNullValue(outputs.get(0).getInitialNumberOfIssues()), fileWriter);
                writeElementWithDelimiter(
                        eliminateSeparatorAndCheckNullValue(outputs.get(0).getTotalNumberOfDefects()), fileWriter);

                for (ModelResult output : outputs) {
                    for (String testName : goodnessOfFitKeys){
                        writeElementWithDelimiter(eliminateSeparatorAndCheckNullValue(
                                output.getGoodnessOfFit().get(testName)), fileWriter);
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
