package com.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class SimpleDialogCreator
{
    private static final Logger logger = LoggerFactory.getLogger(SimpleDialogCreator.class);

    /**
     * Display simple, modal dialog with a message
     * 
     * @param alertType
     * @param message
     */
    public void showSimpleDialog(AlertType alertType, String message)
    {
        logger.info("Display alert dialog of type[{}], message[{}]",
                alertType, message);
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
