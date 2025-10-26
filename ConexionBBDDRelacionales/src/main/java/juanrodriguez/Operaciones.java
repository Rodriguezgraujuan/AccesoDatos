package juanrodriguez;


import juanrodriguez.clase.Asignatura;
import juanrodriguez.clase.Estudiante;
import juanrodriguez.clase.Mascota;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Operaciones {

    public List<Asignatura> listarAsignaturas() {
        List<Asignatura> lista = new ArrayList<>();

        String sql = "SELECT id_asignatura, nombre_asignatura, aula, obligatoria FROM Asignatura";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Asignatura a = new Asignatura(
                        rs.getInt("id_asignatura"),
                        rs.getString("nombre_asignatura"),
                        rs.getString("aula"),
                        rs.getBoolean("obligatoria")
                );
                lista.add(a);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar asignaturas: " + e.getMessage());
        }

        return lista;
    }
    public List<Estudiante> obtenerEstudiantesPorCasa(String nombreCasa) {
        List<Estudiante> estudiantes = new ArrayList<>();

        String sql = """
                SELECT e.id_estudiante, e.nombre, e.apellido
                FROM Estudiante e
                INNER JOIN Casa c ON e.id_casa = c.id_casa
                WHERE c.nombre_casa = ?;
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreCasa);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Estudiante est = new Estudiante(
                            rs.getInt("id_estudiante"),
                            rs.getString("nombre"),
                            rs.getString("apellido")
                    );
                    estudiantes.add(est);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar estudiantes por casa: " + e.getMessage());
        }

        return estudiantes;
    }

    public Mascota obtenerMascotaDeEstudiante(String nombre, String apellido) {
        Mascota mascota = null;

        String sql = """
                SELECT m.id_mascota, m.nombre_mascota AS nombre_mascota, m.especie
                FROM Mascota m
                INNER JOIN Estudiante e ON m.id_estudiante = e.id_estudiante
                WHERE e.nombre = ? AND e.apellido = ?;
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, apellido);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    mascota = new Mascota(
                            rs.getInt("id_mascota"),
                            rs.getString("nombre_mascota"),
                            rs.getString("especie")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar la mascota del estudiante: " + e.getMessage());
        }

        return mascota;
    }

    public Map<String, Integer> numeroEstudiantesPorCasa() {
        Map<String, Integer> resultado = new HashMap<>();

        String sql = """
                SELECT c.nombre_casa, COUNT(e.id_estudiante) AS total_estudiantes
                FROM Casa c
                LEFT JOIN Estudiante e ON c.id_casa = e.id_casa
                GROUP BY c.nombre_casa;
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String casa = rs.getString("nombre_casa");
                int total = rs.getInt("total_estudiantes");
                resultado.put(casa, total);
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar número de estudiantes por casa: " + e.getMessage());
        }

        return resultado;
    }

    public boolean insertarAsignatura(String nombreAsignatura, String aula, boolean obligatoria) {
        String sql = "INSERT INTO Asignatura (nombre_asignatura, aula, obligatoria) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreAsignatura);
            pstmt.setString(2, aula);
            pstmt.setBoolean(3, obligatoria);

            int filasInsertadas = pstmt.executeUpdate();
            return filasInsertadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar asignatura: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarAulaAsignatura(int idAsignatura, String nuevaAula) {
        String sql = "UPDATE Asignatura SET aula = ? WHERE id_asignatura = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nuevaAula);
            pstmt.setInt(2, idAsignatura);

            int filasActualizadas = pstmt.executeUpdate();
            return filasActualizadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al modificar el aula de la asignatura: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarAsignatura(int idAsignatura) {
        String sql = "DELETE FROM Asignatura WHERE id_asignatura = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAsignatura);

            int filasEliminadas = pstmt.executeUpdate();
            return filasEliminadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar la asignatura: " + e.getMessage());
            return false;
        }
    }


    public static void main(String[] args) {
        Operaciones op = new Operaciones();
        List<Asignatura> asignaturas = op.listarAsignaturas();
        System.out.println("LISTADO DE ASIGNATURAS");
        asignaturas.forEach(System.out::println);

        List<Estudiante> estudiantes = op.obtenerEstudiantesPorCasa("Gryffindor");
        System.out.println("LISTADO DE ESTUDIANTES");
        estudiantes.forEach(System.out::println);

        String nombre="Hermione";
        Mascota m = op.obtenerMascotaDeEstudiante(nombre, "Granger");
        if (m != null) {
            System.out.println("La mascota de "+nombre+" es: " + m.getNombre() + " (" + m.getTipo() + ")");
        } else {
            System.out.println(nombre+" no tiene mascota registrada.");
        }

        Map<String, Integer> estudiantesPorCasa = op.numeroEstudiantesPorCasa();

        System.out.println("NUMERO ESTUDIANTES POR CASA:");
        estudiantesPorCasa.forEach((casa, total) ->
                System.out.println("- " + casa + ": " + total + " estudiantes"));


        boolean exitoInsert = op.insertarAsignatura("Acceso a datos", "A-2004", true);

        if (exitoInsert) {
            System.out.println("Asignatura insertada correctamente.");
        } else {
            System.out.println("Error al insertar la asignatura.");
        }

        boolean exitoModify = op.modificarAulaAsignatura(7, "C-303");

        if (exitoModify) {
            System.out.println("Aula de la asignatura modificada correctamente.");
        } else {
            System.out.println("No se pudo modificar el aula. Verifica el ID de la asignatura.");
        }

        boolean exitoDetele = op.eliminarAsignatura(17);

        if (exitoDetele) {
            System.out.println("Asignatura eliminada correctamente.");
        } else {
            System.out.println("No se pudo eliminar la asignatura. Verifica el ID.");
        }

    }
}
