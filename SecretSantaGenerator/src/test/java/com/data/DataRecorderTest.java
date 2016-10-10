package com.data;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.gui.Constants;
import com.gui.SecretSantaDisplayType;

public class DataRecorderTest
{
    @Test
    public void test()
    {
        DataRecorder dataRecorder = new DataRecorder();
        List<SecretSantaDisplayType> testList = createTestSecretSantaDisplayList();
        try
        {
            dataRecorder.save(testList, Constants.DATA_FILE_PATH,
                    Constants.OUTPUT_FILE_PATH);

            // TODO add checks later if every row matches same amount of entries
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
        list.add(new SecretSantaDisplayType("NEWCOMER", "test3"));

        return list;
    }
}
