package com.adguard.commons.lang;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

/**
 * Represents a wildcard
 */
public class Wildcard {

    private final int regexpOptions;
    private final Pattern regexp;
    private final String shortcut;

    /**
     * Initializes a wildcard with the given pattern.
     *
     * @param pattern Pattern
     */
    public Wildcard(String pattern) {
        this(pattern, Pattern.CASE_INSENSITIVE);
    }

    /**
     * Initializes a wildcard with the given search pattern and options.
     *
     * @param pattern       Wildcard pattern
     * @param regexpOptions Regexp options
     */
    public Wildcard(String pattern, int regexpOptions) {
        this.regexpOptions = regexpOptions;
        regexp = Pattern.compile(wildcardToRegex(pattern), regexpOptions);
        shortcut = extractShortcut(pattern);
    }

    /**
     * Gets wildcard shortcut
     *
     * @return Wildcard shortcut
     */
    String getShortcut() {
        return shortcut;
    }

    /**
     * Returns "true" if input text is matching wildcard.
     * This method first checking shortcut -- if shortcut exists in input string -- than it checks regexp.
     *
     * @param input Input string
     * @return true if input string matches wildcard
     */
    public boolean matches(String input) {
        if (StringUtils.isEmpty(input)) {
            return false;
        }

        boolean matchCase = ((regexpOptions & Pattern.CASE_INSENSITIVE) == Pattern.CASE_INSENSITIVE);

        if (matchCase && !StringUtils.contains(input, shortcut)) {
            return false;
        }

        if (!matchCase && !StringUtils.containsIgnoreCase(input, shortcut)) {
            return false;
        }

        return regexp.matcher(input).matches();
    }

    /**
     * Converts wildcard to regular expression
     *
     * @param pattern The wildcard pattern to convert
     * @return A regex equivalent of the given wildcard
     */
    private static String wildcardToRegex(String pattern) {
        return "^" + StringUtils.replaceEach(escapeRegexp(pattern), new String[]{"\\*", "\\?"}, new String[]{".*", "."}) + "$";
    }

    /**
     * Escapes regexp special characters: \, *, +, ?, |, {, [, (,), ^, $,., #, and white space
     *
     * @param pattern Pattern to escape
     * @return Escaped pattern
     */
    private static String escapeRegexp(String pattern) {
        String[] specialCharacters = new String[]{"\\", "*", "+", "?", "|", "{", "[", "(", ")", "^", "$", ".", "#"};
        String[] escapedCharacters = new String[]{"\\\\", "\\*", "\\+", "\\?", "\\|", "\\{", "\\[", "\\(", "\\)", "\\^", "\\$", "\\.", "\\#"};
        return StringUtils.replaceEach(pattern, specialCharacters, escapedCharacters);
    }

    /**
     * Extracts longest string that does not contain * or ? symbols.
     *
     * @param pattern Wildcard pattern
     * @return Longest string without special symbols
     */
    private static String extractShortcut(String pattern) {
        char[] wildcardChars = new char[]{'*', '?'};
        int startIndex = 0;
        int endIndex = StringUtils.indexOfAny(pattern, wildcardChars);

        if (endIndex < 0) {
            return pattern;
        }
        String shortcut = endIndex == startIndex
                ? StringUtils.EMPTY
                : pattern.substring(startIndex, endIndex - startIndex);

        while (endIndex >= 0) {
            startIndex = startIndex + endIndex + 1;
            if (pattern.length() <= startIndex) {
                break;
            }

            endIndex = StringUtils.indexOfAny(pattern.substring(startIndex), wildcardChars);
            String tmpShortcut = endIndex < 0
                    ? pattern.substring(startIndex)
                    : pattern.substring(startIndex, endIndex + startIndex);

            if (tmpShortcut.length() > shortcut.length()) {
                shortcut = tmpShortcut;
            }
        }

        return shortcut;
    }

    @Override
    public String toString() {
        return regexp.toString();
    }
}