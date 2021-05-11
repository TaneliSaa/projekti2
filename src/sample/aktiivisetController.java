package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;


import java.io.IOException;

public class aktiivisetController {

    @FXML
    public Label etunimiLabel;

    @FXML
    public Label sukunimiLabel;

    @FXML
    public Label lahiosoiteLabel;

    @FXML
    public Label postinumeroLabel;

    @FXML
    public Label laskuIDLabel;

    @FXML
    public Label viitenumeroLabel;

    @FXML
    public Label erapaivaLabel;



    public void vastaanOtaTiedot(String viesti) {
        etunimiLabel.setText(viesti);

    }




    //Metodi, jolla pääsee takaisin päänäyttöön
    private Stage stage;
    private Scene scene;
    private Parent root;

    //Luodaan uusi ikkuna, joka korvataan vanhalla
    public void switchToLaskutus(ActionEvent event) throws IOException {
        this.root = (Parent) FXMLLoader.load(this.getClass().getResource("resources/laskutus.fxml"));
        this.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.show();
    }
}
