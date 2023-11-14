package documents.service;

import documents.model.Invoice;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class InvoiceProcessingService {

    public String formatInvoiceForFile(Invoice invoice) {
        return String.join(",",
                invoice.getNumber(),
                invoice.getDate().toString(),
                invoice.getUser(),
                invoice.getAmount().toString(),
                invoice.getCurrency(),
                invoice.getCurrencyRate().toString(),
                invoice.getProduct(),
                invoice.getQuantity().toString());
    }

    public Invoice parseInvoiceFromLine(String line) throws IllegalArgumentException {
        try {
            String[] parts = line.split(",");
            if (parts.length < 8) {
                throw new IllegalArgumentException("Некорректный формат строки: " + line);
            }
            return Invoice.builder()
                    .number(parts[0])
                    .date(LocalDate.parse(parts[1], DateTimeFormatter.ISO_LOCAL_DATE))
                    .user(parts[2])
                    .amount(new BigDecimal(parts[3]))
                    .currency(parts[4])
                    .currencyRate(new BigDecimal(parts[5]))
                    .product(parts[6])
                    .quantity(new BigDecimal(parts[7]))
                    .build();
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new IllegalArgumentException("Ошибка парсинга строки: " + line, e);
        }
    }
}
