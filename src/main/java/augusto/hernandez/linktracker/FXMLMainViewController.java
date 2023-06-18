package augusto.hernandez.linktracker;

import augusto.hernandez.linktracker.model.WebPage;
import augusto.hernandez.linktracker.utils.FileUtils;
import augusto.hernandez.linktracker.utils.MessageUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FXMLMainViewController {
    public MenuItem menuItemExit;
    public Label labelTotalPagesNumber;
    public Label labelProcessedNumber;
    public Label labelTotalLinksNumber;
    public ListView<WebPage> listViewFuentes;
    public ListView<String> listViewLinks;
    public MenuItem itemLoadFile;

    @FXML
    protected void exit() {
        Platform.exit();
    }

    @FXML
    protected void chooseFile(ActionEvent event){
        FileChooser fileChooser = new FileChooser();

        // Agregar filtro de extensión para archivos .txt
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        // Puedes obtener la ventana principal desde el evento para usarla como propietario del FileChooser
        Window ownerWindow = ((MenuItem)event.getTarget()).getParentPopup().getScene().getWindow();

        File selectedFile = fileChooser.showOpenDialog(ownerWindow);

        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            Path path;
            if((path = FileUtils.setFile(filePath)) != null){
                List<WebPage> pages = FileUtils.loadPages(path);
                ObservableList<WebPage> webPages = FXCollections.observableList(pages != null ? pages : new ArrayList<>());
                if(!webPages.isEmpty()){
                    listViewFuentes.setItems(webPages);
                }
            }else{
                MessageUtils.showError("La ruta especificada no es válida");
            }
        }else {
            MessageUtils.showError("No se pudo cargar el archivo");
        }
    }
}