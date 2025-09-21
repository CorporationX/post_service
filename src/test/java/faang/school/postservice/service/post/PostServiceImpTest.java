package faang.school.postservice.service.post;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.comment.SaveCommentDto;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.post.RepeatPublishException;
import faang.school.postservice.factory.UserCacheFactory;
import faang.school.postservice.factory.post.PostCacheFactory;
import faang.school.postservice.factory.post.PostPublishedEventFactory;
import faang.school.postservice.kafka.producer.comment.CommentProducer;
import faang.school.postservice.kafka.producer.post.PostProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.criteria.PostSearchCriteria;
import faang.school.postservice.repository.redis.post.PostCacheRepository;
import faang.school.postservice.repository.redis.user.UserCacheRepository;
import faang.school.postservice.validation.comment.CommentValidator;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostValidator postValidator;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserFeignService userFeignService;

    @Mock
    private CommentValidator commentValidator;

    @Mock
    private CommentProducer commentProducer;

    @Mock
    private PostProducer postProducer;

    @Mock
    private PostPublishedEventFactory postPublishedEventFactory;

    @Mock
    private PostCacheFactory postCacheFactory;

    @Mock
    private PostCacheRepository postCacheRepository;

    @Mock
    private UserCacheRepository userCacheRepository;

    @Mock
    private UserCacheFactory userCacheFactory;

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Spy
    private PostMapper postMapper = new PostMapperImpl();

    @InjectMocks
    private PostServiceImpl postService;

    private final long testPostId = 1L;
    private final long testAuthorId = 11L;
    private final long testProjectId = 111L;
    private final String testOriginalContent = "content";
    private final String testUpdatedContent = "updated content";
    private final CreatePostDto createPostDto = new CreatePostDto(testOriginalContent, testAuthorId, null);
    private final UpdatePostDto updatePostDto = new UpdatePostDto(testUpdatedContent);
    private final Post post = new Post();
    private static final long POST_ID = 1L;
    private static final long AUTHOR_ID = 11L;
    private static final long COMMENT_ID = 1L;
    private static final String COMMENT_TEXT = "text";

    @Captor
    private ArgumentCaptor<CommentEvent> eventCaptor;

    @Captor
    private ArgumentCaptor<Comment> commentCaptor;

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    @Captor
    ArgumentCaptor<PostSearchCriteria> criteriaCaptor;

    @BeforeEach
    void beforeEach() {
        post.setId(testPostId);
        post.setAuthorId(testAuthorId);
        post.setContent(testOriginalContent);
    }

    @Test
    void createDoesNotSaveIfValidationError() {
        doThrow(new EntityNotFoundException("Invalid user"))
                .when(postValidator)
                .validateCreate(createPostDto);

        assertThrows(EntityNotFoundException.class, () -> postService.create(createPostDto));
        verifyNoInteractions(postMapper);
        verifyNoInteractions(postRepository);
    }

    @Test
    void createValidatesSavesAndReturnsDto() {
        Post expectedPost = postMapper.toPost(createPostDto);
        when(postRepository.save(any())).thenReturn(expectedPost);

        PostDto result = postService.create(createPostDto);

        verify(postValidator).validateCreate(createPostDto);
        verify(postRepository).save(postCaptor.capture());
        Post capturedPost = postCaptor.getValue();
        assertEquals(expectedPost.getContent(), result.content());
        assertEquals(expectedPost.getContent(), capturedPost.getContent());
    }

    @Test
    void publishSetsPublishedFieldsPublishesEventAndReturnsDto() {
        when(postRepository.findByIdAndDeletedFalse(testPostId)).thenReturn(Optional.of(post));
        PostPublishedEvent event = new PostPublishedEvent();
        when(postPublishedEventFactory.fromPost(post)).thenReturn(event);

        PostDto result = postService.publish(testPostId);

        verify(postValidator).validatePublish(post);
        verify(postRepository).save(post);
        verify(postProducer).publishPostPublishedEvent(event);
        assertTrue(post.isPublished());
        assertInstanceOf(LocalDateTime.class, post.getPublishedAt());
        assertEquals(post.getContent(), result.content());
    }

    @Test
    void publishThrowsIfPostNotFound() {
        when(postRepository.findByIdAndDeletedFalse(testPostId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.publish(testPostId));
    }

    @Test
    void publishThrowsIfValidationError() {
        when(postRepository.findByIdAndDeletedFalse(testPostId)).thenReturn(Optional.of(post));
        doThrow(new RepeatPublishException("Already published"))
                .when(postValidator)
                .validatePublish(post);

        assertThrows(RepeatPublishException.class, () -> postService.publish(testPostId));
        verify(postRepository, Mockito.never()).save(any());
    }

    //@todo jevgeni amend publish tests with event sending and caching

    @Test
    void updateThrowsIfPostNotFound() {
        when(postRepository.findByIdAndDeletedFalse(testPostId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.update(testPostId, updatePostDto));
    }

    @Test
    void updateThrowsIfValidationError() {
        when(postRepository.findByIdAndDeletedFalse(testPostId)).thenReturn(Optional.of(post));
        doThrow(new EntityNotFoundException("Deleted post"))
                .when(postValidator)
                .validateUpdate(post);

        assertThrows(EntityNotFoundException.class, () -> postService.update(testPostId, updatePostDto));
        verify(postRepository, Mockito.never()).save(any());
    }

    @Test
    void updateMapsUpdateAndReturnsDto() {
        when(postRepository.findByIdAndDeletedFalse(Mockito.anyLong())).thenReturn(Optional.of(post));

        PostDto result = postService.update(testPostId, updatePostDto);

        verify(postValidator).validateUpdate(post);
        verify(postMapper).update(updatePostDto, post);
        verify(postRepository).save(postCaptor.capture());
        Post capturedPost = postCaptor.getValue();

        assertEquals(updatePostDto.content(), capturedPost.getContent());
        assertEquals(updatePostDto.content(), result.content());
    }

    @Test
    void deleteThrowsIfPostNotFound() {
        when(postRepository.findByIdAndDeletedFalse(testPostId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.delete(testPostId));
    }

    @Test
    void deleteSetsDeletedTrueAndSaves() {
        when(postRepository.findByIdAndDeletedFalse(testPostId)).thenReturn(Optional.of(post));

        postService.delete(testPostId);

        verify(postRepository).save(post);
        assertTrue(post.isDeleted());
    }

    @Test
    void getByIdThrowsIfPostNotFound() {
        when(postRepository.findByIdAndDeletedFalse(testPostId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.getById(testPostId));
    }

    @Test
    void getByIdReturnsMappedPostDto() {
        when(postRepository.findByIdAndDeletedFalse(testPostId)).thenReturn(Optional.of(post));

        PostDto result = postService.getById(testPostId);

        assertEquals(testPostId, result.id());
        assertEquals(testOriginalContent, result.content());
    }

    @Test
    void getDraftsByUserCorrectCriteriaAndMapsAllToDtos() {
        when(postRepository.findByCriteria(any(PostSearchCriteria.class))).thenReturn(List.of(post));

        List<PostDto> result = postService.getDraftsByUser(testAuthorId);

        verify(postRepository).findByCriteria(criteriaCaptor.capture());
        PostSearchCriteria capturedCriteria = criteriaCaptor.getValue();

        assertEquals(testAuthorId, capturedCriteria.getAuthorId());
        assertNull(capturedCriteria.getProjectId());
        assertEquals(false, capturedCriteria.getDeleted());
        assertFalse(capturedCriteria.getPublished());
        assertEquals("createdAt", capturedCriteria.getSortField().getField());
        assertEquals(PostSearchCriteria.SortDirection.DESC, capturedCriteria.getSortDirection());

        assertSingleElementListWithTestPost(result);
    }


    private void assertSingleElementListWithTestPost(List<PostDto> result) {
        assertEquals(1, result.size());
        assertEquals(testPostId, result.get(0).id());
    }


    @Test
    void getDraftsByProjectCorrectCriteriaAndMapsAllToDtos() {
        when(postRepository.findByCriteria(any(PostSearchCriteria.class))).thenReturn(List.of(post));

        List<PostDto> result = postService.getDraftsByProject(testProjectId);

        verify(postRepository).findByCriteria(criteriaCaptor.capture());
        PostSearchCriteria capturedCriteria = criteriaCaptor.getValue();

        assertEquals(testProjectId, capturedCriteria.getProjectId());
        assertNull(capturedCriteria.getAuthorId());
        assertEquals(false, capturedCriteria.getDeleted());
        assertFalse(capturedCriteria.getPublished());
        assertEquals("createdAt", capturedCriteria.getSortField().getField());
        assertEquals(PostSearchCriteria.SortDirection.DESC, capturedCriteria.getSortDirection());

        assertSingleElementListWithTestPost(result);
    }

    @Test
    void getPublishedByUserCorrectCriteriaAndMapsAllToDtos() {
        when(postRepository.findByCriteria(any(PostSearchCriteria.class))).thenReturn(List.of(post));

        List<PostDto> result = postService.getPublishedByUser(testAuthorId);

        verify(postRepository).findByCriteria(criteriaCaptor.capture());
        PostSearchCriteria capturedCriteria = criteriaCaptor.getValue();

        assertEquals(testAuthorId, capturedCriteria.getAuthorId());
        assertNull(capturedCriteria.getProjectId());
        assertEquals(false, capturedCriteria.getDeleted());
        assertTrue(capturedCriteria.getPublished());
        assertEquals("publishedAt", capturedCriteria.getSortField().getField());
        assertEquals(PostSearchCriteria.SortDirection.DESC, capturedCriteria.getSortDirection());

        assertSingleElementListWithTestPost(result);
    }

    @Test
    void getPublishedByProjectCorrectCriteriaAndMapsAllToDtos() {
        when(postRepository.findByCriteria(any(PostSearchCriteria.class))).thenReturn(List.of(post));

        List<PostDto> result = postService.getPublishedByProject(testProjectId);

        verify(postRepository).findByCriteria(criteriaCaptor.capture());
        PostSearchCriteria capturedCriteria = criteriaCaptor.getValue();

        assertEquals(testProjectId, capturedCriteria.getProjectId());
        assertNull(capturedCriteria.getAuthorId());
        assertEquals(false, capturedCriteria.getDeleted());
        assertTrue(capturedCriteria.getPublished());
        assertEquals("publishedAt", capturedCriteria.getSortField().getField());
        assertEquals(PostSearchCriteria.SortDirection.DESC, capturedCriteria.getSortDirection());

        assertSingleElementListWithTestPost(result);
    }

    @Test
    @DisplayName("Should create a new comment and publish event to Kafka")
    void shouldSaveCommentReturnDtoAndPublishEvent() {
        SaveCommentDto saveDto = buildSaveCommentDto();
        Post post = buildPost();
        Comment savedComment = buildComment(post);

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentDto result = postService.createComment(POST_ID, AUTHOR_ID, saveDto);

        assertNotNull(result);
        assertEquals(COMMENT_ID, result.id());
        assertEquals(COMMENT_TEXT, result.content());
        assertEquals(AUTHOR_ID, result.authorId());
        assertEquals(POST_ID, result.postId());

        verify(userFeignService).getUserOrFail(AUTHOR_ID);
        verify(postRepository).findById(POST_ID);
        verify(commentMapper).toComment(saveDto);

        verify(commentRepository).save(commentCaptor.capture());
        Comment toSave = commentCaptor.getValue();
        assertEquals(AUTHOR_ID, toSave.getAuthorId());
        assertEquals(POST_ID, toSave.getPost().getId());

        verify(commentMapper).toCommentEvent(savedComment);
        verify(commentProducer).publishCommentEvent(eventCaptor.capture());
        CommentEvent actualEvent = eventCaptor.getValue();
        assertEquals(POST_ID, actualEvent.postId());
        assertEquals(post.getAuthorId(), actualEvent.postAuthorId());
        assertEquals(AUTHOR_ID, actualEvent.authorId());
        assertEquals(savedComment.getId(), actualEvent.commentId());
        assertEquals(savedComment.getContent(), actualEvent.content());

        verify(commentMapper).toCommentDto(savedComment);
        verifyNoMoreInteractions(commentProducer);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException if user does not exist when creating comment")
    void createCommentThrowsIfUserNotFound() {
        SaveCommentDto saveDto = buildSaveCommentDto();

        EntityNotFoundException expected = new EntityNotFoundException("User not found with id: " + AUTHOR_ID);

        doThrow(expected).when(userFeignService).getUserOrFail(AUTHOR_ID);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                postService.createComment(POST_ID, AUTHOR_ID, saveDto));

        assertEquals("User not found with id: " + AUTHOR_ID, ex.getMessage());
        verify(userFeignService).getUserOrFail(AUTHOR_ID);
        verifyNoInteractions(postRepository, commentRepository, commentMapper, commentProducer);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException if post does not exist when creating comment")
    void createCommentThrowsIfPostNotFound() {
        SaveCommentDto saveDto = buildSaveCommentDto();

        when(postRepository.findById(POST_ID))
                .thenThrow(new EntityNotFoundException("Post not found"));

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                postService.createComment(POST_ID, AUTHOR_ID, saveDto));

        assertEquals("Post not found", ex.getMessage());

        verify(userFeignService).getUserOrFail(AUTHOR_ID);
        verify(postRepository).findById(POST_ID);
        verifyNoInteractions(commentRepository, commentMapper, commentProducer);
    }

    @Test
    @DisplayName("Should return list of comments for existing post")
    void getByPostIdReturnsComments() {
        Comment comment1 = buildCommentWithIdAndContent(COMMENT_ID, "first");
        Comment comment2 = buildCommentWithIdAndContent(2L, "second");
        List<Comment> comments = List.of(comment2, comment1);

        when(postRepository.existsById(POST_ID)).thenReturn(true);
        doNothing().when(commentValidator).ensurePostExists(true, POST_ID);
        when(commentRepository.findAllByPostIdOrderByCreatedAtDesc(POST_ID)).thenReturn(comments);

        List<CommentDto> result = postService.getCommentsByPostId(POST_ID);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).id());
        assertEquals(COMMENT_ID, result.get(1).id());

        verify(postRepository).existsById(POST_ID);
        verify(commentValidator).ensurePostExists(true, POST_ID);
        verify(commentRepository).findAllByPostIdOrderByCreatedAtDesc(POST_ID);
        verify(commentMapper).toCommentDtos(comments);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException if post does not exist when retrieving comments")
    void getByPostIdThrowsIfPostNotFound() {
        when(postRepository.existsById(POST_ID)).thenReturn(false);
        doThrow(new EntityNotFoundException("Post not found"))
                .when(commentValidator).ensurePostExists(false, POST_ID);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                postService.getCommentsByPostId(POST_ID));

        assertEquals("Post not found", ex.getMessage());

        verify(postRepository).existsById(POST_ID);
        verify(commentValidator).ensurePostExists(false, POST_ID);
        verifyNoInteractions(commentRepository);
    }

    @Test
    @DisplayName("Should return empty list if no comments found for post")
    void getByPostIdReturnsEmptyListIfNoComments() {
        when(postRepository.existsById(POST_ID)).thenReturn(true);
        doNothing().when(commentValidator).ensurePostExists(true, POST_ID);
        when(commentRepository.findAllByPostIdOrderByCreatedAtDesc(POST_ID)).thenReturn(List.of());

        List<CommentDto> result = postService.getCommentsByPostId(POST_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(postRepository).existsById(POST_ID);
        verify(commentValidator).ensurePostExists(true, POST_ID);
        verify(commentRepository).findAllByPostIdOrderByCreatedAtDesc(POST_ID);
        verify(commentMapper).toCommentDtos(List.of());
    }

    private SaveCommentDto buildSaveCommentDto() {
        return new SaveCommentDto(COMMENT_TEXT);
    }

    private Post buildPost() {
        return Post.builder()
                .id(POST_ID)
                .build();
    }

    private Comment buildComment(Post post) {
        return Comment.builder()
                .id(COMMENT_ID)
                .content(COMMENT_TEXT)
                .authorId(AUTHOR_ID)
                .post(post)
                .build();
    }

    private Comment buildCommentWithIdAndContent(long id, String content) {
        return Comment.builder()
                .id(id)
                .content(content)
                .build();
    }
}