package com.data;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generator.SecretSanta;
import com.gui.Constants;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class DataRecorder
{
    private static final Logger logger = LoggerFactory.getLogger(DataRecorder.class);

    private final CSVReader dataCsvReader;
    // TODO verify dataCsvWriter behavior--may need updating methods.
    // because originally saveNewcomerToCurrentData and saveGenerationResults had
    // different writers because:
    // 1. saveNewcomerToCurrentData appended to existing file
    // 2. and saveGenerationResults saved to new file
    // ...(doing this for now)ez fix would be to pass in a separate CSVWriter specifically for
    // saveGenerationResults where it's instantiated by:
    // CSVWriter writer = new CSVWriter(
    //    new FileWriter(Constants.OUTPUT_FILE_PATH), ',',
    //    CSVWriter.NO_QUOTE_CHARACTER);
    // better way is to do what updateExclusionFile does where it updates the actual data?
    // depends if we want the results to be appended on existing data file or a new data file
    private final CSVWriter dataCsvWriter;
    private final DataReader dataReader;
    private final CSVReader exclusionCsvReader;
    private final CSVWriter exclusionCsvWriter;
    private final CSVWriter generatedResultsDataCsvWriter;

    public DataRecorder(CSVReader dataCsvReader, CSVWriter dataCsvWriter,
            DataReader dataReader, CSVReader exclusionCsvReader,
            CSVWriter exclusionCsvWriter, CSVWriter generatedResultsDataCsvWriter)
    {
        this.dataCsvReader = dataCsvReader;
        this.dataCsvWriter = dataCsvWriter;
        this.dataReader = dataReader;
        this.exclusionCsvReader = exclusionCsvReader;
        this.exclusionCsvWriter = exclusionCsvWriter;
        this.generatedResultsDataCsvWriter = generatedResultsDataCsvWriter;
    }

    public void saveNewcomerToCurrentData(String newcomerName) throws Exception
    {
        logger.info("Start saving newcomer[{}] to data file, exclusion file",
                newcomerName);

        // check if name already exists in the data file
        if (this.dataReader.isDuplicateName(newcomerName))
        {
            final String message = String.format("[%s] is a duplicate name.",
                    newcomerName);
            logger.error(message);
            throw new Exception(message);
        }

        // get number of years first so the CSVReader can safely read and close
        // before a write is attempted below
        int numberYearsCompleted = this.dataReader.getNumberYearsCompleted();
        if (numberYearsCompleted <= 0)
        {
            final String message = String.format(
                    "Invalid numberYearsCompleted[%s]. Cannot save newcomer[%s].",
                    numberYearsCompleted, newcomerName);
            logger.error(message);
            throw new Exception(message);
        }

        // Create new row to be appended on data file
        List<String> newcomerDataRow = createNewcomerDataRow(newcomerName,
                numberYearsCompleted);
        this.dataCsvWriter.writeNext(newcomerDataRow.toArray(new String[0]));
        this.dataCsvWriter.close();

        // Create new row to be appended on exclusion file
        List<String> newcomerExclusionRow = createNewcomerExclusionRow(newcomerName);
        this.exclusionCsvWriter.writeNext(newcomerExclusionRow.toArray(new String[0]));
        this.exclusionCsvWriter.close();

        logger.info("Successfully save newcomer[{}] to data file, exclusion file",
                newcomerName);
    }

    public void updateExclusionFile(FileWriter clearExclusionFileWriter,
            SecretSanta secretSanta) throws IOException
    {
        logger.info("Start updating EXCLUSION list for[{}] to exclusion file",
                secretSanta.getName());

        // =========== read file and put udpated data in updatedExclusionData ===========
        List<List<String>> updatedExclusionData = new ArrayList<List<String>>();

        String[] entries = null;
        while ((entries = this.exclusionCsvReader.readNext()) != null)
        {
            List<String> currentRow = Arrays.asList(entries); // Arrays.asList(entries) is unnmodifiable
            currentRow = new ArrayList<String>(currentRow); // Convert to ArrayList to be modifiable

            if (currentRow.get(0).equals(secretSanta.getName()))
            {
                // current row matches secret santa.
                currentRow.clear();
                currentRow.add(secretSanta.getName());
                for (String excludedName : secretSanta.getExcludedNames())
                {
                    currentRow.add(excludedName);
                }
            }
            else if (secretSanta.getExcludedNames().contains(currentRow.get(0)))
            {
                // current row is part of secret santa's exclusion list.
                // if x excludes y, make sure y excludes x
                List<String> updatedRow = new ArrayList<String>(currentRow);
                currentRow.clear();
                boolean isAlreadyExcludingSecretSanta = false;
                for (String excludedName : updatedRow)
                {
                    if (excludedName != null && !excludedName.isEmpty())
                    {
                        if (secretSanta.getName().equals(excludedName))
                        {
                            isAlreadyExcludingSecretSanta = true;
                        }
                        currentRow.add(excludedName);
                    }
                }
                if (!isAlreadyExcludingSecretSanta)
                {
                    currentRow.add(secretSanta.getName());
                }
            }
            else if (currentRow.contains(secretSanta.getName()))
            {
                // current row is NOT part of secret santa's exclusion list.
                // if x no longer excludes y, make sure y no longer excludes x
                currentRow.remove(secretSanta.getName());
            }

            updatedExclusionData.add(currentRow);
        }
        this.exclusionCsvReader.close();

        // =========== delete old data from file ===========
        clearExclusionFileWriter.write("");
        clearExclusionFileWriter.close();

        // =========== save updated data to file ===========
        for (List<String> row : updatedExclusionData)
        {
            this.exclusionCsvWriter.writeNext(row.toArray(new String[0]));
        }
        this.exclusionCsvWriter.close();

        logger.info("Successfully updated EXCLUSION list for[{}] to exclusion file",
                secretSanta.getName());
    }

    /**
     * TODO change save2 to work for SecretSantaDisplayType2 with unit tests.
     * switch over when done Append new secret santa results onto the current
     * data
     * 
     * @param resultMap
     *            Map containing secret santa results to be appended
     * @param dataFilePath
     * @param outputFilePath
     * @throws Exception
     */
    public void saveGenerationResults(final Map<String, String> resultMap)
            throws Exception
    {
        // first read data.csv
        // then append recordList at end

        int rowSize = 0;

        String[] entries = null;
        while ((entries = this.dataCsvReader.readNext()) != null)
        {
            List<String> list = Arrays.asList(entries); // Arrays.asList(entries) is unnmodifiable
            list = new ArrayList<String>(list); // Convert to ArrayList to be modifiable

            // Check if header line
            if (list.get(0).charAt(0) == '#')
            {
                // This should only occur for the first row
                String lastRecordedYearString = list.get(list.size() - 1);
                int lastRecordedYear = Integer.parseInt(lastRecordedYearString);
                lastRecordedYear++;
                list.add(Integer.toString(lastRecordedYear));
                rowSize = list.size();
            }
            else
            {
                final String rowEntryName = list.get(0);
                if (resultMap.containsKey(rowEntryName))
                {
                    list.add(resultMap.get(rowEntryName));
                    resultMap.remove(rowEntryName);
                }
                else
                {
                    // If no match found, then add empty string at end of current row
                    // i.e. xxx was at previous secret santa but didn't go current year
                    list.add("");
                }
            }

            checkRowSize(rowSize, list);
            this.generatedResultsDataCsvWriter.writeNext(list.toArray(new String[0]));
        }

        // Save newcomers
        for (Map.Entry<String, String> entry : resultMap.entrySet())
        {
            final String newComerName = entry.getKey();
            final String newComerResult = entry.getValue();
            List<String> newComerRow = new ArrayList<String>();
            for (int i = 0; i < rowSize; i++)
            {
                newComerRow.add("");
            }
            newComerRow.set(0, newComerName); // set name as first spot in row
            newComerRow.set(rowSize - 1, newComerResult); // set secret santa as last spot in row
            checkRowSize(rowSize, newComerRow);
            this.generatedResultsDataCsvWriter.writeNext(newComerRow.toArray(new String[0]));
        }

        this.generatedResultsDataCsvWriter.close();
    }

    private List<String> createNewcomerDataRow(String newcomerName, int numberEmptyQuotes)
    {
        List<String> newcomerRow = new ArrayList<String>();
        newcomerRow.add(newcomerName);
        for (int i = 0; i < numberEmptyQuotes; i++)
        {
            newcomerRow.add("");
        }
        return newcomerRow;
    }

    private List<String> createNewcomerExclusionRow(String newcomerName)
    {
        List<String> newcomerRow = new ArrayList<String>();
        newcomerRow.add(newcomerName);
        return newcomerRow;
    }

    private void checkRowSize(int rowSize, List<String> list) throws Exception
    {
        if (rowSize != list.size())
        {
            final String message = "ERROR when saving - header row size does NOT "
                    + "match current row size: " + list.get(0);
            throw new Exception(message);
        }
    }

    //    public void debugConverter(String dataFilePath, String outputFilePath) throws IOException
    //    {
    //        @SuppressWarnings("resource")
    //        CSVReader reader = new CSVReader(new FileReader(dataFilePath));
    //        CSVWriter writer = new CSVWriter(new FileWriter(outputFilePath), ',',
    //                CSVWriter.NO_QUOTE_CHARACTER);
    //
    //        String[] entries = null;
    //        while ((entries = reader.readNext()) != null)
    //        {
    //            List<String> list = Arrays.asList(entries); // Arrays.asList(entries) is unnmodifiable
    //            list = new ArrayList<String>(list); // Convert to ArrayList to be modifiable
    //
    //            writer.writeNext(list.toArray(new String[0]));
    //        }
    //
    //        writer.close();
    //    }
}
