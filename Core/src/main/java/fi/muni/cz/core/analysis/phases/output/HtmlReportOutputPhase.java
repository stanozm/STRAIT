package fi.muni.cz.core.analysis.phases.output;

import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.analysis.phases.output.writers.HtmlReportWriter;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;


public class HtmlReportOutputPhase implements ReliabilityAnalysisPhase {

    private HtmlReportWriter reportWriter;

    public HtmlReportOutputPhase(){
        this.reportWriter = new HtmlReportWriter();
    }

    @Override
    public ReliabilityAnalysisDto execute(ReliabilityAnalysisDto dto) {
        reportWriter.writeOutputDataToFile(dto);
        return dto;
    }

}
