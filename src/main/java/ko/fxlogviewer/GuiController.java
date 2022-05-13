package ko.fxlogviewer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import ko.fxlogviewer.readers.GPUZLogReader;
import ko.fxlogviewer.readers.GigabyteXtremeGammingEngineLogReader;
import ko.fxlogviewer.readers.HWiNFOLogReader;
import ko.fxlogviewer.readers.MsiAfterburnerLogReader;
import ko.fxlogviewer.readers.SpeedFanLogReader;
import ko.fxlogviewer.readers.inter.LogReader;

public class GuiController implements Initializable {

    final String windowTitle = "FX Log Viewer 2";

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
    SplitPane chartsContainer;


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

            App.mainStage.setTitle(String.format("%s - %s", windowTitle, file.getAbsolutePath()));

            chartsContainer.getItems().clear();
            switch (programID) {
                case "GPU-Z":
                    r = new GPUZLogReader(file.getAbsolutePath());
                    break;
                case "HWiNFO":
                    r = new HWiNFOLogReader(file.getAbsolutePath());
                    break;
                case "MsiAfterburner":
                    r = new MsiAfterburnerLogReader(file.getAbsolutePath());
                    break;
                case "GXGE":
                    r = new GigabyteXtremeGammingEngineLogReader(file.getAbsolutePath());
                    break;
                case "SpeedFan":
                    r = new SpeedFanLogReader(file.getAbsolutePath());
                    break;
                default:
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Unknown application");
                    alert.setContentText("Application " + programID + " is not supported.");
                    return;
            }

            try {
                this.columns = r.getHeaderColumns();
                this.data = r.getData();


                ChartComponent m = new ChartComponent(columns, filteredData);
                m.setPrefWidth(Double.MAX_VALUE);
                chartsContainer.getItems().addAll(m);
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
            chartsContainer.getItems().addAll(m);
        }
    }

    public void removeChart(ChartComponent c) {
        chartsContainer.getItems().remove(c);
    }

    @FXML
    private void changePrecision(@SuppressWarnings("unused") ActionEvent event) {
        this.updatePrecision();
    }

    private void updatePrecision() {
        filteredData.clear();

        double width = Screen.getPrimary().getBounds().getMaxX();

        int ratio = (data.size() < width) ? 1 : (int) Math.ceil((data.size() / width) * 2);

        this.filteredData = new ArrayList<>();
        for (var i = 0; i < this.data.size(); i += ratio) {
            filteredData.add(this.data.get(i));
        }

        for (Node x : chartsContainer.getItems()) {
            ChartComponent chart = (ChartComponent) x;
            chart.data = filteredData;
            chart.updateFirstChoiceBox(null);
            chart.updateSecondChoiceBox(null);
            chart.updateThirdChoiceBox(null);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        App.mainStage.widthProperty().addListener((obs, oldVal, newVal) -> {

            ArrayList<ChartComponent> list = new ArrayList<>();
            for (Node x : chartsContainer.getItems()) {
                ChartComponent chart = (ChartComponent) x;
                list.add(chart);
            }
            chartsContainer.getItems().clear();
            final Timeline animation = new Timeline(
                    new KeyFrame(Duration.seconds(.5),
                            actionEvent -> {
                                for (ChartComponent x : list) {
                                    chartsContainer.getItems().add(x);

                                }
                            }

                    ));
            animation.setCycleCount(1);
            animation.play();


        });

        App.mainStage.heightProperty().addListener((obs, oldVal, newVal) -> {


            ArrayList<ChartComponent> list = new ArrayList<>();
            for (Node x : chartsContainer.getItems()) {
                ChartComponent chart = (ChartComponent) x;
                list.add(chart);
            }
            chartsContainer.getItems().clear();
            final Timeline animation = new Timeline(
                    new KeyFrame(Duration.seconds(.5),
                            actionEvent -> {
                                for (ChartComponent x : list) {
                                    chartsContainer.getItems().add(x);

                                }
                            }

                    ));
            animation.setCycleCount(1);
            animation.play();
        });

    }

}