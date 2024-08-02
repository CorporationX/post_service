package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.mapper.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CommentMapperTest {

    @InjectMocks
    private CommentMapperImpl commentMapper;

    private Post post;
    private Comment comment;
    private CommentDto dto;
    private Post post1;
    private Post post2;
    List<Comment> comments;

    @BeforeEach
    void setUp(){
        post = Post.builder()
                .id(1L)
                .build();
        comment = Comment.builder()
                .id(1L)
                .content("Test content")
                .authorId(2L)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
        dto = CommentDto.builder()
                .id(1L)
                .content("Test content")
                .authorId(2L)
                .postId(3L)
                .createdAt(LocalDateTime.now())
                .build();

        post1 = Post.builder()
                .id(1L)
                .build();
        post2 = Post.builder()
                .id(2L)
                .build();

        comments = Arrays.asList(
                Comment.builder().id(1L).content("Content 1").authorId(1L).post(post1).createdAt(LocalDateTime.now()).build(),
                Comment.builder().id(2L).content("Content 2").authorId(2L).post(post2).createdAt(LocalDateTime.now()).build()
        );
    }

    @Test
    void shouldMapToDto() {
        CommentDto dto = commentMapper.toDto(comment);
        assertNotNull(dto);
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getContent(), dto.getContent());
        assertEquals(comment.getAuthorId(), dto.getAuthorId());
        assertEquals(comment.getPost().getId(), dto.getPostId());
        assertEquals(comment.getCreatedAt(), dto.getCreatedAt());
    }

    @Test
    void shouldMapToEntity() {
        Comment entity = commentMapper.toEntity(dto);
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getContent(), entity.getContent());
        assertEquals(dto.getAuthorId(), entity.getAuthorId());
        assertEquals(dto.getPostId(), entity.getPost().getId());
        assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
    }

    @Test
    void shouldMapToDtoList() {
        List<CommentDto> dtos = commentMapper.toDto(comments);
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(comments.get(0).getId(), dtos.get(0).getId());
        assertEquals(comments.get(0).getContent(), dtos.get(0).getContent());
        assertEquals(comments.get(0).getAuthorId(), dtos.get(0).getAuthorId());
        assertEquals(comments.get(0).getPost().getId(), dtos.get(0).getPostId());
        assertEquals(comments.get(0).getCreatedAt(), dtos.get(0).getCreatedAt());
        assertEquals(comments.get(1).getId(), dtos.get(1).getId());
        assertEquals(comments.get(1).getContent(), dtos.get(1).getContent());
        assertEquals(comments.get(1).getAuthorId(), dtos.get(1).getAuthorId());
        assertEquals(comments.get(1).getPost().getId(), dtos.get(1).getPostId());
        assertEquals(comments.get(1).getCreatedAt(), dtos.get(1).getCreatedAt());
    }

    @Test
    void testToEvent() {
        CommentEvent result = commentMapper.toEvent(dto);
        assertNotNull(result);
        assertEquals(dto.getId(), result.getCommentId());
        assertEquals(dto.getAuthorId(), result.getCommentAuthorId());
        assertEquals(dto.getPostId(), result.getPostId());
        assertEquals(dto.getPostId(), result.getPostAuthorId());
    }
}