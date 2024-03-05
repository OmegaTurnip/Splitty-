# General explanation
###### From the merge request
Implements the requirements made by [this issue](https://gitlab.ewi.tudelft.nl/cse1105/2023-2024/teams/oopp-team-70/-/issues/14 "Add (custom) language support"). It contains the means to load and dynamically update the texts in the application from a non hardcoded external file. It also adds a small application to make making translations easier.

### Language class and fxml controllers

The new `Language` class will contain all pieces of text needed in the UI. This way, by switching out the `currentLanguage` attribute in the `Translator` class, all newly gotten pieces of text will be in the newly selected language. To update existing pieces of text, the `refreshText()` method, specified in the `TextPage` interface, should be called on the corresponding fxml controller. This function should ideally also be used for the initial page load.

The `Text` class stores all text ids used to select a specific piece of text. When a piece of text is added to one of the language files (preferably initially the English one) a corresponding attribute should be added to the `Text` class.

So, for example, if we have a page solely consisting of a retry button (stored in the `retry` attribute), in the corresponding controller this function should be implemented:

```java
@Override
public void refreshText() {
    retry.setText(
        Translator.getTranslation(Text.PageName.RetryButton)
    );
}
```
_Note the `@Override` as this controller should implement the `TextPage` interface_

Here `Text.PageName.RetryButton` should probably equal `"pagename.retrybutton"` and the English language file should contain the entry `pagename.retrybutton=retry`.

If a translation is not found in the current language, `Translator.getTranslation()` will fall back on the English translation surrounded by square brackets `"[retry]"` or, if no English translation exists (or English is already the current language), it will return `"<TEXT DOESN'T EXIST>"`.

### Formatting

Another feature implemented in this merge request is text formatting, which is closely related to translation of text. Take for example the case where we want to display to the user a message like "David paid €54 for Drinks". Translating the general pattern (_payer_ paid _price_ for _expense_) can be quite difficult as word ordering differs in different languages.

That is why the `Formatter` class was made so now pieces of text that need dynamic information can be properly translated. The aforementioned message would be stored as `"{{payer}} paid {{price}} for {{transaction}}"`. Calling the `Formatter.format()` function on this string in combination with a hashmap containing the parameters and their replacement values will produce the final correct string.

Code example:
```java
HashMap<String, String> parameters = new HashMap<>();
parameters.put("payer", "David");
parameters.put("price", "€54");
parameters.put("transaction", "Drinks");
Formatter.format("{{payer}} paid {{price}} for {{transaction}}", parameters); // this will equal "David paid €54 for Drinks"
```

The translation of this string to Dutch should be `"{{payer}} betaalde {{price}} voor {{transaction}}"`. A actual code example would look like this:

```java
// assume `lastExpense` (fxml object) and `lastExpenseParameters` (hashmap) are valid attributes.
@Override
public void refreshText() {
    lastExpense.setText(
        Formatter.format(
            Translator.getTranslation(Text.ExpenseOverview.LastExpense), 
            lastExpenseParameters
        )
    );
}
```

### Adding translations
Lastly, the code also contains a simple console application (`TranslationCreator`) which can be used to translate a new language. You first input the source language and the language that needs translations. Then it will resolve all entries present in the source language and not in the 'destination' language and lets you enter translations for those. It will also check if all parameters are present and valid in the translation.


# Tutorial on adding a new piece of text
When you want to add a new piece of text, think a little about where its used/will be reused.
Based on this you should group pieces of text together.

### Adding it

Let's add a new piece of text that is used to ask the user for a event code on the login screen.

It should probably be part of the login screen group and maybe even of the subgroup event selection, depending on how many pieces of text are present on the login screen.

So, in (preferably) the english language file (`/includedLanguages/eng.properties`) add the entry:
```properties
loginscreen.eventselection.eventcodeprompt=Please input the event invite code!
```

Now, to properly select this text id, go to the `Text` class and add an innerclass for each (sub)group.
In our case it would look something like this:

```java
public class Text {

    // ...

    public static final class LoginScreen {

        private static final String level = "loginscreen.";

        public static final class EventSelection {

            private static final String level = LoginScreen.level + "eventselection.";

            public static final String eventCodePrompt = level + "eventcodeprompt";

            // ...
            
        }
        
        // ...
    }
}
```
The `level` attribute exist to save some typing and makes a mistake in the text id less likely.

Now, to select the piece of text that we added, we simply use 
```java
Text.LoginScreen.EventSelection.eventCodePrompt
```
Example usages are given in the previous chapter.

### Translating it
Use the `TranslationCreator` for this purpose.
Following the instructions of the program should be sufficient to make a translation.
Don't forget that you should _not_ translate parameter names.

