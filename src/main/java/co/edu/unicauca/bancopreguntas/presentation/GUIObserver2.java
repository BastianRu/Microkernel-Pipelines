package co.edu.unicauca.bancopreguntas.presentation;

import co.edu.unicauca.bancopreguntas.domain.QuestionState;
import co.edu.unicauca.bancopreguntas.domain.QuestionStatistics;
import co.edu.unicauca.bancopreguntas.infra.Observer;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.util.EnumMap;
import java.util.Map;

/**
 * Capa de presentacion.
 *
 * Segunda vista observadora: grafica en pastel con el porcentaje de preguntas
 * por estado. Se dibuja con Graphics2D, sin librerias externas.
 */
public class GUIObserver2 extends JFrame implements Observer {

    /** Color de fondo del grafico; tambien separa los sectores entre si. */
    private static final Color SURFACE = new Color(0xFC, 0xFC, 0xFB);
    private static final Color TEXT_SECONDARY = new Color(0x5C, 0x5C, 0x5C);

    /** Paleta categorica: un color fijo por estado, nunca reasignado. */
    private static final Map<QuestionState, Color> COLORS = new EnumMap<>(QuestionState.class);

    static {
        COLORS.put(QuestionState.BORRADOR, new Color(0x2A, 0x78, 0xD6));
        COLORS.put(QuestionState.PENDIENTE_REVISION, new Color(0xEB, 0x68, 0x34));
        COLORS.put(QuestionState.ELIMINADA, new Color(0x1B, 0xAF, 0x7A));
    }

    private final PieChartPanel chartPanel = new PieChartPanel();
    private final Map<QuestionState, JLabel> legendLabels = new EnumMap<>(QuestionState.class);

    private QuestionStatistics statistics = new QuestionStatistics(null);

    public GUIObserver2() {
        initComponents();
    }

    /**
     * Construye la interfaz grafica.
     */
    private void initComponents() {
        setTitle("Vista grafica");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Distribucion de preguntas por estado", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(lblTitle, BorderLayout.NORTH);

        add(chartPanel, BorderLayout.CENTER);
        add(buildLegend(), BorderLayout.SOUTH);

        setSize(380, 430);
        setLocation(570, 280);
    }

    /**
     * @return leyenda con el nombre, conteo y porcentaje de cada estado. Las
     *         etiquetas visibles evitan que la identidad dependa solo del color.
     */
    private JPanel buildLegend() {
        JPanel legend = new JPanel(new java.awt.GridLayout(QuestionState.values().length, 1, 0, 4));
        legend.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));
        for (QuestionState state : QuestionState.values()) {
            JLabel label = new JLabel(legendText(state));
            label.setForeground(TEXT_SECONDARY);
            label.setIcon(new SwatchIcon(COLORS.get(state)));
            label.setIconTextGap(8);
            legendLabels.put(state, label);
            legend.add(label);
        }
        return legend;
    }

    /**
     * @param state estado consultado.
     * @return texto de la leyenda para ese estado.
     */
    private String legendText(QuestionState state) {
        return String.format("%s: %.1f%% (%d)",
                state.getLabel(), statistics.getPercentage(state), statistics.getCount(state));
    }

    /**
     * Recibe las estadisticas publicadas por el sujeto y redibuja el pastel.
     *
     * @param data instancia de QuestionStatistics enviada por el modelo.
     */
    @Override
    public void update(Object data) {
        if (!(data instanceof QuestionStatistics newStatistics)) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            this.statistics = newStatistics;
            for (QuestionState state : QuestionState.values()) {
                legendLabels.get(state).setText(legendText(state));
            }
            chartPanel.repaint();
        });
    }

    /**
     * Panel que dibuja el grafico de pastel.
     */
    private class PieChartPanel extends JPanel {

        private static final int MARGIN = 25;

        PieChartPanel() {
            setBackground(SURFACE);
            setPreferredSize(new Dimension(320, 240));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int diameter = Math.min(getWidth(), getHeight()) - 2 * MARGIN;
            if (diameter <= 0) {
                g2.dispose();
                return;
            }
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;

            if (statistics.getTotal() == 0) {
                drawEmptyState(g2, x, y, diameter);
                g2.dispose();
                return;
            }

            double start = 90.0;
            for (QuestionState state : QuestionState.values()) {
                double extent = -(statistics.getPercentage(state) * 360.0) / 100.0;
                if (extent == 0.0) {
                    continue;
                }
                g2.setColor(COLORS.get(state));
                g2.fill(new Arc2D.Double(x, y, diameter, diameter, start, extent, Arc2D.PIE));
                start += extent;
            }

            // Separador de 2px del color de la superficie entre sectores.
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(SURFACE);
            start = 90.0;
            for (QuestionState state : QuestionState.values()) {
                double extent = -(statistics.getPercentage(state) * 360.0) / 100.0;
                if (extent == 0.0) {
                    continue;
                }
                g2.draw(new Arc2D.Double(x, y, diameter, diameter, start, extent, Arc2D.PIE));
                start += extent;
            }
            g2.dispose();
        }

        /**
         * Dibuja el estado vacio cuando no hay preguntas contabilizadas.
         */
        private void drawEmptyState(Graphics2D g2, int x, int y, int diameter) {
            g2.setColor(new Color(0xE0, 0xE0, 0xDE));
            g2.fill(new Ellipse2D.Double(x, y, diameter, diameter));
            g2.setColor(TEXT_SECONDARY);
            String message = "Sin preguntas";
            int width = g2.getFontMetrics().stringWidth(message);
            g2.drawString(message, x + (diameter - width) / 2, y + diameter / 2);
        }
    }

    /**
     * Cuadro de color usado en la leyenda.
     */
    private static class SwatchIcon implements javax.swing.Icon {

        private static final int SIZE = 12;
        private final Color color;

        SwatchIcon(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(java.awt.Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(x, y, SIZE, SIZE, 4, 4);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        public int getIconHeight() {
            return SIZE;
        }
    }
}
