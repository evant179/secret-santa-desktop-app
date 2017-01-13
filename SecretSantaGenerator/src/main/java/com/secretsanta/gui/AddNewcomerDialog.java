package com.secretsanta.gui;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secretsanta.data.DataRecorder;
import com.secretsanta.utility.Utility;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;

public class AddNewcomerDialog extends TextInputDialog
{
    private static final Logger logger = LoggerFactory.getLogger(AddNewcomerDialog.class);

    private final DataRecorder dataRecorder;
    private final SimpleDialogCreator simpleDialogCreator = new SimpleDialogCreator();

    public AddNewcomerDialog(DataRecorder dataRecorder)
    {
        super();

        this.setTitle(Constants.ADD_NEWCOMER_DIALOG_TITLE);
        this.setHeaderText(Constants.ADD_NEWCOMER_DIALOG_HEADER);
        this.setContentText(Constants.ADD_NEWCOMER_DIALOG_CONTENT);

        this.dataRecorder = dataRecorder;
    }

    public boolean showAndProcessResult()
    {
        boolean didSaveOccur = false;
        Optional<String> result = this.showAndWait();
        if (result.isPresent())
        {
            String newcomerName = Utility.formatName(result.get());
            if (!newcomerName.isEmpty())
            {
                didSaveOccur = this.saveNewcomer(newcomerName);
            }
            else
            {
                this.simpleDialogCreator.showSimpleDialog(AlertType.WARNING,
                        Constants.ADD_NEWCOMER_DIALOG_WARNING);
            }
        }
        return didSaveOccur;
    }

    private boolean saveNewcomer(String newcomerName)
    {
        logger.info("AddNewcomerDialog result is present: [{}]", newcomerName);
        boolean isSuccess = false;
        try
        {
            this.dataRecorder.saveNewcomerToCurrentData(newcomerName);
            this.simpleDialogCreator.showSimpleDialog(AlertType.INFORMATION,
                    String.format(Constants.ADD_NEWCOMER_DIALOG_SUCCESS, newcomerName));
            isSuccess = true;
        }
        catch (Exception e)
        {
            logger.error("Error adding newcomer: ", e);
            this.simpleDialogCreator.showSimpleDialog(AlertType.ERROR,
                    String.format(Constants.ADD_NEWCOMER_DIALOG_ERROR, newcomerName,
                            Constants.DATA_FILE_PATH, Constants.EXCLUSION_FILE_PATH,
                            e.getMessage()));
        }
        return isSuccess;
    }
}
