package com.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * Secret santa invite. Keeps track of Name of person and their excluded secret santas
 */
public class SecretSanta implements Comparable<SecretSanta>
{
    private final String name;
    private List<String> excludedNames;
    
    public SecretSanta(String name, List<String> excludedNames)
    {
        this.name = name;
        this.excludedNames = excludedNames;
        
        // Check if exclusion list is null
        if(excludedNames == null)
        {
            this.excludedNames = new ArrayList<String>();
        }
        
        // Add own name--you don't yourself as a secret santa
        if(!this.excludedNames.contains(this.name))
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
    public int compareTo(SecretSanta s)
    {
        return Integer.compare(s.getExcludedNames().size(), this.getExcludedNames().size());
    }
}
