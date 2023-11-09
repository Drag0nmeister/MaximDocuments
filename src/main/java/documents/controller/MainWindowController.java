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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
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
        loadWindow("/view/invoice.fxml", "Счет");
    }

    @FXML
    private void handlePaymentAction(ActionEvent event) {
        loadWindow("/view/payment.fxml", "Платеж");
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
        return "Накладная" + "\n" +
                "ID: " + invoice.getId() + "\n" +
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
        return "Платёжка" + "\n" +
                "ID: " + payment.getId() + "\n" +
                "Номер: " + payment.getNumber() + "\n" +
                "Дата: " + payment.getDate().toString() + "\n" +
                "Пользователь: " + payment.getUser() + "\n" +
                "Сумма: " + payment.getAmount() + "\n" +
                "Сотрудник: " + payment.getEmployee() + "\n";
    }


    private String convertPaymentOrderToString(PaymentOrder paymentOrder) {
        return "Заявка на оплату" + "\n" +
                "ID: " + paymentOrder.getId() + "\n" +
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
        String[] lines = block.split("\n");
        String documentType = lines[0];
        DisplayableDocument document = null;

        if ("Накладная".equals(documentType)) {
            document = parseInvoice(block.substring(documentType.length() + 1));
            if (document != null) {
                invoiceService.saveInvoice((Invoice) document);
            }
        } else if ("Платёжка".equals(documentType)) {
            document = parsePayment(block.substring(documentType.length() + 1));
            if (document != null) {
                paymentService.savePayment((Payment) document);
            }
        } else if ("Заявка на оплату".equals(documentType)) {
            document = parsePaymentOrder(block.substring(documentType.length() + 1));
            if (document != null) {
                paymentOrderService.savePaymentOrder((PaymentOrder) document);
            }
        }
        if (document != null) {
            final DisplayableDocument finalDocument = document;
            Platform.runLater(() -> {
                documentListController.addDocument(finalDocument);
                documentListView.getItems().add(finalDocument);
                documentListView.getSelectionModel().select(finalDocument);
            });
        }
    }

    private Invoice parseInvoice(String block) {
        Map<String, String> dataMap = Arrays.stream(block.split("\n"))
                .map(line -> line.split(": "))
                .collect(Collectors.toMap(parts -> parts[0].trim(), parts -> parts[1].trim()));

        return new Invoice(
                Integer.parseInt(dataMap.get("ID")),
                dataMap.get("Номер"),
                LocalDate.parse(dataMap.get("Дата")),
                dataMap.get("Пользователь"),
                new BigDecimal(dataMap.get("Сумма")),
                dataMap.get("Валюта"),
                new BigDecimal(dataMap.get("Курс валюты")),
                dataMap.get("Товар"),
                new BigDecimal(dataMap.get("Количество"))
        );
    }

    private Payment parsePayment(String block) {
        Map<String, String> dataMap = Arrays.stream(block.split("\n"))
                .map(line -> line.split(": "))
                .collect(Collectors.toMap(parts -> parts[0].trim(), parts -> parts[1].trim()));

        return new Payment(
                Integer.parseInt(dataMap.get("ID")),
                dataMap.get("Номер"),
                LocalDate.parse(dataMap.get("Дата")),
                dataMap.get("Пользователь"),
                new BigDecimal(dataMap.get("Сумма")),
                dataMap.get("Сотрудник")
        );
    }

    private PaymentOrder parsePaymentOrder(String block) {
        Map<String, String> dataMap = Arrays.stream(block.split("\n"))
                .map(line -> line.split(": "))
                .collect(Collectors.toMap(
                        parts -> parts[0].trim(),
                        parts -> parts.length > 1 ? parts[1].trim() : null
                ));

        return new PaymentOrder(
                Integer.parseInt(dataMap.get("ID")),
                dataMap.get("Номер"),
                LocalDate.parse(dataMap.get("Дата")),
                dataMap.get("Пользователь"),
                dataMap.get("Контрагент"),
                new BigDecimal(dataMap.get("Сумма")),
                dataMap.get("Валюта"),
                new BigDecimal(dataMap.get("Курс Валюты")),
                new BigDecimal(dataMap.get("Комиссия"))
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
        List<DisplayableDocument> selectedDocuments = new ArrayList<>(documentListView.getSelectionModel().getSelectedItems());
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
