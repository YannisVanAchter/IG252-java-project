package main.java.be.henallux.project.view;

import main.java.be.henallux.project.model.Document;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Table model used to display a list of {@link Document} in a JTable.
 * Provides column definitions and maps document attributes to table cells,
 * including action columns for editing and deleting.
 * The model is read-only and must be refreshed using
 * {@link #setDocuments(ArrayList)} when the data changes.
 */
public class DocumentTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "ID", "Workflow", "Creation date", "Send/Receipt date", "", ""
    };

    public static final int TBL_BTN_DEL = 5;
    public static final int TBL_BTN_UPDATE = 4;

    private ArrayList<Document> documents;

    public DocumentTableModel(ArrayList<Document> documents) {
        this.documents = documents;
    }

    /**
     * Load the Documents list in the table and allow refresh with new data.
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

    /** {@inheritDoc} */
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