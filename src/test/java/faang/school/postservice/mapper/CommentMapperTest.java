package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.util.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CommentMapperTest {

    @Spy
    private TestData testData;

    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    private Comment comment;

    private CommentDto commentDto;

    private List<CommentDto> commentDtos;

    private List<Comment> comments;

    @BeforeEach
    void init() {
        comment = testData.returnComment();

        commentDto = new CommentDto();
        commentDto.setAuthorId(1L);
        commentDto.setContent("NewContent");
        commentDto.setId(0L);

        commentDtos = new ArrayList<>();
        commentDtos.add(commentDto);
        comments = new ArrayList<>();
        comments.add(comment);
    }

    @Test
    void testToEntity() {
        comment.setCreatedAt(null);
        assertEquals(comment, commentMapper.ToEntity(commentDto));
    }

    @Test
    void testToDto() {
        assertEquals(commentMapper.ToDto(comment), commentDto);
    }

    @Test
    void testToDtoList() {
        assertEquals(commentMapper.ToDtoList(comments), commentDtos);
    }
}
