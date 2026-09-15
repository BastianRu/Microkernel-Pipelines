package co.edu.unicauca.bancopreguntas.presentation.user;

import co.edu.unicauca.bancopreguntas.domain.user.User;
import co.edu.unicauca.bancopreguntas.service.user.AuthenticationException;
import co.edu.unicauca.bancopreguntas.service.user.IUserService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

/**
 * Capa de presentacion (HU01). Pantalla de inicio de sesion, puerto a Swing
 * de la vista original en JavaFX del taller de principios SOLID.
 */
public class LoginView extends JFrame {

    private final IUserService userService;

    private final JTextField txtLogin = new JTextField(18);
    private final JPasswordField txtPassword = new JPasswordField(18);
    private final JLabel lblError = new JLabel(" ");

    public LoginView(IUserService userService) {
        this.userService = userService;
        initComponents();
    }

    private void initComponents() {
        setTitle("Banco de Preguntas Saber Pro - Iniciar sesion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel title = new JLabel("Iniciar sesion");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblError.setForeground(Color.RED);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnLogin = new JButton("Iniciar sesion");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(event -> attemptLogin());

        JButton btnGoRegister = new JButton("No tienes cuenta? Registrate");
        btnGoRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGoRegister.addActionListener(event -> {
            new RegisterView(userService).setVisible(true);
            dispose();
        });

        panel.add(title);
        panel.add(Box.createVerticalStrut(15));
        panel.add(labeled("Usuario:", txtLogin));
        panel.add(Box.createVerticalStrut(8));
        panel.add(labeled("Contrasena:", txtPassword));
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnLogin);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblError);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnGoRegister);

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

    private void attemptLogin() {
        try {
            User user = userService.login(txtLogin.getText(), new String(txtPassword.getPassword()));
            new MainMenuView(user, userService).setVisible(true);
            dispose();
        } catch (AuthenticationException ex) {
            lblError.setText(ex.getMessage());
        }
    }
}
