package view;

import model.Document;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class DocumentTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "ID", "Workflow", "Creation date", "Send/Receipt date", "Edit", "Delete"
    };

    private ArrayList<Document> documents;

    public DocumentTableModel(ArrayList<Document> documents) {
        this.documents = documents;
    }

    public void setDocuments(ArrayList<Document> documents) {
        this.documents = documents;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return documents.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Document doc = documents.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> doc.getId();
            case 1 -> doc.getDocumentType().getName();
            case 2 -> doc.getDateOfCreation();
            case 3 -> doc.getActualSendDate() != null ? doc.getActualSendDate() : doc.getActualDateOfReceipt();
            case 4 -> "Edit";
            case 5 -> "Delete";
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}