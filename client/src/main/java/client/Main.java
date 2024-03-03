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

import com.google.inject.Injector;

import client.scenes.LanguageTestCtrl;

import client.scenes.AddQuoteCtrl;
import client.scenes.MainCtrl;
import client.scenes.QuoteOverviewCtrl;
import javafx.application.Application;
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
    public void start(Stage primaryStage) throws IOException {
        // Change to `true` to load the quote application.
        if (false) {
            var overview = FXML.load(QuoteOverviewCtrl.class,
                    "client", "scenes", "QuoteOverview.fxml");
            var add = FXML.load(AddQuoteCtrl.class,
                    "client", "scenes", "AddQuote.fxml");

            var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
            mainCtrl.initialize(primaryStage, overview, add);

        // This is temporally here as a proof of concept.
        } else {
            var test = FXML.load(LanguageTestCtrl.class,
                    "client", "scenes", "LanguageTest.fxml");
            var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
            mainCtrl.initialize(primaryStage, test);
        }

    }
}