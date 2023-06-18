module augusto.hernandez.linktracker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens augusto.hernandez.linktracker to javafx.fxml;
    exports augusto.hernandez.linktracker;
    opens augusto.hernandez.linktracker.model to  javafx.fxml;
    exports augusto.hernandez.linktracker.model;
}