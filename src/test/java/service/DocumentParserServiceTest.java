package service;

import documents.model.Invoice;
import documents.model.Payment;
import documents.model.PaymentOrder;
import documents.service.DocumentParserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

class DocumentParserServiceTest {

    private final DocumentParserService service = new DocumentParserService();

    @Test
    void testParseInvoice() {
        String invoiceData = "ID: 1\nНомер: 123\nДата: 2023-01-01\nПользователь: User\nСумма: 100\nВалюта: USD\nКурс валюты: 1\nТовар: Product\nКоличество: 10";
        Invoice expected = new Invoice(1, "123", LocalDate.of(2023, 1, 1), "User", new BigDecimal("100"), "USD", new BigDecimal("1"), "Product", new BigDecimal("10"));

        Invoice actual = service.parseInvoice(invoiceData);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getNumber(), actual.getNumber());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getUser(), actual.getUser());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getCurrency(), actual.getCurrency());
        assertEquals(expected.getCurrencyRate(), actual.getCurrencyRate());
        assertEquals(expected.getProduct(), actual.getProduct());
        assertEquals(expected.getQuantity(), actual.getQuantity());
    }

    @Test
    void testParsePayment() {
        String paymentData = "ID: 1\nНомер: 123\nДата: 2023-01-01\nПользователь: User\nСумма: 100\nСотрудник: Employee";
        Payment expected = new Payment(1, "123", LocalDate.of(2023, 1, 1), "User", new BigDecimal("100"), "Employee");

        Payment actual = service.parsePayment(paymentData);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getNumber(), actual.getNumber());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getUser(), actual.getUser());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getEmployee(), actual.getEmployee());
    }

    @Test
    void testParsePaymentOrder() {
        String paymentOrderData = "ID: 1\nНомер: 123\nДата: 2023-01-01\nПользователь: User\nКонтрагент: Contractor\nСумма: 100\nВалюта: USD\nКурс Валюты: 1\nКомиссия: 5";
        PaymentOrder expected = new PaymentOrder(1, "123", LocalDate.of(2023, 1, 1), "User", "Contractor", new BigDecimal("100"), "USD", new BigDecimal("1"), new BigDecimal("5"));

        PaymentOrder actual = service.parsePaymentOrder(paymentOrderData);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getNumber(), actual.getNumber());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getUser(), actual.getUser());
        assertEquals(expected.getContractor(), actual.getContractor());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getCurrency(), actual.getCurrency());
        assertEquals(expected.getCurrencyRate(), actual.getCurrencyRate());
        assertEquals(expected.getCommission(), actual.getCommission());
    }
}
