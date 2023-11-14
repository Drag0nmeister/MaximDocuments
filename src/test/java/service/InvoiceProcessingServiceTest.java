package service;

import documents.model.Invoice;
import documents.service.InvoiceProcessingService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvoiceProcessingServiceTest {

    private final InvoiceProcessingService service = new InvoiceProcessingService();

    @Test
    void testFormatInvoiceForFile() {
        Invoice invoice = new Invoice(1, "123", LocalDate.of(2023, 1, 1), "User", new BigDecimal("100"), "USD", new BigDecimal("1"), "Product", new BigDecimal("10"));
        String expected = "123,2023-01-01,User,100,USD,1,Product,10";

        String actual = service.formatInvoiceForFile(invoice);

        assertEquals(expected, actual);
    }

    @Test
    void testParseInvoiceFromLine() {
        String invoiceLine = "123,2023-01-01,User,100,USD,1,Product,10";
        Invoice expected = new Invoice(1, "123", LocalDate.of(2023, 1, 1), "User", new BigDecimal("100"), "USD", new BigDecimal("1"), "Product", new BigDecimal("10"));

        Invoice actual = service.parseInvoiceFromLine(invoiceLine);

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
    void testParseInvoiceFromLineWithInvalidData() {
        String invalidInvoiceLine = "123,2023-01-01,User,invalid,USD,1,Product,10";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.parseInvoiceFromLine(invalidInvoiceLine);
        });

        String expectedMessage = "Ошибка парсинга строки";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
