package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exeption.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostService postService;
    @Mock
    private CommentValidator commentValidator;
    private long rightId;
    private long wrongId;
    private Comment comment;
    private Post post;
    private List<Comment> comments = new ArrayList<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        rightId = 1L;
        wrongId = -2L;
        comment = Comment.builder()
                .content("First message")
                .build();
        Mockito.when(commentRepository.findById(rightId))
                .thenReturn(Optional.of(comment));
        Mockito.when(postService.getPostById(rightId))
                .thenReturn(post);
        Mockito.when(commentRepository.findAllByPostId(rightId))
                .thenReturn(comments);
    }

    @Test
    void testCreateComment() {
        Comment comment = new Comment();
        CommentDto commentDto = CommentDto.builder().build();
        Mockito.when(commentMapper.toEntity(commentDto))
                .thenReturn(comment);

        assertEquals(null, commentDto.getCreatedAt());
        commentService.createComment(commentDto);
        LocalDateTime time = commentDto.getCreatedAt();

        Mockito.verify(commentMapper, Mockito.times(1))
                .toEntity(commentDto);
        Mockito.verify(commentRepository, Mockito.times(1))
                .save(comment);

        commentService.createComment(commentDto);
        assertEquals(time, commentDto.getCreatedAt());
    }

    @Test
    void testUpdateComment() { //uses or overrides a deprecated API.
        String content = "Second message";
        CommentDto commentDtoRight = CommentDto.builder()
                .id(rightId)
                .postId(rightId)
                .content(content)
                .build();

        commentService.updateComment(commentDtoRight);
        Mockito.verify(commentRepository, Mockito.times(1))
                .findById(rightId);
        Mockito.verify(postService, Mockito.times(1))
                .getPostById(rightId);
        Mockito.verify(commentValidator, Mockito.times(1))
                .validatorUpdateComment(post, comment);
        assertEquals(content, comment.getContent());

        CommentDto commentDtoFalse = CommentDto.builder()
                .id(wrongId)
                .postId(wrongId)
                .build();

        assertThrows(DataValidationException.class,
                () -> commentService.updateComment(commentDtoFalse));
    }

    @Test
    void testGetAllComments() {
        commentService.getAllComments(rightId);

        Mockito.verify(commentMapper, Mockito.times(1))
                .toDto(comments);
        Mockito.verify(commentRepository, Mockito.times(1))
                .findAllByPostId(rightId);
    }

    @Test
    void deleteComment() {
    }
}