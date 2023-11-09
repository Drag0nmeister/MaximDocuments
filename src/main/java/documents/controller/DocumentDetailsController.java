package documents.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import documents.model.DisplayableDocument;
import javafx.event.ActionEvent;
import documents.model.Invoice;
import documents.model.Payment;
import documents.model.PaymentOrder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class DocumentDetailsController {

    @FXML
    private TextArea detailsArea;

    private DisplayableDocument currentDocument;

    public void setCurrentDocument(DisplayableDocument document) {
        this.currentDocument = document;
        displayDetails();
    }

    private void displayDetails() {
        if (currentDocument != null) {
            StringBuilder details = new StringBuilder();

            if (currentDocument instanceof Invoice invoice) {
                details.append("Информация о накладной:\n")
                        .append("Номер: ").append(invoice.getNumber()).append("\n")
                        .append("Дата: ").append(invoice.getDate().toString()).append("\n")
                        .append("Пользователь: ").append(invoice.getUser()).append("\n")
                        .append("Сумма: ").append(invoice.getAmount()).append("\n")
                        .append("Валюта: ").append(invoice.getCurrency()).append("\n")
                        .append("Курс Валюты: ").append(invoice.getCurrencyRate()).append("\n")
                        .append("Товар: ").append(invoice.getProduct()).append("\n")
                        .append("Количество: ").append(invoice.getQuantity()).append("\n");
            } else if (currentDocument instanceof Payment payment) {
                details.append("Информация о платёжке:\n")
                        .append("Номер: ").append(payment.getNumber()).append("\n")
                        .append("Дата: ").append(payment.getDate().toString()).append("\n")
                        .append("Пользователь: ").append(payment.getUser()).append("\n")
                        .append("Сумма: ").append(payment.getAmount()).append("\n")
                        .append("Сотрудник: ").append(payment.getEmployee()).append("\n");
            } else if (currentDocument instanceof PaymentOrder paymentOrder) {
                details.append("Информация о заявке на оплату:\n")
                        .append("Номер: ").append(paymentOrder.getNumber()).append("\n")
                        .append("Дата: ").append(paymentOrder.getDate().toString()).append("\n")
                        .append("Пользователь: ").append(paymentOrder.getUser()).append("\n")
                        .append("Контрагент: ").append(paymentOrder.getContractor()).append("\n")
                        .append("Сумма: ").append(paymentOrder.getAmount()).append("\n")
                        .append("Валюта: ").append(paymentOrder.getCurrency()).append("\n")
                        .append("Курс Валюты: ").append(paymentOrder.getCurrencyRate()).append("\n")
                        .append("Комиссия: ").append(paymentOrder.getCommission()).append("\n");
            }

            detailsArea.setText(details.toString());
        } else {
            detailsArea.setText("No document selected.");
        }
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        ((Stage) detailsArea.getScene().getWindow()).close();
    }

}
