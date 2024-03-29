package dev.struchkov.godfather.telegram.simple.core.service;

import dev.struchkov.godfather.telegram.domain.attachment.DocumentAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.Picture;
import dev.struchkov.godfather.telegram.domain.files.ByteContainer;
import dev.struchkov.godfather.telegram.domain.files.FileContainer;
import dev.struchkov.godfather.telegram.main.context.TelegramConnect;
import dev.struchkov.godfather.telegram.simple.context.service.AttachmentService;
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

public class AttachmentServiceImpl implements AttachmentService {

    private static final Logger log = LoggerFactory.getLogger(AttachmentServiceImpl.class);

    private final AbsSender absSender;
    private final String botToken;

    private String folderPathForFiles;

    public AttachmentServiceImpl(TelegramConnect telegramConnect) {
        this.absSender = telegramConnect.getAbsSender();
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

    @Override
    public FileContainer uploadFile(@NotNull DocumentAttachment documentAttachment) {
        isNotNull(documentAttachment);
        try {
            final File file = downloadFile(documentAttachment);
            return new FileContainer(documentAttachment.getFileName(), documentAttachment.getMimeType(), file);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return FileContainer.empty();
    }

    @Override
    public ByteContainer uploadBytes(@NotNull DocumentAttachment documentAttachment) {
        isNotNull(documentAttachment);
        try {
            final byte[] bytes = downloadBytes(documentAttachment);
            return new ByteContainer(documentAttachment.getFileName(), documentAttachment.getMimeType(), bytes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ByteContainer.empty();
    }

    @Override
    public ByteContainer uploadBytes(@NotNull Picture picture) {
        isNotNull(picture);
        try {
            final byte[] bytes = downloadBytes(picture);
            return new ByteContainer(null, "image/jpeg", bytes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ByteContainer.empty();
    }

    private byte[] downloadBytes(Picture picture) throws TelegramApiException, IOException {
        return telegramDownloadBytes(picture.getFileId());
    }

    private byte[] downloadBytes(DocumentAttachment documentAttachment) throws TelegramApiException, IOException {
        return telegramDownloadBytes(documentAttachment.getFileId());
    }

    private byte[] telegramDownloadBytes(String fileId) throws TelegramApiException, IOException {
        final String fileUrl = getFileUrl(fileId);
        return IOUtils.toByteArray(new URL(fileUrl));
    }

    private File downloadFile(DocumentAttachment documentAttachment) throws IOException, TelegramApiException {
        final String fileUrl = getFileUrl(documentAttachment.getFileId());

        final StringBuilder filePath = new StringBuilder();
        if (folderPathForFiles != null) {
            filePath.append(folderPathForFiles);
        }
        filePath.append(UUID.randomUUID());
        filePath.append("_");
        filePath.append(documentAttachment.getFileName());

        final File localFile = new File(filePath.toString());
        final InputStream is = new URL(fileUrl).openStream();
        FileUtils.copyInputStreamToFile(is, localFile);
        return localFile;
    }

    private String getFileUrl(String fileId) throws TelegramApiException {
        final GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        return absSender.execute(getFile).getFileUrl(botToken);
    }

}
