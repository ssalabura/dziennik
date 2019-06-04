package schoolregister.DataType;

public class Group {
    private String name;
    private String subject;
    private int id;
    private int subjectId;

    public Group() {}

    public Group setGroup(Group group){
        name = group.name;
        subject = group.subject;
        id = group.id;
        subjectId = group.subjectId;

        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String toString(){
        return name + " " + subject;
    }
}
