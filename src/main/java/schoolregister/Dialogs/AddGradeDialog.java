package schoolregister.Dialogs;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.Optional;


public class AddGradeDialog {
    public static Pair<String,String> showAndWait() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add grade");


        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField value = new TextField();
        value.setPromptText("value");
        TextField weight = new TextField();
        weight.setPromptText("weight");

        grid.add(new Label("Value:"), 0, 0);
        grid.add(value, 1, 0);
        grid.add(new Label("Weight:"), 0, 1);
        grid.add(weight, 1, 1);

        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(value::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Pair<>(value.getText(), weight.getText());
            }
            return null;
        });

        Optional<Pair<String,String> > o = dialog.showAndWait();
        return o.orElse(null);
    }
}
