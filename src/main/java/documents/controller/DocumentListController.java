package documents.controller;

import documents.listener.DocumentCreationListener;
import documents.model.DisplayableDocument;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
public class DocumentListController implements DocumentCreationListener {

    @FXML
    public ListView<DisplayableDocument> documentListView;
    private final ObservableList<DisplayableDocument> documentList = FXCollections.observableArrayList();

    public void initialize() {
        documentListView.setItems(documentList);
        documentListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        });
    }

    @Override
    public void onDocumentCreated(DisplayableDocument document) {
        addDocument(document);
    }

    public void updateDocumentListView(List<DisplayableDocument> documents) {
        Platform.runLater(() -> documentList.setAll(documents));
    }

    public void addDocument(DisplayableDocument document) {
        Platform.runLater(() -> documentList.add(document));
    }

    public void removeDocument(DisplayableDocument document) {
        Platform.runLater(() -> documentList.remove(document));
    }

    public ListView<DisplayableDocument> getDocumentListView() {
        return documentListView;
    }

    public void removeDocuments(List<DisplayableDocument> documents) {
        Platform.runLater(() -> documentList.removeAll(documents));
    }
}
