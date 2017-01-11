package com.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.data.CsvFactory.FILETYPE;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class DataRecorderTest
{
    private static final String TEST_DATA1_FILE_PATH = "/test_data1.csv";
    private static final String TEST_DATA2_FILE_PATH = "/test_data2.csv";
    private static final String TEST_EXCLUSIONS1_FILE_PATH = "/test_exclusions1.csv";
    private static final String TEST_EXCLUSIONS2_FILE_PATH = "/test_exclusions2.csv";
    private static final String TEST_OUTPUT_FILE_PATH = "./test_output.csv";
    private static final Logger logger = LoggerFactory.getLogger(DataRecorderTest.class);

    /**
     * Object to be tested
     */
    private DataRecorder dataRecorder;

    /**
     * Set up called before each test case method
     */
    @Before
    public void setUp()
    {
    }

    /**
     * Reads in test data for dataCsvReader
     * 
     * Mocks output writer (and everything else)
     * 
     * TODO convert a lot of common code to a method for easier testing
     * 
     * @throws Exception
     */
    @Test
    public void test_saveGenerationResults() throws Exception
    {
        // ===== set up data =====
        File dataFile = new File(getClass().getResource(TEST_DATA2_FILE_PATH).getFile());
        CSVReader dataCsvReader = new CSVReader(new FileReader(dataFile.getPath()));

        // mock objects used for saveGenerationResults
        CSVWriter mock_outputCsvWriter = mock(CSVWriter.class);
        CsvFactory mock_csvFactory = mock(CsvFactory.class);

        // return real CSVReader instance
        when(mock_csvFactory.createCsvReader(eq(FILETYPE.DATA)))
                .thenReturn(dataCsvReader);
        // return mock CSVWriter (used for verification)
        when(mock_csvFactory.createCsvWriter(eq(FILETYPE.OUTPUT)))
                .thenReturn(mock_outputCsvWriter);

        // mock objects NOT used for saveGenerationResults (no need for verification)
        DataReader mock_dataReader = mock(DataReader.class);

        this.dataRecorder = new DataRecorder(mock_csvFactory, mock_dataReader);

        // create test output
        Map<String, String> testMap = createTestAttendeeToResultMap();

        // ===== test call =====
        dataRecorder.saveGenerationResults(testMap);

        // ===== verification =====
        verify(mock_outputCsvWriter, times(1)).close();

        // verify writeNext was called 11 times: 1 for the header and 10 for existing names.
        // capture the arguments.
        ArgumentCaptor<String[]> writeNextCaptor = ArgumentCaptor
                .forClass(String[].class);
        verify(mock_outputCsvWriter, times(11))
                .writeNext(writeNextCaptor.capture());

        // verify each captured argument
        final String TESTNAME1 = "testName1";
        final String TESTNAME2 = "testName2";
        final String TESTNAME3 = "testName3";
        final String TESTNAME4 = "testName4";
        final String TESTNAME5 = "testName5";
        final String TESTNAME6 = "testName6";
        final String TESTNAME7 = "testName7";
        final String TESTNAME8 = "testName8";
        final String TESTNAME9 = "testName9";
        final String TESTNAME10 = "testName10";
        final String EMPTYTOKEN = "";
        final String[] line0 =
        { "#NAME", "2009", "2010", "2011", "2012" }; // header with 2012 appended
        final String[] line1 =
        { TESTNAME1, TESTNAME2, TESTNAME3, TESTNAME4, TESTNAME8 };
        final String[] line2 =
        { TESTNAME2, TESTNAME3, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN };
        final String[] line3 =
        { TESTNAME3, EMPTYTOKEN, TESTNAME4, EMPTYTOKEN, EMPTYTOKEN };
        final String[] line4 =
        { TESTNAME4, EMPTYTOKEN, EMPTYTOKEN, TESTNAME5, EMPTYTOKEN };
        final String[] line5 =
        { TESTNAME5, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN };
        final String[] line6 =
        { TESTNAME6, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN };
        final String[] line7 =
        { TESTNAME7, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN, TESTNAME9 };
        final String[] line8 =
        { TESTNAME8, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN };
        final String[] line9 =
        { TESTNAME9, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN, TESTNAME10 };
        final String[] line10 =
        { TESTNAME10, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN };

        List<String[]> capturedArguments = writeNextCaptor.getAllValues();
        assertThat(capturedArguments, hasSize(11));
        assertThat(capturedArguments, hasItem(line0));
        assertThat(capturedArguments, hasItem(line1));
        assertThat(capturedArguments, hasItem(line2));
        assertThat(capturedArguments, hasItem(line3));
        assertThat(capturedArguments, hasItem(line4));
        assertThat(capturedArguments, hasItem(line5));
        assertThat(capturedArguments, hasItem(line6));
        assertThat(capturedArguments, hasItem(line7));
        assertThat(capturedArguments, hasItem(line8));
        assertThat(capturedArguments, hasItem(line9));
        assertThat(capturedArguments, hasItem(line10));

        logger.info("========== PASS testSave1 ==========");
    }

    @Test
    public void test_saveGenerationResults_withNewcomers() throws Exception
    {
        // ===== set up data =====
        File dataFile = new File(getClass().getResource(TEST_DATA2_FILE_PATH).getFile());
        CSVReader dataCsvReader = new CSVReader(new FileReader(dataFile.getPath()));

        // mock objects used for saveGenerationResults
        CSVWriter mock_outputCsvWriter = mock(CSVWriter.class);
        CsvFactory mock_csvFactory = mock(CsvFactory.class);

        // return real CSVReader instance
        when(mock_csvFactory.createCsvReader(eq(FILETYPE.DATA)))
                .thenReturn(dataCsvReader);
        // return mock CSVWriter (used for verification)
        when(mock_csvFactory.createCsvWriter(eq(FILETYPE.OUTPUT)))
                .thenReturn(mock_outputCsvWriter);

        // mock objects NOT used for saveGenerationResults (no need for verification)
        DataReader mock_dataReader = mock(DataReader.class);

        this.dataRecorder = new DataRecorder(mock_csvFactory, mock_dataReader);

        // create test output
        Map<String, String> testMap = createTestAttendeeToResultMapWithNewcomers();

        // ===== test call =====
        dataRecorder.saveGenerationResults(testMap);

        // ===== verification =====
        verify(mock_outputCsvWriter, times(1)).close();

        // verify writeNext was called 13 times: 1 for the header, 10 for existing names,
        // and 2 for newcomer names.
        // capture the arguments.
        ArgumentCaptor<String[]> writeNextCaptor = ArgumentCaptor
                .forClass(String[].class);
        verify(mock_outputCsvWriter, times(13))
                .writeNext(writeNextCaptor.capture());

        // verify each captured argument
        final String TESTNAME1 = "testName1";
        final String TESTNAME2 = "testName2";
        final String TESTNAME3 = "testName3";
        final String TESTNAME4 = "testName4";
        final String TESTNAME5 = "testName5";
        final String TESTNAME6 = "testName6";
        final String TESTNAME7 = "testName7";
        final String TESTNAME8 = "testName8";
        final String TESTNAME9 = "testName9";
        final String TESTNAME10 = "testName10";
        final String NEWCOMER1 = "newcomer1";
        final String NEWCOMER2 = "newcomer2";
        final String EMPTYTOKEN = "";
        final String[] line0 =
        { "#NAME", "2009", "2010", "2011", "2012" }; // header with 2012 appended
        final String[] line1 =
        { TESTNAME1, TESTNAME2, TESTNAME3, TESTNAME4, TESTNAME8 };
        final String[] line2 =
        { TESTNAME2, TESTNAME3, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN };
        final String[] line3 =
        { TESTNAME3, EMPTYTOKEN, TESTNAME4, EMPTYTOKEN, EMPTYTOKEN };
        final String[] line4 =
        { TESTNAME4, EMPTYTOKEN, EMPTYTOKEN, TESTNAME5, EMPTYTOKEN };
        final String[] line5 =
        { TESTNAME5, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN };
        final String[] line6 =
        { TESTNAME6, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN };
        final String[] line7 =
        { TESTNAME7, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN };
        final String[] line8 =
        { TESTNAME8, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN };
        final String[] line9 =
        { TESTNAME9, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN };
        final String[] line10 =
        { TESTNAME10, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN, TESTNAME9 };
        final String[] newcomerLine1 =
        { NEWCOMER1, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN, TESTNAME1 };
        final String[] newcomerLine2 =
        { NEWCOMER2, EMPTYTOKEN, EMPTYTOKEN, EMPTYTOKEN, NEWCOMER1 };

        List<String[]> capturedArguments = writeNextCaptor.getAllValues();
        assertThat(capturedArguments, hasSize(13));
        assertThat(capturedArguments, hasItem(line0));
        assertThat(capturedArguments, hasItem(line1));
        assertThat(capturedArguments, hasItem(line2));
        assertThat(capturedArguments, hasItem(line3));
        assertThat(capturedArguments, hasItem(line4));
        assertThat(capturedArguments, hasItem(line5));
        assertThat(capturedArguments, hasItem(line6));
        assertThat(capturedArguments, hasItem(line7));
        assertThat(capturedArguments, hasItem(line8));
        assertThat(capturedArguments, hasItem(line9));
        assertThat(capturedArguments, hasItem(line10));
        assertThat(capturedArguments, hasItem(newcomerLine1));
        assertThat(capturedArguments, hasItem(newcomerLine2));

        logger.info("========== PASS testSave2 ==========");
    }

    //    @Test
    //    public void debugConverterTest()
    //    {
    //        DataRecorder dataRecorder = new DataRecorder();
    //        List<SecretSantaDisplayType> testList = createTestSecretSantaDisplayList();
    //        try
    //        {
    //            dataRecorder.debugConverter(Constants.EXCLUSION_FILE_PATH,
    //                    "resources/output.csv");
    //
    //            // TODO add checks later if every row matches same amount of entries
    //        }
    //        catch (IOException e)
    //        {
    //            // TODO Auto-generated catch block
    //            e.printStackTrace();
    //            fail("File not found or file cannot be written");
    //        }
    //    }

    // TODO create a general method to create results that make sense.
    // current they don't because the attendees and results aren't matching
    private Map<String, String> createTestAttendeeToResultMap()
    {
        Map<String, String> attendeeToResultMap = new HashMap<>();
        attendeeToResultMap.put("testName1", "testName8");
        attendeeToResultMap.put("testName7", "testName9");
        attendeeToResultMap.put("testName9", "testName10");

        return attendeeToResultMap;
    }

    private Map<String, String> createTestAttendeeToResultMapWithNewcomers()
    {
        Map<String, String> attendeeToResultMap = new HashMap<>();
        attendeeToResultMap.put("testName1", "testName8");
        attendeeToResultMap.put("testName10", "testName9");
        attendeeToResultMap.put("newcomer1", "testName1");
        attendeeToResultMap.put("newcomer2", "newcomer1");

        return attendeeToResultMap;
    }
}
