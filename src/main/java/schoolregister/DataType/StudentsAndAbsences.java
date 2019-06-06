package schoolregister.DataType;

public class StudentsAndAbsences extends Person {
    boolean isOnLesson = true;
    public StudentsAndAbsences() {
        super(Type.student);
    }

    public void setAbsence(boolean isOnLesson){
        this.isOnLesson = isOnLesson;
    }
    public char getAbsence() {
        return isOnLesson ? '+' : '-';
    }

}
