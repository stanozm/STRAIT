package fi.muni.cz.core.analysis.phases.output.writers;

import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.dataprocessing.exception.DataProcessingException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** @author Valtteri Valtonen valtonenvaltteri@gmail.com */
public class HtmlReportWriter implements OutputWriter {

  private static final String TEMPLATE_PATH = "template_new.html";
  private Configuration freemakerTemplateConfiguration;

  /** Create new HtmlReportWriter. */
  public HtmlReportWriter() {
    this.freemakerTemplateConfiguration = TemplateConfigurationUtil.getConfiguration();
  }

  @Override
  public void writeOutputDataToFile(ReliabilityAnalysisDto reliabilityAnalysisData) {
    Map<String, Object> templateMap = new HashMap<>();
    templateMap.put("data", reliabilityAnalysisData);

    writeTemplateToFile(templateMap, getFileName(reliabilityAnalysisData));
  }

  private String getFileName(ReliabilityAnalysisDto reliabilityAnalysisDto) {
    return reliabilityAnalysisDto.getProjectName();
  }

  private void writeTemplateToFile(Map<String, Object> templateMap, String fileName) {
    File file = new File("./output/" + fileName + ".html");
    file.getParentFile().mkdirs();

    try (BufferedWriter writer =
        new BufferedWriter(
            new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8))) {
      Template template = freemakerTemplateConfiguration.getTemplate(TEMPLATE_PATH);
      template.process(templateMap, writer);
      System.out.println("File created - " + fileName + ".html");
    } catch (IOException ex) {
      logAndThrowException("Error occured during writing to file.", ex);
    } catch (TemplateException ex) {
      logAndThrowException("Template error.", ex);
    }
  }

  private void logAndThrowException(String message, Exception ex) {
    Logger.getLogger(HtmlReportWriter.class.getName()).log(Level.SEVERE, message, ex);
    throw new DataProcessingException(message, ex);
  }
}
