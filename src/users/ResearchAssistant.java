package users;

import enums.ResearcherType;
import research.Researcher;
import utils.Department;
import java.io.Serializable;

public class ResearchAssistant extends Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    public ResearchAssistant(int id, String firstName, String lastName, String email,
                             String password, String language,
                             String employeeId, double salary, Department department) {
        super(id, firstName, lastName, email, password, language, employeeId, salary, department);
        this.researcher = new Researcher(employeeId + "_res", this, ResearcherType.ASSISTANT);
    }

    @Override
    public void work() {
        System.out.println("Research Assistant " + employeeId + " is conducting research...");
    }
}
