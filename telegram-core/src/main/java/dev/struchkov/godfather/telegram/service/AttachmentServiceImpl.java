package dev.struchkov.godfather.telegram.service;

import dev.struchkov.godfather.telegram.TelegramConnect;
import dev.struchkov.godfather.telegram.domain.attachment.DocumentAttachment;
import dev.struchkov.godfather.telegram.domain.files.ByteContainer;
import dev.struchkov.godfather.telegram.domain.files.FileContainer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Stream;

import static dev.struchkov.haiti.utils.Inspector.isNotNull;

public class AttachmentServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(AttachmentServiceImpl.class);

    private final AbsSender absSender;
    private final String botToken;

    private String folderPathForFiles;

    public AttachmentServiceImpl(TelegramConnect telegramConnect) {
        this.absSender = telegramConnect.getAdsSender();
        this.botToken = telegramConnect.getToken();
    }

    public void setFolderPathForFiles(String folderPathForFiles) {
        if (folderPathForFiles != null) {
            this.folderPathForFiles = folderPathForFiles + "/";
            try (final Stream<Path> pathStream = Files.list(Path.of(folderPathForFiles))) {
                pathStream.forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                });
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

    }

    public FileContainer uploadFile(@NotNull DocumentAttachment documentAttachment) {
        isNotNull(documentAttachment);
        try {
            final File file = downloadFile(documentAttachment);
            return new FileContainer(documentAttachment.getFileName(), file);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return FileContainer.empty();
    }

    public ByteContainer uploadBytes(@NotNull DocumentAttachment documentAttachment) {
        isNotNull(documentAttachment);
        try {
            final byte[] bytes = downloadBytes(documentAttachment);
            return new ByteContainer(documentAttachment.getFileName(), bytes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ByteContainer.empty();
    }

    private byte[] downloadBytes(DocumentAttachment documentAttachment) throws TelegramApiException, IOException {
        final org.telegram.telegrambots.meta.api.objects.File file = getFilePath(documentAttachment);
        final URL url = new URL(file.getFileUrl(botToken));
        return IOUtils.toByteArray(url);
    }

    private File downloadFile(DocumentAttachment documentAttachment) throws IOException, TelegramApiException {
        final org.telegram.telegrambots.meta.api.objects.File file = getFilePath(documentAttachment);

        final StringBuilder filePath = new StringBuilder();
        if (folderPathForFiles != null) {
            filePath.append(folderPathForFiles);
        }
        filePath.append(UUID.randomUUID());
        filePath.append("_");
        filePath.append(documentAttachment.getFileName());

        final java.io.File localFile = new java.io.File(filePath.toString());
        final InputStream is = new URL(file.getFileUrl(botToken)).openStream();
        FileUtils.copyInputStreamToFile(is, localFile);
        return localFile;
    }

    private org.telegram.telegrambots.meta.api.objects.File getFilePath(DocumentAttachment documentAttachment) throws TelegramApiException {
        final GetFile getFile = new GetFile();
        getFile.setFileId(documentAttachment.getFileId());
        return absSender.execute(getFile);
    }

}
