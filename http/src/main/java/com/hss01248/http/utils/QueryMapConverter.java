package com.hss01248.http.utils;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Here's an implementation that takes arbitrarily nested objects (with arrays, sets, lists, ...), 
 * uses Gson to serialize them and then walks the Gson tree to construct the query
 * string in the form of a custom Map<String, String>.
 *
 * @author daixiaogang
 * @version 1.0
 * @since 2019-10-21
 */
public class QueryMapConverter {

    /**
     * Converts an object to serialized URI parameters.
     *
     * @param value The value to be converted (can be an object or collection).
     * @return Serialized URI parameters for use with {@literal @}{@link retrofit2.http.QueryMap}(encoded=true).
     */
    public static Map<String, String> convert(Gson gson,Object value) {
        return convert(gson,value, false);
    }

    /**
     * Converts an object to serialized URI parameters.
     *
     * @param value The value to be converted (can be an object or collection).
     * @param needIndex The params need index like ex[0],ex[1]....             
     * @return Serialized URI parameters for use with {@literal @}{@link retrofit2.http.QueryMap}(encoded=true).
     */
    public static Map<String, String> convert(Gson gson,Object value, boolean needIndex) {
        return convertTree(gson.toJsonTree(value), needIndex);
    }

    /**
     * Converts the given JSON tree to serialized URI parameters. This is equivalent to helpers.js/encodeParams.
     *
     * @param tree The JSON tree (can be an object or array).
     * @return Serialized URI parameters for use with {@literal @}{@link retrofit2.http.QueryMap}(encoded=false).
     */
    public static Map<String, String> convertTree(JsonElement tree) {
        return convertTree(tree, false);
    }

    /**
     * Converts the given JSON tree to serialized URI parameters. This is equivalent to helpers.js/encodeParams.
     *
     * @param tree The JSON tree (can be an object or array).
     * @param needIndex The params need index like ex[0],ex[1]....           
     * @return Serialized URI parameters for use with {@literal @}{@link retrofit2.http.QueryMap}(encoded=false).
     */
    public static Map<String, String> convertTree(JsonElement tree, boolean needIndex) {
        ParamsMap params = new ParamsMap();
        if (tree.isJsonArray()) {
            int i = 0;
            for (JsonElement element : tree.getAsJsonArray()) {
                buildObjectParams(Integer.toString(i), element, params, needIndex);
                i++;
            }
        } else if (tree.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : tree.getAsJsonObject().entrySet()) {
                buildObjectParams(entry.getKey(), entry.getValue(), params, needIndex);
            }
        } else if (!tree.isJsonNull()) {
            throw new IllegalArgumentException("Cannot convert " + tree.toString());
        }
        return params;
    }

    /**
     * Recursive helper method for {@link #convertTree(JsonElement)}. This is equivalent to helpers.js/buildObjectParams.
     *
     * @param prefix The prefix for the parameter names.
     * @param tree   The remaining JSON tree.
     * @param params The params object to write to.
     */
    private static void buildObjectParams(String prefix, JsonElement tree, ParamsMap params) {
        buildObjectParams(prefix, tree, params, false);
    }

    /**
     * Recursive helper method for {@link #convertTree(JsonElement)}. This is equivalent to helpers.js/buildObjectParams.
     *
     * @param prefix The prefix for the parameter names.
     * @param tree   The remaining JSON tree.
     * @param params The params object to write to.
     * @param needIndex The params need index like ex[0],ex[1]....
     */
    private static void buildObjectParams(String prefix, JsonElement tree, ParamsMap params,
            boolean needIndex) {
        if (tree.isJsonArray()) {
            if (needIndex) {
                int i = 0;
                for (JsonElement element : tree.getAsJsonArray()) {
                    buildObjectParams(prefix + "[" + i + "]", element, params);
                    i++;
                }
            } else {
                for (JsonElement element : tree.getAsJsonArray()) {
                    buildObjectParams(prefix, element, params);
                }
            }
        } else if (tree.isJsonObject()) {
            if (needIndex) {
                for (Map.Entry<String, JsonElement> entry : tree.getAsJsonObject().entrySet()) {
                    buildObjectParams(prefix + "[" + entry.getKey() + "]", entry.getValue(),
                            params);
                }
            } else {
                for (Map.Entry<String, JsonElement> entry : tree.getAsJsonObject().entrySet()) {
                    buildObjectParams(prefix, entry.getValue(), params);
                }
            }
        } else if (tree.isJsonPrimitive()) {
            params.put(prefix, tree.getAsJsonPrimitive().getAsString());
        }
    }

    /**
     * A map class that allows multiple entries per key.
     */
    private static class ParamsMap implements Map<String, String> {

        private final Set<Entry<String, String>> entries = new LinkedHashSet<>();

        @Override
        public int size() {
            return entries.size();
        }

        @Override
        public boolean isEmpty() {
            return entries.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            for (Entry<String, String> entry : entries) {
                if (entry.getKey().equals(key)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            for (Entry<String, String> entry : entries) {
                if (entry.getValue().equals(value)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * @param key The key to look for.
         * @return The value of the FIRST matching entry or null if none matches.
         */
        @Override
        public String get(Object key) {
            for (Entry<String, String> entry : entries) {
                if (entry.getKey().equals(key)) {
                    return entry.getValue();
                }
            }
            return null;
        }

        @Override
        public String put(String key, String value) {
            entries.add(new ParamEntry(key, value));
            return null;
        }

        @Override
        public String remove(Object key) {

            return null;
        }

        @Override
        public void putAll(@NonNull Map<? extends String, ? extends String> m) {
            for (Entry<? extends String, ? extends String> entry : m.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public void clear() {
            entries.clear();
        }

        @NonNull
        @Override
        public Set<String> keySet() {
            Set<String> kSet = new LinkedHashSet<>();
            for (Entry<String, String> entry : entries) {
                kSet.add(entry.getKey());
            }
            return kSet;
        }

        @NonNull
        @Override
        public Collection<String> values() {
            LinkedList<String> vList = new LinkedList<>();
            for (Entry<String, String> entry : entries) {
                vList.add(entry.getValue());
            }
            return vList;
        }

        @NonNull
        @Override
        public Set<Entry<String, String>> entrySet() {
            return entries;
        }
    }

    @Keep
    private static class ParamEntry implements Map.Entry<String, String> {

        private final String key;

        private final String value;

        public ParamEntry(String key, String value) {
            this.key = Objects.requireNonNull(key);
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String setValue(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ParamEntry that = (ParamEntry) o;
            return key.equals(that.key) && value.equals(that.value);

        }

        @Override
        public int hashCode() {
            int result = key.hashCode();
            result = 31 * result + value.hashCode();
            return result;
        }
    }

}
