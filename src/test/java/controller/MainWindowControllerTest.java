package controller;

import documents.MySpringApplication;
import documents.config.SpringFXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;
import static org.testfx.matcher.base.NodeMatchers.isVisible;


@SpringBootTest(classes = MySpringApplication.class)
class MainWindowControllerTest extends ApplicationTest {

    @Autowired
    private ConfigurableApplicationContext context;
    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = springFXMLLoader.load("/view/mainWindow.fxml");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void testMainWindowInteractions() {
        clickOn("#invoiceButton");
        clickOn("#paymentButton");
        clickOn("#paymentOrderButton");
        clickOn("#saveButton");
        clickOn("#loadButton");
        clickOn("#viewButton");
        ListView<?> documentListView = lookup("#documentListView").query();
        verifyThat(documentListView, isNotNull());
        verifyThat(documentListView, isVisible());
        clickOn("#exitButton");
    }
}
