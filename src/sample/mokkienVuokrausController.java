package sample;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.connectivity.connectionClass;

public class mokkienVuokrausController implements Initializable {

    public mokkienVuokrausController() {
    }

    // Alustetaan connectClass-luokan olio, jolla yhdistetään sovellus tietokantaan.
    private final connectionClass connectNow = new connectionClass();
    private final Connection connectDB = connectNow.getConnection();

    // Tehdään mukautetut näppäimet dialokeihin
    ButtonType jatka = new ButtonType("Jatka", ButtonBar.ButtonData.OK_DONE);
    ButtonType peruuta = new ButtonType("Peruuta", ButtonBar.ButtonData.CANCEL_CLOSE);

    @FXML
    private Label lblHallintaNotification;
    @FXML
    TextField varausIDHaku, asiakasIDHaku, mokkiNimiHaku;
    @FXML
    Button haeMokki, haeKaikki, varausMuokkaa, varausPoista;
    @FXML
    ChoiceBox<String> cbAlue;
    @FXML
    TableView<Vuokraus> mokkiTiedot;
    @FXML
    TableColumn<Mokki, Integer> idColumn;
    @FXML
    TableColumn<Mokki, String> varattupvmColumn, vahvistuspvmColumn, varauksenalkupvmColumn, varauksenloppupvmColumn, asiakasidColumn, mokkinimiColumn, alueColumn, kestoColumn;

    // Alustetaan taulukkoon sarakkeet
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        idColumn.setCellValueFactory(new PropertyValueFactory<>("varaus_id"));
        varattupvmColumn.setCellValueFactory(new PropertyValueFactory<>("varattu_pvm"));
        vahvistuspvmColumn.setCellValueFactory(new PropertyValueFactory<>("vahvistus_pvm"));
        varauksenalkupvmColumn.setCellValueFactory(new PropertyValueFactory<>("varattu_alkupvm"));
        varauksenloppupvmColumn.setCellValueFactory(new PropertyValueFactory<>("varattu_loppupvm"));
        asiakasidColumn.setCellValueFactory(new PropertyValueFactory<>("asiakas_id"));
        mokkinimiColumn.setCellValueFactory(new PropertyValueFactory<>("mokkinimi"));
        alueColumn.setCellValueFactory(new PropertyValueFactory<>("nimi"));
        kestoColumn.setCellValueFactory(new PropertyValueFactory<>("kestoaika"));

        mokkiTiedot.setRowFactory(tv -> new TableRow<Vuokraus>() {
            @Override
            public void updateItem(Vuokraus item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("");
                } else if (item.getVahvistus()) {
                    setStyle("-fx-background-color: LimeGreen;");
                } else if (!item.getVahvistus()) {
                    setStyle("-fx-background-color: yellow;");
                    if (LocalDate.now().compareTo(LocalDate.parse(item.getVahvistus_pvm())) > 0) {
                        setStyle("-fx-background-color: red;");
                    }
                } else {
                    setStyle("");
                }
            }
        });

        haeKaikki("alueet");
    }

    private void tekstinTasaus(TableColumn<Mokki, String> col) {
        col.setCellFactory(tc -> {
            TableCell<Mokki, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(col.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
    }
    @FXML
    public void pudotusValikko(MouseEvent event) {

        ObservableList<String> toimintaAlueet = haeToimintaAlueet();
        cbAlue.setItems(toimintaAlueet);
        cbAlue.setValue("Valitse toiminta-alue");
    }

    @FXML
    public void haeKaikki(String haettava) {

        ObservableList<String> alueet = haeToimintaAlueet();

        String query = "SELECT v.varaus_id, v.varattu_pvm, v.vahvistus_pvm, v.varattu_alkupvm, " +
                "v.varattu_loppupvm, v.asiakas_id, v.vahvistettu, m.mokkinimi, ta.nimi " +
                "FROM varaus v INNER JOIN mokki m ON v.mokki_mokki_id = m.mokki_id INNER JOIN toimintaalue ta ON m.toimintaalue_id = ta.toimintaalue_id";

        switch (haettava) {
            case "kaikki":
                mokkiHaku(query);
                break;
            case "mokit":
                mokkiHaku(query);
                break;
            case "alueet":
                break;
        }
    }

    @FXML
    public ObservableList<String> haeToimintaAlueet() {

        ObservableList<String> toimintaAlueet = FXCollections.observableArrayList();

        String query = "SELECT * FROM toimintaalue"; // SQL-lause kaikkien toiminta-alueiden hakemiseen.

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(query);

            while (queryResult.next()) {
                toimintaAlueet.add(queryResult.getString("nimi"));
            }
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return toimintaAlueet;
    }

    public void mokkiHaku(String SQL) {

        ObservableList<Vuokraus> vuokraus = FXCollections.observableArrayList();
        try {
            PreparedStatement preparedStmt = connectDB.prepareStatement(SQL);
            ResultSet queryResult = preparedStmt.executeQuery(SQL);
            System.out.println(queryResult);

            while (queryResult.next()) {
                String mokkinimi = queryResult.getString("m.mokkinimi");
                String toimintaalue = queryResult.getString("ta.nimi");
                int varaus_id = queryResult.getInt("v.varaus_id");
                String varattupvm = queryResult.getDate("v.varattu_pvm").toString();
                String vahvistuspvm = queryResult.getDate("v.vahvistus_pvm").toString();
                String varattualkupvm = queryResult.getDate("v.varattu_alkupvm").toString();
                String varattuloppupvm = queryResult.getDate("v.varattu_loppupvm").toString();
                int asiakasid = queryResult.getInt("v.asiakas_id");
                boolean vahvistettu = queryResult.getBoolean("v.vahvistettu");
                long kesto = Math.abs(queryResult.getDate("v.varattu_loppupvm").getTime() - queryResult.getDate("v.varattu_alkupvm").getTime());
                long kestoaika = (TimeUnit.DAYS.convert(kesto, TimeUnit.MILLISECONDS));

                System.out.println(kestoaika);

                vuokraus.add(new Vuokraus(varaus_id, varattupvm, vahvistuspvm, varattualkupvm, varattuloppupvm, asiakasid, mokkinimi, toimintaalue, vahvistettu, kestoaika));

            }
            mokkiTiedot.setItems(vuokraus);
            preparedStmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void haeKaikkiButtonOnAction() {
        haeKaikki("kaikki");
    }

    @FXML
    public void haeEhdoilla() {

        int id = -1;
        int omistaja = -1;
        String alue = cbAlue.getValue();
        String nimi = mokkiNimiHaku.getText();

        try {
            id = Integer.parseInt(varausIDHaku.getText().trim());
        } catch (NumberFormatException ignored) {
        }

        try {
            omistaja = Integer.parseInt(asiakasIDHaku.getText().trim());
        } catch (NumberFormatException ignored) {
        }

        String query = "SELECT v.varaus_id, v.varattu_pvm, v.vahvistus_pvm, v.varattu_alkupvm, v.varattu_loppupvm, v.asiakas_id, " +
                "v.vahvistettu, m.mokkinimi, ta.nimi FROM varaus v INNER JOIN mokki m ON v.mokki_mokki_id = m.mokki_id INNER JOIN " +
                "toimintaalue ta ON m.toimintaalue_id = ta.toimintaalue_id WHERE ";

        if (id > 0) {
            query += "v.varaus_id = " + id;
            if (omistaja > 0) {
                query += " AND v.asiakas_id = " + omistaja;
            }
            if (!alue.equals("Valitse toiminta-alue")) {
                query += " AND ta.nimi = '" + alue + "'";
            }
            if (!nimi.equals("")){
                query += " AND m.mokkinimi = '" + nimi + "'";
            }
        }
        else if (omistaja > 0) {
            query += "v.asiakas_id = " + omistaja;
            if (!alue.equals("Valitse toiminta-alue")) {
                query += " AND ta.nimi = '" + alue + "'";
            }
            if (!nimi.equals("")){
                query += " AND m.mokkinimi = '" + nimi + "'";
            }
        }
        else if (!alue.equals("Valitse toiminta-alue")) {
            query += "ta.nimi = '" + alue + "'";
            if (!nimi.equals("")){
                query += " AND m.mokkinimi = '" + nimi + "'";
            }
        }
        else if (!nimi.equals("")){
            query += "m.mokkinimi = '" + nimi + "'";
        }

        if (id > 0 || omistaja > 0 || !alue.equals("Valitse toiminta-alue") || !nimi.equals("")) {
            mokkiHaku(query);
        } else {
            System.out.println("Haku epäonnistui");
        }
    }


    /*
        Muokkaa-napin toiminto kutsuu metodin, joka avaa dialokin taulukosta valitun mökin tietojen muuttamiseksi.
        Jos mitään mökkiä ei ole valittu, metodi ei tee mitään.
     */
    @FXML
    public void mokinMuokkaus(ActionEvent event) throws IOException {
        if (mokkiTiedot.getSelectionModel().getSelectedItem() != null) {
            openDialog("update", "Muuta vuokrauksen tietoja");
        }
    }

    /*
        Poista-napin toiminto, jolla voidaan poistaa valittu mökki tietokannasta.
        Jos mitään mökkiä ei ole valittu, metodi ei tee mitään. Sisältää vahvistus-dialokin, josta toiminnon voi perua.
     */

    @FXML
    public void mokinPoisto(ActionEvent event) {

        if (mokkiTiedot.getSelectionModel().getSelectedItem() != null) {
            Vuokraus vuokraus = mokkiTiedot.getSelectionModel().getSelectedItem();

            int delete_mokki_id = vuokraus.getVaraus_id();

            vahvistusPoistolle(delete_mokki_id);
        }
    }

    @FXML
    public void openDialog(String toiminto, String otsikko) throws IOException {

        if (toiminto.equals("update")) {
            Vuokraus vuokraus = mokkiTiedot.getSelectionModel().getSelectedItem();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("resources/paivitaVuokraus.fxml"));
            Parent parent = fxmlLoader.load();
            vuokrausDialogController controller = fxmlLoader.getController();
            controller.setVuokrausController(this);
            controller.setVuokrausObservableList(vuokraus);
            Scene scene = new Scene(parent, 700, 230);
            Stage stage = new Stage();
            stage.setTitle(otsikko);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.getScene().getWindow().centerOnScreen();
            stage.showAndWait();
        }
    }

    /*
        Metodi hakee tietokannasta toiminta-alueiden nimet ja palauttaa ne listana.
     */

    private void vahvistusPoistolle(int id) {

        // Muodostetaan SQL-lause mökin tai toimialueen poistoon.
        String delQuery;

        delQuery = "DELETE FROM varaus WHERE varaus_id = " + id;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", jatka, peruuta);
        alert.setTitle("Vahvistaminen");
        alert.getDialogPane().setHeaderText("Haluatko varmasti poistaa valinnan: " + id + "?");
        alert.getDialogPane().setContentText("Valitsemasi kohde poistuu tietokannasta.");
        Optional<ButtonType> confirmationResult = alert.showAndWait();
        if (confirmationResult.isPresent()) {
            if (confirmationResult.get() == jatka) {
                try {
                    PreparedStatement preparedStmt = connectDB.prepareStatement(delQuery);
                    preparedStmt.execute();

                    System.out.println("Mökki (Id: " + id + ") on poistettu tietokannasta!");
                    // Haetaan lopuksi kaikki mökit tauluun
                    // TODO hae vain saman alueen mökit?
                    haeKaikki("mokit");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (confirmationResult.get() == peruuta) {
                System.out.println("Poisto peruutettu!");
            }
        }
    }

    @FXML
    public void mokinVahvistus() {

        if (mokkiTiedot.getSelectionModel().getSelectedItem() != null) {
            Vuokraus vuokraus = mokkiTiedot.getSelectionModel().getSelectedItem();

            boolean vahvistaVaraus = vuokraus.getVahvistus();


            if (!vahvistaVaraus) {
                String updQuery = "UPDATE varaus SET vahvistettu = true WHERE varaus_id = " + vuokraus.getVaraus_id();

                try {
                    PreparedStatement preparedStmt = connectDB.prepareStatement(updQuery);

                    int rowsUpdated = preparedStmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        // lblHallintaNotification.setText("Varaus vahvistettiin onnistuneesti!");
                        System.out.println("Varaus vahvistettiin onnistuneesti!");
                        haeKaikki("mokit");
                    }
                    preparedStmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToPaaNaytto(ActionEvent event) throws IOException {
        this.root = (Parent) FXMLLoader.load(this.getClass().getResource("resources/paaNaytto.fxml"));
        this.stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        this.scene = new Scene(this.root);
        this.stage.setScene(this.scene);
        this.stage.show();
    }
}
