package documents.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment_orders")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOrder implements DisplayableDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String number;

    private LocalDate date;

    @Column(name = "user_name")
    private String user;

    private String contractor;

    private BigDecimal amount;

    private String currency;

    private BigDecimal currencyRate;

    private BigDecimal commission;

    @Override
    public String getDisplayText() {
        return "Заявка на оплату от " + date.toString() + " номер " + number;
    }
}
