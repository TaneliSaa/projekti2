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
    public TextArea txtkuvaus;
    public TextField tftyyppi;
    public TextField palvelu_id;
    public ObservableList<Integer>toimintaaluelista = FXCollections.observableArrayList(1,2,3);
    public ObservableList<String>palvelulista = FXCollections.observableArrayList();
    public TextField tfToimintaalue_id;
    public TableView tvpalvelu;
    public TableColumn tbctyyppi;
    public TableColumn tbchinta;
    public TableColumn tbcalv;
    public TableColumn tbckuvaus;
    public TableColumn tbctoimintaalue_id;
    public TableColumn tbcpalvelu_id;
    public TableColumn tbcnimi;

    //Metodi, jolla haetaan sql tietokannasta taulukon tiedot
    public void initialize() {

        Palvelu palvelu = new Palvelu();
        if (palvelu.listaaPalvelut() != null){
            lataaPalveluTaulu();
        }

        //Asetetaan kaikki taulun tiedot listaan
        tvpalvelu.setItems(palvelulista);

        //Kutsutaan metodia, jolla päivitetään tiedot automaattisesti
        naytaTiedot();

    }



    //Metodi, jolla päivitetään tauluntiedot
    public void paivitaTiedot() {

        try {
            //Lause, jolla tyhjennetään kaikki listan nykyiset tiedot
            palvelulista.clear();
            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();

            //SQL lause palveluiden luomiselle
            String query = "UPDATE palvelu SET toimintaalue_id = ?, nimi = ?, tyyppi = ?, kuvaus = ?, hinta = ?, alv = ?";

            PreparedStatement pst = connectDB.prepareStatement(query);
        } catch (Exception e) {

        }
        return;
    }

    //Palveluiden lisääminen sql palvelu tauluun
    public void btLisaa(){

        try {
            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();
            //Sql lause, jolla luodaan palvelut
            String query = "insert into palvelu (nimi,tyyppi,kuvaus,hinta,alv,toimintaalue_id)values(?,?,?,?,?,?)";
            //Preparedstatement, joka kertoo mitä pitää tehdä sql:ssä
            PreparedStatement pst = connectDB.prepareStatement(query);
            //Tietojen hakeminen textfieldeistä
            pst.setString(1,txtnimi.getText());
            pst.setString(2,tftyyppi.getText());
            pst.setString(3,txtkuvaus.getText());
            pst.setString(4,txthinta.getText());
            pst.setString(5,txtalv.getText());
            pst.setString(6,tfToimintaalue_id.getText());

            //Lause, jolla suoritetaan komennot sql:ssa
            pst.executeUpdate();

            //Kutsutaan metodia, jolla päivitetään tiedot automaattisesti
            paivitaTiedot();

        } catch(Exception e){
            System.err.println("vahinko");
            System.err.println(e.getMessage());
        }
    }

    public void lataaPalveluTaulu(){
        //testissä
        tvpalvelu.getItems().clear();
        Palvelu palveluidenhallinta = new Palvelu();
        List<Palvelu> palvelulista = palveluidenhallinta.listaaPalvelut();
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

            //Lause, joka suoritetaan sql:ssä
            /*ResultSet rs = connectDB.createStatement().executeQuery("select * from palvelu");

            //Taulun kaikkien tietojen läpikäyminen ja lisääminen tableviewiin
            while(rs.next()) {
                palvelulista.add(String.valueOf(new Palvelu(rs.getInt("toimintaalue_id"), rs.getString("nimi"),
                        rs.getInt("tyyppi"), rs.getString("kuvaus"), rs.getDouble("hinta"),rs.getDouble("alv"))));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }*/




    public void btPoista(){
        try {
            //Sql yhteyden määrittäminen
            connectionClass connectNow = new connectionClass();
            Connection connectDB = connectNow.getConnection();
            //Metodi, jolla pystytään suorittamaan sql komennot
            PreparedStatement pst;

            //Sql lause, jolla poistetaan palvelun tiedot taulusta
            String query = "DELETE FROM palvelu WHERE palvelu_id=?";

            //Suoritetaan sql komento DELETE
            pst = connectDB.prepareStatement(query);
            pst.setString(1,palvelu_id.getText());
            pst.execute();

            //Kutsutaan metodia, jolla päivitetään tiedot automaattisesti
            paivitaTiedot();
    }
        catch (Exception e) {
        }
    }

    public void btMuokkaa(){

    }

    //Metodi, jolla näkee taulukon tiedot textfieldeissä
    public void naytaTiedot() {

        //Annetaan hiiren klikkaukselle tapahtuma
        tvpalvelu.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //Lause, jolla valitaan taulukosta kaikki rivit ja sarakkeet
                Palvelu palv = (Palvelu) tvpalvelu.getItems().get(tvpalvelu.getSelectionModel().getSelectedIndex());

                //Näytetään taulukon tiedot omissa textfieldeissä
                txtnimi.setText(palv.getNimi());
                txthinta.setText(String.valueOf(palv.getHinta()));
                txtalv.setText(String.valueOf(palv.getAlv()));
                txtkuvaus.setText(palv.getKuvaus());
                tftyyppi.setText(String.valueOf(palv.getTyyppi()));
            }
        });
    }






    public palveluidenHallintaController() {
    }

    public void switchToPaaNaytto(ActionEvent event) throws IOException {
        this.root = (Parent)FXMLLoader.load(this.getClass().getResource("resources/paaNaytto.fxml"));
        this.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.show();
    }
}
