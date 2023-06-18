package augusto.hernandez.linktracker;

import augusto.hernandez.linktracker.model.WebPage;
import augusto.hernandez.linktracker.utils.FetchService;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class FXMLMainViewController {
    public MenuItem menuItemStart;
    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
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
                    // Agregar un ChangeListener a la selección de modelos de ListView
                    listViewFuentes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue != null) {
                            listViewLinks.setItems(FXCollections.observableList(newValue.getLinks()));
                        }
                    });
                }
            }else{
                MessageUtils.showError("La ruta especificada no es válida");
            }
        }else {
            MessageUtils.showError("No se pudo cargar el archivo");
        }
    }

    @FXML
    private void scrapWeb(ActionEvent actionEvent){
        if (executorService != null && !executorService.isShutdown()) {
            // Si el ExecutorService ya está en ejecución, pausar o reanudar la operación según sea necesario
            if (executorService.) {
                executorService.e();
            } else {
                executorService.pause();
            }
            //TODO terminar esta función de executorService
        } else {
            // Si el ExecutorService no está en ejecución, iniciar la operación de recolección de enlaces
            List<WebPage> selectedWebPages = listViewFuentes.getItems();
            if (!selectedWebPages.isEmpty()) {
                // Crear un ThreadPoolExecutor con un número fijo de hilos
                executorService = Executors.newFixedThreadPool(4);

                // Iniciar FetchService para cada página web seleccionada
                for (WebPage webPage : selectedWebPages) {
                    FetchService fetchService = new FetchService(webPage);
                    executorService.submit(fetchService.createTask());
                }
            }
        }
    }
}

