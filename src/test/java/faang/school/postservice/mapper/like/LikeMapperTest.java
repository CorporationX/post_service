package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LikeMapperTest {

    @InjectMocks
    private LikeMapperImpl likeMapper;

    private Like like;
    private LikeDto likeDto;
    private LikeEvent likeEvent;
    private Comment comment;
    private Post post;

    @BeforeEach
    void setUp(){
        comment = new Comment();
        comment.setId(1L);
        post = new Post();
        post.setId(2L);
        like = Like.builder()
                .id(3L)
                .userId(4L)
                .comment(comment)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
        likeDto = LikeDto.builder()
                .id(1L)
                .userId(2L)
                .commentId(3L)
                .postId(4L)
                .build();
    }

    @Test
    void shouldMapToDto() {
        LikeDto result = likeMapper.toDto(like);
        assertNotNull(result);
        assertEquals(like.getId(), result.getId());
        assertEquals(like.getUserId(), result.getUserId());
        assertEquals(comment.getId(), result.getCommentId());
        assertEquals(post.getId(), result.getPostId());
    }

    @Test
    void shouldMapToEntity() {
        Like result = likeMapper.toEntity(likeDto);
        assertNotNull(result);
        assertEquals(likeDto.getId(), result.getId());
        assertEquals(likeDto.getUserId(), result.getUserId());
        assertNotNull(result.getComment());
        assertEquals(likeDto.getCommentId(), result.getComment().getId());
        assertNotNull(result.getPost());
        assertEquals(likeDto.getPostId(), result.getPost().getId());
    }

    @Test
    void shouldMapToEvent() {
        LikeEvent result = likeMapper.toEvent(likeDto);
        assertNotNull(result);
        assertEquals(likeDto.getId(), result.getId());
        assertEquals(likeDto.getUserId(), result.getUserId());
        assertEquals(likeDto.getCommentId(), result.getCommentId());
        assertEquals(likeDto.getPostId(), result.getPostId());
    }
}
