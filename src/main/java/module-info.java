module ko.fxlogviewer {
    requires javafx.controls;
    requires javafx.fxml;
    
    opens ko.fxlogviewer to javafx.fxml;
    exports ko.fxlogviewer;
}