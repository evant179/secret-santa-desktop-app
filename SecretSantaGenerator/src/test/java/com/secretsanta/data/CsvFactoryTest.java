package com.secretsanta.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secretsanta.data.CsvFactory;
import com.secretsanta.data.CsvFactory.FILETYPE;

/**
 * Unit tests for {@link CsvFactory}
 */
public class CsvFactoryTest
{
    private static final Logger logger = LoggerFactory.getLogger(CsvFactoryTest.class);

    private static final String FAKE_DATA_PATH = "dataPath";
    private static final String FAKE_EXCLUSION_PATH = "exclusionPath";
    private static final String FAKE_OUTPUT_PATH = "outputPath";

    /**
     * Object to be tested
     */
    private CsvFactory csvFactory;

    /**
     * Set up called before each test case method
     */
    @Before
    public void setUp()
    {
    }

    @Test
    public void test_constructor() throws IOException
    {
        // ===== test call =====
        this.csvFactory = new CsvFactory(FAKE_DATA_PATH, FAKE_EXCLUSION_PATH,
                FAKE_OUTPUT_PATH);

        // ===== verification =====
        logger.info("========== path: [{}] ==========", FILETYPE.DATA.getFilePath());
        assertThat(FILETYPE.DATA.getFilePath(), equalTo(FAKE_DATA_PATH));
        assertThat(FILETYPE.EXCLUSION.getFilePath(), equalTo(FAKE_EXCLUSION_PATH));
        assertThat(FILETYPE.OUTPUT.getFilePath(), equalTo(FAKE_OUTPUT_PATH));

        logger.info("========== PASS test_constructor1 ==========");
    }
}
