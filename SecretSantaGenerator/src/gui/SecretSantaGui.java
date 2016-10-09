package gui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import data.DataReader;
import data.DataRecorder;
import generator.GenerateException;
import generator.SecretSanta;
import generator.SecretSantaGenerator;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SecretSantaGui extends Application
{
    private final static int MAX_GENERATE_ATTEMPTS = 100;
    private final static String DATA_READ_ERROR = "Cannot locate data.csv.";
    
    private Stage primaryStage;
    private List<SecretSanta> secretSantaList;
    private final List<CheckBox> checkBoxList = new ArrayList<CheckBox>();
    private final Button saveButton = new Button("Save!");

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        this.primaryStage = stage;
        
        try
        {
            this.secretSantaList = DataReader.parseDataFile();
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
        
        this.primaryStage.setScene(new Scene(border, 300, 700));
        this.primaryStage.show();
    }
    
    private FlowPane addCheckBoxes()
    {
        FlowPane flowPane = new FlowPane();
        flowPane.setMinWidth(200);
        flowPane.setPadding(new Insets(10,0,10,0));
        flowPane.setVgap(5);
        
        for(SecretSanta secretSanta : this.secretSantaList)
        {
            CheckBox checkbox = new CheckBox(secretSanta.getName());
            checkbox.setUserData(secretSanta);
            checkbox.setMnemonicParsing(false);
            checkbox.setMinWidth(100);
            checkbox.setMaxWidth(100);
            checkbox.setPrefWidth(100);
            if ((secretSanta.getName()).equals("KEN"))
            {
                checkbox.setSelected(false);
            }
            else
            {
                checkbox.setSelected(true);
            }
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
    
    /**
     * Create table that displays:
     *  Column 1: Name
     *  Column 2: Secret Santa
     * 
     * @return table with assigned secret santas
     */
    private TableView addTable()
    {
        TableView assignedNameTable = new TableView();
        assignedNameTable.setMinHeight(500);
        assignedNameTable.setMaxHeight(500);
        assignedNameTable.setPrefHeight(500);
        
        TableColumn firstNameCol = new TableColumn("Name");
        TableColumn lastNameCol = new TableColumn("Secret Santa");
        
        assignedNameTable.getColumns().addAll(firstNameCol, lastNameCol);
        
        final ObservableList<SecretSantaDisplayType> observableList = 
                FXCollections.observableArrayList(generateObservableList());
        
        firstNameCol.setCellValueFactory(new PropertyValueFactory<SecretSantaDisplayType,
                String>("name"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<SecretSantaDisplayType,
                String>("secretSanta"));
        
        assignedNameTable.setItems(observableList);
        
        assignedNameTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        return assignedNameTable;
    }
    
    /**
     * Generate an observable list (rows) for the secret santa assignment table
     * 
     * @return observable list of secret santa assignments
     */
    private ObservableList<SecretSantaDisplayType> generateObservableList()
    {
        final ObservableList<SecretSantaDisplayType> secretSantaTableList = FXCollections.observableArrayList();
        
        manageAttendees();
        
        SecretSantaGenerator generator = new SecretSantaGenerator(this.secretSantaList);
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
            this.saveButton.setOnAction(new EventHandler<ActionEvent>() {
                
                @Override
                public void handle(ActionEvent event) {
                    try
                    {
                        DataRecorder.save(recordList);
                    }
                    catch (IOException e)
                    {
                        // TODO Auto-generated catch block
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
    
    private void manageAttendees()
    {
        for(CheckBox checkbox : this.checkBoxList)
        {
            // remove from list of not attending
            if(!checkbox.isSelected())
            {
                this.secretSantaList.remove((SecretSanta)checkbox.getUserData());
            }
            else
            {
                if(!this.secretSantaList.contains((SecretSanta)checkbox.getUserData()))
                {
                    // add back to attending list if not already included in list
                    this.secretSantaList.add((SecretSanta)checkbox.getUserData());
                }
            }
        }
    }
    
    private BorderPane addMenuSelectionPane(BorderPane border)
    {
        // TODO work here
        BorderPane historyPane = new BorderPane();
        
        VBox buttonColumn = new VBox(10);
        buttonColumn.setPadding(new Insets(10,10,5,10));
        
//        for(int i = 0; i < 6; i++)
//        {
//            Button button = new Button("testButton");
//            button.setMinWidth(100);
//            button.setMaxWidth(100);
//            button.setPrefWidth(100);
//            buttonColumn.getChildren().add(button);
//        }
        
        Button btn = new Button();
        btn.setText("Generate!");
        btn.setMinWidth(80);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                border.setBottom(addTable());
            }
        });
        buttonColumn.getChildren().add(btn);
        this.saveButton.setMinWidth(80);
        this.saveButton.setDisable(true);
        buttonColumn.getChildren().add(this.saveButton);
        
        historyPane.setLeft(buttonColumn);
        return historyPane;
    }
}
