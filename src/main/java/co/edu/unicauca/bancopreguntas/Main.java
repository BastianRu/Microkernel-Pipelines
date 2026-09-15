package co.edu.unicauca.bancopreguntas;

import co.edu.unicauca.bancopreguntas.access.user.Argon2PasswordHasher;
import co.edu.unicauca.bancopreguntas.access.user.DatabaseConnection;
import co.edu.unicauca.bancopreguntas.access.user.IUserRepository;
import co.edu.unicauca.bancopreguntas.access.user.SQLiteUserRepository;
import co.edu.unicauca.bancopreguntas.domain.user.IPasswordHasher;
import co.edu.unicauca.bancopreguntas.presentation.user.LoginView;
import co.edu.unicauca.bancopreguntas.service.user.IUserService;
import co.edu.unicauca.bancopreguntas.service.user.UserService;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.sql.Connection;

/**
 * Punto de entrada de la aplicacion (composition root).
 *
 * Ensambla las tres historias de usuario implementadas en este proyecto:
 * <ul>
 *     <li>HU01 - Gestion de usuarios del sistema (login/registro, patron de
 *     capas, persistencia en SQLite con hashing Argon2).</li>
 *     <li>HU02 - Gestion del banco de preguntas (patron de capas + micro
 *     patron MVC + Observer), disponible desde el menu principal.</li>
 *     <li>HU03 - Validacion estructural, implementada con arquitectura
 *     Microkernel (plugins registrados por reflexion) y el patron Tuberias
 *     y Filtros, tambien disponible desde el menu principal.</li>
 * </ul>
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::launch);
    }

    private static void launch() {
        applyLookAndFeel();

        Connection connection = DatabaseConnection.getConnection();
        IUserRepository userRepository = new SQLiteUserRepository(connection);
        IPasswordHasher passwordHasher = new Argon2PasswordHasher();
        IUserService userService = new UserService(userRepository, passwordHasher);

        new LoginView(userService).setVisible(true);
    }

    private static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("No fue posible aplicar la apariencia del sistema: " + ex.getMessage());
        }
    }
}
