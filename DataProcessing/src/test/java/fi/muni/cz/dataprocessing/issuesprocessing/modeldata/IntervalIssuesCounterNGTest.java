package fi.muni.cz.dataprocessing.issuesprocessing.modeldata;

import static org.testng.Assert.assertEquals;

import fi.muni.cz.dataprovider.GeneralIssue;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.math3.util.Pair;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** @author Radoslav Micko, 445611@muni.cz */
public class IntervalIssuesCounterNGTest {

  private final List<GeneralIssue> listOfIssues = new ArrayList<>();

  private IssuesCounter counter = new IntervalIssuesCounter();

  @BeforeClass
  public void setUp() {
    GeneralIssue issue = new GeneralIssue();

    Date date = getDate(2018, 10, 1);
    issue.setCreatedAt(date);
    listOfIssues.add(issue);

    issue = new GeneralIssue();
    date = getDate(2018, 10, 2);
    issue.setCreatedAt(date);
    listOfIssues.add(issue);

    issue = new GeneralIssue();
    date = getDate(2018, 10, 3);
    issue.setCreatedAt(date);
    listOfIssues.add(issue);

    issue = new GeneralIssue();

    date = getDate(2018, 10, 4);
    issue.setCreatedAt(date);
    listOfIssues.add(issue);
    issue = new GeneralIssue();

    date = getDate(2018, 10, 13);
    issue.setCreatedAt(date);
    listOfIssues.add(issue);
  }

  @Test
  public void testIntervalIssuesCounter() {

    List<Pair<Integer, Integer>> countedIssues =
        counter.countIssues(listOfIssues, getDate(2018, 10, 1), getDate(2018, 10, 16));

    assertEquals(countedIssues.get(0).getSecond(), new Integer(4));
    assertEquals(countedIssues.get(1).getSecond(), new Integer(1));
  }

  private Date getDate(int year, int month, int date) {
    Calendar cal = Calendar.getInstance();
    cal.set(year, month, date, 0, 0, 0);
    return cal.getTime();
  }
}
