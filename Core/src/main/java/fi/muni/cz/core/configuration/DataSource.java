package fi.muni.cz.core.configuration;

/**
 * Class that represents a data source.
 * This could be a Github URL, path to a JIRA CSV file etc.
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 * */

public class DataSource {
    private String type;
    private String location;

    public String getType() {
        return type;
    }

    /**
     * @return data source location (url or file path)
     */
    public String getLocation() {
        return location;
    }
}
