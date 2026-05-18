package patterns;

import enums.ResearcherType;
import research.Researcher;
import users.Employee;
import users.Student;
import users.Teacher;

// Factory Pattern - creates Researcher profiles for different user types
public final class ResearcherFactory {

    private ResearcherFactory() {}

    public static Researcher createForStudent(Student student) {
        return new Researcher(student.getStudentId() + "_res", student, ResearcherType.STUDENT);
    }

    public static Researcher createForEmployee(Employee employee) {
        ResearcherType type = employee instanceof Teacher
                ? ResearcherType.TEACHER
                : ResearcherType.ASSISTANT;
        return new Researcher(employee.getEmployeeId() + "_res", employee, type);
    }
}
