package dev.struchkov.godfather.telegram.domain.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileContainer {

    public static final FileContainer EMPTY = new FileContainer(null, null, null);

    private static final Logger log = LoggerFactory.getLogger(FileContainer.class);

    private final String fileName;
    private final File file;
    private final String mimeType;

    public FileContainer(String fileName, String mimeType, File file) {
        this.fileName = fileName;
        this.file = file;
        this.mimeType = mimeType;
    }

    public static FileContainer empty() {
        return EMPTY;
    }

    public String getFileName() {
        return fileName;
    }

    public File getFile() {
        return file;
    }

    public String getMimeType() {
        return mimeType;
    }

    public boolean isNotEmpty() {
        return file != null;
    }

    public void delete() {
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
