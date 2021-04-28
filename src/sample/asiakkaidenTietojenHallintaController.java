package sample;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class asiakkaidenTietojenHallintaController {


    @FXML
    private TableView<asiakas> tableView;
    @FXML
    private TableColumn<asiakas, Integer> asiakas_idColumn;
    @FXML
    private TableColumn<asiakas, Integer> postinroColumn;
    @FXML
    private TableColumn<asiakas, String> etunimiColumn;
    @FXML
    private TableColumn<asiakas, String> sukunimiColumn;
    @FXML
    private TableColumn<asiakas, String> lahiosoiteColumn;
    @FXML
    private TableColumn<asiakas, String> emailColumn;
    @FXML
    private TableColumn<asiakas, String> puhelinnroColumn;


    //Metodi, jolla pääsee takaisin päänäyttöön
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToPaaNaytto(ActionEvent event) throws IOException {
        this.root = (Parent)FXMLLoader.load(this.getClass().getResource("resources/paaNaytto.fxml"));
        this.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    public void initialize() {
        asiakas_idColumn.setCellValueFactory(new PropertyValueFactory<asiakas,Integer>("asiakas_id"));
        postinroColumn.setCellValueFactory(new PropertyValueFactory<asiakas,Integer>("postinro"));
        etunimiColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("etunimi"));
        sukunimiColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("sukunimi"));
        lahiosoiteColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("lahiosoite"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("email"));
        puhelinnroColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("puhelinnro"));

        tableView.setItems(getAsiakas());
    }

    public ObservableList<asiakas> getAsiakas(){

        ObservableList<asiakas> asiakas = FXCollections.observableArrayList();
        asiakas.add(new asiakas(2,70500,"Kalle","Palonen","Lentäjänpolku","Kalle.Palonen@gmail.com",
                "0440946341"));

        return asiakas;
    }


}
