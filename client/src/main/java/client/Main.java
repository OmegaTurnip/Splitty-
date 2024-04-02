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
package client;

import static com.google.inject.Guice.createInjector;
import java.io.IOException;
import java.net.URISyntaxException;

import client.language.Translator;
import client.scenes.*;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    /**
     * @param   args
     *          Ignored.
     *
     * @throws  URISyntaxException
     *          No description was provided in the template.
     * @throws  IOException
     *          If an I/O error occurs reading from the language files.
     */
    public static void main(String[] args)
            throws URISyntaxException, IOException {
        launch();
    }

    /**
     * @param primaryStage no description was provided in the template.
     * @throws IOException no description was provided in the template.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            injectScenesAndUtils(primaryStage);
        } catch (RuntimeException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            try {
                alert.setTitle(Translator
                        .getTranslation(client.language
                                .Text.Alert.serverDownTitle));
                alert.setHeaderText(null);
                alert.setContentText(Translator
                        .getTranslation(client.language
                                .Text.Alert.serverDownContent));
            } catch (NullPointerException nullPointerException) {
                alert.setTitle("Server down");
                alert.setContentText("The server is down," +
                        " please try again later.");
            }
            alert.showAndWait();
        }
    }

    private static void injectScenesAndUtils(Stage primaryStage) {
        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.setPrimaryStage(primaryStage);
        var startUp = FXML.load(StartUpCtrl.class,
                "client", "scenes", "StartUp.fxml");
        var overview = FXML.load(EventOverviewCtrl.class,
                "client", "scenes", "EventOverview.fxml");
        var add = FXML.load(AddParticipantCtrl.class, "client", "scenes",
                "AddParticipant.fxml");
        var addExpense = FXML.load(AddExpenseCtrl.class, "client", "scenes",
                "AddExpense.fxml");
        var admin = FXML.load(AdminCtrl.class, "client", "scenes",
                "Admin.fxml");
        var editName = FXML.load(EditEventNameCtrl.class, "client", "scenes",
                "EditEventName.fxml");
        var server = INJECTOR.getInstance(ServerUtils.class);
        mainCtrl.initialize(overview, add,
                startUp, addExpense, editName, admin);
        mainCtrl.setUtils(server, primaryStage);
        primaryStage.setOnCloseRequest(e -> overview.getKey().stop());
    }
}