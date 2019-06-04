package schoolregister.Factory;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import schoolregister.DataType.*;
import schoolregister.DataType.Absence;
import schoolregister.DataType.Group;
import schoolregister.Database;

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
        resultTable.setMinSize(500,subjects.size()*30+30);
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

        value.setCellValueFactory(
                new PropertyValueFactory<>("value")
        );
        weight.setCellValueFactory(
                new PropertyValueFactory<>("weight")
        );

        resultTable.getColumns().addAll(value, weight);


        return resultTable;
    }

    public ListView<Group> getGroupsFor(int teacherId){
        ObservableList<Group> groups = FXCollections.observableArrayList(Database.getInstance().getGroupsFor(teacherId));

        ListView<Group> resultView = new ListView<>(groups);
        resultView.setEditable(false);


        return resultView;
    }
}