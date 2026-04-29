package model;

import view.Document;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class DocumentTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "", "ID", "Type", "Creation date", "Edit", "Delete"
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
            case 0 -> "";
            case 1 -> doc.getId();
            case 2 -> doc.getType();
            case 3 -> doc.getCreationDate();
            case 4 -> "✏";
            case 5 -> "\uD83D\uDDD1";
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}