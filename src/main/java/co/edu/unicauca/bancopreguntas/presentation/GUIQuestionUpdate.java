package co.edu.unicauca.bancopreguntas.presentation;

import co.edu.unicauca.bancopreguntas.domain.Question;
import co.edu.unicauca.bancopreguntas.domain.QuestionState;
import co.edu.unicauca.bancopreguntas.infra.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Optional;

/**
 * Capa de presentacion.
 *
 * Vista de actualizacion del micro patron MVC: unica responsable de cambiar el
 * estado de una pregunta del banco. La consulta del detalle vive en
 * {@link GUIQuestionLoad}.
 *
 * SRP: no calcula nada, delega el cambio en el controlador; el modelo se
 * encarga de notificar a las vistas observadoras.
 */
public class GUIQuestionUpdate extends JFrame implements Observer {

    private final QuestionController controller;

    private final JComboBox<Question> cmbQuestions = new JComboBox<>();
    private final JTextField txtCurrentState = new JTextField();
    private final JComboBox<QuestionState> cmbNewState = new JComboBox<>(QuestionState.values());
    private final JButton btnUpdate = new JButton("Actualizar estado");

    /** Evita reaccionar a los eventos del comboBox mientras se repuebla. */
    private boolean reloading;

    /**
     * @param controller controlador del MVC.
     */
    public GUIQuestionUpdate(QuestionController controller) {
        this.controller = controller;
        initComponents();
        registerListeners();
        reloadQuestions();
    }

    /**
     * Construye la interfaz grafica.
     */
    private void initComponents() {
        setTitle("Banco de Preguntas Saber Pro - Actualizacion de estado");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("ACTUALIZAR ESTADO DE LA PREGUNTA", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(lblTitle, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        center.add(buildUpdatePanel());
        center.add(Box.createVerticalGlue());
        add(center, BorderLayout.CENTER);

        setSize(520, 260);
        setLocation(30, 610);
    }

    /**
     * @return panel con la pregunta a actualizar y su cambio de estado.
     */
    private JPanel buildUpdatePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("CAMBIO DE ESTADO"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtCurrentState.setEditable(false);

        int row = 0;
        addField(panel, row++, "Pregunta:", cmbQuestions);
        addField(panel, row++, "Estado actual:", txtCurrentState);
        addField(panel, row++, "Nuevo estado:", cmbNewState);

        GridBagConstraints gbc = baseConstraints();
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(btnUpdate, gbc);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    /**
     * Agrega una fila etiqueta/campo al formulario.
     */
    private void addField(JPanel panel, int row, String label, Component field) {
        GridBagConstraints gbc = baseConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    /**
     * @return restricciones comunes para el GridBagLayout.
     */
    private GridBagConstraints baseConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    /**
     * Conecta los eventos de la vista con el controlador.
     */
    private void registerListeners() {
        cmbQuestions.addActionListener(event -> showSelectedState());
        btnUpdate.addActionListener(event -> updateState());
    }

    /**
     * Pide al controlador las preguntas y llena el comboBox conservando la
     * seleccion actual.
     */
    private void reloadQuestions() {
        Question selected = (Question) cmbQuestions.getSelectedItem();
        String selectedId = selected == null ? null : selected.getId();

        reloading = true;
        cmbQuestions.removeAllItems();
        List<Question> questions = controller.listQuestions();
        for (Question question : questions) {
            cmbQuestions.addItem(question);
        }
        reloading = false;

        if (questions.isEmpty()) {
            txtCurrentState.setText("");
            return;
        }
        cmbQuestions.setSelectedIndex(indexOf(questions, selectedId));
        showSelectedState();
    }

    /**
     * @param questions preguntas cargadas en el comboBox.
     * @param id        identificador que estaba seleccionado, puede ser null.
     * @return posicion de esa pregunta, o 0 si ya no esta en el banco.
     */
    private int indexOf(List<Question> questions, String id) {
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId().equals(id)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Muestra el estado vigente de la pregunta seleccionada y lo propone como
     * valor inicial del nuevo estado.
     */
    private void showSelectedState() {
        if (reloading) {
            return;
        }
        Optional<Question> found = selectedQuestion();
        if (found.isEmpty()) {
            txtCurrentState.setText("");
            return;
        }
        Question question = found.get();
        txtCurrentState.setText(question.getState().getLabel());
        cmbNewState.setSelectedItem(question.getState());
    }

    /**
     * @return la pregunta seleccionada tal como esta en el banco.
     */
    private Optional<Question> selectedQuestion() {
        Question selected = (Question) cmbQuestions.getSelectedItem();
        if (selected == null) {
            return Optional.empty();
        }
        return controller.findById(selected.getId());
    }

    /**
     * Solicita al controlador el cambio de estado de la pregunta seleccionada.
     * El modelo se encarga de notificar a las vistas observadoras.
     */
    private void updateState() {
        Question selected = (Question) cmbQuestions.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Primero seleccione una pregunta",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = selected.getId();
        QuestionState newState = (QuestionState) cmbNewState.getSelectedItem();
        try {
            boolean updated = controller.changeState(id, newState);
            if (updated) {
                showSelectedState();
                cmbQuestions.repaint();
                JOptionPane.showMessageDialog(this,
                        "Estado actualizado a: " + newState.getLabel(),
                        "Informacion", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No fue posible actualizar el estado",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IllegalStateException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            showSelectedState();
        }
    }

    /**
     * El modelo publico un cambio: se vuelve a leer el banco para que la lista
     * y el estado actual queden al dia.
     *
     * @param data estadisticas publicadas por el modelo, no se usan aqui.
     */
    @Override
    public void update(Object data) {
        SwingUtilities.invokeLater(this::reloadQuestions);
    }
}
