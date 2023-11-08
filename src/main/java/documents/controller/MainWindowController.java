package documents.controller;

import documents.listener.DocumentCreationListener;
import documents.listener.DocumentCreationListenerAware;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import documents.model.DisplayableDocument;
import documents.model.Invoice;
import documents.model.Payment;
import documents.model.PaymentOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class MainWindowController implements DocumentCreationListener {

    @Autowired
    private final ConfigurableApplicationContext context;
    private DisplayableDocument currentDocument;

    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private PaymentOrderService paymentOrderService;
    @Autowired
    private PaymentService paymentService;
    private DocumentDetailsController documentDetailsController;

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
        loadWindow("/invoice.fxml", "Счет");
    }

    @FXML
    private void handlePaymentAction(ActionEvent event) {
        loadWindow("/payment.fxml", "Платеж");
    }

    @FXML
    public void initialize() {
        setupDocumentListView();
    }

    @Override
    public void onDocumentCreated(DisplayableDocument document) {
        Platform.runLater(() -> {
            documentListView.getItems().add(document);
            documentListView.getSelectionModel().select(document);
        });
    }

    private void loadDocuments() {
        List<DisplayableDocument> invoices = new ArrayList<>(invoiceService.getAllInvoices());
        List<DisplayableDocument> paymentOrders = new ArrayList<>(paymentOrderService.getAllPaymentOrders());
        List<DisplayableDocument> payments = new ArrayList<>(paymentService.getAllPayments());
        List<DisplayableDocument> allDocuments = new ArrayList<>();
        allDocuments.addAll(invoices);
        allDocuments.addAll(paymentOrders);
        allDocuments.addAll(payments);

        documentListView.getItems().setAll(allDocuments);
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
                } else {
                    setText(document.getDisplayText());
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
        loadWindow("/paymentOrder.fxml", "Платежное поручение");
    }

    @FXML
    private void handleSaveAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить документ");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            saveDocumentToFile(selectedFile);
        }
    }

    private void saveDocumentToFile(File file) {
        if (currentDocument == null) {
            showAlert("Сохранение документа", "Не выбран документ для сохранения", Alert.AlertType.WARNING);
            return;
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            if (currentDocument instanceof Invoice invoice) {
                fileWriter.write(convertInvoiceToString(invoice));
            } else if (currentDocument instanceof Payment payment) {
                fileWriter.write(convertPaymentToString(payment));
            } else if (currentDocument instanceof PaymentOrder paymentOrder) {
                fileWriter.write(convertPaymentOrderToString(paymentOrder));
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


    private String convertInvoiceToString(Invoice invoice) {
        return "ID: " + invoice.getId() + "\n" +
                "Номер: " + invoice.getNumber() + "\n" +
                "Дата: " + invoice.getDate().toString() + "\n" +
                "Пользователь: " + invoice.getUser() + "\n" +
                "Сумма: " + invoice.getAmount() + "\n" +
                "Валюта: " + invoice.getCurrency() + "\n" +
                "Курс валюты: " + invoice.getCurrencyRate() + "\n" +
                "Товар: " + invoice.getProduct() + "\n" +
                "Количество: " + invoice.getQuantity() + "\n";
    }

    private String convertPaymentToString(Payment payment) {
        return "ID: " + payment.getId() + "\n" +
                "Номер: " + payment.getNumber() + "\n" +
                "Дата: " + payment.getDate().toString() + "\n" +
                "Пользователь: " + payment.getUser() + "\n" +
                "Сумма: " + payment.getAmount() + "\n" +
                "Сотрудник: " + payment.getEmployee() + "\n";
    }


    private String convertPaymentOrderToString(PaymentOrder paymentOrder) {
        return "ID: " + paymentOrder.getId() + "\n" +
                "Номер: " + paymentOrder.getNumber() + "\n" +
                "Дата: " + paymentOrder.getDate().toString() + "\n" +
                "Пользователь: " + paymentOrder.getUser() + "\n" +
                "Контрагент: " + paymentOrder.getContractor() + "\n" +
                "Сумма: " + paymentOrder.getAmount() + "\n" +
                "Валюта: " + paymentOrder.getCurrency() + "\n" +
                "Курс Валюты: " + paymentOrder.getCurrencyRate() + "\n" +
                "Комиссия: " + paymentOrder.getCommission() + "\n";
    }

    @FXML
    private void handleLoadAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить документ");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            loadDocumentFromFile(selectedFile);
        }
    }

    private void loadDocumentFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                DisplayableDocument document = null;
                if (line.startsWith("Invoice,")) {
                    document = parseInvoice(line);
                } else if (line.startsWith("Payment,")) {
                    document = parsePayment(line);
                } else if (line.startsWith("PaymentOrder,")) {
                    document = parsePaymentOrder(line);
                }
                if (document != null) {
                    final DisplayableDocument finalDocument = document;
                    Platform.runLater(() -> {
                        documentListController.addDocument(finalDocument);
                        documentListView.getSelectionModel().select(finalDocument);
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Invoice parseInvoice(String line) {
        String[] data = line.split(",");
        if (!"Invoice".equals(data[0])) return null;
        return new Invoice(
                Integer.parseInt(data[1]),
                data[2],
                LocalDate.parse(data[3]),
                data[4],
                new BigDecimal(data[5]),
                data[6],
                new BigDecimal(data[7]),
                data[8],
                new BigDecimal(data[9])
        );
    }

    private Payment parsePayment(String line) {
        String[] data = line.split(",");
        if (!"Payment".equals(data[0])) return null;
        return new Payment(
                Integer.parseInt(data[1]),
                data[2],
                LocalDate.parse(data[3]),
                data[4],
                new BigDecimal(data[5]),
                data[6]
        );
    }

    private PaymentOrder parsePaymentOrder(String line) {
        String[] data = line.split(",");
        if (!"PaymentOrder".equals(data[0])) return null;
        return new PaymentOrder(
                Integer.parseInt(data[1]),
                data[2],
                LocalDate.parse(data[3]),
                data[4],
                data[5],
                new BigDecimal(data[6]),
                data[7],
                new BigDecimal(data[8]),
                new BigDecimal(data[9])
        );
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/documentDetails.fxml"));
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
