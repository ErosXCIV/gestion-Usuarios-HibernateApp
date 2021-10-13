package controlador;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import entity.FichausuariosEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import javafx.stage.Modality;
import javafx.stage.Stage;
import org.hibernate.exception.DataException;

import javax.persistence.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.itextpdf.*;

public class controladorHibernateApp implements Initializable {
    public TextField textFieldBuscar;
    public Button buttonModificar;
    public Button buttonNuevo;
    public Button buttonRefresh;
    public ImageView imageViewRefresh;

    // Columnas con <FichausuariosEntity, String> como modelo
    public TableView<FichausuariosEntity> tableViewUsuarios;
    public TableColumn<FichausuariosEntity, String> columnNombre;
    public TableColumn<FichausuariosEntity, String> columnEmail;
    public TableColumn<FichausuariosEntity, Enum> columnServicio;
    public TableColumn<FichausuariosEntity, Integer> columnID;

    // Un ArrayList pero para JavaFX
    public ObservableList<FichausuariosEntity> usuarios = FXCollections.observableArrayList();
    public ObservableList<FichausuariosEntity> filtroUsuarios = FXCollections.observableArrayList();
    public List usuariosDTO = FXCollections.observableArrayList();


    // Entity Manager
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();


    // PDF File
    public static final String DEST = "./results/informePDF.pdf";


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cargarDatosTableView();

        //buttonRefresh.setStyle("-fx-focus-color: transparent;");
// VERSION MODERNA JPA 2.1 PERO FALTA CONVERTIR LIST A OBSERVABLE LIST - NO FUNCIONA
        // Relacionamos cada columna del TableView del diseño de la App al atributo correspondiente
//        columnNombre.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
//        columnEmail.setCellValueFactory((new PropertyValueFactory<>("Email")));
//        columnServicio.setCellValueFactory(new PropertyValueFactory<>("paqServicios"));
//
//        try {
//            transaction.begin();
//
//            Query cargarTabla = entityManager.createNativeQuery("SELECT Nombre AS 'nombre', Email AS 'email', PaqServicios AS 'paqservicios' FROM listausuarios.fichausuarios");
//
//
//            Query query = entityManager.createNativeQuery(
//                    "SELECT Nombre AS 'nombre', Email AS 'email', PaqServicios AS 'paqservicios' FROM listausuarios.fichausuarios",
//                    "FichausuariosEntityMapping");
//
//            usuariosDTO = query.getResultList();
//            tableViewUsuarios.setItems((ObservableList<FichausuariosEntity>) usuariosDTO);
//
//            transaction.commit();
//        } catch (DataException e) {
//            System.out.println("ERROR:" + e);
//
//        } finally {
//            if (transaction.isActive()) {
//                transaction.rollback();
//            }
//            entityManager.close();
//            entityManagerFactory.close();
//        }
    }
    
    public void buscadorTabla(KeyEvent event){
        // Guardamos la búsqueda en un String
        String nombreParaBuscar = this.textFieldBuscar.getText();

        // Si nombreParaBuscar está vacío le volvemos a meter la lista entera sin filtrar
        if (nombreParaBuscar.isEmpty()){
            this.tableViewUsuarios.setItems(usuarios);

        } else {
            // Limpiamos la lista alternativa para que no acumule búsquedas
            this.filtroUsuarios.clear();
            // Buscamos usuarios que coincidan dentro de la lista entera
            for (FichausuariosEntity usuariosFiltrados : this.usuarios) {
                if (usuariosFiltrados.getNombre().toLowerCase().contains(nombreParaBuscar.toLowerCase())
                        || usuariosFiltrados.getPaqServicios().toLowerCase().contains(nombreParaBuscar.toLowerCase())) {
                    this.filtroUsuarios.add(usuariosFiltrados);
                }
            }
            // Añadimos a la tabla la lista de usuarios encontrados
            this.tableViewUsuarios.setItems(filtroUsuarios);
        }
    }

    public void cargarDatosTableView() {
        try {
            Connection nuevaConexion = databaseConnection.getConnection();

            ResultSet resultSet = nuevaConexion.createStatement().executeQuery("SELECT Nombre AS 'nombre', Email AS 'email', PaqServicios AS 'PaqServicios', ID AS 'id' FROM listausuarios.fichausuarios");

            while (resultSet.next()) {
                usuarios.add(new FichausuariosEntity(
                        resultSet.getString("nombre"),
                        resultSet.getString("email"),
                        resultSet.getString("PaqServicios"),
                        resultSet.getInt("id")));
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnServicio.setCellValueFactory(new PropertyValueFactory<>("PaqServicios"));
        columnID.setCellValueFactory(new PropertyValueFactory<>("id"));

        tableViewUsuarios.setItems(usuarios);
    }

    public void modificarUsuario(ActionEvent event){
        FichausuariosEntity usuarioSeleccionado = this.tableViewUsuarios.getSelectionModel().getSelectedItem();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/recogidaDatos.fxml"));

            Parent root = loader.load();
            recogidaController controlador = loader.getController();
            controlador.initAtributtes(usuarios, usuarioSeleccionado);

            Scene scene = new Scene(root);
            Stage secundaryStage = new Stage();
            secundaryStage.initModality(Modality.APPLICATION_MODAL); // Cuando yo lo abra no me va a dejar volver a la ventana anterior.
            secundaryStage.setScene(scene);
            secundaryStage.setTitle("Editar usuario");
            secundaryStage.getIcons().add(new Image("/vista/icons/download.png"));
            secundaryStage.showAndWait();

            FichausuariosEntity aux = controlador.getUsuario();
            if (aux != null){
                refreshTabla();
            }

        } catch (IOException ex) {
            Logger.getLogger(recogidaController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void nuevoUsuario(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/recogidaDatos.fxml"));

            Parent root = loader.load();
            recogidaController controlador = loader.getController();
            controlador.initAtributtes(usuarios);

            Scene scene = new Scene(root);
            Stage secundaryStage = new Stage();
            secundaryStage.initModality(Modality.APPLICATION_MODAL); // Cuando yo lo abra no me va a dejar volver a la ventana anterior.
            secundaryStage.setScene(scene);
            secundaryStage.setTitle("Nuevo usuario");
            secundaryStage.getIcons().add(new Image("/vista/icons/download.png"));
            secundaryStage.showAndWait();

            FichausuariosEntity aux = controlador.getUsuario();
            if (aux != null){
                refreshTabla();
            }

        } catch (IOException ex) {
            Logger.getLogger(recogidaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void eliminarUsuario(ActionEvent event){
        FichausuariosEntity usuarioSeleccionado = this.tableViewUsuarios.getSelectionModel().getSelectedItem();

        try {
            transaction.begin();

            Query borrarUsuario = entityManager.createQuery("DELETE FROM FichausuariosEntity WHERE id = :IDdeUsuarioSeleccionado");
            borrarUsuario.setParameter("IDdeUsuarioSeleccionado", usuarioSeleccionado.getId());
            borrarUsuario.executeUpdate();

            transaction.commit();
        } catch (DataException e){
            System.out.println("ERROR:"+ e.toString());

            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText(null);
            errorAlert.setTitle("ERROR");
            errorAlert.setContentText("Error al eliminar.");
            errorAlert.showAndWait();

        } finally {
            if (transaction.isActive()){
                transaction.rollback();
            }
//            entityManager.close();
//            entityManagerFactory.close();
        }

        Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
        errorAlert.setHeaderText(null);
        errorAlert.setTitle("Info");
        errorAlert.setContentText("Se ha eliminado correctamente.");
        errorAlert.showAndWait();

        refreshTabla();
    }

    public void refreshTabla(){

        // Limpiamos la tabla y el ObservableList usuarios
        tableViewUsuarios.getItems().clear();
        usuarios.clear();

        imageViewRefresh.setOpacity(0.4);

        // Creamos una Task paralela con un delay de 0.5 seg
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                return null;
            }
        };

        // Completamos la funcionalidad de la Task
        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                // RELOAD TABLE
                cargarDatosTableView();
                // RESET BUTTON DESELECTED
                imageViewRefresh.setOpacity(0.9);

            }
        });

        // Iniciamos un nuevo hilo llamando a la Task anterior creada
        new Thread(sleeper).start();
        }

    public void generarInformePDF(ActionEvent event) throws IOException {
        GeneratePDFFileIText generatePDFFileIText = new GeneratePDFFileIText();
        generatePDFFileIText.createPDF(DEST, usuarios);

        // ¿Desea abrir el PDF?
        ButtonType foo = new ButtonType("Abrir", ButtonBar.ButtonData.OK_DONE);
        ButtonType bar = new ButtonType("Ahora no", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "¿Desea abrir el documento?",
                foo,
                bar);

        alert.setHeaderText("Se ha generado el archivo PDF.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.orElse(bar) == foo) {
            Desktop.getDesktop().open(new File(DEST));
        }
    }

    public void wait(int sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
