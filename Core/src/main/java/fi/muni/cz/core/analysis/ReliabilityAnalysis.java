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

    public ReliabilityAnalysis(ArgsParser config){
        this.setConfig(config);
    }

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
