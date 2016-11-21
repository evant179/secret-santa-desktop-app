package com.gui;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generator.SecretSanta;

import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

public class AttendeeChoiceDialog extends ChoiceDialog<SecretSanta>
{
    private static final Logger logger = LoggerFactory
            .getLogger(AttendeeChoiceDialog.class);

    public AttendeeChoiceDialog(List<SecretSanta> secretSantas)
    {
        super(secretSantas.get(0), secretSantas);

        this.setTitle(Constants.ATTENDEE_CHOICE_DIALOG_TITLE);
        this.setHeaderText(Constants.ATTENDEE_CHOICE_DIALOG_HEADER);
        this.setContentText(Constants.ATTENDEE_CHOICE_DIALOG_CONTENT);

        this.customizeComboBox();
    }

    private void customizeComboBox()
    {
        @SuppressWarnings("unchecked")
        // very hacky combobox lookup
        ComboBox<SecretSanta> comboBox = (ComboBox<SecretSanta>) this.getDialogPane()
                .lookup(".combo-box");
        // note: can only transform outer anonymous class because
        // lambda expressions can only be used for interfaces
        comboBox.setCellFactory(p ->
        {
            return new ListCell<SecretSanta>()
            {
                @Override
                protected void updateItem(SecretSanta item, boolean empty)
                {
                    super.updateItem(item, empty);
                    if (empty)
                    {
                        setText(null);
                        setGraphic(null);
                    }
                    else
                    {
                        setText(item.getName());
                    }
                }
            };
        });

        comboBox.setButtonCell(new ListCell<SecretSanta>()
        {
            @Override
            protected void updateItem(SecretSanta item, boolean empty)
            {
                super.updateItem(item, empty);
                if (empty)
                {
                    setText(null);
                    setGraphic(null);
                }
                else
                {
                    setText(item.getName());
                }
            }
        });
    }
}
