package fi.muni.cz.core.factory;

import fi.muni.cz.core.ArgsParser;
import fi.muni.cz.dataprocessing.exception.DataProcessingException;
import fi.muni.cz.dataprocessing.issuesprocessing.FilterByLabel;
import fi.muni.cz.dataprocessing.issuesprocessing.FilterByTime;
import fi.muni.cz.dataprocessing.issuesprocessing.FilterClosed;
import fi.muni.cz.dataprocessing.issuesprocessing.FilterDefects;
import fi.muni.cz.dataprocessing.issuesprocessing.FilterDuplications;
import fi.muni.cz.dataprocessing.issuesprocessing.FilterLatestRelease;
import fi.muni.cz.dataprocessing.issuesprocessing.FilterOutInvalidissues;
import fi.muni.cz.dataprocessing.issuesprocessing.FilterOutIssuesWithLowCriticality;
import fi.muni.cz.dataprocessing.issuesprocessing.FilterOutIssuesWithoutFix;
import fi.muni.cz.dataprocessing.issuesprocessing.FilterOutTestRelatedIssues;
import fi.muni.cz.dataprocessing.issuesprocessing.IssueProcessingAction;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Radoslav Micko, 445611@muni.cz
 */
public class FilterFactory {
    
    private static final List<String> FILTERING_WORDS = Arrays.asList("bug","error","fail","fault","defect");
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    
    /**
     * Get all filters to run.
     * 
     * @param parser  parsed CommandLine.
     * @return list of Filters.
     */
    public static List<IssueProcessingAction> getFilters(ArgsParser parser) {
        List<IssueProcessingAction> listOfFilters = new ArrayList<>();
        
        if (parser.hasOptionFilterLabels()) {
            if (parser.getOptionValuesFilterLabels() == null) {
                listOfFilters.add(new FilterByLabel(FILTERING_WORDS, false));
            } else {
                listOfFilters.add(new FilterByLabel(
                        Arrays.asList(parser.getOptionValuesFilterLabels()), false));
            }
        }
        
        if (parser.hasOptionFilterClosed()) {
            listOfFilters.add(new FilterClosed());
        }
        
        if (parser.hasOptionFilterTime()) {
            DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
            Date startOfTesting = null;
            Date endOfTesting = null;
            try {
                startOfTesting = formatter.parse(parser.getOptionValuesFilterTime()[0]);
                endOfTesting = formatter.parse(parser.getOptionValuesFilterTime()[1]);
            } catch (ParseException ex) {
                throw new DataProcessingException("Wrong format of date. Should match: " + DATE_FORMAT);
            }
            
            listOfFilters.add(new FilterByTime(startOfTesting, endOfTesting));
        }

        if (parser.hasOptionFilterDuplications()) {
            listOfFilters.add(new FilterDuplications());
        }

        if (parser.hasOptionFilterDefects()) {
            listOfFilters.add(new FilterDefects());
        }

        if (parser.hasOptionFilterIssuesWithLowCriticality()) {
            listOfFilters.add(new FilterOutIssuesWithLowCriticality());
        }

        if (parser.hasOptionFilterInvalidIssues()) {
            listOfFilters.add(new FilterOutInvalidissues());
        }

        if (parser.hasOptionFilterTestRelatedissues()) {
            listOfFilters.add(new FilterOutTestRelatedIssues());
        }

        if (parser.hasOptionFilterIssuesWithoutFix()) {
            listOfFilters.add(new FilterOutIssuesWithoutFix());
        }

        if (parser.hasOptionFilterLatestRelease()){
            listOfFilters.add(new FilterLatestRelease());
        }
        
        return listOfFilters;
    }
}
