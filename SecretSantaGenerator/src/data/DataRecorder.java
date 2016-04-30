package data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import gui.SecretSantaDisplayType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DataRecorder
{
    private final static String COMMA_DELIMITER = ",";
    private final static String NEW_LINE_SEPARATOR = "\n";
    private final static String NEW_FILE_PATH = "resources/current_year_data.csv";
    private final static String DATA_SAVE_ERROR = "Cannot save current_year_data.csv.";
    private final static String FILE_HEADER = "#THIS PERSON, WILL GIFT";

    public static void recordData(List<SecretSantaDisplayType> recordList)
    {
        try
        {
            File file = new File(NEW_FILE_PATH);
            file.delete();
            FileWriter fileWriter = new FileWriter(NEW_FILE_PATH);
            fileWriter.append(FILE_HEADER);
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            for(SecretSantaDisplayType record : recordList)
            {
                fileWriter.append(record.getName());
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(record.getSecretSanta());
                fileWriter.append(NEW_LINE_SEPARATOR);
            }
            
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e)
        {
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(VBoxBuilder.create().children(new Text(DATA_SAVE_ERROR))
                    .alignment(Pos.CENTER).padding(new Insets(5)).build()));
            dialogStage.show();
        }
    }
}
