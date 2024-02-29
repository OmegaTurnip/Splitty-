package client.language;

import client.utils.PropertiesFile;
import client.utils.ConfigFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class to help making translations easier. It is not part of the actual
 * program but just a tool for us, the developers, to make adding new
 * translations easier. (That's also why I didn't put effort in properly
 * formatting the code).
 */
public class TranslationCreator {

    private static final Scanner input = new Scanner(System.in);

    private static int reachedIdx = 0;
    private static boolean lastWasGoBack = false;

    private static PropertiesFile source;
    private static ConfigFile destination;

    private static final Pattern PARAMETER_PATTERN =
            Pattern.compile("\\G(?>[^{}]|\\{(?!\\{)|}(?!}))*\\{\\{(" +
                    "\\p{Alnum}+)}}");

    private static PropertiesFile getSourceFileFromUser()
            throws IOException {
        System.out.print("""

                (Absolute) filepath of existing translation (probably english)
                 >\s""");
        return new PropertiesFile(new File(input.nextLine()));
    }

    private static ConfigFile getDestinationFileFromUser()
            throws IOException {
        System.out.print("""

                (Absolute) filepath of language that needs translation
                 >\s""");
        File file = new File(input.nextLine());
        String comment = new Scanner(file).nextLine().substring(1);
        return new ConfigFile(file, comment);
    }

    /**
     * Run this to open the translator application.
     *
     * @param   args
     *          Ignored.
     *
     * @throws  IOException
     *          If an I/O error occurs reading or writing from/to the language
     *          files.
     */
    // I intentionally ignore checkstyle here as this file is not really part
    // of the program.
    @SuppressWarnings
            ({"checkstyle:MethodLength", "checkstyle:CyclomaticComplexity"})
    public static void main(String[] args) throws IOException {
        source = getSourceFileFromUser();

        System.out.println("\nMake sure the new language file already exists " +
                "and has a comment on the first line, e.g. '# The _ " +
                "translation of the program'");

        destination = getDestinationFileFromUser();

        System.out.println("\nUse the '/goback' command to edit the previous " +
                "entry. When inputted twice, it will go back 2 translations " +
                "etc.");


        Set<Object> sourceTranslations = source.getContent().keySet();
        Set<Object>  destTranslations = destination.getContent().keySet();

        sourceTranslations.removeAll(destTranslations);
        List<String> deltaSorted = sourceTranslations.stream()
                .map((o) -> (String) o)
                .sorted()
                .toList();

        for (int textIdx = 0; textIdx < deltaSorted.size(); textIdx++) {
            System.out.println();
            String textId = deltaSorted.get(textIdx);
            String result = getTranslation(textId);
            if (result.equals("/goback")) {
                lastWasGoBack = true;
                if (textIdx == 0) {
                    System.out.println("\u001b[31mCannot go back further!" +
                            "\u001b[0m");
                    textIdx -= 1;
                } else {
                    textIdx -= 2;
                }
            } else if (!Formatter.isValidFormat(result)) {
                System.out.println("\u001b[31mInvalid format!\u001b[0m");
                textIdx -= 1;
            } else if (!Formatter.getParameterOccurrences(
                    source.getAttribute(textId)).equals(
                            Formatter.getParameterOccurrences(result))) {
                System.out.println("\u001b[31mParameters don't match!" +
                        "\u001b[0m");
                textIdx -= 1;
            } else {
                System.out.println("Result: \"" + formatString(result) + "\"");
                destination.setAttribute(textId, result);
                reachedIdx = Math.max(textIdx, reachedIdx);
                if (lastWasGoBack) {
                    lastWasGoBack = false;
                    textIdx = reachedIdx;
                }
            }
        }
        System.out.println("\nDone!\nPress enter to leave...");
        input.nextLine();
    }

    private static String getTranslation(String textId) {
        String sourceTranslation = source.getAttribute(textId);
        System.out.println("Original text: (" + textId + ")");
        System.out.println(formatString(sourceTranslation));
        System.out.println("Translation:");
        return input.nextLine();
    }

    private static String formatString(String format) {
        StringBuilder result = new StringBuilder("\u001b[37m");
        Matcher matcher = PARAMETER_PATTERN.matcher(format);
        int endLastGroup = 0;
        while (matcher.find()) {
            endLastGroup = matcher.end(1);
            result.append(format.substring(matcher.start(),
                            matcher.start(1)))
                    .append("\u001b[33m")
                    .append(format.substring(matcher.start(1),
                            matcher.end(1)))
                    .append("\u001b[37m");
        }
        result.append(format.substring(endLastGroup));
        return result + "\u001b[0m";
    }
}