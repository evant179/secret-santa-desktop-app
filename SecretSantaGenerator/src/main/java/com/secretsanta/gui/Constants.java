package com.secretsanta.gui;

/**
 * Utility class for constants
 */
public final class Constants
{
    public static final String ADDITIONAL_RESOURCES_DIR = "additionalresources";
    public static final String INPUT_DIR = "input";
    public static final String OUTPUT_DIR = "output";
    public static final String DATA_FILE_PATH = "data.csv";
    public static final String EXCLUSION_FILE_PATH = "exclusions.csv";
    public static final String OUTPUT_FILE_PATH = OUTPUT_DIR + "/current_year_data.csv";

    public static final String DIRECTORY_ERROR = "Error reading directory: %s";
    public static final String FILE_ERROR = "Error reading files: %s\n%s";

    public static final String MAIN_DIALOG_REFRESH_ERROR = "Program cannot refresh data. "
            + "Close the program and restart to return to a good state. Tell Evan!\n\n%s";

    public static final String DATA_READ_ERROR = "Cannot read data:\n\n%s";
    public static final String GENERATE_RESULTS_ERROR = "Cannot generate results:\n\n%s";

    public static final String OVERRIDE_ERROR = "Could not map [attendee "
            + "name with overridded result] with [current secret santas]";

    public static final int MAX_GENERATE_ATTEMPTS = 100;

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
