package com.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.data.CsvFactory.FILETYPE;
import com.generator.SecretSanta;
import com.gui.SecretSantaDisplayType;
import com.opencsv.CSVReader;

import javafx.beans.property.SimpleStringProperty;

/**
 * Unit tests for {@link DataReader}}
 */
public class DataReaderTest
{
    private static final String TEST_DATA1_FILE_PATH = "/test_data1.csv";
    private static final String TEST_DATA2_FILE_PATH = "/test_data2.csv";
    private static final String TEST_EXCLUSIONS1_FILE_PATH = "/test_exclusions1.csv";
    private static final String TEST_EXCLUSIONS2_FILE_PATH = "/test_exclusions2.csv";
    private static final Logger logger = LoggerFactory.getLogger(DataReaderTest.class);

    /**
     * Reads in test data files
     * 
     * @throws Exception
     */
    @Test
    public void test_parseDataFileWithExclusionFile() throws Exception
    {
        // ===== set up data =====
        File dataFile = new File(getClass().getResource(TEST_DATA2_FILE_PATH).getFile());
        File exclusionsFile = new File(
                getClass().getResource(TEST_EXCLUSIONS2_FILE_PATH).getFile());

        CsvFactory csvFactory = new CsvFactory(dataFile.getPath(),
                exclusionsFile.getPath(), null);
        ExclusionReader exclusionReader = new ExclusionReader(csvFactory);
        // object to be tested
        DataReader dataReader = new DataReader(csvFactory, exclusionReader);

        // ===== test call =====
        List<SecretSanta> secretSantaList = dataReader
                .parseDataFileWithExclusionFile(true);

        // ===== verification =====
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

        // verify size
        assertThat(secretSantaList, hasSize(10));

        // verify data
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

        logger.info("========== PASS test_parseDataFileWithExclusionFile ==========");
    }

    /**
     * Reads in test data files
     * 
     * @throws Exception
     */
    @Test
    public void test_parseDataFileWithExclusionFileForExclusionDialog() throws Exception
    {
        // ===== set up data =====
        File dataFile = new File(getClass().getResource(TEST_DATA2_FILE_PATH).getFile());
        File exclusionsFile = new File(
                getClass().getResource(TEST_EXCLUSIONS2_FILE_PATH).getFile());

        CsvFactory csvFactory = new CsvFactory(dataFile.getPath(),
                exclusionsFile.getPath(), null);
        ExclusionReader exclusionReader = new ExclusionReader(csvFactory);
        // object to be tested
        DataReader dataReader = new DataReader(csvFactory, exclusionReader);

        // ===== test call =====
        List<SecretSanta> secretSantaList = dataReader
                .parseDataFileWithExclusionFile(false);

        // ===== verification =====
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
        final List<String> testName2ExcludeList = new ArrayList<String>(
                Arrays.asList(TESTNAME1));
        final List<String> testName3ExcludeList = new ArrayList<String>(
                Arrays.asList(TESTNAME1, TESTNAME2));
        final List<String> testName4ExcludeList = new ArrayList<String>(
                Arrays.asList(TESTNAME1, TESTNAME2, TESTNAME3));

        // verify size
        assertThat(secretSantaList, hasSize(10));

        // verify data
        assertThat(secretSantaList,
                hasItem(new SecretSanta(TESTNAME1, new ArrayList<String>())));
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

        logger.info(
                "========== PASS test_parseDataFileWithExclusionFileForExclusionDialog ==========");
    }

    /**
     * Reads in test data files
     * 
     * @throws Exception
     */
    @Test
    public void test_parseYearData() throws Exception
    {
        // ===== set up data =====
        File dataFile = new File(getClass().getResource(TEST_DATA2_FILE_PATH).getFile());
        File exclusionsFile = new File(
                getClass().getResource(TEST_EXCLUSIONS2_FILE_PATH).getFile());

        CsvFactory csvFactory = new CsvFactory(dataFile.getPath(),
                exclusionsFile.getPath(), null);
        ExclusionReader exclusionReader = new ExclusionReader(csvFactory);
        // object to be tested
        DataReader dataReader = new DataReader(csvFactory, exclusionReader);

        // ===== test call =====
        List<String> yearList = dataReader.parseYearData();

        // ===== verification =====
        final String YEAR1 = "2009";
        final String YEAR2 = "2010";
        final String YEAR3 = "2011";

        // verify size
        assertThat(yearList, hasSize(3));

        // verify data. "contains" verifies order
        assertThat(yearList, contains(YEAR1, YEAR2, YEAR3));

        logger.info("========== PASS test_parseYearData ==========");
    }

    /**
     * Reads in test data files
     * 
     * @throws Exception
     */
    @Test
    public void test_sequentialReadCalls() throws Exception
    {
        // ===== set up data =====
        File dataFile = new File(getClass().getResource(TEST_DATA2_FILE_PATH).getFile());
        File exclusionsFile = new File(
                getClass().getResource(TEST_EXCLUSIONS2_FILE_PATH).getFile());

        CsvFactory csvFactory = new CsvFactory(dataFile.getPath(),
                exclusionsFile.getPath(), null);
        ExclusionReader exclusionReader = new ExclusionReader(csvFactory);
        // object to be tested
        DataReader dataReader = new DataReader(csvFactory, exclusionReader);

        // ===== test calls =====
        List<SecretSanta> secretSantaList = dataReader
                .parseDataFileWithExclusionFile(true);
        List<String> yearList = dataReader.parseYearData();

        // ===== verification =====
        assertThat(secretSantaList, hasSize(10));
        assertThat(yearList, hasSize(3));

        logger.info("========== PASS test_sequentialReadCalls ==========");
    }

    /**
     * Mocks data
     * 
     * @throws Exception
     */
    @Test
    public void test_getNumberYearsCompleted_mockTest() throws Exception
    {
        // ===== set up mocks =====
        ExclusionReader mock_exclusionReader = Mockito.mock(ExclusionReader.class);
        CsvFactory mock_csvFactory = Mockito.mock(CsvFactory.class);
        CSVReader mock_dataCsvReader = Mockito.mock(CSVReader.class);

        final String[] line1 =
        { "#NAME", "2011", "2012", "2013", "2014", "2015" };
        when(mock_dataCsvReader.readNext()).thenReturn(line1);
        when(mock_csvFactory.createCsvReader(eq(FILETYPE.DATA)))
                .thenReturn(mock_dataCsvReader);

        // object to be tested
        DataReader dataReader = new DataReader(mock_csvFactory, mock_exclusionReader);

        // ===== test call =====
        int yearCount = dataReader.getNumberYearsCompleted();

        // ===== verification =====
        assertThat(yearCount, equalTo(5));
        verify(mock_csvFactory, times(1)).createCsvReader(eq(FILETYPE.DATA));
        verify(mock_dataCsvReader, times(1)).readNext();
        verify(mock_dataCsvReader, times(1)).close();

        logger.info("========== PASS test_getNumberYearsCompleted_mockTest ==========");
    }

    /**
     * Test used for learning code behavior
     * 
     * @throws Exception
     */
    @Test
    public void test_generateCodeBehaviorTesting() throws Exception
    {
        List<SecretSantaDisplayType> displayList = new ArrayList<>();
        SecretSantaDisplayType display1 = new SecretSantaDisplayType("name1",
                new ArrayList<String>(Arrays.asList("history1", "rigged1")),
                new ArrayList<String>());
        SecretSantaDisplayType display2 = new SecretSantaDisplayType("name2",
                new ArrayList<String>(Arrays.asList("history2", "")), // empty
                new ArrayList<String>());
        SecretSantaDisplayType display3 = new SecretSantaDisplayType("name3",
                new ArrayList<String>(Arrays.asList("history3", "")), // empty
                new ArrayList<String>());
        SecretSantaDisplayType display4 = new SecretSantaDisplayType("name4",
                new ArrayList<String>(Arrays.asList("history4", "rigged4")),
                new ArrayList<String>());
        displayList.add(display1);
        displayList.add(display2);
        displayList.add(display3);
        displayList.add(display4);
        
        // test creating map
        int index = 1;
        Map<String, String> nameToOverriddenSelectedNameMap = displayList.stream()
                .filter(type -> !type.getSecretSantaList().get(index).getValue().isEmpty())
                .collect(Collectors.toMap(SecretSantaDisplayType::getName,
                        type -> type.getSecretSantaList().get(index).getValue()));

        logger.info("map output: {}", nameToOverriddenSelectedNameMap);
        logger.info("========== PASS test_generateCodeBehaviorTesting ==========");
    }
}
