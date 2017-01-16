package com.secretsanta.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secretsanta.data.CsvFactory;
import com.secretsanta.data.DataReader;
import com.secretsanta.data.DataRecorder;
import com.secretsanta.data.ExclusionReader;
import com.secretsanta.data.InputFileLocator;
import com.secretsanta.generator.GenerateException;
import com.secretsanta.generator.ResultGenerator;
import com.secretsanta.generator.SecretSanta;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main Secret Santa Generator class
 */
public class SecretSantaGui extends Application
{
    private static final Logger logger = LoggerFactory.getLogger(SecretSantaGui.class);

    // ===== file handlers =====
    private InputFileLocator inputFileLocator;
    private DataRecorder dataRecorder;
    private DataReader dataReader;
    private ExclusionReader exclusionReader;

    // ===== checkboxes =====    
    private final FlowPane checkBoxesPane = new FlowPane();
    private final List<CheckBox> checkBoxList = new ArrayList<CheckBox>();

    // ===== buttons =====
    private final Button generateButton = new Button("Generate!");
    private final Button resetButton = new Button("Clear Results");
    private final Button saveButton = new Button("Save!");
    private final Button addNewcomerButton = new Button("Add Newcomer");
    private final Button editExclusionButton = new Button(
            Constants.EXCLUSION_BUTTON_NAME);
    private final ToggleButton overrideToggle = new ToggleButton(
            Constants.OVERRIDE_BUTTON_ENABLE);

    // ===== main table display =====
    private MainTableView mainTableView;

    // ===== popup dialog utility =====
    private final SimpleDialogCreator simpleDialogCreator = new SimpleDialogCreator();

    /**
     * Launch program
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        launch(args);
    }

    /*
     * Program entry point for main stage (home screen) set up
     * 
     * (non-Javadoc)
     * 
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage mainStage) throws Exception
    {
        final List<SecretSantaDisplayType> secretSantaDisplayList;
        try
        {
            // locate input files
            this.inputFileLocator = new InputFileLocator();
            List<Path> inputFileList = this.inputFileLocator.findInputFiles(
                    Constants.ADDITIONAL_RESOURCES_DIR, Constants.INPUT_DIR);
            Path dataPath = inputFileList.stream()
                    .filter(file -> file.toString().contains(Constants.DATA_FILE_PATH))
                    .findAny().orElse(null);
            Path exclusionPath = inputFileList.stream()
                    .filter(file -> file.toString().contains(Constants.EXCLUSION_FILE_PATH))
                    .findAny().orElse(null);

            // create single instance of csv factory for use by all file handlers
            CsvFactory csvFactory = new CsvFactory(dataPath.toString(),
                    exclusionPath.toString(), Constants.OUTPUT_FILE_PATH);

            // instantiate file handlers
            this.exclusionReader = new ExclusionReader(csvFactory);
            this.dataReader = new DataReader(csvFactory, this.exclusionReader);
            this.dataRecorder = new DataRecorder(csvFactory, this.dataReader);

            // set up data for main table
            secretSantaDisplayList = this.dataReader.parseRawDataFileWithExclusions();
            this.mainTableView = new MainTableView(this.dataReader,
                    secretSantaDisplayList);
        }
        catch (Exception e)
        {
            logger.error("Cannot read file: ", e);
            this.simpleDialogCreator.showSimpleDialog(AlertType.ERROR,
                    String.format(Constants.DATA_READ_ERROR, e.getMessage()));
            return; // do not continue program if files cannot be read
        }

        // configure panes
        BorderPane border = new BorderPane(); // TODO learn to use GridPane
        border.setRight(this.addMenuSelectionPane());
        this.initializeCheckBoxesPane(secretSantaDisplayList);
        border.setCenter(this.checkBoxesPane);
        border.setBottom(this.mainTableView);

        // configure main stage
        mainStage.setTitle("Secret Santa Generator");
        mainStage.setScene(new Scene(border, 1200, 750));
        mainStage.show();
    }

    /**
     * Initialize pane for checkboxes
     * 
     * @param secretSantaDisplayList
     *            Attendees to be loaded for each checkbox
     */
    private void initializeCheckBoxesPane(
            List<SecretSantaDisplayType> secretSantaDisplayList)
    {
        this.checkBoxesPane.setMinWidth(200);
        this.checkBoxesPane.setPadding(new Insets(10, 0, 10, 0));
        this.checkBoxesPane.setVgap(5);

        this.loadCheckBoxList(secretSantaDisplayList);
        this.checkBoxesPane.getChildren().addAll(this.checkBoxList);
    }

    /**
     * Load and set up checkbox behavior
     * 
     * @param secretSantaDisplayList
     *            Attendees to be loaded for each checkbox
     */
    private void loadCheckBoxList(List<SecretSantaDisplayType> secretSantaDisplayList)
    {
        this.checkBoxList.clear();

        for (SecretSantaDisplayType secretSanta : secretSantaDisplayList)
        {
            // configure checkbox
            CheckBox checkbox = new CheckBox(secretSanta.getName());
            checkbox.setUserData(secretSanta);
            checkbox.setMnemonicParsing(false);
            checkbox.setMinWidth(100);
            checkbox.setMaxWidth(100);
            checkbox.setPrefWidth(100);
            checkbox.setSelected(true);
            checkbox.selectedProperty().addListener((arg, oldValue, newValue) ->
            {
                if (newValue)
                {
                    // display attendee on main table when selected
                    SecretSantaDisplayType attendee = (SecretSantaDisplayType) checkbox
                            .getUserData();
                    logger.info("include attendee [{}]", attendee.getName());
                    mainTableView.addAttendee(attendee);
                }
                else
                {
                    // remove attendee from main table when deselected
                    SecretSantaDisplayType attendee = (SecretSantaDisplayType) checkbox
                            .getUserData();
                    logger.info("remove attendee [{}]", attendee.getName());
                    mainTableView.removeAttendee(attendee.getName());
                }
            });

            this.checkBoxList.add(checkbox);
        }
    }

    private void setDisableCheckBoxes(boolean isDisable)
    {
        for (CheckBox checkBox : this.checkBoxList)
        {
            checkBox.setDisable(isDisable);
        }
    }

    /**
     * Button handler for generating results and updating main table
     */
    private void updateSecretSantasOnMainTableView()
    {
        try
        {
            // make final copy of map to pass into table for viewing
            final Map<String, String> attendeeToResultMap = new HashMap<>(
                    this.generateSecretSantasBasedOnCurrentState());
            this.mainTableView.updateSecretSantasWithResults(attendeeToResultMap);
        }
        catch (Exception e)
        {
            logger.error("Cannot generate results: ", e);
            this.simpleDialogCreator.showSimpleDialog(AlertType.ERROR,
                    String.format(Constants.GENERATE_RESULTS_ERROR, e.getMessage()));
            return;
        }
    }

    /**
     * Generate results for secret santa attendees based on program's current
     * state, which includes: checked attendees, overridden results
     * 
     * @return Map with [attendee name as the key] and [corresponding result
     *         name as the value]
     * @throws FileNotFoundException
     * @throws IOException
     * @throws GenerateException
     */
    private Map<String, String> generateSecretSantasBasedOnCurrentState()
            throws FileNotFoundException, IOException, GenerateException
    {
        final ResultGenerator generator = new ResultGenerator();
        Map<String, String> attendeeToResultMap = new HashMap<String, String>();

        // read in all secret santas and set up for generation
        final List<SecretSanta> secretSantaList = this.dataReader
                .parseDataFileWithExclusionFile(true);
        // modify secret santa list to only contain attendees
        this.manageAttendees(secretSantaList);
        // override results for the rigged secret santas
        this.overrideSecretSantaSelections(secretSantaList);

        // wrap generation call. due to its behavior, an impossible name generation
        // scenario may be ran into.
        // attempt X times before forcing a failure
        int numAttempts = 0;
        while (numAttempts < Constants.MAX_GENERATE_ATTEMPTS)
        {
            logger.info("-----Generate attempt #: [{}] -----", numAttempts + 1);
            try
            {
                // attempt generation
                attendeeToResultMap = generator.generateSecretSantas(secretSantaList);
                break;
            }
            catch (GenerateException e)
            {
                numAttempts++;
                if (numAttempts == Constants.MAX_GENERATE_ATTEMPTS)
                {
                    // force failure since generation exceeded X number of attempts
                    throw e;
                }
            }
        }

        // if there are items in displayList, then enable save button
        if (attendeeToResultMap.size() > 0)
        {
            this.saveButton.setDisable(false);
            // make final copy of map to pass into separate thread for saving
            final Map<String, String> attendeeToResultMapCopy = new HashMap<>(
                    attendeeToResultMap);
            this.saveButton.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    try
                    {
                        // when Save button is pressed, save generation results
                        dataRecorder.saveGenerationResults(attendeeToResultMapCopy);
                        logger.info("Successfully saved current year data");
                        simpleDialogCreator.showSimpleDialog(AlertType.INFORMATION,
                                String.format("Successfully saved to [%s] !",
                                        Constants.OUTPUT_FILE_PATH));
                        saveButton.setDisable(true);
                    }
                    catch (Exception e)
                    {
                        logger.error("Error saving current year data: ", e);
                        simpleDialogCreator.showSimpleDialog(AlertType.ERROR,
                                "Error saving current year data.");
                    }
                }
            });
        }
        else
        {
            this.saveButton.setDisable(true);
        }

        // TODO a new method that checks if everyone is accounted for (DataValidator)

        return attendeeToResultMap;
    }

    /**
     * Update list of attendees of type {@link SecretSanta} based on checkboxes
     * 
     * @param secretSantaList
     *            Attendee list updated by reference
     */
    private void manageAttendees(List<SecretSanta> secretSantaList)
    {
        for (CheckBox checkbox : this.checkBoxList)
        {
            // remove from list of not attending
            if (!checkbox.isSelected())
            {
                SecretSantaDisplayType attendee = (SecretSantaDisplayType) checkbox
                        .getUserData();
                boolean isSuccess = secretSantaList
                        .removeIf(a -> a.getName().equals(attendee.getName()));
                logger.info("manage remove attendee of [{}] is [{}]", attendee.getName(),
                        isSuccess ? "SUCCESS" : "FAILURE");
            }
        }
    }

    /**
     * Update list of attendees of type {@link SecretSanta} based on overriden
     * results
     * 
     * @param secretSantas
     *            Attendee list updated by reference
     */
    private void overrideSecretSantaSelections(List<SecretSanta> secretSantas)
    {
        // from the main table, fetch map of [attendee name] to [overridden result name]
        Map<String, String> nameToOverriddenSelectedNameMap = this.mainTableView
                .getAttendeeNameToOverridenResultNameMap();

        logger.info("!!!!!!!!!!!!!!!!!! number of OVERRIDES detected: [{}]",
                nameToOverriddenSelectedNameMap.size());
        nameToOverriddenSelectedNameMap.forEach((attendeeName, overriddenResultName) ->
        {
            // find matching secret santa to update
            SecretSanta secretSanta = secretSantas.stream()
                    .filter(attendeeType -> attendeeName.equals(attendeeType.getName()))
                    .findAny().orElseThrow(
                            () -> new IllegalStateException(Constants.OVERRIDE_ERROR));
            logger.info("!!!!!!!!!!!!!!!!!! [{}] is OVERRIDDEN with [{}]",
                    secretSanta.getName(), overriddenResultName);
            secretSanta.setOverridenSelection(overriddenResultName);
        });
    }

    /**
     * Create pane home screen's buttons
     * 
     * @return
     */
    private BorderPane addMenuSelectionPane()
    {
        BorderPane buttonPane = new BorderPane();

        // ============= first button column =============
        VBox firstButtonColumn = new VBox(10);
        firstButtonColumn.setPadding(new Insets(10, 10, 5, 10));

        // generate button
        this.generateButton.setMinWidth(80);
        this.generateButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                generateButton.setDisable(true);
                addNewcomerButton.setDisable(true);
                editExclusionButton.setDisable(true);
                overrideToggle.setDisable(true);
                setDisableCheckBoxes(true);
                updateSecretSantasOnMainTableView();
            }
        });
        firstButtonColumn.getChildren().add(this.generateButton);

        // reset button
        this.resetButton.setMinWidth(80);
        this.resetButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                mainTableView.resetCurrentYearColumnSelections();
                generateButton.setDisable(false);
                addNewcomerButton.setDisable(false);
                editExclusionButton.setDisable(false);
                overrideToggle.setDisable(false);
                saveButton.setDisable(true);
                setDisableCheckBoxes(false);
                // TODO check all checkboxes too? verify listenrs are hit
            }
        });
        firstButtonColumn.getChildren().add(resetButton);

        // save button
        this.saveButton.setMinWidth(80);
        this.saveButton.setDisable(true);
        firstButtonColumn.getChildren().add(this.saveButton);

        // set button column to left of border pane
        buttonPane.setLeft(firstButtonColumn);

        // ============= second button column =============
        VBox secondButtonColumn = new VBox(10);
        secondButtonColumn.setPadding(new Insets(10, 10, 5, 10));

        // newcomer button
        this.addNewcomerButton.setMinWidth(80);
        this.addNewcomerButton.setOnAction(e -> this.processNewcomer());
        secondButtonColumn.getChildren().add(this.addNewcomerButton);

        // exclusion button
        this.editExclusionButton.setMinWidth(80);
        this.editExclusionButton.setOnAction(e -> this.processExclusions());
        secondButtonColumn.getChildren().add(this.editExclusionButton);

        // override toggle button
        this.overrideToggle.setMinWidth(80);
        this.overrideToggle.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                toggleOverrideMode();
            }
        });
        secondButtonColumn.getChildren().add(this.overrideToggle);

        // set button column to right of border pane
        buttonPane.setRight(secondButtonColumn);

        return buttonPane;
    }

    /**
     * Button handler for adding newcomer
     */
    private void processNewcomer()
    {
        AddNewcomerDialog addNewcomerDialog = new AddNewcomerDialog(this.dataRecorder);
        boolean didSaveOccur = addNewcomerDialog.showAndProcessResult();
        if (didSaveOccur)
        {
            this.refreshProgram();
        }
        else
        {
            logger.info("No newcomer saved. No refresh needed.");
        }
    }

    /**
     * Button handler for editting exclusions
     */
    private void processExclusions()
    {
        try
        {
            // read data and exclusions files (rather than get current state because
            // the tables will be missing data if checkboxes are unchecked)
            List<SecretSanta> secretSantaList = this.dataReader
                    .parseDataFileWithExclusionFile(false);
            // ask user to select which secret santa's exclusions to edit
            AttendeeChoiceDialog attendeeChoiceDialog = new AttendeeChoiceDialog(
                    secretSantaList);
            Optional<SecretSanta> result = attendeeChoiceDialog.showAndWait();
            if (result.isPresent())
            {
                SecretSanta attendeeToEdit = result.get();
                logger.info("AttendeeChoiceDialog result is present for exclusion "
                        + "edit: [{}]", attendeeToEdit.getName());
                EditExclusionDialog editExclusionDialog = new EditExclusionDialog(
                        attendeeToEdit, this.extractListOfNames(secretSantaList));
                Optional<SecretSanta> updatedResult = editExclusionDialog.showAndWait();
                if (updatedResult.isPresent())
                {
                    // save updated exclusions for selected secret santa
                    this.dataRecorder.updateExclusionFile(updatedResult.get());
                    this.simpleDialogCreator.showSimpleDialog(AlertType.INFORMATION,
                            String.format(Constants.EDIT_EXCLUSION_DIALOG_SUCCESS,
                                    updatedResult.get().getName()));
                    this.refreshProgram();
                }
                else
                {
                    logger.info("No selection occured for edit exclusions.");
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Error editting exclusions: ", e);
            this.simpleDialogCreator.showSimpleDialog(AlertType.ERROR,
                    String.format(Constants.GENERIC_EXCLUSION_ERROR, e.getMessage()));
        }
    }

    private List<String> extractListOfNames(List<SecretSanta> secretSantaList)
    {
        List<String> nameList = new ArrayList<String>();
        secretSantaList.forEach(s -> nameList.add(s.getName()));
        return nameList;
    }

    /**
     * Refresh program's data: 1. Checkboxes 2. Table
     * 
     * Should be called after ANY modification to data.csv and exclusions.csv
     */
    private void refreshProgram()
    {
        logger.info("Start refreshing program with updated data");
        try
        {
            // read updated data file
            final List<SecretSantaDisplayType> secretSantaDisplayList = this.dataReader
                    .parseRawDataFileWithExclusions();

            // update checkboxes
            this.loadCheckBoxList(secretSantaDisplayList);
            this.checkBoxesPane.getChildren().clear();
            this.checkBoxesPane.getChildren().addAll(this.checkBoxList);

            // update table
            this.mainTableView.refreshTableData(secretSantaDisplayList);
            logger.info("Successfully refreshed program with updated data");
        }
        catch (IOException e)
        {
            logger.error("Error refreshing program: ", e);
            this.simpleDialogCreator.showSimpleDialog(AlertType.ERROR,
                    String.format(Constants.MAIN_DIALOG_REFRESH_ERROR, e.getMessage()));
        }
    }

    private void toggleOverrideMode()
    {
        this.mainTableView.toggleEditMode();
        boolean isEditMode = this.mainTableView.isEditable();
        this.setDisableCheckBoxes(isEditMode);
        this.generateButton.setDisable(isEditMode);
        this.resetButton.setDisable(isEditMode);
        this.addNewcomerButton.setDisable(isEditMode);
        this.editExclusionButton.setDisable(isEditMode);
        this.overrideToggle.setText(isEditMode ? Constants.OVERRIDE_BUTTON_DISABLE
                : Constants.OVERRIDE_BUTTON_ENABLE);
    }
}