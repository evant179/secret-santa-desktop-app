package com.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gui.SecretSantaDisplayType;

/**
 * Generator for secret santas
 */
public class SecretSantaGenerator
{
    private static final Logger logger = LoggerFactory
            .getLogger(SecretSantaGenerator.class);

    private final Map<String, Boolean> takenNames = new HashMap<String, Boolean>();
    private final Random random = new Random();

    public final static String IMPOSSIBLE_SCENARIO_ERROR_MESSAGE = "Secret santa list generated has encountered an impossible scenario. "
            + "Attempt Generate again.";

    /**
     * Generate a result for each attendee
     * 
     * @param secretSantaList
     *            Secret santa list to generate reults for. Each entry is
     *            considered an attendee. Will not be modified
     * @return Map with [attendee name as the key] and [corresponding result
     *         name as the value]
     * @throws GenerateException
     *             Thrown during an impossible generation scenario
     */
    public Map<String, String> generateSecretSantas(
            final List<SecretSanta> secretSantaList) throws GenerateException
    {
        // Sort the secret santas from most exclusions to least exclusions. This will lessen
        // the chance of running into an impossible scenario (where a person has no available
        // name to assign to)
        Collections.sort(secretSantaList);

        // create unmodifiable list to ensure no accidental edits
        List<SecretSanta> unmodifiableSecretSantaList = Collections
                .unmodifiableList(secretSantaList);
        final Map<String, String> attendeeToResultMap = new HashMap<String, String>();

        // Create "checkbox" of secret santa.
        // Marked false as default--not taken yet;
        // Marked true when taken;
        for (SecretSanta secretSanta : unmodifiableSecretSantaList)
        {
            // TODO have a check here for attendence
            this.takenNames.put(secretSanta.getName(), false);
        }

        // mark rigged names
        for (SecretSanta secretSanta : unmodifiableSecretSantaList)
        {
            if (secretSanta.getOverridenSelection() != null
                    && !secretSanta.getOverridenSelection().isEmpty())
            {
                logger.info("detect override for [{}]: [{}]", secretSanta.getName(),
                        secretSanta.getOverridenSelection());
                this.takenNames.replace(secretSanta.getOverridenSelection(), true);
            }
        }

        // for each attendee, generate a result
        for (SecretSanta secretSanta : unmodifiableSecretSantaList)
        {
            if (secretSanta.getOverridenSelection() != null
                    && !secretSanta.getOverridenSelection().isEmpty())
            {
                final String riggedName = secretSanta.getOverridenSelection();
                logger.info("successfully override for [{}]: [{}]", secretSanta.getName(),
                        secretSanta.getOverridenSelection());
                // Assign name to secret santa map
                attendeeToResultMap.put(secretSanta.getName(), riggedName);
            }
            else
            {
                String assignedName = this.assignSecretSanta(secretSanta,
                        unmodifiableSecretSantaList);

                // Assign name to secret santa map
                logger.info("///// attendee[{}] ///// result[{}] /////",
                        secretSanta.getName(), assignedName);
                attendeeToResultMap.put(secretSanta.getName(), assignedName);

                // Mark the map of taken names as true (taken)
                this.takenNames.replace(assignedName, true);
            }
        }

        return attendeeToResultMap;
    }

    /**
     * Assign a secret santa based on the passed in secret santa's exclusion
     * list and the current taken secret santas
     * 
     * @param secretSanta
     * @return
     */
    private String assignSecretSanta(SecretSanta secretSanta,
            final List<SecretSanta> secretSantaList) throws GenerateException
    {
        logger.info("Looking for a secret santa for: [{}], exclusion list size: [{}]",
                secretSanta.getName(), secretSanta.getExcludedNames().size());

        // copy the secret santa list's names
        List<String> uniqueNameList = new ArrayList<String>();
        for (SecretSanta s : secretSantaList)
        {
            uniqueNameList.add(s.getName());
        }

        // remove people from exclusion list
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
            Boolean isTaken = entry.getValue();

            if (isTaken)
            {
                uniqueNameList.remove(takenName);
            }
        }

        // create a new random number generator for that new list size
        if (uniqueNameList.size() < 1)
        {
            GenerateException e = new GenerateException(
                    IMPOSSIBLE_SCENARIO_ERROR_MESSAGE);
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
