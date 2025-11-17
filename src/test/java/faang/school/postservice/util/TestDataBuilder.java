package faang.school.postservice.util;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.resource.Resource;
import faang.school.postservice.model.resource.ResourceType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;

public class TestDataBuilder {
    public static final Long POST_ID = 1L;
    public static final MockMultipartFile FILE = new MockMultipartFile(
            "files",
            "test-image.jpg",
            "image/jpeg",
            "test image content".getBytes()
    );
    public static final ResourceDto DTO = new ResourceDto(
            1L,
            "file-key-123",
            "test-image.jpg",
            1024L,
            ResourceType.IMAGE,
            LocalDateTime.now(),
            1L
    );
    public static final Resource RESOURCE = Resource.builder()
            .id(1L)
            .key("file-key-123")
            .name("test-image.jpg")
            .size(1024L)
            .type(ResourceType.IMAGE)
            .createdAt(LocalDateTime.now())
            .build();

}
