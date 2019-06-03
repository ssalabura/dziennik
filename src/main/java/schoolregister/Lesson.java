package schoolregister;

public class Lesson {
    public int subjectId,teacherId,groupId;
    public String subjectName,teacherName,groupName;
    public int dayId,slot;
    @Override
    public String toString() {
        return subjectName;
    }
}
