package com.gui;

import java.util.List;

import com.utility.Utility;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SecretSantaDisplayType2
{
    private final SimpleStringProperty name;

    private List<SimpleStringProperty> secretSantaList;

//    private List<SimpleStringProperty> exclusionList;
    private ObservableList<String>  exclusionList;
    private ObservableList<String>  availableSelectionList;
    
    private String selectedName;

    public SecretSantaDisplayType2(String name, List<String> secretSantaList,
            List<String> exclusionList)
    {
        this.name = new SimpleStringProperty(name);
        this.secretSantaList = Utility.convertList(secretSantaList,
                s -> new SimpleStringProperty(s));
        this.exclusionList = FXCollections.observableList(exclusionList);

        // TODO exclusionList doesn't need to be stored.
        // use exclusionList and secretSantaList to figure out availableSelectionList
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

//    public List<SimpleStringProperty> getExclusionList()
//    {
//        return this.exclusionList;
//    }
//
//    public void setExclusionList(List<SimpleStringProperty> exclusionList)
//    {
//        this.exclusionList = exclusionList;
//    }
}