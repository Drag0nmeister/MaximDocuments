package documents.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import documents.model.DisplayableDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
public class DocumentListController {

    @Autowired
    private DocumentDetailsController documentDetailsController;
    @FXML
    private ListView<DisplayableDocument> documentListView;
    private ObservableList<DisplayableDocument> documentList = FXCollections.observableArrayList();

    public void initialize() {
        documentListView.setItems(documentList);
        documentListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                documentDetailsController.setCurrentDocument(newValue);
            }
        });
    }

    public void updateDocumentListView(List<DisplayableDocument> documents) {
        Platform.runLater(() -> {
            documentList.setAll(documents);
        });
    }

    public void addDocument(DisplayableDocument document) {
        Platform.runLater(() -> {
            documentList.add(document);
        });
    }

    public void removeDocument(DisplayableDocument document) {
        Platform.runLater(() -> {
            documentList.remove(document);
        });
    }

    public ListView<DisplayableDocument> getDocumentListView() {
        return documentListView;
    }
}
