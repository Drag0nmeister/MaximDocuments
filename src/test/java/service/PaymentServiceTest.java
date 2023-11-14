package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import documents.model.Payment;
import documents.repository.PaymentRepository;
import documents.service.PaymentService;
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
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void testCreatePayment() {
        Payment payment = new Payment(1, "123", LocalDate.now(), "User", BigDecimal.valueOf(100), "Employee");
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        Payment createdPayment = paymentService.createPayment(payment);
        assertNotNull(createdPayment);
        assertEquals(payment, createdPayment);
        verify(paymentRepository).save(payment);
    }

    @Test
    void testGetAllPayments() {
        List<Payment> expectedPayments = Arrays.asList(
                new Payment(1, "123", LocalDate.now(), "User", BigDecimal.valueOf(100), "Employee"),
                new Payment(2, "124", LocalDate.now(), "User2", BigDecimal.valueOf(200), "Employee2")
        );
        when(paymentRepository.findAll()).thenReturn(expectedPayments);
        List<Payment> actualPayments = paymentService.getAllPayments();
        assertNotNull(actualPayments);
        assertEquals(expectedPayments, actualPayments);
        verify(paymentRepository).findAll();
    }

    @Test
    void testGetPaymentById() {
        Integer paymentId = 1;
        Payment expectedPayment = new Payment(1, "123", LocalDate.now(), "User", BigDecimal.valueOf(100), "Employee");
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(expectedPayment));
        Payment actualPayment = paymentService.getPaymentById(paymentId);
        assertNotNull(actualPayment);
        assertEquals(expectedPayment.getNumber(), actualPayment.getNumber());
        assertEquals(expectedPayment.getDate(), actualPayment.getDate());
        assertEquals(expectedPayment.getUser(), actualPayment.getUser());
        assertEquals(expectedPayment.getAmount(), actualPayment.getAmount());
        assertEquals(expectedPayment.getEmployee(), actualPayment.getEmployee());
        verify(paymentRepository).findById(paymentId);
    }

    @Test
    void testUpdatePayment() {
        Integer paymentId = 1;
        Payment existingPayment = new Payment(paymentId, "123", LocalDate.now(), "User", BigDecimal.valueOf(100), "Employee");
        Payment updatedDetails = new Payment(paymentId, "124", LocalDate.now().plusDays(1), "User2", BigDecimal.valueOf(200), "Employee2");
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(existingPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedDetails);
        Payment updatedPayment = paymentService.updatePayment(paymentId, updatedDetails);
        assertNotNull(updatedPayment);
        assertEquals(updatedDetails.getNumber(), updatedPayment.getNumber());
        assertEquals(updatedDetails.getDate(), updatedPayment.getDate());
        assertEquals(updatedDetails.getUser(), updatedPayment.getUser());
        assertEquals(updatedDetails.getAmount(), updatedPayment.getAmount());
        assertEquals(updatedDetails.getEmployee(), updatedPayment.getEmployee());
        verify(paymentRepository).findById(paymentId);
        verify(paymentRepository).save(updatedPayment);
    }

    @Test
    void testDeletePayment() {
        Integer paymentId = 1;
        paymentService.deletePayment(paymentId);
        verify(paymentRepository).deleteById(paymentId);
    }
}
