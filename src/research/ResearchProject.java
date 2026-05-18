package research;

import exceptions.LowHIndexException;
import enums.ResRequestStatus;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResearchProject implements Serializable {

    private static final long serialVersionUID = 1L;

    private String projectId;
    private String topic;
    private String name;
    private Researcher supervisor;
    private List<Researcher> members;
    private List<JoinRequest> pendingRequests;
    private List<ResearchPaper> publishedPapers;

    public ResearchProject(String projectId, String topic, String name, Researcher supervisor) throws LowHIndexException {
        if (supervisor.getHIndex() < 3) {
            throw new LowHIndexException("Supervisor H-index must be at least 3. Provided: " + supervisor.getHIndex());
        }
        this.projectId = projectId;
        this.topic = topic;
        this.name = name;
        this.supervisor = supervisor;
        this.members = new ArrayList<>();
        this.pendingRequests = new ArrayList<>();
        this.publishedPapers = new ArrayList<>();
        addParticipant(supervisor);
    }

    public void approveRequest(JoinRequest req, Researcher researcher) {
        req.setStatus(ResRequestStatus.ACCEPTED);
        addParticipant(researcher);
        pendingRequests.remove(req);
        System.out.println("Request approved: " + researcher.getResearcherId() + " joined " + name);
    }

    public void addParticipant(Researcher researcher) {
        if (!members.contains(researcher)) {
            members.add(researcher);
            researcher.getProjects().add(this);
        }
    }

    public void requestToJoin(Researcher researcher) {
        if (members.contains(researcher)) {
            System.out.println(researcher.getResearcherId() + " is already a participant of " + name);
            return;
        }
        for (JoinRequest request : pendingRequests) {
            if (request.getResearcher().equals(researcher)) {
                System.out.println("Join request already exists for: " + researcher.getResearcherId());
                return;
            }
        }
        JoinRequest request = new JoinRequest(researcher, this);
        pendingRequests.add(request);
        System.out.println("Join request created for: " + researcher.getResearcherId());
    }

    public void rejectRequest(JoinRequest req, Researcher researcher) {
        req.setStatus(ResRequestStatus.REJECTED);
        pendingRequests.remove(req);
        System.out.println("Request rejected: " + researcher.getResearcherId() + " denied for " + name);
    }

    public void publishPaper(ResearchPaper paper) {
        publishedPapers.add(paper);
        System.out.println("Paper published in project " + name + ": " + paper.getTitle());
    }

    public void viewPendingRequests() {
        System.out.println("=== Pending Requests for " + name + " ===");
        for (int i = 0; i < pendingRequests.size(); i++) {
            System.out.println(i + ". " + pendingRequests.get(i));
        }
    }

    @Override
    public String toString() {
        return "ResearchProject{'" + name + "', topic='" + topic
                + "', members=" + members.size() + "}";
    }

    public String getTopic()                         { return topic; }
    public String getName()                          { return name; }
    public List<JoinRequest> getPendingRequests()    { return pendingRequests; }
}
