package ua.edu.dnu.booksales.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.Chart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import ua.edu.dnu.booksales.util.PropertiesManager;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Path;


public class PDFCreator {
    private final File directory;
    private final Document document;
    private File file;

    public PDFCreator(File directory)  {
        this.directory = directory;
        document = new Document();
    }

    public void create(String name) throws FileNotFoundException, DocumentException {
        String path = getFilePath(name, ".pdf");
        file = new File(path);
        OutputStream out = new FileOutputStream(file);
        PdfWriter.getInstance(document, out);
        document.open();
    }

    private String getFilePath(String name, String format){
        long time = System.currentTimeMillis();
        String dir = directory.getPath();
        String id = format.equals(".pdf") ? "_" + time : "";
        return dir + "/" + name + id + format;
    }

    public void addTitle(String title) throws DocumentException {
        Paragraph p = new Paragraph(title, Fonts.TITLE_FONT);
        p.setAlignment(Element.ALIGN_CENTER);
        document.add(new Paragraph("\n"));
        document.add(p);
    }

    public void addText(String text) throws DocumentException {
        Chunk chunk = new Chunk(text, Fonts.TEXT_FONT);
        document.add(chunk);
    }

    private static final double MAX_CHART_WIDTH = 500;
    private static final double MAX_CHART_HEIGHT = 400;
    public void addChart(Chart chart) throws IOException, DocumentException {
        double maxWidth = chart.getMaxWidth();
        double maxHeight = chart.getMaxHeight();
        String path = getFilePath(chart.getTitle(), ".png");
        File file = new File(path);
        chart.setMaxSize(MAX_CHART_WIDTH, MAX_CHART_HEIGHT);
        WritableImage image = chart.snapshot(new SnapshotParameters(), null);
        chart.setMaxSize(maxWidth, maxHeight);
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        addImage(file.toPath());
    }

    public void addImage(Path path) throws DocumentException, IOException {
        Image img = Image.getInstance(path.toAbsolutePath().toString());
        document.add(img);
    }

    public <T> void addTable(TableView<T> table) throws DocumentException {
        ObservableList<TableColumn<T, ?>> columns = table.getColumns();
        PdfPTable pdfTable = new PdfPTable(columns.size());

        for (TableColumn<T, ?> column : columns) {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setPhrase(new Phrase(column.getText(), Fonts.TEXT_FONT));
            pdfTable.addCell(header);
        }

        for (T item : table.getItems()) {
            for (TableColumn<T, ?> column : columns) {
                String value = column.getCellObservableValue(item).getValue().toString();
                pdfTable.addCell(new Phrase(value, Fonts.TABLE_ITEM_FONT));
            }
        }
        document.add(pdfTable);
    }

    public void save() {
        document.close();
    }

    public void saveAndShow(){
        document.close();
        HostServices hostServices = (HostServices)Stage.getWindows().get(0).getUserData();
        hostServices.showDocument(file.getAbsolutePath());
    }
}
