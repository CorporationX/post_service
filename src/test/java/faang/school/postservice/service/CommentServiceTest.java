package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.feed.CommentRedisEvent;
import faang.school.postservice.dto.kafkaevents.CommentEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.AuthorRequestEventPublisher;
import faang.school.postservice.publisher.CommentAddedEventPublisher;
import faang.school.postservice.publisher.CommentEvenRedisPublisher;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class CommentServiceTest {
    private UserDto user;
    private CommentDto commentDto;
    private Post post;
    private Comment comment;

    @Mock
    private CommentRepository repository;

    @Mock
    private CommentMapper mapper;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient client;

    @Mock
    private CommentEventPublisher commentEventPublisher;

    @Mock
    private CommentEvenRedisPublisher commentEvenRedisPublisher;

    @Mock
    private CommentAddedEventPublisher commentAddedEventPublisher;

    @Mock
    private AuthorRequestEventPublisher authorRequestEventPublisher;

    @InjectMocks
    private CommentService service;

    @BeforeEach
    public void setUp() {
        user = new UserDto(1L, "name", "@");
        commentDto = CommentDto.builder().build();
        post = new Post();
        comment = new Comment();
    }

    @Test
    public void positiveCreateComment() {
        Comment successComment = Comment.builder().id(1L).content("Ха=ха").build();
        CommentDto goodDto = commentDto = CommentDto.builder().content("Ха=ха").build();
        when(client.getUser(1L)).thenReturn(user);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(mapper.toEntity(goodDto)).thenReturn(successComment);
        when(repository.save(any(Comment.class))).thenReturn(successComment);
        when(mapper.toDto(successComment)).thenReturn(goodDto);
        doNothing().when(commentEventPublisher).publish(any(CommentEvent.class));
        doNothing().when(commentAddedEventPublisher).publish(any(CommentRedisEvent.class));
        doNothing().when(authorRequestEventPublisher).publish(any(Long.class));

        CommentDto result;
        result = service.createComment(1L, 1L, goodDto);

        verify(client, times(1)).getUser(1L);
        verify(postRepository, times(1)).findById(1L);
        verify(repository, times(1)).save(successComment);

        assertNotNull(result);
        assertEquals(goodDto.content(), successComment.getContent());
    }

    @Test
    public void negativeCreateCommentUserNotFound() {

        when(client.getUser(1L)).thenReturn(null);

        assertThrows(DataValidationException.class, () ->
                service.createComment(1L, 1L, commentDto));

        verify(client, times(1)).getUser(1L);
    }

    @Test
    public void negativeCreateCommentPostNotFound() {
        when(client.getUser(1L)).thenReturn(user);
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () ->
               service.createComment(1L, 1L, commentDto));

        verify(client, times(1)).getUser(1L);
        verify(postRepository, times(1)).findById(1L);
        verify(repository, times(0)).save(comment);
    }

    @Test
    public void negativeCreateCommentNullComment() {
        when(client.getUser(1L)).thenReturn(user);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThrows(DataValidationException.class, () ->
                service.createComment(1L, 1L, null));

        verify(client, times(1)).getUser(1L);
        verify(postRepository, times(1)).findById(1L);
        verify(repository, times(0)).save(comment);
    }

    @Test
    public void negativeCreateCommentEmptyComment() {
        CommentDto emptyComment = commentDto = CommentDto.builder().content("").build();
        when(client.getUser(1L)).thenReturn(user);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThrows(DataValidationException.class, () ->
                service.createComment(1L, 1L, emptyComment));

        verify(client, times(1)).getUser(1L);
        verify(postRepository, times(1)).findById(1L);
        verify(repository, times(0)).save(comment);
    }

    @Test
    public void negativeCreateCommentCommentTooLong() {
        when(client.getUser(1L)).thenReturn(user);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        String tooMuch = "a".repeat(4097);
        CommentDto longContent = CommentDto.builder().content(tooMuch).build();

        assertThrows(DataValidationException.class, () ->
                service.createComment(1L, 1L, longContent));

        verify(client, times(1)).getUser(1L);
        verify(postRepository, times(1)).findById(1L);
        verify(repository, times(0)).save(comment);
    }

    @Test
    public void positiveEditComment() {
        Comment successComment = Comment.builder().id(1L).content("Ха=ха=ха").build();
        CommentDto goodDto = commentDto = CommentDto.builder().content("Ха=ха=ха").build();

        when(repository.findById(1L)).thenReturn(Optional.of(successComment));
        when(repository.save(successComment)).thenReturn(successComment);
        when(mapper.toDto(successComment)).thenReturn(goodDto);

        CommentDto result;
        result = service.editComment(commentDto, 1L, "Ха=ха=ха");

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(successComment);
        verify(mapper, times(1)).toDto(successComment);

        assertNotNull(result);
        assertEquals("Ха=ха=ха", result.content());
    }

    @Test
    public void negativeEditCommentNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, ()
                -> service.editComment(commentDto, 1L, "1"));

        verify(repository, times(1)).findById(1L);
        verify(repository, times(0)).save(comment);
    }

    @Test
    public void negativeEditCommentNullComment() {
        when(repository.findById(1L)).thenReturn(Optional.of(comment));

        assertThrows(DataValidationException.class, ()
                -> service.editComment(commentDto, 1L, "1"));

        verify(repository, times(1)).findById(1L);
        verify(repository, times(0)).save(comment);
    }

    @Test
    public void negativeEditCommentContentNullContent() {
        when(repository.findById(1L)).thenReturn(Optional.of(comment));

        assertThrows(DataValidationException.class, ()
                -> service.editComment(commentDto, 1L, ""));

        verify(repository, times(1)).findById(1L);
        verify(repository, times(0)).save(comment);
    }

    @Test
    public void negativeEditCommentTooLongContent() {
        when(repository.findById(1L)).thenReturn(Optional.of(comment));
        String tooMuch = "a".repeat(4097);
        CommentDto longContent = commentDto = CommentDto.builder().content(tooMuch).build();

        assertThrows(DataValidationException.class, ()
                -> service.editComment(longContent, 1L, tooMuch));

        verify(repository, times(1)).findById(1L);
        verify(repository, times(0)).save(comment);
    }

    @Test
    public void positiveGetAllComments() {
        Comment firstComment = Comment.builder().id(1L).content("Комментарий").build();
        CommentDto firstDto = CommentDto.builder().id(1L).content("Комментарий").build();
        List<Comment> commentList = List.of(firstComment);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(repository.findAllByPostId(1L)).thenReturn(commentList);
        when(mapper.toDto(firstComment)).thenReturn(firstDto);

        List<CommentDto> result;
        result = service.getAllComments(1L);

        verify(postRepository, times(1)).findById(1L);
        verify(repository, times(1)).findAllByPostId(1L);
        verify(mapper, times(1)).toDto(firstComment);

        assertNotNull(result);
        assertEquals(firstDto, result.get(0));
    }

    @Test
    public void negativeGetAllCommentsPostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () ->
                service.getAllComments(1L));

        verify(postRepository, times(1)).findById(1L);
        verify(repository, never()).findAllByPostId(1L);
    }

    @Test
    public void positiveDeleteComment() {
        Comment deletedComment = Comment.builder().id(1L).build();
        when(repository.findById(1L)).thenReturn(Optional.of(deletedComment));
        doNothing().when(repository).deleteById(1L);

        service.deleteComment(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    public void negativeDeleteCommentNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () ->
                service.deleteComment(1L));

        verify(repository, times(0)).deleteById(1L);
    }

}
