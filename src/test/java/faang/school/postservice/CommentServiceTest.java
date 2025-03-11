package faang.school.postservice;

import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.dto.comment.UpdateCommentRequest;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.CommentEvent;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.CommentValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @Mock
    private CommentValidator commentValidator;

    @Mock
    private KafkaTemplate<String, CommentEvent> kafkaTemplate;

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Captor
    private ArgumentCaptor<Comment> commentCaptor;

    @Test
    public void createComment_CommentNotFound() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setAuthorId(1L);
        request.setPostId(1L);

        when(postService.getPost(request.getPostId())).thenThrow(new EntityNotFoundException("Post not found"));

        assertThrows(EntityNotFoundException.class, () -> commentService.createComment(request));
    }

    @Test
    public void createComment_AuthorIsNull() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setAuthorId(1L);
        request.setPostId(1L);

        when(postService.getPost(request.getPostId())).thenReturn(new Post());
        doThrow(EntityNotFoundException.class).when(commentValidator).verificationCreatingData(any(Comment.class));

        assertThrows(EntityNotFoundException.class, () -> commentService.createComment(request));
    }

    @Test
    public void testCreateComment() {
        String topic = "comment-event";
        CreateCommentRequest request = new CreateCommentRequest();
        request.setAuthorId(1L);
        request.setPostId(1L);
        request.setContent("Text");
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(1L);
        Comment comment = new Comment();
        comment.setAuthorId(request.getAuthorId());
        comment.setPost(post);
        comment.setContent("Title");
        comment.setContent(request.getContent());
        when(postService.getPost(request.getPostId())).thenReturn(post);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);



        commentService.createComment(request);

        verify(commentRepository, times(1)).save(commentCaptor.capture());
    }

    @Test
    public void updateComment_CommentNotFound() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setId(1L);
        when(commentRepository.findById(request.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.updateComment(request));
    }

    @Test
    public void updateComment_TheCommentHasNotBeenChanged() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setId(1L);
        request.setContent("Text");
        Comment comment = new Comment();
        comment.setAuthorId(1L);
        comment.setContent("Text");
        when(commentRepository.findById(request.getId())).thenReturn(Optional.of(comment));
        doThrow(IllegalArgumentException.class).when(commentValidator).validateForUpdate(comment, request);

        assertThrows(IllegalArgumentException.class, () -> commentService.updateComment(request));
    }

    @Test
    public void testUpdateComment() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setId(1L);
        request.setContent("Text updated");
        Comment comment = new Comment();
        comment.setAuthorId(1L);
        comment.setContent("Text");
        when(commentRepository.findById(request.getId())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        commentService.updateComment(request);

        verify(commentRepository, times(1)).save(commentCaptor.capture());
    }

    @Test
    public void testGetListComment() {
        Comment comment = new Comment();
        long postId = 1L;
        when(commentRepository.findAllByPostId(postId)).thenReturn(List.of(comment));

        commentService.getListComment(postId);

        verify(commentRepository, times(1)).findAllByPostId(postId);
    }

    @Test
    public void deleteComment_CommentNotFound() {
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(commentId));
    }

    @Test
    public void testDeleteComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        commentService.deleteComment(comment.getId());

        verify(commentRepository, times(1)).findById(comment.getId());
        verify(commentRepository, times(1)).deleteById(comment.getId());
    }
}
