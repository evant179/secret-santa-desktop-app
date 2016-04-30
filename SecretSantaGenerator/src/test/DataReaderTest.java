package test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import data.DataReader;

public class DataReaderTest
{

    @Test
    public void test()
    {
        try
        {
            DataReader.parseDataFile();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail("File not found");
        }
    }

}
