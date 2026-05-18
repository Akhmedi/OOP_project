package research;

import patterns.DataStorage;
import users.User;
import enums.ResearcherType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Researcher implements Serializable {

    private static final long serialVersionUID = 1L;

    private String researcherId;
    private User owner;
    private ResearcherType type;
    private List<ResearchProject> projects;
    private List<ResearchPaper> papers;

    public Researcher(String researcherId, User owner, ResearcherType type) {
        this.researcherId = researcherId;
        this.owner = owner;
        this.type = type;
        this.projects = new ArrayList<>();
        this.papers = new ArrayList<>();
    }

    public void publishPaper(ResearchPaper paper) {
        papers.add(paper);
        System.out.println(researcherId + " published: " + paper.getTitle());
    }

    public void requestToJoin(ResearchProject project) {
        project.requestToJoin(this);
    }

    // Prints papers sorted by given comparator
    public void printPapers(Comparator<ResearchPaper> comparator) {
        List<ResearchPaper> sorted = new ArrayList<>(papers);
        sorted.sort(comparator);
        System.out.println("=== Papers of " + owner.getFullName() + " ===");
        for (ResearchPaper p : sorted) {
            System.out.println(p.getAbstract());
        }
    }

    // Static comparators
    public static Comparator<ResearchPaper> byCitations() {
        return (a, b) -> Integer.compare(b.getCitations(), a.getCitations());
    }

    public static Comparator<ResearchPaper> byDate() {
        return (a, b) -> b.getPublicationDate().compareTo(a.getPublicationDate());
    }

    public static Comparator<ResearchPaper> byPages() {
        return (a, b) -> Integer.compare(b.getPages(), a.getPages());
    }

    public int getHIndex() {
        return computeHIndex();
    }

    public int computeHIndex() {
        int h = 0;
        List<Integer> citationCounts = new ArrayList<>();
        for (ResearchPaper p : papers) {
            citationCounts.add(p.getCitations());
        }
        citationCounts.sort(Comparator.reverseOrder());
        for (int i = 0; i < citationCounts.size(); i++) {
            if (citationCounts.get(i) >= i + 1) {
                h = i + 1;
            } else {
                break;
            }
        }
        return h;
    }

    public static void viewUniversityResearchStatistics(Scanner scanner, List<Researcher> researchers,
                                                        DataStorage dataStorage) {
        System.out.println("\n=== UNIVERSITY RESEARCH STATISTICS ===");
        System.out.println("1. View all papers (Sorted by Citations)");
        System.out.println("2. View Top Cited Researcher (University-wide)");
        System.out.println("3. View Top Cited Researcher by School/Major");
        System.out.println("4. View Top Cited Researcher by Year");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            System.out.println("Sort: 1=Citations 2=Date 3=Pages");
            Comparator<ResearchPaper> comparator = resolveComparator(scanner.nextLine().trim());
            List<ResearchPaper> allPapers = dataStorage.getAllResearchPapers(researchers);
            allPapers.sort(comparator);
            for (ResearchPaper paper : allPapers) {
                System.out.println(paper.getAbstract());
            }
        } else if (choice.equals("2")) {
            printTopResearcher(dataStorage.getTopCitedResearcher(researchers), "University-wide");
        } else if (choice.equals("3")) {
            System.out.print("School/Major or Department name (e.g. CS): ");
            String school = scanner.nextLine().trim();
            printTopResearcher(dataStorage.getTopCitedResearcher(researchers, school, 0),
                    "School/Major: " + school);
        } else if (choice.equals("4")) {
            System.out.print("Publication year: ");
            try {
                int year = Integer.parseInt(scanner.nextLine().trim());
                printTopResearcher(dataStorage.getTopCitedResearcher(researchers, null, year),
                        "Year: " + year);
            } catch (NumberFormatException e) {
                System.out.println("Invalid year.");
            }
        }
    }

    private static Comparator<ResearchPaper> resolveComparator(String choice) {
        if ("2".equals(choice)) return byDate();
        if ("3".equals(choice)) return byPages();
        return byCitations();
    }

    public static void printTopResearcher(Researcher top, String scope) {
        if (top != null) {
            System.out.println("Top Researcher (" + scope + "): " + top.getOwner().getFullName()
                    + " | H-Index: " + top.getHIndex());
        } else {
            System.out.println("No researchers found for " + scope + ".");
        }
    }

    @Override
    public String toString() {
        return "Researcher{id=" + researcherId + ", owner=" + owner.getFullName()
                + ", hIndex=" + getHIndex() + ", papers=" + papers.size() + "}";
    }

    public String getResearcherId()          { return researcherId; }
    public User getOwner()                   { return owner; }
    public List<ResearchProject> getProjects(){ return projects; }
    public List<ResearchPaper> getPapers()   { return papers; }
}
