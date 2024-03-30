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

import client.utils.ServerUtils;
import commons.Event;
import jakarta.ws.rs.NotAuthorizedException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private Stage primaryStage;

    private EventOverviewCtrl overviewCtrl;
    private Scene overview;

    private AddParticipantCtrl addParticipantCtrl;
    private Scene add;
    private StartUpCtrl startUpCtrl;
    private Scene startUp;

    private AddExpenseCtrl addExpenseCtrl;
    private Scene addExpense;
    private Scene admin;
    private AdminCtrl adminCtrl;

    private ServerUtils server;

    /**
     * @param overview the fx for the event overview page.
     * @param add the fx for the add participant page.
     * @param startUp The fx for the start-up page.
     * @param addExpense The fx for the add expense page.
     * @param adminPage The fx for the admin page.
     */
    public void initialize(
            Pair<EventOverviewCtrl, Parent> overview,
            Pair<AddParticipantCtrl, Parent> add,
            Pair<StartUpCtrl, Parent> startUp,
            Pair<AddExpenseCtrl, Parent> addExpense,
            Pair<AdminCtrl, Parent> adminPage) {

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

        this.addExpenseCtrl = addExpense.getKey();
        this.addExpense = new Scene(addExpense.getValue());

        this.adminCtrl = adminPage.getKey();
        this.admin = new Scene(adminPage.getValue());


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
     * go to the start-up page (by changing the content of the window).
     * @param event the event to show.
     */
    public void showEventOverview(Event event) {
        overviewCtrl.setEvent(event);
        primaryStage.setTitle("Event Overview");
        primaryStage.setScene(overview);
        overview.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                showStartUp();
                e.consume();
            }
        });
//        event.addParticipant("test"); // test line, remove later
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
     * go to the add quote page (by changing the content of the window).
     * @param event the event the participant is a part of.
     */
    public void showAddParticipant(Event event) {
        addParticipantCtrl.setEvent(event);
        addParticipantCtrl.refresh();
        primaryStage.setTitle("Event Overview: Adding participant");
        primaryStage.setScene(add);
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
        addExpenseCtrl.refresh();
        primaryStage.setTitle("Splitty!");
        primaryStage.setScene(addExpense);
    }

    /**
     * Go to the admin page (by changing the contents of the window)
     * @param password the admin password
     */
    public void showAdminPage(String password) {
        try {
            adminCtrl.setPassword(password);
            adminCtrl.refresh();
            adminCtrl.setEvents(server.getAllEvents(password));
            primaryStage.setScene(admin);
            admin.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    showStartUp();
                    e.consume();
                }
            });
        } catch (NotAuthorizedException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Unauthorized");
            alert.setHeaderText(null);
            alert.setContentText("You entered the wrong admin password.");
            alert.showAndWait();
        }
    }

}