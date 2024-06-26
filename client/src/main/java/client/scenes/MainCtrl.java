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
package client.scenes;

import client.history.ActionHistory;
import client.language.Text;
import client.language.Translator;
import client.utils.ServerUtils;
import commons.Event;
import commons.Participant;
import commons.Transaction;
import jakarta.ws.rs.NotAuthorizedException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.time.LocalDate;

public class MainCtrl {

    private Stage primaryStage;

    private EventOverviewCtrl overviewCtrl;
    private Scene overview;

    private EditEventNameCtrl editEventNameCtrl;
    private Scene editName;
    private AddParticipantCtrl addParticipantCtrl;
    private Scene add;
    private StartUpCtrl startUpCtrl;
    private Scene startUp;

    private AddExpenseCtrl addExpenseCtrl;
    private Scene addExpense;
    private Scene admin;
    private AdminCtrl adminCtrl;

    private ServerUtils server;

    private AlertWrapper alertWrapper;
    private Scene debtPage;
    private DebtPageCtrl debtPageCtrl;

    private boolean filterAdded;

    /**
     * @param overview the fx for the event overview page.
     * @param add the fx for the add participant page.
     * @param startUp The fx for the start-up page.
     * @param addExpense The fx for the add expense page.
     * @param adminPage The fx for the admin page.
     * @param editName The fx for the edit name page.
     * @param debtPage The fx for the open debts page.
     */
    @SuppressWarnings({"checkstyle:ParameterNumber", "checkstyle:MethodLength"})
    public void initialize(
            Pair<EventOverviewCtrl, Parent> overview,
            Pair<AddParticipantCtrl, Parent> add,
            Pair<StartUpCtrl, Parent> startUp,
            Pair<AddExpenseCtrl, Parent> addExpense,
            Pair<EditEventNameCtrl, Parent> editName,
            Pair<AdminCtrl, Parent> adminPage,
            Pair<DebtPageCtrl, Parent> debtPage) {
        
        filterAdded = false;
        this.startUpCtrl = startUp.getKey();
        this.startUp = new Scene(startUp.getValue());
        this.startUp.getStylesheets().add(getClass()
                .getResource("style.css").toExternalForm());

        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());
        this.overview.getStylesheets().add(getClass()
                .getResource("style.css").toExternalForm());

        this.addParticipantCtrl = add.getKey();
        this.add = new Scene(add.getValue());
        this.add.getStylesheets().add(getClass()
                .getResource("style.css").toExternalForm());

        this.addExpenseCtrl = addExpense.getKey();
        this.addExpense = new Scene(addExpense.getValue());
        this.addExpense.getStylesheets().add(getClass()
                .getResource("style.css").toExternalForm());

        this.editEventNameCtrl = editName.getKey();
        this.editName = new Scene(editName.getValue());
        this.editName.getStylesheets().add(getClass()
                .getResource("style.css").toExternalForm());

        this.adminCtrl = adminPage.getKey();
        this.admin = new Scene(adminPage.getValue());
        this.admin.getStylesheets().add(getClass()
                .getResource("style.css").toExternalForm());

        this.debtPageCtrl = debtPage.getKey();
        this.debtPage = new Scene(debtPage.getValue());
        this.debtPage.getStylesheets().add(getClass()
                .getResource("style.css").toExternalForm());

        this.alertWrapper = new AlertWrapper();

        showStartUp();
        primaryStage.show();
    }

    /**
     * Set the server utils.
     * @param server the server utils.
     * @param primaryStage the primary stage.
     */
    public void setUtils(ServerUtils server, Stage primaryStage) {
        this.server = server;
    }

    /**
     * Temp test thingy
     *
     * @param primaryStage  .
     * @param lang .
     */
    public void initialize(
            Stage primaryStage, Pair<EventOverviewCtrl, Parent> lang) {
        this.primaryStage = primaryStage;

        primaryStage.setTitle("Language selector test");
        primaryStage.setScene(new Scene(lang.getValue()));

        primaryStage.show();
    }

    /**
     * Sets the alertWrapper
     * @param alertWrapper sets the alertWrapper
     */
    public void setAlertWrapper(AlertWrapper alertWrapper) {
        this.alertWrapper = alertWrapper;
    }

    /**
     * Go to the Event Overview page.
     * @param event the event to show.
     */
    public void showEventOverview(Event event) {
        overviewCtrl.setEvent(server.getUpdatedEvent(event));
        primaryStage.setTitle("Splitty!");
        primaryStage.setScene(overview);
        if (!filterAdded) {
            overview.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    overviewCtrl.getActionHistory().clear();
                    showStartUp();
                    e.consume();
                }
            });
            filterAdded = true;
        }

        overview.setOnKeyPressed(e -> {
            if (e.isControlDown() && e.getCode() == KeyCode.Z) {
                overviewCtrl.undo();
                e.consume();
            } else if (e.isControlDown() && e.getCode() == KeyCode.Y) {
                overviewCtrl.redo();
                e.consume();
            }
        });
        overviewCtrl.refresh();
    }

    /**
     * Show the start-up page.
     */
    public void showStartUp() {
        primaryStage.setTitle("Splitty!");
        primaryStage.setScene(startUp);
        startUpCtrl.refresh();
    }

    /**
     * Go to the edit participant page.
     * @param event the event the participant is a part of.
     */
    public void showAddParticipant(Event event) {
        addParticipantCtrl.setParticipant(null);
        showParticipant(event);
    }


    private void showParticipant(Event event) {
        addParticipantCtrl.setEvent(event);
        addParticipantCtrl.refresh();
        primaryStage.setTitle("Splitty!");
        primaryStage.setScene(add);
        add.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                showEventOverview(event);
                e.consume();
            }
        });
    }

    /**
     * Go to the edit participant page.
     * @param event the event the participant is a part of.
     * @param participant the participant to edit.
     * @param actionHistory the action history.
     */
    public void showEditParticipant(Event event, Participant participant,
                                    ActionHistory actionHistory) {
        addParticipantCtrl.setActionHistory(actionHistory);
        addParticipantCtrl.setParticipant(participant);
        showParticipant(event);
    }

    /**
     * Sets the scene to the page where the name of the event can be edited
     * @param event event for which the name needs to be changed
     */
    public void showEditName(Event event) {
        editEventNameCtrl.setEvent(event);
        this.editEventNameCtrl.refreshText();
        primaryStage.setTitle("Event Overview: Editing name");
        primaryStage.setScene(editName);
    }


    /**
     * Get the primary stage.
     * @return the primary stage.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Set the primary stage.
     * @param primaryStage the primary stage.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Get the overview scene.
     * @return the overview scene.
     */
    public Scene getOverviewScene() {
        return overview;
    }


    /**
     * go to the add expense page (by changing the content of the window).
     * @param event the event the expense is a part of.
     */
    public void showAddExpense(Event event) {
        addExpenseCtrl.setEvent(event);
        addExpenseCtrl.setExpenseToOverwrite(null);
        addExpenseCtrl.setStartUpDate(LocalDate.now());
        addExpenseCtrl.refresh();
        primaryStage.setScene(addExpense);
    }

    /**
     * Go to the edit expense page.
     * @param event the event the participant is a part of.
     * @param transaction the transaction to edit
     * @param actionHistory the action history
     */
    public void showEditExpense(Event event, Transaction transaction,
                                ActionHistory actionHistory) {
        addExpenseCtrl.setEvent(event);
        addExpenseCtrl.setExpenseToOverwrite(transaction);
        addExpenseCtrl.setActionHistory(actionHistory);
        addExpenseCtrl.setEventOverviewCtrl(overviewCtrl);
        addExpenseCtrl.setStartUpDate(LocalDate.now());
        addExpenseCtrl.refresh();
        primaryStage.setScene(addExpense);
    }

    /**
     * Go to the admin page (by changing the contents of the window)
     * @param password the admin password
     */
    public void showAdminPage(String password) {
        try {
            adminCtrl.setPassword(password);
            adminCtrl.setEvents(server.getAllEvents(password));
            adminCtrl.refresh();
            primaryStage.setScene(admin);
            admin.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    showStartUp();
                    e.consume();
                }
            });
        } catch (NotAuthorizedException e) {
            alertWrapper.showAlert(Alert.AlertType.INFORMATION,
                    Translator.getTranslation(Text.Admin
                            .Alert.unauthorisedTitle),
                    Translator.getTranslation(Text.Admin
                            .Alert.unauthorisedContent));
        }
    }

    /**
     * CHanges the stage to show debtPage
     * @param event the event
     */
    public void showOpenDebts(Event event) {
        debtPageCtrl.setEvent(event);
        debtPageCtrl.refreshText();
        debtPageCtrl.setStartUpDate(LocalDate.now());
        primaryStage.setScene(debtPage);
    }

}