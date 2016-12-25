package com.data;

import java.io.FileReader;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.ObservableList;
import com.gui.Constants;
import com.gui.SecretSantaDisplayType;
import com.gui.SecretSantaGui;
import com.opencsv.CSVReader;

//verify for every name under # name in data.csv has an entry under exclusions.csv
//after a generate, verify selected names got a person correctly in their respective pool
//after a generate, verify all attendees were actually accounted for in the results

/**
 * @author Owner
 *
 */
public class DataValidator
{
    private static final Logger logger = LoggerFactory.getLogger(SecretSantaGui.class);
    private static final int NAME_COLUMN = 0;
   
    
    /**
     * @param secretSantaTableList
     * @description: Method creates a set of all secret santa names and returns true
     * if those names have been chosen by other secret santas
     * @return boolean
     */
    public static boolean allAccountedFor(ObservableList<SecretSantaDisplayType> secretSantaTableList)
    {
        Set<String> secretSantaList = new TreeSet<>();
        Set<String> chosenOnes = new TreeSet<>();
       for (int i = 0; i < secretSantaTableList.size();i++)
       {
           secretSantaList.add(secretSantaTableList.get(i).getName());
           chosenOnes.add(secretSantaTableList.get(i).getSecretSanta());
       }
       
        return secretSantaList.equals(chosenOnes);
    }
    
    
    /**
     * @description: Method checks that both the data file and exclusion files
     * have the same names under the #NAME column
     * @return boolean. Also logs any missing names
     */
    public static boolean containsAllNames()
    {
        try {
            CSVReader csvReader;
            //reads all data from the data and exclusion file
            List<String[]> dataList = new CSVReader(new FileReader(Constants.DATA_FILE_PATH)).readAll();
            List<String[]> exclusionList = new CSVReader(new FileReader(Constants.EXCLUSION_FILE_PATH)).readAll(); 
            
            //set objects that will contain all unique names under the #NAME column
            Set<String> dataList2 = new TreeSet<>();
            Set<String> exclusionList2 = new TreeSet<>();
            
            //first checks if both files have the same amount of rows. If they don't,
            //then they have different names
            if (dataList.size() != exclusionList.size())
                return false;
            else{
                
                //loops through both files and adds names from the #NAME column to
                //the set objects
                for (int i = 0; i < dataList.size(); i++)
                    dataList2.add(dataList.get(i)[NAME_COLUMN]); 
                   
                
                for (int i = 0; i < exclusionList.size(); i++)
                    exclusionList2.add(exclusionList.get(i)[NAME_COLUMN]);
                
                //tests lists for equality
                if (dataList2.equals(exclusionList2))
                       return true;
                else {
                    //remove all same elements from dataList
                    dataList2.removeAll(exclusionList2);
                    logger.info("Exclusion List Missing: {}\n", dataList2.toString());
                    return false;
                }
            }
         
            } catch (Exception e){
                logger.error("Cannot read file: ", e);
                return false;
            }
    }
   
}
