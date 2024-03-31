package server.api;

import commons.Debt;
import commons.Event;
import commons.Money;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;
import server.financial.ExchangeRateAPI;
import server.financial.FrankfurterExchangeRateAPI;
import server.util.DebtSimplifier;

import java.time.LocalDate;
import java.util.Currency;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/event")
public class EventController {

    private final EventRepository eventRepository;
    private final DebtSimplifier debtSimplifier;
    private final Currency baseCurrency = Currency.getInstance("EUR");
    private final ExchangeRateAPI exchangeRateAPI =
            new FrankfurterExchangeRateAPI(baseCurrency);

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

        try {
            this.debtSimplifier.getExchangeRateFactory().loadAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load exchange rates", e);
        }
    }


//    /**
//     * Gets all the events matching the list of invite codes
//     * sent in the request body
//     * @param invCodes List of invite codes
//     * @return List of events
//     */
//    @GetMapping(path = {"/myEvents"})
//    @ResponseBody
//    public ResponseEntity<List<Event>> myEvents(@RequestParam("invCodes")
//                                                    List<String> invCodes) {
//        System.out.println(invCodes);
//        List<Event> myE = eventRepository
//                .findAllByInviteCodeIsIn(invCodes);
//        System.out.println(myE);
//        return ResponseEntity.ok(myE);
//    }

    /**
     * Get all events
     * @return  All events
     */
    @GetMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<List<Event>> allEvents() {
        List<Event> events = eventRepository.findAll();
        return ResponseEntity.ok(events);
    }

    /**
     * Create an event
     * @param event The event to create
     * @return  The created event
     */
    @PostMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        eventRepository.saveAndFlush(event);
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
        eventRepository.save(event);
        messagingTemplate.convertAndSend("/topic/admin", event);
        return ResponseEntity.ok(event);
        //tbf this might not be the proper way to do PUT.
        // PUT methods should specify the URI exactly,
        // so a proper pathing would be /{id}
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
     * Get an event by invite code.
     * @param inviteCodes The list of invite codes
     * @return The event
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
        if (events == null) {
            return ResponseEntity.ok(new ArrayList<>());
//            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(events);
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
            @PathVariable("currency") String currency) {

        if (!Money.isValidCurrencyCode(currency)) {
            return ResponseEntity.badRequest().build();
        }

        Event event = eventRepository.findById(id).orElse(null);

        if (event == null || event.getParticipants().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Currency base = Currency.getInstance(currency);

        debtSimplifier.setup(base, event.getParticipants());

        refreshExchangeRates();

        debtSimplifier.addDebts(event);

        Set<Debt> result = debtSimplifier.simplify();

        return ResponseEntity.ok(result);
    }


    private void refreshExchangeRates() {
        boolean ratesAreUpToDate = exchangeRateAPI.lastRequestDate().isPresent()
                && !exchangeRateAPI.lastRequestDate().get()
                .isBefore(LocalDate.now());

        if (!ratesAreUpToDate) {
            exchangeRateAPI.getExchangeRates().ifPresent(exchangeRates ->
                    debtSimplifier.getExchangeRateFactory()
                            .generateExchangeRates(
                                    baseCurrency, exchangeRates
                            )
            );
        }
    }
}
