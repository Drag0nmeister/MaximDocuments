package documents.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Scope("prototype")
public class MainWindowController {

    @Autowired
    private final ConfigurableApplicationContext context;
    private DisplayableDocument currentDocument;

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
        return String.format(
                "Invoice,%s,%s,%s,%s,%s,%s,%s,%s%n",
                invoice.getId(),
                invoice.getNumber(),
                invoice.getDate(),
                invoice.getUser(),
                invoice.getAmount(),
                invoice.getCurrency(),
                invoice.getCurrencyRate(),
                invoice.getProduct(),
                invoice.getQuantity()
        );
    }

    private String convertPaymentToString(Payment payment) {
        return String.format(
                "Payment,%s,%s,%s,%s,%s,%s%n",
                payment.getId(),
                payment.getNumber(),
                payment.getDate(),
                payment.getUser(),
                payment.getAmount(),
                payment.getEmployee()
        );
    }

    private String convertPaymentOrderToString(PaymentOrder paymentOrder) {
        return String.format(
                "PaymentOrder,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                paymentOrder.getId(),
                paymentOrder.getNumber(),
                paymentOrder.getDate(),
                paymentOrder.getUser(),
                paymentOrder.getContractor(),
                paymentOrder.getAmount(),
                paymentOrder.getCurrency(),
                paymentOrder.getCurrencyRate(),
                paymentOrder.getCommission()
        );
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
        loadWindow("/documentDetails.fxml", "Детали документа");
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
