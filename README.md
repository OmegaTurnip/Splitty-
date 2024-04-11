# OOPP TEAM 70 PROJECT

PLEASE run the server using the main SpringBoot class in the server package. If you use gradle bootRun it may not work
due to pathing issues.

When you create a new language template, it may be needed that you append the name with .properties for it to work. You also need to use a valid ISO-639-3 language code.

Add the template to the `client_config.properties` file after everything is translated. 
The `client_config.properties` is generated after the first run of the program.
To make a particial translation (and thus not have the program crash because of an incomplete translation) remove all 
untranslated entries in the file and the program will replace missing translations with English.

The undo/redo buttons for editing and deleting expenses in the event overview page is Ctrl+Z and Ctrl+Y respectively.

The websockets can be seen implemented on the server side under the API folder, and a simple message converter is used in the classes to reroute API requests. On the client side, it is used on the Admin Page Ctrl, EventOverviewCtrl, StartUpCtrl etc.

Long Polling is done on the EventOverviewCtrl for adding/deleting expenses. registerForMessages is the method we use to communicate with websockets. registerForUpdates is for long-polling. Press ctrl+f on these methods in ServerUtils in a code editor and you can see all the usages of them.


