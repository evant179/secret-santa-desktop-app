package com.data;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gui.Constants;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class ExclusionReader
{
    
    private final Map<String, List<String>> nameToExclusionListMap = new HashMap<String, List<String>>();

    public ExclusionReader(String filePath) throws IOException
    {
        CSVReader reader = new CSVReader(new FileReader(filePath));
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
                this.nameToExclusionListMap.put(name, excludedNames);
            }
        }
    }

    public Map<String, List<String>> getNameToExclusionListMap()
    {
        return nameToExclusionListMap;
    }
}
