package juanrodriguez;

import juanrodriguez.Clases.Asignatura;
import juanrodriguez.Clases.Estudiante;
import juanrodriguez.Clases.Profesor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Operaciones {

    public void crearProfesorYAsignatura(Asignatura asignatura, Profesor profesor) throws SQLException {
        String sqlAsignatura = "INSERT INTO Asignatura (nombre_asignatura, aula, obligatoria) VALUES (?, ?, ?)";
        String sqlProfesor = "INSERT INTO Profesor (nombre, apellido, id_asignatura, fecha_inicio) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // 🔹 iniciar transacción

            int idAsignatura = 0;
            try (PreparedStatement psAsig = conn.prepareStatement(sqlAsignatura, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psAsig.setString(1, asignatura.getNombreAsignatura());
                psAsig.setString(2, asignatura.getAula());
                psAsig.setBoolean(3, asignatura.isObligatoria());
                psAsig.executeUpdate();

                ResultSet rs = psAsig.getGeneratedKeys();
                if (rs.next()) idAsignatura = rs.getInt(1);
            }

            try (PreparedStatement psProf = conn.prepareStatement(sqlProfesor)) {
                psProf.setString(1, profesor.getNombre());
                psProf.setString(2, profesor.getApellido());
                psProf.setInt(3, idAsignatura);
                psProf.setDate(4, profesor.getFechaInicio());
                psProf.executeUpdate();
            }

            conn.commit();
            System.out.println("Creados con exito");

        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = DatabaseConnection.getConnection()) {
                System.out.println("Error al crear, haciendo rollback");
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }

    public boolean funcionMatricularEstudiante(Estudiante estudiante) {
        String sql = "SELECT * FROM matricular_estudiante(?, ?, ?, ?)";
        List<String> resultados = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            System.out.println("Conectado exitosamente a la base de datos");

            pstmt.setString(1, estudiante.getNombre());
            pstmt.setString(2, estudiante.getApellido());
            pstmt.setDate(3, estudiante.getFechaNacimiento());
            pstmt.setInt(4, estudiante.getAnyoCurso());

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int idEst = rs.getInt("out_id_estudiante");
                String nom = rs.getString("nombre");
                String ape = rs.getString("apellido");
                String casa = rs.getString("nombre_casa");
                String mascota = rs.getString("mascota");

                String info = String.format(
                        "ID: %d | %s %s | Casa: %s | Mascota: %s",
                        idEst, nom, ape, casa, mascota
                );
                resultados.add(info);
            }

            System.out.println("Estudiante matriculado correctamente:");
            resultados.forEach(System.out::println);

            mostrarDatosEstudiante(conn, estudiante.getNombre(), estudiante.getApellido());

            return true;

        } catch (SQLException e) {
            System.err.println("Error al ejecutar la función matricular_estudiante: " + e.getMessage());
            return false;
        }


    }

    public void procedimientoMatricularEstudiante(Estudiante estudiante) {
        String sql = "CALL crear_estudiante(?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("Conectado exitosamente a la base de datos.");

            stmt.setString(1, estudiante.getNombre());
            stmt.setString(2, estudiante.getApellido());
            stmt.setDate(3, estudiante.getFechaNacimiento());
            stmt.setInt(4, estudiante.getAnyoCurso());

            stmt.execute();
            System.out.println("Procedimiento crear_estudiante ejecutado correctamente.");

            mostrarDatosEstudiante(conn, estudiante.getNombre(), estudiante.getApellido());

        } catch (SQLException e) {
            System.err.println("Error al ejecutar el procedimiento crear_estudiante: " + e.getMessage());
        }
    }

    private void mostrarDatosEstudiante(Connection conn, String nombre, String apellido) throws SQLException {
        String sql = """
            SELECT e.id_estudiante, e.nombre, e.apellido, e.anyo_curso, c.nombre_casa, a.nombre_asignatura
            FROM Estudiante e
            JOIN Casa c ON e.id_casa = c.id_casa
            JOIN Estudiante_Asignatura ea ON e.id_estudiante = ea.id_estudiante
            JOIN Asignatura a ON ea.id_asignatura = a.id_asignatura
            WHERE e.nombre = ? AND e.apellido = ?
            ORDER BY a.id_asignatura;
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, apellido);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("📘 Información del estudiante matriculado:");
            while (rs.next()) {
                System.out.printf(
                        "ID: %d | %s %s | Curso: %d | Casa: %s | Asignatura: %s%n",
                        rs.getInt("id_estudiante"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getInt("anyo_curso"),
                        rs.getString("nombre_casa"),
                        rs.getString("nombre_asignatura")
                );
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        Operaciones op = new Operaciones();
        /*Asignatura asignatura = new Asignatura("Arte Muggle", "Aula 5", true);
        Profesor profesor = new Profesor("Albus", "Dumbledore", Date.valueOf("1955-09-01"));
        op.crearProfesorYAsignatura(asignatura,profesor);

        op.funcionMatricularEstudiante(new Estudiante("Selena", "Shade", Date.valueOf("2007-05-23"), 4));

         */
        op.procedimientoMatricularEstudiante(new Estudiante("Theo", "Blackthorn", Date.valueOf("2008-10-11"), 3));

    }


}
