package documents.controller;

import documents.listener.DocumentCreationListener;
import documents.listener.DocumentCreationListenerAware;
import jakarta.annotation.PostConstruct;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import documents.model.DisplayableDocument;
import documents.model.Payment;
import documents.service.PaymentService;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    private DocumentListController documentListController;
    @Autowired
    private PaymentService paymentService;

    private final FileChooser fileChooser = new FileChooser();

    @PostConstruct
    private void initialize() {
        loadAllPayments();
    }

    public void setCreationListener(DocumentCreationListener listener) {
        this.creationListener = listener;
    }

    private void loadAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        payments.forEach(payment ->
                documentListController.addDocument(payment));

    }

    @FXML
    private void createPayment(ActionEvent event) {
        try {
            Payment payment = Payment.builder()
                    .number(numberField.getText())
                    .date(datePicker.getValue())
                    .user(userField.getText())
                    .amount(new BigDecimal(amountField.getText()))
                    .employee(employeeField.getText())
                    .build();

            payment = paymentService.createPayment(payment);

            if (this.creationListener != null) {
                this.creationListener.onDocumentCreated(payment);
            }
            documentListController.addDocument(payment);

            Stage stage = (Stage) okButton.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlertWithError("Ошибка ввода", "Пожалуйста, проверьте введённые значения.");
        }
    }


    private void showAlertWithError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String formatPaymentDisplayName(Payment payment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return String.format("Платежка от %s номер %s", payment.getDate().format(formatter), payment.getNumber());
    }

    @FXML
    private void deleteSelectedPayment() {
        DisplayableDocument selected = documentListController.getDocumentListView().getSelectionModel().getSelectedItem();
        if (selected instanceof Payment selectedPayment) {
            paymentService.deletePayment(selectedPayment.getId());
            documentListController.removeDocument(selectedPayment);
        }
    }

    @FXML
    private void savePaymentsToFile() {
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                for (Payment payment : paymentService.getAllPayments()) {
                    writer.println(formatPaymentForFile(payment));
                }
            } catch (Exception e) {
                showAlertWithError("Ошибка сохранения", "Не удалось сохранить файл: " + e.getMessage());
            }
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void loadPaymentsFromFile() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    Payment payment = paymentService.loadPaymentFromFile(line);
                    if (payment != null) {
                        paymentService.createPayment(payment);
                        documentListController.addDocument(payment);
                    }
                }
            } catch (Exception e) {
                showAlertWithError("Ошибка загрузки", "Не удалось загрузить файл: " + e.getMessage());
            }
        }
    }

    private String formatPaymentForFile(Payment payment) {
        return String.join(",",
                payment.getNumber(),
                payment.getDate().toString(),
                payment.getUser(),
                payment.getAmount().toString(),
                payment.getEmployee());
    }

    private Payment parsePaymentFromLine(String line) {
        try {
            String[] parts = line.split(",");
            return Payment.builder()
                    .number(parts[0])
                    .date(LocalDate.parse(parts[1]))
                    .user(parts[2])
                    .amount(new BigDecimal(parts[3]))
                    .employee(parts[4])
                    .build();
        } catch (Exception e) {
            showAlertWithError("Ошибка парсинга", "Не удалось прочитать платеж из строки: " + line);
            return null;
        }
    }
}
