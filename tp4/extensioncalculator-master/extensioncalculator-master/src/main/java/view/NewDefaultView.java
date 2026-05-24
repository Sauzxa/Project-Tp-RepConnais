package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NewDefaultView {
    public NewDefaultView() {
        Stage stage = new Stage();
        stage.setTitle("Add new default");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/newdefault.fxml"));
            stage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.show();
    }
}
