package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sample.connectivity.connectionClass;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class laskutusController {

    @FXML
    private TextField summaTextField;

    @FXML
    private Label etunimiLabel;

    @FXML
    private Label sukunimiLabel;

    @FXML
    private Label lahiosoiteLabel;

    @FXML
    private Label postinumeroLabel;

    @FXML
    private Label mokinNimiLabel;

    @FXML
    private Label mokinPostiNumeroLabel;

    @FXML
    private Label katuosoiteLabel;

    @FXML
    private Label laskunNumeroLabel;

    @FXML
    private TableView<asiakas> tableView;

    @FXML
    private TextField hakuTextField;

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


    ObservableList<asiakas> oblist = FXCollections.observableArrayList();





    public void initialize() {

        try {
            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();

            //Sql lause, jolla valitaan kaikki tiedot asiakas taulusta
            ResultSet rs = connectDB.createStatement().executeQuery("select * from asiakas");

            //Taulun kaikkien tietojen läpikäyminen ja lisääminen tableviewiin
            while(rs.next()) {
                oblist.add(new asiakas(rs.getInt("asiakas_id"),rs.getInt("postinro"),rs.getString("etunimi"),
                        rs.getString("sukunimi"),rs.getString("lahiosoite"),rs.getString("email"),rs.getString("puhelinnro")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //Scene builderin osien tietojen settaaminen tietokannasta
        asiakas_idColumn.setCellValueFactory(new PropertyValueFactory<asiakas,Integer>("asiakas_id"));
        postinroColumn.setCellValueFactory(new PropertyValueFactory<asiakas,Integer>("postinro"));
        etunimiColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("etunimi"));
        sukunimiColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("sukunimi"));
        lahiosoiteColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("lahiosoite"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("email"));
        puhelinnroColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("puhelinnro"));

        //Asetetaan kaikki taulun tiedot listaan
        tableView.setItems(oblist);

        naytaTiedot();


    }

    public void haku() {

        //Tyhjennetään lista
        oblist.clear();

        try {

            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();

            //Sql lause, jolla haetaan tietoja annetulla sanalla tai kirjaimella
            String sana = hakuTextField.getText();
            String query = "SELECT asiakas_id,postinro,etunimi,sukunimi,lahiosoite,email,puhelinnro FROM asiakas WHERE postinro LIKE '%"+ sana + "%' OR\n" +
                    "etunimi LIKE '%" + sana + "%' OR sukunimi LIKE '%" + sana + "%' OR lahiosoite LIKE '%" + sana + "%' OR email LIKE '%" + sana + "%' OR\n" +
                    "puhelinnro LIKE '%" + sana + "%'";

            ResultSet rs = connectDB.createStatement().executeQuery(query);

            //While loop, joka lisää kaikki löydetyt asiakkaat listaan
            while(rs.next()) {
                oblist.add(new asiakas(rs.getInt("asiakas_id"), rs.getInt("postinro"), rs.getString("etunimi"),
                        rs.getString("sukunimi"), rs.getString("lahiosoite"), rs.getString("email"), rs.getString("puhelinnro")));
            }

        } catch(SQLException throwables) {
            throwables.printStackTrace();
        }

        //Scene builderin osien tietojen settaaminen tietokannasta
        asiakas_idColumn.setCellValueFactory(new PropertyValueFactory<asiakas,Integer>("asiakas_id"));
        postinroColumn.setCellValueFactory(new PropertyValueFactory<asiakas,Integer>("postinro"));
        etunimiColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("etunimi"));
        sukunimiColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("sukunimi"));
        lahiosoiteColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("lahiosoite"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("email"));
        puhelinnroColumn.setCellValueFactory(new PropertyValueFactory<asiakas,String>("puhelinnro"));

        //Asetetaan kaikki taulun tiedot listaan
        tableView.setItems(oblist);
    }

    //Metodi, jolla näkee taulkon tiedot textfieldeissä
    public void naytaTiedot() {

        //Annetaan hiiren klikkaukselle tapahtuma
        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //Lause, jolla valitaan taulukosta kaikki rivit ja sarakkeet
                asiakas as = tableView.getItems().get(tableView.getSelectionModel().getSelectedIndex());

                //Näytetään taulukon tiedot omissa textfieldeissä
                postinumeroLabel.setText(as.getPostinro().toString());
                etunimiLabel.setText(as.getEtunimi());
                sukunimiLabel.setText(as.getSukunimi());
                lahiosoiteLabel.setText(as.getLahiosoite());

            }
        });
    }

    public void resetti() {
        postinumeroLabel.setText("Postinumero");
        etunimiLabel.setText("Etunimi");
        sukunimiLabel.setText("Sukunimi");
        lahiosoiteLabel.setText("Lähiosoite");
        hakuTextField.setText("");


    }

    public void laskuta() {


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/aktiivisetLaskut.fxml"));
            Parent root = loader.load();

            aktiivisetController aktiivisetController = loader.getController();

            aktiivisetController.vastaanOtaTiedot(etunimiLabel.getText());
            aktiivisetController.vastaanOtaTiedot(sukunimiLabel.getText());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("kokeilu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


















    //Metodi, jolla pääsee takaisin päänäyttöön
    private Stage stage;
    private Scene scene;
    private Parent root;

    //Luodaan uusi ikkuna, joka korvataan vanhalla
    public void switchToPaaNaytto(ActionEvent event) throws IOException {
        this.root = (Parent) FXMLLoader.load(this.getClass().getResource("resources/paaNaytto.fxml"));
        this.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    //Luodaan uusi ikkuna, joka korvataan vanhalla
    public void switchToAktiiviset(ActionEvent event) throws IOException {
        this.root = (Parent) FXMLLoader.load(this.getClass().getResource("resources/aktiivisetLaskut.fxml"));
        this.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.show();
    }

}
