package vallegrande.edu.pe.view;

import vallegrande.edu.pe.controller.ContactController;
import vallegrande.edu.pe.model.Contact;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Vista principal con Swing que muestra la lista de contactos y botones.
 * Estilo visual moderno con bordes redondeados, emojis, tabla clara y notificaciones.
 */
public class ContactView extends JFrame {
    private final ContactController controller;
    private DefaultTableModel tableModel;
    private JTable table;

    public ContactView(ContactController controller) {
        super("📒 Agenda MVC Swing - Vallegrande");
        this.controller = controller;
        initUI();
        loadContacts();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        Font baseFont = new Font("Segoe UI Emoji", Font.PLAIN, 18);
        Color backgroundColor = new Color(245, 245, 245);
        Color headerColor = new Color(230, 240, 255);
        Color footerColor = new Color(240, 240, 240);
        Color textColor = new Color(51, 51, 51);

        RoundedPanel contentPanel = new RoundedPanel(20);
        contentPanel.setLayout(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPanel.setBackground(backgroundColor);
        setContentPane(contentPanel);

        // Encabezado
        RoundedPanel headerPanel = new RoundedPanel(20);
        headerPanel.setBackground(headerColor);
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel welcomeLabel = new JLabel("👋 Bienvenido a tu Agenda Vallegrande");
        welcomeLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 26));
        welcomeLabel.setForeground(new Color(25, 118, 210));
        headerPanel.add(welcomeLabel);
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabla
        tableModel = new DefaultTableModel(new String[]{"🆔 ID", "👤 Nombre", "📧 Email", "📞 Teléfono"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFont(baseFont);
        table.setRowHeight(45);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(200, 230, 255));
        table.setSelectionForeground(textColor);
        table.getTableHeader().setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        table.getTableHeader().setBackground(new Color(33, 150, 243));
        table.getTableHeader().setForeground(Color.WHITE);

        // Alternancia de filas
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                }
                return c;
            }
        });

        // Ordenar columnas
        table.setAutoCreateRowSorter(true);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Pie de página
        RoundedPanel buttonsPanel = new RoundedPanel(20);
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonsPanel.setBackground(footerColor);
        buttonsPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        RoundedButton addBtn = new RoundedButton("➕ Agregar", new Color(0, 123, 255), 20);
        RoundedButton editBtn = new RoundedButton("✏️ Editar", new Color(255, 193, 7), 20);
        RoundedButton deleteBtn = new RoundedButton("🗑️ Eliminar", new Color(220, 53, 69), 20);

        buttonsPanel.add(addBtn);
        buttonsPanel.add(editBtn);
        buttonsPanel.add(deleteBtn);
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> showAddContactDialog());
        editBtn.addActionListener(e -> editSelectedContact());
        deleteBtn.addActionListener(e -> deleteSelectedContact());
    }

    private void loadContacts() {
        tableModel.setRowCount(0);
        List<Contact> contacts = controller.list();
        for (Contact c : contacts) {
            tableModel.addRow(new Object[]{c.id(), c.name(), c.email(), c.phone()});
        }
    }

    private void showAddContactDialog() {
        AddContactDialog dialog = new AddContactDialog(this, controller);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            loadContacts();
            showToast("✅ Contacto agregado correctamente");
        }
    }

    private void editSelectedContact() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Seleccione un contacto para editar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        showAddContactDialog(); // Placeholder
    }

    private void deleteSelectedContact() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Seleccione un contacto para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "❓ ¿Seguro que desea eliminar este contacto?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.delete(id);
            loadContacts();
            showToast("🗑️ Contacto eliminado");
        }
    }

    private void showToast(String message) {
        JWindow toast = new JWindow();
        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        label.setBackground(new Color(76, 175, 80));
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(10, 20, 10, 20));
        toast.add(label);
        toast.pack();
        toast.setLocationRelativeTo(this);
        toast.setVisible(true);

        new Timer(2000, e -> toast.setVisible(false)).start();
    }

    // Panel con esquinas redondeadas
    static class RoundedPanel extends JPanel {
        private final int radius;

        public RoundedPanel(int radius) {
            super();
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(getBackground());
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
        }
    }

    // Botón con fondo redondeado
    static class RoundedButton extends JButton {
        private final Color baseColor;
        private final int radius;
        private final Dimension normalSize = new Dimension(160, 50);
        private final Dimension zoomSize = new Dimension(170, 55);

        public RoundedButton(String text, Color baseColor, int radius) {
            super(text);
            this.baseColor = baseColor;
            this.radius = radius;
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(normalSize);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setPreferredSize(zoomSize);
                    revalidate();
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setPreferredSize(normalSize);
                    revalidate();
                    repaint();
                }
            });
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(baseColor);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g);
            g2.dispose();
        }
    }
}