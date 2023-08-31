package fi.muni.cz.dataprovider.utils;

/**
 * Represents parsed URL.
 * 
 * @author Radoslav Micko, 445611@muni.cz
 */
public class ParsedUrlData {
    
    private String url;
    private String userName;
    private String repositoryName;

    /**
     * Initialize all private attributes.
     * 
     * @param url URL
     * @param userName user name
     * @param repositoryName repository name
     */
    public ParsedUrlData(String url, String userName, String repositoryName) {
        this.url = url;
        this.userName = userName;
        this.repositoryName = repositoryName;
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    @Override
    public String toString() {
        return "ParsedUrlData{" + "url=" + url + ", userName="
                + userName + ", repositoryName=" + repositoryName + '}';
    }
}
