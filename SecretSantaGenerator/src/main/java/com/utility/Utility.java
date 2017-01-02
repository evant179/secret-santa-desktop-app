package com.utility;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import com.gui.Constants;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

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
    
    public static CSVWriter createCsvWriter(String filePath) throws IOException
    {
        FileWriter fileWriter = new FileWriter(filePath, true);
        CSVWriter csvWriter = new CSVWriter(fileWriter, ',',
                CSVWriter.NO_QUOTE_CHARACTER);
        return csvWriter;
    }
    
    public static CSVReader createCsvReader(String filePath) throws FileNotFoundException
    {
        CSVReader reader = new CSVReader(new FileReader(filePath));
        return reader;
    }
    
}
