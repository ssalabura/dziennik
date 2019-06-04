package schoolregister.DataType;

public class Subject {
    private String subject;
    private int subjectId;

    public Subject(int id, String name) {
        this.subject = name;
        this.subjectId = id;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return subject;
    }
}
