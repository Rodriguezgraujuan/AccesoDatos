package juanrodriguez.Clases;

import java.sql.Date;

public class Estudiante {
    private String nombre;
    private String apellido;
    private Date fechaNacimiento;
    private int anyoCurso;

    public Estudiante(String nombre, String apellido, Date fechaNacimiento, int anyoCurso) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.anyoCurso = anyoCurso;
    }

    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public Date getFechaNacimiento() { return fechaNacimiento; }
    public int getAnyoCurso() { return anyoCurso; }
}

