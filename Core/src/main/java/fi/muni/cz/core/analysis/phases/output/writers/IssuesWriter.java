package fi.muni.cz.core.analysis.phases.output.writers;

import fi.muni.cz.dataprovider.GeneralIssue;
import java.util.List;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public interface IssuesWriter {
    
    /**
     * Write List of GeneralIssue to file.
     * 
     * @param list      GeneralIssue to write.
     * @param fileName  name of file.
     */
    void writeToFile(List<GeneralIssue> list, String fileName);
}
