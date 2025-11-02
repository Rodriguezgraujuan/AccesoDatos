package juanrodriguez.Clases;


import java.sql.Date;
import java.time.LocalDate;

public class Profesor {


    private int idProfesor;
    private String nombre;
    private String apellido;
    private Date fechaInicio;

    public Profesor(){

    }

    public Profesor(String nombre, String apellido, Date fechaInicio){
        this.nombre=nombre;
        this.apellido=apellido;
        this.fechaInicio=fechaInicio;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
}
