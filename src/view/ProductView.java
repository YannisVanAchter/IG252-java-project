package view;

import model.Product;

import javax.swing.*;
import java.awt.*;

/**
 * ProductView displaying detailed information about a single Product.
 *
 * This view is dynamically rebuilt every time a product is loaded using {@link #loadProduct(Product)}.
 * It organizes product data into logical sections: general information, stock information, and promotions.
 *
 * The view also provides a footer with navigation controls to return to the previous screen.
 */
public class ProductView extends JPanel {

    private static final String LABEL_NO_DATA = "N/A";

    private final MainWindow mainWindow;
    private Product product;


    public ProductView(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout());
    }

    /**
     * Loads a product into the view and rebuilds the UI.
     * The method is call in {@link MainWindow#openProductView(Product)}
     * clike on product in Jtable of {@link ProductSearchTable}
     *
     * If the product is null, the user is notified and the view navigates back automatically.
     *
     * The method clears the current UI and reconstructs all components:
     * title, product information, stock information, promotion section, and footer.
     *
     * The main contents are created in {@link #buildContent()}.
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

        card.add(labelValue("Discount", product.getPromotion().getDiscount()));
        card.add(labelValue("Required quantity", String.valueOf(product.getPromotion().getRequiredQuantity())));
        card.add(labelValue("Start date", product.getPromotion().getStartDate()));
        card.add(labelValue("End date", product.getPromotion().getEndDate()));
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
     * Creates a bordered card container with a title.
     *
     * @param title the title of the section
     * @return a styled panel
     */
    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createTitledBorder(title));
        return card;
    }

    /**
     * Creates a key-value label row.
     *
     * @param label the field name
     * @param value the field value
     * @return a horizontal panel displaying the label and value
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