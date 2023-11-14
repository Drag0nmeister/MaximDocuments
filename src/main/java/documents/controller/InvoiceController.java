package documents.controller;

import documents.listener.DocumentCreationListener;
import documents.listener.DocumentCreationListenerAware;
import documents.service.InvoiceProcessingService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import documents.model.Invoice;
import documents.service.InvoiceService;
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
@FxmlView("view/invoice.fxml")
public class InvoiceController implements DocumentCreationListenerAware {

    @FXML
    private TextField numberField, userField, amountField, currencyField, currencyRateField, productField, quantityField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button cancelButton, okButton;

    private DocumentCreationListener creationListener;

    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private InvoiceProcessingService invoiceProcessingService;

    private final FileChooser fileChooser = new FileChooser();

    @Override
    public void setCreationListener(DocumentCreationListener listener) {
        this.creationListener = listener;
    }

    @FXML
    private void createInvoice(ActionEvent event) {
        if (!isValidInput()) {
            showAlertWithError("Ошибка ввода", "Пожалуйста, проверьте введённые значения.");
            return;
        }

        try {
            Invoice invoice = Invoice.builder()
                    .number(numberField.getText())
                    .date(datePicker.getValue())
                    .user(userField.getText())
                    .amount(new BigDecimal(amountField.getText()))
                    .currency(currencyField.getText())
                    .currencyRate(new BigDecimal(currencyRateField.getText()))
                    .product(productField.getText())
                    .quantity(new BigDecimal(quantityField.getText()))
                    .build();

            invoice = invoiceService.createOrUpdateInvoice(invoice);
            notifyDocumentCreation(invoice);
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
                !amountField.getText().isEmpty() &&
                amountField.getText().matches(numberPattern) &&
                currencyField.getText().matches("[a-zA-Zа-яА-Я]*") &&
                currencyRateField.getText().matches(numberPattern) &&
                productField.getText().matches(namePattern) &&
                quantityField.getText().matches(numberPattern);
    }

    private void notifyDocumentCreation(Invoice invoice) {
        if (this.creationListener != null) {
            this.creationListener.onDocumentCreated(invoice);
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
    private void saveInvoicesToFile() {
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                for (Invoice invoice : invoiceService.getAllInvoices()) {
                    writer.println(invoiceProcessingService.formatInvoiceForFile(invoice));
                }
            } catch (Exception e) {
                showAlertWithError("Ошибка сохранения", "Не удалось сохранить файл: " + e.getMessage());
            }
        }
    }

    @FXML
    private void loadInvoicesFromFile() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    try {
                        Invoice invoice = invoiceProcessingService.parseInvoiceFromLine(line);
                        invoiceService.createOrUpdateInvoice(invoice);
                        notifyDocumentCreation(invoice);
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
