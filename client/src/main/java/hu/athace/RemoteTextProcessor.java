package hu.athace;

import hu.athace.business.TextProcessor;
import hu.athace.model.Book;
import hu.athace.model.Dictionary;

import java.io.IOException;

public class RemoteTextProcessor implements TextProcessor {
    @Override
    public Book parseSubtitle(String subtitlePath) throws IOException {
        // todo call rest service
        return null;
    }

    @Override
    public Dictionary readDictionary() throws IOException {
        // todo call rest service
        return null;
    }
}
