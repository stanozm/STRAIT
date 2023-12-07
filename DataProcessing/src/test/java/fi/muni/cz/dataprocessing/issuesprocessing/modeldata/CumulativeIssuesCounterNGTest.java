package fi.muni.cz.dataprocessing.issuesprocessing.modeldata;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import fi.muni.cz.dataprovider.GeneralIssue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.math3.util.Pair;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** @author Radoslav Micko, 445611@muni.cz */
public class CumulativeIssuesCounterNGTest {

  private final List<GeneralIssue> listOfIssues = new ArrayList<>();
  private final List<Pair<Integer, Integer>> listOfPairs = new ArrayList<>();

  @Mock private IssuesCounter counter;

  @InjectMocks private IssuesCounter cumulativeIssuesCounter = new CumulativeIssuesCounter();

  @BeforeClass
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    GeneralIssue issue = new GeneralIssue();
    listOfIssues.add(issue);

    issue = new GeneralIssue();
    listOfIssues.add(issue);

    Pair<Integer, Integer> pair = new Pair<>(1, 1);
    listOfPairs.add(pair);
    pair = new Pair<>(2, 1);
    listOfPairs.add(pair);
    pair = new Pair<>(3, 0);
    listOfPairs.add(pair);
    pair = new Pair<>(4, 0);
    listOfPairs.add(pair);
  }

  @Test
  public void testCumulativeIssuesCounter() {

    Date date = new Date();

    when(counter.countIssues(listOfIssues, date, date)).thenReturn(listOfPairs);
    assertEquals(
        cumulativeIssuesCounter.countIssues(listOfIssues, date, date).get(0).getSecond(),
        Integer.valueOf(1));
    assertEquals(
        cumulativeIssuesCounter.countIssues(listOfIssues, date, date).get(1).getSecond(),
        Integer.valueOf(2));
    assertEquals(
        cumulativeIssuesCounter.countIssues(listOfIssues, date, date).get(3).getSecond(),
        Integer.valueOf(2));
  }
}
