package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.sightengine.textAnalysis.TextAnalysisResponse;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.moderation.sightengine.SightEngineReactiveClient;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private PostService postService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private SightEngineReactiveClient textAnalysisService;

    private long postId;
    private long authorId;
    private long commentId;
    private String content;
    private CommentDto commentDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        postId = 5L;
        authorId = 10L;
        commentId = 15L;
        content = "some content";

        commentDto = CommentDto.builder()
                .authorId(authorId)
                .content(content)
                .build();
        comment = Comment.builder()
                .id(commentId)
                .content("some content")
                .build();
    }

    @Test
    public void testAddComment() {
        // arrange
        Post post = Post.builder()
                .id(postId)
                .build();

        when(postService.findPostById(postId)).thenReturn(Optional.ofNullable(post));
        when(commentMapper.toEntity(commentDto)).thenReturn(comment);

        // act
        commentService.addComment(postId, commentDto);

        // assert
        verify(commentRepository).save(comment);
    }

    @Test
    public void testAddCommentUserDoesNotExist() {
        // arrange
        doThrow(new EntityNotFoundException())
                .when(userServiceClient)
                .getUserById(authorId);

        // act and assert
        assertThrows(EntityNotFoundException.class,
                () -> commentService.addComment(postId, commentDto));
    }

    @Test
    public void testAddCommentPostNotFound() {
        // arrange
        when(postService.findPostById(postId))
                .thenThrow(new EntityNotFoundException());

        // act and assert
        assertThrows(EntityNotFoundException.class,
                () -> commentService.addComment(postId, commentDto));
    }

    @Test
    public void testUpdateComment() {
        // arrange
        String newContent = "new content";
        when(commentRepository.findById(commentId))
                .thenReturn(Optional.of(comment));

        // act
        commentService.updateComment(commentId, newContent);
        String updatedContent = comment.getContent();

        // assert
        assertEquals(newContent, updatedContent);
    }

    @Test
    public void testUpdateCommentNotFound() {
        // arrange
        when(commentRepository.findById(commentId))
                .thenThrow(new EntityNotFoundException());

        // act and assert
        assertThrows(EntityNotFoundException.class,
                () -> commentService.updateComment(commentId, content));
    }

    @Test
    public void testGetPostByIdWithExistentPost(){
        when(commentRepository.findById(commentId))
                .thenReturn(Optional.ofNullable(comment));

        Comment result = commentService.getCommentById(commentId);

        assertNotNull(result);
        assertEquals(commentId, result.getId());
    }

    @Test
    public void testGetPostByIdWhenPostNotExist(){
        when(commentRepository.findById(commentId))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> commentService.getCommentById(commentId));
    }

    @Test
    public void testIsPostNotExistWithExistentPost(){
        when(commentRepository.existsById(commentId)).thenReturn(true);

        boolean result = commentService.isCommentNotExist(commentId);

        assertFalse(result);
    }

    @Test
    public void testIsPostNotExistWhenPostNotExist() {
        when(commentRepository.existsById(commentId)).thenReturn(false);

        boolean result = commentService.isCommentNotExist(commentId);

        assertTrue(result);
    }

    @Test
    public void testVerifyComments() {
        List<Comment> comments = List.of(comment);
        TextAnalysisResponse analysisResponse = new TextAnalysisResponse();

        when(commentRepository.findByVerifiedIsNull()).thenReturn(comments);
        when(textAnalysisService.analyzeText(comment.getContent())).thenReturn(Mono.just(analysisResponse));

        commentService.verifyComments();

        verify(commentRepository).findByVerifiedIsNull();
    }
}
