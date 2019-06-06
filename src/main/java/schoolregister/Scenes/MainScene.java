package schoolregister.Scenes;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import schoolregister.DataType.Person;
import schoolregister.Database;
import schoolregister.Factory.SceneFactory;
import schoolregister.Wrapper.PersonWrapper;

import static schoolregister.Main.*;

public class MainScene {
    public static Scene newScene() {
        StackPane stackPane = new StackPane();
        VBox vbox = new VBox(20);

        Text name = new Text();
        name.setFont(Font.font(30));

        Button studentButton = new Button("Student");
        studentButton.setFont(Font.font(20));
        Button teacherButton = new Button("Teacher");
        teacherButton.setFont(Font.font(20));
        Button guardianButton = new Button("Guardian");
        guardianButton.setFont(Font.font(20));
        Button userInfoButton = new Button("User Info");
        userInfoButton.setFont(Font.font(20));
        Button logoutButton = new Button("Log out");
        logoutButton.setFont(Font.font(20));

        vbox.getChildren().add(name);

        PersonWrapper person = new PersonWrapper();

        if((userMask & studentMask) != 0) {
            vbox.getChildren().add(studentButton);
            person.setPerson(Database.getInstance().getPerson(userIDs[studentMask],Person.Type.student));
            name.setText(person.getPerson().getName() + " " + person.getPerson().getSurname());
        }
        if((userMask & teacherMask) != 0) {
            vbox.getChildren().add(teacherButton);
            person.setPerson(Database.getInstance().getPerson(userIDs[teacherMask],Person.Type.teacher));
            name.setText(person.getPerson().getName() + " " + person.getPerson().getSurname());
        }
        if((userMask & guardianMask) != 0) {
            vbox.getChildren().add(guardianButton);
            person.setPerson(Database.getInstance().getPerson(userIDs[guardianMask],Person.Type.guardian));
            name.setText(person.getPerson().getName() + " " + person.getPerson().getSurname());
        }

        vbox.getChildren().add(userInfoButton);
        vbox.getChildren().add(logoutButton);
        vbox.setAlignment(Pos.CENTER);
        stackPane.getChildren().add(vbox);

        studentButton.setOnAction(actionEvent -> {
            studentScene = SceneFactory.getInstance().createStudentScene(userIDs[studentMask], false);
            window.setScene(studentScene);
        });

        teacherButton.setOnAction(actionEvent -> {
            teacherScene = SceneFactory.getInstance().createTeacherScene(userIDs[teacherMask]);
            window.setScene(teacherScene);
        });

        guardianButton.setOnAction(actionEvent -> {
            SceneFactory.getInstance().createGuardianScene(userIDs[guardianMask]);
            window.setScene(studentsScenes.get(0));
        });

        userInfoButton.setOnAction(actionEvent -> {
            userInfoScene = SceneFactory.getInstance().createUserInfoScreen(person.getPerson());
            window.setScene(userInfoScene);
        });

        logoutButton.setOnAction(actionEvent -> {
            window.setScene(LoginScene.newScene());
        });

        return new Scene(stackPane, 1280, 720);
    }
}
