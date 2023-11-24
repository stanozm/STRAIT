package fi.muni.cz.core.analysis.phases.dataprocessing;

import static fi.muni.cz.core.factory.FilterFactory.parseDateOption;

import fi.muni.cz.core.ArgsParser;
import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import fi.muni.cz.dataprocessing.issuesprocessing.IssueProcessingStrategy;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesCollection;
import fi.muni.cz.dataprovider.GeneralIssue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** @author Valtteri Valtonen valtonenvaltteri@gmail.com */
public class IssueReportProcessingPhase implements ReliabilityAnalysisPhase {

  private IssueProcessingStrategy issueProcessingStrategy;
  private List<Map<String, String>> issueProcessingResults;
  private List<GeneralIssuesCollection> processedIssues;

  /**
   * Create new issue report processing phase
   *
   * @param issueProcessingStrategy the issue report processing strategy that is to be used
   */
  public IssueReportProcessingPhase(IssueProcessingStrategy issueProcessingStrategy) {
    this.issueProcessingStrategy = issueProcessingStrategy;
    this.issueProcessingResults = new ArrayList<>();
    this.processedIssues = new ArrayList<>();
  }

  @Override
  public ReliabilityAnalysisDto execute(ReliabilityAnalysisDto originalDto) {
    System.out.println("Processing issue reports");

    ReliabilityAnalysisDto dto = setupTestingPeriodDatesBeforeProcessing(originalDto);

    dto.setIssueReportAmountBeforeProcessing(calculateTotalIssuesInDto(dto));

    List<GeneralIssuesCollection> issueReportSets = dto.getIssueReportSets();
    issueReportSets.forEach(
        issuesCollection -> {
          processedIssues.add(applyIssueProcessingStrategyToIssueCollection(issuesCollection));
          issueProcessingResults.add(issueProcessingStrategy.getIssueProcessingActionResults());
        });

    dto.setIssueReportSets(processedIssues);
    dto.setIssueReportAmountAfterProcessing(calculateTotalIssuesInDto(dto));
    dto.setIssueProcessingResults(issueProcessingResults);

    return setupTestingPeriodDatesAfterProcessing(dto);
  }

  private int calculateTotalIssuesInDto(ReliabilityAnalysisDto dto) {
    return (int)
        dto.getIssueReportSets().stream()
            .flatMap(issueReportSet -> issueReportSet.getListOfGeneralIssues().stream())
            .count();
  }

  private GeneralIssuesCollection applyIssueProcessingStrategyToIssueCollection(
      GeneralIssuesCollection issuesCollection) {
    issuesCollection.setListOfGeneralIssues(
        issueProcessingStrategy.apply(
            issuesCollection.getListOfGeneralIssues(),
            issuesCollection.getRepositoryInformation()));
    return issuesCollection;
  }

  private ReliabilityAnalysisDto setupTestingPeriodDatesBeforeProcessing(
      ReliabilityAnalysisDto dto) {

    ArgsParser configuration = dto.getConfiguration();

    if (configuration.hasOptionFilterTime()) {
      List<Date> parsingResult = parseDateOption(configuration);
      dto.setTestingPeriodStartDate(parsingResult.get(0));
      dto.setTestingPeriodEndDate(parsingResult.get(1));

      return dto;
    }

    List<GeneralIssue> sortedIssues = getSortedIssuesFormDto(dto);

    dto.setTestingPeriodStartDate(sortedIssues.get(0).getCreatedAt());
    dto.setTestingPeriodEndDate(sortedIssues.get(sortedIssues.size() - 1).getCreatedAt());

    return dto;
  }

  private ReliabilityAnalysisDto setupTestingPeriodDatesAfterProcessing(
      ReliabilityAnalysisDto dto) {

    ArgsParser configuration = dto.getConfiguration();

    if (configuration.hasOptionFilterBeforeFirstRelease()) {
      List<GeneralIssue> sortedIssues = getSortedIssuesFormDto(dto);
      dto.setTestingPeriodStartDate(sortedIssues.get(0).getCreatedAt());
    }

    return dto;
  }

  private List<GeneralIssue> getSortedIssuesFormDto(ReliabilityAnalysisDto dto) {
    List<GeneralIssue> allIssues =
        dto.getIssueReportSets().stream()
            .flatMap(issuesCollection -> issuesCollection.getListOfGeneralIssues().stream())
            .toList();

    return allIssues.stream()
        .sorted(Comparator.comparing(GeneralIssue::getCreatedAt))
        .collect(Collectors.toList());
  }
}
