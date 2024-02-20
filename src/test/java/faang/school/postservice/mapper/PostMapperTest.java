package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class PostMapperTest {
    private final PostMapper postMapper = new PostMapperImpl();
    private PostDto postDto;
    private Post post;

    @BeforeEach
    void init() {
        long postId = 1L;
        long authorId = 2L;
        long projectId = 3L;
        String content = "content";
        boolean deleted = false;
        boolean published = false;
        Resource resource1 = Resource.builder().id(1L).build();
        Resource resource2 = Resource.builder().id(2L).build();
        Resource resource3 = Resource.builder().id(3L).build();
        List<Resource> resources = new ArrayList<>(List.of(resource1, resource2, resource3));
        List<Long> resourceIds = resources.stream().map(Resource::getId).toList();

        postDto = PostDto.builder()
                .id(postId)
                .authorId(authorId)
                .content(content)
                .deleted(deleted)
                .published(published)
                .projectId(projectId)
                .resourceIds(resourceIds)
                .build();

        post = Post.builder()
                .id(postId)
                .authorId(authorId)
                .content(content)
                .deleted(deleted)
                .published(published)
                .projectId(projectId)
                .resources(resources)
                .build();
    }


    @Test
    void testToDto_entityNotNull_returnsDto() {
        PostDto dto = postMapper.toDto(post);

        assertEquals(postDto.getId(), dto.getId());
        assertEquals(postDto.getAuthorId(), dto.getAuthorId());
        assertEquals(postDto.getProjectId(), dto.getProjectId());
        assertEquals(postDto.getContent(), dto.getContent());
        assertEquals(postDto.isDeleted(), dto.isDeleted());
        assertEquals(postDto.isPublished(), dto.isPublished());
        assertEquals(postDto.getResourceIds(), dto.getResourceIds());
    }

    @Test
    void testToDto_entityNull_returnsNull() {
        PostDto dto = postMapper.toDto(null);

        assertNull(dto);
    }

    @Test
    void testToEntity_dtoNotNull_returnsEntity() {
        Post entity = postMapper.toEntity(postDto);

        assertEquals(post.getId(), entity.getId());
        assertEquals(post.getAuthorId(), entity.getAuthorId());
        assertEquals(post.getProjectId(), entity.getProjectId());
        assertEquals(post.getContent(), entity.getContent());
        assertEquals(post.isDeleted(), entity.isDeleted());
        assertEquals(post.isPublished(), entity.isPublished());
        assertNull(entity.getResources());
    }

    @Test
    void testToEntity_dtoNull_returnsNull() {
        Post entity = postMapper.toEntity(null);

        assertNull(entity);
    }
}