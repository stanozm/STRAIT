package fi.muni.cz.dataprocessing.issuesprocessing;

import static org.testng.Assert.assertEquals;

import fi.muni.cz.dataprovider.GeneralIssue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class FilterByTimeNGTest {
    
    private final Calendar cal = new GregorianCalendar();
    private final List<GeneralIssue> listOfIssues = new ArrayList<>();
    
    @BeforeClass
    public void setUp() {
        GeneralIssue issue = new GeneralIssue();
        
        cal.set(2018, 10, 1, 0, 0, 0);
        Date date = cal.getTime();
        issue.setCreatedAt(date);
        listOfIssues.add(issue);
        
        issue = new GeneralIssue();
        cal.set(2018, 10, 2, 0, 0, 0);
        date = cal.getTime();
        issue.setCreatedAt(date);
        listOfIssues.add(issue);
        
        issue = new GeneralIssue();
        cal.set(2018, 10, 3, 0, 0, 0);
        date = cal.getTime();
        issue.setCreatedAt(date);
        listOfIssues.add(issue);
        
        issue = new GeneralIssue();
        cal.set(2018, 10, 4, 0, 0, 0);
        date = cal.getTime();
        issue.setCreatedAt(date);
        listOfIssues.add(issue);
    }
    
    @Test
    public void testFilterClosed() {
        cal.set(2018, 9, 1, 0, 0, 0);
        Date start = cal.getTime();
        cal.set(2018, 10, 2, 0, 0, 0);
        Date end = cal.getTime();
        
        Filter filter = new FilterByTime(start, end);
        assertEquals(filter.apply(listOfIssues, ).size(), 1);
        
        cal.set(2018, 10, 8, 0, 0, 0);
        end = cal.getTime();
        filter = new FilterByTime(start, end);
        assertEquals(filter.apply(listOfIssues, ).size(), 4);
        
        cal.set(2018, 10, 2, 0, 0, 0);
        start = cal.getTime();
        filter = new FilterByTime(start, end);
        assertEquals(filter.apply(listOfIssues, ).size(), 2);
    }
}