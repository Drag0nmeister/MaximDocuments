package documents.controller;

import documents.listener.DocumentCreationListener;
import documents.listener.DocumentCreationListenerAware;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import documents.model.Payment;
import documents.service.PaymentService;
import documents.service.PaymentProcessingService;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Scanner;


@Component
@Scope("prototype")
@FxmlView("view/payment.fxml")
public class PaymentController implements DocumentCreationListenerAware {

    @FXML
    private TextField numberField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField userField;
    @FXML
    private TextField amountField;
    @FXML
    private TextField employeeField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button okButton;

    private DocumentCreationListener creationListener;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentProcessingService paymentProcessingService;

    private final FileChooser fileChooser = new FileChooser();

    @Override
    public void setCreationListener(DocumentCreationListener listener) {
        this.creationListener = listener;
    }

    @FXML
    private void createPayment(ActionEvent event) {
        if (!isValidInput()) {
            showAlertWithError("Ошибка ввода", "Пожалуйста, проверьте введённые значения.");
            return;
        }

        try {
            Payment payment = Payment.builder()
                    .number(numberField.getText())
                    .date(datePicker.getValue())
                    .user(userField.getText())
                    .amount(new BigDecimal(amountField.getText()))
                    .employee(employeeField.getText())
                    .build();

            payment = paymentService.createPayment(payment);
            notifyDocumentCreation(payment);
            closeWindow();
        } catch (NumberFormatException e) {
            showAlertWithError("Ошибка формата числа", "Пожалуйста, проверьте введённые числовые значения.");
        }
    }

    private boolean isValidInput() {
        String namePattern = "[a-zA-Zа-яА-Я ]*";
        return numberField.getText().matches("\\d+") &&
                datePicker.getValue() != null &&
                userField.getText().matches(namePattern) &&
                employeeField.getText().matches(namePattern) &&
                !amountField.getText().isEmpty() &&
                amountField.getText().matches("\\d+(\\.\\d+)?");
    }

    private void notifyDocumentCreation(Payment payment) {
        if (this.creationListener != null) {
            this.creationListener.onDocumentCreated(payment);
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
    private void savePaymentsToFile() {
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                for (Payment payment : paymentService.getAllPayments()) {
                    writer.println(paymentProcessingService.formatPaymentForFile(payment));
                }
            } catch (Exception e) {
                showAlertWithError("Ошибка сохранения", "Не удалось сохранить файл: " + e.getMessage());
            }
        }
    }

    @FXML
    private void loadPaymentsFromFile() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    try {
                        Payment payment = paymentProcessingService.parsePaymentFromLine(line);
                        paymentService.createPayment(payment);
                        notifyDocumentCreation(payment);
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
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}
