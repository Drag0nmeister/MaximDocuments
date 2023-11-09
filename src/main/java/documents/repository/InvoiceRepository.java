package documents.repository;

import documents.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

}
