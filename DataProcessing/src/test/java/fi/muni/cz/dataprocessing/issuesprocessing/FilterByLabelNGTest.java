package fi.muni.cz.dataprocessing.issuesprocessing;

import static org.testng.Assert.assertEquals;

import fi.muni.cz.dataprocessing.exception.DataProcessingException;
import fi.muni.cz.dataprovider.GeneralIssue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class FilterByLabelNGTest {
    
    private final Filter filterWithNoWords = new FilterByLabel(new ArrayList<>(), false);
    private final Filter filterForWordError = new FilterByLabel(Arrays.asList("error"), false);
    private final Filter filterForSomeWord = new FilterByLabel(Arrays.asList("some"), false);
    
    private final List<GeneralIssue> listOfIssues = new ArrayList<>();
    
    @BeforeClass
    public void setUp() {
        GeneralIssue issue = new GeneralIssue();
        issue.setLabels(Arrays.asList("bug", "error", "fault"));
        listOfIssues.add(issue);
        
        issue = new GeneralIssue();
        issue.setLabels(Arrays.asList("otherLabel"));
        listOfIssues.add(issue);
        
        issue = new GeneralIssue();
        issue.setLabels(new ArrayList<>());
        listOfIssues.add(issue);
    }
    
    @Test(expectedExceptions = DataProcessingException.class)
    public void testFilterWithNoWords() {
        filterWithNoWords.apply(listOfIssues, );
    }
    
    @Test
    public void testFilterWithWords() {
        assertEquals(filterForWordError.apply(listOfIssues, ).size(), 1);
        assertEquals(filterForSomeWord.apply(listOfIssues, ).size(), 0);
    }
}