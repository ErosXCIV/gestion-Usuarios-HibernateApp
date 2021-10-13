package controlador;

import entity.FichausuariosEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.exception.DataException;

import javax.persistence.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class recogidaController extends controladorHibernateApp implements Initializable {
    public TextField textNombre;
    public TextField textEmail;
    public String auxServicios;
    public Button botonGuardar;
    public Button botonSalir;
    public ComboBox comboBoxServicios;

    public ObservableList<FichausuariosEntity> fichausuariosEntityObservableList;
    public ObservableList<String> opcionesServicio = FXCollections.observableArrayList();
    public FichausuariosEntity usuarioSeleccionado;
    public FichausuariosEntity newUsuarioEntity = null;
    public int id = 0;


    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();

    public FichausuariosEntity getUsuario() {
        return newUsuarioEntity;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initAtributtes(ObservableList<FichausuariosEntity> usuariosLista){
        initComboBox();
        this.fichausuariosEntityObservableList = usuariosLista;
        this.botonGuardar.setText("Crear usuario");


    }

    public void initAtributtes(ObservableList<FichausuariosEntity> usuariosLista, FichausuariosEntity usuarioSeleccionadoPorMain){
        initComboBox();

        this.fichausuariosEntityObservableList = usuariosLista;
        this.usuarioSeleccionado = usuarioSeleccionadoPorMain;
        this.textNombre.setText(usuarioSeleccionadoPorMain.getNombre());
        this.textEmail.setText(usuarioSeleccionadoPorMain.getEmail());
        auxServicios = usuarioSeleccionadoPorMain.getPaqServicios();
        this.comboBoxServicios.getSelectionModel().select(auxServicios);
        id = this.usuarioSeleccionado.getId();


    }

    public void initComboBox() {
        try {
            Connection connection = databaseConnection.getConnection();
            String comilla = " \"','\" ";

            ResultSet resultSet = connection.createStatement().executeQuery(" SELECT DISTINCT SUBSTRING_INDEX(SUBSTRING_INDEX(SUBSTRING(COLUMN_TYPE, 7, LENGTH(COLUMN_TYPE) - 8)," + comilla + ", 1 + units.i + tens.i * 10) ," + comilla + ", -1) " +
                    "AS 'listaservicios' FROM INFORMATION_SCHEMA.COLUMNS " +
                    "CROSS JOIN (SELECT 0 AS i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) units " +
                    "CROSS JOIN (SELECT 0 AS i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) tens " +
                    "WHERE TABLE_NAME = 'fichausuarios' " +
                    "AND COLUMN_NAME = 'PaqServicios'; ");

            while (resultSet.next()) {
                opcionesServicio.add((resultSet.getString("listaservicios")));
            }

            this.comboBoxServicios.setItems(opcionesServicio);

        } catch (SQLException sqlException) {
            System.out.println("ERROR! " + sqlException);
        }

        comboBoxServicios.getSelectionModel().selectFirst();

    }

    public void editarUsuario(ActionEvent event){
    if (usuarioSeleccionado != null){
        String nombre = this.textNombre.getText();
        String Email = this.textEmail.getText();
        String paqServicios = auxServicios;

        newUsuarioEntity = new FichausuariosEntity(nombre, Email, paqServicios, id);

        if(fichausuariosEntityObservableList.contains(usuarioSeleccionado)) { // Comprobamos que en la lista no existe ese usuario
            System.out.println("ENTRAMOS A EDITAR USUARIO");

            System.out.println("HERE!");

            // Editar
            try {
                transaction.begin();

                System.out.println("HERE NOW!");

                Query updateDatos = entityManager.createQuery("UPDATE FichausuariosEntity ficha SET ficha.nombre = :newNombre, ficha.email = :newEmail, ficha.paqServicios = :newServicio WHERE ficha.id = :userId");
                updateDatos.setParameter("newNombre", nombre);
                updateDatos.setParameter("newEmail", Email);
                updateDatos.setParameter("newServicio", comboBoxServicios.getValue().toString());
                updateDatos.setParameter("userId", id);
                updateDatos.executeUpdate();

                transaction.commit();

                Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
                errorAlert.setHeaderText(null);
                errorAlert.setTitle("Info");
                errorAlert.setContentText("Se ha editado correctamente.");
                errorAlert.showAndWait();

            } catch (DataException e) {
                System.out.println("ERROR:" + e.toString());
                System.out.println("CAGASTE");

            } finally {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                entityManager.close();
                entityManagerFactory.close();
                }

            }
        } else {
            System.out.println("ENTRAMOS A CREAR NUEVO");
                // Insertar nuevo

                try {
                transaction.begin();

                newUsuarioEntity = new FichausuariosEntity();
                //nuevoUser.setId();
                    newUsuarioEntity.setNombre(textNombre.getText());
                    newUsuarioEntity.setEmail(textEmail.getText());
                    newUsuarioEntity.setPaqServicios(comboBoxServicios.getValue().toString());
                    entityManager.persist(newUsuarioEntity);

                transaction.commit();

                Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
                    errorAlert.setHeaderText(null);
                    errorAlert.setTitle("Info");
                    errorAlert.setContentText("Se ha añadido correctamente.");
                    errorAlert.showAndWait();

            } catch (DataException e) {
                    System.out.println("ERROR:" + e.toString());
            } finally {
                if (transaction.isActive()){
                    transaction.rollback();
                }
                entityManager.close();
                entityManagerFactory.close();
                }
            }

            // Terminan los procesos y cerramos recogidaDatos
            Stage stage = (Stage) this.botonGuardar.getScene().getWindow();
            stage.close();

        }

    public void salir(ActionEvent event){
        this.usuarioSeleccionado = null;
        Stage stage = (Stage) this.botonGuardar.getScene().getWindow();
        stage.close();
    }


}
