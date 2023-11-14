package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import documents.exception.ServiceOperationException;
import documents.model.Invoice;
import documents.repository.InvoiceRepository;
import documents.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    void testCreateOrUpdateInvoice() {
        Invoice invoice = new Invoice(1, "123", LocalDate.now(), "User", BigDecimal.valueOf(100), "USD", BigDecimal.valueOf(1), "Product", BigDecimal.valueOf(10));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        Invoice createdInvoice = invoiceService.createOrUpdateInvoice(invoice);
        assertNotNull(createdInvoice);
        assertEquals(invoice, createdInvoice);
        verify(invoiceRepository).save(invoice);
    }

    @Test
    void testGetInvoiceById() {
        Integer invoiceId = 1;
        Invoice expectedInvoice = new Invoice(1, "123", LocalDate.now(), "User", BigDecimal.valueOf(100), "USD", BigDecimal.valueOf(1), "Product", BigDecimal.valueOf(10));
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(expectedInvoice));
        Optional<Invoice> actualInvoice = invoiceService.getInvoiceById(invoiceId);
        assertTrue(actualInvoice.isPresent());
        assertEquals(expectedInvoice, actualInvoice.get());
        verify(invoiceRepository).findById(invoiceId);
    }

    @Test
    void testGetAllInvoices() {
        List<Invoice> expectedInvoices = Arrays.asList(
                new Invoice(1, "123", LocalDate.now(), "User", BigDecimal.valueOf(100), "USD", BigDecimal.valueOf(1), "Product", BigDecimal.valueOf(10)),
                new Invoice(2, "124", LocalDate.now(), "User2", BigDecimal.valueOf(200), "EUR", BigDecimal.valueOf(0.9), "Product2", BigDecimal.valueOf(20))
        );
        when(invoiceRepository.findAll()).thenReturn(expectedInvoices);
        List<Invoice> actualInvoices = invoiceService.getAllInvoices();
        assertNotNull(actualInvoices);
        assertEquals(expectedInvoices, actualInvoices);
        verify(invoiceRepository).findAll();
    }

    @Test
    void testDeleteInvoice() {
        Integer invoiceId = 1;
        invoiceService.deleteInvoice(invoiceId);
        verify(invoiceRepository).deleteById(invoiceId);
    }

    @Test
    void testLoadInvoiceFromFileWithException() {
        String filename = "invalid_file.txt";
        assertThrows(ServiceOperationException.class, () -> {
            invoiceService.loadInvoiceFromFile(filename);
        });
    }
}
