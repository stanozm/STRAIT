package fi.muni.cz.core.factory;

import fi.muni.cz.core.ArgsParser;
import fi.muni.cz.dataprocessing.exception.DataProcessingException;
import fi.muni.cz.dataprocessing.issuesprocessing.*;

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
            List<Date> parsingResult = parseDateOption(parser);
            
            listOfFilters.add(new FilterByTime(parsingResult.get(0), parsingResult.get(1)));
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

        if (parser.hasOptionFilterBeforeFirstRelease()){
            listOfFilters.add(new FilterOutIssuesBeforeFirstRelease());
        }
        
        return listOfFilters;
    }

    /**
     * Parse date option from configuration object
     * @param configuration The configuration object
     * @return List of dates where the first date is the date period start date and the second date is the end date.
     */
    public static List<Date> parseDateOption(ArgsParser configuration) {
        DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        List<Date> parsedDates = new ArrayList<>();
        try {
            parsedDates.add(formatter.parse(configuration.getOptionValuesFilterTime()[0]));
            parsedDates.add(formatter.parse(configuration.getOptionValuesFilterTime()[1]));
            return parsedDates;
        } catch (ParseException ex) {
            throw new DataProcessingException("Wrong format of date. Should match: " + DATE_FORMAT);
        }
    }



}
