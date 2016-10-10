package com.data;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.gui.Constants;
import com.gui.SecretSantaDisplayType;

public class ExclusionReaderTest
{
    @Test
    public void test()
    {
        try
        {
            ExclusionReader exclusionReader = new ExclusionReader(Constants.EXCLUSION_FILE_PATH);
            int stop = 5;

            // TODO add actual tests
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail("File not found or file cannot be written");
        }
    }

    private List<SecretSantaDisplayType> createTestSecretSantaDisplayList()
    {
        List<SecretSantaDisplayType> list = new ArrayList<SecretSantaDisplayType>();
        list.add(new SecretSantaDisplayType("EVAN", "test1"));
        list.add(new SecretSantaDisplayType("JUSTIN", "test2"));

        return list;
    }
}
