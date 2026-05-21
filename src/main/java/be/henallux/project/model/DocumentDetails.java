package main.java.be.henallux.project.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DocumentDetails implements Model {
    private Document doc;
    private List<Detail> details;

    public DocumentDetails(Document doc, List<Detail> details) {
        this.doc = doc;

        if (details == null)
            this.details = new ArrayList<>();
        else
            this.details = details;
    }

    public DocumentDetails(Document doc) {
        this(doc, null);
    }

    public Document getDocument() { return doc; }

    public List<Detail> getDetails() { return Collections.unmodifiableList(details); }

    public void addDetail(Detail detail) {
        if (!details.contains(detail))
            details.add(detail);
    }

    @Override
    public String getLabel() {
        StringBuilder out = new StringBuilder(doc.getLabel());
        for (Detail detail : details){
            out.append(" - ").append(detail.getLabel());
        }
        return out.toString();
    }

    @Override
    public String toString() {
        return String.format("DocumentDetails{document=%s, details=%s", doc.toString(), details.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        DocumentDetails other = (DocumentDetails) obj;
        return  doc.equals(other.getDocument()) && 
                details.stream().allMatch(e -> other.getDetails().contains(e));
    }

    @Override
    public int hashCode() {
        int result = doc.hashCode();
        result = 31 * result + details.hashCode();
        return result;
    }
}
