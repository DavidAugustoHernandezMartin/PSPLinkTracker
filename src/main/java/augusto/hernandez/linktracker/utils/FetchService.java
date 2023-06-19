package augusto.hernandez.linktracker.utils;

import augusto.hernandez.linktracker.model.WebPage;

import java.util.List;
import java.util.concurrent.Callable;

public class FetchService implements Callable<WebPage> {

    private final WebPage webPage;

    public FetchService(WebPage webPage) {
        this.webPage = webPage;
    }

    @Override
    public WebPage call() {
        List<String> enlaces = LinkReader.getLinks(webPage.getUrl());
        webPage.setLinks(enlaces);
        return webPage;
    }
}
