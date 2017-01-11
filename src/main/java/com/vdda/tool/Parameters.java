package com.vdda.tool;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
public class Parameters {

    private Parameters() {
    }

    /**
     * Seems that slack sends only one value per parameter (i.e no value lists are require)
     */
    public static Map<String, String> parse(String parameters) {

        Map<String, String> list = new HashMap<>();

        if (parameters == null) {
            return list;
        }

        list = Arrays.stream(parameters.split("&"))
                .map(p -> p.split("="))
                .filter(a -> a.length == 2)
                .collect(Collectors.toMap(e -> decode(e[0]), e -> decode(e[1])));

        return list;
    }

    private static String decode(final String encoded) {
        try {
            return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("Impossible: UTF-8 is a required encoding", e);
        }
    }
}