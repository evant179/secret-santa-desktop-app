package com.data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generator.SecretSanta;
import com.gui.SecretSantaDisplayType;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class DataRecorder
{
    private static final Logger logger = LoggerFactory.getLogger(DataRecorder.class);

    private final String dataFilePath;
    private final String exclusionFilePath;
    private DataReader dataReader = new DataReader();
    private FileWriter fileWriter = null;
    private CSVWriter csvWriter = null;
    private FileReader fileReader = null;
    private CSVReader csvReader = null;
    
    public DataRecorder(String dataFilePath, String exclusionFilePath)
    {
        this.dataFilePath = dataFilePath;
        this.exclusionFilePath = exclusionFilePath;
    }
    
    public void saveNewcomerToCurrentData(String newcomerName) throws Exception
    {
        logger.info(
                "Start saving newcomer[{}] to dataFilePath[{}], exclusionFilePath[{}]",
                newcomerName, this.dataFilePath, this.exclusionFilePath);

        // check if name already exists in the data file
        if (this.dataReader.isDuplicateName(this.dataFilePath, newcomerName))
        {
            final String message = String.format("[%s] is a duplicate name.",
                    newcomerName);
            logger.error(message);
            throw new Exception(message);
        }
        
        // get number of years first so the CSVReader can safely read and close
        // before a write is attempted below
        int numberYearsCompleted = this.dataReader
                .getNumberYearsCompleted(this.dataFilePath);
        if (numberYearsCompleted <= 0)
        {
            final String message = String.format(
                    "Invalid numberYearsCompleted[%s]. Cannot save newcomer[%s].",
                    numberYearsCompleted, newcomerName);
            logger.error(message);
            throw new Exception(message);
        }

        // Create new row to be appended on data file
        this.fileWriter = new FileWriter(this.dataFilePath, true);
        this.csvWriter = new CSVWriter(this.fileWriter, ',',
                CSVWriter.NO_QUOTE_CHARACTER);
        List<String> newcomerDataRow = this.createNewcomerDataRow(newcomerName, numberYearsCompleted);
        this.csvWriter.writeNext(newcomerDataRow.toArray(new String[0]));
        this.csvWriter.close();

        // Create new row to be appended on exclusion file
        this.fileWriter = new FileWriter(this.exclusionFilePath, true);
        this.csvWriter = new CSVWriter(this.fileWriter, ',',
                CSVWriter.NO_QUOTE_CHARACTER);
        List<String> newcomerExclusionRow = this.createNewcomerExclusionRow(newcomerName);
        this.csvWriter.writeNext(newcomerExclusionRow.toArray(new String[0]));
        this.csvWriter.close();

        logger.info(
                "Successfully save newcomer[{}] to dataFilePath[{}], exclusionFilePath[{}]",
                newcomerName, this.dataFilePath, this.exclusionFilePath);
    }

    public void updateExclusionFile(SecretSanta secretSanta) throws IOException
    {
        logger.info("Start updating EXCLUSION list for[{}] to exclusionFilePath[{}]",
                secretSanta.getName(), this.exclusionFilePath);

        // =========== read file and put udpated data in updatedExclusionData ===========
        this.fileReader = new FileReader(this.exclusionFilePath);
        this.csvReader = new CSVReader(new FileReader(this.exclusionFilePath));
        List<List<String>> updatedExclusionData = new ArrayList<List<String>>();

        String[] entries = null;
        while ((entries = this.csvReader.readNext()) != null)
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
        this.csvReader.close();

        // =========== delete old data from file ===========
        this.fileWriter = new FileWriter(this.exclusionFilePath);
        this.fileWriter.write("");
        this.fileWriter.close();

        // =========== save updated data to file ===========
        this.fileWriter = new FileWriter(this.exclusionFilePath, true);
        this.csvWriter = new CSVWriter(this.fileWriter, ',',
                CSVWriter.NO_QUOTE_CHARACTER);
        for (List<String> row : updatedExclusionData)
        {
            this.csvWriter.writeNext(row.toArray(new String[0]));
        }
        this.csvWriter.close();

        logger.info(
                "Successfully updated EXCLUSION list for[{}] to exclusionFilePath[{}]",
                secretSanta.getName(), this.exclusionFilePath);
    }
    
    /**
     * Append new secret santa results onto the current data
     * 
     * @param recordList
     *            List containing secret santa results to be appended
     * @param dataFilePath
     * @param outputFilePath
     * @throws IOException
     */
    public void save(List<SecretSantaDisplayType> recordList, String dataFilePath,
            CSVWriter writer) throws IOException
    {
        // first read data.csv
        // then append recordList at end

        @SuppressWarnings("resource")
        CSVReader reader = new CSVReader(new FileReader(dataFilePath));

        int rowSize = 0;

        String[] entries = null;
        while ((entries = reader.readNext()) != null)
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
                boolean isMatchFound = false;
                for (SecretSantaDisplayType record : recordList)
                {
                    if (list.get(0).equals(record.getName()))
                    {
                        list.add(record.getSecretSanta());
                        recordList.remove(record);
                        isMatchFound = true;
                        break;
                    }
                }

                if (!isMatchFound)
                {
                    // If no match found, then add empty string at end of current row
                    // i.e. xxx was at previous secret santa but didn't go current year
                    list.add("");
                }
            }

            checkRowSize(rowSize, list);

            writer.writeNext(list.toArray(new String[0]));
        }

        // Save newcomers
        for (SecretSantaDisplayType newComer : recordList)
        {
            List<String> newComerRow = new ArrayList<String>();
            for (int i = 0; i < rowSize; i++)
            {
                newComerRow.add("");
            }
            newComerRow.set(0, newComer.getName()); // set name as first spot in row
            newComerRow.set(rowSize - 1, newComer.getSecretSanta()); // set secret santa as last spot in row
            checkRowSize(rowSize, newComerRow);
            writer.writeNext(newComerRow.toArray(new String[0]));
        }

        writer.close();
    }

    public String getDataFilePath()
    {
        return this.dataFilePath;
    }
    
    public String getExclusionFilePath()
    {
        return this.exclusionFilePath;
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

    private void checkRowSize(int rowSize, List<String> list)
    {
        if (rowSize != list.size())
        {
            System.out.println(
                    "ERROR when saving - header row size does NOT match current row size: "
                            + list.get(0));
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
