package faang.school.postservice.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public enum ImageType {
    JPEG("image/jpeg"),
    PNG("image/png");

    private final String mimeType;
}
