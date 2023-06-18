package augusto.hernandez.linktracker.utils;

import augusto.hernandez.linktracker.model.WebPage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.List;

public class FetchService extends Service<WebPage> {

    private final WebPage webPage;
    public FetchService(WebPage webPage){
        this.webPage = webPage;
    }
    @Override
    public Task<WebPage> createTask() {
        return new Task<>() {
            @Override
            protected WebPage call() {
                List<String> enlaces = LinkReader.getLinks(webPage.getUrl());
                webPage.setLinks(enlaces);
                return webPage;
            }
        };
    }
}
