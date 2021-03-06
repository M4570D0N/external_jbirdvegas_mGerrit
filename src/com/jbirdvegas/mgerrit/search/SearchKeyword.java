package com.jbirdvegas.mgerrit.search;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/*
 * Copyright (C) 2013 Android Open Kang Project (AOKP)
 *  Author: Evan Conway (P4R4N01D), 2013
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
public abstract class SearchKeyword {

    private final String mOpName;
    private final String mOpParam;
    private String mOperator;

    private static final Map<String, Class<? extends SearchKeyword>>_KEYWORDS;
    static {
        _KEYWORDS = new HashMap<String, Class<? extends SearchKeyword>>();
    }

    public SearchKeyword(String name, String param) {
        this.mOpName = name;
        this.mOpParam = param;
    }

    public SearchKeyword(String name, String operator, String param) {
        mOpName = name;
        mOperator = operator;
        mOpParam = param;
    }

    protected static void registerKeyword(String opName, Class<? extends SearchKeyword> clazz) {
        _KEYWORDS.put(opName, clazz);
    }

    public String getName() { return mOpName; }
    public String getParam() { return mOpParam; }
    public String getOperator() { return mOperator; }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append(mOpName).append(":\"");
        if (mOperator != null) builder.append(mOperator);
        builder.append(mOpParam).append("\"");
        return builder.toString();
    }

    /**
     * Build a search keyword given a name and its parameter
     * @param name
     * @param param
     * @return
     */
    private static SearchKeyword buildToken(String name, String param) {

        Iterator<Entry<String, Class<? extends SearchKeyword>>> it = _KEYWORDS.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Class<? extends SearchKeyword>> entry = it.next();
            if (name.equalsIgnoreCase(entry.getKey())) {
                Constructor<? extends SearchKeyword> constructor = null;
                try {
                    constructor = entry.getValue().getDeclaredConstructor(String.class, String.class);
                    constructor.newInstance(name, param);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static SearchKeyword buildToken(String tokenStr) {
        String[] s = tokenStr.split(":", 2);
        if (s.length == 2) return buildToken(s[0], s[1]);
        else return null;
    }

    public static Set<SearchKeyword> constructTokens(String query) {
        Set<SearchKeyword> set = new HashSet<SearchKeyword>();
        String currentToken = "";
        for (int i = 0, n = query.length(); i < n; i++) {
            char c = query.charAt(i);
            if (Character.isWhitespace(c)) {
                if (currentToken.length() > 0) {
                    set.add(buildToken(currentToken));
                    currentToken = "";
                }
            } else if (c == '"') {
                int index = query.indexOf('"', i + 1);
                currentToken += query.substring(i, index);
                i = index + 1; // We have processed this many characters
            } else if (c == '{') {
                int index = query.indexOf('}', i + 1);
                currentToken += query.substring(i, index + 1);
                i = index + 1; // We have processed this many characters
            } else {
                currentToken += c;
            }
        }
        return set;
    }

    private static String constructDbSearchQuery(Set<SearchKeyword> tokens) {
        StringBuilder whereQuery = new StringBuilder();
        Iterator<SearchKeyword> it = tokens.iterator();
        while (it.hasNext()) {
            SearchKeyword token = it.next();
            whereQuery.append(token.buildSearch());
            if (it.hasNext()) whereQuery.append(" AND ");
        }
        return whereQuery.toString();
    }

    public static void search(String query) {
        String where = constructDbSearchQuery(constructTokens(query));
    }

    public abstract String buildSearch();
}
