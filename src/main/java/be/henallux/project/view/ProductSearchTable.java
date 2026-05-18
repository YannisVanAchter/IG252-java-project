package main.java.be.henallux.project.view;

import main.java.be.henallux.project.controller.ProductController;
import main.java.be.henallux.project.controller.ProductSearchController;
import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * A Swing panel that provides a searchable table of products.
 * <p>This view allows users to filter products by name, category, and promotion status,
 * and displays the filtered results in a table format.
 * <p>This view allows users to filter clients by name, category, and promotion
 * and displays the results in a table format.</p>
 * <p>When a row action is triggered, the corresponding product is opened in a detailed view
 * using {@link MainWindow#openProductView(Product)}.</p>
 *
 * @see MainWindow#openProductView(Product)
 * @see ProductSearchController
 * @see ProductController
 * @see ProductTableModel
 */
public class ProductSearchTable extends JPanel {
    private static final int TBL_BTN_SEE = 9;

    private MainWindow mainWindow;
    private ProductSearchController productSearchController;
    private ProductController productController;
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
        this.productSearchController = new ProductSearchController();
        this.productController = new ProductController();
        this.displayProducts = productSearchController.searchProducts(null, null, null);

        setLayout(new BorderLayout(10, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));

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

        txtProductName = ViewUtils.addFilterListener(new JTextField(10), this::onSearchClick);
        JPanel nameFields = new JPanel(new BorderLayout(0, 4));
        nameFields.add(new JLabel("Product name"), BorderLayout.NORTH);
        nameFields.add(txtProductName, BorderLayout.CENTER);

        comboCategory = new JComboBox<>(productController.getCategoryNames());
        comboCategory = ViewUtils.addFilterListener(comboCategory, this::onSearchClick);
        JPanel categoryFields = new JPanel(new BorderLayout(0, 4));
        categoryFields.add(new JLabel("Category"), BorderLayout.NORTH);
        categoryFields.add(comboCategory, BorderLayout.CENTER);

        chkPromotion = ViewUtils.addFilterListener(new JCheckBox("Promotion only"), this::onSearchClick);

        btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> onSearchClick());

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

    private JScrollPane buildTablePanel() {
        model = new ProductTableModel(displayProducts);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (table.columnAtPoint(e.getPoint()) == TBL_BTN_SEE) {
                    onRowClick();
                }
            }
        });

        table.getColumnModel().getColumn(TBL_BTN_SEE).setCellRenderer(new ButtonRenderer());


        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 250));
        return scroll;
    }

    /**
     * Handles the search action triggered by the user.
     * <p>Calls the controller with filter values. Empty fields are converted to {@code null}
     * to indicate no filtering for that criterion.
     * <p>The result updates both the internal {@code displayProducts} list
     * and the table model via {@code ProductTableModel#setProducts(List)}.
     * @see ProductSearchController
     */
    public void onSearchClick() {
        String name = txtProductName.getText().trim();
        String category = (String) comboCategory.getSelectedItem();
        Boolean promo = chkPromotion.isSelected() ? true : null;

        ArrayList<Product> results = productSearchController.searchProducts(
                name.isBlank() ? null : name,
                category == null || category.equals("All") ? null : category,
                promo
        );

        displayProducts = results;
        model.setProducts(new ArrayList<>(results));
    }

    /**
     * Opens the detailed view for the selected product.
     * <p>If no row is selected, this method does nothing.</p>
     * @see MainWindow#openProductView(Product)
     * @see ProductView
     */
    public void onRowClick() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        mainWindow.openProductView(model.getProductAt(selectedRow));
    }
}