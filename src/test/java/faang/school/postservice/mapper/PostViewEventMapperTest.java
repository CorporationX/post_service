package faang.school.postservice.mapper;

import faang.school.postservice.dto.redis.PostViewEventDto;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PostViewEventMapperTest {

    @Spy
    private PostViewEventMapperImpl postViewEventMapper;
    private Post post;
    private PostViewEventDto dto;

    @BeforeEach
    void initData() {
        post = Post.builder()
                .id(1L)
                .authorId(2L)
                .build();
        dto = PostViewEventDto.builder()
                .postId(1L)
                .authorId(2L)
                .build();
    }
    @Test
    void testToDto() {
        PostViewEventDto actualDto = postViewEventMapper.toDto(post);
        assertEquals(dto, actualDto);
    }

    @Test
    void testToEntity() {
        Post actualPost = postViewEventMapper.toEntity(dto);
        assertEquals(post, actualPost);
    }
}
