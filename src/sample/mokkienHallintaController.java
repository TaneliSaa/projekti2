package sample;

import java.io.IOException;
import java.sql.Connection;
import javafx.fxml.FXML;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.connectivity.connectionClass;

public class mokkienHallintaController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    public mokkienHallintaController() {
    }

    public void switchToScene1(ActionEvent event) throws IOException {
        this.root = (Parent)FXMLLoader.load(this.getClass().getResource("resources/paaNaytto.fxml"));
        this.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    @FXML
    public void testiNappi(ActionEvent event) {

        connectionClass connectNow = new connectionClass();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT * FROM toimintaalue";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(query);

            while (queryResult.next()) {
                String nimi = queryResult.getString("nimi");
                System.out.println(nimi);
            }
            statement.close();

            } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
