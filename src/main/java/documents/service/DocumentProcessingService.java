package documents.service;

import documents.model.Invoice;
import documents.model.Payment;
import documents.model.PaymentOrder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class DocumentProcessingService {

    public String convertInvoiceToString(Invoice invoice) {
        return String.join("\n",
                "Накладная",
                "ID: " + invoice.getId(),
                "Номер: " + invoice.getNumber(),
                "Дата: " + invoice.getDate().format(DateTimeFormatter.ISO_DATE),
                "Пользователь: " + invoice.getUser(),
                "Сумма: " + invoice.getAmount(),
                "Валюта: " + invoice.getCurrency(),
                "Курс валюты: " + invoice.getCurrencyRate(),
                "Товар: " + invoice.getProduct(),
                "Количество: " + invoice.getQuantity()
        );
    }

    public String convertPaymentToString(Payment payment) {
        return String.join("\n",
                "Платёжка",
                "ID: " + payment.getId(),
                "Номер: " + payment.getNumber(),
                "Дата: " + payment.getDate().format(DateTimeFormatter.ISO_DATE),
                "Пользователь: " + payment.getUser(),
                "Сумма: " + payment.getAmount(),
                "Сотрудник: " + payment.getEmployee()
        );
    }

    public String convertPaymentOrderToString(PaymentOrder paymentOrder) {
        return String.join("\n",
                "Заявка на оплату",
                "ID: " + paymentOrder.getId(),
                "Номер: " + paymentOrder.getNumber(),
                "Дата: " + paymentOrder.getDate().format(DateTimeFormatter.ISO_DATE),
                "Пользователь: " + paymentOrder.getUser(),
                "Контрагент: " + paymentOrder.getContractor(),
                "Сумма: " + paymentOrder.getAmount(),
                "Валюта: " + paymentOrder.getCurrency(),
                "Курс Валюты: " + paymentOrder.getCurrencyRate(),
                "Комиссия: " + paymentOrder.getCommission()
        );
    }

}
