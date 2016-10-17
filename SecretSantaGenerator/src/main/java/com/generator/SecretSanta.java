package com.generator;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Secret santa invite. Keeps track of Name of person and their excluded secret
 * santas
 */
public class SecretSanta implements Comparable<SecretSanta>
{
    private static final Logger logger = LoggerFactory.getLogger(SecretSanta.class);

    private final String name;
    private List<String> excludedNames;

    public SecretSanta(String name, List<String> excludedNames)
    {
        this.name = name;
        this.excludedNames = excludedNames;

        // Check if exclusion list is null
        if (excludedNames == null)
        {
            this.excludedNames = new ArrayList<String>();
        }

        // Add own name--you don't yourself as a secret santa
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
                    logger.info(
                            "name[{}]excludedNames[{}]====isEqualsTo====name[{}]excludedNames[{}]",
                            this.name, this.excludedNames, other.getName(),
                            other.getExcludedNames());
                }
            }
        }
        return isEquals;
    }

    // TODO learn about overriding hashCode

    @Override
    public int compareTo(SecretSanta s)
    {
        return Integer.compare(s.getExcludedNames().size(),
                this.getExcludedNames().size());
    }
}
