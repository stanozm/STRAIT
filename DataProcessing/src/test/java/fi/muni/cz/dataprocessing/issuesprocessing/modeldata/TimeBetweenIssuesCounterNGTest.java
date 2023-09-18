package fi.muni.cz.dataprocessing.issuesprocessing.modeldata;

import static org.testng.Assert.assertEquals;

import fi.muni.cz.dataprovider.GeneralIssue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class TimeBetweenIssuesCounterNGTest {

    private final List<GeneralIssue> listOfIssues = new ArrayList<>();
    private Date firstDate;
    private Date secondDate;
    
    @BeforeClass
    public void setUp() {
        GeneralIssue issue = new GeneralIssue();
        Calendar cal = Calendar.getInstance();
        cal.set(2018, 10, 1, 0, 0, 0);
        firstDate = cal.getTime();
        issue.setCreatedAt(firstDate);
        listOfIssues.add(issue);
        
        issue = new GeneralIssue();
        cal.set(2018, 10, 2, 0, 0, 0);
        secondDate = cal.getTime();
        issue.setCreatedAt(secondDate);
        listOfIssues.add(issue);
    }
    
    @Test
    public void testTimeBetweenIssuesCounter() {
        IssuesCounter counter = new TimeBetweenIssuesCounter(IssuesCounter.SECONDS);
        assertEquals(
                counter.countIssues(listOfIssues, firstDate, secondDate).get(0).getSecond().longValue(), 0
        );
        assertEquals(counter.countIssues(listOfIssues, firstDate, secondDate).get(1).getSecond().longValue(),
                (secondDate.getTime() - firstDate.getTime()) / 1000);
        
        counter = new TimeBetweenIssuesCounter(IssuesCounter.MINUTES);
        assertEquals(
                counter.countIssues(listOfIssues, firstDate, secondDate).get(0).getSecond().longValue(),
                0);
        assertEquals(counter.countIssues(listOfIssues, firstDate, secondDate).get(1).getSecond().longValue(),
                (secondDate.getTime() - firstDate.getTime()) / 1000 / 60);
        
        counter = new TimeBetweenIssuesCounter(IssuesCounter.HOURS);
        assertEquals(counter.countIssues(
                listOfIssues, firstDate, secondDate).get(0).getSecond().longValue(),
                0);
        assertEquals(counter.countIssues(listOfIssues, firstDate, secondDate).get(1).getSecond().longValue(),
                (secondDate.getTime() - firstDate.getTime()) / 1000 / 60 / 60);
        
        counter = new TimeBetweenIssuesCounter(IssuesCounter.DAYS);
        assertEquals(
                counter.countIssues(listOfIssues, firstDate, secondDate).get(0).getSecond().longValue(),
                0);
        assertEquals(counter.countIssues(listOfIssues, firstDate, secondDate).get(1).getSecond().longValue(),
                (secondDate.getTime() - firstDate.getTime()) / 1000 / 60 / 60 / 24);
        
        counter = new TimeBetweenIssuesCounter(IssuesCounter.WEEKS);
        assertEquals(
                counter.countIssues(listOfIssues, firstDate, secondDate).get(0).getSecond().longValue(),
                0);
        assertEquals(counter.countIssues(listOfIssues, firstDate, secondDate).get(1).getSecond().longValue(),
                (secondDate.getTime() - firstDate.getTime()) / 1000 / 60 / 60 / 24 / 7);
    } 
}