package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.language.Language;
import client.language.Text;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.Event;
import commons.Money;
import commons.Participant;
import commons.Transaction;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.stomp.StompSession;
import org.testfx.framework.junit5.ApplicationTest;

import org.testfx.util.WaitForAsyncUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;

public class EventOverviewCtrlTest extends ApplicationTest {
    @InjectMocks
    private EventOverviewCtrl sut;
    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private ServerUtils server;


    Event event;

    @Override
    public void start(Stage stage) throws Exception {
        try (MockedStatic<UserConfig> userConfigMockedStatic = Mockito.mockStatic(UserConfig.class)) {
            try (MockedConstruction<ServerUtils> mockPaymentService = Mockito.mockConstruction(ServerUtils.class,(mock, context)-> {
                when(mock.connect(Mockito.anyString())).thenReturn(Mockito.mock(StompSession.class));
            })) {
                UserConfig userConfig = Mockito.mock(UserConfig.class);
                userConfigMockedStatic.when(UserConfig::get).thenReturn(userConfig);
                Mockito.when(userConfig.getUserLanguage()).thenReturn("eng");
                Mockito.when(userConfig.getPreferredCurrency())
                        .thenReturn(Currency.getInstance("EUR"));
                Language.fromLanguageFile(
                        "eng", new File("../includedLanguages/eng.properties")
                );
                Translator.setCurrentLanguage(Language.languages.get("eng"));
                Injector injector = Guice.createInjector(new MyModule());
                MyFXML FXML = new MyFXML(injector);

                Pair<EventOverviewCtrl, Parent> eventOverview = FXML.load(EventOverviewCtrl.class,
                        "client", "scenes", "EventOverview.fxml");

                this.sut = eventOverview.getKey();
                MockitoAnnotations.openMocks(this).close();
                Mockito.when(server.connect(Mockito.anyString())).thenReturn(Mockito.mock(StompSession.class));
                event = new Event("testEvent");
                Scene scene = new Scene(eventOverview.getValue());
                stage.setScene(scene);
                stage.show();
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
    @AfterEach
    void breakDown() {
        Mockito.reset(server);
        Mockito.reset(mainCtrl);
    }

    @Test
    void returnToOverviewTest() {
        MainCtrl mainCtrlMock = Mockito.mock(MainCtrl.class);
        sut.setMainCtrl(mainCtrlMock);
        doNothing().when(mainCtrlMock).showStartUp();
        clickOn("#rtoButton");
        clickOn("#returnToOverview");
        verify(mainCtrlMock, times(1)).showStartUp();
    }

    @Test
    void testAddExpense()  {
        sut.setServer(server);
        UserConfig mockUserconfig = mock(UserConfig.class);
        UserConfig.dependencyInject(mockUserconfig);
        when(mockUserconfig.getPreferredCurrency()).thenReturn(Currency.getInstance("EUR"));
        sut.setEvent(event);
        event.addParticipant("testParticipant1");
        Participant participant1 = event.getParticipants().get(0);
        server.saveParticipant(participant1);
        event.addParticipant("testparticipant2");
        Participant participant2 = event.getParticipants().get(1);
        server.saveParticipant(participant2);

        sut.addExpense();
        AddExpenseCtrl addExpenseCtrlMock = new AddExpenseCtrl(server,mainCtrl);
        TextField name = new TextField("coffee");
        addExpenseCtrlMock.setEvent(event);
        addExpenseCtrlMock.setExpenseName(name);
        addExpenseCtrlMock.setExpensePayer(participant1);
        List<Participant> participantList = new ArrayList<Participant>();
        participantList.add(participant2);
        addExpenseCtrlMock.setParticipantList(participantList);
        addExpenseCtrlMock.setDate(new DatePicker());
        addExpenseCtrlMock.setPrice(new TextField("5"));
        addExpenseCtrlMock.setExpenseTag(null);
        ChoiceBox<String> mockCurrency = mock(ChoiceBox.class);
        addExpenseCtrlMock.setCurrency(mockCurrency);
        when(mockCurrency.getValue()).thenReturn("EUR");
        Money amount = new Money(new BigDecimal(5),  Currency.getInstance("EUR"));
        when(server.convertMoney(any(Money.class), any(Currency.class), any(LocalDate.class))).thenReturn(amount);

        Transaction transaction = addExpenseCtrlMock.getExpense();
        server.saveEvent(event);
        sut.refresh();
        Transaction equalTransaction = new Transaction(participant1, name.getText(), amount, participantList,event, null, false);
        transaction.setTransactionId(1L);
        equalTransaction.setTransactionId(1L);
        assertEquals(transaction, equalTransaction);
        WaitForAsyncUtils.waitForFxEvents();
        Transaction transaction1 = sut.getExpensesListView().getItems().getFirst();
        assertEquals(transaction1, transaction);

        // add second expense
        name = new TextField("tea");
        addExpenseCtrlMock.setEvent(event);
        addExpenseCtrlMock.setExpenseName(name);
        addExpenseCtrlMock.setExpensePayer(participant1);
        addExpenseCtrlMock.setParticipantList(participantList);
        addExpenseCtrlMock.setDate(new DatePicker());
        addExpenseCtrlMock.setPrice(new TextField("5"));
        addExpenseCtrlMock.setExpenseTag(null);
        Transaction transaction2 = addExpenseCtrlMock.getExpense();
        server.saveEvent(event);
        sut.refresh();
        WaitForAsyncUtils.waitForFxEvents();
        List<Transaction> ExspenseListView = sut.getExpensesListView().getItems();
        Transaction equalTransaction2 = new Transaction(participant1, name.getText(), amount, participantList,event, null, false);
        transaction2.setTransactionId(2L);
        equalTransaction2.setTransactionId(2L);
        assertEquals(transaction2, equalTransaction2);
        assertEquals(sut.getExpensesListView().getItems().get(1), transaction2);
    }

    @Test
    void testGetMainCtrl() {
        sut.setMainCtrl(mainCtrl);
        assertEquals(sut.getMainCtrl(), mainCtrl);
    }

    @Test
    public void testShowInviteCodeCopy() {
        sut.setEvent(event);
        Platform.runLater(() -> sut.showInviteCode());
        WaitForAsyncUtils.waitForFxEvents();
        Button copyButton = lookup(Translator.getTranslation(Text.EditName.copy)).queryButton();
        clickOn(copyButton);
        verify(mainCtrl, times(1)).showEventOverview(event);

    }

    @Test
    public void testShowInviteCodeCancel() {
        sut.setEvent(event);
        Platform.runLater(() -> sut.showInviteCode());
        WaitForAsyncUtils.waitForFxEvents();
        Button cancelButton = lookup(Translator.getTranslation(Text.EditName.cancel)).queryButton();
        clickOn(cancelButton);
        verify(mainCtrl, times(2)).showEventOverview(event);
    }



}
