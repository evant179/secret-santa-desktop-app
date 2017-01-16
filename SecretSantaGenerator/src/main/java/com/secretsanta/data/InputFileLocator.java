package com.secretsanta.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secretsanta.gui.Constants;

/**
 * File locator for input files.
 * 
 * @formatter:off
 * 
 * Input files can exist in [additionalreources > input] (i.e. running via Eclipse):
 * -- runnable_jar_dir
 *        |-- additionalresources
 *        |       |-- input
 *        |       |       |-- data.csv
 *        |       |       |-- exclusions.csv
 *        
 * Or input files can exist in [input] (i.e. running via jfx packager):
 * -- runnable_jar_dir
 *        |-- input
 *        |       |-- data.csv
 *        |       |-- exclusions.csv
 *        
 * @formatter:on
 */
public class InputFileLocator
{
    private static final Logger logger = LoggerFactory.getLogger(InputFileLocator.class);

    /**
     * Determine location of input files and retrieve them
     * 
     * @return
     * @throws IOException
     */
    public List<Path> findInputFiles(String possibleParentDir, String guaranteedDir)
            throws IOException
    {
        List<Path> inputFileList = new ArrayList<>();

        // determine which file path contains the data files
        logger.info("Determine if input files exists within [{}]", possibleParentDir);
        Path inputDir = Paths.get(possibleParentDir);
        if (this.isDirectoryValid(inputDir))
        {
            inputDir = Paths.get(possibleParentDir, guaranteedDir);
            if (this.isDirectoryValid(inputDir))
            {
                inputFileList = this.getFiles(inputDir);
            }
            else
            {
                throw new IOException(
                        String.format(Constants.DIRECTORY_ERROR, inputDir.toString()));
            }
        }
        else
        {
            logger.info("Determine if input files exists within [{}]", guaranteedDir);
            inputDir = Paths.get(guaranteedDir);
            if (this.isDirectoryValid(inputDir))
            {
                inputFileList = this.getFiles(inputDir);
            }
            else
            {
                throw new IOException(
                        String.format(Constants.DIRECTORY_ERROR, inputDir.toString()));
            }
        }

        return inputFileList;
    }

    /**
     * Get files from specified path
     * 
     * @param inputDir
     * @return
     * @throws IOException
     */
    private List<Path> getFiles(Path inputDir) throws IOException
    {
        List<Path> fileList = new ArrayList<>();
        Path dataFilePath = Paths.get(inputDir.toString(), Constants.DATA_FILE_PATH);
        Path exclusionFilePath = Paths.get(inputDir.toString(),
                Constants.EXCLUSION_FILE_PATH);
        if (this.isFileValid(dataFilePath) && this.isFileValid(exclusionFilePath))
        {
            fileList.add(dataFilePath);
            fileList.add(exclusionFilePath);
        }
        else
        {
            throw new IOException(String.format(Constants.FILE_ERROR,
                    dataFilePath.toString(), exclusionFilePath.toString()));
        }
        return fileList;
    }

    /**
     * Verify if path exists and is a directory
     * 
     * @param path
     * @return
     */
    private boolean isDirectoryValid(Path path)
    {
        boolean isValid = false;
        if (Files.exists(path) && Files.isDirectory(path))
        {
            logger.info("Directory is VALID: [{}]", path.toString());
            isValid = true;
        }
        else
        {
            logger.warn("Directory is INVALID: [{}]", path.toString());
        }
        return isValid;
    }

    /**
     * Verify if path exists and is a file
     * 
     * @param path
     * @return
     */
    private boolean isFileValid(Path path)
    {
        boolean isValid = false;
        if (Files.exists(path) && Files.isRegularFile(path))
        {
            logger.info("File is VALID: [{}]", path.toString());
            isValid = true;
        }
        else
        {
            logger.warn("File is INVALID: [{}]", path.toString());
        }
        return isValid;
    }
}
