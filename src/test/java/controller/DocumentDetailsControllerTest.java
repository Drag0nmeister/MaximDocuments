package controller;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import documents.model.Invoice;
import documents.controller.DocumentDetailsController;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
class DocumentDetailsControllerTest extends ApplicationTest {

    private DocumentDetailsController controller;
    private TextArea detailsArea;

    @Start
    public void start(Stage stage) {
        detailsArea = new TextArea();
        controller = new DocumentDetailsController();
        controller.detailsArea = detailsArea;
        VBox vBox = new VBox(detailsArea);
        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void testInvoiceDetailsDisplay() {
        Invoice invoice = new Invoice();
        invoice.setNumber("123");
        invoice.setDate(LocalDate.now());
        invoice.setUser("User");
        invoice.setAmount(BigDecimal.valueOf(100));
        invoice.setCurrency("USD");
        invoice.setCurrencyRate(BigDecimal.valueOf(1));
        invoice.setProduct("Product");
        invoice.setQuantity(BigDecimal.valueOf(10));
        controller.setCurrentDocument(invoice);
        String expectedDetails = String.join("\n",
                "Информация о накладной:",
                "Номер: 123",
                "Дата: " + LocalDate.now().toString(),
                "Пользователь: User",
                "Сумма: 100",
                "Валюта: USD",
                "Курс валюты: 1",
                "Товар: Product",
                "Количество: 10"
        );
        assertEquals(expectedDetails, detailsArea.getText());
    }

    @Stop
    public void stop() {
    }
}
