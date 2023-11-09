package documents.controller;

import documents.listener.DocumentCreationListener;
import documents.listener.DocumentCreationListenerAware;
import jakarta.annotation.PostConstruct;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import documents.model.DisplayableDocument;
import documents.model.PaymentOrder;
import documents.service.PaymentOrderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

@Component
@Scope("prototype")
@FxmlView("view/paymentOrder.fxml")
public class PaymentOrderController implements DocumentCreationListenerAware {


    @FXML
    private TextField numberField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField userField;
    @FXML
    private TextField contractorField;
    @FXML
    private TextField amountField;
    @FXML
    private TextField currencyField;
    @FXML
    private TextField currencyRateField;
    @FXML
    private TextField commissionField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button okButton;

    private DocumentCreationListener creationListener;

    @Autowired
    private DocumentListController documentListController;

    @Autowired
    private PaymentOrderService paymentOrderService;
    private final FileChooser fileChooser = new FileChooser();

    @PostConstruct
    private void initialize() {
        loadAllPaymentOrders();
    }

    public void setCreationListener(DocumentCreationListener listener) {
        this.creationListener = listener;
    }

    private void loadAllPaymentOrders() {
        List<PaymentOrder> paymentOrders = paymentOrderService.getAllPaymentOrders();
        paymentOrders.forEach(paymentOrder ->
                documentListController.addDocument(paymentOrder));
    }

    @FXML
    private void createPaymentOrder(ActionEvent event) {
        try {
            PaymentOrder paymentOrder = PaymentOrder.builder()
                    .number(numberField.getText())
                    .date(datePicker.getValue())
                    .user(userField.getText())
                    .contractor(contractorField.getText())
                    .amount(new BigDecimal(amountField.getText()))
                    .currency(currencyField.getText())
                    .currencyRate(new BigDecimal(currencyRateField.getText()))
                    .commission(new BigDecimal(commissionField.getText()))
                    .build();

            paymentOrder = paymentOrderService.createPaymentOrder(paymentOrder);
            if (this.creationListener != null) {
                this.creationListener.onDocumentCreated(paymentOrder);
            }
            documentListController.addDocument(paymentOrder);
            Stage stage = (Stage) okButton.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            showAlertWithError("Ошибка ввода", "Пожалуйста, проверьте введённые  значения.");
        }
    }

    private void showAlertWithError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void deleteSelectedPaymentOrder() {
        DisplayableDocument selected = documentListController.getDocumentListView().getSelectionModel().getSelectedItem();
        if (selected instanceof PaymentOrder selectedPaymentOrder) {
            paymentOrderService.deletePaymentOrder(selectedPaymentOrder.getId());
            documentListController.removeDocument(selectedPaymentOrder);
        }
    }

    @FXML
    private void savePaymentOrdersToFile() {
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            List<PaymentOrder> paymentOrders = paymentOrderService.getAllPaymentOrders();
            paymentOrders.forEach(paymentOrder -> {
                try {
                    paymentOrderService.savePaymentOrderToFile(paymentOrder, file.getAbsolutePath());
                } catch (Exception e) {
                    showAlertWithError("Ошибка сохранения", "Не удалось сохранить файл: " + e.getMessage());
                }
            });
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void loadPaymentOrdersFromFile() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    PaymentOrder paymentOrder = paymentOrderService.loadPaymentOrderFromFile(line);
                    if (paymentOrder != null) {
                        paymentOrderService.createPaymentOrder(paymentOrder);
                        documentListController.addDocument(paymentOrder);
                    }
                }
            } catch (Exception e) {
                showAlertWithError("Ошибка загрузки", "Не удалось загрузить файл: " + e.getMessage());
            }
        }
    }

    private String formatPaymentOrderForFile(PaymentOrder paymentOrder) {
        return String.join(",",
                paymentOrder.getNumber(),
                paymentOrder.getDate().toString(),
                paymentOrder.getUser(),
                paymentOrder.getAmount().toString(),
                paymentOrder.getCurrency(),
                paymentOrder.getCurrencyRate().toString(),
                paymentOrder.getContractor(),
                paymentOrder.getCommission().toString()
        );
    }

    private PaymentOrder parsePaymentOrderFromLine(String line) {
        try {
            String[] parts = line.split(",");
            return PaymentOrder.builder()
                    .number(parts[0])
                    .date(LocalDate.parse(parts[1]))
                    .user(parts[2])
                    .amount(new BigDecimal(parts[3]))
                    .currency(parts[4])
                    .currencyRate(new BigDecimal(parts[5]))
                    .contractor(parts[6])
                    .commission(new BigDecimal(parts[7]))
                    .build();
        } catch (Exception e) {
            showAlertWithError("Ошибка парсинга", "Не удалось прочитать платежное поручение из строки: " + line);
            return null;
        }
    }
}
