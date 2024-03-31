package server.api;

import commons.Debt;
import commons.Event;
import commons.Money;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;
import server.financial.ExchangeRateAPI;
import server.financial.FrankfurterExchangeRateAPI;
import server.util.DebtSimplifier;

import java.time.LocalDate;
import java.util.Currency;
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


    /**
     * Constructor for the EventController.
     *
     * @param   eventRepository
     *          The event repository.
     * @param   debtSimplifier
     *          The debt simplifier.
     */
    public EventController(EventRepository eventRepository,
                           DebtSimplifier debtSimplifier) {
        this.eventRepository = eventRepository;
        this.debtSimplifier = debtSimplifier;

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
     * Save events
     * @param events The events to save
     * @return The events saved
     */
    @PutMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<List<Event>> saveEvents(
            @RequestBody List<Event> events) {
        eventRepository.saveAll(events);
        return ResponseEntity.ok(events);
    }

    /**
     * Save an event
     * @param event The event to save
     * @return  The event saved
     */
    @PostMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Event> saveEvent(@RequestBody Event event) {

        eventRepository.saveAndFlush(event);

        return ResponseEntity.ok(event);
    }

    /**
     * Delete an event
     * @param event The event to delete
     * @return The event deleted
     */
    @DeleteMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Event> deleteEvent(@RequestBody Event event) {
        eventRepository.delete(event);
        return ResponseEntity.ok(event);
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
     * @param inviteCode The invite code
     * @return The event
     */
    @GetMapping("/invite/{inviteCode}")
    @ResponseBody
    public ResponseEntity<Event> getEventByInviteCode(
            @PathVariable("inviteCode") String inviteCode) {
        Event event = eventRepository.findByInviteCode(inviteCode);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
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
