# OOPP TEAM 70 PROJECT

PLEASE run the server using the main class in the server package. If you use gradle bootRun it doesn't work
due to pathing issues.

When you create a new language template, it may be needed that you append the name with .properties for it to work. You also need to use a valid ISO-639-3 language code.

Add the template to the `client_config.properties` file after everything is translated. 
The `client_config.properties` is generated after the first run of the program.
To make a particial translation (and thus not have the program crash because of an  incomplete translation) remove all 
untranslated entries in the file and the program will replace missing translations with English.
