
package patterns;

import utils.LogEntry;
import users.User;
import users.Student;
import users.Teacher;
import users.Manager;
import users.Admin;
import users.ResearchAssistant;
import academic.Course;
import academic.Schedule;
import utils.Department;
import utils.Room;
import utils.News;
import research.Researcher;
import research.ResearchPaper;
import exceptions.AuthenticationException;
import users.Employee;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// Singleton Pattern - only one DataStorage instance exists
public class DataStorage {

    private static DataStorage instance;
    private String filePath;
    private List<LogEntry> logs;

    private DataStorage() {
        this.filePath = "data/";
        this.logs = new ArrayList<>();
        new File(filePath).mkdirs();
    }

    public static DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    public void serialize(Object obj, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath + filename))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }

    public Object deserialize(String filename) {
        File file = new File(filePath + filename);
        if (!file.exists()) return null;
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading: " + e.getMessage());
            return null;
        }
    }

    public void saveSystemData(List<User> allUsers, List<Course> courses, 
                               List<Room> rooms, List<Department> departments, 
                               Schedule schedule, List<News> allNews,
                               List<research.ResearchProject> allProjects) {
        serialize(allUsers, "allUsers.ser");
        serialize(courses, "courses.ser");
        serialize(rooms, "rooms.ser");
        serialize(departments, "departments.ser");
        serialize(schedule, "schedule.ser");
        serialize(allNews, "allNews.ser");
        serialize(allProjects, "allProjects.ser");
    }

    public boolean loadSystemData(List<User> allUsers, List<Student> students,
                                  List<Teacher> teachers, List<Manager> managers,
                                  List<Admin> admins, List<Researcher> researchers,
                                  List<Course> courses, List<Room> rooms,
                                  List<Department> departments, List<News> allNews,
                                  List<research.ResearchProject> allProjects,
                                  DataWrapper wrapper) {
        try {
            List<User> loadedUsers = (List<User>) deserialize("allUsers.ser");
            if (loadedUsers == null) return false;

            allUsers.clear();
            allUsers.addAll(loadedUsers);

            List<Course> loadedCourses = (List<Course>) deserialize("courses.ser");
            if (loadedCourses != null) { courses.clear(); courses.addAll(loadedCourses); }

            List<Room> loadedRooms = (List<Room>) deserialize("rooms.ser");
            if (loadedRooms != null) { rooms.clear(); rooms.addAll(loadedRooms); }

            List<Department> loadedDepts = (List<Department>) deserialize("departments.ser");
            if (loadedDepts != null) { departments.clear(); departments.addAll(loadedDepts); }

            List<News> loadedNews = (List<News>) deserialize("allNews.ser");
            if (loadedNews != null) { allNews.clear(); allNews.addAll(loadedNews); }
            
            List<research.ResearchProject> loadedProjects = (List<research.ResearchProject>) deserialize("allProjects.ser");
            if (loadedProjects != null) { allProjects.clear(); allProjects.addAll(loadedProjects); }

            wrapper.schedule = (Schedule) deserialize("schedule.ser");

            // Re-populate specific lists
            students.clear();
            teachers.clear();
            managers.clear();
            admins.clear();
            researchers.clear();
            for (User u : allUsers) {
                if (u instanceof Student) {
                    Student s = (Student) u;
                    students.add(s);
                    if (s.getResearcher() != null && !researchers.contains(s.getResearcher())) {
                        researchers.add(s.getResearcher());
                    }
                } else if (u instanceof Teacher) {
                    Teacher t = (Teacher) u;
                    teachers.add(t);
                    if (t.getResearcher() != null && !researchers.contains(t.getResearcher())) {
                        researchers.add(t.getResearcher());
                    }
                } else if (u instanceof Manager) {
                    managers.add((Manager) u);
                } else if (u instanceof Admin) {
                    admins.add((Admin) u);
                } else if (u instanceof ResearchAssistant) {
                    ResearchAssistant ra = (ResearchAssistant) u;
                    if (ra.getResearcher() != null && !researchers.contains(ra.getResearcher())) {
                        researchers.add(ra.getResearcher());
                    }
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public User authenticate(List<User> allUsers, String email, String password) throws AuthenticationException {
        for (User u : allUsers) {
            if (u.login(email, password)) {
                return u;
            }
        }
        throw new AuthenticationException("Invalid email or password.");
    }

    // Helper to wrap Schedule since it's reassigned
    // Returns all papers from all researchers
    public List<research.ResearchPaper> getAllResearchPapers(List<Researcher> researchers) {
        List<research.ResearchPaper> allPapers = new ArrayList<>();
        for (Researcher r : researchers) {
            allPapers.addAll(r.getPapers());
        }
        return allPapers;
    }

    public Researcher getTopCitedResearcher(List<Researcher> researchers) {
        return getTopCitedResearcher(researchers, null, 0);
    }

    public Researcher getTopCitedResearcher(List<Researcher> researchers, String schoolMajor, int year) {
        Researcher top = null;
        int maxCitations = -1;
        for (Researcher r : researchers) {
            if (schoolMajor != null && !schoolMajor.isEmpty() && !matchesSchool(r, schoolMajor)) {
                continue;
            }
            int totalCitations = countCitationsForYear(r, year);
            if (totalCitations > maxCitations) {
                maxCitations = totalCitations;
                top = r;
            }
        }
        return top;
    }

    private int countCitationsForYear(Researcher researcher, int year) {
        int total = 0;
        for (ResearchPaper p : researcher.getPapers()) {
            if (year <= 0) {
                total += p.getCitations();
            } else {
                Calendar cal = Calendar.getInstance();
                cal.setTime(p.getPublicationDate());
                if (cal.get(Calendar.YEAR) == year) {
                    total += p.getCitations();
                }
            }
        }
        return total;
    }

    private boolean matchesSchool(Researcher researcher, String schoolMajor) {
        User owner = researcher.getOwner();
        if (owner instanceof Student) {
            return schoolMajor.equalsIgnoreCase(((Student) owner).getMajor());
        }
        if (owner instanceof Employee) {
            Employee emp = (Employee) owner;
            return emp.getDepartment() != null
                    && schoolMajor.equalsIgnoreCase(emp.getDepartment().getName());
        }
        return false;
    }

    public static class DataWrapper {
        public Schedule schedule;
    }

    public void writeLog(LogEntry entry) {
        logs.add(entry);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath + "logs.txt", true))) {
            writer.println(entry.toString());
        } catch (IOException e) {
            System.out.println("Error writing log: " + e.getMessage());
        }
    }

    public List<LogEntry> readLogs() {
        return new ArrayList<>(logs);
    }

}
