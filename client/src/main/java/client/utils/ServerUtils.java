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

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


import commons.Event;
import commons.Participant;
import commons.Transaction;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;

import commons.Quote;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;


public class ServerUtils {

    private final UserConfig userSettings;

    private String server;

    private Client client;

    /**
     * Getter.
     *
     * @return Get User Settings.
     */
    public UserConfig getUserSettings() {
        return userSettings;
    }

    /**
     * Getter.
     *
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
    }

    /**
     * Injectable constructor
     *
     * @param userSettings Inject the userSettings.
     */
    public ServerUtils(UserConfig userSettings) {
        this.userSettings = userSettings;
        this.server = userSettings.getServerUrl();
    }

    /**
     * Injectable constructor
     *
     * @param userSettings Inject the userSettings.
     * @param server       Inject custom URL.
     * @param client       Inject client.
     */
    public ServerUtils(UserConfig userSettings, String server, Client client) {
        this.userSettings = userSettings;
        this.server = server;
        this.client = client;
    }

    /**
     * @throws IOException        no description was provided in the template.
     * @throws URISyntaxException no description was provided in the template.
     */
    public void getQuotesTheHardWay() throws IOException, URISyntaxException {
        var url = new URI(server + "api/quotes").toURL();
        var is = url.openConnection().getInputStream();
        var br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    /**
     * @return no description was provided in the template.
     */
    public List<Quote> getQuotes() {
        return client //
                .target(server).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Quote>>() {
                });
    }

    /**
     * @param quote no description was provided in the template.
     * @return no description was provided in the template.
     */
    public Quote addQuote(Quote quote) {
        return client //
                .target(server).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
    }

    /**
     * Create/save Event REST API request.
     *
     * @param event The event to be created/saved.
     * @return The created/saved event.
     */
    public Event saveEvent(Event event) {
        return client //
                .target(server).path("api/event") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(event, APPLICATION_JSON), Event.class);
    }

    /**
     * Gets all events
     *
     * @return List of Events
     */
    public List<Event> getMyEvents() {
        List<String> invCodes = userSettings.getEventCodes();
        String commaSeparatedInvCodes = String.join(",", invCodes);
        return client.target(server)
                .path("api/event/invite/" + commaSeparatedInvCodes)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});
    }

    /**
     * Join an event
     *
     * @param code The event code
     * @return The event
     */
    public Event joinEvent(String code) {
        return client
                .target(server).path("api/event/invite/" + code)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Event.class);
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
//        TODO make sure events that don't exist are
//         deleted from the user config for all users
    }

//    /**
//     * Save events
//     * @param events The events to save
//     * @return The saved events
//     */
//    public List<Event> saveEvents(List<Event> events) {
//        return client //
//                .target(server).path("api/event") //
//                .request(APPLICATION_JSON) //
//                .accept(APPLICATION_JSON) //
//                .put(Entity.entity(events, APPLICATION_JSON),
//                        new GenericType<List<Event>>() {});
//    }

    /**
     * Gets participants for event
     *
     * @param event the Event to get participants from
     * @return a list of Participant
     */

    public List<Participant> getParticipantsOfEvent(Event event) {
        return client.target(server)
                .path("api/event/" + event.getId() + "/participants")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});
    }

    /**
     * Setter for the server URL
     * @param server the server url
     */
    public void setServer(String server) {
        this.server = server;
    }
        /**
         * Creates a participant
         * @param participant participant to create
         * @return created participant
         */
    public Participant createParticipant(Participant participant){
        return client
                .target(server).path("/api/event/" + participant.getEvent()
                        .getId() + "/participants")
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(participant, APPLICATION_JSON),
                        Participant.class);
    }

    /**
     * Get all transactions of the event
     * @param event Event of which needs to be returned
     * @return list of transactions
     */

    public List<Transaction> getTransactionsOfEvent(Event event){
        return client.target(server)
                .path("api/event/" + event.getId() + "/transactions")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Transaction>>() {});
    }
    /**
     * Edit transaction
     * This still needs to be converted to long-polling
     * @param event Event of which the transaction needs to be edited
     * @param transaction The transaction to edit
     * @return The edited transaction
     */
    public Transaction editTransaction(Event event, Transaction transaction) {
        return client.target(server)
                .path("api/event/" + event.getId() + "/transactions")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(transaction, APPLICATION_JSON),
                        Transaction.class);
    }
    private static final ExecutorService EXEC =
            Executors.newSingleThreadExecutor();

    /**
     * Registers for updates (adding of transactions)
     * In case of no content, the rest of the loop is skipped (continue)
     * Still needs to be fixed: now only 1 EXEC at the time
     * Needs to be solved by creating a set of listeners,
     * add all consumers to the set, iterate over all consumers
     * @param consumer consumer of the transaction
     * @param event current event
     */
    public void registerForUpdates(Consumer<Transaction> consumer, Event event){

        EXEC.submit(() -> {
            while (!Thread.interrupted()) {
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
        });

    }

    /**
     * Stops the executor thread
     */
    public void stop() {
        EXEC.shutdownNow();
    }

}