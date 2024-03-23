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

import commons.Event;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private Stage primaryStage;

    private EventOverviewCtrl overviewCtrl;
    private Scene overview;

    private AddParticipantCtrl addParticipantCtrl;
    private AddExpenseCtrl addExpenseCtrl;
    private Scene add;
    private Scene addExpense;
    private StartUpCtrl startUpCtrl;
    private Scene startUp;

    /**
     * @param primaryStage the window.
     * @param overview the fx for the event overview page.
     * @param add the fx for the add participant page.
     * @param startUp The fx for the start-up page.
     */
    public void initialize(
            Stage primaryStage, Pair<EventOverviewCtrl, Parent> overview,
            Pair<AddParticipantCtrl, Parent> add,
            Pair<StartUpCtrl, Parent> startUp, Pair<AddExpenseCtrl, Parent> addExpense) {
        this.primaryStage = primaryStage;
        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());
        this.startUpCtrl = startUp.getKey();
        this.startUp = new Scene(startUp.getValue());
        this.startUp.getStylesheets().add(getClass()
                .getResource("style.css").toExternalForm());

        this.addParticipantCtrl = add.getKey();
        this.add = new Scene(add.getValue());
        this.addExpenseCtrl = addExpense.getKey();
        this.addExpense = new Scene(addExpense.getValue());


        showStartUp();
        primaryStage.show();
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
        this.overviewCtrl.refreshText();
        primaryStage.setTitle("Event Overview");
        primaryStage.setScene(overview);
    }

    /**
     * Show the start-up page.
     */
    public void showStartUp() {
        primaryStage.setTitle("Splitty!");
        primaryStage.setScene(startUp);
    }

    /**
     * go to the add quote page (by changing the content of the window).
     * @param event the event the participant is a part of.
     */
    public void showAddParticipant(Event event) {
        addParticipantCtrl.setEvent(event);
        this.addParticipantCtrl.refreshText();
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

    public void showAddExpense(Event event) {
        addExpenseCtrl.setEvent(event);
        addExpenseCtrl.refreshText();
        primaryStage.setTitle("Splitty!");
        primaryStage.setScene(addExpense);
    }
}