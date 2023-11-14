package service;

import documents.model.PaymentOrder;
import documents.service.PaymentOrderProcessingService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentOrderProcessingServiceTest {

    private final PaymentOrderProcessingService service = new PaymentOrderProcessingService();

    @Test
    void testFormatPaymentOrderForFile() {
        PaymentOrder paymentOrder = new PaymentOrder(1, "123", LocalDate.of(2023, 1, 1), "User", "Contractor", new BigDecimal("100"), "USD", new BigDecimal("1"), new BigDecimal("5"));
        String expected = "123,2023-01-01,User,Contractor,100,USD,1,5";

        String actual = service.formatPaymentOrderForFile(paymentOrder);

        assertEquals(expected, actual);
    }

    @Test
    void testParsePaymentOrderFromLine() {
        String paymentOrderLine = "123,2023-01-01,User,Contractor,100,USD,1,5";
        PaymentOrder expected = new PaymentOrder(1, "123", LocalDate.of(2023, 1, 1), "User", "Contractor", new BigDecimal("100"), "USD", new BigDecimal("1"), new BigDecimal("5"));

        PaymentOrder actual = service.parsePaymentOrderFromLine(paymentOrderLine);

        assertEquals(expected.getNumber(), actual.getNumber());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getUser(), actual.getUser());
        assertEquals(expected.getContractor(), actual.getContractor());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getCurrency(), actual.getCurrency());
        assertEquals(expected.getCurrencyRate(), actual.getCurrencyRate());
        assertEquals(expected.getCommission(), actual.getCommission());
    }

    @Test
    void testParsePaymentOrderFromLineWithInvalidData() {
        String invalidPaymentOrderLine = "123,2023-01-01,User,Contractor,invalid,USD,1,5";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.parsePaymentOrderFromLine(invalidPaymentOrderLine);
        });

        String expectedMessage = "Ошибка парсинга строки";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
