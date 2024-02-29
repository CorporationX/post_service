package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CommentMapperTest {

    private Comment comment;

    @Mock
    private Post post;

    private CommentMapper commentMapper = new CommentMapperImpl();

    private CommentDto commentDto;

    private UserDto userDto;

    private List<Like> likes = new ArrayList<>();


    @BeforeEach
    public void init() {
        comment = new Comment(2L, "Content", 1L, likes, post, LocalDateTime.now(), LocalDateTime.now());
        commentDto = CommentDto.builder()
                .authorId(1L)
                .id(2L)
                .content("Content")
                .postId(3L)
                .build();
        userDto = UserDto.builder()
                .id(1L)
                .email("Email")
                .username("Username")
                .build();
    }

    @Test
    void testToDto() {
        CommentDto toDto = commentMapper.toDTO(comment);
        assertEquals(commentDto.getContent(), toDto.getContent());
        assertEquals(commentDto.getId(), toDto.getId());
        assertEquals(commentDto.getAuthorId(), toDto.getAuthorId());
        assertEquals(0L, toDto.getPostId());
    }

    @Test
    void testToEntity() {
        Comment toDto = commentMapper.toEntity(commentDto);
        assertEquals(commentDto.getContent(), toDto.getContent());
        assertEquals(commentDto.getId(), toDto.getId());
        assertEquals(commentDto.getAuthorId(), toDto.getAuthorId());
    }
}
