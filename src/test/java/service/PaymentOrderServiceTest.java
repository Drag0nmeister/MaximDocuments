package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import documents.model.PaymentOrder;
import documents.repository.PaymentOrderRepository;
import documents.service.PaymentOrderService;
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
class PaymentOrderServiceTest {

    @Mock
    private PaymentOrderRepository paymentOrderRepository;

    @InjectMocks
    private PaymentOrderService paymentOrderService;

    @Test
    void testCreatePaymentOrder() {
        PaymentOrder paymentOrder = new PaymentOrder(1, "123", LocalDate.now(), "User", "Contractor", BigDecimal.valueOf(100), "USD", BigDecimal.valueOf(1), BigDecimal.valueOf(0.1));
        when(paymentOrderRepository.save(any(PaymentOrder.class))).thenReturn(paymentOrder);
        PaymentOrder createdPaymentOrder = paymentOrderService.createPaymentOrder(paymentOrder);
        assertNotNull(createdPaymentOrder);
        assertEquals(paymentOrder, createdPaymentOrder);
        verify(paymentOrderRepository).save(paymentOrder);
    }

    @Test
    void testGetAllPaymentOrders() {
        List<PaymentOrder> expectedPaymentOrders = Arrays.asList(
                new PaymentOrder(1, "123", LocalDate.now(), "User", "Contractor", BigDecimal.valueOf(100), "USD", BigDecimal.valueOf(1), BigDecimal.valueOf(0.1)),
                new PaymentOrder(2, "124", LocalDate.now(), "User2", "Contractor2", BigDecimal.valueOf(200), "EUR", BigDecimal.valueOf(0.9), BigDecimal.valueOf(0.2))
        );
        when(paymentOrderRepository.findAll()).thenReturn(expectedPaymentOrders);
        List<PaymentOrder> actualPaymentOrders = paymentOrderService.getAllPaymentOrders();
        assertNotNull(actualPaymentOrders);
        assertEquals(expectedPaymentOrders, actualPaymentOrders);
        verify(paymentOrderRepository).findAll();
    }

    @Test
    void testGetPaymentOrderById() {
        Integer paymentOrderId = 1;
        PaymentOrder expectedPaymentOrder = new PaymentOrder(1, "123", LocalDate.now(), "User", "Contractor", BigDecimal.valueOf(100), "USD", BigDecimal.valueOf(1), BigDecimal.valueOf(0.1));
        when(paymentOrderRepository.findById(paymentOrderId)).thenReturn(Optional.of(expectedPaymentOrder));
        PaymentOrder actualPaymentOrder = paymentOrderService.getPaymentOrderById(paymentOrderId);
        assertNotNull(actualPaymentOrder);
        assertEquals(expectedPaymentOrder, actualPaymentOrder);
        verify(paymentOrderRepository).findById(paymentOrderId);
    }

    @Test
    void testUpdatePaymentOrder() {
        Integer paymentOrderId = 1;
        PaymentOrder existingPaymentOrder = new PaymentOrder(1, "123", LocalDate.now(), "User", "Contractor", BigDecimal.valueOf(100), "USD", BigDecimal.valueOf(1), BigDecimal.valueOf(0.1));
        PaymentOrder updatedDetails = new PaymentOrder(null, "1234", null, null, null, null, null, null, null);
        when(paymentOrderRepository.findById(paymentOrderId)).thenReturn(Optional.of(existingPaymentOrder));
        when(paymentOrderRepository.save(any(PaymentOrder.class))).thenReturn(existingPaymentOrder);
        PaymentOrder updatedPaymentOrder = paymentOrderService.updatePaymentOrder(paymentOrderId, updatedDetails);
        assertNotNull(updatedPaymentOrder);
        assertEquals("1234", updatedPaymentOrder.getNumber());
        verify(paymentOrderRepository).save(existingPaymentOrder);
    }

    @Test
    void testDeletePaymentOrder() {
        Integer paymentOrderId = 1;
        paymentOrderService.deletePaymentOrder(paymentOrderId);
        verify(paymentOrderRepository).deleteById(paymentOrderId);
    }
}
