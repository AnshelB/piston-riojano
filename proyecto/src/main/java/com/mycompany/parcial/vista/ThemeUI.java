package com.mycompany.parcial.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Utilidad de estilos visuales minimalistas y modernos para la interfaz Swing.
 * Mantiene intacta la logica de negocio y el formato de los formularios NetBeans.
 */
public final class ThemeUI {

    // Paleta de colores inspirada en el diseno institucional y moderno
    public static final Color COLOR_PRIMARIO = new Color(204, 0, 0);          // ROJO
    public static final Color COLOR_SUBTITULO = new Color(208, 225, 249);       // #D0E1F9 - Azul cielo claro para subtitulo
    public static final Color COLOR_BORDE_CARD = new Color(226, 232, 240);      // #E2E8F0 - Gris sutil
    public static final Color COLOR_TEXTO_CUERPO = new Color(30, 41, 59);       // #1E293B - Gris pizarra oscuro
    public static final Color COLOR_ROJO_ELIMINAR = new Color(197, 48, 48);     // #C53030 - Rojo sobrio
    public static final Color COLOR_SELECCION_TABLA = new Color(219, 234, 254); // #DBEAFE - Azul tenue para seleccion

    // Tipografias Segoe UI (elegantes y estandar en Windows)

    public static final Font FONT_HEADER_VENTANA = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_SUBTITULO_VENTANA = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TITULO_BORDE = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_BASE = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BASE_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TABLA = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TABLA_HEADER = new Font("Segoe UI", Font.BOLD, 12);

    private ThemeUI() {
    }

    /**
     * Aplica el encabezado institucional azul y los estilos a un formulario JInternalFrame.
     */
    public static void estilizarFormulario(JInternalFrame frame, String titulo, String subtitulo) {
        Container contentPane = frame.getContentPane();
        if (contentPane.getLayout() instanceof BorderLayout borderLayout) {
            Component componenteNorte = borderLayout.getLayoutComponent(BorderLayout.NORTH);
            if (componenteNorte != null) {
                contentPane.remove(componenteNorte);
                JPanel contenedorNorte = new JPanel(new BorderLayout());
                contenedorNorte.add(crearBannerEncabezado(titulo, subtitulo), BorderLayout.NORTH);
                contenedorNorte.add(componenteNorte, BorderLayout.CENTER);
                contentPane.add(contenedorNorte, BorderLayout.NORTH);
            } else {
                contentPane.add(crearBannerEncabezado(titulo, subtitulo), BorderLayout.NORTH);
            }
        }
        estilizarContenedor(contentPane);
        frame.setBackground(Color.WHITE);
    }

    /**
     * Crea la franja superior azul oscura con titulo y descripcion.
     */
    public static JPanel crearBannerEncabezado(String titulo, String subtitulo) {
        JPanel panel = new JPanel(new BorderLayout(4, 2));
        panel.setBackground(COLOR_PRIMARIO);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONT_HEADER_VENTANA);
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(FONT_SUBTITULO_VENTANA);
        lblSub.setForeground(COLOR_SUBTITULO);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblSub, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Aplica recursivamente fuentes, colores de bordes y formatos a los componentes.
     */
    public static void estilizarContenedor(Component comp) {
        if (comp instanceof JComponent jcomp) {
            // Estilizar bordes titulados
            if (jcomp.getBorder() instanceof TitledBorder tb) {
                tb.setTitleFont(FONT_TITULO_BORDE);
                tb.setTitleColor(COLOR_PRIMARIO);
            }

            // Tablas
            if (comp instanceof JTable tabla) {
                tabla.setFont(FONT_TABLA);
                tabla.setRowHeight(26);
                tabla.setSelectionBackground(COLOR_SELECCION_TABLA);
                tabla.setSelectionForeground(COLOR_TEXTO_CUERPO);
                tabla.setGridColor(COLOR_BORDE_CARD);
                tabla.setShowVerticalLines(false);
                tabla.setShowHorizontalLines(true);
                if (tabla.getTableHeader() != null) {
                    tabla.getTableHeader().setFont(FONT_TABLA_HEADER);
                    tabla.getTableHeader().setBackground(new Color(245, 247, 250));
                    tabla.getTableHeader().setForeground(COLOR_TEXTO_CUERPO);
                    ((DefaultTableCellRenderer) tabla.getTableHeader().getDefaultRenderer())
                            .setHorizontalAlignment(SwingConstants.LEFT);
                }
            }

            // Botones
            if (comp instanceof JButton btn) {
                btn.setFont(FONT_BASE_BOLD);
                btn.setFocusPainted(false);
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn.setMargin(new Insets(5, 14, 5, 14));
                String texto = btn.getText();
                if (texto != null && texto.equalsIgnoreCase("Eliminar")) {
                    btn.setForeground(COLOR_ROJO_ELIMINAR);
                }
            }

            // Cajas de texto
            if (comp instanceof JTextField tf) {
                tf.setFont(FONT_BASE);
                tf.setMargin(new Insets(3, 6, 3, 6));
            }

            // Etiquetas
            if (comp instanceof JLabel lbl) {
                if (lbl.getFont() == null || lbl.getFont().getSize() < 14) {
                    lbl.setFont(FONT_BASE);
                }
                if (lbl.getForeground() == null || lbl.getForeground().equals(Color.BLACK)) {
                    lbl.setForeground(COLOR_TEXTO_CUERPO);
                }
            }

            // Desplegables y RadioButtons
            if (comp instanceof JComboBox<?> cb) {
                cb.setFont(FONT_BASE);
            }
            if (comp instanceof JRadioButton rb) {
                rb.setFont(FONT_BASE);
            }
        }

        if (comp instanceof Container container) {
            for (Component hijo : container.getComponents()) {
                estilizarContenedor(hijo);
            }
        }
    }
}
