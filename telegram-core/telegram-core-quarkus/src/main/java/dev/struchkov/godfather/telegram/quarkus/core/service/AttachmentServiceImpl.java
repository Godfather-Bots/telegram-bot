package dev.struchkov.godfather.telegram.quarkus.core.service;

import dev.struchkov.godfather.telegram.domain.attachment.DocumentAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.Picture;
import dev.struchkov.godfather.telegram.domain.files.ByteContainer;
import dev.struchkov.godfather.telegram.domain.files.FileContainer;
import dev.struchkov.godfather.telegram.main.context.TelegramConnect;
import dev.struchkov.godfather.telegram.quarkus.context.service.AttachmentService;
import io.smallrye.mutiny.Uni;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static dev.struchkov.haiti.utils.Inspector.isNotNull;
import static java.util.concurrent.CompletableFuture.completedFuture;

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
    public Uni<FileContainer> uploadFile(@NotNull DocumentAttachment documentAttachment) {
        isNotNull(documentAttachment);
        return downloadFile(documentAttachment)
                .onItem().ifNotNull().transform(file -> new FileContainer(documentAttachment.getFileName(), documentAttachment.getMimeType(), file));
    }

    @Override
    public Uni<ByteContainer> uploadBytes(@NotNull DocumentAttachment documentAttachment) {
        isNotNull(documentAttachment);
        return downloadBytes(documentAttachment)
                .onItem().ifNotNull().transform(bytes -> new ByteContainer(documentAttachment.getFileName(), documentAttachment.getMimeType(), bytes));
    }

    @Override
    public Uni<ByteContainer> uploadBytes(@NotNull Picture picture) {
        isNotNull(picture);
        return downloadBytes(picture)
                .onItem().ifNotNull().transform(bytes -> new ByteContainer(null, "image/jpeg", bytes));
    }

    private Uni<byte[]> downloadBytes(Picture picture) {
        return telegramDownloadBytes(picture.getFileId());
    }

    private Uni<byte[]> downloadBytes(DocumentAttachment documentAttachment) {
        return telegramDownloadBytes(documentAttachment.getFileId());
    }

    private Uni<byte[]> telegramDownloadBytes(String fileId) {
        return getFileUrl(fileId)
                .onItem().ifNotNull().transformToUni(
                        fileUrl -> Uni.createFrom().completionStage(
                                CompletableFuture.supplyAsync(
                                        () -> {
                                            final URL url;
                                            try {
                                                url = new URL(fileUrl);
                                                return IOUtils.toByteArray(url);
                                            } catch (IOException e) {
                                                log.error(e.getMessage(), e);
                                            }
                                            return null;
                                        }
                                )
                        )
                );
    }

    private Uni<File> downloadFile(DocumentAttachment documentAttachment) {
        return getFileUrl(documentAttachment.getFileId())
                .onItem().ifNotNull().transformToUni(fileUrl -> Uni.createFrom().completionStage(
                        CompletableFuture.supplyAsync(() -> {
                                    final StringBuilder filePath = new StringBuilder();
                                    if (folderPathForFiles != null) {
                                        filePath.append(folderPathForFiles);
                                    }
                                    filePath.append(UUID.randomUUID()).append("_").append(documentAttachment.getFileName());
                                    final File localFile = new File(filePath.toString());
                                    final InputStream is;
                                    try {
                                        is = new URL(fileUrl).openStream();
                                        FileUtils.copyInputStreamToFile(is, localFile);
                                    } catch (IOException e) {
                                        log.error(e.getMessage(), e);
                                    }
                                    return localFile;
                                }
                        )
                ));
    }

    private Uni<String> getFileUrl(String fileId) {
        return Uni.createFrom().completionStage(getFileCompletableFuture(fileId))
                .onItem().ifNotNull().transform(file -> file.getFileUrl(botToken));
    }

    private CompletableFuture<org.telegram.telegrambots.meta.api.objects.File> getFileCompletableFuture(String fileId) {
        try {
            return absSender.executeAsync(GetFile.builder().fileId(fileId).build());
        } catch (TelegramApiRequestException e) {
            log.error(e.getApiResponse());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return completedFuture(null);
    }

}
