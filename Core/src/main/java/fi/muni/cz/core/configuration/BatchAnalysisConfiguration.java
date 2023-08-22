package fi.muni.cz.core.configuration;

import java.util.List;

/**
 * Class that represents a configuration for batch analysis.
 * Contains data source information that is used in analysis.
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 * */
public class BatchAnalysisConfiguration {

    private List<DataSource> dataSources;

    /**
     * @return data sources
     */
    public List<DataSource> getDataSources() {
        return dataSources;
    }

    /**
     * Set data sources
     * @param dataSources list of data sources
     * */
    public void setDataSources(List<DataSource> dataSources) {
        this.dataSources = dataSources;
    }
}
