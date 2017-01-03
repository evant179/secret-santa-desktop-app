package com.utility;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public class Utility
{
    //for lists
    public static <T, U> List<U> convertList(List<T> from, Function<T, U> func)
    {
        return from.stream().map(func).collect(Collectors.toList());
    }

    //for arrays
    public static <T, U> U[] convertArray(T[] from, Function<T, U> func,
            IntFunction<U[]> generator)
    {
        return Arrays.stream(from).map(func).toArray(generator);
    }

    public static String formatName(String name)
    {
        String formattedName = "";
        if (name != null)
        {
            // trim leading and trailing whitespace
            formattedName = name.trim();
            // replace each whitespace (should exist only in between chars) with "_"
            formattedName = formattedName.replaceAll("\\s", "_");
            // uppercase all chars
            formattedName = formattedName.toUpperCase();
        }
        return formattedName;
    }
}
