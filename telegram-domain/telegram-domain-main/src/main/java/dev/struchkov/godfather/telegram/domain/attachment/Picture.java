package dev.struchkov.godfather.telegram.domain.attachment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Picture {

    private String fileId;
    private String fileUniqueId;
    private Integer fileSize;
    private Integer weight;
    private Integer height;

}
