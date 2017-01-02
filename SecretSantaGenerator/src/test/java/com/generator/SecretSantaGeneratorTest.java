package com.generator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.data.DataReader;
import com.data.DataReaderTest;
import com.data.ExclusionReader;
import com.gui.Constants;
import com.gui.SecretSantaDisplayType;
import com.opencsv.CSVReader;

/**
 * Generator for secret santas
 */
public class SecretSantaGeneratorTest
{
    private static final String TEST_DATA1_FILE_PATH = "/test_data1.csv";
    private static final String TEST_DATA2_FILE_PATH = "/test_data2.csv";
    private static final String TEST_EXCLUSIONS1_FILE_PATH = "/test_exclusions1.csv";
    private static final String TEST_EXCLUSIONS2_FILE_PATH = "/test_exclusions2.csv";
    private static final Logger logger = LoggerFactory
            .getLogger(SecretSantaGeneratorTest.class);

    /**
     * Object to be tested
     */
    //    private SecretSantaGenerator secretSantaGenerator;

    /**
     * Used as a real instance (not mocked)
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
    public void testGeneration1() throws IOException, GenerateException
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

        SecretSantaGenerator secretSantaGenerator = new SecretSantaGenerator(
                secretSantaList);
        List<SecretSantaDisplayType> resultList = null;

        resultList = this.testGenerateCall(secretSantaGenerator, resultList);

        // test size
        assertThat(resultList, hasSize(10));

        // verify proper exclusions
        for (SecretSantaDisplayType type : resultList)
        {
            final String attendeeName = type.getName();
            final String resultName = type.getSecretSanta();
            logger.info("verify for: ///// attendee[{}] ///// result[{}] /////",
                    attendeeName, resultName);
            assertThat(attendeeName, notNullValue());
            assertThat(resultName, notNullValue());

            SecretSanta secretSanta = secretSantaList.stream()
                    .filter(s -> s.getName().equals(attendeeName)).findAny().orElse(null);
            assertThat(secretSanta, notNullValue());
            assertThat(attendeeName, equalTo(secretSanta.getName()));
            assertThat(secretSanta.getExcludedNames(), not(hasItem(resultName)));
        }

        // verify uniqueness
        Set<String> attendeeSet = new TreeSet<>();
        Set<String> resultSet = new TreeSet<>();
        resultList.forEach(r ->
        {
            attendeeSet.add(r.getName());
            resultSet.add(r.getSecretSanta());
        });

        assertThat(attendeeSet.size(), equalTo(resultList.size()));
        assertThat(resultSet.size(), equalTo(resultList.size()));
        assertThat(attendeeSet, equalTo(resultSet));

        logger.info("========== PASS testGeneration1 ==========");
    }

    private List<SecretSantaDisplayType> testGenerateCall(
            SecretSantaGenerator secretSantaGenerator,
            List<SecretSantaDisplayType> resultList) throws GenerateException
    {
        int numAttempts = 0;
        while (numAttempts < Constants.MAX_GENERATE_ATTEMPTS)
        {
            logger.info("-----Generate attempt #: [{}] -----", numAttempts + 1);
            try
            {
                resultList = secretSantaGenerator.generateSecretSantas();
                break;
            }
            catch (GenerateException e)
            {
                numAttempts++;
                if (numAttempts == Constants.MAX_GENERATE_ATTEMPTS)
                {
                    throw e;
                }
            }
        }
        return resultList;
    }
}
