package fi.muni.cz.core.analysis.phases.output;

import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.analysis.phases.output.writers.HtmlReportWriter;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;

/** @author Valtteri Valtonen valtonenvaltteri@gmail.com */
public class HtmlReportOutputPhase implements ReliabilityAnalysisPhase {

  private HtmlReportWriter reportWriter;

  /** Create new HTML report output phase */
  public HtmlReportOutputPhase() {
    this.reportWriter = new HtmlReportWriter();
  }

  @Override
  public ReliabilityAnalysisDto execute(ReliabilityAnalysisDto dto) {
    System.out.println("Writing html report");
    reportWriter.writeOutputDataToFile(dto);
    dto.clearIssueReportsAndDataPoints();
    return dto;
  }
}
