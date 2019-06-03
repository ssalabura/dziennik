package schoolregister;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        stage.setScene(new LessonsTable(1280,720));
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
