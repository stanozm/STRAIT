package fi.muni.cz.dataprocessing.issuesprocessing;

import static org.testng.Assert.assertEquals;

import fi.muni.cz.dataprovider.GeneralIssue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class FilterClosedNGTest {
    
    private final Filter filter = new FilterClosed();
   
    private final List<GeneralIssue> listOfIssues = new ArrayList<>();
    private final List<GeneralIssue> listOfIssuesNoClosed = new ArrayList<>();
    
    @BeforeClass
    public void setUp() {
        GeneralIssue issue = new GeneralIssue();
        issue.setState("closed");
        listOfIssues.add(issue);
        
        issue = new GeneralIssue();
        issue.setState("opened");
        listOfIssues.add(issue);
        listOfIssuesNoClosed.add(issue);
    }
    
    @Test
    public void testFilterClosed() {
        assertEquals(filter.apply(listOfIssues, null).size(), 1);
        assertEquals(filter.apply(listOfIssuesNoClosed, null).size(), 0);
    }
}