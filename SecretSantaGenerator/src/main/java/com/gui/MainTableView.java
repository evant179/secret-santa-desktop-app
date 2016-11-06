package com.gui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.data.DataReader;
import com.utility.Utility;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

/**
 * Table for viewing secret santa data
 */
public class MainTableView extends TableView<SecretSantaDisplayType2>
{
    private static final Logger logger = LoggerFactory.getLogger(MainTableView.class);

    private DataReader dataReader = new DataReader();

    private static final String CURRENT_YEAR_COLUMN_NAME = "%s [Current Year]";
    private int currentYearIndex;

    /**
     * Constructor
     * 
     * @param secretSantaDisplayList
     *            Secret santas to display
     * @throws FileNotFoundException
     * @throws IOException
     */
    public MainTableView(List<SecretSantaDisplayType2> secretSantaDisplayList)
            throws FileNotFoundException, IOException
    {
        this.setEditable(true); // allows dropdown boxes to appear on "current year" column

        this.setMinHeight(700);
        this.setMaxHeight(700);
        this.setPrefHeight(700);

        this.constructColumns();

        this.constructRows(secretSantaDisplayList);

        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void resetCurrentYearColumnSelections()
    {
        //        this.debugCurrentYearSelection("before reset button");

        logger.info("reset table");
        // reset selected values to null for current year
        for (SecretSantaDisplayType2 displayType : this.getItems())
        {
            displayType.getSecretSantaList().get(this.currentYearIndex).setValue(null);
        }

        // doing this kind of refresh breaks the program if done when user is in
        // editting mode for a dropdown!
        //                this.getItems().removeAll(this.getItems());
        //                this.getItems().addAll(FXCollections
        //                        .observableArrayList(this.secretSantaDisplayList));

        this.refresh();

        //        this.debugCurrentYearSelection("after reset button");
    }

    public void addAttendee(SecretSantaDisplayType2 attendee)
    {
        // read from data file, parsing for specific name
        this.getItems().add(attendee);
        logger.info("addAttendee [{}] ", attendee.getName());
        this.resetCurrentYearColumnSelections();
    }

    public void removeAttendee(String name)
    {
        boolean isSuccess = this.getItems().removeIf(s -> s.getName().equals(name));

        String result = isSuccess ? "SUCCESS" : "FAILURE";
        logger.info("removeAttendee of [{}] is [{}]", name, result);
        this.resetCurrentYearColumnSelections();
    }

    public void updateSecretSantasWithResults(
            ObservableList<SecretSantaDisplayType> oldDisplayTypeList)
    {
        logger.info("update table with results");
        for (SecretSantaDisplayType oldType : oldDisplayTypeList)
        {
            for (SecretSantaDisplayType2 row : this.getItems())
            {
                if (oldType.getName().equals(row.getName()))
                {
                    row.getSecretSantaList().get(this.currentYearIndex)
                            .setValue(oldType.getSecretSanta());
                    logger.info("after update result: [{}] : [{}]", row.getName(), row
                            .getSecretSantaList().get(this.currentYearIndex).getValue());
                    break;
                }
            }
        }

        TableColumn<SecretSantaDisplayType2, String> lastColumn = (TableColumn<SecretSantaDisplayType2, String>) // cast
        this.getColumns().get(getColumns().size() - 1);
        lastColumn.setCellValueFactory(cellData -> cellData.getValue()
                .getSecretSantaList().get(this.currentYearIndex));

        this.setEditable(false);
        this.refresh();
    }

    private void constructColumns() throws FileNotFoundException, IOException
    {
        List<String> yearList = dataReader.parseYearData(Constants.DATA_FILE_PATH);

        // ------------ set up name column ------------
        TableColumn<SecretSantaDisplayType2, String> nameColumn = new TableColumn<SecretSantaDisplayType2, String>(
                "Name");
        nameColumn.setCellValueFactory(
                new PropertyValueFactory<SecretSantaDisplayType2, String>("name"));
        this.getColumns().add(nameColumn);

        // ------------ set up previous years columns ------------
        TableColumn<SecretSantaDisplayType2, String> prevousYearColumns = new TableColumn<SecretSantaDisplayType2, String>(
                "Previous Years");
        for (int i = 0; i < yearList.size(); i++)
        {
            final int currentIteration = i; // need to make final copy to pass into lambda
            TableColumn<SecretSantaDisplayType2, String> yearColumn = new TableColumn<SecretSantaDisplayType2, String>(
                    yearList.get(currentIteration));
            yearColumn.setCellValueFactory(cellData -> cellData.getValue()
                    .getSecretSantaList().get(currentIteration));
            prevousYearColumns.getColumns().add(yearColumn);
        }
        this.getColumns().add(prevousYearColumns);

        // ------------ set up current year column ------------
        int currentYear = Integer.valueOf(yearList.get(yearList.size() - 1));
        currentYear++; // increment to current year
        logger.info("Current year [{}]", currentYear);
        TableColumn<SecretSantaDisplayType2, String> currentYearColumn = new TableColumn<SecretSantaDisplayType2, String>(
                String.format(CURRENT_YEAR_COLUMN_NAME, currentYear));

        // when selected, enable dropdown
        currentYearColumn.setCellFactory(
                new Callback<TableColumn<SecretSantaDisplayType2, String>, TableCell<SecretSantaDisplayType2, String>>()
                {
                    @Override
                    public TableCell<SecretSantaDisplayType2, String> call(
                            TableColumn<SecretSantaDisplayType2, String> p)
                    {
                        return new ComboBoxCell();
                    }
                });

        // when name from dropdown is selected, update row data
        currentYearColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<SecretSantaDisplayType2, String>>()
                {
                    @Override
                    public void handle(
                            TableColumn.CellEditEvent<SecretSantaDisplayType2, String> t)
                    {
                        SecretSantaDisplayType2 currentSecretSanta = ((SecretSantaDisplayType2) t
                                .getTableView().getItems()
                                .get(t.getTablePosition().getRow()));
                        logger.info("setOnEditCommit, currentSecretSanta: {} : {}",
                                currentSecretSanta.getName(),
                                currentSecretSanta.getSecretSantaList()
                                        .get(currentYearIndex).getValue());
                        currentSecretSanta.getSecretSantaList().get(currentYearIndex)
                                .set(t.getNewValue());
                        logger.info(
                                "setOnEditCommit, currentSecretSanta updated: {} : {}",
                                currentSecretSanta.getName(),
                                currentSecretSanta.getSecretSantaList()
                                        .get(currentYearIndex).getValue());
                    }
                });

        this.getColumns().add(currentYearColumn);
    }

    /**
     * Construct rows for display. Each row is type SecretSantaDisplayType
     * 
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void constructRows(List<SecretSantaDisplayType2> secretSantaDisplayList)
            throws FileNotFoundException, IOException
    {
        // add empty current year data to display list
        for (SecretSantaDisplayType2 displayType : secretSantaDisplayList)
        {
            displayType.getSecretSantaList().add(new SimpleStringProperty());
        }

        final ObservableList<SecretSantaDisplayType2> observableList = FXCollections
                .observableArrayList(secretSantaDisplayList);
        this.setItems(observableList);

        this.currentYearIndex = secretSantaDisplayList.get(0).getSecretSantaList().size()
                - 1;
        logger.info("current year index: {}", this.currentYearIndex);
    }

    private void debugCurrentYearSelection(String message)
    {
        for (SecretSantaDisplayType2 displayType : getItems())
        {
            logger.info("{} - name[{}], current year selection[{}]", message,
                    displayType.getName(),
                    displayType.getSecretSantaList().get(currentYearIndex).getValue());
        }
    }

    private class ComboBoxCell extends TableCell<SecretSantaDisplayType2, String>
    {
        private ComboBox<String> comboBox;

        public ComboBoxCell()
        {
            comboBox = new ComboBox<>();
        }

        @Override
        public void startEdit()
        {
            if (!isEmpty())
            {
                super.startEdit();

                List<String> attendeeList = Utility.convertList(getTableView().getItems(),
                        s -> s.getName());
                logger.info("create dropdown - initial attendees [{}]", attendeeList);
                SecretSantaDisplayType2 currentRow = getTableView().getItems()
                        .get(getIndex());
                attendeeList.remove(currentRow.getName());
                for (SimpleStringProperty previousSecretSanta : currentRow
                        .getSecretSantaList())
                {
                    if (attendeeList.contains(previousSecretSanta.getValue()))
                    {
                        final String name = previousSecretSanta.getValue();
                        attendeeList.remove(name);
                    }
                }
                for (String nameFromExclusionList : currentRow.getExclusionList())
                {
                    if (attendeeList.contains(nameFromExclusionList))
                    {
                        attendeeList.remove(nameFromExclusionList);
                    }
                }
                logger.info("create dropdown - final dropdown [{}]", attendeeList);

                comboBox.setItems(FXCollections.observableList(attendeeList));
                comboBox.getSelectionModel().select(getItem());

                comboBox.setOnAction((event) ->
                {
                    String selectedName = comboBox.getSelectionModel().getSelectedItem();
                    if (selectedName != null && !selectedName.isEmpty())
                    {
                        logger.info("ComboBox Action: {})", selectedName);
                        commitEdit(selectedName);
                    }
                });

                setText(null);
                setGraphic(comboBox);
            }
        }

        @Override
        public void cancelEdit()
        {
            logger.info("cancel edit called");
            super.cancelEdit();

            setText(getTableView().getItems().get(getIndex()).getSecretSantaList()
                    .get(currentYearIndex).getValue());
            setGraphic(null);
        }

        @Override
        public void updateItem(String item, boolean empty)
        {
            super.updateItem(item, empty);

            if (empty)
            {
                setText(null);
                setGraphic(null);
            }
            else
            {
                if (isEditing())
                {
                    setText(null);
                    setGraphic(comboBox);
                }
                else
                {
                    setText(getItem());
                    setGraphic(null);
                }
            }
        }
    }
}

// reference for binding to list of SimpleStringProperties:
//  http://stackoverflow.com/questions/37548835/how-can-i-associate-data-listsimplestringproperty-with-the-table-columns-of-a