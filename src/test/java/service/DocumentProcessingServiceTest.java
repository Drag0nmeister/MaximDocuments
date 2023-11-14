package service;

import documents.model.Invoice;
import documents.model.Payment;
import documents.model.PaymentOrder;
import documents.service.DocumentProcessingService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

class DocumentProcessingServiceTest {

    private final DocumentProcessingService service = new DocumentProcessingService();

    @Test
    void testConvertInvoiceToString() {
        Invoice invoice = new Invoice(1, "123", LocalDate.of(2023, 1, 1), "User", BigDecimal.valueOf(100), "USD", BigDecimal.valueOf(1), "Product", BigDecimal.valueOf(10));
        String expected = String.join("\n",
                "Накладная",
                "ID: 1",
                "Номер: 123",
                "Дата: 2023-01-01",
                "Пользователь: User",
                "Сумма: 100",
                "Валюта: USD",
                "Курс валюты: 1",
                "Товар: Product",
                "Количество: 10"
        );
        assertEquals(expected, service.convertInvoiceToString(invoice));
    }

    @Test
    void testConvertPaymentToString() {
        Payment payment = new Payment(1, "123", LocalDate.of(2023, 1, 1), "User", BigDecimal.valueOf(100), "Employee");
        String expected = String.join("\n",
                "Платёжка",
                "ID: 1",
                "Номер: 123",
                "Дата: 2023-01-01",
                "Пользователь: User",
                "Сумма: 100",
                "Сотрудник: Employee"
        );
        assertEquals(expected, service.convertPaymentToString(payment));
    }

    @Test
    void testConvertPaymentOrderToString() {
        PaymentOrder paymentOrder = new PaymentOrder(1, "123", LocalDate.of(2023, 1, 1), "User", "Contractor", BigDecimal.valueOf(100), "USD", BigDecimal.valueOf(1), BigDecimal.valueOf(5));
        String expected = String.join("\n",
                "Заявка на оплату",
                "ID: 1",
                "Номер: 123",
                "Дата: 2023-01-01",
                "Пользователь: User",
                "Контрагент: Contractor",
                "Сумма: 100",
                "Валюта: USD",
                "Курс Валюты: 1",
                "Комиссия: 5"
        );
        assertEquals(expected, service.convertPaymentOrderToString(paymentOrder));
    }
}