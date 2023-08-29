package fi.muni.cz.core.dto;

import fi.muni.cz.dataprovider.GeneralIssue;
import fi.muni.cz.dataprovider.RepositoryInformation;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * @author Valtteri Valtonen, valtonenvaltteri@gmail.com
 */
@Entity
@Table(name = "ISSUE_REPORT_SET")
public class IssueReportSet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    private RepositoryInformation repositoryInformation;
    private List<GeneralIssue> issueReports;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GeneralIssue> getIssueReports() {
        return issueReports;
    }

    public void setIssueReports(List<GeneralIssue> issueReports) {
        this.issueReports = issueReports;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RepositoryInformation getRepositoryInformation() {
        return repositoryInformation;
    }

    public void setRepositoryInformation(RepositoryInformation repositoryInformation) {
        this.repositoryInformation = repositoryInformation;
    }
}
