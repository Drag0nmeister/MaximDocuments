package documents.listener;

import documents.model.DisplayableDocument;

public interface DocumentCreationListener {
    void onDocumentCreated(DisplayableDocument document);
}
