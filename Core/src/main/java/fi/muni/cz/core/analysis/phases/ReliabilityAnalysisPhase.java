package fi.muni.cz.core.analysis.phases;

import fi.muni.cz.core.dto.ReliabilityAnalysisDto;

/**
 * @author Valtteri Valtonen valtonenvaltteri@gmail.com
 */
public interface ReliabilityAnalysisPhase {

    /**
     * Execute this reliability analysis phase
     * @param dto Reliability analysis dto
     * @return Updated reliability analysis dto
     */
    ReliabilityAnalysisDto execute(ReliabilityAnalysisDto dto);

}
