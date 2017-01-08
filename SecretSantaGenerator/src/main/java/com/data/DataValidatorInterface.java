package com.data;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.generator.SecretSanta;

/**
 * Interface for data validator
 * 
 * @author et
 *
 */
public interface DataValidatorInterface
{
    /**
     * Verify each name in the data file has an entry under the exclusions file.
     * 
     * Name is the FIRST column of both the data file and exclusion file
     * 
     * @param dataFilePath
     * @param exclusionFilePath
     * @return True if synchronous; otherwise, false
     */
    public boolean isDataSynchronous(String dataFilePath, String exclusionFilePath);

    /**
     * Verify the contents of collection A equals the contents of collection B.
     * Order does not matter.
     * 
     * In additon, also verify no duplication exists within either collection.
     * 
     * This validation will be used to:
     * 
     * - Verify the results of the secret santa generation by checking that each
     * attendee is selected as a secret santa result for another attendee (by
     * passing in a list of attendees and a list of results).
     * 
     * - Verify the names of the checked checkboxes matches the list of
     * attendees outputted by the secret santa generation (by passing in a list
     * of checked names and a list of attendees).
     * 
     * @param <T>
     *            Collection type parameter
     * 
     * @param collectionA
     *            Must not be null
     * @param collectionB
     *            Must not be null
     * @return True if collections are valid and equal; otherwise, false
     */
    public <T> boolean areCollectionsValidAndEqual(Collection<T> collectionA,
            Collection<T> collectionB);

    /**
     * Verify for each {@link SecretSanta}, their result is NOT contained in
     * their exclusion list
     * 
     * @param secretSantas
     *            List of secret santas, each containing their respective
     *            exclusion list
     * @param attendeeToResultMap
     *            Attendee name to secret santa result map
     * @return True if validation generation; otherwise, false
     */
    public boolean isValidGeneration(List<SecretSanta> secretSantas,
            Map<String, String> attendeeToResultMap);
}
