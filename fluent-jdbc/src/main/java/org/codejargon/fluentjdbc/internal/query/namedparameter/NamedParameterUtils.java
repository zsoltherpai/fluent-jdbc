/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codejargon.fluentjdbc.internal.query.namedparameter;

import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.internal.support.Preconditions;

import java.util.*;


/**
 * Helper methods for named parameter parsing.
 *
 * <p>Only intended for internal use within Spring's JDBC framework.
 *
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @since 2.0
 */
public abstract class NamedParameterUtils {

    private static final char[] PARAMETER_SEPARATORS =
            new char[] {'"', '\'', ':', '&', ',', ';', '(', ')', '|', '=', '+', '-', '*', '%', '/', '\\', '<', '>', '^'};
    private static final String[] START_SKIP =
            new String[] {"'", "\"", "--", "/*"};
    private static final String[] STOP_SKIP =
            new String[] {"'", "\"", "\n", "*/"};

    public static ParsedSql parseSqlStatement(final String sql) {
        Preconditions.checkNotNull(sql, "SQL must not be null");

        Set<String> namedParameters = new HashSet<>();
        String sqlToUse = sql;
        List<ParameterHolder> parameterList = new ArrayList<>();

        char[] statement = sql.toCharArray();
        int namedParameterCount = 0;
        int unnamedParameterCount = 0;
        int totalParameterCount = 0;

        int escapes = 0;
        int i = 0;
        while (i < statement.length) {
            int skipToPosition = i;
            while (i < statement.length) {
                skipToPosition = skipCommentsAndQuotes(statement, i);
                if (i == skipToPosition) {
                    break;
                }
                else {
                    i = skipToPosition;
                }
            }
            if (i >= statement.length) {
                break;
            }
            char c = statement[i];
            if (c == ':' || c == '&') {
                int j = i + 1;
                if (j < statement.length && statement[j] == ':' && c == ':') {
                    // Postgres-style "::" casting operator - to be skipped.
                    i = i + 2;
                    continue;
                }
                String parameter = null;
                if (j < statement.length && c == ':' && statement[j] == '{') {
                    // :{x} style parameter
                    while (j < statement.length && !('}' == statement[j])) {
                        j++;
                        if (':' == statement[j] || '{' == statement[j]) {
                            throw new FluentJdbcException("Parameter name contains invalid character '" +
                                    statement[j] + "' at position " + i + " in statement: " + sql);
                        }
                    }
                    if (j >= statement.length) {
                        throw new FluentJdbcException(
                                "Non-terminated named parameter declaration at position " + i + " in statement: " + sql);
                    }
                    if (j - i > 3) {
                        parameter = sql.substring(i + 2, j);
                        namedParameterCount = addNewNamedParameter(namedParameters, namedParameterCount, parameter);
                        totalParameterCount = addNamedParameter(parameterList, totalParameterCount, escapes, i, j + 1, parameter);
                    }
                    j++;
                }
                else {
                    while (j < statement.length && !isParameterSeparator(statement[j])) {
                        j++;
                    }
                    if (j - i > 1) {
                        parameter = sql.substring(i + 1, j);
                        namedParameterCount = addNewNamedParameter(namedParameters, namedParameterCount, parameter);
                        totalParameterCount = addNamedParameter(parameterList, totalParameterCount, escapes, i, j, parameter);
                    }
                }
                i = j - 1;
            }
            else {
                if (c == '\\') {
                    int j = i + 1;
                    if (j < statement.length && statement[j] == ':') {
                        // this is an escaped : and should be skipped
                        sqlToUse = sqlToUse.substring(0, i - escapes) + sqlToUse.substring(i - escapes + 1);
                        escapes++;
                        i = i + 2;
                        continue;
                    }
                }
                if (c == '?') {
                    unnamedParameterCount++;
                    totalParameterCount++;
                }
            }
            i++;
        }
        ParsedSql parsedSql = new ParsedSql(sqlToUse);
        for (ParameterHolder ph : parameterList) {
            parsedSql.addNamedParameter(ph.getParameterName(), ph.getStartIndex(), ph.getEndIndex());
        }
        parsedSql.setNamedParameterCount(namedParameterCount);
        parsedSql.setUnnamedParameterCount(unnamedParameterCount);
        parsedSql.setTotalParameterCount(totalParameterCount);
        return parsedSql;
    }

    private static int addNamedParameter(
            List<ParameterHolder> parameterList, int totalParameterCount, int escapes, int i, int j, String parameter) {

        parameterList.add(new ParameterHolder(parameter, i - escapes, j - escapes));
        totalParameterCount++;
        return totalParameterCount;
    }

    private static int addNewNamedParameter(Set<String> namedParameters, int namedParameterCount, String parameter) {
        if (!namedParameters.contains(parameter)) {
            namedParameters.add(parameter);
            namedParameterCount++;
        }
        return namedParameterCount;
    }

    private static int skipCommentsAndQuotes(char[] statement, int position) {
        for (int i = 0; i < START_SKIP.length; i++) {
            if (statement[position] == START_SKIP[i].charAt(0)) {
                boolean match = true;
                for (int j = 1; j < START_SKIP[i].length(); j++) {
                    if (!(statement[position + j] == START_SKIP[i].charAt(j))) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    int offset = START_SKIP[i].length();
                    for (int m = position + offset; m < statement.length; m++) {
                        if (statement[m] == STOP_SKIP[i].charAt(0)) {
                            boolean endMatch = true;
                            int endPos = m;
                            for (int n = 1; n < STOP_SKIP[i].length(); n++) {
                                if (m + n >= statement.length) {
                                    // last comment not closed properly
                                    return statement.length;
                                }
                                if (!(statement[m + n] == STOP_SKIP[i].charAt(n))) {
                                    endMatch = false;
                                    break;
                                }
                                endPos = m + n;
                            }
                            if (endMatch) {
                                // found character sequence ending comment or quote
                                return endPos + 1;
                            }
                        }
                    }
                    // character sequence ending comment or quote not found
                    return statement.length;
                }

            }
        }
        return position;
    }

    public static String substituteNamedParameters(ParsedSql parsedSql, Map<String, ?> namedParams) {
        String originalSql = parsedSql.getOriginalSql();
        StringBuilder actualSql = new StringBuilder();
        List<String> paramNames = parsedSql.getParameterNames();
        int lastIndex = 0;
        for (int i = 0; i < paramNames.size(); i++) {
            String paramName = paramNames.get(i);
            int[] indexes = parsedSql.getParameterIndexes(i);
            int startIndex = indexes[0];
            int endIndex = indexes[1];
            actualSql.append(originalSql, lastIndex, startIndex);
            if (namedParams.containsKey(paramName)) {
                Object value = namedParams.get(paramName);
                if (value instanceof Collection) {
                    for(int e = 0; e < ((Collection) value).size(); ++e) {
                        if (e > 0) {
                            actualSql.append(", ");
                        }
                        actualSql.append("?");
                    }
                } else {
                    actualSql.append("?");
                }
            } else {
                actualSql.append("?");
            }
            lastIndex = endIndex;
        }
        actualSql.append(originalSql, lastIndex, originalSql.length());
        return actualSql.toString();
    }

    public static Object[] buildValueArray(
            ParsedSql parsedSql, Map<String, ?> namedParams) {

        Object[] paramArray = new Object[parsedSql.getTotalParameterCount()];
        if (parsedSql.getNamedParameterCount() > 0 && parsedSql.getUnnamedParameterCount() > 0) {
            throw new FluentJdbcException(
                    "Not allowed to mix named and traditional ? placeholders. You have " +
                            parsedSql.getNamedParameterCount() + " named parameter(s) and " +
                            parsedSql.getUnnamedParameterCount() + " traditional placeholder(s) in statement: " +
                            parsedSql.getOriginalSql());
        }
        List<String> paramNames = parsedSql.getParameterNames();
        for (int i = 0; i < paramNames.size(); i++) {
            String paramName = paramNames.get(i);
            if (!namedParams.containsKey(paramName)) {
                throw new FluentJdbcException(String.format("Named parameter not set: '%s'", paramName));
            }
            paramArray[i] = namedParams.get(paramName);
        }
        return paramArray;
    }

    private static boolean isParameterSeparator(char c) {
        if (Character.isWhitespace(c)) {
            return true;
        }
        for (char separator : PARAMETER_SEPARATORS) {
            if (c == separator) {
                return true;
            }
        }
        return false;
    }


    private static class ParameterHolder {

        private final String parameterName;

        private final int startIndex;

        private final int endIndex;

        public ParameterHolder(String parameterName, int startIndex, int endIndex) {
            this.parameterName = parameterName;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public String getParameterName() {
            return this.parameterName;
        }

        public int getStartIndex() {
            return this.startIndex;
        }

        public int getEndIndex() {
            return this.endIndex;
        }
    }
} 