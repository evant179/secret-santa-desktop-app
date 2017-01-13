package com.secretsanta.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.secretsanta.data.CsvFactory.FILETYPE;
import com.secretsanta.generator.SecretSanta;
import com.secretsanta.gui.SecretSantaDisplayType;

/**
 * Class to read data from the data file
 * 
 * TODO unit test and refactor this class
 */
public class DataReader
{
    private static final Logger logger = LoggerFactory.getLogger(DataReader.class);

    private final CsvFactory csvFactory;
    private final ExclusionReader exclusionReader;

    /**
     * Constructor
     * 
     * @param csvFactory
     * @param exclusionReader
     */
    public DataReader(CsvFactory csvFactory, ExclusionReader exclusionReader)
    {
        this.csvFactory = csvFactory;
        this.exclusionReader = exclusionReader;
    }

    /**
     * Parse and merge data file and exclusion file, returning a list of
     * SecretSanta types
     * 
     * @param isHistoryIncluded
     *            If true, then include history from data file into the
     *            SecretSanta exclusion list; otherwise, only names from
     *            exclusion file is added to exclusion list
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public List<SecretSanta> parseDataFileWithExclusionFile(boolean isHistoryIncluded)
            throws FileNotFoundException, IOException
    {
        CSVReader dataCsvReader = this.csvFactory.createCsvReader(FILETYPE.DATA);

        // parsing into tokens: http://howtodoinjava.com/2013/05/27/parse-csv-files-in-java/
        final List<SecretSanta> secretSantaList = new ArrayList<SecretSanta>();
        String[] tokens = null;

        // get exclusion data
        Map<String, List<String>> nameToExclusionNameListMap = this.exclusionReader
                .getExclusionListDataFromFile();

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
                        if (!isHistoryIncluded)
                        {
                            break; // do not read in rest of row from data file
                        }
                    }
                    else if (isHistoryIncluded)
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
     * Parse raw data, INCLUDING empty tokens (""), returning an object for the
     * MainTableView
     * 
     * @param dataFilePath
     * @param exclusionFilePath
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public List<SecretSantaDisplayType> parseRawDataFileWithExclusions()
            throws FileNotFoundException, IOException
    {
        CSVReader dataCsvReader = this.csvFactory.createCsvReader(FILETYPE.DATA);

        final List<SecretSantaDisplayType> secretSantaDisplayList = new ArrayList<SecretSantaDisplayType>();
        String[] tokens = null;

        // get exclusion data
        Map<String, List<String>> nameToExclusionNameListMap = this.exclusionReader
                .getExclusionListDataFromFile();

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
                SecretSantaDisplayType secretSantaDisplayType = new SecretSantaDisplayType(
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
    public List<String> parseYearData() throws FileNotFoundException, IOException
    {
        CSVReader dataCsvReader = this.csvFactory.createCsvReader(FILETYPE.DATA);

        final List<String> yearList = new ArrayList<String>();

        // Read only the first line of the file, expecting to retrieve year data
        String[] tokens = dataCsvReader.readNext();

        for (String token : tokens)
        {
            if ((tokens.length > 0) && (!(token.charAt(0) == '#')))
            {
                String currentData = token.toUpperCase();
                logger.info("Detect previous year [{}]", currentData);
                yearList.add(currentData);
            }
        }

        dataCsvReader.close();

        return yearList;
    }

    public boolean isDuplicateName(String name) throws FileNotFoundException, IOException
    {
        boolean isDuplicate = false;
        // TODO make own method later than using the below one
        List<SecretSanta> dataList = this.parseDataFileWithExclusionFile(true);
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
    public int getNumberYearsCompleted() throws FileNotFoundException, IOException
    {
        CSVReader dataCsvReader = this.csvFactory.createCsvReader(FILETYPE.DATA);

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
