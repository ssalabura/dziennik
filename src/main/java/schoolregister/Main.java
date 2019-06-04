package schoolregister;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import schoolregister.Factory.SceneFactory;

import java.util.ArrayList;

public class Main extends Application {
    public static final short teacherMask = 1;
    public static final short guardianMask = 1 << 1;
    public static final short studentMask = 1 << 2;
    public static short userMask = 0;
    public static int[] userIDs = new int[5];

    public static Stage window;

    public static Scene mainScene;
    public static Scene lessonsTableScene;
    public static Scene studentScene;
    public static Scene teacherScene;

    public static ArrayList<Integer> guardianKids;
    public static int currentIndex;
    public static ArrayList<Scene> studentsScenes;

    private SceneFactory sceneFactory = SceneFactory.getInstance();
    @Override
    public void start(Stage stage) {
        window = stage;
        window.setTitle("School Register");
        window.setWidth(1280);
        window.setHeight(720);
        window.setResizable(false);
        window.setScene(sceneFactory.createLoginScene());
        window.show();
    }
    @Override
    public void stop() {
        Database.close();
    }

    static void onLaunch(String[] args) {
        launch();
    }
}
