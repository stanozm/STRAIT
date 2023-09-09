package fi.muni.cz.core.analysis.phases.output.writers;

import static fi.muni.cz.dataprocessing.output.CsvUtil.CSV_FILE_SUFFIX;
import static fi.muni.cz.dataprocessing.output.CsvUtil.NEW_LINE_SEPARATOR;
import static fi.muni.cz.dataprocessing.output.CsvUtil.eliminateSeparatorAndCheckNullValue;
import static fi.muni.cz.dataprocessing.output.CsvUtil.writeElementWithDelimiter;

import fi.muni.cz.dataprocessing.exception.DataProcessingException;
import fi.muni.cz.dataprovider.GeneralIssue;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class CsvFileIssuesWriter implements IssuesWriter {
    private static final String FILE_HEADER = "Created at\tClosed at\tUpdated at\tState\tLabels"
            + "\tComments\tNumber\tTitle\tUrl\tHtml url\tBody\tUser name\tUser email\tMilestone created at"
            + "\tMilestone due on\tMilestone descrtiption\tMilestone state\tMilestone title";

    @Override
    public void writeToFile(List<GeneralIssue> list, String fileName) {
        File file = new File("./output/" + fileName + CSV_FILE_SUFFIX);
        try (FileWriter fileWriter = new FileWriter(file)){
                fileWriter.append(FILE_HEADER);
                fileWriter.append(NEW_LINE_SEPARATOR);
                for (GeneralIssue issue : list) {
                    writeElementWithDelimiter(
                            eliminateSeparatorAndCheckNullValue(issue.getCreatedAt()), fileWriter);
                    writeElementWithDelimiter(
                            eliminateSeparatorAndCheckNullValue(issue.getClosedAt()), fileWriter);
                    writeElementWithDelimiter(
                            eliminateSeparatorAndCheckNullValue(issue.getUpdatedAt()), fileWriter);
                    writeElementWithDelimiter(
                            eliminateSeparatorAndCheckNullValue(issue.getState()), fileWriter);
                    writeElementWithDelimiter(
                            eliminateSeparatorAndCheckNullValue(issue.getLabels()), fileWriter);
                    writeElementWithDelimiter(
                            eliminateSeparatorAndCheckNullValue(issue.getComments()), fileWriter);
                    writeElementWithDelimiter(
                            eliminateSeparatorAndCheckNullValue(issue.getNumber()), fileWriter);
                    writeElementWithDelimiter(
                            eliminateSeparatorAndCheckNullValue(issue.getTitle()), fileWriter);
                    writeElementWithDelimiter(
                            eliminateSeparatorAndCheckNullValue(issue.getUrl()), fileWriter);
                    writeElementWithDelimiter(
                            eliminateSeparatorAndCheckNullValue(issue.getHtmlUrl()), fileWriter);
                    writeElementWithDelimiter(
                            eliminateSeparatorAndCheckNullValue(issue.getBody()), fileWriter);
                    writeElementWithDelimiter(
                            eliminateSeparatorAndCheckNullValue(issue.getUserName()), fileWriter);
                    writeElementWithDelimiter(
                            eliminateSeparatorAndCheckNullValue(issue.getUserEmail()), fileWriter);
                    writeElementWithDelimiter(
                            eliminateSeparatorAndCheckNullValue(issue.getMilestoneCreatedAt()), fileWriter);
                    writeElementWithDelimiter(
                            eliminateSeparatorAndCheckNullValue(issue.getMilestoneDueOn()), fileWriter);
                    writeElementWithDelimiter(
                            eliminateSeparatorAndCheckNullValue(
                                    issue.getMilestoneDescription()), fileWriter);
                    writeElementWithDelimiter(
                            eliminateSeparatorAndCheckNullValue(issue.getMilestoneState()), fileWriter);
                    writeElementWithDelimiter(
                            eliminateSeparatorAndCheckNullValue(issue.getMilestoneTitle()), fileWriter);
                    fileWriter.append(NEW_LINE_SEPARATOR);
                }
        } catch (IOException ex) {
            throw new DataProcessingException("Error while creating csv file.", ex);
        }
    } 

}