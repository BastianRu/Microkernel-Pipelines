package co.edu.unicauca.bancopreguntas.presentation;

import co.edu.unicauca.bancopreguntas.domain.Question;
import co.edu.unicauca.bancopreguntas.domain.QuestionDistractors;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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
 * Vista de consulta del micro patron MVC: permite seleccionar una pregunta del
 * banco y cargar su detalle en modo solo lectura. No modifica el modelo, el
 * cambio de estado vive en {@link GUIQuestionUpdate}.
 *
 * Al ser observadora del modelo, refresca la lista y el detalle cuando otra
 * vista cambia el estado de una pregunta o el microkernel agrega una nueva.
 */
public class GUIQuestionLoad extends JFrame implements Observer {

    private final QuestionController controller;

    private final JComboBox<Question> cmbQuestions = new JComboBox<>();
    private final JTextField txtId = new JTextField();
    private final JTextField txtName = new JTextField();
    private final JTextArea txtStatement = new JTextArea(3, 20);
    private final JTextArea txtOptions = new JTextArea(4, 20);
    private final JTextField txtCorrectAnswer = new JTextField();
    private final JTextField txtCurrentState = new JTextField();
    private final JButton btnLoad = new JButton("Cargar pregunta");

    /** Evita reaccionar a los eventos del comboBox mientras se repuebla. */
    private boolean reloading;

    /**
     * @param controller controlador del MVC.
     */
    public GUIQuestionLoad(QuestionController controller) {
        this.controller = controller;
        initComponents();
        registerListeners();
        reloadQuestions();
    }

    /**
     * Construye la interfaz grafica.
     */
    private void initComponents() {
        setTitle("Banco de Preguntas Saber Pro - Consulta de preguntas");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("CONSULTA DE PREGUNTAS", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(lblTitle, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        center.add(buildSelectorPanel());
        center.add(Box.createVerticalStrut(10));
        center.add(buildDetailPanel());
        add(center, BorderLayout.CENTER);

        setSize(520, 560);
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
     * @return panel con el detalle de la pregunta, todo en solo lectura.
     */
    private JPanel buildDetailPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("DETALLE DE LA PREGUNTA"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtStatement.setLineWrap(true);
        txtStatement.setWrapStyleWord(true);
        txtOptions.setBackground(new Color(245, 245, 245));

        int row = 0;
        addField(panel, row++, "Id:", txtId);
        addField(panel, row++, "Nombre:", txtName);
        addField(panel, row++, "Pregunta:", new JScrollPane(txtStatement));
        addField(panel, row++, "Opciones:", new JScrollPane(txtOptions));
        addField(panel, row++, "Respuesta correcta:", txtCorrectAnswer);
        addField(panel, row, "Estado actual:", txtCurrentState);

        txtId.setEditable(false);
        txtName.setEditable(false);
        txtStatement.setEditable(false);
        txtOptions.setEditable(false);
        txtCorrectAnswer.setEditable(false);
        txtCurrentState.setEditable(false);

        return panel;
    }

    /**
     * Agrega una fila etiqueta/campo al detalle.
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
     * Conecta los eventos de la vista con el controlador.
     */
    private void registerListeners() {
        btnLoad.addActionListener(event -> loadSelectedQuestion());
    }

    /**
     * Pide al controlador las preguntas, llena el comboBox conservando la
     * seleccion actual y refresca el detalle mostrado.
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
            clearDetail();
            return;
        }
        cmbQuestions.setSelectedIndex(indexOf(questions, selectedId));
        loadSelectedQuestion();
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
     * Carga en el detalle los datos de la pregunta seleccionada.
     */
    private void loadSelectedQuestion() {
        if (reloading) {
            return;
        }
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
     * Vuelca una pregunta en los campos del detalle.
     *
     * @param question pregunta a mostrar.
     */
    private void showQuestion(Question question) {
        QuestionDistractors distractors = question.getDistractors();
        txtId.setText(question.getId());
        txtName.setText(question.getName());
        txtStatement.setText(question.getStatement());
        txtStatement.setCaretPosition(0);

        StringBuilder options = new StringBuilder();
        for (int i = 0; i < distractors.getOptions().size(); i++) {
            options.append(distractors.getLabeledOption(i)).append(System.lineSeparator());
        }
        txtOptions.setText(options.toString().trim());
        txtOptions.setCaretPosition(0);

        txtCorrectAnswer.setText(distractors.getCorrectLetter() + ". " + distractors.getCorrectAnswer());
        txtCurrentState.setText(question.getState().getLabel());
    }

    /**
     * Deja el detalle en blanco cuando el banco no tiene preguntas.
     */
    private void clearDetail() {
        txtId.setText("");
        txtName.setText("");
        txtStatement.setText("");
        txtOptions.setText("");
        txtCorrectAnswer.setText("");
        txtCurrentState.setText("");
    }

    /**
     * El modelo publico un cambio: se vuelve a leer el banco para que la lista
     * y el detalle queden al dia.
     *
     * @param data estadisticas publicadas por el modelo, no se usan aqui.
     */
    @Override
    public void update(Object data) {
        SwingUtilities.invokeLater(this::reloadQuestions);
    }
}
