package fi.muni.cz.dataprocessing.output;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class CsvUtil {

    public static final String CSV_FILE_SUFFIX = ".csv";
    public static final String COMMA_DELIMITER = ",";
    public static final String NEW_LINE_SEPARATOR = "\n";

    /**
     * Convert null objects to string form
     * @param obj an object
     * @return the string representation of the object or the string null if the  object is null
     * */
    public static String checkNullValueToString(Object obj) {
        return obj == null ? "null" : obj.toString();
    }

    /**
     * Write element to file using a delimiter
     * @param element a string
     * @param fileWriter file writer
     * @throws IOException io exception
     * */
    public static void writeElementWithDelimiter(String element, FileWriter fileWriter) throws IOException {
        fileWriter.append(element);
        fileWriter.append(COMMA_DELIMITER);
    }

    /**
     * Check object for null value and eliminate separators
     * @param obj an object
     * @return result of checkNullValueToString with separators eliminated
     * */
    public static String eliminateSeparatorAndCheckNullValue(Object obj) {
        return checkNullValueToString(obj)
                .replaceAll("\\t", " ")
                .replaceAll("\\n", " ")
                .replaceAll("\\v", " ");
    }



}
