package co.edu.unicauca.bancopreguntas.presentation.user;

import co.edu.unicauca.bancopreguntas.domain.user.Role;
import co.edu.unicauca.bancopreguntas.service.user.IUserService;
import co.edu.unicauca.bancopreguntas.service.user.InvalidPasswordException;
import co.edu.unicauca.bancopreguntas.service.user.UserAlreadyExistsException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

/**
 * Capa de presentacion (HU01). Pantalla de registro de usuarios, puerto a
 * Swing de la vista original en JavaFX.
 */
public class RegisterView extends JFrame {

    private final IUserService userService;

    private final JTextField txtLogin = new JTextField(18);
    private final JTextField txtFullName = new JTextField(18);
    private final JComboBox<Role> cmbRole = new JComboBox<>(Role.values());
    private final JPasswordField txtPassword = new JPasswordField(18);
    private final JLabel lblMessage = new JLabel(" ");

    public RegisterView(IUserService userService) {
        this.userService = userService;
        initComponents();
    }

    private void initComponents() {
        setTitle("Banco de Preguntas Saber Pro - Registro de usuario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel title = new JLabel("Registro de usuario");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnRegister = new JButton("Registrar");
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.addActionListener(event -> handleRegister());

        JButton btnBack = new JButton("Volver a inicio de sesion");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(event -> {
            new LoginView(userService).setVisible(true);
            dispose();
        });

        panel.add(title);
        panel.add(Box.createVerticalStrut(15));
        panel.add(labeled("Usuario:", txtLogin));
        panel.add(Box.createVerticalStrut(8));
        panel.add(labeled("Nombre completo:", txtFullName));
        panel.add(Box.createVerticalStrut(8));
        panel.add(labeled("Rol:", cmbRole));
        panel.add(Box.createVerticalStrut(8));
        panel.add(labeled("Contrasena:", txtPassword));
        panel.add(Box.createVerticalStrut(4));
        JLabel hint = new JLabel("Minimo 6 caracteres, 1 digito, 1 mayuscula, 1 especial");
        hint.setFont(hint.getFont().deriveFont(Font.ITALIC, 11f));
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(hint);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnRegister);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblMessage);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnBack);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel labeled(String label, Component field) {
        JPanel row = new JPanel();
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.add(new JLabel(label));
        row.add(field);
        return row;
    }

    private void handleRegister() {
        Role selectedRole = (Role) cmbRole.getSelectedItem();
        try {
            userService.register(txtLogin.getText(), txtFullName.getText(),
                    selectedRole, new String(txtPassword.getPassword()));
            showSuccess("Usuario registrado con exito. Ya puedes iniciar sesion.");
        } catch (UserAlreadyExistsException ex) {
            showError(ex.getMessage());
        } catch (InvalidPasswordException ex) {
            showError(String.join(", ", ex.getErrors()));
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void showError(String message) {
        lblMessage.setForeground(Color.RED);
        lblMessage.setText("<html><div style='width:280px;text-align:center;'>" + message + "</div></html>");
    }

    private void showSuccess(String message) {
        lblMessage.setForeground(new Color(0x1B, 0xAF, 0x7A));
        lblMessage.setText(message);
    }
}
