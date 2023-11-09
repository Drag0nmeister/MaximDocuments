package documents.service;

import jakarta.persistence.EntityNotFoundException;
import documents.exception.ServiceOperationException;
import documents.model.Payment;
import documents.repository.PaymentRepository;
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
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Payment getPaymentById(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id " + id));
    }

    @Transactional
    public Payment updatePayment(Integer id, Payment paymentDetails) {
        Payment payment = getPaymentById(id);

        payment.setNumber(paymentDetails.getNumber());
        payment.setDate(paymentDetails.getDate());
        payment.setUser(paymentDetails.getUser());
        payment.setAmount(paymentDetails.getAmount());
        payment.setEmployee(paymentDetails.getEmployee());

        return paymentRepository.save(payment);
    }

    @Transactional
    public void deletePayment(Integer id) {
        paymentRepository.deleteById(id);
    }

    public void savePaymentToFile(Payment payment, String filename) throws ServiceOperationException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("Number: " + payment.getNumber() + "\n");
            writer.write("Date: " + payment.getDate().toString() + "\n");
            writer.write("User: " + payment.getUser() + "\n");
            writer.write("Amount: " + payment.getAmount().toPlainString() + "\n");
            writer.write("Employee: " + payment.getEmployee() + "\n");
        } catch (IOException e) {
            throw new ServiceOperationException("Ошибка при сохранении платежки в файл: " + filename, e);
        }
    }

    public Payment loadPaymentFromFile(String filename) throws ServiceOperationException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String number = reader.readLine().split(": ")[1];
            LocalDate date = LocalDate.parse(reader.readLine().split(": ")[1]);
            String user = reader.readLine().split(": ")[1];
            BigDecimal amount = new BigDecimal(reader.readLine().split(": ")[1]);
            String employee = reader.readLine().split(": ")[1];

            return Payment.builder()
                    .number(number)
                    .date(date)
                    .user(user)
                    .amount(amount)
                    .employee(employee)
                    .build();
        } catch (IOException e) {
            throw new ServiceOperationException("Ошибка при чтении платежки из файла: " + filename, e);
        }
    }

    @Transactional
    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }
}
