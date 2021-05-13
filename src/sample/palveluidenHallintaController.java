package sample;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sample.connectivity.connectionClass;

public class palveluidenHallintaController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    public ComboBox cboxtoimintaalue;
    public TextField txtnimi;
    public TextField txthinta;
    public TextField txtalv;
    public TextField txtkuvaus;
    public TextField tftyyppi;
    public TextField palvelu_id;
    public ObservableList<Integer> toimintaaluelista = FXCollections.observableArrayList(1, 2, 3);
    //public ObservableList<String>palvelulista = FXCollections.observableArrayList();
    public TextField tfToimintaalue_id;
    public TableView tvpalvelu;
    public TableColumn tbctyyppi;
    public TableColumn tbchinta;
    public TableColumn tbcalv;
    public TableColumn tbckuvaus;
    public TableColumn tbctoimintaalue_id;
    public TableColumn tbcpalvelu_id;
    public TableColumn tbcnimi;

    public void initialize() {
        Palvelu palvelu = new Palvelu();
        if (palvelu.listaaPalvelut() != null) {
            lataaPalvelutaulu();
        }
    }

    public void PaivitaPalvelu() {

        try {
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();
            String query = "UPDATE palvelu SET toimintaalue_id = ?, nimi = ?, tyyppi = ?, kuvaus = ?, hinta = ?, alv = ? WHERE palvelu_id = ?";

            PreparedStatement pst = connectDB.prepareStatement(query);
        } catch (Exception e) {
        }
        return;
    }

    //palveluiden lisääminen tauluun
    public void btLisaa() {

        try {
            //SQL yhteys määritetään

            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();

            String query = "INSERT INTO palvelu (toimintaalue_id, nimi, tyyppi, kuvaus, hinta, alv)values(?,?,?,?,?,?)";

            PreparedStatement sqlpalvelu = connectDB.prepareStatement(query);

            sqlpalvelu.setInt(1, Integer.parseInt(tfToimintaalue_id.getText()));
            sqlpalvelu.setString(2, txtnimi.getText());
            sqlpalvelu.setInt(3, Integer.parseInt(tftyyppi.getText()));
            sqlpalvelu.setString(4, txtkuvaus.getText());
            sqlpalvelu.setDouble(5, Double.parseDouble(txthinta.getText()));
            sqlpalvelu.setDouble(6, Double.parseDouble(txtalv.getText()));

            sqlpalvelu.executeUpdate();

            PaivitaPalvelu();

            Palvelu palvelu = new Palvelu();
            if (palvelu.listaaPalvelut() != null) {
                lataaPalvelutaulu();
            }

        } catch (Exception e) {

        }
    }

    public void lataaPalvelutaulu() {
        tvpalvelu.getItems().clear();
        Palvelu palveluhallinta = new Palvelu();
        List<Palvelu> palvelulista = palveluhallinta.listaaPalvelut();
        ObservableList<Palvelu> taulunpalvelut = FXCollections.observableArrayList(palvelulista);

        tbcpalvelu_id.setCellValueFactory(
                new PropertyValueFactory<Palvelu, Integer>("palvelu_id"));
        tbctoimintaalue_id.setCellValueFactory(
                new PropertyValueFactory<Palvelu, Integer>("toimintaalue_id"));
        tbcnimi.setCellValueFactory(
                new PropertyValueFactory<Palvelu, String>("nimi"));
        tbctyyppi.setCellValueFactory(
                new PropertyValueFactory<Palvelu, Integer>("tyyppi"));
        tbckuvaus.setCellValueFactory(
                new PropertyValueFactory<Palvelu, String>("kuvaus"));
        tbchinta.setCellValueFactory(
                new PropertyValueFactory<Palvelu, Double>("hinta"));
        tbcalv.setCellValueFactory(
                new PropertyValueFactory<Palvelu, Double>("alv"));
        tvpalvelu.setItems(taulunpalvelut);
    }


    public palveluidenHallintaController() {
    }

    public void switchToPaaNaytto(ActionEvent event) throws IOException {
        this.root = (Parent) FXMLLoader.load(this.getClass().getResource("resources/paaNaytto.fxml"));
        this.stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    public void btPoista() throws SQLException {

        Palvelu poistapalvelu = new Palvelu();
        Palvelu valittupalvelu = (Palvelu) tvpalvelu.getSelectionModel().getSelectedItem();
        Palvelu palvelu = new Palvelu();
        palvelu.setPalvelu_id(valittupalvelu.getPalvelu_id());
        poistapalvelu.poistaPalvelu(palvelu.getPalvelu_id());
        //tyhjennaTekstiKentat_palvelu();
        lataaPalvelutaulu();
    }


    public void btMuokkaa() throws SQLException {

        /*try {
            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();

            //Metodi, jolla pystytään suorittamaan sql komennot
            PreparedStatement pst;

            //Tietojen näyttäminen textfieldeissä
            String rivi1 = tfToimintaalue_id.getText();
            String rivi2 = txtnimi.getText();
            String rivi3 = tftyyppi.getText();
            String rivi4 = txtkuvaus.getText();
            String rivi5 = txthinta.getText();
            String rivi6 = txtalv.getText();


            //Sql lause, jolla päivitetään asiakkaan tietoja taulussa
            String query = "UPDATE palvelu set toimintaalue_id= '"+rivi1+"',nimi='"+rivi2+"',tyyppi='"+rivi3+"',kuvaus='"+rivi4+"',hinta='"+rivi5+"',alv='"+rivi6
                    +"' where palvelu_id='"+rivi1+"'";

            //Lause, jolla suoritetaan komennot sql:ssa
            pst = connectDB.prepareStatement(query);
            pst.execute();

            //Kutsutaan metodia, jolla päivitetään tiedot automaattisesti
            paivitaTiedot();


        } catch (Exception e) {
        }
    }*/
    }
}
