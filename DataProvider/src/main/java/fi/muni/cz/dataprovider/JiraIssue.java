package fi.muni.cz.dataprovider;

import com.opencsv.bean.CsvBindByName;
import java.util.Date;

/** @author Valtteri Valtonen, valtonenvaltteri@gmail.com */
public class JiraIssue {

  @CsvBindByName(column = "Summary")
  private String summary;

  @CsvBindByName(column = "IssueType")
  private String issueType;

  @CsvBindByName(column = "Status")
  private String status;

  @CsvBindByName(column = "Priority")
  private String priority;

  @CsvBindByName(column = "Created")
  private Date created;

  @CsvBindByName(column = "Updated")
  private Date updated;

  @CsvBindByName(column = "Last Viewed")
  private Date lastViewed;

  @CsvBindByName(column = "Description")
  private String description;

  @CsvBindByName(column = "Resolved")
  private String resolved;

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getIssueType() {
    return issueType;
  }

  public void setIssueType(String issueType) {
    this.issueType = issueType;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getPriority() {
    return priority;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  public Date getLastViewed() {
    return lastViewed;
  }

  public void setLastViewed(Date lastViewed) {
    this.lastViewed = lastViewed;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getResolved() {
    return resolved;
  }

  public void setResolved(String resolved) {
    this.resolved = resolved;
  }
}
