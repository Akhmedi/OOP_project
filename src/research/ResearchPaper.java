package research;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ResearchPaper implements Serializable, Comparable<ResearchPaper> {

    private static final long serialVersionUID = 1L;

    private String paperId;
    private String title;
    private List<String> authors;
    private Date publicationDate;
    private String journalName;
    private int citations;
    private int pages;
    private String doi;
    private String keywords;

    public ResearchPaper(String paperId, String title, List<String> authors,
                         Date publicationDate, String journalName,
                         int citations, int pages, String doi, String keywords) {
        this.paperId = paperId;
        this.title = title;
        this.authors = authors;
        this.publicationDate = publicationDate;
        this.journalName = journalName;
        this.citations = citations;
        this.pages = pages;
        this.doi = doi;
        this.keywords = keywords;
    }

    public String getAbstract() {
        return "Paper: " + title + " | Journal: " + journalName
                + " | Authors: " + authors
                + " | Citations: " + citations + " | Pages: " + pages
                + " | DOI: " + doi + " | Date: " + publicationDate;
    }

    @Override
    public int compareTo(ResearchPaper other) {
        return Integer.compare(other.citations, this.citations); // default: sort by citations desc
    }

    @Override
    public String toString() {
        return "ResearchPaper{'" + title + "', citations=" + citations + ", pages=" + pages + "}";
    }

    public String getPaperId()                   { return paperId; }
    public String getTitle()                     { return title; }
    public List<String> getAuthors()             { return authors; }
    public Date getPublicationDate()             { return publicationDate; }
    public String getJournalName()               { return journalName; }
    public int getCitations()                    { return citations; }
    public int getPages()                        { return pages; }
    public String getDoi()                       { return doi; }
    public String getKeywords()                  { return keywords; }
    public void addCitation()                    { this.citations++; }
}
