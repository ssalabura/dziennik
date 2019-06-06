package schoolregister.Dialogs;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import schoolregister.DataType.LessonsOnSlot;
import schoolregister.Database;
import schoolregister.utils.Time;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddTopicDialog {

    public static Date date;
    public static int slot;
    public static String topic;

    public static void showAndWait(int teacherId) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add new lesson to ");

        ButtonType doneButton = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(doneButton, ButtonType.CANCEL);

        List<LessonsOnSlot> lessons =  Database.getInstance().getLessonsAssignedTo(teacherId,false);

        DatePicker datePicker = new DatePicker();

        final ComboBox<String> slotBox = new ComboBox<>();

        datePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observableValue, LocalDate oldDate, LocalDate newDate) {
                ArrayList<String> days = new ArrayList<>();
                for (LessonsOnSlot l : lessons) {
                    Date date = Date.valueOf(newDate);
                    int dayId = Time.dateToSqlDayId(date)-1;
                    if(dayId >= 0 && dayId < 5 && l.get(dayId) != null)
                        days.add(l.get(dayId).getSlot()+" ("+l.get(dayId).getGroupName()+")");
                }
                ObservableList<String> options = FXCollections.observableArrayList(days);
                slotBox.setItems(options);
            }
        });



        slotBox.maxWidth(30);
        slotBox.setPromptText("Lesson nr");
        TextField topicField = new TextField();
        topicField.setPromptText("Topic");
        topicField.minWidth(800);

        GridPane grid = new GridPane();

        GridPane.setMargin(datePicker,new Insets(0,5,0,5));
        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);

        grid.add(new Label("Lesson nr:"), 2, 0);
        GridPane.setMargin(slotBox,new Insets(0,5,0,5));
        grid.add(slotBox, 3, 0);
        Label label = new Label("Topic");
        GridPane.setMargin(label,new Insets(5,5,5,5));
        GridPane.setMargin(topicField,new Insets(5,5,5,5));
        grid.add(label, 0, 1);
        grid.add(topicField, 1, 1,3,1);


        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == doneButton) {
                try {
                    LocalDate localDate = datePicker.getValue();
                    date = Date.valueOf(localDate);
                    slot = Integer.parseInt(slotBox.getValue().substring(0,slotBox.getValue().indexOf(' ')));
                    topic = topicField.getText();
                    System.out.println(date+" |"+slot+"| "+topic);
                    return new Pair<>("", "");
                }
                catch (Exception e){
                    return null;
                }
            }
            return null;
        });

        Optional<Pair<String, String>> result =  dialog.showAndWait();
        if(!result.isPresent()){
            date = null;
            slot = 0;
            topic = null;
        }
    }
}
