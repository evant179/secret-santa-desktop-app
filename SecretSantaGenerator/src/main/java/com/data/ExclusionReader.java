package com.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.data.CsvFactory.FILETYPE;
import com.opencsv.CSVReader;

public class ExclusionReader
{
    private final CsvFactory csvFactory;

    public ExclusionReader(CsvFactory csvFactory)
    {
        this.csvFactory = csvFactory;
    }

    public Map<String, List<String>> getExclusionListDataFromFile() throws IOException
    {
        CSVReader exclusionCsvReader = this.csvFactory
                .createCsvReader(FILETYPE.EXCLUSION);
        Map<String, List<String>> nameToExclusionNameListMap = new HashMap<String, List<String>>();

        String[] tokens = null;

        // read each line
        while ((tokens = exclusionCsvReader.readNext()) != null)
        {
            String name = null;
            final List<String> excludedNames = new ArrayList<String>();

            if ((tokens.length > 0) && (!((tokens[0]).charAt(0) == '#')))
            {
                for (String token : tokens)
                {
                    String currentData = token.toUpperCase();

                    if ((currentData == null) || (currentData.isEmpty()))
                    {
                        // skip
                    }
                    else if (name == null)
                    {
                        // store first token as name
                        name = currentData;
                    }
                    else
                    {
                        // add following tokens as part of exclusion list
                        excludedNames.add(currentData);
                    }
                }

                // TODO add check here if name/key already exists then show
                // window with error that a duplicate exists
                nameToExclusionNameListMap.put(name, excludedNames);
            }
        }

        exclusionCsvReader.close();

        return nameToExclusionNameListMap;
    }
}
