package com.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hamcrest.collection.IsMapContaining;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExclusionReaderTest
{
    private static final String TEST_EXCLUSIONS1_FILE_PATH = "/test_exclusions1.csv";
    private static final String TEST_EXCLUSIONS2_FILE_PATH = "/test_exclusions2.csv";
    private static final Logger logger = LoggerFactory
            .getLogger(ExclusionReaderTest.class);

    /**
     * Object to be tested
     */
    private ExclusionReader exclusionReader;

    /**
     * Set up called before each test case method
     */
    @Before
    public void setUp()
    {
    }

    @Test
    public void testExclusionMap() throws Exception
    {
        // ===== set up data =====
        File exclusionsFile = new File(
                getClass().getResource(TEST_EXCLUSIONS2_FILE_PATH).getFile());

        CsvFactory csvFactory = new CsvFactory(null, exclusionsFile.getPath(), null);
        this.exclusionReader = new ExclusionReader(csvFactory);

        // ===== test call =====
        Map<String, List<String>> nameToExclusionListMap = this.exclusionReader
                .getExclusionListDataFromFile();

        // ===== verification =====
        nameToExclusionListMap.forEach((name, exclusionList) -> logger
                .info("name [{}] ////// values [{}]", name, exclusionList));

        final String TESTNAME1 = "TESTNAME1";
        final String TESTNAME2 = "TESTNAME2";
        final String TESTNAME3 = "TESTNAME3";
        final String TESTNAME4 = "TESTNAME4";
        final String TESTNAME5 = "TESTNAME5";
        final List<String> emptyList = new ArrayList<String>();
        final List<String> testName2EntryList = Arrays.asList(TESTNAME1);
        final List<String> testName3EntryList = Arrays.asList(TESTNAME1, TESTNAME2);
        final List<String> testName4EntryList = Arrays.asList(TESTNAME1, TESTNAME2,
                TESTNAME3);

        // test map size
        assertThat(nameToExclusionListMap.size(), equalTo(5));

        // test map keys
        assertThat(nameToExclusionListMap, IsMapContaining.hasKey(TESTNAME1));
        assertThat(nameToExclusionListMap, IsMapContaining.hasKey(TESTNAME2));
        assertThat(nameToExclusionListMap, IsMapContaining.hasKey(TESTNAME3));
        assertThat(nameToExclusionListMap, IsMapContaining.hasKey(TESTNAME4));
        assertThat(nameToExclusionListMap, IsMapContaining.hasKey(TESTNAME5));

        // test map entries
        assertThat(nameToExclusionListMap,
                IsMapContaining.hasEntry(TESTNAME1, emptyList));
        assertThat(nameToExclusionListMap,
                IsMapContaining.hasEntry(TESTNAME2, testName2EntryList));
        assertThat(nameToExclusionListMap,
                IsMapContaining.hasEntry(TESTNAME3, testName3EntryList));
        assertThat(nameToExclusionListMap,
                IsMapContaining.hasEntry(TESTNAME4, testName4EntryList));
        assertThat(nameToExclusionListMap,
                IsMapContaining.hasEntry(TESTNAME5, emptyList));

        logger.info("========== PASS testExclusionMap ==========");
    }
}
