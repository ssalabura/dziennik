package schoolregister.DataType;


import javafx.scene.control.CheckBox;

public class StudentsAndAbsences extends Person {
    CheckBox isOnLesson = new CheckBox();
    public StudentsAndAbsences() {
        super(Type.student);
    }

    public void setAbsence(CheckBox isOnLesson){
        this.isOnLesson = isOnLesson;
    }
    public CheckBox getAbsence() {
        return isOnLesson;
    }

}
