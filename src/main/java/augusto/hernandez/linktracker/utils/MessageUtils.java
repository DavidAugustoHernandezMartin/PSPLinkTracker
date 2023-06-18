package augusto.hernandez.linktracker.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class MessageUtils {
    public static void showError(String message){
        new Alert(Alert.AlertType.ERROR,message, ButtonType.CLOSE).show();
    }
    public static void showMessage(String message){
        new Alert(Alert.AlertType.INFORMATION,message, ButtonType.OK).show();
    }
    // public static void fatalError(String message){}

    public static boolean approvalRequest(String message) {
        Optional<ButtonType> respuesta = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO)
                .showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.YES;
    }
}

