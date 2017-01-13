package com.secretsanta.gui;

import java.util.List;

import org.controlsfx.control.ListSelectionView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secretsanta.generator.SecretSanta;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

public class EditExclusionDialog extends Dialog<SecretSanta>
{
    private static final Logger logger = LoggerFactory
            .getLogger(EditExclusionDialog.class);

    private final SecretSanta secretSanta;
    private final List<String> nameList;
    private final ListSelectionView<String> listSelectionView = new ListSelectionView<String>();

    public EditExclusionDialog(SecretSanta secretSanta, List<String> nameList)
    {
        super();

        this.secretSanta = secretSanta;
        this.nameList = nameList;

        this.setTitle(Constants.EDIT_EXCLUSION_DIALOG_TITLE);
        this.setHeaderText(String.format(Constants.EDIT_EXCLUSION_DIALOG_HEADER,
                this.secretSanta.getName()));
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Convert result into SecretSanta object
        this.setResultConverter(dialogButton ->
        {
            if (dialogButton == ButtonType.OK)
            {
                return this.createUpdatedSecretSanta();
            }
            return null;
        });

        this.initializeListSelectionView();
        this.getDialogPane().setContent(this.listSelectionView);
    }

    private void initializeListSelectionView()
    {
        ((Label) this.listSelectionView.getSourceHeader()).setText("Available");
        ((Label) this.listSelectionView.getTargetHeader()).setText("Excluded");
        for (String name : this.nameList)
        {
            if (!this.secretSanta.getName().equals(name)) // skip own name
            {
                if (this.secretSanta.getExcludedNames().contains(name))
                {
                    // excluded names
                    this.listSelectionView.getTargetItems().add(name);
                }
                else
                {
                    // availale names
                    this.listSelectionView.getSourceItems().add(name);
                }
            }
        }
    }

    private SecretSanta createUpdatedSecretSanta()
    {
        this.secretSanta.getExcludedNames().clear();
        this.listSelectionView.getTargetItems()
                .forEach(s -> this.secretSanta.getExcludedNames().add(s));
        return this.secretSanta;
    }
}
