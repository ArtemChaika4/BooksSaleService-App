package ua.edu.dnu.booksales.util;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Properties;

public class PropertiesManager {
    private final String PROPERTIES_FILE_NAME;

    public PropertiesManager(String name){
        PROPERTIES_FILE_NAME = name;
    }

    public void save(Properties properties){
        try (OutputStream output = new FileOutputStream(new File(Objects.requireNonNull(
                PropertiesManager.class.getClassLoader().getResource(PROPERTIES_FILE_NAME)).toURI()))) {
            properties.store(output, "Properties");
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void load(Properties properties){
        try (InputStream input = PropertiesManager.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE_NAME)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

}
