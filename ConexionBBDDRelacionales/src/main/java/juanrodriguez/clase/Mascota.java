package juanrodriguez.clase;

public class Mascota {
    private int idMascota;
    private String nombre;
    private String tipo;

    public Mascota() {}

    public Mascota(int idMascota, String nombre, String tipo) {
        this.idMascota = idMascota;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return nombre + " (" + tipo + ")";
    }
}
