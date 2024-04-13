package server.api;

import commons.*;

import jakarta.servlet.http.Part;
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

    @Mock
    private Event event;

    @Mock
    private Participant participant;

    private Long eventId;

    private Currency currency;

    private ArrayList<Participant> participants;



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

        eventId = 1L;
        event = Mockito.mock(Event.class);
        currency = Currency.getInstance("USD");
        when(ds.getExchangeRateFactory().getKnownCurrencies()).thenReturn(new HashSet<>(List.of(currency)));

        participants = new ArrayList<>();
        participant = Mockito.mock(Participant.class);
        participants.add(participant);

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
        EventController sut = new EventController(db, null, sim);
        Event event = Mockito.mock(Event.class);
        when(event.getEventName()).thenReturn("Test Event");

        List<Tag> tagList = new ArrayList<>();
        Tag tagMock = Mockito.mock(Tag.class);
        tagList.add(tagMock);
        when(event.getTags()).thenReturn(tagList);


        when(event.getParticipants()).thenReturn(participants);

        List<Transaction> transactionList = new ArrayList<>();
        Transaction transaction = Mockito.mock(Transaction.class);
        transactionList.add(transaction);
        when(event.getTransactions()).thenReturn(transactionList);

        when(transaction.getPayer()).thenReturn(participant);
        when(participant.getParticipantId()).thenReturn(1L);
        when(event.getParticipantById(1L)).thenReturn(participant);
        when(transaction.getParticipants()).thenReturn(participants);

        when(db.save(event)).thenReturn(event);
        ResponseEntity<Event> result = sut.saveEvent(event);

        assertEquals(ResponseEntity.ok(event), result);
        verify(sim, times(1)).convertAndSend("/topic/admin", event);
    }

    @Test
    public void testGetCurrencies() {
        EventController sut = new EventController(eventRepository, ds, sim);
        Set<Currency> expectedCurrencies = new HashSet<>();
        expectedCurrencies.add(Currency.getInstance("USD"));
        expectedCurrencies.add(Currency.getInstance("EUR"));
        when(exchangeRateFactory.getKnownCurrencies()).thenReturn(expectedCurrencies);
        ResponseEntity<Set<Currency>> result = sut.getCurrencies();
        assertEquals(ResponseEntity.ok(expectedCurrencies), result);
    }

    @Test
    public void testGetSumOfExpenses() {
        EventRepository eventRepository = Mockito.mock(EventRepository.class);
        EventController sut = new EventController(eventRepository, ds, sim);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        BigDecimal amount = new BigDecimal(100);
        Money expected = new Money(amount, currency);
        when(ds.sumOfExpenses(event, currency)).thenReturn(expected);

        Set<Currency> knownCurrencies = new HashSet<>();
        knownCurrencies.add(currency);
        when(exchangeRateFactory.getKnownCurrencies()).thenReturn(knownCurrencies);

        ResponseEntity<Money> result = sut.getSumOfExpenses(eventId, currency);
        assertEquals(ResponseEntity.ok(expected), result);
    }

    @Test
    public void testGetSumOfExpensesErrorCases() {
        EventRepository eventRepositoryMock = Mockito.mock(EventRepository.class);
        EventController sut = new EventController(eventRepositoryMock, ds, null);

        Currency invalid = null;

        ResponseEntity<Money> result = sut.getSumOfExpenses(eventId, invalid);
        assertEquals(ResponseEntity.badRequest().build(), result);

        when(eventRepositoryMock.findById(eventId)).thenReturn(Optional.empty());
        ResponseEntity<Money> result2 = sut.getSumOfExpenses(eventId, invalid);
        assertEquals(ResponseEntity.badRequest().build(), result2);

        when(eventRepositoryMock.findById(eventId)).thenReturn(Optional.of(Mockito.mock(Event.class)));
        when(ds.getExchangeRateFactory().getKnownCurrencies()).thenReturn(new HashSet<>());
        ResponseEntity<Money> result3 = sut.getSumOfExpenses(eventId, currency);
        assertEquals(ResponseEntity.badRequest().build(), result3);
    }

    @Test
    public void testGetSimplification() {
        EventRepository eventRepository = Mockito.mock(EventRepository.class);
        EventController sut = new EventController(eventRepository, ds, null);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        Currency currency2 = null;

        ResponseEntity<Set<Debt>> intResult = ResponseEntity.badRequest().build();
        assertEquals(sut.getSimplification(eventId, currency2), intResult);

        ResponseEntity<Set<Debt>> intResult2 = ResponseEntity.ok(new HashSet<>());
        assertEquals(sut.getSimplification(eventId, currency), intResult2);
        when(event.getParticipants()).thenReturn(participants);

        when(ds.getExchangeRateFactory().getKnownCurrencies()).thenReturn(new HashSet<>(List.of(currency)));

        Set<Debt> expectedDebts = new HashSet<>();
        Debt debt = Mockito.mock(Debt.class);
        expectedDebts.add(debt);
        when(ds.simplify()).thenReturn(expectedDebts);

        ResponseEntity<Set<Debt>> result = sut.getSimplification(eventId, currency);
        assertEquals(ResponseEntity.ok(expectedDebts), result);
    }

    @Test
    public void testConvertMoney() {
        EventController sut = new EventController(null, ds, null);

        Money money = Mockito.mock(Money.class);
        LocalDate date = LocalDate.now();

        Money expectedMoney = Mockito.mock(Money.class);
        ExchangeRate exchangeRate = Mockito.mock(ExchangeRate.class);
        when(exchangeRateFactory.getExchangeRate(date, money.getCurrency(), currency)).thenReturn(exchangeRate);
        when(exchangeRate.convert(money)).thenReturn(expectedMoney);
        ResponseEntity<Set<Debt>> intResult = ResponseEntity.badRequest().build();
        assertEquals(sut.convertMoney(null, currency, date), intResult);

        Set<Currency> knownCurrencies = new HashSet<>();
        knownCurrencies.add(currency);
        when(exchangeRateFactory.getKnownCurrencies()).thenReturn(knownCurrencies);

        ResponseEntity<Money> result = sut.convertMoney(money, currency, date);
        assertEquals(ResponseEntity.ok(expectedMoney), result);
    }


    @Test
    public void testGetBalanceOfParticipants() {
        EventRepository eventRepository = Mockito.mock(EventRepository.class);
        EventController sut = new EventController(eventRepository, ds, null);

        ResponseEntity<Set<Debt>> intResult = ResponseEntity.notFound().build();
        assertEquals(sut.getSimplification(eventId, currency), intResult);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        when(event.getParticipants()).thenReturn(participants);

        Set<ParticipantValuePair> expectedBalances = new HashSet<>();
        ParticipantValuePair participantValuePair = Mockito.mock(ParticipantValuePair.class);
        expectedBalances.add(participantValuePair);
        when(ds.toBalances()).thenReturn(expectedBalances);

        intResult = ResponseEntity.badRequest().build();
        assertEquals(sut.getSimplification(eventId, null), intResult);

        ResponseEntity<Set<ParticipantValuePair>> result = sut.getBalanceOfParticipants(eventId, currency);
        assertEquals(ResponseEntity.ok(expectedBalances), result);
    }

    @Test
    public void testGetShareOfParticipants() {
        EventRepository eventRepositoryMock = Mockito.mock(EventRepository.class);
        EventController eventController = new EventController(eventRepositoryMock, ds, null);
        when(eventRepositoryMock.findById(eventId)).thenReturn(Optional.of(event));

        when(ds.getExchangeRateFactory().getKnownCurrencies()).thenReturn(new HashSet<>(List.of(currency)));
        when(event.getParticipants()).thenReturn(participants);

        Set<ParticipantValuePair> expectedShares = new HashSet<>();
        ParticipantValuePair participantValuePair = Mockito.mock(ParticipantValuePair.class);
        expectedShares.add(participantValuePair);
        when(ds.shareOfExpenses(event, currency)).thenReturn(expectedShares);

        ResponseEntity<Set<ParticipantValuePair>> result = eventController.getShareOfParticipants(eventId, currency);
        assertEquals(ResponseEntity.ok(expectedShares), result);
    }

}
