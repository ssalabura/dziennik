package schoolregister.Factory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import schoolregister.DataType.*;
import schoolregister.Database;
import schoolregister.Scenes.TeacherScene;
import schoolregister.utils.ExceptionHandler;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ViewFactory {
    private static ViewFactory factory;

    private ViewFactory() {}

    public static ViewFactory getInstance(){
        if(factory == null)
            factory = new ViewFactory();
        return factory;
    }

    public GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.BASELINE_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        return grid;
    }

    @SuppressWarnings("unchecked")
    public TableView<GradeList> getGradesFor(int studentId){
        TableView<GradeList> resultTable = new TableView<>();
        resultTable.setEditable(false);

        List<Subject> subjects = Database.getInstance().getAllSubjects(studentId);
        List<GradeList> gradeLists = new ArrayList<>();
        for(Subject s : subjects)
            gradeLists.add(new GradeList(s));

        for(Grade g : Database.getInstance().getGrades(studentId)){
            GradeList gl;
            for(GradeList l : gradeLists) {
                if(l.getSubjectId() == g.getSubjectId()) {
                    gl = l;
                    gl.addGrades(g);
                    break;
                }
            }

        }

        ObservableList<GradeList> observableGradeList = FXCollections.observableArrayList(gradeLists);

        TableColumn<GradeList, String> subjectName = new TableColumn<>("subject");
        TableColumn<GradeList, String> gradesColumn = new TableColumn<>("grades");
        TableColumn<GradeList, String> averageColumn = new TableColumn<>("average");

        subjectName.setCellValueFactory(new PropertyValueFactory<>("subject"));
        gradesColumn.setCellValueFactory(new PropertyValueFactory<>("grades"));
        averageColumn.setCellValueFactory(new PropertyValueFactory<>("average"));

        subjectName.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.34));
        gradesColumn.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.5));
        gradesColumn.setStyle("-fx-alignment: CENTER");
        averageColumn.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.15));

        resultTable.setItems(observableGradeList);
        resultTable.setMinSize(500,440);
        resultTable.getColumns().addAll(subjectName, gradesColumn,averageColumn);

        return resultTable;
    }

    @SuppressWarnings("unchecked")
    public TableView<Absence> getAbsencesFor(int studentId){
        TableView<Absence> resultTable = new TableView<>();
        resultTable.setEditable(false);

        ObservableList<Absence> absences = FXCollections.observableArrayList(Database.getInstance().getAbsences(studentId));
        TableColumn<Absence, String> dateColumn = new TableColumn<>("date");
        TableColumn<Absence, String> dayIdColumn = new TableColumn<>("day of week");
        TableColumn<Absence, Integer> slotColumn = new TableColumn<>("lesson nr");
        TableColumn<Absence, String> subjectColumn = new TableColumn<>("subject");

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dayIdColumn.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
        slotColumn.setCellValueFactory(new PropertyValueFactory<>("slot"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));

        resultTable.setItems(absences);
        resultTable.getColumns().addAll(dateColumn,dayIdColumn, slotColumn, subjectColumn);

        dateColumn.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.2));
        dayIdColumn.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.19));
        slotColumn.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.2));
        subjectColumn.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.4));

        resultTable.setMinWidth(600);
        return resultTable;
    }

    @SuppressWarnings("unchecked")
    public TableView<Person> getPeopleFor(Person.Type type){
        TableView<Person> resultTable = new TableView<>();
        resultTable.setEditable(false);

        ObservableList<Person> people = FXCollections.observableArrayList(Database.getInstance().getPeople(type));

        TableColumn<Person, Integer> idCol = new TableColumn<>("id");
        TableColumn<Person, String> nameCol = new TableColumn<>("name");
        TableColumn<Person, String> surnameCol = new TableColumn<>("surname");
        TableColumn<Person, String> peselCol = new TableColumn<>("pesel");

        idCol.setCellValueFactory(
                new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(
                new PropertyValueFactory<>("name"));
        surnameCol.setCellValueFactory(
                new PropertyValueFactory<>("surname"));
        peselCol.setCellValueFactory(
                new PropertyValueFactory<>("pesel"));


        resultTable.setItems(people);
        resultTable.getColumns().addAll(idCol, nameCol, surnameCol, peselCol);

        return resultTable;
    }

    @SuppressWarnings("unchecked")
    public TableView<Grade> getGrades(){
        TableView<Grade> resultTable = new TableView<>();
        resultTable.setEditable(false);
        TableColumn<Grade, String> value = new TableColumn<>("value");
        TableColumn<Grade, Integer> weight = new TableColumn<>("weight");

        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        value.setCellFactory(TextFieldTableCell.forTableColumn());
        value.setOnEditCommit(
                (TableColumn.CellEditEvent<Grade, String> t) -> {
                    final int pos = t.getTablePosition().getRow();
                    final Grade grade =  t.getTableView().getItems().get(pos);
                    try {
                        Database.getInstance().updateGrade(grade, t.getNewValue(), grade.getWeight());
                        grade.setValue(t.getNewValue());
                    }
                    catch (SQLException e) {
                        ExceptionHandler.onFailUpdate(e, TeacherScene.currentType.getValue());
                    }
                });

        weight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        weight.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        weight.setOnEditCommit(
                (TableColumn.CellEditEvent<Grade, Integer> t) -> {
                    final int pos = t.getTablePosition().getRow();
                    final Grade grade =  t.getTableView().getItems().get(pos);
                    try {
                        Database.getInstance().updateGrade(grade, grade.getValue(), t.getNewValue());
                        grade.setWeight(t.getNewValue());
                    }
                    catch (SQLException e) {
                        ExceptionHandler.onFailUpdate(e, TeacherScene.currentType.getValue());
                    }
                });


        resultTable.getColumns().addAll(value, weight);
        return resultTable;
    }

    @SuppressWarnings("unchecked")
    public TableView<Group> getGroupsFor(int teacherId){
        TableView<Group> resultView = new TableView<>();
        ObservableList<Group> groups = FXCollections.observableArrayList(Database.getInstance().getGroupsFor(teacherId));

        TableColumn<Group, String> groupName = new TableColumn<>("group");
        TableColumn<Group, String> subjectName = new TableColumn<>("subject");

        groupName.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );
        subjectName.setCellValueFactory(
                new PropertyValueFactory<>("subject")
        );

        resultView.getColumns().addAll(groupName, subjectName);
        resultView.setEditable(false);

        resultView.setItems(groups);

        return resultView;
    }

    @SuppressWarnings("unchecked")
    public TableView<Person> getStudents(){
        TableView<Person> resultView = new TableView<>();

        TableColumn<Person, Integer> studentId = new TableColumn<>("id");
        TableColumn<Person, String> name = new TableColumn<>("name");
        TableColumn<Person, String> surName = new TableColumn<>("surname");

        studentId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );
        name.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );
        surName.setCellValueFactory(
                new PropertyValueFactory<>("surname")
        );

        resultView.getColumns().addAll(studentId, name, surName);
        resultView.setEditable(false);

        return resultView;
    }
    public TableView<StudentsAndAbsences> getStudentsAndAbsences() {
        TableView<StudentsAndAbsences> resultView = new TableView<>();
        TableColumn<StudentsAndAbsences, Integer> studentId = new TableColumn<>("id");
        TableColumn<StudentsAndAbsences, String> name = new TableColumn<>("name");
        TableColumn<StudentsAndAbsences, String> surName = new TableColumn<>("surname");
        TableColumn<StudentsAndAbsences, CheckBox> absencesColumn = new TableColumn<>( "is" );


        studentId.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        surName.setCellValueFactory(new PropertyValueFactory<>("surname"));
        absencesColumn.setCellValueFactory(new PropertyValueFactory<>("absence"));
      //  absencesColumn.setCellFactory( tc -> new CheckBoxTableCell<>().tableColumnProperty());

        resultView.getColumns().addAll(studentId, name, surName,absencesColumn);
        resultView.setEditable(true);
        return resultView;
    }


    @SuppressWarnings("unchecked")
    public TableView<Absence> getAbsences(){
        TableView<Absence> resultView = new TableView<>();

        TableColumn<Absence, Date> date = new TableColumn<>("date");
        TableColumn<Absence, Integer> day = new TableColumn<>("day");
        TableColumn<Absence, Integer> slot = new TableColumn<>("slot");


        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        day.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
        slot.setCellValueFactory(new PropertyValueFactory<>("slot"));

        date.prefWidthProperty().bind(resultView.widthProperty().multiply(0.4));
        date.setStyle("-fx-alignment: CENTER");
        day.prefWidthProperty().bind(resultView.widthProperty().multiply(0.39));
        day.setStyle("-fx-alignment: CENTER");
        slot.prefWidthProperty().bind(resultView.widthProperty().multiply(0.2));
        slot.setStyle("-fx-alignment: CENTER");


        resultView.getColumns().addAll(date, day, slot);
        resultView.setEditable(true);

        return resultView;
    }

    @SuppressWarnings("unchecked")
    public TableView<Lesson> getLessonsForTeacher() {
        TableView<Lesson> resultView = new TableView<>();

        TableColumn<Lesson, Date> date = new TableColumn<>("date");
        TableColumn<Lesson, String> day = new TableColumn<>("day");
        TableColumn<Lesson, Integer> slot = new TableColumn<>("slot");
        TableColumn<Lesson, String> topic = new TableColumn<>("topic");

        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        day.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
        slot.setCellValueFactory(new PropertyValueFactory<>("slot"));
        topic.setCellValueFactory(new PropertyValueFactory<>("topic"));
        topic.setCellFactory(TextFieldTableCell.forTableColumn());
        topic.setOnEditCommit(
                (TableColumn.CellEditEvent<Lesson, String> t) -> {
                    final int pos = t.getTablePosition().getRow();
                    final Lesson lesson = t.getTableView().getItems().get(pos);
                    try {
                        Database.getInstance().updateLesson(lesson, t.getNewValue());
                        lesson.setTopic(t.getNewValue());
                    }
                    catch (SQLException e) {
                        ExceptionHandler.onFailUpdate(e, TeacherScene.currentType.getValue());
                    }
                });

        date.prefWidthProperty().bind(resultView.widthProperty().multiply(0.10));
        date.setStyle("-fx-alignment: CENTER");
        day.prefWidthProperty().bind(resultView.widthProperty().multiply(0.10));
        day.setStyle("-fx-alignment: CENTER");
        slot.prefWidthProperty().bind(resultView.widthProperty().multiply(0.05));
        slot.setStyle("-fx-alignment: CENTER");
        topic.prefWidthProperty().bind(resultView.widthProperty().multiply(0.74));


        resultView.getColumns().addAll(date,day, slot, topic);
        resultView.setEditable(true);

        return resultView;
    }

    @SuppressWarnings("unchecked")
    public TableView<Lesson> getLessonsForStudent(int studentId) {
        TableView<Lesson> resultView = new TableView<>();

        TableColumn<Lesson, Date> date = new TableColumn<>("date");
        TableColumn<Lesson, String> day = new TableColumn<>("day");
        TableColumn<Lesson, Integer> slot = new TableColumn<>("slot");
        TableColumn<Lesson, String> subject = new TableColumn<>("subjectName");
        TableColumn<Lesson, String> topic = new TableColumn<>("topic");

        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        day.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
        slot.setCellValueFactory(new PropertyValueFactory<>("slot"));
        subject.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        topic.setCellValueFactory(new PropertyValueFactory<>("topic"));

        date.prefWidthProperty().bind(resultView.widthProperty().multiply(0.15));
        date.setStyle("-fx-alignment: CENTER");
        day.prefWidthProperty().bind(resultView.widthProperty().multiply(0.10));
        day.setStyle("-fx-alignment: CENTER");
        slot.prefWidthProperty().bind(resultView.widthProperty().multiply(0.05));
        slot.setStyle("-fx-alignment: CENTER");
        subject.prefWidthProperty().bind(resultView.widthProperty().multiply(0.2));
        topic.prefWidthProperty().bind(resultView.widthProperty().multiply(0.495));


        resultView.getColumns().addAll(date,day, slot,subject, topic);
        resultView.setItems(FXCollections.observableArrayList(Database.getInstance().getLessonsForStudent(studentId)));
        resultView.setEditable(false);

        return resultView;
    }

    @SuppressWarnings("unchecked")
    public TableView<Exam> getExamsForTeacher() {
        TableView<Exam> resultView = new TableView<>();

        TableColumn<Exam, Date> date = new TableColumn<>("date");
        TableColumn<Exam, String> day = new TableColumn<>("day");
        TableColumn<Exam, Integer> slot = new TableColumn<>("slot");
        TableColumn<Exam, String> description = new TableColumn<>("description");

        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        day.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
        slot.setCellValueFactory(new PropertyValueFactory<>("slot"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));

        date.prefWidthProperty().bind(resultView.widthProperty().multiply(0.10));
        date.setStyle("-fx-alignment: CENTER");
        day.prefWidthProperty().bind(resultView.widthProperty().multiply(0.10));
        day.setStyle("-fx-alignment: CENTER");
        slot.prefWidthProperty().bind(resultView.widthProperty().multiply(0.05));
        slot.setStyle("-fx-alignment: CENTER");
        description.prefWidthProperty().bind(resultView.widthProperty().multiply(0.74));


        description.setCellFactory(TextFieldTableCell.forTableColumn());
        description.setOnEditCommit(
                (TableColumn.CellEditEvent<Exam, String> t) -> {
                    final int pos = t.getTablePosition().getRow();
                    final Exam exam =  t.getTableView().getItems().get(pos);
                    try {
                        Database.getInstance().updateExam(exam, t.getNewValue());
                        exam.setDescription(t.getNewValue());
                    }
                    catch (SQLException e) {
                        ExceptionHandler.onFailUpdate(e, TeacherScene.currentType.getValue());
                    }
                });

        resultView.getColumns().addAll(date,day, slot, description);
        resultView.setEditable(true);

        return resultView;
    }

    @SuppressWarnings("unchecked")
    public TableView<Exam> getExamsForStudent(int studentId) {
        TableView<Exam> resultView = new TableView<>();

        TableColumn<Exam, String> subjectName = new TableColumn<>("subject");
        TableColumn<Exam, Date> date = new TableColumn<>("date");
        TableColumn<Exam, String> day = new TableColumn<>("day");
        TableColumn<Exam, Integer> slot = new TableColumn<>("slot");
        TableColumn<Exam, String> description = new TableColumn<>("description");

        subjectName.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        day.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
        slot.setCellValueFactory(new PropertyValueFactory<>("slot"));
        description.setCellValueFactory(new PropertyValueFactory<>("topic"));

        subjectName.prefWidthProperty().bind(resultView.widthProperty().multiply(0.10));
        date.prefWidthProperty().bind(resultView.widthProperty().multiply(0.10));
        date.setStyle("-fx-alignment: CENTER");
        day.prefWidthProperty().bind(resultView.widthProperty().multiply(0.10));
        day.setStyle("-fx-alignment: CENTER");
        slot.prefWidthProperty().bind(resultView.widthProperty().multiply(0.05));
        slot.setStyle("-fx-alignment: CENTER");
        description.prefWidthProperty().bind(resultView.widthProperty().multiply(0.74));


        resultView.setItems(FXCollections.observableArrayList(Database.getInstance().getExamsForStudent(studentId)));

        resultView.getColumns().addAll(date,day, slot, description);
        resultView.setEditable(false);

        return resultView;
    }



    @SuppressWarnings("unchecked")
    public TableView<Attribute> userInfoTable(Person user){
        TableView<Attribute> resultView = new TableView<>();
        TableColumn<Attribute, String> attributes = new TableColumn<>("");
        TableColumn<Attribute, String> values = new TableColumn<>("");

        Attribute name = new Attribute("Name", user.getName());
        Attribute surname = new Attribute("Surname", user.getSurname());
        Attribute pesel = new Attribute("PESEL", user.getPesel());
        Attribute email = new Attribute("Email", user.getEmail());
        Attribute phone = new Attribute("Phone", user.getPhone());
        Attribute city = new Attribute("City", user.getCity());
        Attribute street = new Attribute("Street", user.getStreet());
        Attribute postalCode = new Attribute("Postalcode", user.getPostalCode());

        attributes.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );

        values.setCellValueFactory(
                new PropertyValueFactory<>("value")
        );


        attributes.prefWidthProperty().bind(resultView.widthProperty().multiply(0.10));
        values.prefWidthProperty().bind(resultView.widthProperty().multiply(0.90));

        ObservableList<Attribute> list = FXCollections.observableArrayList();

        list.addAll(name, surname, pesel, email, phone, city, street, postalCode);

        System.out.println(list.size());

        resultView.getColumns().addAll(attributes, values);

        resultView.setItems(list);

        resultView.setEditable(false);

        resultView.setFixedCellSize(60);
        resultView.getSelectionModel().setCellSelectionEnabled(false);

        attributes.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Attribute, String> call(TableColumn<Attribute, String> attributeStringTableColumn) {
                return new TableCell<>(){
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if(isEmpty()){
                            setText("");
                        }
                        else{
                            setFont(Font.font ("Verdana", 14));
                            setText(item);
                        }
                    }
                };
            }
        });

        return resultView;
    }

}
