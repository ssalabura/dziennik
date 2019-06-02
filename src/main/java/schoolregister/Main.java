package schoolregister;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("JavaFX version " + javafxVersion + ", running on Java " + javaVersion + ", studentsCount "+ Database.getInstance().getStudentsCount());
        Scene scene = new Scene(new StackPane(l), 640, 480);
        stage.setScene(scene);
        stage.show();
    }
    @Override
    public void stop() {
        Database.close();
    }

    static void onLaunch(String[] args) {
        launch();
    }

}
