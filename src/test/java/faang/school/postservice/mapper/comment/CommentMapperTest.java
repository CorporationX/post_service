package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class CommentMapperTest {

    @Spy
    private CommentMapperImpl commentMapper;

    private CommentDto commentDto;
    private Comment comment;

    @BeforeEach
    public void setUp() {
        Post post = Post.builder().id(1L).build();
        commentDto = CommentDto.builder().id(1L).content("content").authorId(1L).postId(1L).build();
        comment = Comment.builder().id(1L).content("content").authorId(1L).post(post).build();
    }

    @Test
    public void testToEntityMapper() {
        Comment entity = commentMapper.toEntity(commentDto);

        assertThat(entity.getId()).isEqualTo(commentDto.getId());
        assertThat(entity.getContent()).isEqualTo(commentDto.getContent());
        assertThat(entity.getAuthorId()).isEqualTo(commentDto.getAuthorId());
        assertThat(entity.getPost().getId()).isEqualTo(commentDto.getPostId());
    }

    @Test
    public void testToDtoMapper() {
        CommentDto dto = commentMapper.toDto(comment);

        assertThat(dto.getId()).isEqualTo(comment.getId());
        assertThat(dto.getContent()).isEqualTo(comment.getContent());
        assertThat(dto.getAuthorId()).isEqualTo(comment.getAuthorId());
        assertThat(dto.getPostId()).isEqualTo(comment.getPost().getId());
    }
}
