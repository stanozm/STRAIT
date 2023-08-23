package fi.muni.cz.dataprocessing.issuesprocessing;
import fi.muni.cz.dataprovider.GeneralIssue;
import java.util.List;

/**
 * Represents different data processing actions
 * {@link fi.muni.cz.reliability.tool.dataprovider.GeneralIssue}
 *
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */

public interface IssueProcessingAction {

    /**
     * Apply data processing action to provided list of general issues
     *
     * @param list that data processing action will be applied to
     * @return List of general issues for which the data processing action has been applied to
     */
    List<GeneralIssue> apply(List<GeneralIssue> list);

    /**
     * Information about this data processing action.
     *
     * @return String info
     */
    String infoAboutIssueProcessingAction();

    /**
     * Information about the result of applying this data processing action.
     *
     * @return String info
     */
    String infoAboutApplicationResult();
}