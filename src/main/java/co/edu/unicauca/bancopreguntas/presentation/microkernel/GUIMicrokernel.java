package co.edu.unicauca.bancopreguntas.presentation.microkernel;

import co.edu.unicauca.bancopreguntas.microkernel.Question;
import co.edu.unicauca.bancopreguntas.microkernel.QuestionBankBridge;
import co.edu.unicauca.bancopreguntas.microkernel.QuestionMicrokernel;
import co.edu.unicauca.bancopreguntas.microkernel.QuestionRequest;
import co.edu.unicauca.bancopreguntas.microkernel.pipeline.ValidationException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Capa de presentacion (HU03). Interfaz para generar preguntas a traves del
 * microkernel de plugins: el usuario elige un tipo de pregunta, diligencia
 * los datos crudos y el nucleo ejecuta el plugin correspondiente, que a su
 * vez corre la tuberia de filtros de validacion estructural. Las preguntas
 * generadas se incorporan al banco de preguntas de HU02 mediante
 * {@link QuestionBankBridge}.
 */
public class GUIMicrokernel extends JFrame {

    private final QuestionMicrokernel microkernel;
    private final QuestionBankBridge bridge;

    private final JComboBox<String> cmbType = new JComboBox<>(
            new String[]{"MULTIPLE_CHOICE", "CASE_STUDY", "MULTIMEDIA", "EXCEL_IMPORT"});
    private final JTextField txtTitle = new JTextField(28);
    private final JTextArea txtContext = new JTextArea(3, 28);
    private final JTextField txtDirectQuestion = new JTextField(28);
    private final JTextField txtCorrectAnswer = new JTextField(28);
    private final JTextField[] txtDistractors = {
            new JTextField(28), new JTextField(28), new JTextField(28), new JTextField(28)
    };
    private final JTextField txtCompetencia = new JTextField(28);
    private final JTextField txtTema = new JTextField(28);
    private final JTextField txtSubtema = new JTextField(28);
    private final JComboBox<String> cmbNivel = new JComboBox<>(new String[]{"BAJO", "MEDIO", "ALTO"});
    private final JTextField txtMediaUrl = new JTextField(28);
    private final JTextField txtSourceFilePath = new JTextField(28);
    private final JLabel lblMediaUrl = new JLabel("URL del recurso multimedia:");
    private final JLabel lblSourceFilePath = new JLabel("Archivo de origen (Excel):");

    private final DefaultListModel<Question> generatedModel = new DefaultListModel<>();

    public GUIMicrokernel(QuestionMicrokernel microkernel, QuestionBankBridge bridge) {
        this.microkernel = microkernel;
        this.bridge = bridge;
        initComponents();
        refreshGeneratedList();
    }

    private void initComponents() {
        setTitle("HU03 - Generador de preguntas (Microkernel + Tuberias y Filtros)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("GENERADOR DE PREGUNTAS", JLabel.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 16f));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(header, BorderLayout.NORTH);

        add(buildFormPanel(), BorderLayout.CENTER);
        add(buildResultPanel(), BorderLayout.EAST);

        cmbType.addActionListener(e -> updateVisibleFields());
        updateVisibleFields();

        setSize(900, 620);
        setLocationRelativeTo(null);
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos de la pregunta"));

        txtContext.setLineWrap(true);
        txtContext.setWrapStyleWord(true);

        int row = 0;
        addField(panel, row++, "Tipo de pregunta (plugin):", cmbType);
        addField(panel, row++, "Titulo:", txtTitle);
        addField(panel, row++, "Contexto:", new JScrollPane(txtContext));
        addField(panel, row++, "Pregunta directa:", txtDirectQuestion);
        addField(panel, row++, "Respuesta correcta:", txtCorrectAnswer);
        addField(panel, row++, "Distractor 1:", txtDistractors[0]);
        addField(panel, row++, "Distractor 2:", txtDistractors[1]);
        addField(panel, row++, "Distractor 3:", txtDistractors[2]);
        addField(panel, row++, "Distractor 4:", txtDistractors[3]);
        addField(panel, row++, "Competencia:", txtCompetencia);
        addField(panel, row++, "Tema:", txtTema);
        addField(panel, row++, "Subtema (opcional):", txtSubtema);
        addField(panel, row++, "Nivel de dificultad:", cmbNivel);
        addLabeledRow(panel, row++, lblMediaUrl, txtMediaUrl);
        addLabeledRow(panel, row++, lblSourceFilePath, txtSourceFilePath);

        JButton btnGenerate = new JButton("Generar pregunta");
        btnGenerate.addActionListener(e -> generateQuestion());
        GridBagConstraints gbc = baseConstraints();
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(btnGenerate, gbc);

        return panel;
    }

    private JPanel buildResultPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Preguntas generadas por el nucleo"));
        panel.setPreferredSize(new java.awt.Dimension(280, 400));

        JList<Question> list = new JList<>(generatedModel);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);

        JLabel plugins = new JLabel("<html>Plugins registrados:<br>" + microkernel.listPluginNames() + "</html>");
        plugins.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(plugins, BorderLayout.SOUTH);

        return panel;
    }

    private void addField(JPanel panel, int row, String label, java.awt.Component field) {
        addLabeledRow(panel, row, new JLabel(label), field);
    }

    private void addLabeledRow(JPanel panel, int row, JLabel label, java.awt.Component field) {
        GridBagConstraints gbc = baseConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private GridBagConstraints baseConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void updateVisibleFields() {
        String type = (String) cmbType.getSelectedItem();
        boolean isMultimedia = "MULTIMEDIA".equals(type);
        boolean isExcel = "EXCEL_IMPORT".equals(type);
        lblMediaUrl.setVisible(isMultimedia);
        txtMediaUrl.setVisible(isMultimedia);
        lblSourceFilePath.setVisible(isExcel);
        txtSourceFilePath.setVisible(isExcel);
    }

    private void generateQuestion() {
        String type = (String) cmbType.getSelectedItem();

        QuestionRequest request = new QuestionRequest();
        request.setType(type);
        request.setTitle(txtTitle.getText());
        request.setContext(txtContext.getText());
        request.setDirectQuestion(txtDirectQuestion.getText());
        request.setOptions(List.of(
                txtCorrectAnswer.getText(),
                txtDistractors[0].getText(),
                txtDistractors[1].getText(),
                txtDistractors[2].getText(),
                txtDistractors[3].getText()
        ));
        request.setCorrectIndex(0);
        request.setCompetencia(txtCompetencia.getText());
        request.setTema(txtTema.getText());
        request.setSubtema(txtSubtema.getText());
        request.setNivelDificultad((String) cmbNivel.getSelectedItem());
        request.setMediaUrl(txtMediaUrl.getText());
        request.setSourceFilePath(txtSourceFilePath.getText());

        try {
            Question generated = microkernel.executePlugin(type, request);
            bridge.publish(generated, request);
            refreshGeneratedList();
            JOptionPane.showMessageDialog(this,
                    "Pregunta generada y agregada al banco de preguntas:\n" + generated.getId(),
                    "Exito", JOptionPane.INFORMATION_MESSAGE);
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this,
                    "La pregunta no paso la validacion estructural (HU03):\n- "
                            + String.join("\n- ", ex.getErrors()),
                    "Errores de validacion", JOptionPane.WARNING_MESSAGE);
        } catch (NoSuchElementException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshGeneratedList() {
        generatedModel.clear();
        for (Question question : microkernel.getQuestions()) {
            generatedModel.addElement(question);
        }
    }
}
