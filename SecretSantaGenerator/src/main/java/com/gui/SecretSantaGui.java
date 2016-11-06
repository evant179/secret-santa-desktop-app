package com.gui;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.data.DataReader;
import com.data.DataRecorder;
import com.generator.GenerateException;
import com.generator.SecretSanta;
import com.generator.SecretSantaGenerator;
import com.opencsv.CSVWriter;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SecretSantaGui extends Application
{
    private static final Logger logger = LoggerFactory.getLogger(SecretSantaGui.class);
    
    private final static int MAX_GENERATE_ATTEMPTS = 100;
    private final static String DATA_READ_ERROR = "Cannot locate data.csv.";
    
    private Stage primaryStage;
    private DataRecorder dataRecorder;
    private DataReader dataReader;
    
    private List<SecretSantaDisplayType2> secretSantaDisplayList;
    private final List<CheckBox> checkBoxList = new ArrayList<CheckBox>();
    private final Button generateButton = new Button("Generate!");
    private final Button saveButton = new Button("Save!");
    private MainTableView mainTableView;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        this.primaryStage = stage;
        this.dataRecorder = new DataRecorder();
        this.dataReader = new DataReader();
        
        try
        {
            this.secretSantaDisplayList = this.dataReader.parseRawDataFileWithExclusions(
                    Constants.DATA_FILE_PATH, Constants.EXCLUSION_FILE_PATH);
            this.mainTableView = new MainTableView(this.secretSantaDisplayList);
        }
        catch (FileNotFoundException e)
        {
            // TODO make nicer error gui window
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(VBoxBuilder.create().children(new Text(DATA_READ_ERROR))
                    .alignment(Pos.CENTER).padding(new Insets(5)).build()));
            dialogStage.show();
            return;
        }

        this.primaryStage.setTitle("Secret Santa Generator");
        BorderPane border = new BorderPane();
        
//        HBox buttonPane = new HBox(19);
//        buttonPane.setPadding(new Insets(5,10,5,10));
        
        
//        border.setTop(buttonPane);
        border.setRight(this.addMenuSelectionPane(border));
//        BorderPane.setAlignment(btn, Pos.CENTER);
        
        border.setCenter(addCheckBoxes());
        
        border.setBottom(this.mainTableView);
        
        this.primaryStage.setScene(new Scene(border, 1200, 700));
        this.primaryStage.show();
    }
    
    private FlowPane addCheckBoxes()
    {
        FlowPane flowPane = new FlowPane();
        flowPane.setMinWidth(200);
        flowPane.setPadding(new Insets(10,0,10,0));
        flowPane.setVgap(5);
        
        for(SecretSantaDisplayType2 secretSanta : this.secretSantaDisplayList)
        {
            CheckBox checkbox = new CheckBox(secretSanta.getName());
            checkbox.setUserData(secretSanta);
            checkbox.setMnemonicParsing(false);
            checkbox.setMinWidth(100);
            checkbox.setMaxWidth(100);
            checkbox.setPrefWidth(100);
//            if ((secretSanta.getName()).equals("KEN"))
//            {
//                checkbox.setSelected(false);
//            }
//            else
//            {
            checkbox.setSelected(true);
//            }
            checkbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                public void changed(ObservableValue<? extends Boolean> ov,
                    Boolean oldValue, Boolean newValue) {
                        if (newValue)
                        {
                            SecretSantaDisplayType2 attendee =
                                    (SecretSantaDisplayType2) checkbox.getUserData();
                            logger.info("include attendee [{}]", attendee.getName());
                            mainTableView.addAttendee(attendee);
                        }
                        else
                        {
                            SecretSantaDisplayType2 attendee =
                                    (SecretSantaDisplayType2) checkbox.getUserData();
                            logger.info("remove attendee [{}]", attendee.getName());
                            mainTableView.removeAttendee(attendee.getName());
                        }
                }
            });
            flowPane.getChildren().add(checkbox);
            this.checkBoxList.add(checkbox);
        }
        
//        // test
//        for(int i = 0; i < 10; i++)
//        {
//            CheckBox checkbox = new CheckBox("test");
//            checkbox.setSelected(true);
//            checkbox.setMinWidth(100);
//            checkbox.setMaxWidth(100);
//            checkbox.setPrefWidth(100);
//            flowPane.getChildren().add(checkbox);
//        }
        
        return flowPane;
    }
    
    
    private void lockCheckBoxes()
    {
        for (CheckBox checkBox : this.checkBoxList)
        {
            checkBox.setDisable(true);
        }
    }
    
    private void unlockCheckBoxes()
    {
        for (CheckBox checkBox : this.checkBoxList)
        {
            checkBox.setDisable(false);
        }
    }
    
    /**
     * Create table that displays:
     *  Column 1: Name
     *  Column 2: Secret Santa
     * 
     * @return table with assigned secret santas
     */
    private void updateSecretSantasOnMainTableView()
    {
        this.mainTableView.updateSecretSantasWithResults(
                this.generateObservableList());
    }
    
    /**
     * Generate an observable list (rows) for the secret santa assignment table
     * 
     * @return observable list of secret santa assignments
     */
    private ObservableList<SecretSantaDisplayType> generateObservableList()
    {
        final ObservableList<SecretSantaDisplayType> secretSantaTableList = FXCollections.observableArrayList();
        
        
        List<SecretSanta> secretSantaList = null;
        try
        {
            secretSantaList = dataReader.parseDataFileWithExclusionFile(
                    Constants.DATA_FILE_PATH, Constants.EXCLUSION_FILE_PATH);
            manageAttendees(secretSantaList);
            this.overrideSecretSantaSelections(secretSantaList);
        }
        catch (IOException e1)
        {
            // TODO throw error window
            e1.printStackTrace();
        }
        
        SecretSantaGenerator generator = new SecretSantaGenerator(secretSantaList);
        List<SecretSantaDisplayType> displayList = new ArrayList<SecretSantaDisplayType>();
        
        int numAttempts = 0;
        while (numAttempts < MAX_GENERATE_ATTEMPTS)
        {
            System.out.println("---Generate attempt #: " + (numAttempts + 1) + "-----");
            try
            {
                displayList = generator.generateSecretSantas();
                break;
            } catch (GenerateException e)
            {
                numAttempts++;
                if (numAttempts == MAX_GENERATE_ATTEMPTS)
                {
                    Stage dialogStage = new Stage();
                    dialogStage.initModality(Modality.APPLICATION_MODAL);
                    dialogStage.setScene(new Scene(VBoxBuilder.create().children(new Text(e.getMessage()))
                            .alignment(Pos.CENTER).padding(new Insets(5)).build()));
                    dialogStage.show();
                    break;
                }
            }
        }
        
        for (SecretSantaDisplayType row : displayList)
        {
            // Test output
            System.out.println(row.getName() + "/////" + row.getSecretSanta());

            secretSantaTableList.add(row);
        }
        
        // if there are items in displayList, then enable save button
        if(displayList.size() > 0)
        {
            this.saveButton.setDisable(false);
            final List<SecretSantaDisplayType> recordList = new ArrayList<SecretSantaDisplayType>(displayList);
            this.saveButton.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event) {
                    try
                    {
                        CSVWriter writer = new CSVWriter(
                                new FileWriter(Constants.OUTPUT_FILE_PATH), ',',
                                CSVWriter.NO_QUOTE_CHARACTER);
                        dataRecorder.save(recordList, Constants.DATA_FILE_PATH,
                                writer);
                        saveButton.setDisable(true);
                    }
                    catch (IOException e)
                    {
                        // TODO show error window
                        e.printStackTrace();
                        System.out.println("ERROR SAVING CURRENT YEAR DATA");
                    }
                }
            });
        }
        else
        {
            this.saveButton.setDisable(true);
        }
        
        // TODO add a SUCCESS/FAIL label that displays after writing
        // a new method that checks if everyone is accounted for
        
        return secretSantaTableList;
    }
    
    private void manageAttendees(List<SecretSanta> secretSantaList)
    {
        for(CheckBox checkbox : this.checkBoxList)
        {
            // remove from list of not attending
            if(!checkbox.isSelected())
            {
                SecretSantaDisplayType2 attendee =
                        (SecretSantaDisplayType2) checkbox.getUserData();
                boolean isSuccess = secretSantaList.removeIf(
                        a -> a.getName().equals(attendee.getName()));
                logger.info("manage remove attendee of [{}] is [{}]",
                        attendee.getName(), isSuccess ? "SUCCESS" : "FAILURE");
            }
        }
    }
    
    private void overrideSecretSantaSelections(List<SecretSanta> secretSantas)
    {
        Map<String, String> nameToOverriddenSelectedNameMap =
                new HashMap<String, String>();
        for(SecretSantaDisplayType2 row : this.mainTableView.getItems())
        {
            int indexForLatestYear = row.getSecretSantaList().size() - 1;
            String overrideenName = row.getSecretSantaList().get(indexForLatestYear).getValue();
            if (overrideenName != null && !overrideenName.isEmpty())
            {
                nameToOverriddenSelectedNameMap.put(row.getName(), overrideenName);
            }
        }
        
        for (SecretSanta secretSanta : secretSantas)
        {
            String riggedName = nameToOverriddenSelectedNameMap.get(secretSanta.getName());
            if (riggedName != null)
            {
                logger.info("SecretSanta: [{}] is rigged with [{}]",
                        secretSanta.getName(), riggedName);
                secretSanta.setOverridenSelection(riggedName);
            }
        }
    }
    
    private BorderPane addMenuSelectionPane(BorderPane border)
    {
        BorderPane buttonPane = new BorderPane();
        
        VBox buttonColumn = new VBox(10);
        buttonColumn.setPadding(new Insets(10,10,5,10));
        
        // generate button
        this.generateButton.setMinWidth(80);
        this.generateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                generateButton.setDisable(true);
                lockCheckBoxes();
                updateSecretSantasOnMainTableView();
            }
        });
        buttonColumn.getChildren().add(this.generateButton);
        
        // reset button
        Button resetButton = new Button();
        resetButton.setText("Reset");
        resetButton.setMinWidth(80);
        resetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mainTableView.resetCurrentYearColumnSelections();
                mainTableView.setEditable(true);
                generateButton.setDisable(false);
                saveButton.setDisable(true);
                unlockCheckBoxes();
                // TODO check all checkboxes too? verify listenrs are hit
            }
        });
        buttonColumn.getChildren().add(resetButton);
        
        // save button
        this.saveButton.setMinWidth(80);
        this.saveButton.setDisable(true);
        buttonColumn.getChildren().add(this.saveButton);
        
        buttonPane.setLeft(buttonColumn);
        return buttonPane;
    }
}
