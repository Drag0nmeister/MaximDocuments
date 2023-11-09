package documents.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment implements DisplayableDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String number;

    private LocalDate date;

    @Column(name = "user_name")
    private String user;

    private BigDecimal amount;

    private String employee;

    @Override
    public String getDisplayText() {
        return "Платёжка от " + date.toString() + " номер " + number;
    }
}
