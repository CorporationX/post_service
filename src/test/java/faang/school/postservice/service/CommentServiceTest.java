package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.util.ErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.MessageFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
    @InjectMocks
    private CommentService commentService;

    private CommentDto commentDto;
    private CommentDto updatedCommentDto;
    private Comment comment;
    private Long commentId;

    @BeforeEach
    void setUp(){
        commentId = 1L;
        commentDto = CommentDto.builder().id(commentId).content("content").build();
        updatedCommentDto = CommentDto.builder().id(commentId).content("updated").build();
        comment = commentMapper.commentToEntity(updatedCommentDto);
    }

    @Test
    void createCommentTest(){
        CommentDto commentDto = CommentDto.builder().content("content").build();
        Comment comment = Comment.builder().content("content").build();

        CommentDto expectedDto = CommentDto.builder().id(0L).content("content").authorId(0L).build();
        CommentDto result = commentService.create(commentDto);

        Mockito.verify(commentRepository).save(comment);
        assertEquals(expectedDto, result);
    }

    @Test
    public void commentUpdateTest() {
        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        CommentDto resultDto = commentService.update(updatedCommentDto);

        assertEquals(updatedCommentDto.getContent(), resultDto.getContent());
    }

    @Test
    public void testUpdateCommentNotFound() {

        Mockito.lenient().when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> commentService.update(commentDto));

        String expectedMessage = MessageFormat.format(ErrorMessage.COMMENT_NOT_FOUND_FORMAT, commentId);
        assertEquals(expectedMessage, exception.getMessage());
    }
}