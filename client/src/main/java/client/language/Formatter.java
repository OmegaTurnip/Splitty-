package client.language;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {


    /**
     * A regex which checks if a {@code String} adheres to the specified format.
     * I.e. no unmatched double curly brackets and all parameter names are
     * alphanumeric.
     */
    private static final Pattern FORMAT_PATTERN =
            Pattern.compile(
                     "^(?>[^{}]|(?>\\{[^{])|(?>}[^}]))*(?>\\{\\{" +
                           "\\p{Alnum}+}}(?>[^{}]|(?>\\{[^{])|(?>}[^}]))*)*$");
    /**
     * A regex which checks if a {@code String} would be a valid parameter name.
     * I.e. all characters are alphanumeric and the string is not empty.
     */
    private static final Pattern PARAMETER_NAME_PATTERN =
            Pattern.compile("^\\p{Alnum}+$");

    /**
     * A regex which will split the string in such a way that whenever the
     * {@link Matcher#find() find()} method is called, the next parameter will
     * be returned by a call to {@link Matcher#group(int) group(1)}. It is
     * similar to {@link Formatter#FORMAT_PATTERN FORMAT_PATTERN} in a way, but
     * where {@code FORMAT_PATTERN} is used to validate a format string, this
     * pattern is used to retrieve all parameters from a format string in an
     * iterator type fashion.
     */
    private static final Pattern PARAMETER_PATTERN =
            Pattern.compile("\\G(?>[^{}]|(?>\\{[^{])|(?>}[^}]))*" +
                    "\\{\\{(\\p{Alnum}+)}}");


    /**
     * Formats a {@code String} with the given parameters. The parameters must
     * be alphanumeric. The parameters in the format string should be surrounded
     * by double curly brackets, e.g. {@code "The price is: {{price}}!"}.
     * Parameters can't be nested and any other occurrences of double curly
     * brackets are prohibited. Disallows parameters to be unassigned using a
     * fail fast method.
     *
     * @param   format
     *          The {@code String} that is to be formatted.
     * @param   parameters
     *          The parameters for the string formatting.
     *
     * @return  The resulting formatted {@code String}.
     */
    public static String format(String format, Map<String, String> parameters) {
        if (!getParameterOccurrences(format).keySet()
                .equals(parameters.keySet())) {
            throw new IllegalArgumentException(
                    "Not all parameters were bound!");
        }
        return formatUnsafe(format, parameters);
    }

    /**
     * Formats a {@code String} with the given parameters. The parameters must
     * be alphanumeric. The parameters in the format string should be surrounded
     * by double curly brackets, e.g. {@code "The price is: {{price}}!"}.
     * Parameters can't be nested and any other occurrences of double curly
     * brackets are prohibited. Allows parameters to be unassigned.
     *
     * @param   format
     *          The {@code String} that is to be formatted.
     * @param   parameters
     *          The parameters for the string formatting.
     *
     * @return  The resulting formatted {@code String}.
     */
    public static String formatUnsafe(String format,
                                      Map<String, String> parameters) {
        if (!isValidFormat(format))
            throw new IllegalArgumentException("Invalid format string!");

        if (parameters == null)
            throw new IllegalArgumentException("parameters is null!");

        String result = format;
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            if (!isValidParameter(parameter.getKey()))
                throw new IllegalArgumentException(
                        "Invalid parameter!"
                );

            result = result.replaceAll(toTextParameter(parameter.getKey()),
                    parameter.getValue());
        }

        return result;
    }

    /**
     * Checks if the format string is valid, i.e. it can be used by the
     * formatter.
     *
     * @param   format
     *          The format string to be checked.
     *
     * @return  Whether it is valid.
     */
    public static boolean isValidFormat(String format) {
        return format != null && FORMAT_PATTERN.matcher(format).matches();
    }

    /**
     * Checks if the parameter name is valid, i.e. if it is alphanumerical.
     *
     * @param   parameter
     *          The parameter to be checked.
     *
     * @return  Whether it is valid.
     */
    public static boolean isValidParameter(String parameter) {
        return parameter != null &&
                PARAMETER_NAME_PATTERN.matcher(parameter).matches();
    }

    private static String toTextParameter(String parameter) {
        return "\\{\\{" + parameter + "}}";
    }

    /**
     * Gets the amount of times each parameter is found in a format string as a
     * key value relation.
     *
     * @param   format
     *          The format string containing the parameters.
     *
     * @return  the amount of times each parameter is found as a
     *          key value relation.
     */
    public static HashMap<String, Integer> getParameterOccurrences(
            String format) {
        if (!isValidFormat(format))
            throw new IllegalArgumentException("Invalid format string!");

        HashMap<String, Integer> result = new HashMap<>();

        Matcher matcher = PARAMETER_PATTERN.matcher(format);
        while (matcher.find()) {
            result.put(matcher.group(1),
                    result.getOrDefault(matcher.group(1), 0) + 1);
        }
        return result;
    }
}
