package co.edu.unicauca.bancopreguntas.presentation;

import co.edu.unicauca.bancopreguntas.domain.QuestionState;
import co.edu.unicauca.bancopreguntas.domain.QuestionStatistics;
import co.edu.unicauca.bancopreguntas.infra.Observer;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.EnumMap;
import java.util.Map;

/**
 * Capa de presentacion.
 *
 * Primera vista observadora: muestra cuantas preguntas hay en cada estado. Se
 * repinta sola cuando el modelo notifica un cambio.
 */
public class GUIObserver1 extends JFrame implements Observer {

    private final Map<QuestionState, JLabel> valueLabels = new EnumMap<>(QuestionState.class);
    private final JLabel lblTotal = new JLabel();

    public GUIObserver1() {
        initComponents();
    }

    /**
     * Construye la interfaz grafica.
     */
    private void initComponents() {
        setTitle("Vista de estadisticas");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Preguntas por estado", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(lblTitle, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(QuestionState.values().length + 1, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        for (QuestionState state : QuestionState.values()) {
            JLabel value = new JLabel("0");
            value.setFont(value.getFont().deriveFont(Font.BOLD));
            valueLabels.put(state, value);
            panel.add(new JLabel(state.getLabel() + ":"));
            panel.add(value);
        }
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD));
        lblTotal.setText("0");
        panel.add(new JLabel("Total:"));
        panel.add(lblTotal);
        add(panel, BorderLayout.CENTER);

        setSize(320, 220);
        setLocation(570, 30);
    }

    /**
     * Recibe las estadisticas publicadas por el sujeto y refresca las etiquetas.
     *
     * @param data instancia de QuestionStatistics enviada por el modelo.
     */
    @Override
    public void update(Object data) {
        if (!(data instanceof QuestionStatistics statistics)) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            for (QuestionState state : QuestionState.values()) {
                valueLabels.get(state).setText(String.valueOf(statistics.getCount(state)));
            }
            lblTotal.setText(String.valueOf(statistics.getTotal()));
        });
    }
}
