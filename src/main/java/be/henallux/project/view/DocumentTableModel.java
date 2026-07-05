package be.henallux.project.view;

import be.henallux.project.model.Document;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Table model used to display a list of {@link Document} in a JTable.
 * <p>Provides column definitions and maps document attributes to table cells,
 * including action columns for editing and deleting.
 * <p>The model is read-only and must be refreshed using
 * {@link #setDocuments(ArrayList)} when the data changes.
 * @see DocumentTable
 * @see Document
 */
public class DocumentTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "ID", "Workflow", "Creation date", "Send/Receipt date", "", ""
    };

    public static final int TBL_BTN_DEL = 5;
    public static final int TBL_BTN_UPDATE = 4;

    private ArrayList<Document> documents;

    /**
     * Initializes the table model with an initial list of documents.
     *
     * @param documents the initial list of {@link Document} to display in the table
     */
    public DocumentTableModel(ArrayList<Document> documents) {
        this.documents = documents;
    }

    /**
     * Load the Documents list in the table and allow refresh with new data.
     * <p>This method is used when the underlying document list is modified (filtering, deletion, or reload from controller).
     *
     * @see AbstractTableModel#fireTableDataChanged()
     * @param documents list to display in table
     */
    public void setDocuments(ArrayList<Document> documents) {
        this.documents = documents;
        fireTableDataChanged();
    }

    /** {@inheritDoc} */
    @Override
    public int getRowCount() {
        return documents.size();
    }

    /** {@inheritDoc} */
    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    /** {@inheritDoc} */
    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    /**
     * Returns the value displayed in a specific table cell.
     * <p>The returned value is computed based on the column index and the corresponding
     * {@link Document} located at the given row.
     *
     * <p>Columns include:
     * <ul><li>Document identifier</li>
     *   <li>Workflow label (or "N/A" if not available)</li>
     *   <li>Date of creation</li>
     *   <li>Send date if available, otherwise receipt date</li>
     *   <li>Edit &amp; Delete action label</li></ul>
     *
     * <p>Date values are formatted for display using {@link ViewUtils#formatDate(LocalDate)}.
     * Missing workflow labels are replaced using {@link ViewUtils#safeText(String, String)}.
     *
     * @param rowIndex the row index corresponding to a {@link Document} in the table model
     * @param columnIndex the column index defining which attribute of the document is displayed
     * @return the formatted value to display in the table cell, or {@code null} if the column index is invalid
     * @see Document
     * @see ViewUtils#formatDate(LocalDate)
     * @see ViewUtils#safeText(String, String)
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Document doc = documents.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> doc.getId();
            case 1 -> ViewUtils.safeText(doc.getWorkflow().getLabel(), "N/A");
            case 2 -> ViewUtils.formatDate(doc.getDateOfCreation());
            case 3 -> doc.getActualSendDate() != null ? ViewUtils.formatDate(doc.getActualSendDate()) : ViewUtils.formatDate(doc.getActualDateOfReceipt());
            case 4 -> "Edit";
            case 5 -> "Delete";
            default -> null;
        };
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}