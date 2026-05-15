package view;

import model.Product;

import javax.swing.*;
import java.awt.*;

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
public class ProductView extends JPanel {

    private static final String LABEL_NO_DATA = "N/A";

    private MainWindow mainWindow;
    private Product product;


    public ProductView(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout());
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
     * @param product the product to display
     */
    public void loadProduct(Product product) {
        this.product = product;

        if (product == null) {
            JOptionPane.showMessageDialog(this, "Please select a product.");
            mainWindow.goBack();
            return;
        }

        removeAll();
        add(new JScrollPane(buildContent()), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private JPanel buildContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(buildTitle());
        panel.add(buildProductInfo());
        panel.add(buildStockInfo());
        panel.add(buildPromotion());
        return panel;
    }

    private JPanel buildTitle() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(product.getName());
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label);
        return panel;
    }


    private JPanel buildProductInfo() {
        JPanel card = createCard("Product Information");
        card.add(labelValue("Product label", product.getName()));
        card.add(labelValue("Category", product.getCategory().toString()));
        card.add(labelValue("Price (Excl. Tax)", formatPrice(product.getPrice())));
        card.add(labelValue("VAT", product.getVat() + "%"));
        card.add(labelValue("Loyalty points", product.getPoints() + " pts"));
        return card;
    }

    private JPanel buildStockInfo() {
        JPanel card = createCard("Stock Information");

        if (product.getQuantity() == null) {
            card.add(labelValue("No stock data available", LABEL_NO_DATA));
            return card;
        }

        card.add(labelValue("Number of products", String.valueOf(product.getQuantity().getNbProduct())));
        card.add(labelValue("Minimum threshold", String.valueOf(product.getMinStock())));
        card.add(labelValue("Shelf", String.valueOf(product.getQuantity().getShelf())));
        card.add(labelValue("Floor", String.valueOf(product.getQuantity().getFloor())));
        card.add(labelValue("Refrigerated", product.getQuantity().getLocation().getIsRefrigerated() ? "yes" : "no"));
        return card;
    }

    private JPanel buildPromotion() {
        JPanel card = createCard("Promotion");

        if (product.getPromotion() == null) {
            card.add(labelValue("No promotion available", LABEL_NO_DATA));
            return card;
        }

        card.add(labelValue("Discount", String.valueOf(product.getPromotion().getDiscountPercentage())));
        card.add(labelValue("Required quantity", String.valueOf(product.getPromotion().getRequiredQuantity())));
        card.add(labelValue("Start date", String.valueOf(product.getPromotion().getStartDate())));
        card.add(labelValue("End date", String.valueOf(product.getPromotion().getEndDate())));
        return card;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = new JButton("Back");
        back.addActionListener(e -> mainWindow.goBack());
        panel.add(back);
        return panel;
    }

    /**
     * Creates a titled container panel used as a visual section card.
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
        panel.add(new JLabel(value));
        return panel;
    }

    private String formatPrice(double price) {
        return price + "€";
    }
}