package fi.muni.cz.core.analysis;

import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import java.util.List;

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class ReliabilityAnalysis {

    private List<ReliabilityAnalysisPhase> phases;

    /**
     * Create new reliability analysis object
     * @param analysisPhases List of analysis phases
     */
    public ReliabilityAnalysis(List<ReliabilityAnalysisPhase> analysisPhases) {
        this.phases = analysisPhases;
    }

    /**
     * Perform reliability analysis and save data to given dto
     * @param dto Reliability analysis dto
     * @return Reliability analysis dto with filled data
     */
    public ReliabilityAnalysisDto performAnalysis(ReliabilityAnalysisDto dto){
        ReliabilityAnalysisDto currentDto = dto;
        for(ReliabilityAnalysisPhase phase : phases){
            currentDto = phase.execute(currentDto);
        }
        return currentDto;
    }

    public void setPhases(List<ReliabilityAnalysisPhase> phases) {
        this.phases = phases;
    }

}
