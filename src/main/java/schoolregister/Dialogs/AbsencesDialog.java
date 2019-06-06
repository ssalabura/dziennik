package schoolregister.Dialogs;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import schoolregister.DataType.Person;
import schoolregister.DataType.StudentsAndAbsences;
import schoolregister.Database;
import schoolregister.Factory.ViewFactory;

import java.util.ArrayList;
import java.util.List;

public class AbsencesDialog {
    public static List<StudentsAndAbsences> res;

    public static void showAndWait(int group_id) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Check student\'s absences");

        ButtonType doneButton = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(doneButton, ButtonType.CANCEL);

        TableView<StudentsAndAbsences> students = ViewFactory.getInstance().getStudentsAndAbsences();
        List<Person> rawList =  Database.getInstance().getStudentsFor(group_id);
        List<StudentsAndAbsences> studentsList = new ArrayList<>();
        for(Person p : rawList) {
            StudentsAndAbsences s = new StudentsAndAbsences();
            s.setId(p.getId());
            s.setName(p.getName());
            s.setSurname(p.getSurname());
            s.setPesel(p.getPesel());
            studentsList.add(s);
        }

        students.setItems(FXCollections.observableArrayList(studentsList));

        GridPane pane = new GridPane();
        pane.add(students,0,0,2,1);

        Node addButton = dialog.getDialogPane().lookupButton(doneButton);

        dialog.getDialogPane().setContent(pane);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == doneButton) {
                res = new ArrayList<>();
                for(StudentsAndAbsences s : students.getItems()) {
                    res.add(s);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
}
