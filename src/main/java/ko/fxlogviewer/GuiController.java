package ko.fxlogviewer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;
import ko.fxlogviewer.readers.GPUZLogReader;
import ko.fxlogviewer.readers.GigabyteXtremeGammingEngineLogReader;
import ko.fxlogviewer.readers.HWiNFOLogReader;
import ko.fxlogviewer.readers.MsiAfterburnerLogReader;
import ko.fxlogviewer.readers.SpeedFanLogReader;
import ko.fxlogviewer.readers.inter.LogReader;

public class GuiController implements Initializable {

    public String windowTitle="FX Log Viewer 2";

    private static GuiController singleton;

    public GuiController() {
        App.mainStage.setTitle(windowTitle);
        singleton = this;
    }

    public static GuiController getSingleton() {
        return singleton;
    }

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button openButton;
    @FXML
    SplitPane chartsContrainer;


    ArrayList<String> columns = new ArrayList<>();
    ArrayList<String[]> data = new ArrayList<>();

    ArrayList<String[]> filteredData = new ArrayList<>();

    @FXML
    private void handleOpenGpuzLog(ActionEvent event) {
        openSetLogData("GPU-Z", "GPU-Z log file (*.txt)", "*.txt", "Open GPU-Z Sensor Log");
    }

    @FXML
    private void handleOpenHWiNFOLog(ActionEvent event) {
        openSetLogData("HWiNFO", "HWiNFO log file (*.csv)", "*.csv", "Open HWiNFO Log");
    }

    @FXML
    private void handleOpenMsiAfterburnerLog(ActionEvent event) {
        openSetLogData("MsiAfterburner", "MsiAfterburner log file (*.hml)", "*.hml", "Open MsiAfterburner Log");
    }

    @FXML
    private void handleOpenSpeedFanLog(ActionEvent event) {
        openSetLogData("SpeedFan", "SpeedFan log file (*.csv)", "*.csv", "Open SpeedFan Log");
    }

    @FXML
    private void handleGXGEFanLog(ActionEvent event) {
        openSetLogData("GXGE", "Gigabyte Xtreme Gamming Engine log file (*.txt)", "*.txt",
                "Open Gigabyte Xtreme Gamming Engine Log");
    }

    private void openSetLogData(String programID, String extFilterString, String extFilterString2, String windowTitle) {

        LogReader r;
        Stage stage = (Stage) anchorPane.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        ExtensionFilter ef = new ExtensionFilter(extFilterString, extFilterString2);

        fileChooser.getExtensionFilters().add(ef);
        fileChooser.setTitle(windowTitle);
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {

            App.mainStage.setTitle(String.format("%s - %s",windowTitle,file.getAbsolutePath()));

            chartsContrainer.getItems().clear();
            if (programID.equals("GPU-Z"))
                r = new GPUZLogReader(file.getAbsolutePath());
            else if (programID.equals("HWiNFO"))
                r = new HWiNFOLogReader(file.getAbsolutePath());
            else if (programID.equals("MsiAfterburner"))
                r = new MsiAfterburnerLogReader(file.getAbsolutePath());
            else if (programID.equals("GXGE"))
                r = new GigabyteXtremeGammingEngineLogReader(file.getAbsolutePath());
            else if (programID.equals("SpeedFan"))
                r = new SpeedFanLogReader(file.getAbsolutePath());
            else {

                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Unknown application");
                alert.setContentText("Application " + programID + " is not supported.");
                return;
            }

            try {
                this.columns = r.getHeaderColumns();
                this.data = r.getData();


                ChartComponent m = new ChartComponent(columns, data);
                m.setPrefWidth(Double.MAX_VALUE);
                chartsContrainer.getItems().addAll(m);
                this.updatePrecision();
            } catch (Exception ex) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid file data");
                alert.setContentText("Error" + ex.getMessage());
                alert.showAndWait();
            }

        }

    }

    @FXML
    private void addNewGraph(ActionEvent event) {
        if (data.size() == 0) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Missing log data");
            alert.setContentText("Please choose log data file.");
            alert.showAndWait();
        } else {

            this.updatePrecision();
            ChartComponent m = new ChartComponent(columns, filteredData);
            chartsContrainer.getItems().addAll(m);
        }
    }

    public void removeChart(ChartComponent c) {
        chartsContrainer.getItems().remove(c);
    }

    @FXML
    private void changePrecision(ActionEvent event) {
        this.updatePrecision();

    }

    private void updatePrecision() {
        filteredData.clear();

        double width = Screen.getPrimary().getBounds().getMaxX();

        int ratio = (data.size() < width) ? 1 : (int) Math.ceil(data.size() / width);

        this.filteredData = new ArrayList<>();
        for (var i = 0; i < this.data.size(); i += ratio) {
            filteredData.add(this.data.get(i));
        }

        for (Node x : chartsContrainer.getItems()) {
            ChartComponent chart = (ChartComponent) x;
            chart.data = filteredData;
            chart.updateFirstChoiceBox(null);
            chart.updateSecondChoiceBox(null);
            chart.updateThirdChoiceBox(null);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}