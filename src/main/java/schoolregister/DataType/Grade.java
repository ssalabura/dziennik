package schoolregister.DataType;

public class Grade {
    private String subject;
    private String value;
    private int weight;
    private int subjectId;
    private float floatValue;

    public Grade(int subjectId, String subject, String value, float floatValue, int weight) {
        this.subjectId = subjectId;
        this.subject = subject;
        this.floatValue = floatValue;
        this.value = value;
        this.weight = weight;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    @Override
    public String toString() {
        return value;
    }

    public float getFloatValue() {
        return floatValue;
    }
}
