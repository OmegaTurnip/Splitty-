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

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private Stage primaryStage;

    private EventOverviewCtrl overviewCtrl;
    private Scene overview;

    private AddParticipantCtrl addParticipantCtrl;
    private Scene add;
    /**
     * @param primaryStage the window.
     * @param overview the fx for the start-up page.
     * @param add the fx for the add quote page.
     */
    public void initialize(
            Stage primaryStage, Pair<EventOverviewCtrl, Parent> overview,
            Pair<AddParticipantCtrl, Parent> add) {
        this.primaryStage = primaryStage;
        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.addParticipantCtrl = add.getKey();
        this.add = new Scene(add.getValue());

        showOverview();
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
     */
    public void showOverview() {
        primaryStage.setTitle("Event Overview");
        primaryStage.setScene(overview);
    }

    /**
     * go to the add quote page (by changing the content of the window).
     */
    public void showAdd() {
        primaryStage.setTitle("Event Overview: Adding participant");
        primaryStage.setScene(add);
    }
}