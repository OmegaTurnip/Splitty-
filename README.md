# OOPP TEAM 70 PROJECT

PLEASE run the server using the main SpringBoot class in the server package. If you use gradle bootRun it may not work
due to pathing issues. 

Also you might need to run the client from intellij by running the main method. You might be able to run it some other way, but this works for sure.

When you create a new language template, it may be needed that you append the name with .properties for it to work. You also need to use a valid ISO-639-3 language code.

Add the template to the `client_config.properties` file after everything is translated. 
The `client_config.properties` is generated after the first run of the program.
To make a particial translation (and thus not have the program crash because of an incomplete translation) remove all 
untranslated entries in the file and the program will replace missing translations with English.

The undo/redo buttons for editing and deleting expenses in the event overview page is Ctrl+Z and Ctrl+Y respectively.

The websockets can be seen implemented on the server side under the API folder, and a simple message converter is used in the classes to reroute API requests. On the client side, it is used on the Admin Page Ctrl, EventOverviewCtrl, StartUpCtrl etc.

Long Polling is done on the EventOverviewCtrl and Debt overview page for adding expenses. registerForMessages is the method we use to communicate with websockets. registerForUpdates is for long-polling. Press ctrl+f on these methods in ServerUtils in a code editor and you can see all the usages of them.

Whenever you update or delete a participant, it's going to clear the action history of all users. This is to avoid null-pointer bugs, but also to prevent the reverting of changes to participant when you redo/undo expenses. The alternative solution is to add undo/redo functionality to participants but this strays from the rubric.

In StartUpCtrl, you can right-click or press DELETE on an event to try and delete it.

In AdminCtrl, double-click on the eventName column cell to go to that Event page.

In add/edit page, you can press enter on the combo check box to add participants to the transaction, and then use
space bar to check and uncheck the selected participant whilst also using arrow keys to navigate.
You can press enter on the currency combo box to change currency.
You can press up/down while hovered over the payer box to choose payer.

Extentions we tried to implement:
    - Live language switch
    - Detailed expenses
    - Foreign currency
    - Open debts

HCI features we tried to implement:
    - Colour contrast
    - Keyboard shortcuts
    - Multi-modal visualisation
    - Logical navigation
    - Keyboard navigation
    - Supporting Undo-actions
    - Error messages
    - Informative feedback
    - Confirmation for key actions

