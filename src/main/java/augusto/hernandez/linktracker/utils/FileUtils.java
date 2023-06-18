package augusto.hernandez.linktracker.utils;

import augusto.hernandez.linktracker.model.WebPage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    static boolean is_created;
    public static Path setFile(String filePath){
        File file = new File(filePath);
        if(file.exists()){
            return file.toPath();
        }
        return null;
    }
    public static List<WebPage> loadPages(Path file){
        try(FileReader reader = new FileReader(file.toFile());
            BufferedReader leer = new BufferedReader(reader)){
            List<WebPage> webPages = new ArrayList<>();
            String texto;
            String[] lista;

            while((texto = leer.readLine()) != null){
                lista = texto.split(";");
                webPages.add(new WebPage(lista[0],lista[1]));
            }
            MessageUtils.showMessage("Se cargaron "+webPages.size()+" registros de páginas");
            return webPages;
        }catch(IOException e){
            MessageUtils.showError("Ocurrió un error al cargar los datos del archivo ${file}");
        }
        return null;
    }
}
