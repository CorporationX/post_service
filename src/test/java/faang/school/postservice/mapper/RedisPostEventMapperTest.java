package faang.school.postservice.mapper;

import faang.school.postservice.dto.redis.PostEventDto;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RedisPostEventMapperTest {

    @Spy
    private PostEventMapperImpl postEventMapper;
    private Post post;
    private PostEventDto dto;

    @BeforeEach
    void initData() {
        post = Post.builder()
                .id(1L)
                .authorId(2L)
                .build();
        dto = PostEventDto.builder()
                .postId(1L)
                .authorId(2L)
                .build();
    }
    @Test
    void testToDto() {
        PostEventDto actualDto = postEventMapper.toDto(post);
        assertEquals(dto, actualDto);
    }

    @Test
    void testToEntity() {
        Post actualPost = postEventMapper.toEntity(dto);
        assertEquals(post, actualPost);
    }
}
