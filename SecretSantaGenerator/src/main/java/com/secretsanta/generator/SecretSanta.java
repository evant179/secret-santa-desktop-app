package com.secretsanta.generator;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Secret santa attendee type.
 * 
 * Contains attendee name and corresponding list of excluded names.
 */
public class SecretSanta implements Comparable<SecretSanta>
{
    private static final Logger logger = LoggerFactory.getLogger(SecretSanta.class);

    private final String name;
    private List<String> excludedNames;
    private String overridenSelection;

    /**
     * Constructor
     * 
     * @param name
     *            Attendee name
     * @param excludedNames
     *            List of excluded names
     */
    public SecretSanta(String name, List<String> excludedNames)
    {
        this.name = name;
        this.excludedNames = excludedNames;

        // Check if exclusion list is null
        if (excludedNames == null)
        {
            this.excludedNames = new ArrayList<String>();
        }

        // Add own name--you don't receive yourself as a secret santa result
        if (!this.excludedNames.contains(this.name))
        {
            this.excludedNames.add(this.name);
        }
    }

    public String getName()
    {
        return name;
    }

    public List<String> getExcludedNames()
    {
        return excludedNames;
    }

    public String getOverridenSelection()
    {
        return overridenSelection;
    }

    public void setOverridenSelection(String overridenSelection)
    {
        this.overridenSelection = overridenSelection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean isEquals = false;
        if (obj instanceof SecretSanta)
        {
            SecretSanta other = (SecretSanta) obj;
            if (this.name.equals(other.getName()))
            {
                isEquals = this.excludedNames.size() == other.getExcludedNames().size()
                        && this.excludedNames.containsAll(other.getExcludedNames());
                if (isEquals)
                {
                    // debug
                    //                    logger.info(
                    //                            "name[{}]excludedNames[{}]====isEqualsTo====name[{}]excludedNames[{}]",
                    //                            this.name, this.excludedNames, other.getName(),
                    //                            other.getExcludedNames());
                }
            }
        }
        return isEquals;
    }

    // TODO learn about overriding hashCode

    /*
     * Sort from highest number of excluded names to lower number of excluded
     * names.
     * 
     * Used for faster results in result generation
     * 
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(SecretSanta s)
    {
        return Integer.compare(s.getExcludedNames().size(),
                this.getExcludedNames().size());
    }
}
