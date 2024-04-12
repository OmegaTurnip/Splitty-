package server.api;

import commons.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.EventRepository;
import server.financial.DebtSimplifier;
import server.financial.ExchangeRate;
import server.financial.ExchangeRateFactory;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


public class EventControllerTest {
    private TestEventRepository eventRepository;
    private EventController sut;

    private Event testEvent1;
    private List<Event> events;
    @Mock
    private SimpMessagingTemplate sim;

    @Mock
    private ExchangeRateFactory exchangeRateFactory;

    @Mock
    private DebtSimplifier ds;



    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        eventRepository = new TestEventRepository();

        LocalDate today = LocalDate.now();
        Currency USD = Currency.getInstance("USD");
        Currency EUR = Currency.getInstance("EUR");

        exchangeRateFactory = Mockito.mock(ExchangeRateFactory.class);
        ds = Mockito.mock(DebtSimplifier.class);

        sut = new EventController(eventRepository, null, sim);
        testEvent1 = new Event("testEvent1");
        testEvent1.setId(100L);
        testEvent1.setInviteCode("43fabbfca0644e5db1d0c1e3cb0d5416");
        events = new ArrayList<>();
        events.add(testEvent1);
        when(ds.getExchangeRateFactory()).thenReturn(exchangeRateFactory);
    }

    @Test
    void createEventTest() {
        var retEvent = sut.createEvent(testEvent1);
        Mockito.doNothing().when(sim).convertAndSend("/topic/admin", testEvent1);
        assertEquals(retEvent.getBody(), testEvent1);

    }

    @Test
    void saveEventTest() {
        Mockito.doNothing().when(sim).convertAndSend("/topic/admin", testEvent1);
        sut.saveEvent(testEvent1);
        var retEvent = eventRepository.findById(testEvent1.getId()).get();
        assertEquals(retEvent.getEventName(), "testEvent1");
        testEvent1.setEventName("newName");
        sut.saveEvent(testEvent1);
        retEvent = eventRepository.findById(testEvent1.getId()).get();
        assertEquals(retEvent.getEventName(), "newName");
    }

    @Test
    void saveEventBadNameTest() {
        testEvent1.setEventName("");
        Mockito.doNothing().when(sim).convertAndSend("/topic/admin", testEvent1);
        var retEvent = sut.saveEvent(testEvent1);
        assertTrue(retEvent.getStatusCode().is4xxClientError());
    }

    @Test
    void getEventTest() {
        eventRepository.save(testEvent1);
        var retEvent = sut.getEvent(testEvent1.getId());
        assertEquals(retEvent.getBody(), testEvent1);
    }

    @Test
    void getEventTestNonExistentTest() {
        var retEvent = sut.getEvent(100L);
        assertTrue(retEvent.getStatusCode().is4xxClientError());
    }
    @Test
    void getEventsByInviteCodeTest() {
        eventRepository.save(testEvent1);
        var retEvents = sut.getEventsByInviteCode("43fabbfca0644e5db1d0c1e3cb0d5416");
        assertEquals(retEvents.getBody(), events);
    }

    @Test
    void getEventsByInviteCodeEmptyCodesTest() {
        var retEvents = sut.getEventsByInviteCode("");
        assertTrue(retEvents.getBody().isEmpty());

    }

    @Test
    void getEventsByInviteCodeEmptyEventsTest() {
        var retEvents = sut.getEventsByInviteCode("43fabbfca0644e5db1d0c1e3cb0d5416");
        assertTrue(retEvents.getBody().isEmpty());

    }


    @Test
    public void testSaveEvent() {
        EventRepository db = Mockito.mock(EventRepository.class);
        EventController eventController = new EventController(db, null, sim);
        Event eventMock = Mockito.mock(Event.class);
        when(eventMock.getEventName()).thenReturn("Test Event");

        List<Tag> tagList = new ArrayList<>();
        Tag tagMock = Mockito.mock(Tag.class);
        tagList.add(tagMock);
        when(eventMock.getTags()).thenReturn(tagList);

        List<Participant> participantList = new ArrayList<>();
        Participant participant = Mockito.mock(Participant.class);
        participantList.add(participant);
        when(eventMock.getParticipants()).thenReturn(participantList);

        List<Transaction> transactionList = new ArrayList<>();
        Transaction transaction = Mockito.mock(Transaction.class);
        transactionList.add(transaction);
        when(eventMock.getTransactions()).thenReturn(transactionList);

        when(transaction.getPayer()).thenReturn(participant);
        when(participant.getParticipantId()).thenReturn(1L);
        when(eventMock.getParticipantById(1L)).thenReturn(participant);

        List<Participant> transactionParticipants = new ArrayList<>();
        transactionParticipants.add(participant);
        when(transaction.getParticipants()).thenReturn(transactionParticipants);

        when(db.save(eventMock)).thenReturn(eventMock);
        ResponseEntity<Event> result = eventController.saveEvent(eventMock);

        assertEquals(ResponseEntity.ok(eventMock), result);
        verify(sim, times(1)).convertAndSend("/topic/admin", eventMock);
    }

    @Test
    public void testGetCurrencies() {
        when(ds.getExchangeRateFactory()).thenReturn(exchangeRateFactory);
        EventController eventController = new EventController(null, ds, null);

        Set<Currency> expectedCurrencies = new HashSet<>();
        expectedCurrencies.add(Currency.getInstance("USD"));
        expectedCurrencies.add(Currency.getInstance("EUR"));
        when(exchangeRateFactory.getKnownCurrencies()).thenReturn(expectedCurrencies);
        ResponseEntity<Set<Currency>> result = eventController.getCurrencies();
        assertEquals(ResponseEntity.ok(expectedCurrencies), result);
    }

    @Test
    public void testGetSumOfExpenses() {
        // Arrange
        EventRepository eventRepository = Mockito.mock(EventRepository.class);
        EventController eventController = new EventController(eventRepository, ds, null);
        Long eventId = 1L;
        Event event = Mockito.mock(Event.class);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        String currencyCode = "USD";
        Currency currency = Currency.getInstance(currencyCode);
        BigDecimal amount = new BigDecimal(100);
        Money expected = new Money(amount, currency);
        when(ds.sumOfExpenses(event, currency)).thenReturn(expected);
        Set<Currency> knownCurrencies = new HashSet<>();
        knownCurrencies.add(currency);
        when(exchangeRateFactory.getKnownCurrencies()).thenReturn(knownCurrencies);
        ResponseEntity<Money> result = eventController.getSumOfExpenses(eventId, currencyCode);
        assertEquals(ResponseEntity.ok(expected), result);
    }
}
