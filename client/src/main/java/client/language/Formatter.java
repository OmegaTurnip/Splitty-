package client.language;

import java.util.Map;
import java.util.regex.Pattern;

public class Formatter {

    private static final Pattern FORMAT_PATTERN =
            Pattern.compile(
                     "^(?>[^{}]|(?>\\{[^{])|(?>}[^}]))*(?>\\{\\{" +
                           "[A-Za-z0-9]+}}(?>[^{}]|(?>\\{[^{])|(?>}[^}]))*)*$");
    private static final Pattern PARAMETER_PATTERN =
            Pattern.compile("^[A-Za-z0-9]+$");


    /**
     * Formats a {@code String} with the given parameters. The parameters must
     * be alphanumeric. The parameters in the format string should be surrounded
     * by double curly brackets, e.g. {@code "The price is: {{price}}!"}.
     * Parameters can't be nested and any other occurrences of double curly
     * brackets are prohibited.
     *
     * @param   format
     *          The {@code String} that is to be formatted.
     * @param   parameters
     *          The parameters for the string formatting.
     *
     * @return  The resulting formatted {@code String}.
     */
    public static String format(String format, Map<String, String> parameters) {
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

            result = format.replaceAll("\\{\\{" + parameter.getKey() + "}}",
                    parameter.getValue());
        }

        return result;
    }
    
    private static boolean isValidFormat(String format) {
        return format != null && FORMAT_PATTERN.matcher(format).matches();
    }

    private static boolean isValidParameter(String parameter) {
        return parameter != null &&
                PARAMETER_PATTERN.matcher(parameter).matches();
    }

}
