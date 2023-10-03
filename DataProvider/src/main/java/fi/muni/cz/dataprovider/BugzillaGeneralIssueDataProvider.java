package fi.muni.cz.dataprovider;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 * */
public class BugzillaGeneralIssueDataProvider implements GeneralIssueDataProvider{

    @Override
    public List<GeneralIssue> getIssuesByUrl(String url) {
        try {
            System.out.println("Reading issues from file " + url + " ...");
            return readBugzillaIssuesFromCsvFile(url)
                    .stream()
                    .map(GeneralIssue::fromBugzillaIssue)
                    .sorted(Comparator.comparing(GeneralIssue::getCreatedAt))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log(Level.SEVERE, "Error while reading Bugzilla CSV file", e);
        }
        return new ArrayList<>();
    }

    private List<BugzillaIssue> readBugzillaIssuesFromCsvFile(String filePath)
            throws IOException, CsvValidationException {

        return new CsvToBeanBuilder<BugzillaIssue>(new FileReader(filePath))
                .withType(BugzillaIssue.class)
                .withSeparator(',')
                .build()
                .parse();
    }

    private void log(Level level, String message, Exception ex) {
        Logger.getLogger(JiraGeneralIssueDataProvider.class.getName())
                .log(level, message, ex);
    }


}