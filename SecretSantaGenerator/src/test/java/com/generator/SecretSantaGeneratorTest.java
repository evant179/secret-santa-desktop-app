package com.generator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.data.CsvFactory;
import com.data.DataReader;
import com.data.ExclusionReader;
import com.gui.Constants;
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
    private ResultGenerator generator;

    /**
     * Set up called before each test case method
     */
    @Before
    public void setUp()
    {
        this.generator = new ResultGenerator();
    }

    /**
     * Reads in test data files
     * 
     * @throws Exception
     */
    @Test
    public void testGeneration1() throws Exception
    {
        // ===== set up data =====
        File dataFile = new File(getClass().getResource(TEST_DATA2_FILE_PATH).getFile());
        File exclusionsFile = new File(
                getClass().getResource(TEST_EXCLUSIONS2_FILE_PATH).getFile());

        CsvFactory csvFactory = new CsvFactory(dataFile.getPath(),
                exclusionsFile.getPath(), null);

        ExclusionReader exclusionReader = new ExclusionReader(csvFactory);
        DataReader dataReader = new DataReader(csvFactory, exclusionReader);
        List<SecretSanta> secretSantaList = dataReader
                .parseDataFileWithExclusionFile(true);

        // ===== test call =====
        final Map<String, String> attendeeToResultMap = this
                .testGenerateCall(secretSantaList);

        // ===== verification =====
        // test size
        // NOTE: hamcrest call "aMapWithSize" is not in current 1.3 version
        assertThat(attendeeToResultMap.entrySet(), hasSize(10));

        // verify proper exclusions
        for (Map.Entry<String, String> attendeeToResultEntry : attendeeToResultMap
                .entrySet())
        {
            final String attendeeName = attendeeToResultEntry.getKey();
            final String resultName = attendeeToResultEntry.getValue();
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
        attendeeToResultMap.forEach((attendeeName, resultName) ->
        {
            attendeeSet.add(attendeeName);
            resultSet.add(resultName);
        });

        assertThat(attendeeSet, hasSize(10));
        assertThat(resultSet, hasSize(10));
        assertThat(attendeeSet.size(), equalTo(attendeeToResultMap.size()));
        assertThat(resultSet.size(), equalTo(attendeeToResultMap.size()));
        assertThat(attendeeSet, equalTo(resultSet));

        logger.info("========== PASS testGeneration1 ==========");
    }

    /**
     * Wrapper to call {@link ResultGenerator#generateSecretSantas(List)}.
     * 
     * Due to its behavior, an impossible name generation scenario may be ran
     * into.
     * 
     * The method call is attempted {@link Constants#MAX_GENERATE_ATTEMPTS}
     * times before the test is forced to fail
     * 
     * @param secretSantaList
     *            Secret santa list to generate reults for. Each entry is
     *            considered an attendee
     * @return Map with [attendee name as the key] and [corresponding result
     *         name as the value]. Assert the map size matches the size of the
     *         secret santa list passed in
     */
    private Map<String, String> testGenerateCall(List<SecretSanta> secretSantaList)
    {
        Map<String, String> attendeeToResultMap = new HashMap<String, String>();
        int numAttempts = 0;
        while (numAttempts < Constants.MAX_GENERATE_ATTEMPTS)
        {
            logger.info("-----Generate attempt #: [{}] -----", numAttempts + 1);
            try
            {
                attendeeToResultMap = this.generator
                        .generateSecretSantas(secretSantaList);
                break;
            }
            catch (GenerateException e)
            {
                numAttempts++;
                if (numAttempts == Constants.MAX_GENERATE_ATTEMPTS)
                {
                    fail(e.getMessage());
                }
            }
        }
        return attendeeToResultMap;
    }
}
