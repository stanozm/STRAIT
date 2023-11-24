package fi.muni.cz.core.analysis.phases.output.writers;

import com.opencsv.CSVWriter;
import fi.muni.cz.core.analysis.phases.modelfitting.TrendTestResult;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** @author Valtteri Valtonen, valtonenvaltteri@gmail.com */
public class CsvFileBatchAnalysisReportWriter implements BatchOutputWriter {

  private static final String FILE_NAME = "batchAnalysisReport.csv";

  @Override
  public void writeBatchOutputDataToFile(List<ReliabilityAnalysisDto> dtoList) {
    File file = new File(FILE_NAME);
    try {
      FileWriter outputFileWriter = new FileWriter(file);
      CSVWriter writer = new CSVWriter(outputFileWriter);

      writer.writeNext(generateFileHeader(dtoList.get(0)).toArray(new String[0]));

      for (ReliabilityAnalysisDto dto : dtoList) {
        writer.writeNext(generateFileRow(dto).toArray(new String[0]));
      }

      writer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private List<String> generateFileHeader(ReliabilityAnalysisDto dto) {
    List<String> header = new ArrayList<>();
    header.add("Project name");

    for (int i = 0; i < dto.getModelResults().size(); i++) {

      header.add("Issue amount");
      header.add("Trend test");

      List<ModelResult> modelResults = dto.getModelResults().get(i);

      for (ModelResult modelResult : modelResults) {

        modelResult.getGoodnessOfFitData().forEach((first, second) -> header.add(first));
      }

      for (ModelResult modelResult : modelResults) {

        modelResult.getPredictiveAccuracyData().forEach((first, second) -> header.add(first));
      }

      Map<String, String> issueProcessingResults = dto.getIssueProcessingResults().get(i);
      issueProcessingResults.forEach((first, second) -> header.add(first));
    }

    return header;
  }

  private List<String> generateFileRow(ReliabilityAnalysisDto dto) {
    List<String> row = new ArrayList<>();
    row.add(dto.getProjectName());

    for (int i = 0; i < dto.getModelResults().size(); i++) {

      row.add(String.valueOf(dto.getIssueReportAmountAfterProcessing()));

      TrendTestResult trendTestResult = dto.getTrendTestResults().get(i);
      row.add(String.valueOf(trendTestResult.isTrendFound()));

      List<ModelResult> modelResults = dto.getModelResults().get(i);

      for (ModelResult modelResult : modelResults) {

        modelResult.getGoodnessOfFitData().forEach((first, second) -> row.add(second));
      }

      for (ModelResult modelResult : modelResults) {

        modelResult.getPredictiveAccuracyData().forEach((first, second) -> row.add(second));
      }

      Map<String, String> issueProcessingResults = dto.getIssueProcessingResults().get(i);
      issueProcessingResults.forEach((first, second) -> row.add(second));
    }

    return row;
  }
}
