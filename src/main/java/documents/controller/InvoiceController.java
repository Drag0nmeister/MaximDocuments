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
import documents.model.Invoice;
import documents.service.InvoiceService;
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
@FxmlView("view/invoice.fxml")
public class InvoiceController implements DocumentCreationListenerAware {

    @FXML
    private TextField numberField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField userField;
    @FXML
    private TextField amountField;
    @FXML
    private TextField currencyField;
    @FXML
    private TextField currencyRateField;
    @FXML
    private TextField productField;
    @FXML
    private TextField quantityField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button okButton;

    private DocumentCreationListener creationListener;

    @Autowired
    private DocumentListController documentListController;

    @Autowired
    private InvoiceService invoiceService;

    private final FileChooser fileChooser = new FileChooser();

    @PostConstruct
    private void init() {
        loadAllInvoices();
    }

    public void setCreationListener(DocumentCreationListener listener) {
        this.creationListener = listener;
    }

    private void loadAllInvoices() {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        invoices.forEach(invoice ->
                documentListController.addDocument(invoice));
    }

    @FXML
    private void createInvoice(ActionEvent event) {
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
            if (this.creationListener != null) {
                this.creationListener.onDocumentCreated(invoice);
            }
            documentListController.addDocument(invoice);
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

    private String formatInvoiceDisplayName(Invoice invoice) {
        return String.format("%s - %s", invoice.getId(), invoice.getDate().format(DateTimeFormatter.ISO_DATE));
    }

    @FXML
    private void deleteSelectedInvoice() {
        DisplayableDocument selected = documentListController.getDocumentListView().getSelectionModel().getSelectedItem();
        if (selected instanceof Invoice selectedInvoice) {
            invoiceService.deleteInvoice(selectedInvoice.getId());
            documentListController.removeDocument(selectedInvoice);
        }
    }

    private int extractIdFromInvoiceDisplay(String displayString) {
        return Integer.parseInt(displayString.split(" - ")[0]);
    }

    @FXML
    private void saveInvoicesToFile() {
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                for (Invoice invoice : invoiceService.getAllInvoices()) {
                    writer.println(formatInvoiceForFile(invoice));
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
    private void loadInvoicesFromFile() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    Invoice invoice = invoiceService.loadInvoiceFromFile(line);
                    if (invoice != null) {
                        invoiceService.createOrUpdateInvoice(invoice);
                        documentListController.addDocument(invoice);
                    }
                }
            } catch (Exception e) {
                showAlertWithError("Ошибка загрузки", "Не удалось загрузить файл: " + e.getMessage());
            }
        }
    }

    private String formatInvoiceForFile(Invoice invoice) {
        return String.join(",",
                invoice.getNumber(),
                invoice.getDate().toString(),
                invoice.getUser(),
                invoice.getAmount().toString(),
                invoice.getCurrency(),
                invoice.getCurrencyRate().toString(),
                invoice.getProduct(),
                invoice.getQuantity().toString()
        );
    }

    private Invoice parseInvoiceFromLine(String line) {
        try {
            String[] parts = line.split(",");
            return Invoice.builder()
                    .number(parts[0])
                    .date(LocalDate.parse(parts[1]))
                    .user(parts[2])
                    .amount(new BigDecimal(parts[3]))
                    .currency(parts[4])
                    .currencyRate(new BigDecimal(parts[5]))
                    .product(parts[6])
                    .quantity(new BigDecimal(parts[7]))
                    .build();
        } catch (Exception e) {
            showAlertWithError("Ошибка парсинга", "Не удалось прочитать инвойс из строки: " + line);
            return null;
        }
    }
}
