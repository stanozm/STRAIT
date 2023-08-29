package fi.muni.cz.core;

import fi.muni.cz.core.configuration.ReliabilityAnalysisConfiguration;
import fi.muni.cz.core.dto.ReliabilityAnalysisDto;

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class ReliabilityAnalysis {

    ReliabilityAnalysisConfiguration config;

    public ReliabilityAnalysis(ReliabilityAnalysisConfiguration config){
        this.config = config;
    }

    public ReliabilityAnalysisDto performAnalysis(ReliabilityAnalysisDto dto){
        return null;
    }

}
