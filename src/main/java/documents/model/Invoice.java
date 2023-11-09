package documents.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice implements DisplayableDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String number;

    @Column(name = "date", columnDefinition = "DATE")
    private LocalDate date;

    @Column(name = "user_name")
    private String user;

    private BigDecimal amount;

    private String currency;

    private BigDecimal currencyRate;

    private String product;

    private BigDecimal quantity;

    @Override
    public String getDisplayText() {
        return "Накладная от " + date.toString() + " номер " + number;
    }
}
