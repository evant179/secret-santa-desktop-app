package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import generator.SecretSanta;

public class DataReader
{
    private static final String FILE_PATH = "resources/data.csv";
    
    // TODO parsing into tokens: http://howtodoinjava.com/2013/05/27/parse-csv-files-in-java/
    public static List<SecretSanta> parseDataFile() throws FileNotFoundException, IOException
    {
        final List<SecretSanta> secretSantaList = new ArrayList<SecretSanta>();

        // Input file which needs to be parsed
        BufferedReader fileReader = null;

        // Delimiter used in CSV file
        final String DELIMITER = ",";
        String line = "";

        // Create the file reader
        fileReader = new BufferedReader(new FileReader(FILE_PATH));

        // Read the file line by line
        while ((line = fileReader.readLine()) != null)
        {
            String name = null;
            final List<String> excludedNames = new ArrayList<String>();

            // Get all tokens available in line
            String[] tokens = line.split(DELIMITER);

            // Check if tokens are not empty
            // and doesn't start with '#' (we're skipping these lines)
            if ((tokens.length > 0) && (!((tokens[0]).charAt(0) == '#')))
            {
                for (String token : tokens)
                {
                    String currentData = token.toUpperCase();

                    if ((currentData == null) || (currentData.isEmpty()))
                    {
                        // skip
                    } else if (name == null)
                    {
                        // Assign the first data entry of the row as the name
                        name = currentData;
                    } else
                    {
                        // Add the remaining data entries as excludedNames
                        excludedNames.add(currentData);
                    }
                }

                // // debug
                // System.out.print(name + "//excludes//");
                // for(String excludedName : excludedNames)
                // {
                // System.out.print(excludedName + "||");
                // }
                // System.out.println();

                // Create SecretSanta for each row entry
                SecretSanta secretSanta = new SecretSanta(name, excludedNames);
                // Add SecretSanta to list to be returned
                secretSantaList.add(secretSanta);
            }
        }
        
        fileReader.close();
            
        return secretSantaList;
    }
}
