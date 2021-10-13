package controlador;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.sun.javafx.font.FontConstants;
import entity.FichausuariosEntity;
import javafx.collections.ObservableList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class GeneratePDFFileIText extends controladorHibernateApp {
        public static final String DEST = "./results/informePDF.pdf";
        public static final String DATOS = "C:\\Users\\Eros\\Desktop\\dataUsuarios.txt";
        public static ObservableList observableList;

        public static void main(String[] args) throws IOException{
            File file = new File(DEST);
            file.getParentFile().mkdirs();
            new GeneratePDFFileIText().createPDF(DEST, observableList);
        }

        public void createPDF(String destino, ObservableList<FichausuariosEntity> listado) throws IOException{
            // Convertir ObservableList listado a String
            System.out.println(listado.toString() + "\n");
            ArrayList<String> listadoToList = new ArrayList<String>();
            for(int i =0 ; i < listado.size(); i++)
            {
                listadoToList.add(listado.get(i).toString());
            }
            System.out.println(listadoToList);

            FileOutputStream fileOutputStream = new FileOutputStream(DEST);
            PdfWriter pdfWriter = new PdfWriter(DEST);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document documento = new Document(pdfDocument, PageSize.A4.rotate());
            documento.setMargins(20,40,20,40);

            FontProgram fontProgram = FontProgramFactory.createFont("B:\\Imágenes\\Lettering\\fonts\\coolvetica rg.ttf");
            PdfFont coolveticaFont = PdfFontFactory.createFont(fontProgram, PdfEncodings.WINANSI);

            documento.add(new Paragraph("Lista de usuarios by Eros").setFont(coolveticaFont).setBold());

            Table tabla = new Table(new float[]{3,5,3}); // número de columnas con su ancho, que es el tamaño del número
            //tabla.setWidth(100); // Le indico que debe utilizar el 100% de los márgenes

            BufferedReader bufferedReader = new BufferedReader(new FileReader(DATOS));
            String linea = bufferedReader.readLine();

            procesoRellenarPDF(tabla, linea, coolveticaFont,true);

            while ((linea = bufferedReader.readLine())!= null){
                procesoRellenarPDF(tabla, linea, coolveticaFont, false);
            }

//            Cell headerCell = new Cell(1,1).setTextAlignment(TextAlignment.CENTER);
//            tabla.addHeaderCell(headerCell.add(new Paragraph("header cell")));

//            Cell celda = new Cell(1, 1).setVerticalAlignment(VerticalAlignment.MIDDLE);
//            tabla.addCell(celda);
//            tabla.addCell(new Paragraph("Cell 1.1"));
//            tabla.addCell(new Cell());
//            tabla.addCell(new Cell().setMargin(1));
//            tabla.addCell(new Cell().setPadding(1));
//            tabla.addCell(celda);

            bufferedReader.close();
            documento.add(tabla);
            documento.close();
        }

        public void procesoRellenarPDF(Table table, String lineaTexto, PdfFont fuente, boolean isHeader){
            StringTokenizer tokenizer = new StringTokenizer(lineaTexto, ",");
            while (tokenizer.hasMoreTokens()){
                if (isHeader){
                    table.addHeaderCell(new Cell().add(new Paragraph(tokenizer.nextToken()).setFont(fuente)).setBold());
                } else {
                    table.addCell(new Cell().add(new Paragraph(tokenizer.nextToken())));
                }
            }
        }
}
