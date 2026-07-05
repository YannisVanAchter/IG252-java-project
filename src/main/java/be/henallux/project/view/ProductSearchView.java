package be.henallux.project.view;

import be.henallux.project.model.Discount;
import be.henallux.project.model.Product;
import be.henallux.project.model.QuantityProduct;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * A Swing-based view that displays detailed information about a single {@link Product}.
 * <p>This panel is dynamically rebuilt each time a product is loaded via {@link #loadProduct(Product)}.
 * <p>The view organizes product information into three main sections:
 * general product details, stock information, and promotion details.
 * <p>A footer provides navigation controls to return to the previous screen using {@link MainWindow#goBack()}.
 *
 * @see Product
 * @see ProductSearchTable
 * @see MainWindow
 */
public class ProductSearchView extends JPanel {

    private static final String LABEL_NO_DATA = "N/A";
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 20);

    private final MainWindow mainWindow;
    private Product product;


    public ProductSearchView(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(8, 16, 8, 16));
    }

    private void build() {
        removeAll();

        JPanel top = new JPanel(new BorderLayout());
        top.add(buildContent(), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(top);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(0, 250));

        add(scrollPane, BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    /**
     * Loads a product into the view and rebuilds all UI components.
     * <p>This method is triggered when a product is selected in the JTable of {@link ProductSearchTable}
     * via {@link MainWindow#openProductView(Product)}.
     * <p>If the provided product is {@code null}, a warning dialog is displayed
     * and the application automatically navigates back using {@link MainWindow#goBack()}.
     * <p>When valid, the method clears the current UI and rebuilds the full view:
     * title, product information, stock section, promotion section, and footer.
     * <p>The main content layout is generated in {@link #buildContent()}.
     *
     * @param product the {@link Product} to display, or {@code null} if no selection was made
     * @see MainWindow#openProductView(Product)
     * @see MainWindow#goBack()
     */
    public void loadProduct(Product product) {
        this.product = product;

        if (product == null) {
            JOptionPane.showMessageDialog(this, "Please select a product.");
            mainWindow.goBack();
            return;
        }

        build();
    }

    private JPanel buildContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(0, 0, 8, 0));
        panel.add(buildTitle());
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildProductInfo());
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildStockInfo());
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildPromotion());
        return panel;
    }

    private JPanel buildTitle() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(ViewUtils.safeText(product.getName()));
        label.setFont(FONT_TITLE);
        panel.add(label);
        return panel;
    }


    private JPanel buildProductInfo() {
        JPanel card = createCard("Product Information");
        card.add(labelValue("Product label", product.getName()));
        card.add(labelValue("Category", product.getCategory() != null ? product.getCategory().getLabel() : LABEL_NO_DATA));
        card.add(labelValue("Price (Excl. Tax)", product.getPriceEVAT() != null ? formatPrice(product.getPriceEVAT().floatValue()) : "N/A"));
        card.add(labelValue("Price (Incl. Tax)", formatPrice(product.getPrice())));
        card.add(labelValue("VAT", product.getVat() + "%"));
        card.add(labelValue("Loyalty points", product.getFidelityPoint() + " pts"));
        card.add(labelValue("Edible", product.getIsEdible() ? "Yes" : "No"));
        return card;
    }

    private JPanel buildStockInfo() {
        JPanel card = createCard("Stock Information");

        List<QuantityProduct> locations = product.getLocation();

        if (locations == null || locations.isEmpty()) {
            card.add(labelValue("No stock data available", LABEL_NO_DATA));
            return card;
        }

        card.add(labelValue("Total quantity", String.valueOf(product.getTotalQuantity())));
        card.add(labelValue("Stock quantity", String.valueOf(product.getStockQuantity())));
        card.add(labelValue("Non-stock quantity", String.valueOf(product.getNonStockQuantity())));
        card.add(labelValue("Minimum threshold", String.valueOf(product.getMinStockQuantity())));

        for (QuantityProduct qp : locations) {
            JPanel locationCard = createCard("Location " + (qp.getLocationProduct() != null ? qp.getLocationProduct().getLabel() : LABEL_NO_DATA));
            locationCard.add(labelValue("Quantity", String.valueOf(qp.getQuantity())));
            card.add(locationCard);
        }

        return card;
    }

    private JPanel buildPromotion() {
        JPanel card = createCard("Promotion");

        List<Discount> discounts = product.getDiscounts();
        if (discounts == null || discounts.isEmpty()) {
            card.add(labelValue("No promotion available", LABEL_NO_DATA));
            return card;
        }

        Discount current = product.getCurrentDiscount();
        if (current != null) {
            card.add(labelValue("Discount", current.getDiscountPercentage() + "%"));
            card.add(labelValue("Required quantity", String.valueOf(current.getRequiredQuantity())));
            card.add(labelValue("Start date", ViewUtils.formatDate(current.getStartDate())));
            card.add(labelValue("End date", ViewUtils.formatDate(current.getEndDate())));
        }
        for (Discount discount : discounts) {
            if (current != null && !current.equals(discount)) {
                JPanel promoCard = createCard("Previous");
                promoCard.add(labelValue("Date", ViewUtils.formatDate(discount.getStartDate()) + " - " + ViewUtils.formatDate(discount.getEndDate())));
                promoCard.add(labelValue("Discount", discount.getDiscountPercentage() + "%"));
                promoCard.add(labelValue("Required quantity", String.valueOf(discount.getRequiredQuantity())));
                card.add(promoCard);
            }
        }
        return card;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = new JButton("Back");
        ViewUtils.setCursor(back);
        back.addActionListener(e -> mainWindow.goBack());
        panel.add(back);
        return panel;
    }

    /**
     * Creates a titled container panel used as a visual section card.
     *
     * @param title the title displayed on the card border
     * @return a styled JPanel configured with a vertical layout
     */
    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createTitledBorder(title));
        return card;
    }

    /**
     * Creates a horizontal label-value row.
     * <p>This helper method is used to display product attributes in a consistent format.
     *
     * @param label the field name
     * @param value the field value
     * @return a JPanel containing a formatted key-value display
     */
    private JPanel labelValue(String label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(label + " : "));
        panel.add(new JLabel(ViewUtils.safeText(value, LABEL_NO_DATA)));
        return panel;
    }

    private String formatPrice(float price) {
        return String.format("%.2f €", price);
    }

    public void refresh() {

    }
}