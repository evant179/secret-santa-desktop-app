package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import gui.SecretSantaDisplayType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DataRecorder
{
    private final static String COMMA_DELIMITER = ",";
    private final static String NEW_LINE_SEPARATOR = "\n";
    private static final String FILE_PATH = "resources/data.csv";
    private final static String NEW_FILE_PATH = "resources/current_year_data.csv";
    private final static String DATA_SAVE_ERROR = "Cannot save current_year_data.csv.";
    private final static String FILE_HEADER = "#THIS PERSON, WILL GIFT";
    
    public static void save(List<SecretSantaDisplayType> recordList) throws IOException
    {
        // first read data.csv
        // then append recordList at end
        
        CSVReader reader = new CSVReader(new FileReader(FILE_PATH));
        CSVWriter writer = new CSVWriter(new FileWriter(NEW_FILE_PATH), ',');
        
        int rowSize = 0;
        
        String[] entries = null;
        while ((entries = reader.readNext()) != null) {
            List<String> list = Arrays.asList(entries); // Arrays.asList(entries) is unnmodifiable
            list = new ArrayList<String>(list); // Convert to ArrayList to be modifiable

            // Check if header line
            if (list.get(0).charAt(0) == '#')
            {
                // This should only occur for the first row
                String lastRecordedYearString = list.get(list.size() - 1);
                int lastRecordedYear = Integer.parseInt(lastRecordedYearString);
                lastRecordedYear++;
                list.add(Integer.toString(lastRecordedYear));
                rowSize = list.size();
            }
            else
            {
                boolean isMatchFound = false;
                for (SecretSantaDisplayType record : recordList)
                {
                    if (list.get(0).equals(record.getName()))
                    {
                        list.add(record.getSecretSanta());
                        recordList.remove(record);
                        isMatchFound = true;
                        break;
                    }
                }
                
                if (!isMatchFound)
                {
                    // If no match found, then add empty string at end of current row
                    // i.e. xxx was at previous secret santa but didn't go current year
                    list.add("");
                }
            }

            checkRowSize(rowSize, list);
            
            writer.writeNext(list.toArray(new String[0]));
        }
        
        // Save newcomers
        for (SecretSantaDisplayType newComer : recordList)
        {
            List<String> newComerRow = new ArrayList<String>();
            for (int i = 0; i < rowSize; i++)
            {
                newComerRow.add("");
            }
            newComerRow.set(0, newComer.getName()); // set name as first spot in row
            newComerRow.set(rowSize - 1, newComer.getSecretSanta()); // set secret santa as last spot in row
            checkRowSize(rowSize, newComerRow);
            writer.writeNext(newComerRow.toArray(new String[0]));
        }
        
        writer.close();
    }

    private static void checkRowSize(int rowSize, List<String> list)
    {
        if (rowSize != list.size())
        {
            System.out.println("ERROR when saving - header row size does NOT match current row size: " + list.get(0));
        }
    }
}
