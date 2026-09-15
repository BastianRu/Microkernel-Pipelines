package co.edu.unicauca.bancopreguntas.presentation;

import co.edu.unicauca.bancopreguntas.domain.Question;
import co.edu.unicauca.bancopreguntas.domain.QuestionDistractors;
import co.edu.unicauca.bancopreguntas.domain.QuestionState;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Color;
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
 * Vista principal del micro patron MVC: permite seleccionar una pregunta del
 * banco, ver su detalle y cambiar su estado. No calcula nada, todo lo delega en
 * el controlador.
 */
public class GUIQuestions extends JFrame {

    private final QuestionController controller;

    private final JComboBox<Question> cmbQuestions = new JComboBox<>();
    private final JTextField txtId = new JTextField();
    private final JTextField txtName = new JTextField();
    private final JTextArea txtStatement = new JTextArea(3, 20);
    private final JTextArea txtOptions = new JTextArea(4, 20);
    private final JTextField txtCorrectAnswer = new JTextField();
    private final JTextField txtCurrentState = new JTextField();
    private final JComboBox<QuestionState> cmbNewState = new JComboBox<>(QuestionState.values());
    private final JButton btnLoad = new JButton("Cargar pregunta");
    private final JButton btnUpdate = new JButton("Actualizar estado");

    /**
     * @param controller controlador del MVC.
     */
    public GUIQuestions(QuestionController controller) {
        this.controller = controller;
        initComponents();
        loadQuestionsIntoCombo();
        registerListeners();
    }

    /**
     * Construye la interfaz grafica.
     */
    private void initComponents() {
        setTitle("Banco de Preguntas Saber Pro - Gestion de preguntas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("GESTION DE PREGUNTAS", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(lblTitle, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        center.add(buildSelectorPanel());
        center.add(Box.createVerticalStrut(10));
        center.add(buildFormPanel());
        add(center, BorderLayout.CENTER);

        setSize(520, 640);
        setLocation(30, 30);
    }

    /**
     * @return panel con el comboBox de preguntas y el boton de carga.
     */
    private JPanel buildSelectorPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("SELECCIONAR PREGUNTA"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = baseConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(new JLabel("Pregunta:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(cmbQuestions, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(btnLoad, gbc);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    /**
     * @return panel con el formulario de detalle de la pregunta.
     */
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("FORMULARIO DE PREGUNTA"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtStatement.setLineWrap(true);
        txtStatement.setWrapStyleWord(true);
        txtOptions.setEditable(false);
        txtOptions.setBackground(new Color(245, 245, 245));

        int row = 0;
        addField(panel, row++, "Id:", txtId);
        addField(panel, row++, "Nombre:", txtName);
        addField(panel, row++, "Pregunta:", new JScrollPane(txtStatement));
        addField(panel, row++, "Opciones:", new JScrollPane(txtOptions));
        addField(panel, row++, "Respuesta correcta:", txtCorrectAnswer);
        addField(panel, row++, "Estado actual:", txtCurrentState);
        addField(panel, row++, "Nuevo estado:", cmbNewState);

        txtId.setEditable(false);
        txtName.setEditable(false);
        txtStatement.setEditable(false);
        txtCorrectAnswer.setEditable(false);
        txtCurrentState.setEditable(false);

        GridBagConstraints gbc = baseConstraints();
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(btnUpdate, gbc);

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
        gbc.anchor = GridBagConstraints.NORTHWEST;
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
     * Conecta los eventos de los botones con el controlador.
     */
    private void registerListeners() {
        btnLoad.addActionListener(event -> loadSelectedQuestion());
        btnUpdate.addActionListener(event -> updateState());
    }

    /**
     * Pide al controlador las preguntas y llena el comboBox.
     */
    private void loadQuestionsIntoCombo() {
        cmbQuestions.removeAllItems();
        List<Question> questions = controller.listQuestions();
        for (Question question : questions) {
            cmbQuestions.addItem(question);
        }
        if (!questions.isEmpty()) {
            cmbQuestions.setSelectedIndex(0);
            loadSelectedQuestion();
        }
    }

    /**
     * Carga en el formulario los datos de la pregunta seleccionada.
     */
    private void loadSelectedQuestion() {
        Question selected = (Question) cmbQuestions.getSelectedItem();
        if (selected == null) {
            return;
        }
        Optional<Question> found = controller.findById(selected.getId());
        if (found.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La pregunta ya no existe en el banco",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        showQuestion(found.get());
    }

    /**
     * Vuelca una pregunta en los campos del formulario.
     *
     * @param question pregunta a mostrar.
     */
    private void showQuestion(Question question) {
        QuestionDistractors distractors = question.getDistractors();
        txtId.setText(question.getId());
        txtName.setText(question.getName());
        txtStatement.setText(question.getStatement());

        StringBuilder options = new StringBuilder();
        for (int i = 0; i < distractors.getOptions().size(); i++) {
            options.append(distractors.getLabeledOption(i)).append(System.lineSeparator());
        }
        txtOptions.setText(options.toString().trim());
        txtOptions.setCaretPosition(0);

        txtCorrectAnswer.setText(distractors.getCorrectLetter() + ". " + distractors.getCorrectAnswer());
        txtCurrentState.setText(question.getState().getLabel());
        cmbNewState.setSelectedItem(question.getState());
    }

    /**
     * Solicita al controlador el cambio de estado de la pregunta cargada. El
     * modelo se encarga de notificar a las vistas observadoras.
     */
    private void updateState() {
        String id = txtId.getText();
        if (id.isBlank()) {
            JOptionPane.showMessageDialog(this, "Primero cargue una pregunta",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        QuestionState newState = (QuestionState) cmbNewState.getSelectedItem();
        try {
            boolean updated = controller.changeState(id, newState);
            if (updated) {
                controller.findById(id).ifPresent(this::showQuestion);
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
            controller.findById(id).ifPresent(this::showQuestion);
        }
    }
}
