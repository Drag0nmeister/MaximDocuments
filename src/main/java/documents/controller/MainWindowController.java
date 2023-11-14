package documents.controller;

import documents.listener.DocumentCreationListener;
import documents.listener.DocumentCreationListenerAware;
import documents.service.DocumentParserService;
import documents.service.DocumentProcessingService;
import documents.service.InvoiceService;
import documents.service.PaymentOrderService;
import documents.service.PaymentService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import documents.model.DisplayableDocument;
import documents.model.Invoice;
import documents.model.Payment;
import documents.model.PaymentOrder;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Component
@Scope("prototype")
@FxmlView("view/mainWindow.fxml")
public class MainWindowController implements DocumentCreationListener {


    @Autowired
    private ConfigurableApplicationContext context;
    private DisplayableDocument currentDocument;

    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private PaymentOrderService paymentOrderService;
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private DocumentProcessingService documentProcessingService;
    private DocumentDetailsController documentDetailsController;
    @Autowired
    private DocumentParserService documentParserService;

    @Autowired
    private DocumentListController documentListController;

    @FXML
    private ListView<DisplayableDocument> documentListView;

    @Autowired
    public MainWindowController(ConfigurableApplicationContext context) {
        this.context = context;
    }

    @FXML
    private void handleInvoiceAction(ActionEvent event) {
        loadWindow("/view/invoice.fxml", "Счет");
    }

    @FXML
    private void handlePaymentAction(ActionEvent event) {
        loadWindow("/view/payment.fxml", "Платеж");
    }

    @FXML
    public void initialize() {
        setupDocumentListView();
        loadDocuments();
    }

    @Override
    public void onDocumentCreated(DisplayableDocument document) {
        Platform.runLater(() -> {
            documentListView.getItems().add(document);
            documentListView.getSelectionModel().select(document);
        });
    }

    private void loadDocuments() {
        loadInvoices();
        loadPaymentOrders();
        loadPayments();
    }

    private void loadInvoices() {
        List<DisplayableDocument> invoices = new LinkedList<>(invoiceService.getAllInvoices());
        documentListView.getItems().addAll(invoices);
    }

    private void loadPaymentOrders() {
        List<DisplayableDocument> paymentOrders = new LinkedList<>(paymentOrderService.getAllPaymentOrders());
        documentListView.getItems().addAll(paymentOrders);
    }

    private void loadPayments() {
        List<DisplayableDocument> payments = new LinkedList<>(paymentService.getAllPayments());
        documentListView.getItems().addAll(payments);
    }

    private void displayDocumentDetails(DisplayableDocument document) {
        if (documentDetailsController != null) {
            documentDetailsController.setCurrentDocument(document);
        }
    }

    private void setupDocumentListView() {
        documentListView.setCellFactory(param -> new ListCell<DisplayableDocument>() {
            @Override
            protected void updateItem(DisplayableDocument document, boolean empty) {
                super.updateItem(document, empty);
                if (empty || document == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox container = new HBox(10);
                    CheckBox checkBox = new CheckBox();
                    Label label = new Label(document.getDisplayText());
                    container.getChildren().addAll(checkBox, label);
                    setGraphic(container);
                }
            }
        });

        documentListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        currentDocument = newValue;
                        displayDocumentDetails(newValue);
                    }
                });

        loadDocuments();
    }

    @FXML
    private void handlePaymentOrderAction(ActionEvent event) {
        loadWindow("/view/paymentOrder.fxml", "Платежное поручение");
    }

    private void saveDocumentToFile(File file) {
        if (currentDocument == null) {
            showAlert("Сохранение документа", "Не выбран документ для сохранения", Alert.AlertType.WARNING);
            return;
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            if (currentDocument instanceof Invoice invoice) {
                fileWriter.write(documentProcessingService.convertInvoiceToString(invoice));
            } else if (currentDocument instanceof Payment payment) {
                fileWriter.write(documentProcessingService.convertPaymentToString(payment));
            } else if (currentDocument instanceof PaymentOrder paymentOrder) {
                fileWriter.write(documentProcessingService.convertPaymentOrderToString(paymentOrder));
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка сохранения", "Не удалось сохранить документ: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleSaveAction(ActionEvent event) {
        File selectedFile = chooseFileForSave();
        if (selectedFile != null) {
            saveDocumentToFile(selectedFile);
        }
    }

    private File chooseFileForSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить документ");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        return fileChooser.showSaveDialog(null);
    }

    @FXML
    private void handleLoadAction(ActionEvent event) {
        File selectedFile = chooseFileForLoad();
        if (selectedFile != null) {
            loadDocumentFromFile(selectedFile);
        }
    }

    private File chooseFileForLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить документ");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        return fileChooser.showOpenDialog(null);
    }

    private void loadDocumentFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder blockBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                blockBuilder.append(line).append("\n");
            }
            String block = blockBuilder.toString();
            processDocumentBlock(block);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processDocumentBlock(String block) {
        String documentType = extractDocumentType(block);
        DisplayableDocument document = parseDocumentByType(block, documentType);
        if (document != null) {
            saveAndDisplayDocument(document);
        }
    }

    private String extractDocumentType(String block) {
        String[] lines = block.split("\n");
        return (lines.length > 0) ? lines[0] : "";
    }

    private DisplayableDocument parseDocumentByType(String block, String documentType) {
        String dataBlock = block.substring(documentType.length()).trim();
        return switch (documentType) {
            case "Накладная" -> documentParserService.parseInvoice(dataBlock);
            case "Платёжка" -> documentParserService.parsePayment(dataBlock);
            case "Заявка на оплату" -> documentParserService.parsePaymentOrder(dataBlock);
            default -> null;
        };
    }

    private void saveAndDisplayDocument(DisplayableDocument document) {
        saveDocument(document);
        Platform.runLater(() -> {
            documentListController.addDocument(document);
            documentListView.getItems().add(document);
            documentListView.getSelectionModel().select(document);
        });
    }

    private void saveDocument(DisplayableDocument document) {
        if (document instanceof Invoice) {
            invoiceService.saveInvoice((Invoice) document);
        } else if (document instanceof Payment) {
            paymentService.savePayment((Payment) document);
        } else if (document instanceof PaymentOrder) {
            paymentOrderService.savePaymentOrder((PaymentOrder) document);
        }
    }

    @FXML
    private void handleViewAction(ActionEvent event) {
        DisplayableDocument selectedDocument = documentListView.getSelectionModel().getSelectedItem();
        if (selectedDocument != null) {
            showDocumentDetails(selectedDocument);
        } else {
            showAlert("Просмотр деталей", "Документ не выбран", Alert.AlertType.WARNING);
        }
    }

    private void showDocumentDetails(DisplayableDocument document) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/documentDetails.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            DocumentDetailsController controller = loader.getController();
            controller.setCurrentDocument(document);

            Stage detailsStage = new Stage();
            detailsStage.setTitle(document.getDisplayText());
            detailsStage.setScene(new Scene(root));
            detailsStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить окно деталей документа", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleExitAction(ActionEvent event) {
        cancel(event);
    }

    @FXML
    private void handleDeleteAction(ActionEvent event) {
        List<DisplayableDocument> selectedDocuments = new LinkedList<>(documentListView.getSelectionModel().getSelectedItems());
        documentListController.removeDocuments(selectedDocuments);
    }

    private void loadWindow(String path, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof DocumentCreationListenerAware) {
                ((DocumentCreationListenerAware) controller).setCreationListener(this);
            }
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
