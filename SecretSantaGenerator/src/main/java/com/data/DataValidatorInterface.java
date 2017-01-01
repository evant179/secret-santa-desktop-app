package com.data;

import java.util.Map;

//verify all names are unique
//under #name
//verify for every name under # name in data.csv has an entry under exclusions.csv
//after a generate, verify selected names got a person correctly in their respective pool
//after a generate, verify all attendees were actually accounted for in the results
//each one I listed is a method
//I can provide an interface for ya tomorrow so you know the passed in parameters to work with
//it should be standalone so you can unit test it

public interface DataValidatorInterface
{
    public boolean verifyUniqueNamesFromDataFile(String dataFilePath, String exclusionFilePath);
    
    public boolean verifyResultsAccountedFor(Map<String, String> resultMap);
}
