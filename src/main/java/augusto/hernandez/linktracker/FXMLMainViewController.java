package augusto.hernandez.linktracker;

import augusto.hernandez.linktracker.model.WebPage;
import augusto.hernandez.linktracker.utils.FetchService;
import augusto.hernandez.linktracker.utils.FileUtils;
import augusto.hernandez.linktracker.utils.MessageUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class FXMLMainViewController {
    public MenuItem menuItemStart;
    private ExecutorService executorService;
    public MenuItem menuItemExit;
    public Label labelTotalPagesNumber;
    public Label labelProcessedNumber;
    public Label labelTotalLinksNumber;
    public ListView<WebPage> listViewFuentes;
    public ListView<String> listViewLinks;
    public MenuItem itemLoadFile;
    private final SimpleStringProperty labelProcessed = new SimpleStringProperty("0");
    private final SimpleStringProperty labelTotalLinks = new SimpleStringProperty("0");
    private final AtomicLong processed = new AtomicLong(0);
    private final AtomicLong total_links = new AtomicLong(0);

    private synchronized void addProcessed(String number){
        labelProcessed.set(number);
    }
    private synchronized void addTotalLinks(String number){
        labelTotalLinks.set(number);
    }

    // Esto se llama típicamente después de que los controles han sido inicializados desde el archivo FXML
    @FXML
    public void initialize() {
        // Enlaza las propiedades SimpleStringProperty a los textos de los Labels
        labelTotalLinksNumber.textProperty().bind(labelTotalLinks);
        labelProcessedNumber.textProperty().bind(labelProcessed);
    }

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
                    labelTotalPagesNumber.setText(String.valueOf(listViewFuentes.getItems().size()));
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
    private void scrapWeb() {
        labelTotalPagesNumber.setText(String.valueOf(listViewFuentes.getItems().size()));
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<WebPage> selectedWebPages = listViewFuentes.getItems();
        if (!selectedWebPages.isEmpty()) {
            List<Future<WebPage>> futures = new ArrayList<>();

            for (WebPage webPage : selectedWebPages) {
                Callable<WebPage> callable = new FetchService(webPage);
                Future<WebPage> future = executorService.submit(callable);
                futures.add(future);
            }

            // Usar un ScheduledService para procesar los resultados
            ScheduledService<HashMap<String, String>> scheduledService = new ScheduledService<>() {
                @Override
                protected Task<HashMap<String, String>> createTask() {
                    return new Task<>() {
                        @Override
                        protected HashMap<String, String> call() {
                            boolean allDone = true;
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("processed", "");
                            hashMap.put("total links", "");
                            hashMap.put("All done","NOT DONE");
                            total_links.set(0);
                            processed.set(0);
                            for (Future<WebPage> future : futures) {
                                if (future.isDone()) {
                                    try {
                                        WebPage webPage = future.get();
                                        String links = String.valueOf(total_links.addAndGet(webPage.getLinks().size()));
                                        String processes = String.valueOf(processed.incrementAndGet());
                                        hashMap.replace("processed", processes);
                                        hashMap.replace("total links", links);
                                    } catch (InterruptedException | ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    allDone = false;
                                }
                            }

                            // Si todas las tareas están completas, cancela el ScheduledService
                            if (allDone) {
                                hashMap.put("All done","DONE");
                            }
                            return hashMap;
                        }
                    };
                }
            };

            scheduledService.setOnSucceeded(event -> {
                HashMap<String, String> resultMap = scheduledService.getValue();
                if (resultMap != null) {
                    String processed = resultMap.get("processed");
                    String totalLinks = resultMap.get("total links");
                    String done = resultMap.get("All done");
                    addProcessed(processed);
                    addTotalLinks(totalLinks);
                    if(done.equals("DONE")){
                        scheduledService.cancel();
                        executorService.shutdown();
                        WebPage value = listViewFuentes.getSelectionModel().getSelectedItem();
                            if (value != null) {
                                listViewLinks.setItems(FXCollections.observableList(value.getLinks()));
                            }
                        MessageUtils.showMessage("Links cargados");
                    }
                }
            });
            // Configurar el ScheduledService para ejecutarse una vez después de un breve retardo
            scheduledService.setDelay(Duration.millis(100));
            scheduledService.setPeriod(Duration.millis(100));
            scheduledService.start();
        }

    }

    @FXML
    protected void clear(ActionEvent actionEvent) {
        listViewFuentes.getItems().forEach(WebPage::clear);
        listViewLinks.setItems(FXCollections.observableList(new ArrayList<>()));
        total_links.set(0);
        processed.set(0);
        addTotalLinks("0");
        addProcessed("0");
        labelTotalPagesNumber.setText("0");
        MessageUtils.showMessage("Datos borrados");
    }
}

