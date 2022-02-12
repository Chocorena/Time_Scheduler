package G13;

import G13.database.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApp extends Application {
    private static Stage stg;
    public static DatabaseConnection connectNow = new DatabaseConnection();

    /**
     * First Scene in the program Login-view.fxml
     * @param stage
     * @throws IOException
     * @author Ercan
     */
    @Override
    public void start(Stage stage) throws IOException {
        stg = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("Login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setResizable(false);
        stage.setTitle("Time Scheduler EMEF");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Change the Scene to AdminLogin-view.fxml or UserLogin-view.fxml
     * @param fxml
     * @throws IOException
     * @author Ercan
     */
    public void logedInScene(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        stg.setScene(new Scene(pane,1100,594));
        stg.getScene().setRoot(pane);
        stg.centerOnScreen();
    }

    /**
     * Change the Scene to Login-view.fxml
     * @param fxml
     * @throws IOException
     * @author Ercan
     */
    public void loginScene(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        stg.setScene(new Scene(pane,600,400));
        stg.getScene().setRoot(pane);
        stg.centerOnScreen();
    }

    /**
     * Open a new window the Register-info.fxml
     * @param fxml
     * @throws IOException
     * @author Ercan
     */
    public void regInfoScene(String fxml) throws IOException {
        Stage stage = new Stage();
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        stage.setResizable(false);
        stage.setTitle("Successfully");
        stage.setScene(new Scene(pane,260,100));
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * @param fxml
     * @throws IOException
     * @author Ercan
     */
    public void resInfoScene(String fxml) throws IOException {
        Stage stage = new Stage();
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        stage.setResizable(false);
        stage.setTitle("Password reset successfully");
        stage.setScene(new Scene(pane, 260,100));
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * @param fxml
     * @throws IOException
     * @author Ercan
     */
    public void succInfo(String fxml) throws  IOException {
        Stage stage = new Stage();
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        stage.setResizable(false);
        stage.setTitle("Personal data successfully updated");
        stage.setScene(new Scene(pane, 260,100));
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * @param fxml
     * @throws IOException
     * @author Ercan
     */
    public void ChangePwd(String fxml) throws IOException {
        Stage stage = new Stage();
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        stage.setResizable(false);
        stage.setTitle("Change Password");
        stage.setScene(new Scene(pane, 440,260));
        stage.centerOnScreen();
        stage.show();
    }


    /**
     * Runs application
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }
}