package client.scenes;

import client.language.Language;
import client.language.Text;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import com.sun.javafx.application.PlatformImpl;
import jakarta.ws.rs.client.Client;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AddExpenseCtrlTest {
    @Mock
    private AddExpenseCtrl sut;
    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private UserConfig userConfig;
    @Mock
    private Client client;
    @InjectMocks
    private ServerUtils server;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        server.setServer("http://localhost:8080");
        sut = new AddExpenseCtrl(server, mainCtrl);

        Language.fromLanguageFile(
                "eng", new File("../includedLanguages/eng.properties")
        );
        Translator.setCurrentLanguage(Language.languages.get("eng"));
    }

    @Test
    void verifyPriceTest() {
        String testPrice1 = "0,005";
        String testPrice2 = "67.89";
        String testPrice3 = ".67";
        String testPrice4 = "67,935.99";
        assertTrue(sut.verifyPrice(testPrice1));
        assertTrue(sut.verifyPrice(testPrice2));

        AddExpenseCtrl addExpenseCtrl = spy(sut);

        doNothing().when(addExpenseCtrl).showAlert(
                Translator.getTranslation(Text.AddExpense.Alert.invalidPrice),
                Translator.getTranslation(Text.AddExpense.Alert.startWithDigit));
        addExpenseCtrl.verifyPrice(testPrice3);
        verify(addExpenseCtrl, times(1)).showAlert(
                Translator.getTranslation(Text.AddExpense.Alert.invalidPrice),
                Translator.getTranslation(Text.AddExpense.Alert.startWithDigit));

        doNothing().when(addExpenseCtrl).showAlert(
                Translator.getTranslation(Text.AddExpense.Alert.invalidPrice),
                Translator.getTranslation(Text.AddExpense.Alert.onlyOnePeriodOrComma));
        addExpenseCtrl.verifyPrice(testPrice4);
        verify(addExpenseCtrl, times(1)).showAlert(
                Translator.getTranslation(Text.AddExpense.Alert.invalidPrice),
                Translator.getTranslation(Text.AddExpense.Alert.onlyOnePeriodOrComma));
    }
}
