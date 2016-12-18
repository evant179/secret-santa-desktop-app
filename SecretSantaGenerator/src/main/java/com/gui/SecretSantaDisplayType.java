package com.gui;

import javafx.beans.property.SimpleStringProperty;

public class SecretSantaDisplayType
{
    private final SimpleStringProperty name;
    private final SimpleStringProperty secretSanta;

    public SecretSantaDisplayType(String name, String secretSanta)
    {
        this.name = new SimpleStringProperty(name);
        this.secretSanta = new SimpleStringProperty(secretSanta);
    }

    public String getName()
    {
        return this.name.get();
    }

    public void setName(String name)
    {
        this.name.set(name);
    }

    public String getSecretSanta()
    {
        return this.secretSanta.get();
    }

    public void setSecretSanta(String secretSanta)
    {
        this.secretSanta.set(secretSanta);
    }
}

// TODO
// first, work on new DataRecorder save to switch to SecretSantaDisplayType2,
// adding new unit tests, then work way up
