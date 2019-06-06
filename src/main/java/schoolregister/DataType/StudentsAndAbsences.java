package schoolregister.DataType;


import javafx.scene.control.CheckBox;

public class StudentsAndAbsences extends Person {
    CheckBox isOnLesson = new CheckBox();
    public StudentsAndAbsences() {
        super(Type.student);
    }
    public StudentsAndAbsences(Person p) {
        this();
        this.setPesel(p.getPesel());
        this.setName(p.getName());
        this.setSurname(p.getSurname());
        this.setId(p.getId());
        this.setEmail(p.getEmail());
        this.setPhone(p.getPhone());
        this.setHash(p.getHash());
        this.setCity(p.getCity());
        this.setPostalCode(p.getPostalCode());
        this.setStreet(p.getStreet());
    }
    public void setAbsence(CheckBox isOnLesson){
        this.isOnLesson = isOnLesson;
    }
    public CheckBox getAbsence() {
        return isOnLesson;
    }

}
