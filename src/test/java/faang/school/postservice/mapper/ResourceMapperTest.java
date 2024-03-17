package faang.school.postservice.mapper;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ResourceMapperTest {
    private final ResourceMapper resourceMapper = new ResourceMapperImpl();
    private ResourceDto resourceDto;
    private Resource resource;

    @BeforeEach
    void init() {
        long id = 1L;
        String name = "name";
        long size = 50L;
        String type = "type";
        String key = "key";
        Post post = Post.builder().id(1L).build();

        resourceDto = ResourceDto.builder()
                .id(id)
                .name(name)
                .size(size)
                .type(type)
                .key(key)
                .postId(post.getId())
                .build();

        resource = Resource.builder()
                .id(id)
                .name(name)
                .size(size)
                .type(type)
                .key(key)
                .post(post)
                .build();
    }

    @Test
    void testToDto_entityNotNull_returnsDto() {
        ResourceDto dto = resourceMapper.toDto(resource);

        assertEquals(resourceDto.getId(), dto.getId());
        assertEquals(resourceDto.getName(), dto.getName());
        assertEquals(resourceDto.getSize(), dto.getSize());
        assertEquals(resourceDto.getType(), dto.getType());
        assertEquals(resourceDto.getKey(), dto.getKey());
        assertEquals(resourceDto.getPostId(), dto.getPostId());
    }

    @Test
    void testToDto_entityNull_returnsNull() {
        ResourceDto dto = resourceMapper.toDto(null);

        assertNull(dto);
    }
}
