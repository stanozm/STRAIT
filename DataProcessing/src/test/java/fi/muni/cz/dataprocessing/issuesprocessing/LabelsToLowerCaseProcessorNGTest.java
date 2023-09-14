package fi.muni.cz.dataprocessing.issuesprocessing;

import static org.testng.Assert.assertEquals;

import fi.muni.cz.dataprovider.GeneralIssue;
import fi.muni.cz.dataprovider.RepositoryInformation;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class LabelsToLowerCaseProcessorNGTest {
    
    private final IssuesProcessor processor = new LabelsToLowerCaseProcessor();
   
    private final List<GeneralIssue> listOfIssues = new ArrayList<>();
    
    @BeforeClass
    public void setUp() {
        GeneralIssue issue = new GeneralIssue();
        issue.setLabels(Arrays.asList("BUG", "Error", "fault"));
        listOfIssues.add(issue);
    }
    
    @Test
    public void testLabelsToLowerCase() {
        assertEquals(processor.apply(
                listOfIssues,
                new RepositoryInformation()).get(0).getLabels().get(0),
                "bug"
        );
        assertEquals(processor.apply(
                listOfIssues,
                new RepositoryInformation()).get(0).getLabels().get(1),
                "error"
        );
        assertEquals(processor.apply(
                listOfIssues,
                new RepositoryInformation()).get(0).getLabels().get(2),
                "fault"
        );
    }
}
