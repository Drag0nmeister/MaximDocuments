package documents.controller;

import documents.listener.DocumentSelectedListener;
import documents.model.DisplayableDocument;
import documents.model.Invoice;
import documents.model.Payment;
import documents.model.PaymentOrder;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class DocumentDetailsController implements DocumentSelectedListener {

    @FXML
    public TextArea detailsArea;

    @Override
    public void onDocumentSelected(DisplayableDocument document) {
        setCurrentDocument(document);
    }

    public void setCurrentDocument(DisplayableDocument document) {
        String details;

        if (document instanceof Invoice invoice) {
            details = String.join("\n",
                    "Информация о накладной:",
                    "Номер: " + invoice.getNumber(),
                    "Дата: " + invoice.getDate().toString(),
                    "Пользователь: " + invoice.getUser(),
                    "Сумма: " + invoice.getAmount(),
                    "Валюта: " + invoice.getCurrency(),
                    "Курс валюты: " + invoice.getCurrencyRate(),
                    "Товар: " + invoice.getProduct(),
                    "Количество: " + invoice.getQuantity()
            );
        } else if (document instanceof Payment payment) {
            details = String.join("\n",
                    "Информация о платёжке:",
                    "Номер: " + payment.getNumber(),
                    "Дата: " + payment.getDate().toString(),
                    "Пользователь: " + payment.getUser(),
                    "Сумма: " + payment.getAmount(),
                    "Сотрудник: " + payment.getEmployee()
            );
        } else if (document instanceof PaymentOrder paymentOrder) {
            details = String.join("\n",
                    "Информация о заявке на оплату:",
                    "Номер: " + paymentOrder.getNumber(),
                    "Дата: " + paymentOrder.getDate().toString(),
                    "Пользователь: " + paymentOrder.getUser(),
                    "Контрагент: " + paymentOrder.getContractor(),
                    "Сумма: " + paymentOrder.getAmount(),
                    "Валюта: " + paymentOrder.getCurrency(),
                    "Курс валюты: " + paymentOrder.getCurrencyRate(),
                    "Комиссия: " + paymentOrder.getCommission()
            );
        } else {
            details = "Документ не выбран.";
        }

        detailsArea.setText(details);
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        ((Stage) detailsArea.getScene().getWindow()).close();
    }
}
