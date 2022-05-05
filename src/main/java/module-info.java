module ko.fxlogviewer2 {
    requires javafx.controls;
    requires javafx.fxml;
    
    opens ko.fxlogviewer2 to javafx.fxml;
    exports ko.fxlogviewer2;
}