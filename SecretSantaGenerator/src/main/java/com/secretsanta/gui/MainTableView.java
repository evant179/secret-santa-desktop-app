package com.secretsanta.gui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secretsanta.data.DataReader;
import com.secretsanta.utility.Utility;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Table for viewing secret santa data
 */
public class MainTableView extends TableView<SecretSantaDisplayType>
{
    private static final Logger logger = LoggerFactory.getLogger(MainTableView.class);

    private final DataReader dataReader;
    /**
     * Runnable to cancel combobox editting mode when toggling override mode
     */
    private Runnable cancelCombobox = null;

    private static final String CURRENT_YEAR_COLUMN_NAME = "%s [Current Year]";
    private int currentYearIndex;

    /**
     * Constructor
     * 
     * @param dataReader
     * @param secretSantaDisplayList
     *            Secret santas to display
     * @throws FileNotFoundException
     * @throws IOException
     */
    public MainTableView(DataReader dataReader,
            List<SecretSantaDisplayType> secretSantaDisplayList)
            throws FileNotFoundException, IOException
    {
        this.dataReader = dataReader;

        this.setEditable(false);

        this.setMinHeight(750);
        this.setMaxHeight(750);
        this.setPrefHeight(750);
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        this.constructTable(secretSantaDisplayList);
    }

    /**
     * Refresh entire table's data.
     * 
     * Should be called after ANY modification to data.csv and exclusions.csv
     * 
     * @param secretSantaDisplayList
     * @throws IOException
     * @throws FileNotFoundException
     */
    public void refreshTableData(List<SecretSantaDisplayType> secretSantaDisplayList)
            throws FileNotFoundException, IOException
    {
        logger.info("Refresh table");
        this.getColumns().removeAll(this.getColumns());
        this.constructTable(secretSantaDisplayList);
    }

    public void toggleEditMode()
    {
        logger.info("toggleEditMode to [{}]", !this.isEditable());

        // cancel edit mode for combobox if open
        if (cancelCombobox != null)
        {
            cancelCombobox.run();
        }

        // update editable status
        this.setEditable(!this.isEditable());

        // TODO update cell colors

        logger.info("toggleEditMode end");
    }

    public void resetCurrentYearColumnSelections()
    {
        //        this.debugCurrentYearSelection("before reset button");

        logger.info("Reset table");
        // reset selected values to null for current year
        for (SecretSantaDisplayType displayType : this.getItems())
        {
            displayType.getSecretSantaList().get(this.currentYearIndex).setValue(null);
        }

        // doing this kind of refresh breaks the program if done when user is in
        // editting mode for a dropdown!
        //                this.getItems().removeAll(this.getItems());
        //                this.getItems().addAll(FXCollections
        //                        .observableArrayList(this.secretSantaDisplayList));

        // THIS FIXES A BUG
        @SuppressWarnings("unchecked")
        TableColumn<SecretSantaDisplayType, String> lastColumn = (TableColumn<SecretSantaDisplayType, String>) // cast
        this.getColumns().get(getColumns().size() - 1);
        lastColumn.setCellValueFactory(null);

        this.refresh();

        //        this.debugCurrentYearSelection("after reset button");
    }

    public void addAttendee(SecretSantaDisplayType attendee)
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

    /**
     * Get map of [attendee name] to corresponding [overridden result name], if
     * available
     * 
     * @return Map of [attendee name] to [overridden result name]
     */
    public Map<String, String> getAttendeeNameToOverridenResultNameMap()
    {
        // note: for each display type, if a name exists at currentYearIndex,
        // then assume that's the overridden name
        Map<String, String> nameToOverriddenNameMap = this.getItems().stream().filter(
                type -> null != type.getSecretSantaList().get(currentYearIndex).getValue()
                        && !type.getSecretSantaList().get(currentYearIndex).getValue()
                                .isEmpty()) // filter for only selected overrides
                .collect(Collectors.toMap(SecretSantaDisplayType::getName, type -> type
                        .getSecretSantaList().get(currentYearIndex).getValue()));
        // if no overridden result names found, then map returned is empty

        return nameToOverriddenNameMap;
    }

    /**
     * Update table display with generated results
     * 
     * @param attendeeToResultMap
     *            Generated attendee name to result name map
     */
    public void updateSecretSantasWithResults(
            final Map<String, String> attendeeToResultMap)
    {
        logger.info("update table with results");
        attendeeToResultMap.forEach((attendeeName, resultName) ->
        {
            // get matching row based on attendee name
            SecretSantaDisplayType attendeeRow = this.getItems().stream()
                    .filter(row -> attendeeName.equals(row.getName())).findAny()
                    .orElse(null);
            if (attendeeRow != null)
            {
                // update row with corresponding result name
                attendeeRow.getSecretSantaList().get(this.currentYearIndex)
                        .setValue(resultName);
                logger.info("after update result: attendee[{}] : result[{}]",
                        attendeeRow.getName(), attendeeRow.getSecretSantaList()
                                .get(this.currentYearIndex).getValue());
            }
        });

        @SuppressWarnings("unchecked")
        TableColumn<SecretSantaDisplayType, String> lastColumn = (TableColumn<SecretSantaDisplayType, String>) this
                .getColumns().get(getColumns().size() - 1);
        lastColumn.setCellValueFactory(cellData -> cellData.getValue()
                .getSecretSantaList().get(this.currentYearIndex));

        this.setEditable(false);
        this.refresh();
    }

    private void constructTable(List<SecretSantaDisplayType> secretSantaDisplayList)
            throws FileNotFoundException, IOException
    {
        this.constructColumns();
        this.constructRows(secretSantaDisplayList);
    }

    private void constructColumns() throws FileNotFoundException, IOException
    {
        List<String> yearList = this.dataReader.parseYearData();

        // ------------ set up name column ------------
        TableColumn<SecretSantaDisplayType, String> nameColumn = new TableColumn<SecretSantaDisplayType, String>(
                "Name");
        nameColumn.setCellValueFactory(
                new PropertyValueFactory<SecretSantaDisplayType, String>("name"));
        this.getColumns().add(nameColumn);

        // ------------ set up previous years columns ------------
        TableColumn<SecretSantaDisplayType, String> prevousYearColumns = new TableColumn<SecretSantaDisplayType, String>(
                "Previous Years");
        for (int i = 0; i < yearList.size(); i++)
        {
            final int currentIteration = i; // need to make final copy to pass into lambda
            TableColumn<SecretSantaDisplayType, String> yearColumn = new TableColumn<SecretSantaDisplayType, String>(
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
        TableColumn<SecretSantaDisplayType, String> currentYearColumn = new TableColumn<SecretSantaDisplayType, String>(
                String.format(CURRENT_YEAR_COLUMN_NAME, currentYear));

        // when selected, enable dropdown
        currentYearColumn.setCellFactory(p ->
        {
            return new ComboBoxCell();
        });

        // when name from dropdown is selected, update row data
        currentYearColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<SecretSantaDisplayType, String>>()
                {
                    @Override
                    public void handle(
                            TableColumn.CellEditEvent<SecretSantaDisplayType, String> t)
                    {
                        SecretSantaDisplayType currentSecretSanta = ((SecretSantaDisplayType) t
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
    private void constructRows(List<SecretSantaDisplayType> secretSantaDisplayList)
            throws FileNotFoundException, IOException
    {
        // add empty current year data to display list
        for (SecretSantaDisplayType displayType : secretSantaDisplayList)
        {
            displayType.getSecretSantaList().add(new SimpleStringProperty());
        }

        final ObservableList<SecretSantaDisplayType> observableList = FXCollections
                .observableArrayList(secretSantaDisplayList);
        this.setItems(observableList);

        this.currentYearIndex = secretSantaDisplayList.get(0).getSecretSantaList().size()
                - 1;
        logger.info("current year index: {}", this.currentYearIndex);
    }

    @SuppressWarnings("unused")
    private void debugCurrentYearSelection(String message)
    {
        for (SecretSantaDisplayType displayType : getItems())
        {
            logger.info("{} - name[{}], current year selection[{}]", message,
                    displayType.getName(),
                    displayType.getSecretSantaList().get(currentYearIndex).getValue());
        }
    }

    private class ComboBoxCell extends TableCell<SecretSantaDisplayType, String>
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
                logger.info("combobox startEdit");
                super.startEdit();

                List<String> attendeeList = Utility.convertList(getTableView().getItems(),
                        s -> s.getName());
                logger.info("create dropdown - initial attendees [{}]", attendeeList);
                SecretSantaDisplayType currentRow = getTableView().getItems()
                        .get(getIndex());
                logger.info("combobox startEdit for: {}", currentRow.getName());
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
                        logger.info("ComboBox selected name: {})", selectedName);
                        commitEdit(selectedName);
                    }
                });

                cancelCombobox = () ->
                {
                    logger.info("combo call cancelEdit from runnable");
                    cancelEdit();
                };

                setText(null);
                setGraphic(comboBox);
            }
        }

        @Override
        public void cancelEdit()
        {
            logger.info("combobox cancelEdit");
            super.cancelEdit();

            cancelCombobox = null;

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