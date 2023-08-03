package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.util.ErrorMessage;
import jakarta.persistence.EntityNotFoundException;
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
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
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
    public void updateCommentNotFoundTest() {

        Mockito.lenient().when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> commentService.update(commentDto));

        String expectedMessage = MessageFormat.format(ErrorMessage.COMMENT_NOT_FOUND_FORMAT, commentId);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void deleteCommentSuccessfulTest(){
        Comment comment = Comment.builder().id(commentId).build();

        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        commentService.delete(commentId);

        Mockito.verify(commentRepository, Mockito.times(1)).delete(comment);
    }

    @Test
    public void deleteCommentNotFoundTest(){
        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        String expectedMessage = MessageFormat.format(ErrorMessage.COMMENT_NOT_FOUND_FORMAT, commentId);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.delete(commentId));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void getCommentsForPostSortedByCreationDateTest(){
        Long postId = 1L;
        Comment comment1 = Comment.builder().createdAt(
                LocalDateTime.of(2023, Month.JULY, 28, 0, 0, 0)).build();
        Comment comment2 = Comment.builder().createdAt(
                LocalDateTime.of(2023, Month.JULY, 25, 0, 0, 0)).build();
        Comment comment3 = Comment.builder().createdAt(
                LocalDateTime.of(2023, Month.JULY, 15, 0, 0, 0)).build();

        List<Comment> commentList = List.of(comment3, comment2, comment1);
        List<CommentDto> expectedList = List.of(
                commentMapper.commentToDto(comment1),
                commentMapper.commentToDto(comment2),
                commentMapper.commentToDto(comment3));

        Mockito.when(commentRepository.findAllByPostId(postId)).thenReturn(commentList);
        List<CommentDto> result = commentService.getCommentsForPost(postId);

        assertEquals(expectedList, result);
    }
}