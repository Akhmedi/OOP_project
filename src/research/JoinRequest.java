package research;

import enums.ResRequestStatus;
import java.io.Serializable;
import java.util.Date;

public class JoinRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private ResRequestStatus status;
    private Date createdDate;
    private Researcher researcher;
    private ResearchProject project;

    public JoinRequest(Researcher researcher, ResearchProject project) {
        this.researcher = researcher;
        this.project = project;
        this.status = ResRequestStatus.PENDING;
        this.createdDate = new Date();
    }

    public void setStatus(ResRequestStatus s) { this.status = s; }
    public Researcher getResearcher()         { return researcher; }

    @Override
    public String toString() {
        return "JoinRequest{researcher=" + researcher.getResearcherId()
                + ", project=" + project.getName() + ", status=" + status + "}";
    }
}
