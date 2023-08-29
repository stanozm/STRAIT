package fi.muni.cz.dataprocessing.output;

import java.util.List;

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public interface BatchOutputWriter {
    
    /**
     * Write batch analysis output data to a file.
     * A batch analysis involves multiple data sources.
     * 
     * @param outputData data to write 
     * @param fileName name of file
     */
    void writeBatchOutputDataToFile(List<List<ModelResult>> outputData, String fileName);
}
