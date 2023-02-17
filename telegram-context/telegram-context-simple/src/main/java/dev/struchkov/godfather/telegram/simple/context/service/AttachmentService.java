package dev.struchkov.godfather.telegram.simple.context.service;

import dev.struchkov.godfather.telegram.domain.attachment.DocumentAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.Picture;
import dev.struchkov.godfather.telegram.domain.files.ByteContainer;
import dev.struchkov.godfather.telegram.domain.files.FileContainer;
import org.jetbrains.annotations.NotNull;

public interface AttachmentService {

    FileContainer uploadFile(@NotNull DocumentAttachment documentAttachment);

    ByteContainer uploadBytes(@NotNull DocumentAttachment documentAttachment);

    ByteContainer uploadBytes(@NotNull Picture picture);

}
