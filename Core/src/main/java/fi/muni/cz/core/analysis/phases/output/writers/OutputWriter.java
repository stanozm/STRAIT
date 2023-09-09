package fi.muni.cz.core.analysis.phases.output.writers;

import fi.muni.cz.core.dto.ReliabilityAnalysisDto;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public interface OutputWriter {
    
    /**
     * Write data from OutputData to certain file.
     * 
     * @param reliabilityAnalysisData data to write
     */
    void writeOutputDataToFile(ReliabilityAnalysisDto reliabilityAnalysisData);
}
