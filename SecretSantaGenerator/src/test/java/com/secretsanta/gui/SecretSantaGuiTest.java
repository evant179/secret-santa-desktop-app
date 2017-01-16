package com.secretsanta.gui;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit.ApplicationTest;

import javafx.stage.Stage;

public class SecretSantaGuiTest extends ApplicationTest
{
    private static final Logger logger = LoggerFactory
            .getLogger(SecretSantaGuiTest.class);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        ApplicationTest.launch(SecretSantaGui.class);
    }

    @Before
    public void setUp()
    {
    }

    @Override
    public void start(Stage arg0) throws Exception
    {
    }

    @Test
    public void test()
    {
        logger.info(
                "/////////////////////////////////////////////////// sample testfx test");
    }

}
