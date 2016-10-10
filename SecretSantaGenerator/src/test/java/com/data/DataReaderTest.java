package com.data;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class DataReaderTest
{

    @Test
    public void test()
    {
        DataReader dataReader = new DataReader();
        try
        {
            dataReader.parseDataFile();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail("File not found");
        }
    }

}
