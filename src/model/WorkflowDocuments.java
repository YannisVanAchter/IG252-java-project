package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkflowDocuments {
    private WorkFlow workflow;
    private List<Document> documents;

    public WorkflowDocuments(WorkFlow workflow, List<Document> documents) {
        this.workflow = workflow;

        if (documents == null)
            this.documents = new ArrayList<>();
        else 
            this.documents = documents;
    }

    public WorkflowDocuments(WorkFlow workflow) {
        this(workflow, null);
    }

    public WorkFlow getWorkFlow() { return workflow; }

    public List<Document> getDocuments() { return Collections.unmodifiableList(documents); }

    public void addDocument(Document doc) {
        if ( !documents.contains(doc) ) 
            documents.add(doc);
    }

    @Override
    public String toString() {
        return String.format("WorkflowDocuments{workflow=%s, documents=%s", workflow.toString(), documents.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkflowDocuments other = (WorkflowDocuments) o;
        return  workflow == other.getWorkFlow() && 
                documents.stream().allMatch(e -> other.getDocuments().contains(e));
    }

    @Override
    public int hashCode() {
        int result = workflow.hashCode();
        result = 31 * result + documents.hashCode();
        return result;
    }
}
