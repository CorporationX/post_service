package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.producer.kafka.KafkaPostProducer;
import faang.school.postservice.publisher.PostCreatePublisher;
import faang.school.postservice.publisher.PostViewPublisher;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.NotFoundEntityException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.repository.cache.UserCacheRepository;
import faang.school.postservice.repository.post.PostFilterRepository;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.validator.post.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private UserContext userContext;

    @Mock
    private PostViewPublisher postViewPublisher;

    @Mock
    private PostPublishService postPublishService;

    @Mock
    private PostCreatePublisher postCreatePublisher;

    @Mock
    private ModerationDictionary moderationDictionary;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostValidator postValidator;

    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Mock
    private PostFilterRepository authorFilterSpecification;

    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostCacheRepository postCacheRepository;
    @Mock
    private KafkaPostProducer kafkaPostProducer;
    @Mock
    private UserCacheRepository userCacheRepository;
    @Mock
    private UserMapper userMapper;

    private List<PostFilterRepository> postFilterRepository;

//    @InjectMocks
    private PostService postService;

    private PostCreateDto postCreateDto;
    PostUpdateDto postUpdatedDto;
    private Post post;

    @BeforeEach
    public void init() {
        postUpdatedDto = PostUpdateDto.builder().id(1L).content("updated content").build();
        post = Post.builder().id(1L).content("content").authorId(null).projectId(1L).build();
        postFilterRepository = List.of(authorFilterSpecification);

        postService = new PostService(postRepository, postFilterRepository, postValidator, postMapper,
                postPublishService, postViewPublisher, postCreatePublisher, userContext, moderationDictionary,
                userServiceClient, postCacheRepository, kafkaPostProducer, userCacheRepository, userMapper);
        ReflectionTestUtils.setField(postService, "followersBatchSize", 10);
    }

    @Test
    void publishScheduledPosts_success() {
        Post post1 = new Post();
        post1.setPublished(false);
        post1.setPublishedAt(LocalDateTime.now().minusMinutes(1));

        Post post2 = new Post();
        post2.setPublished(false);
        post2.setPublishedAt(LocalDateTime.now().minusMinutes(2));

        Post post3 = new Post();
        post3.setPublished(false);
        post3.setPublishedAt(LocalDateTime.now().minusMinutes(3));

        List<Post> scheduledPosts = Arrays.asList(post1, post2, post3);

        ReflectionTestUtils.setField(postService, "postsBatchSize", 1);

        when(postRepository.findReadyToPublish()).thenReturn(scheduledPosts);

        when(postPublishService.publishBatch(anyList()))
                .thenAnswer(invocation -> CompletableFuture.runAsync(() -> {
                    List<Post> posts = invocation.getArgument(0);
                    posts.forEach(post -> post.setPublished(true));
                }));

        postService.publishScheduledPosts();

        for (Post post : scheduledPosts) {
            assertTrue(post.isPublished());
        }

        verify(postPublishService, times(3)).publishBatch(any());
        verify(postRepository, times(3)).saveAll(any());
    }

    @Test
    public void testCreatePostWithNullPostDto() {
        postCreateDto = null;

        assertThrows(NullPointerException.class, () -> postService.create(postCreateDto));
        verify(postValidator, never()).checkIfPostHasAuthor(any(), any());
        verify(postRepository, never()).save(any());
    }

    static Stream<Arguments> initIncorrectAuthorAndProjectIdPostCreate() {
        return Stream.of(
                Arguments.of(PostCreateDto.builder().content("content").projectId(null).authorId(null).build(), "Only one of projectId or authorId must be provided"),
                Arguments.of(PostCreateDto.builder().content("content").projectId(1L).authorId(1L).build(), "Only one of projectId or authorId must be provided")
        );
    }

    @ParameterizedTest
    @MethodSource("initIncorrectAuthorAndProjectIdPostCreate")
    public void testCreatePostWithIncorrectAuthorAndProjectId(PostCreateDto postCreateDto, String errorMessage) {
        doThrow(new DataValidationException(errorMessage)).when(postValidator).checkIfPostHasAuthor(postCreateDto.getAuthorId(), postCreateDto.getProjectId());

        assertThrows(DataValidationException.class, () -> postService.create(postCreateDto));

        verify(postRepository, never()).save(any());
    }

    @Test
    public void testPublishWithDoesntPost() {
        when(postRepository.findById(post.getId())).thenThrow(DataValidationException.class);

        assertThrows(DataValidationException.class, () -> postService.publish(post.getId()));
        verify(postRepository, never()).save(any());
    }

    @Test
    public void testPublishWithAlreadyPublishedPost() {
        post.setPublished(true);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        doThrow(DataValidationException.class).when(postValidator).checkPostPublished(post.getId(), post.isPublished());

        assertThrows(DataValidationException.class, () -> postService.publish(post.getId()));
        verify(postRepository, never()).save(any());
    }

    @Test
    public void testPublishSuccessfully() {
        Post expectedPost = Post.builder().id(1L).content("content").authorId(1L).projectId(1L).published(true).build();
        PostDto expectedDtoPost = PostDto.builder().id(1L).content("content").authorId(1L).projectId(1L).published(true).build();
        Post postToUpdate = Post.builder().id(1L).content("content").authorId(1L).projectId(1L).published(false).build();
        UserDto userDto = UserDto.builder().followersId(List.of(2L,3L,4L)).build();
        when(postRepository.findById(postToUpdate.getId())).thenReturn(Optional.of(postToUpdate));
        doNothing().when(postValidator).checkPostPublished(post.getId(), post.isPublished());
        when(postRepository.save(postToUpdate)).thenReturn(expectedPost);
        when(postMapper.toDto(any(Post.class))).thenReturn(expectedDtoPost);
        when(userServiceClient.getUser(anyLong())).thenReturn(userDto);

        PostDto actualPublished = postService.publish(postToUpdate.getId());

        assertTrue(actualPublished.isPublished());
    }

    @Test
    public void testDeleteByIdWithDoesntExistPost() {
        Long id = 1L;
        when(postRepository.findById(id)).thenThrow(new DataValidationException(String.format("Post %s doesn't exist", id)));

        assertThrows(DataValidationException.class, () -> postService.delete(id));
    }

    @Test
    public void testDeleteByIdSuccessfully() {
        Post deletedPost = Post.builder().id(1L).content("content").authorId(null).projectId(1L).published(false).deleted(true).build();
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(deletedPost));

        postService.delete(deletedPost.getId());

        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(deletedPost);
    }

    @Test
    public void testGetByIdWithDoesntExistPost() {
        Long id = 1L;
        when(postRepository.findById(id)).thenThrow(new DataValidationException(String.format("Post %s doesn't exist", id)));

        assertThrows(DataValidationException.class, () -> postService.getById(id));
    }

    @Test
    public void testGetByIdSuccessfully() {
        Long id = 1L;
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        doNothing().when(postViewPublisher).publish(any());

        PostDto actual = postService.getById(id);

        assertEquals(post.getId(), actual.getId());
    }

    @Test
    public void testGetDraftOrPublishedPostsSuccessfully() {
        PostFilterDto postFilter = PostFilterDto.builder().projectId(1L).authorId(null).published(true).page(0).size(1).build();

        Page<Post> postPage = new PageImpl<>(Collections.singletonList(post));
        when(postFilterRepository.get(0).isApplicable(any())).thenReturn(true);
        when(postFilterRepository.get(0).apply(any())).thenReturn((root, query, builder) -> builder.equal(root.get("authorId"), postFilter.getAuthorId()));
        when(postRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(postPage);

        Page<PostDto> result = postService.getPostsByPublishedStatus(postFilter);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(postRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void updatePostWhenPostDoesNotExist() {
        doNothing().when(postValidator).validateForUpdating(postUpdatedDto);
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> postService.update(postUpdatedDto));

        assertEquals(String.format("Post %s doesn't exist", postUpdatedDto.getId()), exception.getMessage());
    }

    @Test
    void updatePostSuccessfully() {
        doNothing().when(postValidator).validateForUpdating(postUpdatedDto);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostDto result = postService.update(postUpdatedDto);

        assertNotNull(result);
    }

    @Test
    void validationAndPostReceived_ShouldReturnPost_WhenValidPostIdIsProvided() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        LikeDto likeDto = LikeDto.builder().postId(postId).build();
        when(postRepository.existsById(postId)).thenReturn(true);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.validationAndPostReceived(likeDto);

        assertEquals(post, result);
    }

    @Test
    void validationAndPostReceived_ShouldThrowDataValidationExceptions_WhenPostIdIsNull() {
        LikeDto likeDto = LikeDto.builder().postId(null).build();

        assertThrows(DataValidationException.class, () ->
                postService.validationAndPostReceived(likeDto));
    }

    @Test
    void validationAndPostReceived_ShouldThrowDataValidationExceptions_WhenPostDoesNotExist() {
        Long postId = 1L;
        LikeDto likeDto = LikeDto.builder().postId(postId).build();
        when(postRepository.existsById(postId)).thenReturn(false);

        assertThrows(DataValidationException.class, () ->
                postService.validationAndPostReceived(likeDto));
    }

    @Test
    void validationAndPostReceived_ShouldThrowNotFoundElementException_WhenPostNotFound() {
        Long postId = 1L;
        LikeDto likeDto = LikeDto.builder().postId(postId).build();
        when(postRepository.existsById(postId)).thenReturn(true);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(NotFoundEntityException.class, () ->
                postService.validationAndPostReceived(likeDto));
    }
}
