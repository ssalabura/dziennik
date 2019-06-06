package schoolregister.Scenes;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import schoolregister.DataType.Attribute;
import schoolregister.DataType.Person;
import schoolregister.Factory.ViewFactory;

import static schoolregister.Main.mainScene;
import static schoolregister.Main.window;

public class UserInfoScene {
    private static ViewFactory viewFactory = ViewFactory.getInstance();

    public static Scene newScene(Person user){
        GridPane grid = viewFactory.createGrid();

        Button backButton = new Button("Back");
        backButton.setFont(Font.font(20));
        backButton.setMinSize(80, 30);

        TableView<Attribute> attributes = viewFactory.userInfoTable(user);

        attributes.setMinSize(900, 570);

        grid.add(attributes,10, 0);
        grid.add(backButton, 13, 2);

        backButton.setOnAction(actionEvent -> window.setScene(mainScene));

        return new Scene(grid, 1280, 720);
    }
}
