package fi.muni.cz.dataprovider;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import fi.muni.cz.dataprovider.exception.DataProviderException;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class GitHubDataProviderTest {
    
    @Mock
    IssueService issueService;

    @InjectMocks
    private GitHubGeneralIssueDataProvider provider = new GitHubGeneralIssueDataProvider(new GitHubClient());
    
    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testGetIssuesByUrl() throws IOException {
        Map<String, String> filter = new HashMap<>();
        filter.put(IssueService.FILTER_STATE, IssueService.STATE_CLOSED);
        when(issueService.getIssues("user", "repository", filter)).thenReturn(Arrays.asList(new Issue().setCreatedAt(new Date())));
        filter = new HashMap<>();
        filter.put(IssueService.FILTER_STATE, IssueService.STATE_OPEN);
        when(issueService.getIssues("user", "repository", filter)).thenReturn(Arrays.asList(new Issue().setCreatedAt(new Date())));
        assertEquals(2, provider.getIssuesByUrl("https://github.com/user/repository").size());
    }
    
    @Test(expectedExceptions = DataProviderException.class)
    public void testIncorrectUrl() {
        provider.getIssuesByUrl("notUrl");
    }   

    @Test(expectedExceptions = DataProviderException.class)
    public void testNotCompleteUrl() {
        provider.getIssuesByUrl("https://github.com/user/");
    } 
}
