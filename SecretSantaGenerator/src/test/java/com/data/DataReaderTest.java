package com.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generator.SecretSanta;
import com.opencsv.CSVReader;

public class DataReaderTest
{
    private static final String TEST_DATA1_FILE_PATH = "/test_data1.csv";
    private static final String TEST_DATA2_FILE_PATH = "/test_data2.csv";
    private static final String TEST_EXCLUSIONS1_FILE_PATH = "/test_exclusions1.csv";
    private static final String TEST_EXCLUSIONS2_FILE_PATH = "/test_exclusions2.csv";
    private static final Logger logger = LoggerFactory.getLogger(DataReaderTest.class);

    /**
     * Object to be tested
     */
    private DataReader dataReader;

    /**
     * Used as a real instance (not mocked)
     */
    private ExclusionReader exclusionReader;

    /**
     * Set up called before each test case method
     */
    @Before
    public void setUp()
    {
        this.dataReader = new DataReader();
        this.exclusionReader = new ExclusionReader();
    }

    @Test
    public void testDataReader() throws IOException
    {
        File dataFile = new File(getClass().getResource(TEST_DATA2_FILE_PATH).getFile());
        CSVReader dataCsvReader = new CSVReader(new FileReader(dataFile.getPath()));
        File exclusionsFile = new File(
                getClass().getResource(TEST_EXCLUSIONS2_FILE_PATH).getFile());
        CSVReader exclusionCsvReader = new CSVReader(
                new FileReader(exclusionsFile.getPath()));

        List<SecretSanta> secretSantaList = new ArrayList<SecretSanta>();

        secretSantaList = this.dataReader.parseDataFileWithExclusionFile(dataCsvReader,
                exclusionCsvReader, this.exclusionReader);

        final String TESTNAME1 = "TESTNAME1";
        final String TESTNAME2 = "TESTNAME2";
        final String TESTNAME3 = "TESTNAME3";
        final String TESTNAME4 = "TESTNAME4";
        final String TESTNAME5 = "TESTNAME5";
        final String TESTNAME6 = "TESTNAME6";
        final String TESTNAME7 = "TESTNAME7";
        final String TESTNAME8 = "TESTNAME8";
        final String TESTNAME9 = "TESTNAME9";
        final String TESTNAME10 = "TESTNAME10";
        final List<String> testName1ExcludeList = new ArrayList<String>(
                Arrays.asList(TESTNAME2, TESTNAME3, TESTNAME4));
        final List<String> testName2ExcludeList = new ArrayList<String>(
                Arrays.asList(TESTNAME1, TESTNAME3));
        final List<String> testName3ExcludeList = new ArrayList<String>(
                Arrays.asList(TESTNAME1, TESTNAME2, TESTNAME4));
        final List<String> testName4ExcludeList = new ArrayList<String>(
                Arrays.asList(TESTNAME1, TESTNAME2, TESTNAME3, TESTNAME5));

        // test size
        assertThat(secretSantaList, hasSize(10));

        // test data
        assertThat(secretSantaList,
                hasItem(new SecretSanta(TESTNAME1, testName1ExcludeList)));
        assertThat(secretSantaList,
                hasItem(new SecretSanta(TESTNAME2, testName2ExcludeList)));
        assertThat(secretSantaList,
                hasItem(new SecretSanta(TESTNAME3, testName3ExcludeList)));
        assertThat(secretSantaList,
                hasItem(new SecretSanta(TESTNAME4, testName4ExcludeList)));
        // an empty list has to be instantiated for each test due to SecretSanta modifying it
        assertThat(secretSantaList,
                hasItem(new SecretSanta(TESTNAME5, new ArrayList<String>())));
        assertThat(secretSantaList,
                hasItem(new SecretSanta(TESTNAME6, new ArrayList<String>())));
        assertThat(secretSantaList,
                hasItem(new SecretSanta(TESTNAME7, new ArrayList<String>())));
        assertThat(secretSantaList,
                hasItem(new SecretSanta(TESTNAME8, new ArrayList<String>())));
        assertThat(secretSantaList,
                hasItem(new SecretSanta(TESTNAME9, new ArrayList<String>())));
        assertThat(secretSantaList,
                hasItem(new SecretSanta(TESTNAME10, new ArrayList<String>())));

        logger.info("========== PASS testDataReader ==========");
    }
}
