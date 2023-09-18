package fi.muni.cz.dataprovider;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import java.util.Date;

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 * */
public class BugzillaIssue {
    @CsvBindByName(column = "Bug ID")
    private String bugId;
    @CsvBindByName(column = "Product")
    private String product;
    @CsvBindByName(column = "Component")
    private String component;
    @CsvBindByName(column = "Assignee")
    private String assignee;
    @CsvBindByName(column = "Status")
    private String status;
    @CsvBindByName(column = "Resolution")
    private String resolution;
    @CsvBindByName(column = "Summary")
    private String summary;
    @CsvDate(value = "yyyy-MM-d HH:mm:ss")
    @CsvBindByName(column = "Changed")
    private Date changed;
    @CsvBindByName(column = "Alias")
    private String alias;
    @CsvBindByName(column = "Assignee Real name")
    private String assigneeRealName;
    @CsvBindByName(column = "Blocks")
    private String blocks;
    @CsvBindByName(column = "Classification")
    private String classification;
    @CsvBindByName(column = "Deadline")
    private String deadline;
    @CsvBindByName(column = "Depends on")
    private String dependsOn;
    @CsvBindByName(column = "Flags")
    private String flags;
    @CsvBindByName(column = "Hardware")
    private String hardware;
    @CsvBindByName(column = "Keywords")
    private String keywords;
    @CsvDate(value = "yyyy-MM-d HH:mm:ss")
    @CsvBindByName(column = "Last Visit")
    private Date lastVisit;
    @CsvBindByName(column = "Number of Comments")
    private int numberOfComments;
    @CsvDate(value = "yyyy-MM-d HH:mm:ss")
    @CsvBindByName(column = "Opened")
    private Date opened;
    @CsvBindByName(column = "OS")
    private String os;
    @CsvBindByName(column = "Personal Tags")
    private String personalTags;
    @CsvBindByName(column = "Priority")
    private String priority;
    @CsvBindByName(column = "QA Contact")
    private String qaContact;
    @CsvBindByName(column = "QA Contact Real Name")
    private String qaContactRealName;
    @CsvBindByName(column = "Reporter")
    private String reporter;
    @CsvBindByName(column = "Reporter Real Name")
    private String reporterRealName;
    @CsvBindByName(column = "Severity")
    private String severity;
    @CsvBindByName(column = "Target Milestone")
    private String targetMilestone;
    @CsvBindByName(column = "URL")
    private String url;
    @CsvBindByName(column = "Version")
    private String version;
    @CsvBindByName(column = "Votes")
    private int votes;
    @CsvBindByName(column = "Whiteboard")
    private String whiteboard;


    public String getBugId() {
        return bugId;
    }

    public void setBugId(String bugId) {
        this.bugId = bugId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getChanged() {
        return changed;
    }

    public void setChanged(Date changed) {
        this.changed = changed;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAssigneeRealName() {
        return assigneeRealName;
    }

    public void setAssigneeRealName(String assigneeRealName) {
        this.assigneeRealName = assigneeRealName;
    }

    public String getBlocks() {
        return blocks;
    }

    public void setBlocks(String blocks) {
        this.blocks = blocks;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(String dependsOn) {
        this.dependsOn = dependsOn;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public String getHardware() {
        return hardware;
    }

    public void setHardware(String hardware) {
        this.hardware = hardware;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Date getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(Date lastVisit) {
        this.lastVisit = lastVisit;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public Date getOpened() {
        return opened;
    }

    public void setOpened(Date opened) {
        this.opened = opened;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getPersonalTags() {
        return personalTags;
    }

    public void setPersonalTags(String personalTags) {
        this.personalTags = personalTags;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getQaContact() {
        return qaContact;
    }

    public void setQaContact(String qaContact) {
        this.qaContact = qaContact;
    }

    public String getQaContactRealName() {
        return qaContactRealName;
    }

    public void setQaContactRealName(String qaContactRealName) {
        this.qaContactRealName = qaContactRealName;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getReporterRealName() {
        return reporterRealName;
    }

    public void setReporterRealName(String reporterRealName) {
        this.reporterRealName = reporterRealName;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getTargetMilestone() {
        return targetMilestone;
    }

    public void setTargetMilestone(String targetMilestone) {
        this.targetMilestone = targetMilestone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getWhiteboard() {
        return whiteboard;
    }

    public void setWhiteboard(String whiteboard) {
        this.whiteboard = whiteboard;
    }
}
