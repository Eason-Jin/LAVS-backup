package uoa.lavs;

import java.util.HashMap;
import javafx.scene.Parent;

/** Manager class for the current view being displayed in the scene on the stage. */
public class SceneManager {

    /** The different views available. */
    public enum AppScene {
        START,
        ADD_CUSTOMER,
        SEARCH,
        CUSTOMER_DETAILS
    }

    public static HashMap<AppScene, Parent> sceneMap = new HashMap<AppScene, Parent>();

    public static void addScene(AppScene scene, Parent root) {
        sceneMap.put(scene, root);
    }

    public static Parent getScene(AppScene scene) {
        return sceneMap.get(scene);
    }
}