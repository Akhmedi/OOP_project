package academic;

import enums.LessonType;
import enums.RoomType;
import users.Teacher;
import utils.Room;
import exceptions.RoomOccupiedException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Bonus: Schedule generation taking into account room load and room type
public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Room> rooms;
    private List<Lesson> lessons;

    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private static final String[] SLOTS = {"08:00", "10:00", "12:00", "14:00", "16:00"};

    public Schedule(List<Room> rooms) {
        this.rooms = rooms;
        this.lessons = new ArrayList<>();
    }

    // Auto-assign lesson to an available room matching type
    public Lesson scheduleLesson(LessonType type, Course course, Teacher teacher, int durationMin) throws RoomOccupiedException {
        RoomType needed = (type == LessonType.LECTURE) ? RoomType.LECTURE_HALL : RoomType.SEMINAR_ROOM;

        for (String day : DAYS) {
            for (String slot : SLOTS) {
                Room available = findRoom(needed, day, slot);
                if (available != null) {
                    Lesson lesson = new Lesson(type, course, teacher, available, day, slot, durationMin);
                    lessons.add(lesson);
                    // In a real system, we'd mark room-day-slot as occupied. 
                    // For this simplified logic, we just add it to the list.
                    System.out.println("Scheduled: " + lesson);
                    return lesson;
                }
            }
        }
        throw new RoomOccupiedException("No available " + needed + " found for " + course.getCourseName() + " in any time slot!");
    }

    private Room findRoom(RoomType type, String day, String slot) {
        for (Room r : rooms) {
            // Check if room matches type
            if (r.getType() != type) continue;
            
            // Check if room is already booked for this specific day and slot
            boolean isBooked = false;
            for (Lesson l : lessons) {
                if (l.room.getRoomId().equals(r.getRoomId()) && 
                    l.dayOfWeek.equals(day) && 
                    l.timeSlot.equals(slot)) {
                    isBooked = true;
                    break;
                }
            }
            
            if (!isBooked) return r;
        }
        return null;
    }

    public void printSchedule() {
        System.out.println("=== Full Schedule ===");
        for (Lesson l : lessons) {
            System.out.println(l);
        }
    }

    public List<Lesson> getLessons() {
        return new ArrayList<>(lessons);
    }
}