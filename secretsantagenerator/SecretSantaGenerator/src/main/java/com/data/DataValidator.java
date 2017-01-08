package com.data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.ObservableList;

import com.generator.SecretSanta;
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
public class DataValidator implements DataValidatorInterface
{
    private static final Logger logger = LoggerFactory.getLogger(SecretSantaGui.class);
    private static final int NAME_COLUMN = 0;

    /**
     * @param secretSantaTableList
     * @description: Method creates a set of all secret santa names and returns
     *               true if those names have been chosen by other secret santas
     * @return boolean
     */
    @Override
    public <T> boolean areCollectionsValidAndEqual(Collection<T> collectionA,
            Collection<T> collectionB)
    {
        Objects.requireNonNull(collectionA, "collectionA is null");
        Objects.requireNonNull(collectionB, "collectionB is null");

        Set<T> setA = new TreeSet<>();
        Set<T> setB = new TreeSet<>();

        collectionA.forEach(a -> setA.add(a));
        collectionB.forEach(b -> setB.add(b));

        if (collectionA.size() != setA.size() || collectionB.size() != setB.size())
        {
            logger.warn("Duplicate data found");
            return false;
        }

        return setA.equals(setB);
    }

    /**
     * @description: Method checks that both the data file and exclusion files
     *               have the same names under the #NAME column
     * @return boolean. Also logs any missing names
     */
    @Override
    public boolean isDataSynchronous(String dataFilePath, String exclusionFilePath)
    {
        try
        {
            
            CSVReader csvReader;
            //reads all data from the data and exclusion file
            List<String[]> dataList = new CSVReader(new FileReader(dataFilePath))
                    .readAll();
            List<String[]> exclusionList = new CSVReader(
                    new FileReader(exclusionFilePath)).readAll();

            //set objects that will contain all unique names under the #NAME column
            Set<String> dataList2 = new TreeSet<>();
            Set<String> exclusionList2 = new TreeSet<>();

            //first checks if both files have the same amount of rows. If they don't,
            //then they have different names
            if (dataList.size() != exclusionList.size())
                return false;
            else
            {

                //loops through both files and adds names from the #NAME column to
                //the set objects
                for (int i = 0; i < dataList.size(); i++)
                {
                    final String dataName = dataList.get(i)[NAME_COLUMN];
                    logger.info("Add name from data file: {}", dataName);
                    dataList2.add(dataName);
                }

                for (int i = 0; i < exclusionList.size(); i++)
                {
                    final String exclusionName = exclusionList.get(i)[NAME_COLUMN];
                    logger.info("Add name from exclusion file: {}", exclusionName);
                    exclusionList2.add(exclusionName);
                }

                //tests lists for equality
                if (dataList2.equals(exclusionList2))
                    return true;
                else
                {
                    //remove all same elements from dataList
                    dataList2.removeAll(exclusionList2);
                    logger.info("Exclusion List Missing: {}\n", dataList2.toString());
                    return false;
                }
            }

        }
        catch (Exception e)
        {
            logger.error("Cannot read file: ", e);
            return false;
        }
    }

    @Override
    public boolean isValidGeneration(List<SecretSanta> secretSantas,
            Map<String, String> attendeeToResultMap)
    {
       
        // for unit testing, you can optionally copy what DataReaderTest did
        // to quickly create a list of SecretSantas (lines 31-47)
        
        //Checks each santa's exclusion list to see if their chosen person
        //is contained in it. If they are, then it's not a valid
        //generation
        for (SecretSanta santa : secretSantas)
        {
            List<String> excludedNamesForSanta = santa.getExcludedNames();
            String c = santa.getName();
            String santasResult = attendeeToResultMap.get(santa.getName());
            
            
            if (excludedNamesForSanta.contains(santasResult))
                return false;
        }
        return true;
    }
    

}
