package documents.controller;

import documents.listener.DocumentCreationListener;
import documents.listener.DocumentCreationListenerAware;
import documents.service.PaymentOrderProcessingService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import documents.model.PaymentOrder;
import documents.service.PaymentOrderService;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
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
    private PaymentOrderService paymentOrderService;
    private final FileChooser fileChooser = new FileChooser();

    @Autowired
    private PaymentOrderProcessingService paymentOrderProcessingService;

    @Override
    public void setCreationListener(DocumentCreationListener listener) {
        this.creationListener = listener;
    }

    @FXML
    private void createPaymentOrder(ActionEvent event) {
        if (!isValidInput()) {
            showAlertWithError("Ошибка ввода", "Пожалуйста, проверьте введённые значения.");
            return;
        }

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
            notifyDocumentCreation(paymentOrder);
            closeWindow();
        } catch (NumberFormatException e) {
            showAlertWithError("Ошибка формата числа", "Пожалуйста, проверьте введённые числовые значения.");
        }
    }

    private boolean isValidInput() {
        String namePattern = "[a-zA-Zа-яА-Я ]*";
        String numberPattern = "\\d+(\\.\\d+)?";
        return numberField.getText().matches("\\d+") &&
                datePicker.getValue() != null &&
                userField.getText().matches(namePattern) &&
                contractorField.getText().matches(namePattern) &&
                !amountField.getText().isEmpty() &&
                amountField.getText().matches(numberPattern) &&
                currencyField.getText().matches(namePattern) &&
                currencyRateField.getText().matches(numberPattern) &&
                commissionField.getText().matches(numberPattern);
    }

    private void notifyDocumentCreation(PaymentOrder paymentOrder) {
        if (this.creationListener != null) {
            this.creationListener.onDocumentCreated(paymentOrder);
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
    private void savePaymentOrdersToFile() {
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                for (PaymentOrder paymentOrder : paymentOrderService.getAllPaymentOrders()) {
                    writer.println(paymentOrderProcessingService.formatPaymentOrderForFile(paymentOrder));
                }
            } catch (Exception e) {
                showAlertWithError("Ошибка сохранения", "Не удалось сохранить файл: " + e.getMessage());
            }
        }
    }

    @FXML
    private void loadPaymentOrdersFromFile() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    try {
                        PaymentOrder paymentOrder = paymentOrderProcessingService.parsePaymentOrderFromLine(line);
                        paymentOrderService.createPaymentOrder(paymentOrder);
                        notifyDocumentCreation(paymentOrder);
                    } catch (IllegalArgumentException e) {
                        showAlertWithError("Ошибка парсинга", e.getMessage());
                    }
                }
            } catch (Exception e) {
                showAlertWithError("Ошибка загрузки", "Не удалось загрузить файл: " + e.getMessage());
            }
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
