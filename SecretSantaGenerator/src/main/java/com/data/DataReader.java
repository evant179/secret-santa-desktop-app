package com.data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generator.SecretSanta;
import com.gui.Constants;
import com.gui.SecretSantaDisplayType2;
import com.opencsv.CSVReader;

public class DataReader
{
    private static final Logger logger = LoggerFactory.getLogger(DataReader.class);

    /**
     * Parse and merge data file and exclusion file, returning a list of
     * SecretSanta
     * 
     * @param dataFilePath
     * @param exclusionFilePath
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public List<SecretSanta> parseDataFileWithExclusionFile(CSVReader dataCsvReader,
            CSVReader exclusionCsvReader, ExclusionReader exclusionReader)
            throws FileNotFoundException, IOException
    {
        // parsing into tokens: http://howtodoinjava.com/2013/05/27/parse-csv-files-in-java/
        final List<SecretSanta> secretSantaList = new ArrayList<SecretSanta>();
        String[] tokens = null;

        // get exclusion data
        Map<String, List<String>> nameToExclusionNameListMap = exclusionReader
                .getExclusionListDataFromFile(exclusionCsvReader);

        // Read the file line by line
        while ((tokens = dataCsvReader.readNext()) != null)
        {
            String name = null;
            final List<String> excludedNames = new ArrayList<String>();

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
                    }
                    else if (name == null)
                    {
                        // Assign the first data entry of the row as the name
                        name = currentData;
                    }
                    else
                    {
                        // Add the remaining data entries as excludedNames
                        excludedNames.add(currentData);
                    }
                }

                if (name != null)
                {
                    List<String> exclusionListFromFile = nameToExclusionNameListMap
                            .get(name);
                    if (null != exclusionListFromFile)
                    {
                        for (String excludedNameFromFile : exclusionListFromFile)
                        {
                            if (!excludedNames.contains(excludedNameFromFile))
                            {
                                excludedNames.add(excludedNameFromFile);
                            }
                        }
                    }
                }

                logger.info("Create new SecretSanta where name[{}] excludes[{}]", name,
                        excludedNames);

                // Create SecretSanta for each row entry
                SecretSanta secretSanta = new SecretSanta(name, excludedNames);
                // Add SecretSanta to list to be returned
                secretSantaList.add(secretSanta);
            }
        }

        dataCsvReader.close();

        return secretSantaList;
    }

    /**
     * Parse and merge data file and exclusion file, returning a list of
     * SecretSanta
     * 
     * TODO refactor this!
     * 
     * @param dataFilePath
     * @param exclusionFilePath
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public List<SecretSanta> parseDataFileWithExclusionFileForExclusionDialog(
            CSVReader dataCsvReader, CSVReader exclusionCsvReader,
            ExclusionReader exclusionReader) throws FileNotFoundException, IOException
    {
        // parsing into tokens: http://howtodoinjava.com/2013/05/27/parse-csv-files-in-java/
        final List<SecretSanta> secretSantaList = new ArrayList<SecretSanta>();
        String[] tokens = null;

        // get exclusion data
        Map<String, List<String>> nameToExclusionNameListMap = exclusionReader
                .getExclusionListDataFromFile(exclusionCsvReader);

        // Read the file line by line
        while ((tokens = dataCsvReader.readNext()) != null)
        {
            String name = null;
            final List<String> excludedNames = new ArrayList<String>();

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
                    }
                    else if (name == null)
                    {
                        // Assign the first data entry of the row as the name
                        name = currentData;
                        break;
                    }
                    //                    else
                    //                    {
                    //                        // Add the remaining data entries as excludedNames
                    //                        excludedNames.add(currentData);
                    //                    }
                }

                if (name != null)
                {
                    List<String> exclusionListFromFile = nameToExclusionNameListMap
                            .get(name);
                    if (null != exclusionListFromFile)
                    {
                        for (String excludedNameFromFile : exclusionListFromFile)
                        {
                            if (!excludedNames.contains(excludedNameFromFile))
                            {
                                excludedNames.add(excludedNameFromFile);
                            }
                        }
                    }
                }

                // Create SecretSanta for each row entry
                SecretSanta secretSanta = new SecretSanta(name, excludedNames);
                // Add SecretSanta to list to be returned
                secretSantaList.add(secretSanta);
            }
        }

        dataCsvReader.close();

        return secretSantaList;
    }

    /**
     * Parse raw data, INCLUDING empty tokens (""), returning an object for the
     * MainTableView
     * 
     * @param dataFilePath
     * @param exclusionFilePath
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public List<SecretSantaDisplayType2> parseRawDataFileWithExclusions(
            CSVReader dataCsvReader, CSVReader exclusionCsvReader,
            ExclusionReader exclusionReader) throws FileNotFoundException, IOException
    {
        final List<SecretSantaDisplayType2> secretSantaDisplayList = new ArrayList<SecretSantaDisplayType2>();
        String[] tokens = null;

        // get exclusion data
        Map<String, List<String>> nameToExclusionNameListMap = exclusionReader
                .getExclusionListDataFromFile(exclusionCsvReader);

        // Read the file line by line
        while ((tokens = dataCsvReader.readNext()) != null)
        {
            String name = null;
            final List<String> previousSecretSantas = new ArrayList<String>();
            final List<String> excludedNames = new ArrayList<String>();

            // Check if tokens are not empty
            // and doesn't start with '#' (we're skipping these lines)
            if ((tokens.length > 0) && (!((tokens[0]).charAt(0) == '#')))
            {
                for (String token : tokens)
                {
                    String currentData = token.toUpperCase();

                    if (name == null)
                    {
                        // Assign the first data entry of the row as the name
                        name = currentData;
                    }
                    else if ((currentData == null) || (currentData.isEmpty()))
                    {
                        previousSecretSantas.add(""); // add empty token
                    }
                    else
                    {
                        previousSecretSantas.add(currentData);
                    }
                }

                if (name != null)
                {
                    List<String> exclusionListFromFile = nameToExclusionNameListMap
                            .get(name);
                    if (null != exclusionListFromFile)
                    {
                        for (String excludedNameFromFile : exclusionListFromFile)
                        {
                            if (!excludedNames.contains(excludedNameFromFile))
                            {
                                excludedNames.add(excludedNameFromFile);
                            }
                        }
                    }
                }

                // Create SecretSanta for each row entry
                SecretSantaDisplayType2 secretSantaDisplayType = new SecretSantaDisplayType2(
                        name, previousSecretSantas, excludedNames);
                // Add SecretSanta to list to be returned
                secretSantaDisplayList.add(secretSantaDisplayType);
            }
        }

        dataCsvReader.close();

        return secretSantaDisplayList;
    }

    /**
     * Parse year data from data file
     * 
     * @param dataFilePath
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public List<String> parseYearData(String dataFilePath)
            throws FileNotFoundException, IOException
    {
        final List<String> yearList = new ArrayList<String>();

        // Create the file reader
        CSVReader reader = new CSVReader(new FileReader(dataFilePath));

        // Read only the first line of the file, expecting to retrieve year data
        String[] tokens = reader.readNext();

        for (String token : tokens)
        {
            if ((tokens.length > 0) && (!(token.charAt(0) == '#')))
            {
                String currentData = token.toUpperCase();
                logger.info("Detect previous year [{}]", currentData);
                yearList.add(currentData);
            }
        }

        reader.close();

        return yearList;
    }

    public boolean isDuplicateName(CSVReader dataCsvReader, CSVReader exclusionCsvReader,
            ExclusionReader exclusionReader, String name)
            throws FileNotFoundException, IOException
    {
        boolean isDuplicate = false;
        // TODO make own method later than using the below one
        List<SecretSanta> dataList = this.parseDataFileWithExclusionFile(dataCsvReader,
                exclusionCsvReader, exclusionReader);
        for (SecretSanta entry : dataList)
        {
            if (entry.getName().equals(name))
            {
                logger.info("Duplicate found in existing data for [{}]", name);
                isDuplicate = true;
                break;
            }
        }
        return isDuplicate;
    }

    /**
     * Return the number of years completed
     * 
     * @param dataFilePath
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public int getNumberYearsCompleted(CSVReader dataCsvReader)
            throws FileNotFoundException, IOException
    {
        int yearCount = 0;

        // Read only the first line of the file, expecting to retrieve year data
        String[] tokens = dataCsvReader.readNext();

        for (String token : tokens)
        {
            if ((tokens.length > 0) && (!(token.charAt(0) == '#')))
            {
                yearCount++;
            }
        }

        dataCsvReader.close();

        logger.info("Number of years completed: [{}]", yearCount);
        return yearCount;
    }
}
