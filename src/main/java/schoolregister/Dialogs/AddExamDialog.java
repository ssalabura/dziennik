package schoolregister.Dialogs;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class AddExamDialog {
    public static String showAndWait() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Exam");


        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField value = new TextField();
        value.setPromptText("description");

        grid.add(new Label("Description:"), 0, 0);
        grid.add(value, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(value::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return value.getText();
            }
            return null;
        });

        Optional<String> o = dialog.showAndWait();
        return o.orElse(null);
    }
}
