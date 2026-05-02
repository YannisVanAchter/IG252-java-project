package view;

import controller.ProductController;
import exception.DataValidationException;
import model.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ProductSearchTable extends JPanel {
    private MainWindow mainWindow;
    private ProductController controller;
    private ProductTableModel model;
    private ArrayList<Product> products;
    private ArrayList<Product> displayProducts;

    private JPanel searchPanel, tablePanel;
    private JTextField txtProductName;
    private JComboBox<String> comboCategory;
    private JCheckBox chkPromotion;
    private JButton btnSearch;

    private JTable table;

    public ProductSearchTable(MainWindow mainWindow) throws DataValidationException {
        this.mainWindow = mainWindow;
        this.controller = new ProductController();
        this.products = controller.getAllProduct();

        setLayout(new BorderLayout(10, 16));

        displayProducts = new ArrayList<>(products);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildSearchPanel(), BorderLayout.CENTER);
        add(buildTablePanel(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JLabel title = new JLabel("Product Search");
        title.setFont(new Font("Inter", Font.BOLD, 20));

        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.add(title, BorderLayout.NORTH);
        return header;
    }

    private JPanel buildSearchPanel() {
        JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        txtProductName = new JTextField(10);
        txtProductName.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { onFilterClick(); }
            public void removeUpdate(DocumentEvent e) { onFilterClick(); }
            public void changedUpdate(DocumentEvent e) { onFilterClick(); }
        });
        fieldsPanel.add(labeled("Product name", txtProductName));

        comboCategory = new JComboBox<>(controller.getCategoryNames());
        comboCategory.addActionListener(e -> onFilterClick());
        fieldsPanel.add(labeled("Category", comboCategory));

        chkPromotion = new JCheckBox("Promotion only");
        chkPromotion.addActionListener(e -> onFilterClick());
        fieldsPanel.add(labeled("", chkPromotion));

        btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> onFilterClick());
        fieldsPanel.add(btnSearch);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(fieldsPanel, BorderLayout.CENTER);
        return panel;
    }


    private JPanel buildTablePanel() {
        model = new ProductTableModel(displayProducts);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (table.columnAtPoint(e.getPoint()) == 5) {
                    onRowClik();
                }
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    public void onFilterClick() {
        String nameText = txtProductName.getText().trim().toLowerCase();
        String selectedCategory = (String) comboCategory.getSelectedItem();
        boolean promoOnly = chkPromotion.isSelected();

        displayProducts = new ArrayList<>();

        for (Product p : products) {
            boolean match = true;

            if (!nameText.isEmpty()
                    && !p.getName().toLowerCase().contains(nameText)) {
                match = false;
            }

            if (selectedCategory != null && !selectedCategory.equals("All")) {
                if (p.getCategory() == null
                        || p.getCategory().getName() == null
                        || !selectedCategory.equalsIgnoreCase(p.getCategory().getName())) {
                    match = false;
                }
            }

            if (promoOnly && !p.isInPromotion()) {
                match = false;
            }

            if (match) {
                displayProducts.add(p);
            }
        }

        model.setProducts(displayProducts);
    }

    public void onRowClik() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product.");
            return;
        }

        Product product = displayProducts.get(selectedRow);

        mainWindow.openProductView(product);
    }

    private JPanel labeled(String text, JComponent comp) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(new JLabel(text));
        p.add(Box.createVerticalStrut(4));
        p.add(comp);
        return p;
    }
}