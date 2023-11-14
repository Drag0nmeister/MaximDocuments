package controller;

import documents.MySpringApplication;
import documents.config.SpringFXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.testfx.framework.junit5.ApplicationTest;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootTest(classes = MySpringApplication.class)
class InvoiceControllerTest extends ApplicationTest {

    @Autowired
    private ConfigurableApplicationContext context;
    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = springFXMLLoader.load("/view/invoice.fxml");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void testCreateInvoice() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate testDate = LocalDate.parse("14.11.2023", formatter);
        String testNumber = "123";
        String testUser = "Test User";
        String testAmount = "100.00";
        String testCurrency = "USD";
        String testCurrencyRate = "1.00";
        String testProduct = "Test Product";
        String testQuantity = "10";

        clickOn("#numberField").write(testNumber);
        clickOn("#datePicker").write(testDate.format(formatter)).push(KeyCode.ENTER);
        clickOn("#userField").write(testUser);
        clickOn("#amountField").write(testAmount);
        clickOn("#currencyField").write(testCurrency);
        clickOn("#currencyRateField").write(testCurrencyRate);
        clickOn("#productField").write(testProduct);
        clickOn("#quantityField").write(testQuantity);
        clickOn("#okButton");
    }
}
