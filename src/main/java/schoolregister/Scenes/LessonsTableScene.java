package schoolregister.Scenes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import schoolregister.DataType.LessonsOnSlot;
import schoolregister.Database;
import schoolregister.Main;

import java.util.ArrayList;

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

        TableColumn<LessonsOnSlot, String> idColumn = createColumn("ID","slotId");
        TableColumn<LessonsOnSlot, String> timeColumn = createColumn("Time","timeInterval");
        TableColumn<LessonsOnSlot, String> mon = createColumn("Monday","mon");
        TableColumn<LessonsOnSlot, String> tue = createColumn("Tuesday","tue");
        TableColumn<LessonsOnSlot, String> wed = createColumn("Wednesday","wed");
        TableColumn<LessonsOnSlot, String> thu = createColumn("Thursday","thu");
        TableColumn<LessonsOnSlot, String> fri = createColumn("Friday","fri");

        lessonsTable.setItems(lessons);

        idColumn.prefWidthProperty().bind(lessonsTable.widthProperty().multiply(0.03));
        timeColumn.prefWidthProperty().bind(lessonsTable.widthProperty().multiply(0.11));
        mon.prefWidthProperty().bind(lessonsTable.widthProperty().multiply(0.17));
        tue.prefWidthProperty().bind(lessonsTable.widthProperty().multiply(0.17));
        wed.prefWidthProperty().bind(lessonsTable.widthProperty().multiply(0.17));
        thu.prefWidthProperty().bind(lessonsTable.widthProperty().multiply(0.17));
        fri.prefWidthProperty().bind(lessonsTable.widthProperty().multiply(0.17));

        lessonsTable.setFixedCellSize(60);
        lessonsTable.getColumns().addAll(idColumn,timeColumn, mon, tue, wed,thu,fri);
        lessonsTable.setEditable(false);



        Button btn = new Button("Back");
        btn.setMinSize(300,50);

        if(isStudent)
            btn.setOnAction(e -> Main.window.setScene(Main.studentScene));
        else
            btn.setOnAction(e -> Main.window.setScene(Main.teacherScene));

        BorderPane pane = new BorderPane();
        BorderPane.setMargin(lessonsTable,new Insets(12,12,12,12));
        BorderPane.setMargin(btn,new Insets(12,12,12,12));
        BorderPane.setAlignment(btn,Pos.CENTER);
        pane.setCenter(lessonsTable);
        pane.setBottom(btn);

        return new Scene(pane, 1280, 720);
    }

    private static TableColumn<LessonsOnSlot, String> createColumn(String columnName, String propertyName) {
        TableColumn<LessonsOnSlot, String> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setSortable(false);
        //column.setReorderable(false);
        return column;

    }
}