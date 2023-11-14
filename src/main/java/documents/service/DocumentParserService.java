package documents.service;

import org.springframework.stereotype.Service;
import documents.model.Invoice;
import documents.model.Payment;
import documents.model.PaymentOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class DocumentParserService {
    public Invoice parseInvoice(String block) {
        Map<String, String> dataMap = extractDataMap(block);
        return new Invoice(
                Integer.parseInt(dataMap.get("ID")),
                dataMap.get("Номер"),
                LocalDate.parse(dataMap.get("Дата")),
                dataMap.get("Пользователь"),
                new BigDecimal(dataMap.get("Сумма")),
                dataMap.get("Валюта"),
                new BigDecimal(dataMap.get("Курс валюты")),
                dataMap.get("Товар"),
                new BigDecimal(dataMap.get("Количество"))
        );
    }

    public Payment parsePayment(String block) {
        Map<String, String> dataMap = extractDataMap(block);
        return new Payment(
                Integer.parseInt(dataMap.get("ID")),
                dataMap.get("Номер"),
                LocalDate.parse(dataMap.get("Дата")),
                dataMap.get("Пользователь"),
                new BigDecimal(dataMap.get("Сумма")),
                dataMap.get("Сотрудник")
        );
    }

    public PaymentOrder parsePaymentOrder(String block) {
        Map<String, String> dataMap = extractDataMap(block);
        return new PaymentOrder(
                Integer.parseInt(dataMap.get("ID")),
                dataMap.get("Номер"),
                LocalDate.parse(dataMap.get("Дата")),
                dataMap.get("Пользователь"),
                dataMap.get("Контрагент"),
                new BigDecimal(dataMap.get("Сумма")),
                dataMap.get("Валюта"),
                new BigDecimal(dataMap.get("Курс Валюты")),
                new BigDecimal(dataMap.get("Комиссия"))
        );
    }

    private Map<String, String> extractDataMap(String block) {
        return Arrays.stream(block.split("\n"))
                .map(line -> line.split(": ", 2))
                .collect(Collectors.toMap(parts -> parts[0].trim(), parts -> parts.length > 1 ? parts[1].trim() : ""));
    }
}
