package com.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.gui.SecretSantaDisplayType;

/**
 * Generator for secret santas
 */
public class SecretSantaGenerator
{
    private final List<SecretSanta> secretSantas;
    private final Map<String, Boolean> takenNames = new HashMap<String, Boolean>();
    private final Random random = new Random();
    
    public final static String IMPOSSIBLE_SCENARIO_ERROR_MESSAGE = 
            "Secret santa list generated has encountered an impossible scenario. " +
            "Attempt Generate again.";
    
    
    public SecretSantaGenerator(List<SecretSanta> secretSantas)
    {
        this.secretSantas = secretSantas;
        
    }

    public List<SecretSantaDisplayType> generateSecretSantas() throws GenerateException
    {
        final Map<String, String> secretSantaMap = new HashMap<String, String>();
        final List<SecretSantaDisplayType> displayList = new ArrayList<SecretSantaDisplayType>();
        
        // Create "checkbox" of secret santa.
        // Marked false as default--not taken yet;
        // Marked true when taken;
        for(SecretSanta secretSanta : this.secretSantas)
        {
            // TODO have a check here for attendence
            this.takenNames.put(secretSanta.getName(), false);
        }

        // Sort the secret santas from most exclusions to least exclusions. This will lessen
        // the chance of running into an impossible scenario (where a person has no available
        // name to assign to)
        Collections.sort(this.secretSantas);
        
        for(SecretSanta secretSanta : this.secretSantas)
        {
            String assignedName = assignSecretSanta(secretSanta);
            
            // Assign name to secret santa map
            secretSantaMap.put(secretSanta.getName(), assignedName);

            // Mark the map of taken names as true (taken)
            this.takenNames.replace(assignedName, true);
        }
        
        // Convert map to a list displayable on the generated table
        for (Map.Entry<String, String> entry : secretSantaMap.entrySet())
        {
            String name = entry.getKey();
            String secretSanta = entry.getValue();
            
            displayList.add(new SecretSantaDisplayType(
                    name.toString(), secretSanta.toString()));
        }
        
        return displayList;
    }
    
    /**
     * Assign a secret santa based on the passed in secret santa's exclusion list and
     * the current taken secret santas
     * 
     * @param secretSanta
     * @return
     */
    private String assignSecretSanta(SecretSanta secretSanta) throws GenerateException
    {
        // debug
        System.out.println("Looking for a secret santa for: " + secretSanta.getName() +
                ", exclusion list size: " + secretSanta.getExcludedNames().size());
        
        // copy the secret santa list's names
        List<String> uniqueNameList = new ArrayList<String>();
        for (SecretSanta s : this.secretSantas)
        {
            uniqueNameList.add(s.getName());
        }
        
        // remove people from exlusion list
        for (String excludedName : secretSanta.getExcludedNames())
        {
            if (secretSanta.getExcludedNames().contains(excludedName))
            {
                uniqueNameList.remove(excludedName);
            }
        }
        
        // remove people from "taken" checkbox
        for (Map.Entry<String, Boolean> entry : this.takenNames.entrySet())
        {
            String takenName = entry.getKey();
            Boolean isNotTaken = entry.getValue();
            
            if (isNotTaken)
            {
                uniqueNameList.remove(takenName);
            }
        }
        
        // create a new random number generator for that new list size
        if (uniqueNameList.size() < 1)
        {
            GenerateException e = new GenerateException(IMPOSSIBLE_SCENARIO_ERROR_MESSAGE);
            throw e;
        }
        final int maxIndexUniqueList = uniqueNameList.size() - 1;
        
        // choose random number based on that to select name from unique list
        // note: random.nextInt((max - min) + 1) + min;
        int randomNameIndex = random.nextInt((maxIndexUniqueList - 0) + 1) + 0;
        String assignedName = uniqueNameList.get(randomNameIndex);
        
        
        return assignedName;
    }
}
