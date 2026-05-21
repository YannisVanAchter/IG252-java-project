package main.java.be.henallux.project.view;

import main.java.be.henallux.project.*;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.Discount;
import main.java.be.henallux.project.model.FidelityCard;
import main.java.be.henallux.project.model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * ReceiptPayment is a Swing panel responsible for displaying the final payment and receipt summary before transaction validation.
 * <p>This view is the final step of the checkout workflow and allows the user to:
 * <ul><li>Review client information</li>
 *     <li>Apply loyalty points discounts</li>
 *     <li>Display purchased products and promotions</li>
 *     <li>Calculate totals, discounts, and VAT</li>
 *     <li>Select a payment method</li>
 *     <li>Validate and complete the payment</li></ul>
 *
 * <p>The receipt data is stored using a {@link java.util.LinkedHashMap}
 * where {@code Key} is {@link main.java.be.henallux.project.model.Product}. {@code Value} is {@code Integer} representing purchased quantity.
 *
 * @see main.java.be.henallux.project.model.Product
 * @see main.java.be.henallux.project.model.ClientSupplier
 * @see main.java.be.henallux.project.model.Discount
 * @see ReceiptView
 * @see java.util.LinkedHashMap
 */
public class ReceiptPayment extends JPanel {
    private static final Font FONT_REG = new Font("SansSerif", Font.PLAIN, 16);
    private static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 18);
    private static final Font FONT_TOTAL = new Font("SansSerif", Font.BOLD, 18);
    private static final Font FONT_PROMO = new Font("SansSerif", Font.ITALIC, 14);
    private static final Color COLOR_PROMO = new Color(0, 140, 0);

    private final MainWindow mainWindow;
    private final ReceiptView receiptView;
    private final ClientSupplier clientSupplier;
    private final LinkedHashMap<Product, Integer> receipt;
    private int pointsUsed;

    private JPanel ticketPanel;
    private JLabel lblPointsUsed;
    private JButton btnPay;

    public ReceiptPayment(MainWindow mainWindow, ReceiptView receiptView, ClientSupplier clientSupplier, LinkedHashMap<Product, Integer> receipt) {
        this.mainWindow = mainWindow;
        this.receiptView = receiptView;
        this.clientSupplier = clientSupplier;
        this.receipt = receipt;
        this.pointsUsed = 0;

        setLayout(new BorderLayout(10, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.add(buildClientPanel());
        center.add(Box.createVerticalStrut(12));
        center.add(buildLoyaltySection());
        center.add(Box.createVerticalStrut(12));
        center.add(buildTicketSection());
        center.add(Box.createVerticalStrut(12));
        center.add(buildPaymentSection());

        JScrollPane scrollPane = new JScrollPane(center);
        scrollPane.setBorder(null);
        scrollPane.setViewportBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
    }

    /**
     * Builds the client information section.
     * <p>If a client is associated with the receipt, the panel displays the Full name and Email address
     * <p>If no client is selected, a placeholder message is shown.
     *
     * @return configured client information panel
     * @see #row(String, String)
     */
    private JPanel buildClientPanel() {
        JPanel panel = section("Client Information");
        if (clientSupplier != null) {
            panel.add(row("Name:", clientSupplier.getName() + " " + clientSupplier.getFirstname()));
            panel.add(row("Email:", clientSupplier.getEmail()));
        } else {
            panel.add(row("No client selected", null));
        }
        return panel;
    }

    /**
     * Builds the loyalty card section.
     * <p>This section allows the user to view available loyalty points on the card
     * and use or remove loyalty discounts.
     * <p>Points are retrieved from the client's {@link FidelityCard}.
     * If the client has no fidelity card, the section shows a placeholder message.
     * <p>Method uses {@link #togglePoints()}, {@link #updatePoints()} to control points.
     *
     * @return loyalty section panel
     */
    private JPanel buildLoyaltySection() {
        JPanel panel = section("Loyalty Card");
        if (clientSupplier != null) {
            FidelityCard card = clientSupplier.getFidelityCard();
            if (card == null) {
                panel.add(row("No fidelity card", null));
                return panel;
            }
            panel.add(row("Available points:", String.valueOf(card.getTotalPoint())));

            lblPointsUsed = new JLabel("0");
            JPanel pointsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            pointsRow.setOpaque(false);
            JLabel label = new JLabel("Points used:");
            label.setFont(FONT_BOLD);
            pointsRow.add(label);
            pointsRow.add(lblPointsUsed);
            panel.add(pointsRow);

            JButton btnUsePoints = new JButton("Use points");
            ViewUtils.setCursor(btnUsePoints);
            btnUsePoints.setEnabled(card.getIsValid());
            btnUsePoints.addActionListener(e -> togglePoints());
            panel.add(btnUsePoints);
        } else {
            panel.add(row("No client selected", null));
        }
        return panel;
    }

    /**
     * Builds the receipt display section.
     * <p>The section contains:
     * <ul><li>Purchased products</li>
     *     <li>Quantities and line totals</li>
     *     <li>Applied promotions</li>
     *     <li>Subtotal, VAT, discounts, and final total</li></ul>
     * <p>The ticket is refreshed by {@link #refreshTicket()}
     *
     * @return receipt ticket panel
     */
    private JPanel buildTicketSection() {
        ticketPanel = new JPanel();
        ticketPanel.setLayout(new BoxLayout(ticketPanel, BoxLayout.Y_AXIS));
        ticketPanel.setBackground(Color.WHITE);
        ticketPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(12, 12, 12, 12)
        ));
        ticketPanel.setAlignmentX(LEFT_ALIGNMENT);

        refreshTicket();
        return ticketPanel;
    }

    /**
     * Rebuilds the receipt content dynamically using:
     * <ul><li>{@link #createTicketLine(Product, int)}</li>
     *     <li>{@link #createLine(String, String, Font, Color)}</li></ul>
     * <p>It also recalculates: Subtotal, Discounts, VAT, Final total
     *
     * @see #getSubTotal()
     * @see #getProductDiscount()
     * @see #getPointDiscount()
     * @see #getVATTotal()
     * @see #getTotal()
     */
    private void refreshTicket() {
        ticketPanel.removeAll();

        JLabel title = new JLabel("RECEIPT");
        title.setFont(FONT_TITLE);
        ticketPanel.add(title);

        ticketPanel.add(Box.createVerticalStrut(10));

        for (Map.Entry<Product, Integer> entry : receipt.entrySet()) {
            ticketPanel.add(createTicketLine(entry.getKey(), entry.getValue()));
        }

        ticketPanel.add(Box.createVerticalStrut(10));

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        container.add(createLine("Subtotal:", formatPrice(getSubTotal()), FONT_BOLD, null));
        container.add(createLine("Discount:", "-" + formatPrice(getProductDiscount()), FONT_BOLD, COLOR_PROMO));
        container.add(createLine("Points:", "-" + formatPrice(getPointDiscount()), FONT_BOLD, COLOR_PROMO));
        container.add(createLine("VAT:", formatPrice(getVATTotal()), FONT_BOLD, null));
        container.add(Box.createVerticalStrut(20));
        container.add(createLine("TOTAL:", formatPrice(getTotal()), FONT_TOTAL, null));

        ticketPanel.add(container);
        ticketPanel.revalidate();
        ticketPanel.repaint();
    }

    /**
     * Creates a visual line representing a purchased product in the receipt.
     * <p>The line displays:
     * <ul><li>Product quantity</li>
     *     <li>Product name</li>
     *     <li>Line total price</li>
     *     <li>Promotion discount if applicable</li></ul>
     *
     * @param product  displayed product
     * @param quantity purchased quantity
     * @return configured receipt line {@code JPanel}
     * @see #createLine(String, String, Font, Color)
     */
    private JPanel createTicketLine(Product product, int quantity) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        BigDecimal lineTotal = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(quantity));
        container.add(createLine(quantity + " x " + product.getName(), formatPrice(lineTotal), FONT_REG, null));

        Discount discount = product.getCurrentDiscount();
        if (discount != null && product.getIsDiscounted() && quantity >= discount.getRequiredQuantity()) {
            BigDecimal discountAmount = lineTotal
                    .multiply(discount.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            container.add(createLine("   Discount -" + (int) discount.getDiscountPercentage().doubleValue() + "%", String.format("-%.2f€", discountAmount), FONT_PROMO, COLOR_PROMO));
        }
        return container;
    }

    /**
     * Creates a generic formatted line with left and right aligned labels.
     * <p>This utility method is used throughout the receipt display for totals, discounts, taxes, and product lines.
     *
     * @param leftText  text displayed on the left side. The title
     * @param rightText text displayed on the right side. The value
     * @param font      font applied to labels
     * @param color     optional text color, may be {@code null}
     * @return formatted line {@code JPanel}
     */
    private JPanel createLine(String leftText, String rightText, Font font, Color color) {
        JPanel line = new JPanel(new BorderLayout());
        line.setOpaque(false);

        JLabel left = new JLabel(leftText);
        left.setFont(font);
        if (color != null) left.setForeground(color);

        JLabel right = new JLabel(rightText);
        right.setFont(font);
        if (color != null) right.setForeground(color);

        line.add(left, BorderLayout.WEST);
        line.add(right, BorderLayout.EAST);

        return line;
    }

    /**
     * Builds the payment selection section.
     * <p>The section contains a {@code JComboBox}.
     *
     * @return payment section {@code JPanel}
     */
    private JPanel buildPaymentSection() {
        JPanel section = section("Payment");
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JComboBox<String> paymentMethod = new JComboBox<>(new String[]{"Credit card", "Cash", "Cheque"});
        paymentMethod.setFont(FONT_REG);
        paymentMethod.setMaximumSize(new Dimension(300, 30));
        panel.add(paymentMethod);
        section.add(panel);
        return section;
    }

    /**
     * Builds the bottom action button panel.
     * <p>Contains a back button to return to the previous page and a pay button to finalize the transaction
     *
     * @return button action panel
     * @see #onPayClick()
     * @see MainWindow#goBack()
     */
    private JPanel buildButtons() {
        JButton btnBack = new JButton("Back");
        ViewUtils.setCursor(btnBack);
        btnBack.setFont(FONT_REG);
        btnBack.addActionListener(e -> mainWindow.goBack());

        btnPay = new JButton("Pay");
        ViewUtils.setCursor(btnPay);
        btnPay.setFont(FONT_REG);
        btnPay.addActionListener(e -> onPayClick());
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        p.add(btnBack);
        p.add(btnPay);
        return p;
    }

    /**
     * Handles payment confirmation.
     * <p>When payment succeeds:
     * <ul><li>A success message is displayed</li>
     *     <li>The payment button is disabled</li>
     *     <li>The receipt is cleared {@link ReceiptView#clearAll()}</li>
     *     <li>The application navigates back to the receipt page {@link MainWindow#setPage(String)}</li></ul>
     */
    private void onPayClick() {
        JOptionPane.showMessageDialog(this, "Payment successful", "Success", JOptionPane.INFORMATION_MESSAGE);
        btnPay.setEnabled(false);
        receiptView.clearAll();
        mainWindow.setPage("RECEIPT");
    }

    /**
     * Calculates the subtotal of all products before discounts and VAT.
     *
     * @return subtotal amount
     */
    private BigDecimal getSubTotal() {
        BigDecimal subTotal = BigDecimal.ZERO;

        for (Map.Entry<Product, Integer> entry : receipt.entrySet()) {
            BigDecimal lineTotal = BigDecimal.valueOf(entry.getKey().getPrice())
                    .multiply(BigDecimal.valueOf(entry.getValue()));
            subTotal = subTotal.add(lineTotal);
        }
        return subTotal;
    }

    /**
     * Calculates the total discount amount generated by product promotions.
     * <p>Only active promotions meeting required quantity conditions are included in the calculation.
     *
     * @return total promotional discount amount
     */
    private BigDecimal getProductDiscount() {
        BigDecimal discount = BigDecimal.ZERO;

        for (Map.Entry<Product, Integer> entry : receipt.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            Discount d = product.getCurrentDiscount();

            if (d != null && product.getIsDiscounted() && quantity >= d.getRequiredQuantity()) {
                BigDecimal lineTotal = BigDecimal.valueOf(product.getPrice())
                        .multiply(BigDecimal.valueOf(quantity));

                discount = discount.add(lineTotal.multiply(toRate(d.getDiscountPercentage())));
            }
        }
        return discount;
    }
    /**
     * Calculates the discount amount generated by loyalty points usage.
     * <p>Current conversion: <pre> 1 point = 0.01€ </pre>
     *
     * @return loyalty points discount amount
     */
    private BigDecimal getPointDiscount() {
        return BigDecimal.valueOf(pointsUsed).multiply(BigDecimal.valueOf(0.01));
    }

    /**
     * Calculates the total VAT amount applied to the receipt.
     * <p>VAT is computed after promotional discounts are applied.
     *
     * @return total VAT amount
     */
    private BigDecimal getVATTotal() {
        BigDecimal vat = BigDecimal.ZERO;

        for (Map.Entry<Product, Integer> entry : receipt.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();

            BigDecimal lineTotal = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(quantity));

            Discount d = product.getCurrentDiscount();

            if (d != null && product.getIsDiscounted() && quantity >= d.getRequiredQuantity()) {
                BigDecimal discountAmount = lineTotal
                        .multiply(toRate(d.getDiscountPercentage()))
                        .setScale(2, RoundingMode.HALF_UP);
                lineTotal = lineTotal.subtract(discountAmount);
            }
            BigDecimal vatRate = toRate(product.getVat());
            BigDecimal lineVat = lineTotal
                    .multiply(vatRate)
                    .divide(BigDecimal.ONE.add(vatRate), 2, RoundingMode.HALF_UP);
            vat = vat.add(lineVat);
            System.out.println("VAT de " + product.getName() + " : " + product.getVat());
        }
        return vat.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the final payable amount.
     * <p>Formula: <pre>subtotal - productDiscount - pointDiscount + VAT</pre>
     *
     * @return final receipt total
     * @see #getSubTotal()
     * @see #getProductDiscount()
     * @see #getPointDiscount()
     * @see #getVATTotal()
     */
    private BigDecimal getTotal() {
        return getSubTotal()
                .subtract(getProductDiscount())
                .subtract(getPointDiscount())
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Creates a reusable titled section panel.
     * <p>This helper method standardizes layout and visual appearance for all major UI sections.
     *
     * @param title displayed section title
     * @return configured section {@code JPanel}
     */
    private JPanel section(String title) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 8));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setAlignmentX(LEFT_ALIGNMENT);
        return panel;
    }

    /**
     * Applies or removes loyalty points from the current receipt.
     * <p>If points are already applied, they are reset to zero.
     * Otherwise, the maximum possible discount is calculated and applied according to available points
     * retrieved from the client's {@link FidelityCard}.
     * <p>Points can only be used if the fidelity card exists and is valid.
     *
     * @see #updatePoints()
     * @see #refreshTicket()
     */
    private void togglePoints() {
        if (clientSupplier == null) return;

        FidelityCard card = clientSupplier.getFidelityCard();
        if (card == null || !card.getIsValid()) return;

        int availablePoints = card.getTotalPoint();

        if (pointsUsed > 0) {
            pointsUsed = 0;
        } else {
            BigDecimal maxDiscountEuros = getSubTotal().subtract(getProductDiscount());
            pointsUsed = Math.min(
                    availablePoints,
                    maxDiscountEuros
                            .multiply(BigDecimal.valueOf(100))
                            .intValue()
            );
        }
        updatePoints();
        refreshTicket();
    }

    /**
     * Updates the loyalty points display label.
     * <p>This method refreshes the UI after loyalty point changes.
     *
     * @see #togglePoints()
     */
    private void updatePoints() {
        if (lblPointsUsed != null) {
            lblPointsUsed.setText(String.valueOf(pointsUsed));
        }
        revalidate();
        repaint();
    }

    /**
     * Creates a simple labeled row used in information sections.
     * <p>The left label is displayed in bold, and the right label displays the associated value.
     *
     * @param title label title
     * @param value associated value, may be {@code null}
     * @return formatted row {@code JPanel}
     */
    private JPanel row(String title, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        panel.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setFont(FONT_BOLD);
        panel.add(label);
        if (value != null) panel.add(new JLabel(value));
        return panel;
    }

    private String formatPrice(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP) + "€";
    }

    /**
     * Converts a percentage value to a decimal rate.
     * <p>Values greater than or equal to 1 are treated as percentages and divided by 100.
     * <p>Examples: {@code 6 → 0.06}, {@code 21 → 0.21}, {@code 0.06 → 0.06} (unchanged)
     *
     * @param value the percentage or decimal rate value
     * @return the normalized decimal rate
     */
     private BigDecimal toRate(BigDecimal value) {
        return value.compareTo(BigDecimal.ONE) >= 1 ? value.divide(BigDecimal.valueOf(100)) : value;
    }
}