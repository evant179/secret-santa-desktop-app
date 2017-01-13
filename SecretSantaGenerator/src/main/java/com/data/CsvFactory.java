package com.data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

/**
 * Helper class to create {@link CSVReader} and {@link CSVWriter} objects
 */
public class CsvFactory
{
    /**
     * Enumeration specifying file type.
     * 
     * Used to store file path and is treated as a static final due to Java
     * handling of enumerations.
     */
    public enum FILETYPE
    {
        DATA,
        EXCLUSION,
        OUTPUT;

        /**
         * File path for a specific {@link FILETYPE}
         */
        private String filePath;

        public String getFilePath()
        {
            return this.filePath;
        }

        /**
         * Set file path
         * 
         * @param filePath
         */
        private void setFilePath(String filePath)
        {
            this.filePath = filePath;
        }
    }

    /**
     * Constructor
     * 
     * @param dataFilePath
     *            Data file path; can be null if unused
     * @param exclusionFilePath
     *            Exclusion file path; can be null if unused
     * @param outputFilePath
     *            Output file path; can be null if unused
     */
    public CsvFactory(String dataFilePath, String exclusionFilePath,
            String outputFilePath)
    {
        FILETYPE.DATA.setFilePath(dataFilePath);
        FILETYPE.EXCLUSION.setFilePath(exclusionFilePath);
        FILETYPE.OUTPUT.setFilePath(outputFilePath);
    }

    /**
     * Create {@link CSVReader} based on specified {@link FILETYPE}
     * 
     * @param fileType
     * @return
     * @throws FileNotFoundException
     */
    public CSVReader createCsvReader(FILETYPE fileType) throws FileNotFoundException
    {
        return new CSVReader(new FileReader(fileType.filePath));
    }

    /**
     * Create {@link CSVWriter} based on specified {@link FILETYPE}
     * 
     * @param fileType
     * @return
     * @throws IOException
     */
    public CSVWriter createCsvWriter(FILETYPE fileType) throws IOException
    {
        final FileWriter fileWriter;
        if (fileType == FILETYPE.DATA || fileType == FILETYPE.EXCLUSION)
        {
            // append existing files
            fileWriter = this.createFileWriter(fileType, true);
        }
        else
        {
            // FILETYPE.OUTPUT
            // write brand new file
            fileWriter = this.createFileWriter(fileType, false);
        }
        return new CSVWriter(fileWriter, ',', CSVWriter.NO_QUOTE_CHARACTER);
    }

    /**
     * Create {@link FileWriter} based on specified {@link FILETYPE}
     * 
     * @param fileType
     * @param isAppend
     * @return
     * @throws IOException
     */
    public FileWriter createFileWriter(FILETYPE fileType, boolean isAppend)
            throws IOException
    {
        final FileWriter fileWriter;
        if (isAppend)
        {
            // append existing files
            fileWriter = new FileWriter(fileType.filePath, true);
        }
        else
        {
            // FILETYPE.OUTPUT
            // write brand new file
            fileWriter = new FileWriter(fileType.filePath);
        }
        return fileWriter;
    }
}
