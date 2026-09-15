package co.edu.unicauca.bancopreguntas.presentation.user;

import co.edu.unicauca.bancopreguntas.access.QuestionImplRepository;
import co.edu.unicauca.bancopreguntas.domain.QuestionRepository;
import co.edu.unicauca.bancopreguntas.domain.QuestionService;
import co.edu.unicauca.bancopreguntas.domain.user.Role;
import co.edu.unicauca.bancopreguntas.domain.user.User;
import co.edu.unicauca.bancopreguntas.microkernel.PluginLoader;
import co.edu.unicauca.bancopreguntas.microkernel.QuestionBankBridge;
import co.edu.unicauca.bancopreguntas.microkernel.QuestionMicrokernel;
import co.edu.unicauca.bancopreguntas.presentation.GUIObserver1;
import co.edu.unicauca.bancopreguntas.presentation.GUIObserver2;
import co.edu.unicauca.bancopreguntas.presentation.GUIQuestions;
import co.edu.unicauca.bancopreguntas.presentation.QuestionController;
import co.edu.unicauca.bancopreguntas.presentation.microkernel.GUIMicrokernel;
import co.edu.unicauca.bancopreguntas.service.user.IUserService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Component;
import java.awt.Font;

/**
 * Capa de presentacion (HU01). Menu principal mostrado tras un login
 * exitoso; desde aqui se abren los modulos de HU02 (banco de preguntas) y
 * HU03 (generador de preguntas basado en el microkernel de plugins).
 *
 * Cada usuario comparte un unico QuestionService/QuestionMicrokernel en toda
 * la sesion, de modo que las preguntas generadas por los plugins del
 * microkernel quedan visibles en la vista de gestion del banco de preguntas.
 */
public class MainMenuView extends JFrame {

    private final User user;
    private final IUserService userService;

    /** Un unico banco de preguntas compartido por HU02 y HU03 durante la sesion. */
    private QuestionService sharedQuestionService;

    public MainMenuView(User user, IUserService userService) {
        this.user = user;
        this.userService = userService;
        initComponents();
    }

    private void initComponents() {
        setTitle("Banco de Preguntas Saber Pro - Menu principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        JLabel welcome = new JLabel("Bienvenido, " + user.getFullName() + " (" + user.getRole() + ")");
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, 16f));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(welcome);
        panel.add(Box.createVerticalStrut(20));

        boolean canManageQuestions = user.getRole() == Role.ADMINISTRADOR
                || user.getRole() == Role.AUTOR_PREGUNTAS
                || user.getRole() == Role.REVISOR;

        JButton btnQuestionBank = new JButton("Gestionar banco de preguntas");
        btnQuestionBank.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnQuestionBank.setEnabled(canManageQuestions);
        btnQuestionBank.addActionListener(e -> openQuestionBank());
        panel.add(btnQuestionBank);
        panel.add(Box.createVerticalStrut(10));

        JButton btnMicrokernel = new JButton("Generador de preguntas (Microkernel + Pipeline)");
        btnMicrokernel.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnMicrokernel.setEnabled(canManageQuestions);
        btnMicrokernel.addActionListener(e -> openMicrokernel());
        panel.add(btnMicrokernel);
        panel.add(Box.createVerticalStrut(20));

        JButton btnLogout = new JButton("Cerrar sesion");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> {
            new LoginView(userService).setVisible(true);
            dispose();
        });
        panel.add(btnLogout);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * @return el banco de preguntas de la sesion, creandolo la primera vez
     *         que se solicita (HU02 y HU03 comparten esta misma instancia).
     */
    private QuestionService questionService() {
        if (sharedQuestionService == null) {
            QuestionRepository repository = new QuestionImplRepository();
            sharedQuestionService = new QuestionService(repository);
        }
        return sharedQuestionService;
    }

    /**
     * Ensambla y abre las vistas de HU02 (banco de preguntas y sus
     * observadores de estadisticas).
     */
    private void openQuestionBank() {
        QuestionService service = questionService();
        QuestionController controller = new QuestionController(service);

        GUIQuestions mainView = new GUIQuestions(controller);
        GUIObserver1 statisticsView = new GUIObserver1();
        GUIObserver2 chartView = new GUIObserver2();

        service.addObserver(statisticsView);
        service.addObserver(chartView);
        service.notifyAllObservers(service.getStatistics());

        mainView.setVisible(true);
        statisticsView.setVisible(true);
        chartView.setVisible(true);
    }

    /**
     * Abre el modulo de HU03: genera preguntas mediante los plugins del
     * microkernel (validadas por la tuberia de filtros) y las incorpora al
     * mismo banco de preguntas que gestiona HU02.
     */
    private void openMicrokernel() {
        QuestionMicrokernel microkernel = new QuestionMicrokernel();
        PluginLoader.loadFromProperties(microkernel, "plugins.properties");
        QuestionBankBridge bridge = new QuestionBankBridge(questionService());
        new GUIMicrokernel(microkernel, bridge).setVisible(true);
    }
}
