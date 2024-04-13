package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.history.ActionHistory;
import client.history.NoRedoActionsLeftException;
import client.language.Language;
import client.language.Text;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import com.google.inject.Guice;
import com.google.inject.Injector;

import commons.*;
import jakarta.servlet.http.Part;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Parent;

import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.IndexedCheckModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.*;
import org.springframework.messaging.simp.stomp.StompSession;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AddExpenseCtrlTest extends ApplicationTest {
    @InjectMocks
    private AddExpenseCtrl sut;
    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private ServerUtils server;

    @Mock
    private AlertWrapper alertWrapper;
    @Mock
    private EventOverviewCtrl eventOverviewCtrl;

    private Button addExpense;
    Event event;

    @Override
    public void start(Stage stage) throws Exception {
        try (MockedStatic<UserConfig> userConfigMockedStatic = Mockito.mockStatic(UserConfig.class)) {
            try (MockedConstruction<ServerUtils> mockPaymentService = Mockito.mockConstruction(ServerUtils.class, (mock, context) -> {
                when(mock.connect(Mockito.anyString())).thenReturn(Mockito.mock(StompSession.class));
            })) {
                MockitoAnnotations.openMocks(this);
                UserConfig userConfig = Mockito.mock(UserConfig.class);
                doNothing().when(mainCtrl).showEventOverview(any(Event.class));
                userConfigMockedStatic.when(UserConfig::get).thenReturn(userConfig);
                Mockito.when(userConfig.getUserLanguage()).thenReturn("eng");
                Mockito.when(userConfig.getPreferredCurrency()).thenReturn(Currency.getInstance("EUR"));
                Language.fromLanguageFile(
                        "eng", new File("../includedLanguages/eng.properties")
                );
                Translator.setCurrentLanguage(Language.languages.get("eng"));
                Injector injector = Guice.createInjector(new MyModule());
                MyFXML FXML = new MyFXML(injector);

                Pair<AddExpenseCtrl, Parent> editExpense = FXML.load(AddExpenseCtrl.class,
                        "client", "scenes", "AddExpense.fxml");

                this.sut = editExpense.getKey();
                this.sut.setEventOverviewCtrl(eventOverviewCtrl);
                this.sut.setActionHistory(new ActionHistory());
                sut.setMainCtrl(Mockito.mock(MainCtrl.class));
                sut.setAlertWrapper(Mockito.mock(AlertWrapper.class));
                Mockito.when(server.connect(Mockito.anyString())).thenReturn(Mockito.mock(StompSession.class));

                event = new Event("testEvent");
                Scene scene = new Scene(editExpense.getValue());
                stage.setScene(scene);
                stage.show();

                addExpense = lookup("#addExpense").queryAs(Button.class);
            }
        }
    }

    @BeforeAll
    public static void setupSpec() throws Exception {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
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

        when(alertWrapper.showAlertButton(Mockito.any(Alert.AlertType.class),
                Mockito.anyString(), Mockito.anyString())).thenReturn(ButtonType.OK);

        addExpenseCtrl.verifyPrice(testPrice3);
        addExpenseCtrl.verifyPrice(testPrice4);
    }

    @AfterEach
    void breakDown() {
        Mockito.reset(server);
        Mockito.reset(mainCtrl);
    }

    @Test
    public void payerSelectionNullTest() {
        clickOn("#payer");
        press(KeyCode.DOWN);
        press(KeyCode.ENTER);
        assertNull(sut.getExpensePayer());
    }

    @Test
    void payerSelectionTest() {
        ArrayList<Object> list = new ArrayList<>();
        Participant test1 = event.addParticipant("Billy");
        list.add("Boo");
        list.add(test1);

        Platform.runLater(() -> sut.setPayerItems(list));

        clickOn("#payer");
        press(KeyCode.DOWN);
        press(KeyCode.DOWN);
        press(KeyCode.ENTER);
        assertEquals(test1, sut.getExpensePayer());
    }

    @Test
    void tagSelectionTest() {
        List<Object> list = new ArrayList<>();
        list.add(Translator.getTranslation(Text.AddExpense.expenseTypePrompt));
        list.addAll(event.getTags());
        Platform.runLater(() -> sut.setExpenseTypeItems(list));

        clickOn("#expenseType");
        press(KeyCode.DOWN);
        press(KeyCode.DOWN);
        press(KeyCode.ENTER);
        assertEquals(event.getTags().get(0), sut.getExpenseTag());
    }

    @Test
    public void tagSelectionNullTest() {
        clickOn("#expenseType");
        press(KeyCode.DOWN);
        press(KeyCode.ENTER);
        assertNull(sut.getExpenseTag());
    }

    @Test
    public void emptyFromString() {
        AddExpenseCtrl.MyLocalDateStringConverter converter =
                new AddExpenseCtrl.MyLocalDateStringConverter("dd/MM/yyyy");
        assertEquals(converter.fromString(""), null);
        assertEquals(converter.fromString(null), null);
    }

    @Test
    public void wrongFormatString() {
        AddExpenseCtrl.MyLocalDateStringConverter converter =
                new AddExpenseCtrl.MyLocalDateStringConverter("dd/MM/yyyy");
        converter.setAlertWrapper(Mockito.mock(AlertWrapper.class));
        when(alertWrapper.showAlertButton(Mockito.any(Alert.AlertType.class),
                Mockito.anyString(), Mockito.anyString())).thenReturn(ButtonType.OK);
        assertNull(converter.fromString("test"));
    }

    @Test
    public void FormatString() {
        AddExpenseCtrl.MyLocalDateStringConverter converter =
                new AddExpenseCtrl.MyLocalDateStringConverter("dd/MM/yyyy");
        String input = "2000-01-01";
        LocalDate date = LocalDate.parse(input);
        assertEquals(converter.fromString("01/01/2000"), date);
    }

    @Test
    public void getCheckedParticipantsTest() {
        Participant participant1 = event.addParticipant("Test 1");
        Participant participant2 = event.addParticipant("Test 2");
        List<Participant> participantList = new ArrayList<>();
        participantList.add(participant1);
        sut.setParticipantList(participantList);

        CheckComboBox<Object> participantsMock = new CheckComboBox<>();
        sut.setParticipants(participantsMock);
        List<Object> list = new ArrayList<>();
        list.add(Translator.getTranslation(Text.AddExpense.expenseParticipantsPrompt));
        list.add(participant1);
        list.add(participant2);
        participantsMock.setCheckModel(mock(IndexedCheckModel.class));
        participantsMock.getItems().add(Translator.getTranslation(Text.AddExpense.expenseParticipantsPrompt));
        when(participantsMock.getCheckModel().getCheckedItems())
                .thenReturn(FXCollections.observableArrayList(list));

        List<Participant> result = new ArrayList<>();
        result.add(participant1);
        result.add(participant2);
        sut.getCheckedParticipants();
        assertEquals(result, sut.getParticipantList());
    }

    @Test
    void addExpenseTest() {
        sut.setExpenseToOverwrite(null);
        sut.setEvent(event);
        sut.setServer(server);

        Participant participant1 = event.addParticipant("Test 1");
        Participant participant2 = event.addParticipant("Test 2");
        List<Participant> participantList = new ArrayList<>();
        participantList.add(participant1);
        participantList.add(participant2);
        Money amount = new Money(new BigDecimal(5), Currency.getInstance("EUR"));
        Transaction test = event.registerDebt
                (participant1, "Test", amount, participantList, null, event.getTags().get(0));

        Transaction expectedResult = event.registerDebt
                (participant1, "Test", amount, participantList, null, event.getTags().get(0));
        event.removeTransaction(expectedResult);
        expectedResult.setTransactionId(1L);

        when(server.saveTransaction(any(Transaction.class))).thenReturn(expectedResult);
        sut.registerExpense(test);
        assertEquals(expectedResult, event.getTransactions().getLast());
    }

    @Test
    void editExpenseTest() {
        Participant participant1 = event.addParticipant("Test 1");
        Participant participant2 = event.addParticipant("Test 2");
        List<Participant> participantList = new ArrayList<>();
        participantList.add(participant1);
        participantList.add(participant2);
        Money amount = new Money(new BigDecimal(5), Currency.getInstance("EUR"));
        Transaction test = event.registerDebt
                (participant1, "Test", amount, participantList, null, event.getTags().get(0));
        test.setTransactionId(1L);

        Transaction expectedResult = event.registerDebt
                (participant2, "Test 1", amount, participantList, null, event.getTags().get(0));

        sut.setExpenseToOverwrite(test);
        sut.setEvent(event);
        sut.setServer(server);

        when(server.saveEvent(any(Event.class))).thenReturn(event);
        sut.registerExpense(expectedResult);
        expectedResult.setTransactionId(1L);
        assertEquals(expectedResult, event.getTransactions().getLast());
        assertEquals(expectedResult.getName(), event.getTransactions().getLast().getName());
        verify(server, times(1)).saveEvent(any(Event.class));


    }

    @Test
    public void testFromString() {
        AddExpenseCtrl.MyLocalDateStringConverter converter =
                new AddExpenseCtrl.MyLocalDateStringConverter("dd/MM/yyyy");
        converter.setAlertWrapper(Mockito.mock(AlertWrapper.class));
        
        String validDateString = "09/12/2004";
        LocalDate expected = LocalDate.of(2004, 12, 9);
        LocalDate result = converter.fromString(validDateString);
        assertEquals(expected, result);

        String invalidDateString = "invalid date";
        result = converter.fromString(invalidDateString);
        assertNull(result);
        result = converter.fromString(null);
        assertNull(result);
        result = converter.fromString("");
        assertNull(result);
        String result2 = converter.toString(null);
        assertEquals("", result2);
    }


    @Test
    public void settersGettersExpensePayer() {
        Participant testPayer = new Participant();
        testPayer.setName("payer");
        sut.setExpensePayer(testPayer);
        assertEquals(testPayer, sut.getExpensePayer());
    }

    @Test
    void editExpenseUndoAndRedoTest() {
        Participant participant1 = event.addParticipant("Test 1");
        Participant participant2 = event.addParticipant("Test 2");
        List<Participant> participantList = new ArrayList<>();
        participantList.add(participant1);
        participantList.add(participant2);
        Money amount = new Money(new BigDecimal(5), Currency.getInstance("EUR"));
        Transaction test = event.registerDebt
                (participant1, "Test", amount, participantList, LocalDate.of(23,1,1), event.getTags().get(0));
        test.setTransactionId(1L);

        Transaction expectedResult = event.registerDebt
                (participant2, "Test 1", amount, participantList, LocalDate.of(23,1,1), event.getTags().get(0));

        event.removeTransaction(expectedResult); //has null id but still removes it

        sut.setExpenseToOverwrite(test);
        sut.setEvent(event);
        sut.setServer(server);

        when(server.saveEvent(any(Event.class))).thenReturn(event);
        ActionHistory actionHistory = sut.getActionHistory();
        sut.registerExpense(expectedResult);
        expectedResult.setTransactionId(1L);
        assertEquals(1, actionHistory.totalSize());
        actionHistory.undo();
        assertEquals(test, event.getTransactions().getFirst());
        actionHistory.redo();
        assertEquals(expectedResult, event.getTransactions().getFirst());
        assertThrows(NoRedoActionsLeftException.class, actionHistory::redo);

    }
}
