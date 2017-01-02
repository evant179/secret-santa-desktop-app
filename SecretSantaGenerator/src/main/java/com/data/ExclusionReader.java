package com.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;

public class ExclusionReader
{
    public Map<String, List<String>> getExclusionListDataFromFile(CSVReader reader) throws IOException
    {
        Map<String, List<String>> nameToExclusionNameListMap = new HashMap<String, List<String>>();
        
        String[] tokens = null;

        // read each line
        while ((tokens = reader.readNext()) != null)
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
                // window
                // wit error that a duplicate exists
                nameToExclusionNameListMap.put(name, excludedNames);
            }
        }

        return nameToExclusionNameListMap;
    }
}
