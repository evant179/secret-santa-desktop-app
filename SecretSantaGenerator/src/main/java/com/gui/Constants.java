package com.gui;

public final class Constants
{
    public static final String DATA_FILE_PATH = "resources/data.csv";
    public static final String EXCLUSION_FILE_PATH = "resources/exclusions.csv";
    public static final String OUTPUT_FILE_PATH = "resources/current_year_data.csv";

    public static final String MAIN_DIALOG_REFRESH_ERROR = "Program cannot refresh data. "
            + "Close the program and restart to return to a good state. Tell Evan!\n\n%s";

    public static final String DATA_READ_ERROR = "Cannot read data from data file [%s] "
            + "and exclusion file [%s]:\n\n%s";

    public static final String EXCLUSION_BUTTON_NAME = "Edit Exclusions";

    public static final String OVERRIDE_BUTTON_ENABLE = "Enable Override Mode";
    public static final String OVERRIDE_BUTTON_DISABLE = "Disable Override Mode";

    public static final String ADD_NEWCOMER_DIALOG_TITLE = "Add Newcomer";
    public static final String ADD_NEWCOMER_DIALOG_HEADER = "Add Newcomer!";
    public static final String ADD_NEWCOMER_DIALOG_CONTENT = "Enter name:";
    public static final String ADD_NEWCOMER_DIALOG_WARNING = "No name entered. No newcomer was added.";
    public static final String ADD_NEWCOMER_DIALOG_SUCCESS = "[%s] successfully added! Data will refresh.";
    public static final String ADD_NEWCOMER_DIALOG_ERROR = "Error occured when adding [%s] to data file [%s] "
            + "and exclusion file [%s]:\n\n%s";

    // ============ exclusion constants ============
    public static final String GENERIC_EXCLUSION_ERROR = "Error occured when editing exclusions:\n\n%s";

    public static final String ATTENDEE_CHOICE_DIALOG_TITLE = "Name Selection";
    public static final String ATTENDEE_CHOICE_DIALOG_HEADER = "Select a person to edit their exclusions";
    public static final String ATTENDEE_CHOICE_DIALOG_CONTENT = "Select name:";

    public static final String EDIT_EXCLUSION_DIALOG_TITLE = "Edit Exclusions";
    public static final String EDIT_EXCLUSION_DIALOG_HEADER = "Edit exclusions for [%s]";
    public static final String EDIT_EXCLUSION_DIALOG_SUCCESS = "Exclusions successfully updated for [%s]! "
            + "Data will refresh.";
}
