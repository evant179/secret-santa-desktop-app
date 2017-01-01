package com.data;

import java.io.FileReader;
import java.util.List;
import java.util.Map;
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
    public boolean verifyResultsAccountedFor(Map<String, String> resultMap)
    {
        Set<String> secretSantaList = new TreeSet<>();
        Set<String> chosenOnes = new TreeSet<>();

        resultMap.forEach((key, value) ->
        {
            logger.info("Verifying for key[{}], value[{}]", key, value);
            secretSantaList.add(key);
            chosenOnes.add(value);
        });

        return secretSantaList.equals(chosenOnes);
    }

    /**
     * @description: Method checks that both the data file and exclusion files
     *               have the same names under the #NAME column
     * @return boolean. Also logs any missing names
     */
    @Override
    public boolean verifyUniqueNamesFromDataFile(String dataFilePath,
            String exclusionFilePath)
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

}
