package fi.muni.cz.core.analysis;

import fi.muni.cz.core.ArgsParser;
import fi.muni.cz.core.analysis.phases.ReliabilityAnalysisPhase;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;
import java.util.List;

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class ReliabilityAnalysis {

    private List<ReliabilityAnalysisPhase> phases;
    private ArgsParser config;

    /**
     * Create new reliability analysis object
     * @param config Command line configuration
     */
    public ReliabilityAnalysis(ArgsParser config){
        this.setConfig(config);
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

    public List<ReliabilityAnalysisPhase> getPhases() {
        return phases;
    }

    public void setPhases(List<ReliabilityAnalysisPhase> phases) {
        this.phases = phases;
    }

    public ArgsParser getConfig() {
        return config;
    }

    public void setConfig(ArgsParser config) {
        this.config = config;
    }
}
