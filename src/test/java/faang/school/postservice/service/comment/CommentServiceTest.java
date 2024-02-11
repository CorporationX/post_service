package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.comment.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.comment.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    private CommentValidator commentValidator;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    @Mock
    private Comment comment;

    @Mock
    private Post post;

    private CommentDto commentDto;

    private UserDto userDto;

    private List<Comment> comments = new ArrayList<>();


    @BeforeEach
    public void init() {
        commentValidator = new CommentValidator(userServiceClient);
        commentService = new CommentService(commentRepository, postRepository, commentValidator, commentMapper);
        commentDto = CommentDto.builder()
                .authorId(1L)
                .id(2L)
                .content("Content")
                .postId(3L)
                .build();
        userDto = UserDto.builder()
                .email("Email")
                .username("Username")
                .build();
    }

    @Test
    public void whenAddNewCommentThenIncorrectDataInDB() {
        Mockito.when(userServiceClient.getUser(anyLong()))
                .thenReturn(null);
        try {
            commentService.addNewComment(1L, commentDto);
        } catch (DataValidationException e) {
            assertThat(e).isInstanceOf(RuntimeException.class)
                    .hasMessage("User from user service is null");
        }
        Mockito.when(userServiceClient.getUser(anyLong()))
                .thenReturn(userDto);
        try {
            commentService.addNewComment(1L, commentDto);
        } catch (DataValidationException e) {
            assertThat(e).isInstanceOf(RuntimeException.class)
                    .hasMessage("User data is not correct");
        }
    }


    @Test
    public void whenChangeCommentThenIncorrectDataInDB() {
        Mockito.when(userServiceClient.getUser(anyLong()))
                .thenReturn(null);
        try {
            commentService.changeComment(commentDto);
        } catch (DataValidationException e) {
            assertThat(e).isInstanceOf(RuntimeException.class)
                    .hasMessage("User from user service is null");
        }
        Mockito.when(userServiceClient.getUser(anyLong()))
                .thenReturn(userDto);
        try {
            commentService.changeComment(commentDto);
        } catch (DataValidationException e) {
            assertThat(e).isInstanceOf(RuntimeException.class)
                    .hasMessage("User data is not correct");
        }
    }


    @Test
    public void whenAddNewCommentThenSuccess() {
        Mockito.when(userServiceClient.getUser(anyLong()))
                .thenReturn(userDto);
        userDto.setId(1L);
        Mockito.when(postRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));
        Mockito.when(commentMapper.toEntity(commentDto))
                .thenReturn(comment);
        commentService.addNewComment(1L, commentDto);
        Mockito.verify(commentRepository, times(1))
                .save(comment);
        Mockito.verify(commentMapper, times(1))
                .toEntity(commentDto);
        Mockito.verify(commentMapper, times(1))
                .toDTO(comment);
    }

    @Test
    public void whenChangeCommentThenSuccess() {
        Mockito.when(userServiceClient.getUser(anyLong()))
                .thenReturn(userDto);
        userDto.setId(1L);
        Mockito.when(postRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));
        Mockito.when(commentMapper.toEntity(commentDto))
                .thenReturn(comment);
        commentService.addNewComment(1L, commentDto);
        Mockito.verify(commentRepository, times(1))
                .save(comment);
        Mockito.verify(commentMapper, times(1))
                .toEntity(commentDto);
        Mockito.verify(commentMapper, times(1))
                .toDTO(comment);
    }

    @Test
    public void whenDeleteCommentThenSuccess() {
        commentService.deleteComment(commentDto);
        Mockito.verify(commentRepository, times(1))
                .deleteById(2L);
    }


    @Test
    public void whenGetAllCommentThenSuccess() {
        Mockito.when(commentRepository.findAllByPostId(anyLong()))
                .thenReturn(comments);
        commentService.getAllComments(1L);
        Mockito.verify(commentRepository, times(1))
                .findAllByPostId(1L);
        Mockito.verify(commentMapper, times(1))
                .toDtoList(comments);
    }
}

