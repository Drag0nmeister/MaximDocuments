package documents.service;

import documents.model.PaymentOrder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class PaymentOrderProcessingService {

    public String formatPaymentOrderForFile(PaymentOrder paymentOrder) {
        return String.join(",",
                paymentOrder.getNumber(),
                paymentOrder.getDate().toString(),
                paymentOrder.getUser(),
                paymentOrder.getContractor(),
                paymentOrder.getAmount().toString(),
                paymentOrder.getCurrency(),
                paymentOrder.getCurrencyRate().toString(),
                paymentOrder.getCommission().toString());
    }

    public PaymentOrder parsePaymentOrderFromLine(String line) throws IllegalArgumentException {
        try {
            String[] parts = line.split(",");
            if (parts.length < 8) {
                throw new IllegalArgumentException("Некорректный формат строки: " + line);
            }
            return PaymentOrder.builder()
                    .number(parts[0])
                    .date(LocalDate.parse(parts[1], DateTimeFormatter.ISO_LOCAL_DATE))
                    .user(parts[2])
                    .contractor(parts[3])
                    .amount(new BigDecimal(parts[4]))
                    .currency(parts[5])
                    .currencyRate(new BigDecimal(parts[6]))
                    .commission(new BigDecimal(parts[7]))
                    .build();
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new IllegalArgumentException("Ошибка парсинга строки: " + line, e);
        }
    }
}
