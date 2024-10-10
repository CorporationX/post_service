package faang.school.postservice.utils;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageData {
    private long fileSize;
    private String contentType;
    private String originalFilename;
    private byte[] content;
}
