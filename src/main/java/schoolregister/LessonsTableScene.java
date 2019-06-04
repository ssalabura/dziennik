package schoolregister;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import schoolregister.DataType.Grade;
import schoolregister.DataType.Lesson;
import schoolregister.DataType.LessonsOnSlot;

public class LessonsTableScene {
    private static int id = -1;
    private static boolean isStudent;

    public static void setToStudent(int id) {
        isStudent = true;
        LessonsTableScene.id = id;
    }

    public static void setToTeacher(int id) {
        isStudent = false;
        LessonsTableScene.id = id;
    }
    @SuppressWarnings("unchecked")
    public static Scene newScene() {
        TableView<LessonsOnSlot> lessonsTable = new TableView<>();
        lessonsTable.setEditable(false);

        ObservableList<LessonsOnSlot> lessons = FXCollections.observableArrayList(Database.getInstance().getLessonsAssignedTo(id,isStudent));

        TableColumn<LessonsOnSlot, String> mon = new TableColumn<>("Monday");
        TableColumn<LessonsOnSlot, String> tue = new TableColumn<>("Tuesday");
        TableColumn<LessonsOnSlot, String > wed = new TableColumn<>("Wednesday");
        TableColumn<LessonsOnSlot, String > thu = new TableColumn<>("Thursday");
        TableColumn<LessonsOnSlot, String > fri = new TableColumn<>("Friday");

        mon.setCellValueFactory(new PropertyValueFactory<>("mon"));
        tue.setCellValueFactory(new PropertyValueFactory<>("tue"));
        wed.setCellValueFactory(new PropertyValueFactory<>("wed"));
        thu.setCellValueFactory(new PropertyValueFactory<>("thu"));
        fri.setCellValueFactory(new PropertyValueFactory<>("fri"));

        mon.setSortable(false);
        tue.setSortable(false);
        wed.setSortable(false);
        thu.setSortable(false);
        fri.setSortable(false);

        lessonsTable.setItems(lessons);

        lessonsTable.getColumns().addAll(mon, tue, wed,thu,fri);
        lessonsTable.setEditable(false);



        Button btn = new Button("Back");
        btn.setMinSize(300,50);


        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Main.window.setScene(Main.studentScene);
            }
        });

        BorderPane pane = new BorderPane();
        BorderPane.setMargin(lessonsTable,new Insets(12,12,12,12));
        BorderPane.setMargin(btn,new Insets(12,12,12,12));
        BorderPane.setAlignment(btn,Pos.CENTER);
        pane.setCenter(lessonsTable);
        pane.setBottom(btn);

        return new Scene(pane, 1280, 720);
    }
}