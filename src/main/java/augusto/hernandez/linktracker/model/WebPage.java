package augusto.hernandez.linktracker.model;

import java.util.ArrayList;
import java.util.List;

public class WebPage {
    private String nombre;
    private String url;

    @Override
    public String toString() {
        return nombre;
    }

    private List<String> links;

    public WebPage(String nombre, String url){
        links = new ArrayList<>();
        this.nombre = nombre;
        this.url = url;
    }
    public void clear(){
        links.clear();
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public List<String> getLinks() {
        return links;
    }
    public void setLinks(List<String> links) {
        this.links = links;
    }
}
