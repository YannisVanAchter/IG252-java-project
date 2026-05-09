package view;

import controller.ProductController;
import exception.DataValidationException;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * This view displays and manages a searchable table of products.
 * This class allows users to filter products based on name, category, and promotion status
 * and view the filtered results in a table format.
 * Clicking on a row opens a detailed product view through the main application window.
 * @see MainWindow#openProductView(Product)
 */
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
        setBorder(new EmptyBorder(16, 16, 16, 16));

        displayProducts = new ArrayList<>(products);

        JPanel top = new JPanel(new BorderLayout());
        top.add(buildHeader(), BorderLayout.NORTH);
        top.add(buildSearchPanel(), BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JLabel title = new JLabel("Product Search");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.add(title);
        return header;
    }

    private JPanel buildSearchPanel() {

        txtProductName = ViewUtils.addFilterListener(new JTextField(10), this::onFilterClick);
        JPanel nameFields = new JPanel(new BorderLayout(0, 4));
        nameFields.add(new JLabel("Product name"), BorderLayout.NORTH);
        nameFields.add(txtProductName, BorderLayout.CENTER);

        comboCategory = new JComboBox<>(controller.getCategoryNames());
        comboCategory = ViewUtils.addFilterListener(comboCategory, this::onFilterClick);
        JPanel categoryFields = new JPanel(new BorderLayout(0, 4));
        categoryFields.add(new JLabel("Category"), BorderLayout.NORTH);
        categoryFields.add(comboCategory,          BorderLayout.CENTER);

        chkPromotion = ViewUtils.addFilterListener(new JCheckBox("Promotion only"), this::onFilterClick);

        btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> onFilterClick());

        JPanel fieldsColumn = new JPanel();
        fieldsColumn.setLayout(new BoxLayout(fieldsColumn, BoxLayout.Y_AXIS));
        fieldsColumn.setBorder(BorderFactory.createTitledBorder("Filters"));

        fieldsColumn.add(ViewUtils.makeRow(nameFields));
        fieldsColumn.add(Box.createVerticalStrut(6));
        fieldsColumn.add(ViewUtils.makeRow(categoryFields));
        fieldsColumn.add(Box.createVerticalStrut(6));
        fieldsColumn.add(ViewUtils.makeRow(chkPromotion));
        fieldsColumn.add(Box.createVerticalStrut(8));
        fieldsColumn.add(ViewUtils.makeRow(btnSearch));

        return fieldsColumn;
    }

    /**
     * Enveloppe un composant dans un JPanel BorderLayout CENTER
     * pour qu'il s'étire horizontalement, avec une hauteur fixe.
     */
    private JPanel makeRow(JComponent component) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBorder(new EmptyBorder(0, 6, 0, 6));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                component.getPreferredSize().height + 4));
        row.add(component, BorderLayout.CENTER);
        return row;
    }

    private JScrollPane buildTablePanel() {
        model = new ProductTableModel(displayProducts);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (table.columnAtPoint(e.getPoint()) == 5) {
                    onRowClick();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 250));
        return scroll;
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

            if (match) displayProducts.add(p);
        }

        model.setProducts(displayProducts);
    }

    public void onRowClick() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        Product product = displayProducts.get(selectedRow);
        mainWindow.openProductView(product);
    }
}