package fi.muni.cz.dataprocessing.output;

import java.util.List;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public interface OutputWriter {
    
    /**
     * Write data from OutputData to certain file.
     * 
     * @param outputData data to write 
     * @param fileName name of file
     */
    void writeOutputDataToFile(List<ModelResult> outputData, String fileName);
}
