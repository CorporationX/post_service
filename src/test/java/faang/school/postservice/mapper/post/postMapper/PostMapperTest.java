package faang.school.postservice.mapper.post.postMapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PostMapperTest {

    @Mock
    private PostMapper postMapper;

    private PostDto postDto;
    private Post postEntity;

    @BeforeEach
    void setUp() {
        postDto = PostDto.builder()
                .id(1L)
                .content("Test post")
                .authorId(123L)
                .projectId(456L)
                .scheduledAt(LocalDateTime.now().plusDays(1))
                .build();
        postEntity = Post.builder()
                .id(1L)
                .content("Test post")
                .authorId(123L)
                .projectId(456L)
                .scheduledAt(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    void toDtoShouldConvertPostEntityToPostDto() {
        when(postMapper.toDto(postEntity)).thenReturn(postDto);
        PostDto resultDto = postMapper.toDto(postEntity);
        assertEquals(postDto, resultDto);
        assertEquals(postEntity.getId(), resultDto.getId());
        assertEquals(postEntity.getContent(), resultDto.getContent());
        assertEquals(postEntity.getAuthorId(), resultDto.getAuthorId());
        assertEquals(postEntity.getProjectId(), resultDto.getProjectId());
        assertThat(resultDto.getScheduledAt()).isEqualToIgnoringSeconds(postEntity.getScheduledAt());
    }

    @Test
    void toEntityShouldConvertPostDtoToPostEntity() {
        when(postMapper.toEntity(postDto)).thenReturn(postEntity);
        Post resultEntity = postMapper.toEntity(postDto);
        assertEquals(postEntity, resultEntity);
        assertEquals(postDto.getId(), resultEntity.getId());
        assertEquals(postDto.getContent(), resultEntity.getContent());
        assertEquals(postDto.getAuthorId(), resultEntity.getAuthorId());
        assertEquals(postDto.getProjectId(), resultEntity.getProjectId());
        assertEquals(postDto.getScheduledAt(), resultEntity.getScheduledAt());
    }
}