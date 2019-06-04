package schoolregister.Scenes;

import javafx.animation.PauseTransition;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.util.Duration;
import schoolregister.Database;
import schoolregister.Factory.SceneFactory;
import schoolregister.Main;

import static schoolregister.Main.mainScene;
import static schoolregister.Main.userMask;

public class LoginScene {
    static int badTries;

    @SuppressWarnings("unchecked")
    public static Scene newScene() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle = new Text("Sign in");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        sceneTitle.setTextAlignment(TextAlignment.CENTER);
        grid.add(sceneTitle, 0, 0, 2, 1);
        GridPane.setHalignment(sceneTitle, HPos.CENTER);

        Label email = new Label("Email:");
        grid.add(email, 0, 1);

        TextField emailTextField = new TextField();
        emailTextField.setText("Alison@carlo.com");
        grid.add(emailTextField, 1, 1);


        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        pwBox.setText("moc.olrac@nosilA");
        grid.add(pwBox, 1, 2);

        Button btn = new Button("Log in");
        btn.setDefaultButton(true);
        btn.setMinSize(100,40);

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actionTarget = new Text();
        grid.add(actionTarget, 1, 6);



        Text invalidEmailMessage = new Text("Invalid email/password");
        invalidEmailMessage.setStyle("-fx-font-size: 24");
        invalidEmailMessage.setFill(Color.RED);
        invalidEmailMessage.setVisible(false);


        grid.add(invalidEmailMessage, 0, 3,2,1);

        btn.setOnAction(e -> {
            userMask = Database.getInstance().logUser(emailTextField.getText(), pwBox.getText());
            if(userMask != 0) {
                mainScene = SceneFactory.getInstance().createMainScene();
                Main.window.setScene(mainScene);
            }
            else{
                invalidEmailMessage.setVisible(true);
                badTries++;
                PauseTransition delay = new PauseTransition(Duration.seconds(3));
                delay.setOnFinished( event -> {
                    badTries--;
                    if(badTries == 0) {
                        invalidEmailMessage.setVisible(false);
                    }
                });
                delay.play();
            }
        });

        return new Scene(grid, 1280, 720);
    }
}
