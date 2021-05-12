package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.connectivity.connectionClass;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class varausDialogController implements Initializable {

    private boolean lippu;
    private int asiakas_id;
    private int mokki_id;
    private int vahvistus;

    // Alustetaan connectClass-luokan olio, jolla yhdistetään sovellus tietokantaan.
    private final connectionClass connectNow = new connectionClass();
    private final Connection connectDB = connectNow.getConnection();

    @FXML
    private DatePicker datePickerAlku, datePickerLoppu;
    @FXML
    private TextField tfNimi, tfVahvistus;
    public paaNayttoController controller;
    public Label lblHallintaNotification, lblMokki, lblMokkiOsoite, lblHinta, lblOmistaja, lblAsiakasEmail, lblAsiakas,
            lblAsiakasPuhelin, lblValidateAsiakas, lblValidatePaivamaara, lblValidateVahvistus;

    public void setMokkiController(paaNayttoController controller) {
        this.controller = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePickerAlku.setValue(LocalDate.now());
        datePickerLoppu.setValue(LocalDate.now());
        datePickerAlku.setShowWeekNumbers(false);
        datePickerLoppu.setShowWeekNumbers(false);
    }

    public void setMokitObservableList(Mokki mokki) {

        String omistajaQuery = "SELECT etunimi, sukunimi FROM asiakas WHERE asiakas_id = " + mokki.getMokki_omistajaid();

        this.mokki_id = mokki.getMokki_id();
        lblMokki.setText(mokki.getMokki_nimi() + " (Id: " + this.mokki_id + ")");
        lblMokkiOsoite.setText(mokki.getMokki_osoite() + ", " + mokki.getMokki_postinro());
        lblHinta.setText(mokki.getMokki_hinta() + "€/vrk, " + mokki.getMokki_henkilot() + " henkilöä");

        try {
            PreparedStatement preparedStmt = connectDB.prepareStatement(omistajaQuery);
            ResultSet queryResult = preparedStmt.executeQuery(omistajaQuery);

            while (queryResult.next()) {
                lblOmistaja.setText(queryResult.getString("etunimi") + " " + queryResult.getString("sukunimi") +
                        " (Id: " + mokki.getMokki_omistajaid() + ")");
            }
            preparedStmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void haeAsiakas(ActionEvent event) {

        if (tfNimi.getText().trim().isEmpty()) {
            lblAsiakas.setText("");
            lblAsiakasEmail.setText("Asiakasta ei löydy!");
            lblAsiakasPuhelin.setText("");
        } else {
            String asiakasQuery = "SELECT asiakas_id, etunimi, sukunimi, email, puhelinnro FROM asiakas WHERE " +
                    "asiakas_id = " + tfNimi.getText();
            try {
                PreparedStatement preparedStmt = connectDB.prepareStatement(asiakasQuery);
                ResultSet queryResult = preparedStmt.executeQuery(asiakasQuery);

                while (queryResult.next()) {
                    lblAsiakas.setText(queryResult.getString("etunimi") + " " + queryResult.getString("sukunimi"));
                    lblAsiakasEmail.setText(queryResult.getString("email"));
                    lblAsiakasPuhelin.setText(queryResult.getString("puhelinnro"));
                }
                preparedStmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Metodi peruuta-napin toiminnolle.
*/
    @FXML
    private void peruutaToiminto(ActionEvent event) {
        closeStage(event);
        lblHallintaNotification.setText("Varauksen tekeminen peruutettu!");
        System.out.println("Varauksen tekeminen peruutettu!");
    }

    /*
        Metodi, joka sulkee dialokin/ikkunan.
    */
    private void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void btnLisaaVaraus(ActionEvent event) {

        System.out.println("Lisää-nappia painettu 'Tee varaus'-dialokissa.");
        this.lippu = false;

        java.sql.Date alkupvm = java.sql.Date.valueOf(datePickerAlku.getValue());
        java.sql.Date loppupvm = java.sql.Date.valueOf(datePickerLoppu.getValue());

        vahvistusPvm();
        validateAsiakas();
        vahvistusPvm();
        validatePaivamaarat();

        if (!lippu) {
            try {
                String insQuery = "INSERT INTO varaus (asiakas_id, mokki_mokki_id, varattu_pvm, vahvistus_pvm, varattu_alkupvm, varattu_loppupvm) " +
                        "VALUES (" + this.asiakas_id + ", " + this.mokki_id + ", CURDATE(), " + "DATE_SUB('" + alkupvm + "', INTERVAL " + this.vahvistus +
                        " DAY), '" + alkupvm + "', '" + loppupvm + "')";

                System.out.println(insQuery);
                PreparedStatement preparedStmt = connectDB.prepareStatement(insQuery);

                // Suoritetaan tietokantaan lisäys.
                int rowsUpdated = preparedStmt.executeUpdate();
                if (rowsUpdated > 0) {
                    lblHallintaNotification.setText("Varaus tehtiin onnistuneesti!");
                    System.out.println("Varaus tehtiin onnistuneesti!");
                }
                preparedStmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            closeStage(event);
        }
    }

    private void validateAsiakas() {

        if (tfNimi.getText() == null || tfNimi.getText().trim().isEmpty()) {
            this.lippu = true;
            lblValidateAsiakas.setText("Asiakasta ei ole annettu!");
        } else {
            try {
                this.asiakas_id = Integer.parseInt(tfNimi.getText().trim());
                if (checkAsiakasId(asiakas_id)) {
                    lblValidateAsiakas.setText("");
                } else {
                    this.lippu = true;
                    lblValidateAsiakas.setText("Asiakasta ei löydy tietokannasta!");
                }
            } catch (NumberFormatException e) {
                this.lippu = true;
            }
        }
    }

    private boolean checkAsiakasId(int asiakas_id) {

        String query = "SELECT asiakas_id FROM asiakas WHERE asiakas_id = '" + asiakas_id + "'";
        boolean result = false;

        try {
            PreparedStatement preparedStmt = connectDB.prepareStatement(query);
            ResultSet queryResult = preparedStmt.executeQuery(query);
            result = queryResult.next(); // True, jos query palauttaa rivin.
            preparedStmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void vahvistusPvm() {

        if (tfVahvistus.getText() != null || !tfVahvistus.getText().trim().isEmpty()) {
            try {
                this.vahvistus = Integer.parseInt(tfVahvistus.getText().trim());
                if (this.vahvistus < 0) {
                    lblValidateVahvistus.setText("Vahvistus pvm täytyy olla positiivinen kokonaisluku tai nolla!");
                    this.lippu = true;
                }
            } catch (Exception ignored) {
            }
        } else {
            this.vahvistus = 5;
        }
    }

    private void validatePaivamaarat() {

        if ((datePickerAlku.getValue().compareTo(datePickerLoppu.getValue()) > 0)
                || (datePickerAlku.getValue().compareTo(LocalDate.now()) < 0)
                || (datePickerLoppu.getValue().compareTo(LocalDate.now()) < 0)) {
            lblValidatePaivamaara.setText("Tarkista päivämäärät!");
            this.lippu = true;
        } else {
            lblValidatePaivamaara.setText("");
        }
    }
}
