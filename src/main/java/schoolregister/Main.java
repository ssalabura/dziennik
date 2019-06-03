package schoolregister;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {
    public static Person.Type userType;
    public static int userID;
    private Stage window;
    private Scene mainScene;
    @Override
    public void start(Stage stage) {
        window = stage;
        window.setTitle("School Register");
        window.setWidth(1280);
        window.setHeight(720);
        window.setResizable(false);
        window.setScene(createLoginScene());
        window.show();
    }
    @Override
    public void stop() {
        Database.close();
    }

    @SuppressWarnings("unchecked")
    private Scene createMainScene(){
        TableView<Person> table = new TableView<>();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.BASELINE_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        ObservableList<Person> students = FXCollections.observableArrayList(Database.getInstance().getPeople(Person.Type.student));

        final Label label = new Label("Students");
        label.setFont(new Font("Arial", 20));
        grid.add(label, 0, 5);

        table.setEditable(false);


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


        table.setItems(students);
        table.getColumns().addAll(idCol, nameCol, surnameCol, peselCol);

        grid.add(table, 0, 6);

        return new Scene(grid, 1280, 720);
    }



    private Scene createLoginScene(){
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle = new Text("Welcome");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        Label email = new Label("Email:");
        grid.add(email, 0, 1);

        TextField emailTextField = new TextField();
        grid.add(emailTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actionTarget = new Text();
        grid.add(actionTarget, 1, 6);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                Database.getInstance().logUser(emailTextField.getText(), pwBox.getText());
                mainScene = createMainScene();
                window.setScene(mainScene);
            }
        });

        return new Scene(grid, 1280, 720);
    }

    static void onLaunch(String[] args) {
        launch();
    }

}
