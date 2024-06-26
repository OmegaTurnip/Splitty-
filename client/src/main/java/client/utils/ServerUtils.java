/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

    private final UserConfig userSettings;

    private String server;
    private String webSocketServer;

    private Client client;

    private StompSession session;

    private Set<Currency> availableCurrenciesCache = null;

    /**
     * Getter.
     * @return Get User Settings.
     */
    public UserConfig getUserSettings() {
        return userSettings;
    }

    /**
     * Getter.
     * @return Get server URL.
     */
    public String getServer() {
        return server;
    }
    /**
     * Server Utils constructed with UserConfig file.
     */
    public ServerUtils() {
        this.userSettings = UserConfig.get();
        this.server = userSettings.getServerUrl();
        this.client = ClientBuilder.newClient(new ClientConfig());
//        this.webSocketServer = "ws://localhost:8080/websocket";
        this.webSocketServer = generateWsURL(server);
        //Make this configurable rather than hard coded.
        session = connect(webSocketServer);
    }

    /**
     * Generate WebSocket URL.
     * @param url The URL.
     * @return The web socket URL.
     */
    public String generateWsURL(String url) {
        return "ws:" + url.split(":", 2)[1] + "websocket";
    }

    /**
     * Injectable constructor
     * @param userSettings Inject the userSettings.
     */
    public ServerUtils(UserConfig userSettings) {
        this.userSettings = userSettings;
        this.server = userSettings.getServerUrl();
    }


    /**
     * Injectable constructor
     * @param userSettings Inject the userSettings.
     * @param client Inject client.
     */
    public ServerUtils(UserConfig userSettings, Client client) {
        this.userSettings = userSettings;
        this.client = client;
    }

    /**
     * Injectable constructor
     * @param userSettings Inject the userSettings.
     * @param server Inject custom URL.
     * @param client Inject client.
     */
    public ServerUtils(UserConfig userSettings, String server, Client client) {
        this.userSettings = userSettings;
        this.server = server;
        this.client = client;
    }

    /**
     * Get available currencies, is guaranteed to always return the same due to
     * caching (so storing the result also isn't needed).
     *
     * @return  The available currencies.
     */
    public Set<Currency> getAvailableCurrencies() {
        if (availableCurrenciesCache == null) {
            availableCurrenciesCache = client //
                    .target(server).path("api/event/currencies") //
                    .request(APPLICATION_JSON) //
                    .accept(APPLICATION_JSON) //
                    .get(new GenericType<Set<Currency>>() {
                    });
        }
        return availableCurrenciesCache;
    }

    /**
     * Create Event REST API request.
     * @param event The event to be created
     * @return The created
     */
    public Event createEvent(Event event) {
        return client //
                .target(server).path("api/event") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(event, APPLICATION_JSON), Event.class);
    }

    /**
     * Removes a participant from the database
     * @param participant the participant to remove
     * @param event the event
     * @return removed participant
     */
    public Participant removeParticipant(Participant participant, Event event) {
        var path = "api/event/" + event.getId() +
                "/participants/" + participant.getParticipantId();
        return client
                .target(server).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(new GenericType<>() {});
    }

    /**
     * Save Event REST API request.
     * @param event The event to be saved
     * @return The saved event
     */
    public Event saveEvent(Event event) {
        return client //
                .target(server).path("api/event/") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .put(Entity.entity(event, APPLICATION_JSON),
                        Event.class);
    }

    /**
     * Gets all events
     * @return List of Events
     */
    public List<Event> getMyEvents() {
        List<String> invCodes = userSettings.getEventCodes();
        String commaSeparatedInvCodes = "";
        if (!invCodes.isEmpty()) {
            commaSeparatedInvCodes = String.join(",", invCodes);
        }
        return client.target(server)
                .path("api/event/invite/" + commaSeparatedInvCodes)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});
    }

    /**
     * Request to get an updated version of the event.
     *
     * @param   event
     *          The event to get the updated version of.
     *
     * @return  The updated event.
     */
    public Event getUpdatedEvent(Event event) {
        return client
                .target(server).path("api/event/" + event.getId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Event.class);
    }

    /**
     * Join an event
     * @param code The event code
     * @return The event
     */
    public Event joinEvent(String code) {
        List<Event> events = client
                .target(server).path("api/event/invite/" + code)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});
        if (!events.isEmpty()) return events.get(0);
        return null;
    }

    /**
     * Get all events
     *
     * @param password the password for the admin
     * @return a list of all events in the database
     */
    public List<Event> getAllEvents(String password) {
        return client
                .register(HttpAuthenticationFeature.basic("admin", password))
                .target(server).path("api/admin/events")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});
    }

    /**
     * Delete an event
     *
     * @param event    the event to delete
     * @param password the password for the admin
     * @return the deleted event
     */
    public Event deleteEvent(Event event, String password) {
        return client
                .register(HttpAuthenticationFeature.basic("admin", password))
                .target(server).path("api/admin/events/" + event.getId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(new GenericType<>() {});
    }

    /**
     * Setter for the server URL
     * @param server the server url
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * Connects to the  websocket server
     * @param url the url for the websocket server
     * @return the StompSession
     */
    public StompSession connect(String url) {
        var client = new StandardWebSocketClient();
        var stomp = new WebSocketStompClient(client);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        MappingJackson2MessageConverter converter =
                new MappingJackson2MessageConverter();
        converter.setObjectMapper(mapper);
        stomp.setMessageConverter(converter);
        try {
            return stomp.connect(url, new StompSessionHandlerAdapter() {
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException();
    }


    /**
     * Method for registering for messages from a websocket destination.
     * @param dest The destination to listen to.
     * @param type The type of the message.
     * @param consumer The consumer to handle the message.
     * @param <T> The type of the message.
     */
    public <T> void registerForMessages(String dest, Class<T> type,
                                        Consumer<T> consumer) {
        session.subscribe(dest, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                consumer.accept((T) payload);
            }
        });
    }


    /**
     * Simplify debts of an event in a certain currency. Aka get the payment
     * instructions.
     *
     * @param   event
     *          The event of which the debts need to be simplified.
     * @param   currency
     *          The currency of the resulting payment instructions.
     *
     * @return  The payment instructions.
     */
    public Set<Debt> simplifyDebts(Event event, Currency currency) {
        return client.target(server)
                .path("api/event/" + event.getId() + "/simplify/" + currency)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});
    }

    /**
     * Gets the sum of all expenses of the event in a certain currency.
     *
     * @param   event
     *          Event which expenses need to be summed.
     * @param   currency
     *          The currency of the sum.
     *
     * @return  The sum of all expenses of the event in the specified currency.
     */
    public Money getSumOfAllExpenses(Event event, Currency currency) {
        return client.target(server)
                .path("api/event/" + event.getId() + "/sum/" + currency)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});
    }

    /**
     * Gets the balance of the participants in the specified event in the
     * specified currency.
     *
     * @param   event
     *          The event.
     * @param   currency
     *          The currency of the balance.
     *
     * @return  The balance of the participants in the event.
     */
    public Set<ParticipantValuePair> getBalanceOfParticipants(
            Event event, Currency currency) {
        return client.target(server)
                .path("api/event/" + event.getId() + "/balance/" + currency)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});
    }

    /**
     * Gets the shares of the participants in the specified event in the
     * specified currency.
     *
     * @param   event
     *          The event.
     * @param   currency
     *          The currency of the shares.
     *
     * @return  The shares of the participants in the event.
     */
    public Set<ParticipantValuePair> getSharesOfParticipants(
            Event event, Currency currency) {
        return client.target(server)
                .path("api/event/" + event.getId() + "/share/" + currency)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});
    }

    /**
     * Get the amount of a transaction in a certain currency.
     *
     * @param   money
     *          The amount to convert.
     * @param   currency
     *          The currency of the result.
     * @param   date
     *          The date of the exchange rate.
     *
     * @return  The resulting amount in the specified currency.
     */
    public Money convertMoney(Money money, Currency currency, LocalDate date) {
        return client.target(server)
                .path("api/event/convert/" + currency + "/" + date)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(money, APPLICATION_JSON),
                        Money.class);
    }


    private static final ExecutorService EXEC =
            Executors.newSingleThreadExecutor();

    private Future<?> future;

    /**
     * Stops the long polling.
     */
    public void stopLongPolling() {
        if (future != null) {
            future.cancel(true);
        }
        future = null;
    }

    /**
     * Registers for updates (adding of transactions)
     * In case of no content, the rest of the loop is skipped (continue)
     * Future should enable the stopping of the long-polling
     * @param consumer consumer of the transaction
     * @param event current event
     */
    public void registerForUpdates(Consumer<Transaction> consumer, Event event){
        if (future != null) {
            future.cancel(true);
        }

        future = EXEC.submit(() -> {
            while (!Thread.interrupted()) {
                try{
                    var res = client.target(server)
                            .path("api/event/" + event.getId()
                                    + "/transactions/updates")
                            .request(APPLICATION_JSON)
                            .accept(APPLICATION_JSON)
                            .get(Response.class);

                    if(res.getStatus() == 204){
                        continue;
                    };
                    var t = res.readEntity(Transaction.class);
                    consumer.accept(t);
                }
                catch (Exception e) {
                    System.err.println("An error occurred in the thread: " +
                            e.getMessage());
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Stops the executor thread
     */
    public void stop() {
        EXEC.shutdownNow();
    }

    /**
     * Remove transaction from db
     * @param transaction the transaction to remove
     * @param event event
     * @return the removed transaction
     */
    public Transaction removeTransaction(Transaction transaction, Event event) {
        var path = "api/event/" + event.getId() +
                "/transactions/" + transaction.getTransactionId();
        return client
                .target(server).path(path)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(new GenericType<>() {});
    }

    /**
     * saves an transaction
     * @param transaction transaction to be saved
     * @param event event
     * @return Transaction that is saved
     */
    public Transaction saveTransaction(Transaction transaction, Event event) {
        Transaction returned = client
                .target(server).path("/api/event/" +
                        event.getId() + "/transactions")
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(transaction, APPLICATION_JSON),
                        Transaction.class);
        return returned;
    }

    /**
     * Send a message to a websocket destination.
     * This is mainly going to be used to update action history
     * across clients.
     * @param dest The destination to send to.
     * @param o The object to send.
     */
    public void send(String dest, Object o) {
        session.send(dest, o);
    }

    /**
     * Undo delete transaction
     * @param transaction the transaction to undo delete
     * @param event event
     * @return the transaction that is undone
     */
    public Transaction undoDeleteTransaction(Transaction transaction,
                                             Event event) {
        return client
                .target(server).path("/api/event/" +
                        event.getId() + "/transactions/undoDelete")
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(transaction, APPLICATION_JSON),
                        Transaction.class);
    }
}