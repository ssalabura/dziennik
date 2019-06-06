package schoolregister.Dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import schoolregister.DataType.Group;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

public class AddTopicDialog {

    public static Date date;
    public static int slot;
    public static String topic;

    public static void showAndWait() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add new lesson to ");

        ButtonType doneButton = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(doneButton, ButtonType.CANCEL);



        DatePicker datePicker = new DatePicker();

        TextField slotField = new TextField();
        slotField.maxWidth(30);
        slotField.setPromptText("Lesson nr");
        TextField topicField = new TextField();
        topicField.setPromptText("Topic");
        topicField.minWidth(800);

        GridPane grid = new GridPane();

        GridPane.setMargin(datePicker,new Insets(0,5,0,5));
        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);

        grid.add(new Label("Lesson nr:"), 2, 0);
        GridPane.setMargin(slotField,new Insets(0,5,0,5));
        grid.add(slotField, 3, 0);
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
                    slot = Integer.parseInt(slotField.getText());
                    topic = topicField.getText();
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
