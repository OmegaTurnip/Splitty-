package client.scenes;

import client.history.ActionHistory;
import client.history.NoRedoActionsLeftException;
import client.language.Language;
import client.language.Translator;
import client.utils.ServerUtils;
import commons.Event;
import commons.Money;
import commons.Participant;
import commons.Transaction;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TransactionCellControllerTest {
    Event event;
    TransactionCellController sut;
    @Mock
    EventOverviewCtrl eventOverviewCtrl;
    @Mock
    MainCtrl mainCtrl;
    @Mock
    ServerUtils server;
    @Mock
    AlertWrapper alertWrapper;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
//        server.setServer("http://localhost:8080");
        server = mock(ServerUtils.class);
        sut = new TransactionCellController();
        event = new Event("Test event");
        eventOverviewCtrl = mock(EventOverviewCtrl.class);
        alertWrapper = mock(AlertWrapper.class);
        sut.setEvent(event);
        sut.setServer(server);
        sut.setAlertWrapper(alertWrapper);
        sut.setEventOverviewCtrl(eventOverviewCtrl);
        sut.setActionHistory(new ActionHistory());
        sut.setMainCtrl(mainCtrl);
        doNothing().when(mainCtrl).showEventOverview(any(Event.class));

        Language.fromLanguageFile(
                "eng", new File("../includedLanguages/eng.properties")
        );
        Translator.setCurrentLanguage(Language.languages.get("eng"));

        Money amount = new Money(new BigDecimal(5), Currency.getInstance("EUR"));
        Participant participant1 = event.addParticipant("Test 1");
        Participant participant2 = event.addParticipant("Test 2");
        List<Participant> list = new ArrayList<>();
        list.add(participant1);
        list.add(participant2);
        Transaction transaction = event.registerDebt(participant1, "test debt", amount, list, null);
        sut.setTransaction(transaction);
        doNothing().when(eventOverviewCtrl).refresh();
        when(alertWrapper.showAlertButton(any(Alert.AlertType.class), anyString(), anyString())).thenReturn(ButtonType.OK);
        when(server.removeTransaction(any(Transaction.class))).thenReturn(transaction);
    }

    @Test
    void removeTransactionTest() {
        sut.removeTransaction();
        assertEquals(0, event.getTransactions().size());
    }

    @Test
    void undoAndRedoRemoveTransactionTest() {
        sut.removeTransaction();
        ActionHistory actionHistory = sut.getActionHistory();
        assertEquals(0, event.getTransactions().size());
        actionHistory.undo();
        assertEquals(1, event.getTransactions().size());
        actionHistory.redo();
        assertEquals(0, event.getTransactions().size());
        assertThrows(NoRedoActionsLeftException.class, () -> sut.getActionHistory().redo());
    }

    @Test
    void setMainCtrlTest() {
        MainCtrl mainCtrl = new MainCtrl();
        sut.setMainCtrl(mainCtrl);
        assertEquals(mainCtrl, sut.getMainCtrl());
    }
}