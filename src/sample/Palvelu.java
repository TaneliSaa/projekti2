package sample;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import sample.connectivity.connectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Palvelu {

    private String nimi;
    private int tyyppi;
    private double hinta;
    private double alv;
    private String kuvaus;
    private int toimintaalue_id;
    private int palvelu_id;

    public int getPalvelu_id() {
        return palvelu_id;
    }



    public Palvelu() {
    }

    public Palvelu(String nimi, int tyyppi, double hinta, double alv, int toimintaalue_id) {
        this.nimi = nimi;
        this.tyyppi = tyyppi;
        this.hinta = hinta;
        this.alv = alv;
        this.toimintaalue_id = toimintaalue_id;
    }

    /**
     * Palauttaa listan kaikista tietokannassa olevista Palvelu-olioista.
     *
     * @return Lista kaikista Palvelu-olioista.
     * @throws SQLException Heitetään, jos tietokannan kanssa kommunikoinnissa ilmenee ongelmia.
     */
    public List<Palvelu> listaa() throws SQLException {
        connectionClass connectNow = new connectionClass();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT * FROM palvelu";

        PreparedStatement lause = connectDB.prepareStatement(query);

        ResultSet tulokset = lause.executeQuery();

        // Jos kysely ei tuota tuloksia, palautetaan tyhjä
        // Samalla siirrytään ResultSet-olion ensimmäiselle riville

        if (!tulokset.next()) return null;

        List<Palvelu> palvelut = new ArrayList<>();

        // Ollaan jo ResultSet-olion ensimmäisellä rivillä. Rivin lukeminen täytyy tapahtua ennen seuraavaa
        // tulokset.next()-metodikutsua!

        do {
            Palvelu palvelu = luoPalveluTuloksista(tulokset);
            palvelut.add(palvelu);
        } while (tulokset.next());

        tulokset.close();
        lause.close();
        //yhteys.close();

        return palvelut;

    }

    private Palvelu luoPalveluTuloksista(ResultSet tulokset) throws SQLException {
        var palvelu = new Palvelu();
        palvelu.setPalvelu_id(tulokset.getInt("palvelu_id"));
        palvelu.setToimintaalue_id(tulokset.getInt("toimintaalue_id"));
        palvelu.setNimi(tulokset.getString("nimi"));
        palvelu.setTyyppi(tulokset.getInt("tyyppi"));
        palvelu.setKuvaus(tulokset.getString("kuvaus"));
        palvelu.setHinta(tulokset.getDouble("hinta"));
        palvelu.setAlv(tulokset.getDouble("alv"));

        return palvelu;
    }

    public List<Palvelu> listaaPalvelut() {
        try {
            return listaa();
        } catch (SQLException e) {

            e.printStackTrace();

            return null;
        }
    }

    public void poistaPalvelu(Integer id) throws SQLException{
        connectionClass connectNow = new connectionClass();
        Connection connectDB = connectNow.getConnection();

        String query = "DELETE FROM palvelu WHERE palvelu_id =?";

        PreparedStatement lause = connectDB.prepareStatement(query);
        lause.setInt(1, id);

        lause.executeUpdate();
        lause.close();

    }


    public void setPalvelu_id(int palvelu_id) {
        this.palvelu_id = palvelu_id;
    }



    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public int getTyyppi() {
        return tyyppi;
    }

    public void setTyyppi(int tyyppi) {
        this.tyyppi = tyyppi;
    }

    public double getHinta() {
        return hinta;
    }

    public void setHinta(double hinta) {
        this.hinta = hinta;
    }

    public double getAlv() {
        return alv;
    }

    public void setAlv(double alv) {
        this.alv = alv;
    }

    public int getToimintaalue_id() {
        return toimintaalue_id;
    }

    public void setToimintaalue_id(int toimintaalue_id) {
        this.toimintaalue_id = toimintaalue_id;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }


}






