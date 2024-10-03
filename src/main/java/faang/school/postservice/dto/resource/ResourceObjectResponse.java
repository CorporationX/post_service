package faang.school.postservice.dto.resource;

import lombok.Builder;

import java.io.InputStream;

@Builder
public record ResourceObjectResponse(
        InputStream content,
        String contentType,
        long contentLength) {
}
