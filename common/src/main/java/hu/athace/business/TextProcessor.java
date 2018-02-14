package hu.athace.business;

import hu.athace.model.Book;
import hu.athace.model.Dictionary;

import java.io.IOException;

public interface TextProcessor {
    Book parseSubtitle(String subtitlePath) throws IOException;
    Dictionary readDictionary() throws IOException;
}
