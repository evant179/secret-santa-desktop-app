package com.secretsanta.gui;

import java.util.List;

import com.secretsanta.utility.Utility;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Display type for the main table view {@link MainTableView}.
 *
 * Each instance of this type represents a row in the table.
 */
public class SecretSantaDisplayType
{
    private final SimpleStringProperty name;

    private List<SimpleStringProperty> secretSantaList;

    private ObservableList<String> exclusionList;

    private String selectedName;

    public SecretSantaDisplayType(String name, List<String> secretSantaList,
            List<String> exclusionList)
    {
        this.name = new SimpleStringProperty(name);
        this.secretSantaList = Utility.convertList(secretSantaList,
                s -> new SimpleStringProperty(s));
        this.exclusionList = FXCollections.observableList(exclusionList);
    }

    public String getName()
    {
        return this.name.get();
    }

    public void setName(String name)
    {
        this.name.set(name);
    }

    public List<SimpleStringProperty> getSecretSantaList()
    {
        return this.secretSantaList;
    }

    public void setSecretSantaList(List<SimpleStringProperty> secretSantaList)
    {
        this.secretSantaList = secretSantaList;
    }

    public String getSelectedName()
    {
        return this.selectedName;
    }

    public void setSelectedName(String selectedName)
    {
        this.selectedName = selectedName;
    }

    public ObservableList<String> getExclusionList()
    {
        return this.exclusionList;
    }

    public void setExclusionList(ObservableList<String> exclusionList)
    {
        this.exclusionList = exclusionList;
    }
}