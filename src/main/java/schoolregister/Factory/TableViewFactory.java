package schoolregister.Factory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import schoolregister.Database;
import schoolregister.DataType.Grade;
import schoolregister.DataType.Person;

public class TableViewFactory {
    private static TableViewFactory factory;

    private TableViewFactory() {}

    public static TableViewFactory getInstance(){
        if(factory == null)
            factory = new TableViewFactory();
        return factory;
    }

    @SuppressWarnings("unchecked")
    public TableView<Grade> getGradesFor(int studentId){
        TableView<Grade> resultTable = new TableView<>();
        resultTable.setEditable(false);

        ObservableList<Grade> grades = FXCollections.observableArrayList(Database.getInstance().getGrades(studentId));

        TableColumn<Grade, String> subjectName = new TableColumn<>("subject");
        TableColumn<Grade, String> value = new TableColumn<>("value");
        TableColumn<Grade, Integer> weight = new TableColumn<>("weight");

        subjectName.setCellValueFactory(
                new PropertyValueFactory<>("subject")
        );
        value.setCellValueFactory(
                new PropertyValueFactory<>("value")
        );
        weight.setCellValueFactory(
                new PropertyValueFactory<>("weight")
        );

        resultTable.setItems(grades);
        resultTable.getColumns().addAll(subjectName, value, weight);


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
}
