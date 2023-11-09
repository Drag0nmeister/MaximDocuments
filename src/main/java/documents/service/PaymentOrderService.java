package documents.service;

import jakarta.persistence.EntityNotFoundException;
import documents.exception.ServiceOperationException;
import documents.model.PaymentOrder;
import documents.repository.PaymentOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentOrderService {

    private final PaymentOrderRepository paymentOrderRepository;

    @Autowired
    public PaymentOrderService(PaymentOrderRepository paymentOrderRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
    }

    @Transactional
    public PaymentOrder createPaymentOrder(PaymentOrder paymentOrder) {
        return paymentOrderRepository.save(paymentOrder);
    }

    @Transactional(readOnly = true)
    public List<PaymentOrder> getAllPaymentOrders() {
        return paymentOrderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PaymentOrder getPaymentOrderById(Integer id) {
        return paymentOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment order not found with id " + id));
    }

    @Transactional
    public PaymentOrder updatePaymentOrder(Integer id, PaymentOrder paymentOrderDetails) {
        PaymentOrder paymentOrder = getPaymentOrderById(id);

        paymentOrder.setNumber(paymentOrderDetails.getNumber());
        paymentOrder.setDate(paymentOrderDetails.getDate());
        paymentOrder.setUser(paymentOrderDetails.getUser());
        paymentOrder.setContractor(paymentOrderDetails.getContractor());
        paymentOrder.setAmount(paymentOrderDetails.getAmount());
        paymentOrder.setCurrency(paymentOrderDetails.getCurrency());
        paymentOrder.setCurrencyRate(paymentOrderDetails.getCurrencyRate());
        paymentOrder.setCommission(paymentOrderDetails.getCommission());

        return paymentOrderRepository.save(paymentOrder);
    }

    @Transactional
    public void deletePaymentOrder(Integer id) {
        paymentOrderRepository.deleteById(id);
    }

    public void savePaymentOrderToFile(PaymentOrder paymentOrder, String filename) throws ServiceOperationException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("Number: " + paymentOrder.getNumber() + "\n");
            writer.write("Date: " + paymentOrder.getDate().toString() + "\n");
            writer.write("User: " + paymentOrder.getUser() + "\n");
            writer.write("Contractor: " + paymentOrder.getContractor() + "\n");
            writer.write("Amount: " + paymentOrder.getAmount().toPlainString() + "\n");
            writer.write("Currency: " + paymentOrder.getCurrency() + "\n");
            writer.write("Currency Rate: " + paymentOrder.getCurrencyRate().toPlainString() + "\n");
            writer.write("Commission: " + paymentOrder.getCommission().toPlainString() + "\n");
        } catch (IOException e) {
            throw new ServiceOperationException("Ошибка при сохранении платежного поручения в файл: " + filename, e);
        }
    }

    public PaymentOrder loadPaymentOrderFromFile(String filename) throws ServiceOperationException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String number = reader.readLine().split(": ")[1];
            LocalDate date = LocalDate.parse(reader.readLine().split(": ")[1]);
            String user = reader.readLine().split(": ")[1];
            String contractor = reader.readLine().split(": ")[1];
            BigDecimal amount = new BigDecimal(reader.readLine().split(": ")[1]);
            String currency = reader.readLine().split(": ")[1];
            BigDecimal currencyRate = new BigDecimal(reader.readLine().split(": ")[1]);
            BigDecimal commission = new BigDecimal(reader.readLine().split(": ")[1]);

            return PaymentOrder.builder()
                    .number(number)
                    .date(date)
                    .user(user)
                    .contractor(contractor)
                    .amount(amount)
                    .currency(currency)
                    .currencyRate(currencyRate)
                    .commission(commission)
                    .build();
        } catch (IOException e) {
            throw new ServiceOperationException("Ошибка при чтении платежного поручения из файла: " + filename, e);
        }
    }

    @Transactional
    public void savePaymentOrder(PaymentOrder paymentOrder) {
        paymentOrderRepository.save(paymentOrder);
    }
}
