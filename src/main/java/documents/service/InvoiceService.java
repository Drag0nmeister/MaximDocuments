package documents.service;

import documents.exception.ServiceOperationException;
import documents.model.Invoice;
import documents.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public Invoice createOrUpdateInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public Optional<Invoice> getInvoiceById(Integer id) {
        return invoiceRepository.findById(id);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Transactional
    public void deleteInvoice(Integer id) {
        invoiceRepository.deleteById(id);
    }

    public Invoice loadInvoiceFromFile(String filename) throws ServiceOperationException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String number = reader.readLine().split(": ")[1];
            LocalDate date = LocalDate.parse(reader.readLine().split(": ")[1]);
            String user = reader.readLine().split(": ")[1];
            BigDecimal amount = new BigDecimal(reader.readLine().split(": ")[1]);
            String currency = reader.readLine().split(": ")[1];
            BigDecimal currencyRate = new BigDecimal(reader.readLine().split(": ")[1]);
            String product = reader.readLine().split(": ")[1];
            BigDecimal quantity = new BigDecimal(reader.readLine().split(": ")[1]);

            return Invoice.builder()
                    .number(number)
                    .date(date)
                    .user(user)
                    .amount(amount)
                    .currency(currency)
                    .currencyRate(currencyRate)
                    .product(product)
                    .quantity(quantity)
                    .build();
        } catch (IOException e) {
            throw new ServiceOperationException("Ошибка при чтении накладной из файла: " + filename, e);
        }
    }

    @Transactional
    public void saveInvoice(Invoice invoice) {
        invoiceRepository.save(invoice);
    }
}
