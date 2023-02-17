package dev.struchkov.godfather.telegram.quarkus.context.service;

import dev.struchkov.godfather.telegram.domain.attachment.DocumentAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.Picture;
import dev.struchkov.godfather.telegram.domain.files.ByteContainer;
import dev.struchkov.godfather.telegram.domain.files.FileContainer;
import io.smallrye.mutiny.Uni;
import org.jetbrains.annotations.NotNull;

public interface AttachmentService {

    Uni<FileContainer> uploadFile(@NotNull DocumentAttachment documentAttachment);

    Uni<ByteContainer> uploadBytes(@NotNull DocumentAttachment documentAttachment);

    Uni<ByteContainer> uploadBytes(@NotNull Picture picture);

}
