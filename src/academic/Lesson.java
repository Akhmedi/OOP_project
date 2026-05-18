package academic;

import enums.LessonType;
import users.Teacher;
import utils.Room;
import java.io.Serializable;

public class Lesson implements Serializable {

    private static final long serialVersionUID = 1L;

    public LessonType type;
    public Course course;
    public Teacher instructor;
    public Room room;
    public String dayOfWeek;
    public String timeSlot;
    public int durationMin;

    public Lesson(LessonType type, Course course, Teacher instructor,
                  Room room, String dayOfWeek, String timeSlot, int durationMin) {
        this.type = type;
        this.course = course;
        this.instructor = instructor;
        this.room = room;
        this.dayOfWeek = dayOfWeek;
        this.timeSlot = timeSlot;
        this.durationMin = durationMin;
    }

    @Override
    public String toString() {
        return "Lesson{" + type + ", course=" + course.getCourseName()
                + ", room=" + room.getName() + ", " + dayOfWeek + " " + timeSlot + "}";
    }
}
