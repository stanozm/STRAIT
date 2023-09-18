package fi.muni.cz.dataprovider;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.CSVReaderHeaderAwareBuilder;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 * */
public class JiraGeneralIssueDataProvider implements GeneralIssueDataProvider{

    // For example: 08/Jun/23 1:59 PM
    private static final String ACCEPTED_DATE_FORMAT = "dd/MMM/yy h:mm a";

    @Override
    public List<GeneralIssue> getIssuesByUrl(String url) {
        try {
            System.out.println("Reading issues from file " + url + " ...");
            return readJiraIssuesFromCsvFile(url)
                    .stream()
                    .map(GeneralIssue::fromJiraIssue)
                    .sorted(Comparator.comparing(GeneralIssue::getCreatedAt))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log(Level.SEVERE, "Error while reading Jira CSV file", e);
        }
        return new ArrayList<>();
    }

    private List<JiraIssue> readJiraIssuesFromCsvFile(String filePath) throws IOException, CsvValidationException {
        return readDataFromCsvFile(filePath).stream().map(this::mapRawDataToJiraIssue).collect(Collectors.toList());
    }

    private List<Map<String, String>> readDataFromCsvFile(String filePath) throws IOException, CsvValidationException {

        CSVReaderHeaderAware csvReader = new CSVReaderHeaderAwareBuilder(
                new FileReader(filePath))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(';')
                                .build())
                .build();

        List<Map<String, String>> rawData = new ArrayList<>();

        Map<String, String> nextRow;
        while((nextRow = csvReader.readMap()) != null){
            rawData.add(nextRow);
        }
        return rawData;
    }

    private JiraIssue mapRawDataToJiraIssue(Map<String, String> headerValueMap){
        JiraIssue jiraIssue = new JiraIssue();
        jiraIssue.setSummary(headerValueMap.getOrDefault("Summary", ""));
        jiraIssue.setStatus(headerValueMap.getOrDefault("Status", ""));
        jiraIssue.setCreated(parseDateFromString(headerValueMap.getOrDefault("Created", "")));
        jiraIssue.setUpdated(parseDateFromString(headerValueMap.getOrDefault("Updated", "")));
        jiraIssue.setLastViewed(parseDateFromString(headerValueMap.getOrDefault("Last Viewed", "")));
        jiraIssue.setIssueType(headerValueMap.getOrDefault("Issue Type", ""));
        jiraIssue.setDescription(headerValueMap.getOrDefault("Description", ""));
        jiraIssue.setPriority(headerValueMap.getOrDefault("Priority", ""));

        return jiraIssue;
    }

    private Date parseDateFromString(String string) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ACCEPTED_DATE_FORMAT).withLocale(Locale.ENGLISH);
        LocalDateTime dateTime = LocalDateTime.parse(string, formatter);
        return Date.from(dateTime.toInstant(ZoneOffset.UTC));
    }
    private void log(Level level, String message, Exception ex) {
        Logger.getLogger(JiraGeneralIssueDataProvider.class.getName())
                .log(level, message, ex);
    }
}