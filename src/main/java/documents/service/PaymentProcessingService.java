package documents.service;

import documents.model.Payment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class PaymentProcessingService {

    public String formatPaymentForFile(Payment payment) {
        return String.join(",",
                payment.getNumber(),
                payment.getDate().toString(),
                payment.getUser(),
                payment.getAmount().toString(),
                payment.getEmployee());
    }

    public Payment parsePaymentFromLine(String line) throws IllegalArgumentException {
        String[] parts = line.split(",");
        if (parts.length < 5) {
            throw new IllegalArgumentException("Некорректный формат строки: " + line);
        }
        return Payment.builder()
                .number(parts[0])
                .date(LocalDate.parse(parts[1]))
                .user(parts[2])
                .amount(new BigDecimal(parts[3]))
                .employee(parts[4])
                .build();
    }
}
