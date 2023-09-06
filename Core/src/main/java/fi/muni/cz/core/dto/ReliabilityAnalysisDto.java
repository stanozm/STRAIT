package fi.muni.cz.core.dto;

import fi.muni.cz.core.ArgsParser;
import fi.muni.cz.dataprocessing.output.ModelResult;
import fi.muni.cz.dataprocessing.persistence.GeneralIssuesCollection;
import fi.muni.cz.dataprovider.ReleaseDTO;
import java.util.Date;
import java.util.List;

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
public class ReliabilityAnalysisDto {

    // Configuration

    private ArgsParser configuration;
    
    // Project metadata
    private String projectName;
    private String projectUrl;
    private String projectUser;
    private String projectDescription;
    private int projectSize;
    private int projectContributors;
    private int projectWatchers;
    private int projectForks;
    private Date projectLastPushedAt;
    private Date projectFirstPushedAt;
    private long projectDevelopmentDays;
    private List<ReleaseDTO> releases;

    // Analysis metadata
    private String solver;

    // Analysis issue data
    private int issueReportAmountBeforeProcessing;
    private int issueReportAmountAfterProcessing;
    private List<GeneralIssuesCollection> issueReportSets;

    // Model input data points
    private List<DataPointCollection> cumulativeIssueReportCollections;
    private List<DataPointCollection> timeBetweenDefectsCollections;
    private String timeBetweenDefectsUnit;

    // Model analysis results
    private List<List<ModelResult>> modelResults;

    // Analysis step results
    private List<ReliabilityAnalysisStepResult> analysisStepResults;


    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getProjectUser() {
        return projectUser;
    }

    public void setProjectUser(String projectUser) {
        this.projectUser = projectUser;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public int getProjectSize() {
        return projectSize;
    }

    public void setProjectSize(int projectSize) {
        this.projectSize = projectSize;
    }

    public int getProjectContributors() {
        return projectContributors;
    }

    public void setProjectContributors(int projectContributors) {
        this.projectContributors = projectContributors;
    }

    public int getProjectWatchers() {
        return projectWatchers;
    }

    public void setProjectWatchers(int projectWatchers) {
        this.projectWatchers = projectWatchers;
    }

    public int getProjectForks() {
        return projectForks;
    }

    public void setProjectForks(int projectForks) {
        this.projectForks = projectForks;
    }

    public Date getProjectLastPushedAt() {
        return projectLastPushedAt;
    }

    public void setProjectLastPushedAt(Date projectLastPushedAt) {
        this.projectLastPushedAt = projectLastPushedAt;
    }

    public Date getProjectFirstPushedAt() {
        return projectFirstPushedAt;
    }

    public void setProjectFirstPushedAt(Date projectFirstPushedAt) {
        this.projectFirstPushedAt = projectFirstPushedAt;
    }

    public long getProjectDevelopmentDays() {
        return projectDevelopmentDays;
    }

    public void setProjectDevelopmentDays(long projectDevelopmentDays) {
        this.projectDevelopmentDays = projectDevelopmentDays;
    }

    public List<ReleaseDTO> getReleases() {
        return releases;
    }

    public void setReleases(List<ReleaseDTO> releases) {
        this.releases = releases;
    }

    public String getSolver() {
        return solver;
    }

    public void setSolver(String solver) {
        this.solver = solver;
    }

    public int getIssueReportAmountBeforeProcessing() {
        return issueReportAmountBeforeProcessing;
    }

    public void setIssueReportAmountBeforeProcessing(int issueReportAmountBeforeProcessing) {
        this.issueReportAmountBeforeProcessing = issueReportAmountBeforeProcessing;
    }

    public int getIssueReportAmountAfterProcessing() {
        return issueReportAmountAfterProcessing;
    }

    public void setIssueReportAmountAfterProcessing(int issueReportAmountAfterProcessing) {
        this.issueReportAmountAfterProcessing = issueReportAmountAfterProcessing;
    }

    public List<GeneralIssuesCollection> getIssueReportSets() {
        return issueReportSets;
    }

    public void setIssueReportSets(List<GeneralIssuesCollection> issueReportSets) {
        this.issueReportSets = issueReportSets;
    }

    public List<DataPointCollection> getCumulativeIssueReportCollections() {
        return cumulativeIssueReportCollections;
    }

    public void setCumulativeIssueReportCollections(List<DataPointCollection> cumulativeIssueReportCollections) {
        this.cumulativeIssueReportCollections = cumulativeIssueReportCollections;
    }

    public List<DataPointCollection> getTimeBetweenDefectsCollections() {
        return timeBetweenDefectsCollections;
    }

    public void setTimeBetweenDefectsCollections(List<DataPointCollection> timeBetweenDefectsCollections) {
        this.timeBetweenDefectsCollections = timeBetweenDefectsCollections;
    }

    public String getTimeBetweenDefectsUnit() {
        return timeBetweenDefectsUnit;
    }

    public void setTimeBetweenDefectsUnit(String timeBetweenDefectsUnit) {
        this.timeBetweenDefectsUnit = timeBetweenDefectsUnit;
    }

    public List<List<ModelResult>> getModelResults() {
        return modelResults;
    }

    public void setModelResults(List<List<ModelResult>> modelResults) {
        this.modelResults = modelResults;
    }

    public List<ReliabilityAnalysisStepResult> getAnalysisStepResults() {
        return analysisStepResults;
    }

    public void setAnalysisStepResults(List<ReliabilityAnalysisStepResult> analysisStepResults) {
        this.analysisStepResults = analysisStepResults;
    }

    public ArgsParser getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ArgsParser configuration) {
        this.configuration = configuration;
    }
}
