package com.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generator.SecretSanta;
import com.gui.Constants;
import com.gui.SecretSantaDisplayType;
import com.opencsv.CSVReader;

public class DataValidatorTest
{
    private static final String TEST_DATA2_PASS_FILE_PATH = "/validator_test_data1_pass.csv";
    private static final String TEST_DATA3_PASS_FILE_PATH = "/validator_test_data2_pass.csv";
    private static final String TEST_EXCLUSIONS2_PASS_FILE_PATH = "/validator_test_exclusions1_pass.csv";
    private static final String TEST_DATA2_FAIL_FILE_PATH = "/validator_test_data1_fail.csv";
    private static final String TEST_EXCLUSIONS2_FAIL_FILE_PATH = "/validator_test_exclusions1_fail.csv";
    private static final String TEST_EXCLUSIONS3_PASS_FILE_PATH = "/validator_test_exclusions2_pass.csv";
    
   

    private static final Logger logger = LoggerFactory.getLogger(DataValidatorTest.class);

    /**
     * Object to be tested
     */
    private DataValidatorInterface validator;

    /**
     * Used for test methods to expect an exception.
     * 
     * Rule annotation redefines this member before each test. If used, make
     * sure to set up AFTER the assert statements!
     */
    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Set up called before each test case method
     */
    @Before
    public void setUp()
    {
        this.validator = new DataValidator();
    }

    @Test
    public void test_isDataSynchronous_pass()
    {
        // ===== set up =====
        File dataFile = new File(
                getClass().getResource(TEST_DATA2_PASS_FILE_PATH).getFile());
        File exclusionsFile = new File(
                getClass().getResource(TEST_EXCLUSIONS2_PASS_FILE_PATH).getFile());

        // ===== test call =====
        boolean result = this.validator.isDataSynchronous(dataFile.getPath(),
                exclusionsFile.getPath());

        // ===== verification =====
        assertTrue(result);
    }

    @Test
    public void test_isDataSynchronous_fail()
    {
        // ===== set up =====
        File dataFile = new File(
                getClass().getResource(TEST_DATA2_FAIL_FILE_PATH).getFile());
        File exclusionsFile = new File(
                getClass().getResource(TEST_EXCLUSIONS2_FAIL_FILE_PATH).getFile());

        // ===== test call =====
        boolean result = this.validator.isDataSynchronous(dataFile.getPath(),
                exclusionsFile.getPath());

        // ===== verification =====
        assertFalse(result);
    }

    @Test
    public void test_areCollectionsValidAndEqual_pass()
    {
        // ===== set up =====
        Map<String, String> resultMap = new HashMap<>();

        resultMap.put("nameA", "nameB");
        resultMap.put("nameB", "nameA");

        // ===== test call =====
        boolean result = this.validator.areCollectionsValidAndEqual(resultMap.keySet(),
                resultMap.values());

        // ===== verification =====
        assertTrue(result);
    }

    @Test
    public void test_areCollectionsValidAndEqual_fail()
    {
        // ===== set up =====
        Map<String, String> resultMap = new HashMap<>();

        resultMap.put("nameA", "nameB");
        resultMap.put("nameB", "nameC");

        // ===== test call =====
        boolean result = this.validator.areCollectionsValidAndEqual(resultMap.keySet(),
                resultMap.values());

        // ===== verification =====
        assertFalse(result);
    }

    @Test
    public void test_areCollectionsValidAndEqual_fail_duplicate1()
    {
        // ===== set up =====
        final List<Integer> listA = new ArrayList<>(Arrays.asList(1, 2, 3, 1));
        final List<Integer> listB = new ArrayList<>(Arrays.asList(1, 2, 3));

        // ===== test call =====
        boolean result = this.validator.areCollectionsValidAndEqual(listA, listB);

        // ===== verification =====
        assertFalse(result);
    }

    @Test
    public void test_areCollectionsValidAndEqual_fail_duplicate2()
    {
        // ===== set up =====
        final List<Integer> listA = new ArrayList<>(Arrays.asList(1, 2, 3));
        final List<Integer> listB = new ArrayList<>(Arrays.asList(1, 2, 3, 3));

        // ===== test call =====
        boolean result = this.validator.areCollectionsValidAndEqual(listA, listB);

        // ===== verification =====
        assertFalse(result);
    }

    @Test
    public void test_areCollectionsValidAndEqual_exception1()
    {
        // ===== set up =====
        this.exception.expect(NullPointerException.class);
        this.exception.expectMessage(containsString("collectionA"));

        // ===== test call =====
        boolean result = this.validator.areCollectionsValidAndEqual(null,
                new ArrayList<String>());

        // ===== verification =====
        // no result because an exception is expected
    }

    @Test
    public void test_areCollectionsValidAndEqual_exception2()
    {
        // ===== set up =====
        this.exception.expect(NullPointerException.class);
        this.exception.expectMessage(containsString("collectionB"));

        // ===== test call =====
        boolean result = this.validator
                .areCollectionsValidAndEqual(new ArrayList<String>(), null);

        // ===== verification =====
        // no result because an exception is expected
    }
    
    @Test
    public void test_isValidGeneration_pass() throws FileNotFoundException, IOException
    {
        
     // ===== set up =====
        File dataFile = new File(
                getClass().getResource(TEST_DATA3_PASS_FILE_PATH).getFile());
       
        File exclusionsFile = new File(
                getClass().getResource(TEST_EXCLUSIONS3_PASS_FILE_PATH).getFile());
        
        DataReader dataReader = new DataReader();
        List<SecretSanta> secretSantaList = dataReader.parseDataFileWithExclusionFile(
                dataFile.getPath(), exclusionsFile.getPath());
        
        //creates an attendee to result map with no exclusions mapped to
        //an attendee
        Map<String, String> attendeeToResultMap = new HashMap<>();
        attendeeToResultMap.put("TESTNAME1","TESTNAME5");
        attendeeToResultMap.put("TESTNAME5","TESTNAME1");
        attendeeToResultMap.put("TESTNAME4","TESTNAME3");
        attendeeToResultMap.put("TESTNAME3","TESTNAME4");
        attendeeToResultMap.put("TESTNAME2","TESTNAME6");
        attendeeToResultMap.put("TESTNAME6","TESTNAME2");
        
      
        // ===== test call =====
        boolean result = this.validator.isValidGeneration(secretSantaList, attendeeToResultMap);
       
     // ===== verification =====
        assertTrue(result);
       


    }
    
    @Test
    public void test_isValidGeneration_fail() throws FileNotFoundException, IOException
    {
        
     // ===== set up =====
        File dataFile = new File(
                getClass().getResource(TEST_DATA3_PASS_FILE_PATH).getFile());
       
        File exclusionsFile = new File(
                getClass().getResource(TEST_EXCLUSIONS3_PASS_FILE_PATH).getFile());
        
        DataReader dataReader = new DataReader();
        List<SecretSanta> secretSantaList = dataReader.parseDataFileWithExclusionFile(
                dataFile.getPath(), exclusionsFile.getPath());
        
        //creates a attendee/result map specifically with exclusions assigned to
        //an attendee
        Map<String, String> attendeeToResultMap = new HashMap<>();
        attendeeToResultMap.put("TESTNAME1","TESTNAME5");
        attendeeToResultMap.put("TESTNAME5","TESTNAME1");
        attendeeToResultMap.put("TESTNAME4","TESTNAME3");
        attendeeToResultMap.put("TESTNAME3","TESTNAME2");
        attendeeToResultMap.put("TESTNAME2","TESTNAME6");
        attendeeToResultMap.put("TESTNAME6","TESTNAME4");
        
      
        // ===== test call =====
        boolean result = this.validator.isValidGeneration(secretSantaList, attendeeToResultMap);
       
     // ===== verification =====
        assertFalse(result);
       


    }
   
       

}
