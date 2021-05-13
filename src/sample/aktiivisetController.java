package sample;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;

public class aktiivisetController {

    String file_path="D:\\Opiskelu\\OTI\\pdf\\";

    @FXML
    public Label etunimiLabel, sukunimiLabel, lahiosoiteLabel, postinumeroLabel, laskuIDLabel,
    viitenumeroLabel, erapaivaLabel, paivamaaraLabel, summaLabel, alkuaikaLabel, loppuaikaLabel, varausLabel;

    @FXML
    public void luoPdf() {

        try {
            String file_name = file_path + "lasku_" + laskuIDLabel.getText();
            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(file_name));

            document.open();

            Paragraph lasku = new Paragraph("LaskuID: \n " + laskuIDLabel.getText() + "\n Varaus: " + varausLabel.getText());
            document.add(lasku);
            document.add(new Paragraph(""));

            Paragraph asiakas = new Paragraph("Asiakas: \n " + etunimiLabel.getText() + " " + sukunimiLabel.getText()
                    + "\n" + lahiosoiteLabel.getText() + ", " + postinumeroLabel.getText());
            document.add(asiakas);
            document.add(new Paragraph(""));

            Paragraph ajanjakso = new Paragraph("Ajanjakso: " + alkuaikaLabel.getText() + " - " + loppuaikaLabel.getText());
            document.add(ajanjakso);
            document.add(new Paragraph(""));

            Paragraph tiedot = new Paragraph("Saaja: Village Newbies Oy \n FI05 4631 1666 2345 55 \n " + "Viitenumero:" + viitenumeroLabel.getText()
            + "\n Summa: " + summaLabel.getText() + "\n Päiväys: " + paivamaaraLabel.getText());
            document.add(tiedot);
            document.add(new Paragraph(""));

           PdfPTable table = new PdfPTable(3);
            PdfPCell c1 = new PdfPCell(new Phrase("Heading 1"));
            table.addCell(c1);

            PdfPCell c2 = new PdfPCell(new Phrase("Heading 2"));
            table.addCell(c2);

            PdfPCell c3 = new PdfPCell(new Phrase("Heading 3"));
            table.addCell(c3);

            table.addCell("1.0");
            table.addCell("1.1");
            table.addCell("1.2");
            table.addCell("2.1");
            table.addCell("2.2");
            table.addCell("2.3");

            document.close();

        } catch (Exception e) {
            System.err.println(e);
        }

    }

    /*
        Metodi, joka sulkee dialokin/ikkunan.
    */
    @FXML
    public void switchToLaskutus(ActionEvent event) throws IOException {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        }
}
