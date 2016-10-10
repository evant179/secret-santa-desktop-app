package com.data;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import com.gui.Constants;

public class DataReaderTest
{

    @Test
    public void test()
    {
        DataReader dataReader = new DataReader();
        try
        {
            dataReader.parseDataFile(Constants.DATA_FILE_PATH,
                    Constants.EXCLUSION_FILE_PATH);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail("File not found");
        }
    }

}
