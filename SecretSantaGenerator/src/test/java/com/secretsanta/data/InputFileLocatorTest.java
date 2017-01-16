package com.secretsanta.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secretsanta.gui.Constants;

/**
 * Unit tests for {@link InputFileLocator}
 */
public class InputFileLocatorTest
{
    private static final String TESTFOLDER_1 = "testFolder1";
    private static final String TESTFOLDER_2 = "testFolder2";
    private static final String BADFOLDER = "badFolder";
    private static final String BADFILE = "badFile.txt";

    private static final String TEST_DIRECTORY_ERROR = "Error reading directory";
    private static final String TEST_FILE_ERROR = "Error reading files";

    private static final Logger logger = LoggerFactory
            .getLogger(InputFileLocatorTest.class);

    /**
     * Object to be tested
     */
    private InputFileLocator inputFileLocator;

    /**
     * Rule that is reinstantiated to this set value before each test.
     * 
     * Used to create temporary folders and files for testing
     */
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Rule that is reinstantiated to this set value before each test.
     * 
     * Used to set up expected exceptions
     */
    @Rule
    public ExpectedException expectedExcletion = ExpectedException.none();

    /**
     * Set up called before each test case method
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.inputFileLocator = new InputFileLocator();
    }

    /**
     * Test with temporary folders and files
     * 
     * @throws Exception
     */
    @Test
    public void test_validPossibleParentDir_success() throws Exception
    {
        // ===== set up data =====
        File createdFolder1 = this.temporaryFolder.newFolder(TESTFOLDER_1);
        this.temporaryFolder.newFolder(TESTFOLDER_1, TESTFOLDER_2);
        this.temporaryFolder.newFile(
                TESTFOLDER_1 + "/" + TESTFOLDER_2 + "/" + Constants.DATA_FILE_PATH);
        this.temporaryFolder.newFile(
                TESTFOLDER_1 + "/" + TESTFOLDER_2 + "/" + Constants.EXCLUSION_FILE_PATH);

        // ===== test call =====
        List<Path> inputFileList = this.inputFileLocator
                .findInputFiles(createdFolder1.getPath(), TESTFOLDER_2);

        // ===== verification =====
        assertThat(inputFileList, hasSize(2));
        Path dataPath = inputFileList.stream()
                .filter(file -> file.toString().contains(Constants.DATA_FILE_PATH))
                .findAny().orElse(null);
        Path exclusionPath = inputFileList.stream()
                .filter(file -> file.toString().contains(Constants.EXCLUSION_FILE_PATH))
                .findAny().orElse(null);
        assertThat(dataPath, notNullValue());
        assertThat(exclusionPath, notNullValue());

        logger.info("========== PASS test_validPossibleParentDir_success ==========");
    }

    /**
     * Test with temporary folders and files
     * 
     * @throws Exception
     */
    @Test
    public void test_validPossibleParentDir_fail_invalidGuaranteedDir() throws Exception
    {
        // ===== set up data =====
        File createdFolder1 = this.temporaryFolder.newFolder(TESTFOLDER_1);
        this.temporaryFolder.newFolder(TESTFOLDER_1, BADFOLDER);

        // ===== verification =====
        this.expectedExcletion.expect(IOException.class);
        this.expectedExcletion.expectMessage(containsString(TEST_DIRECTORY_ERROR));

        // ===== test call =====
        this.inputFileLocator.findInputFiles(createdFolder1.getPath(), TESTFOLDER_2);

        logger.info(
                "========== PASS test_validPossibleParentDir_fail_invalidGuaranteedDir ==========");
    }

    /**
     * Test with temporary folders and files
     * 
     * @throws Exception
     */
    @Test
    public void test_validPossibleParentDir_fail_invalidFile() throws Exception
    {
        // ===== set up data =====
        File createdFolder1 = this.temporaryFolder.newFolder(TESTFOLDER_1);
        this.temporaryFolder.newFolder(TESTFOLDER_1, TESTFOLDER_2);
        this.temporaryFolder.newFile(
                TESTFOLDER_1 + "/" + TESTFOLDER_2 + "/" + Constants.DATA_FILE_PATH);
        this.temporaryFolder.newFile(TESTFOLDER_1 + "/" + TESTFOLDER_2 + "/" + BADFILE);

        // ===== verification =====
        this.expectedExcletion.expect(IOException.class);
        this.expectedExcletion.expectMessage(containsString(TEST_FILE_ERROR));

        // ===== test call =====
        this.inputFileLocator.findInputFiles(createdFolder1.getPath(), TESTFOLDER_2);
        logger.info(
                "========== PASS test_validPossibleParentDir_fail_invalidFile ==========");
    }

    /**
     * Test with temporary folders and files
     * 
     * @throws Exception
     */
    @Test
    public void test_validGuaranteedDir_success() throws Exception
    {
        // ===== set up data =====
        File createdFolder2 = this.temporaryFolder.newFolder(TESTFOLDER_2);
        this.temporaryFolder.newFile(TESTFOLDER_2 + "/" + Constants.DATA_FILE_PATH);
        this.temporaryFolder.newFile(TESTFOLDER_2 + "/" + Constants.EXCLUSION_FILE_PATH);

        // ===== test call =====
        List<Path> inputFileList = this.inputFileLocator.findInputFiles(BADFOLDER,
                createdFolder2.getPath());

        // ===== verification =====
        assertThat(inputFileList, hasSize(2));
        Path dataPath = inputFileList.stream()
                .filter(file -> file.toString().contains(Constants.DATA_FILE_PATH))
                .findAny().orElse(null);
        Path exclusionPath = inputFileList.stream()
                .filter(file -> file.toString().contains(Constants.EXCLUSION_FILE_PATH))
                .findAny().orElse(null);
        assertThat(dataPath, notNullValue());
        assertThat(exclusionPath, notNullValue());

        logger.info("========== PASS test_validGuaranteedDir_success ==========");
    }

    /**
     * Test with temporary folders and files
     * 
     * @throws Exception
     */
    @Test
    public void test_validGuaranteedDir_fail_invalidFile() throws Exception
    {
        // ===== set up data =====
        File createdFolder2 = this.temporaryFolder.newFolder(TESTFOLDER_2);
        this.temporaryFolder.newFile(TESTFOLDER_2 + "/" + BADFILE);
        this.temporaryFolder.newFile(TESTFOLDER_2 + "/" + Constants.EXCLUSION_FILE_PATH);

        // ===== verification =====
        this.expectedExcletion.expect(IOException.class);
        this.expectedExcletion.expectMessage(containsString(TEST_FILE_ERROR));

        // ===== test call =====
        this.inputFileLocator.findInputFiles(BADFOLDER, createdFolder2.getPath());
        logger.info(
                "========== PASS test_validGuaranteedDir_fail_invalidFile ==========");
    }

    /**
     * Test with temporary folders and files
     * 
     * @throws Exception
     */
    @Test
    public void test_noValidDirs() throws Exception
    {
        // ===== verification =====
        this.expectedExcletion.expect(IOException.class);
        this.expectedExcletion.expectMessage(containsString(TEST_DIRECTORY_ERROR));

        // ===== test call =====
        this.inputFileLocator.findInputFiles(TESTFOLDER_1, TESTFOLDER_2);
        logger.info("========== PASS test_noValidDirs ==========");
    }
}
