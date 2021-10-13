import entity.FichausuariosEntity;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.exception.DataException;
import javax.persistence.*;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/vista/hibernateApp.fxml"));
        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add("/vista/CSS/design.css");
        primaryStage.setTitle("HibernateApp");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("/vista/icons/download.png"));

        primaryStage.show();
    }

    public static void main(String[]args){
        launch(args);

//        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
//        EntityManager entityManager = entityManagerFactory.createEntityManager();
//        EntityTransaction transaction = entityManager.getTransaction();

//        try {
//            transaction.begin();
            // BÚSQUEDA COMPLEJA
//            TypedQuery<FichausuariosEntity> usuariosPorTipoServicio = entityManager.createNamedQuery("tipoDeServicio", FichausuariosEntity.class);
//            usuariosPorTipoServicio.setParameter(1, "Premium");
//            for (FichausuariosEntity usuarioPorServicio: usuariosPorTipoServicio.getResultList()) {
//                System.out.println(usuarioPorServicio);
//            }

//            // ACTUALIZAR DATOS
//            Query updateDatos = entityManager.createQuery("UPDATE FichausuariosEntity ficha SET ficha.paqServicios = :newServicio WHERE ficha.id = :userId");
//            updateDatos.setParameter("newServicio", "Basic");
//            updateDatos.setParameter("userId", 3);
//            updateDatos.executeUpdate();

//             BÚSQUEDA SENCILLA
//            Query busqueda = entityManager.createNativeQuery("SELECT ficha.PaqServicios FROM listausuarios.fichausuarios ficha WHERE ficha.Nombre = :nombre"); // WHERE Nombre = ?
//            busqueda.setParameter("nombre", "Eva"); // Aquí relleno el ? con el :nombre que busco
//            System.out.println("Nombre:" + busqueda.getSingleResult());


             // NUEVO USUARIO EVA
//            FichausuariosEntity Eva = new FichausuariosEntity();
//            Eva.setId(3);
//            Eva.setNombre("Eva");
//            Eva.setEmail("eva_otero@hotmail.com");
//            Eva.setPaqServicios("Premium");
//            entityManager.persist(Eva);
//
//            transaction.commit();
//        } catch (DataException e){
//            System.out.println("ERROR:"+ e.toString());
//
//        } finally {
//            if (transaction.isActive()){
//                transaction.rollback();
//            }
//            entityManager.close();
//            entityManagerFactory.close();
//        }
    }


}
