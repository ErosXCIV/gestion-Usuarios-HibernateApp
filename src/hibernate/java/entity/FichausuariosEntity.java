package entity;

import javax.persistence.*;

@Entity

@NamedQuery(name = "tipoDeServicio", query = "SELECT ficha FROM FichausuariosEntity ficha WHERE ficha.paqServicios = ?1")
@Table(name = "fichausuarios", schema = "listausuarios") // catalog = ""
public class FichausuariosEntity {
    private int id;
    private String nombre;
    private String email;
    private Integer contraseña;
    private String paqServicios;

    public FichausuariosEntity(String nombre, String email, String paqServicios, int id){
        this.nombre = nombre;
        this.email = email;
        this.paqServicios = paqServicios;
        this.id = id;
    }

    public FichausuariosEntity(String paqServicios){
        this.paqServicios = paqServicios;
    }

    public FichausuariosEntity() {

    }



    @SqlResultSetMapping(name="FichausuariosEntityMapping",
            classes = {
                    @ConstructorResult(targetClass = FichausuariosDTO.class,
                            columns = {@ColumnResult(name="nombre"), @ColumnResult(name="email"), @ColumnResult(name = "paqServicios")}
                    )}
    )
    public class FichausuariosDTO {
        String nombre;
        String email;
        String paqServicios;

        public FichausuariosDTO(String nombre, String email, String paqServicios) {
            this.nombre = nombre;
            this.email = email;
            this.paqServicios = paqServicios;
        }
    }


    @Id
    @Column(name = "ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "Nombre")
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Basic
    @Column(name = "Email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "Contraseña")
    public Integer getContraseña() {
        return contraseña;
    }

    public void setContraseña(Integer contraseña) {
        this.contraseña = contraseña;
    }

    @Basic
    @Column(name = "PaqServicios")
    public String getPaqServicios() {
        return paqServicios;
    }

    public void setPaqServicios(String paqServicios) {
        this.paqServicios = paqServicios;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FichausuariosEntity that = (FichausuariosEntity) o;

        if (id != that.id) return false;
        if (nombre != null ? !nombre.equals(that.nombre) : that.nombre != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (contraseña != null ? !contraseña.equals(that.contraseña) : that.contraseña != null) return false;
        if (paqServicios != null ? !paqServicios.equals(that.paqServicios) : that.paqServicios != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (contraseña != null ? contraseña.hashCode() : 0);
        result = 31 * result + (paqServicios != null ? paqServicios.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FichausuariosEntity{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", contraseña=" + contraseña +
                ", paqServicios='" + paqServicios + '\'' +
                '}';
    }
}

