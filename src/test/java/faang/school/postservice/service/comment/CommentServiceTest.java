package faang.school.postservice.service.comment;

import faang.school.postservice.client.CommentAnalyzer;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.commentAnalyzer.response.AttributeScoreDto;
import faang.school.postservice.dto.commentAnalyzer.response.SpanScoreDto;
import faang.school.postservice.dto.commentAnalyzer.response.SummaryScoreDto;
import faang.school.postservice.dto.commentAnalyzer.response.ToxicityScoreDto;
import faang.school.postservice.enums.CommentToxicityType;
import faang.school.postservice.mapper.CommentRequestMapper;
import faang.school.postservice.mapper.CommentResponseMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static faang.school.postservice.contants.ErrorMessage.ERROR_NOT_AUTHOR_COMMENT;
import static faang.school.postservice.contants.ErrorMessage.ERROR_NULL_AUTHOR_ID;
import static faang.school.postservice.contants.ErrorMessage.ERROR_NULL_COMMENT_ID;
import static faang.school.postservice.contants.ErrorMessage.ERROR_NULL_CONTENT;
import static faang.school.postservice.contants.ErrorMessage.ERROR_NULL_POST_ID;
import static faang.school.postservice.contants.ErrorMessage.getErrorNotFoundComment;
import static faang.school.postservice.contants.ErrorMessage.getErrorNotFoundPost;
import static faang.school.postservice.contants.ErrorMessage.getErrorNotFoundUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    private static final Long POST_ID = 1L;
    private static final Long COMMENT_ID = 2L;
    private static final Long AUTHOR_ID = 3L;

    private static final String CONTENT = "Content";
    private static final String UPDATE_CONTENT = "Update content";

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentAnalyzer commentAnalyzer;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private CommentResponseMapper commentResponseMapper = Mappers.getMapper(CommentResponseMapper.class);

    @Spy
    private CommentRequestMapper commentRequestMapper = Mappers.getMapper(CommentRequestMapper.class);

    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;
    private CommentUpdateDto commentUpdateDto;
    private Comment comment;
    private Post post;
    private Mono<ToxicityScoreDto> toxicityScore;
    private final ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
    private final int commentModerationBatchSize = 3;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId(POST_ID);

        comment = new Comment();
        comment.setId(COMMENT_ID);
        comment.setPost(post);
        comment.setAuthorId(AUTHOR_ID);

        commentRequestDto = new CommentRequestDto();
        commentRequestDto.setPostId(POST_ID);
        commentRequestDto.setAuthorId(AUTHOR_ID);
        commentRequestDto.setContent(CONTENT);

        commentResponseDto = new CommentResponseDto();
        commentRequestDto.setPostId(POST_ID);
        commentRequestDto.setAuthorId(AUTHOR_ID);
        commentRequestDto.setContent(CONTENT);

        commentUpdateDto = new CommentUpdateDto();
        commentUpdateDto.setAuthorId(AUTHOR_ID);
        commentUpdateDto.setContent(UPDATE_CONTENT);

        ReflectionTestUtils.setField(commentService, "commentModerationTimeoutHours", 1);
        ReflectionTestUtils.setField(commentService, "commentModerationBatchSize", commentModerationBatchSize);

        toxicityScore = Mono.just(ToxicityScoreDto.builder()
                .attributeScores(Map.of(
                        CommentToxicityType.TOXICITY, new AttributeScoreDto(
                                List.of(new SpanScoreDto(new SummaryScoreDto(0.15, "PROBABILITY"))),
                                new SummaryScoreDto(0.15, "PROBABILITY")
                        ),
                        CommentToxicityType.INSULT, new AttributeScoreDto(
                                List.of(new SpanScoreDto(new SummaryScoreDto(0.05, "PROBABILITY"))),
                                new SummaryScoreDto(0.05, "PROBABILITY")
                        )
                ))
                .languages(List.of("en"))
                .detectedLanguages(List.of("en"))
                .build());
    }

    //Positive
    @Test
    @DisplayName("Test should create comment")
    void createComment() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(commentRequestMapper.toComment(commentRequestDto)).thenReturn(comment);

        commentService.createComment(commentRequestDto);

        verify(commentRepository, times(1)).save(commentCaptor.capture());
        assertEquals(comment, commentCaptor.getValue());
    }

    @Test
    @DisplayName("Test should update comment")
    void updateComment() {
        comment.setContent(CONTENT);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        commentService.updateComment(COMMENT_ID, commentUpdateDto);

        verify(commentRepository, times(1)).save(commentCaptor.capture());
        assertEquals(UPDATE_CONTENT, comment.getContent());
    }

    @Test
    @DisplayName("Test should get List CommentDto")
    void getCommentsByPostId() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(List.of(comment));
        when(commentResponseMapper.toCommentDto(any(Comment.class))).thenReturn(commentResponseDto);

        List<CommentResponseDto> result = commentService.getCommentsByPostId(POST_ID);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(List.of(commentResponseDto), result);
    }

    @Test
    @DisplayName("Test should delete comment")
    void deleteComment() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).deleteById(COMMENT_ID);

        commentService.deleteComment(COMMENT_ID);

        verify(commentRepository, times(1)).deleteById(COMMENT_ID);
    }

    //Negative
    @Test
    @DisplayName("Test Should ThrowException when fields are")
    void createComment_NullFieldsAre() {
        commentRequestDto = new CommentRequestDto();

        assertThrows(IllegalArgumentException.class,
                () -> commentService.createComment(commentRequestDto));
    }

    @Test
    @DisplayName("Test Should ThrowException when Content is Null")
    void createComment_NullContent() {
        commentRequestDto.setContent(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.createComment(commentRequestDto));

        assertEquals(ERROR_NULL_CONTENT, exception.getMessage());

    }

    @Test
    @DisplayName("Test Should ThrowException when AuthorId is Null")
    void createComment_NullAuthorId() {
        commentRequestDto.setAuthorId(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.createComment(commentRequestDto));

        assertEquals(ERROR_NULL_AUTHOR_ID, exception.getMessage());
    }

    @Test
    @DisplayName("Test Should ThrowException when PostId is Null")
    void createComment_NullPostId() {
        commentRequestDto.setPostId(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.createComment(commentRequestDto));

        assertEquals(ERROR_NULL_POST_ID, exception.getMessage());
    }

    @Test
    @DisplayName("Test Should ThrowException when not found post")
    void createComment_NotFoundPostId() {
        when(postRepository.findById(POST_ID))
                .thenThrow(new IllegalArgumentException(getErrorNotFoundPost(POST_ID)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.createComment(commentRequestDto));

        assertEquals(getErrorNotFoundPost(POST_ID), exception.getMessage());
        verify(postRepository, times(1)).findById(POST_ID);
    }

    @Test
    @DisplayName("Test Should ThrowException when not found user.")
    void createComment_NotFoundUser() {
        Request mockRequest = Request.create(Request.HttpMethod.GET, "/user/" + AUTHOR_ID, new HashMap<>(), null, StandardCharsets.UTF_8);
        when(userServiceClient.getUser(AUTHOR_ID))
                .thenThrow(new FeignException.NotFound(getErrorNotFoundUser(AUTHOR_ID), mockRequest,
                        null, null));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.createComment(commentRequestDto));
        assertTrue(exception.getMessage().contains(getErrorNotFoundUser(AUTHOR_ID)));
        verify(userServiceClient, times(1)).getUser(AUTHOR_ID);
    }

    @Test
    @DisplayName("Test Should ThrowException when fields are")
    void updateComment_NullFieldsAre() {
        commentUpdateDto = new CommentUpdateDto();

        assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(COMMENT_ID, commentUpdateDto));
    }

    @Test
    @DisplayName("Test Should ThrowException when Content is Null")
    void updateComment_NullContent() {
        commentUpdateDto.setContent(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(COMMENT_ID, commentUpdateDto));

        assertEquals(ERROR_NULL_CONTENT, exception.getMessage());

    }

    @Test
    @DisplayName("Test Should ThrowException when AuthorId is Null")
    void updateComment_NullAuthorId() {
        commentUpdateDto.setAuthorId(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(COMMENT_ID, commentUpdateDto));

        assertEquals(ERROR_NULL_AUTHOR_ID, exception.getMessage());
    }

    @Test
    @DisplayName("Test Should ThrowException when not found comment")
    void updateComment_NotFoundCommentId() {
        when(commentRepository.findById(COMMENT_ID))
                .thenThrow(new IllegalArgumentException(getErrorNotFoundComment(COMMENT_ID)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(COMMENT_ID, commentUpdateDto));

        assertEquals(getErrorNotFoundComment(COMMENT_ID), exception.getMessage());
        verify(commentRepository, times(1)).findById(COMMENT_ID);
    }

    @Test
    @DisplayName("Test Should ThrowException when not author comment")
    void updateComment_NotAuthorComment() {
        commentUpdateDto.setAuthorId(0L);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(COMMENT_ID, commentUpdateDto));

        assertEquals(ERROR_NOT_AUTHOR_COMMENT, exception.getMessage());
    }

    @Test
    @DisplayName("Test Should ThrowException when not found post")
    void getCommentsByPostId_NotFoundPostId() {
        when(postRepository.findById(POST_ID))
                .thenThrow(new IllegalArgumentException(getErrorNotFoundPost(POST_ID)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.getCommentsByPostId(POST_ID));

        assertEquals(getErrorNotFoundPost(POST_ID), exception.getMessage());
        verify(postRepository, times(1)).findById(POST_ID);
    }

    @Test
    @DisplayName("Test Should ThrowException when no comments found")
    void getCommentsByPostId_NoCommentsFound() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(Collections.emptyList());

        List<CommentResponseDto> comments = commentService.getCommentsByPostId(POST_ID);

        assertTrue(comments.isEmpty());
        verify(commentRepository, times(1)).findAllByPostId(POST_ID);
    }

    @Test
    @DisplayName("Test Should ThrowException when NULL comment id")
    void deleteComment_NullCommentId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.deleteComment(null));

        assertEquals(ERROR_NULL_COMMENT_ID, exception.getMessage());
    }

    @Test
    @DisplayName("Test Should ThrowException when not found comment")
    void deleteComment_NotFoundCommentId() {
        when(commentRepository.findById(COMMENT_ID))
                .thenThrow(new IllegalArgumentException(getErrorNotFoundComment(COMMENT_ID)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentService.deleteComment(COMMENT_ID));

        assertEquals(getErrorNotFoundComment(COMMENT_ID), exception.getMessage());
        verify(commentRepository, times(1)).findById(COMMENT_ID);
    }

    @Test
    public void testModerateComments_moderationPassed() {
        Comment comment1 = Comment.builder().content("content1").build();
        Comment comment2 = Comment.builder().content("content2").build();
        Comment comment3 = Comment.builder().content("content2").build();

        List<Comment> comments = List.of(comment1, comment2, comment3);
        Page<Comment> commentPage = new PageImpl<>(comments, PageRequest.of(0, commentModerationBatchSize), 3);

        when(commentRepository.count()).thenReturn(3L);
        when(commentRepository.findComments(any())).thenReturn(commentPage);
        when(commentAnalyzer.analyzeComment(anyString())).thenReturn(toxicityScore);

        Mono<Void> result = commentService.moderateComments();
        StepVerifier.create(result)
                .verifyComplete();

        verify(commentRepository, times(3)).save(commentCaptor.capture());
        List<Comment> capturedComments = commentCaptor.getAllValues();
        assertEquals(3, capturedComments.size());
        assertTrue(capturedComments.containsAll(comments));
        assertTrue(capturedComments.stream().allMatch(Comment::isVerified));
    }

    @Test
    public void testModerateComments_moderationFailed() {
        Comment comment1 = Comment.builder().content("This text contains offensive content").build();
        toxicityScore.block().getAttributeScores().get(CommentToxicityType.TOXICITY)
                .setSummaryScore(new SummaryScoreDto(0.45, "PROBABILITY"));

        List<Comment> comments = List.of(comment1);
        Page<Comment> commentPage = new PageImpl<>(comments, PageRequest.of(0, 1), 1);

        when(commentRepository.count()).thenReturn(1L);
        when(commentRepository.findComments(any())).thenReturn(commentPage);
        when(commentAnalyzer.analyzeComment(anyString())).thenReturn(toxicityScore);

        Mono<Void> result = commentService.moderateComments();
        StepVerifier.create(result)
                .verifyComplete();

        verify(commentRepository, times(1)).save(commentCaptor.capture());
        List<Comment> capturedComments = commentCaptor.getAllValues();
        assertEquals(1, capturedComments.size());
        assertTrue(capturedComments.containsAll(comments));
        assertFalse(capturedComments.stream().allMatch(Comment::isVerified));
    }
}