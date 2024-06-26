package server.api;

import commons.*;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;

import server.financial.ExchangeRateFactory;
import server.financial.DebtSimplifier;

import java.time.LocalDate;
import java.util.*;


@RestController
@RequestMapping("/api/event")
public class EventController {

    private final EventRepository eventRepository;
    private final DebtSimplifier debtSimplifier;
    private final SimpMessagingTemplate messagingTemplate;


    /**
     * Constructor for the EventController.
     *
     * @param   eventRepository
     *          The event repository.
     * @param   debtSimplifier
     *          The debt simplifier.
     * @param   messagingTemplate
     *          The messaging template
     */
    public EventController(EventRepository eventRepository,
                           DebtSimplifier debtSimplifier,
                           SimpMessagingTemplate messagingTemplate) {
        this.eventRepository = eventRepository;
        this.debtSimplifier = debtSimplifier;
        this.messagingTemplate = messagingTemplate;

        if (debtSimplifier != null) {
            try {
                this.debtSimplifier.getExchangeRateFactory().loadAll();
            } catch (Exception e) {
                throw new RuntimeException("Failed to load exchange rates", e);
            }
        }
    }


    /**
     * Create an event
     * @param event The event to create
     * @return  The created event
     */
    @PostMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        eventRepository.save(event);
        messagingTemplate.convertAndSend("/topic/admin", event);
        return ResponseEntity.ok(event);
    }

    /**
     * Save an event
     * @param event The event to save
     * @return The saved event
     */
    @PutMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Event> saveEvent(@RequestBody Event event) {
        if (event.getEventName().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        for (Transaction transaction : event.getTransactions()) {
            transaction.setPayer(event.getParticipantById(
                    transaction.getPayer().getParticipantId()
            ));
            List<Participant> participants = new ArrayList<>();
            for (Participant participant : transaction.getParticipants()) {

                participants.add(event.getParticipantById(
                        participant.getParticipantId()
                ));
            }
            transaction.setParticipants(participants);
            if (transaction.getTag() != null) {
                transaction.setTag(event.getTagById(
                        transaction.getTag().getTagId()
                ));
            }

        }

        Event dbEvent = eventRepository.save(event);
        messagingTemplate.convertAndSend("/topic/admin", dbEvent);
        return ResponseEntity.ok(dbEvent);
    }

    /**
     * Get an event by id
     * @param id The id of the event
     * @return The event
     */
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Event> getEvent(@PathVariable("id") Long id) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }

    /**
     * Get events by invite codes.
     * @param inviteCodes The list of invite codes
     * @return The events
     */
    @GetMapping("/invite/{inviteCodes}")
    @ResponseBody
    public ResponseEntity<List<Event>> getEventsByInviteCode(
            @PathVariable String inviteCodes) {
        if (inviteCodes.isEmpty())
            return ResponseEntity.ok(new ArrayList<Event>());
        List<String> inviteCodesList = List.of(inviteCodes.split(","));
        List<Event> events = eventRepository
                .findByInviteCodeIn(inviteCodesList);
        if (events == null || events.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
//            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(events);
    }

    /**
     * Convert an amount to a different currency.
     * @param   money
     *          The amount to convert.
     * @param   currency
     *          The currency of the result.
     * @param   date
     *          The date of the exchange rate.
     *
     * @return  Money object with the converted amount.
     */
    @PutMapping("/convert/{currency}/{date}")
    @ResponseBody
    public ResponseEntity<Money> convertMoney(
            @RequestBody Money money,
            @PathVariable("currency") Currency currency,
            @PathVariable("date") LocalDate date) {

        if (money == null || currency == null || date == null) {
            return ResponseEntity.badRequest().build();
        }

        ExchangeRateFactory exchangeRateFactory = debtSimplifier
                .getExchangeRateFactory();

        if (!exchangeRateFactory.getKnownCurrencies().contains(currency)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                exchangeRateFactory.getExchangeRate(
                        date,
                        money.getCurrency(),
                        currency
                ).convert(money)
        );
    }

    /**
     * Get the simplified version of the debts of an event.
     *
     * @param   id
     *          The id of the event.
     * @param   currency
     *          The currency of the result.
     *
     * @return  The simplified version of the debts of the event.
     */
    @GetMapping("/{id}/simplify/{currency}")
    @ResponseBody
    public ResponseEntity<Set<Debt>> getSimplification(
            @PathVariable("id") Long id,
            @PathVariable("currency") Currency currency) {

        if (currency == null || !debtSimplifier.getExchangeRateFactory()
                .getKnownCurrencies().contains(currency)) {
            return ResponseEntity.badRequest().build();
        }

        Event event = eventRepository.findById(id).orElse(null);

        if (event == null) {
            return ResponseEntity.notFound().build();
        }

        debtSimplifier.setup(currency, event.getParticipants());

        debtSimplifier.addDebts(event);

        Set<Debt> result = debtSimplifier.simplify();

        return ResponseEntity.ok(result);
    }

    /**
     * Get the sum of the debts of an event.
     *
     * @param   id
     *          The id of the event.
     * @param   currency
     *          The currency of the result.
     *
     * @return  The sum of the debts of an event.
     */
    @GetMapping("/{id}/sum/{currency}")
    @ResponseBody
    public ResponseEntity<Money> getSumOfExpenses(
            @PathVariable("id") Long id,
            @PathVariable("currency") Currency currency) {

        if (currency == null || !debtSimplifier.getExchangeRateFactory()
                .getKnownCurrencies().contains(currency)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Event> event = eventRepository.findById(id);

        if (event.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                debtSimplifier.sumOfExpenses(event.get(), currency));
    }

    /**
     * Get the balances of the participants of an event.
     *
     * @param   id
     *          The id of the event.
     * @param   currency
     *          The currency of the result.
     *
     * @return  The balances of the participants of the event.
     */
    @GetMapping("/{id}/balance/{currency}")
    @ResponseBody
    public ResponseEntity<Set<ParticipantValuePair>> getBalanceOfParticipants(
            @PathVariable("id") Long id,
            @PathVariable("currency") Currency currency) {

        if (currency == null || !debtSimplifier.getExchangeRateFactory()
                .getKnownCurrencies().contains(currency)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Event> event = eventRepository.findById(id);

        if (event.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        debtSimplifier.setup(currency, event.get().getParticipants());

        debtSimplifier.addDebts(event.get());

        return ResponseEntity.ok(
                debtSimplifier.toBalances());
    }

    /**
     * Get the shares of the participants of an event.
     *
     * @param   id
     *          The id of the event.
     * @param   currency
     *          The currency of the result.
     *
     * @return  The shares of the participants of the event.
     */
    @GetMapping("/{id}/share/{currency}")
    @ResponseBody
    public ResponseEntity<Set<ParticipantValuePair>> getShareOfParticipants(
            @PathVariable("id") Long id,
            @PathVariable("currency") Currency currency) {

        if (currency == null || !debtSimplifier.getExchangeRateFactory()
                .getKnownCurrencies().contains(currency)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Event> event = eventRepository.findById(id);

        if (event.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                debtSimplifier.shareOfExpenses(event.get(), currency));
    }


    /**
     * Returns all available currencies.
     *
     * @return  All available currencies.
     */
    @GetMapping("/currencies")
    @ResponseBody
    public ResponseEntity<Set<Currency>> getCurrencies() {
        debtSimplifier.getExchangeRateFactory().retrieveExchangeRates();
        return ResponseEntity.ok(debtSimplifier.getExchangeRateFactory()
                .getKnownCurrencies());
    }

}
