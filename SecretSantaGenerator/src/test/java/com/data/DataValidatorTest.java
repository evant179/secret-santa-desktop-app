package com.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generator.SecretSanta;

public class DataValidatorTest
{
    private static final String TEST_DATA2_PASS_FILE_PATH = "/validator_test_data1_pass.csv";
    private static final String TEST_EXCLUSIONS2_PASS_FILE_PATH = "/validator_test_exclusions1_pass.csv";
    private static final String TEST_DATA2_FAIL_FILE_PATH = "/validator_test_data1_fail.csv";
    private static final String TEST_EXCLUSIONS2_FAIL_FILE_PATH = "/validator_test_exclusions1_fail.csv";
    private static final Logger logger = LoggerFactory.getLogger(DataValidatorTest.class);

    @Test
    public void test_verifyUniqueNamesFromDataFile_pass()
    {
        logger.info("RUNNING NEW UNIT TEST 1 HERE");
        DataValidatorInterface validator = new DataValidator();

        File dataFile = new File(
                getClass().getResource(TEST_DATA2_PASS_FILE_PATH).getFile());
        File exclusionsFile = new File(
                getClass().getResource(TEST_EXCLUSIONS2_PASS_FILE_PATH).getFile());

        boolean result = validator.verifyUniqueNamesFromDataFile(dataFile.getPath(),
                exclusionsFile.getPath());

        assertTrue(result);
    }

    @Test
    public void test_verifyUniqueNamesFromDataFile2_fail()
    {
        logger.info("RUNNING NEW UNIT TEST 2 HERE");
        DataValidatorInterface validator = new DataValidator();

        File dataFile = new File(
                getClass().getResource(TEST_DATA2_FAIL_FILE_PATH).getFile());
        File exclusionsFile = new File(
                getClass().getResource(TEST_EXCLUSIONS2_FAIL_FILE_PATH).getFile());

        boolean result = validator.verifyUniqueNamesFromDataFile(dataFile.getPath(),
                exclusionsFile.getPath());

        assertFalse(result);
    }

    @Test
    public void test_verifyResultsAccountedFor_pass()
    {
        logger.info("RUNNING NEW UNIT TEST 3 HERE");
        DataValidatorInterface validator = new DataValidator();
        Map<String, String> resultMap = new HashMap<>();
        
        resultMap.put("nameA", "nameB");
        resultMap.put("nameB", "nameA");
        
        boolean result = validator.verifyResultsAccountedFor(resultMap);

        assertTrue(result);
    }
    
    @Test
    public void test_verifyResultsAccountedFor_fail()
    {
        logger.info("RUNNING NEW UNIT TEST 4 HERE");
        DataValidatorInterface validator = new DataValidator();
        Map<String, String> resultMap = new HashMap<>();
        
        resultMap.put("nameA", "nameB");
        resultMap.put("nameB", "nameC");
        
        boolean result = validator.verifyResultsAccountedFor(resultMap);

        assertFalse(result);
    }
}
