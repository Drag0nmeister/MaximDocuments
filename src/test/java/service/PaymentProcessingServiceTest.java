package service;

import documents.model.Payment;
import documents.service.PaymentProcessingService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentProcessingServiceTest {

    private final PaymentProcessingService service = new PaymentProcessingService();

    @Test
    void testFormatPaymentForFile() {
        Payment payment = new Payment(1, "123", LocalDate.of(2023, 1, 1), "User", new BigDecimal("100"), "Employee");
        String expected = "123,2023-01-01,User,100,Employee";

        String actual = service.formatPaymentForFile(payment);

        assertEquals(expected, actual);
    }

    @Test
    void testParsePaymentFromLine() {
        String paymentLine = "123,2023-01-01,User,100,Employee";
        Payment expected = new Payment(1, "123", LocalDate.of(2023, 1, 1), "User", new BigDecimal("100"), "Employee");

        Payment actual = service.parsePaymentFromLine(paymentLine);

        assertEquals(expected.getNumber(), actual.getNumber());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getUser(), actual.getUser());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getEmployee(), actual.getEmployee());
    }
}
