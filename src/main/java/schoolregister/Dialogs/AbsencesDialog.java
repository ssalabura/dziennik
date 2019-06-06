package schoolregister.Dialogs;

import javafx.collections.FXCollections;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import schoolregister.DataType.Lesson;
import schoolregister.DataType.StudentsAndAbsences;
import schoolregister.Database;
import schoolregister.Factory.ViewFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AbsencesDialog {
    public static List<StudentsAndAbsences> before;
    public static List<StudentsAndAbsences> after;

    public static void showAndWait(Lesson currentLesson, int group_id) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Check student\'s absences");

        ButtonType doneButton = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(doneButton, ButtonType.CANCEL);

        before = Database.getInstance().getStudentsAndAbsencesFor(group_id,currentLesson.getLessonId());
        after = new ArrayList<>();
        for(StudentsAndAbsences s : before){
            StudentsAndAbsences sa = new StudentsAndAbsences(s);
            sa.getAbsence().setSelected(s.getAbsence().isSelected());
            after.add(sa);
        }
        TableView<StudentsAndAbsences> students = ViewFactory.getInstance().getStudentsAndAbsences();
        students.setItems(FXCollections.observableArrayList(after));


        GridPane pane = new GridPane();
        pane.add(students,0,0,2,1);

        dialog.getDialogPane().setContent(pane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == doneButton) {
                return new Pair<>("","");
            }
            return null;
        });

        Optional<Pair<String, String>> result =  dialog.showAndWait();
        if(!result.isPresent()){
            after = null;
            before = null;
        }
    }
}
