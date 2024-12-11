package faang.school.postservice.service;


import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.redis.RedisTopicProperties;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.sightengine.textAnalysis.ModerationClasses;
import faang.school.postservice.dto.sightengine.textAnalysis.TextAnalysisResponse;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.SightengineBadRequestException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.moderation.ModerationDictionary;
import faang.school.postservice.service.moderation.sightengine.ModerationVerifier;
import faang.school.postservice.service.moderation.sightengine.ModerationVerifierFactory;
import faang.school.postservice.service.moderation.sightengine.SightEngineReactiveClient;
import faang.school.postservice.message.producer.MessagePublisher;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private SightEngineReactiveClient sightEngineReactiveClient;

    @Mock
    private ModerationVerifierFactory verifierFactory;

    @Mock
    private ModerationDictionary dictionary;

    @Mock
    private MessagePublisher messagePublisher;

    @Mock
    private RedisTopicProperties redisTopicProperties;

    private Post post;
    private PostDto postDto;
    private ProjectDto projectDto;
    private UserDto userDto;
    private List<Post> postList;
    private long postId;
    private Post post5;
    private String verifiedContent;
    private ModerationVerifier moderationVerifier;

    private static final int MAX_UNVERIFIED_POSTS_BEFORE_BAN = 5;

    @BeforeEach
    public void setUp() {
        post = new Post();
        post.setId(1L);
        post.setCreatedAt(LocalDateTime.MIN);
        post.setPublishedAt(LocalDateTime.now());

        userDto = UserDto.builder()
                .id(1L)
                .username("John")
                .email("john@example.com")
                .build();

        projectDto = ProjectDto.builder()
                .id(1L)
                .build();

        postDto = PostDto.builder()
                .authorId(1L)
                .build();

        Post secondPost = new Post();
        secondPost.setId(2L);
        secondPost.setAuthorId(2L);
        secondPost.setCreatedAt(LocalDateTime.now());
        secondPost.setPublishedAt(LocalDateTime.now());
        postList = List.of(post, secondPost);

        postId = 5L;
        post5 = Post.builder()
                .id(postId)
                .build();

        verifiedContent = "verified content";

        moderationVerifier = ModerationVerifier.builder()
                .sexual(0.7)
                .discriminatory(0.7)
                .insulting(0.7)
                .violent(0.7)
                .toxic(0.7)
                .build();
    }

    @Test
    public void testCreatePost() {
        when(postMapper.toEntity(postDto)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(postDto);
        when(userServiceClient.getUserById(userDto.getId())).thenReturn(userDto);
        when(postRepository.save(post)).thenReturn(post);

        postService.createPost(postDto);

        verify(postMapper, times(1)).toEntity(postDto);
        verify(postMapper, times(1)).toDto(post);
        verify(postRepository, times(1)).save(post);
        verify(userServiceClient, times(1)).getUserById(userDto.getId());
    }

    @Test
    public void testCreatePostWithoutAuthor() {
        postDto = PostDto.builder().build();
        assertThrows(DataValidationException.class, () -> postService.createPost(postDto));
    }

    @Test
    public void testCreatePostWithProjectAndUserAuthor() {
        postDto = PostDto.builder()
                .projectId(1L)
                .authorId(1L)
                .build();
        assertThrows(DataValidationException.class, () -> postService.createPost(postDto));
    }

    @Test
    public void testPublishPost() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        PostDto dto = postService.publishPost(1L);

        verify(postRepository, times(1)).findById(anyLong());
        assertTrue(dto.published());
    }

    @Test
    public void testPublishDeletedPost() {
        post.setDeleted(true);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        assertThrows(DataValidationException.class, () -> postService.publishPost(1L));
        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testUpdatePost() {
        post.setContent("content");
        UpdatePostDto updatePostDto = UpdatePostDto.builder()
                .content("new content")
                .build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        PostDto dto = postService.updatePost(1L, updatePostDto);

        verify(postRepository, times(1)).findById(anyLong());
        assertEquals(dto.content(), updatePostDto.content());
    }

    @Test
    public void testUpdateDeletedPost() {
        UpdatePostDto updatePostDto = UpdatePostDto.builder()
                .content("new content")
                .build();
        post.setDeleted(true);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        assertThrows(DataValidationException.class, () -> postService.updatePost(1L, updatePostDto));

        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testDeletePost() {
        post.setDeleted(false);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        PostDto dto = postService.deletePost(1L);

        verify(postRepository, times(1)).findById(anyLong());
        assertTrue(dto.deleted());
    }

    @Test
    public void testDeleteDeletedPost() {
        post.setDeleted(true);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        assertThrows(DataValidationException.class, () -> postService.deletePost(1L));
        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testGetAllDraftNotDeletedPostsByUserId() {
        when(postRepository.findByAuthorId(anyLong())).thenReturn(postList);

        List<PostDto> dtoList = postService.getAllDraftNotDeletedPostsByUserId(1L);

        verify(postRepository, times(1)).findByAuthorId(anyLong());
        assertTrue(dtoList.stream()
                .allMatch(dto -> !dto.published() && !dto.deleted()));
    }

    @Test
    public void testGetAllPublishedNotDeletedPostsByUserId() {
        postList.forEach(p -> p.setPublished(true));
        when(postRepository.findByAuthorId(anyLong())).thenReturn(postList);

        List<PostDto> dtoList = postService.getAllPublishedNotDeletedPostsByUserId(1L);

        verify(postRepository, times(1)).findByAuthorId(anyLong());
        assertTrue(dtoList.stream()
                .allMatch(dto -> dto.published() && !dto.deleted()));
    }

    @Test
    public void testGetById() {
        // arrange
        long postId = 5L;
        Optional<Post> post = Optional.ofNullable(Post.builder()
                .id(postId)
                .build());

        when(postRepository.findById(postId)).thenReturn(post);

        // act
        Optional<Post> returnedPost = postService.findPostById(postId);

        // assert
        assertEquals(post, returnedPost);
    }

    @Test
    public void testGetPostByIdWithExistentPost() {
        when(postRepository.findById(postId))
                .thenReturn(Optional.ofNullable(post5));

        Post result = postService.getPostById(postId);

        assertNotNull(result);
        assertEquals(postId, result.getId());
    }

    @Test
    public void testGetPostByIdWhenPostNotExist() {
        when(postRepository.findById(postId))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> postService.getPostById(postId));
    }

    @Test
    public void testIsPostNotExistWithExistentPost() {
        when(postRepository.existsById(postId)).thenReturn(true);

        boolean result = postService.isPostNotExist(postId);

        assertFalse(result);
    }

    @Test
    public void testIsPostNotExistWhenPostNotExist() {
        when(postRepository.existsById(postId)).thenReturn(false);

        boolean result = postService.isPostNotExist(postId);

        assertTrue(result);
    }

    @Test
    void testVerifyPostAsync_WhenSuccessfulResponse_ShouldVerifyAndSavePost() {
        post.setContent(verifiedContent);
        List<Post> posts = List.of(post);

        TextAnalysisResponse textAnalysisResponse = new TextAnalysisResponse();
        ModerationClasses moderationClasses = new ModerationClasses();
        moderationClasses.setSexual(0.1);
        moderationClasses.setDiscriminatory(0.1);
        moderationClasses.setInsulting(0.1);
        moderationClasses.setViolent(0.1);
        moderationClasses.setToxic(0.1);
        textAnalysisResponse.setModerationClasses(moderationClasses);

        when(verifierFactory.create()).thenReturn(moderationVerifier);
        when(sightEngineReactiveClient.analyzeText(verifiedContent))
                .thenReturn(Mono.just(textAnalysisResponse));

        postService.verifyPostAsync(posts);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());

        Post post = postCaptor.getValue();
        assertEquals(verifiedContent, post.getContent());
        assertNotNull(post.getVerifiedDate());
        assertTrue(post.isVerified());
    }

    @Test
    void testVerifyPostAsync_WhenClientError_ShouldUseDictionaryAndSavePost() {
        post.setContent(verifiedContent);
        List<Post> posts = List.of(post);

        when(sightEngineReactiveClient.analyzeText(verifiedContent))
                .thenReturn(Mono.error(new SightengineBadRequestException("Bad request")));
        when(dictionary.isVerified(anyString())).thenReturn(true);

        postService.verifyPostAsync(posts);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());

        Post post = postCaptor.getValue();
        assertEquals(verifiedContent, post.getContent());
        assertNotNull(post.getVerifiedDate());
        assertTrue(post.isVerified());
    }

    @Test
    void testVerifyPostAsync_WhenResponseIsNull_ShouldUseDictionaryAndSavePost() {
        post.setContent(verifiedContent);
        List<Post> posts = List.of(post);

        TextAnalysisResponse textAnalysisResponse = new TextAnalysisResponse();
        when(sightEngineReactiveClient.analyzeText(verifiedContent))
                .thenReturn(Mono.just(textAnalysisResponse));
        when(dictionary.isVerified(anyString())).thenReturn(true);

        postService.verifyPostAsync(posts);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());

        Post post = postCaptor.getValue();
        assertEquals(verifiedContent, post.getContent());
        assertNotNull(post.getVerifiedDate());
        assertTrue(post.isVerified());
    }

    @Test
    void testIsVerified_WhenHighToxicityLevels_ShouldNotVerifyContent() {
        post.setContent(verifiedContent);

        TextAnalysisResponse response = new TextAnalysisResponse();
        ModerationClasses moderationClasses = new ModerationClasses();
        moderationClasses.setSexual(0.9);
        moderationClasses.setDiscriminatory(0.9);
        moderationClasses.setInsulting(0.9);
        moderationClasses.setViolent(0.9);
        moderationClasses.setToxic(0.9);
        response.setModerationClasses(moderationClasses);

        when(verifierFactory.create()).thenReturn(moderationVerifier);
        when(sightEngineReactiveClient.analyzeText(verifiedContent))
                .thenReturn(Mono.just(response));

        postService.verifyPostAsync(List.of(post));

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());

        Post post = postCaptor.getValue();
        assertEquals(verifiedContent, post.getContent());
        assertNotNull(post.getVerifiedDate());
        assertFalse(post.isVerified());
    }

    @Test
    void testBanAuthorsWithTooManyUnverifiedPosts_withNoUnverifiedPosts_shouldNotBan() {
        when(postRepository.findByVerifiedIsFalse()).thenReturn(Collections.emptyList());

        postService.banAuthorsWithTooManyUnverifiedPosts();

        verify(messagePublisher, never()).publish(anyString(), anyString());
    }

    @Test
    void testBanAuthorsWithTooManyUnverifiedPosts_withExceedLimit_shouldBan() {
        Long authorId1 = 1L;
        Long authorId2 = 2L;
        String banUserTopic = "banUserTopic";

        List<Post> posts = new ArrayList<>();
        posts.addAll(createUnverifiedPosts(authorId1, MAX_UNVERIFIED_POSTS_BEFORE_BAN + 1));
        posts.addAll(createUnverifiedPosts(authorId2, MAX_UNVERIFIED_POSTS_BEFORE_BAN - 1));

        when(postRepository.findByVerifiedIsFalse()).thenReturn(posts);
        when(redisTopicProperties.getBanUserTopic()).thenReturn(banUserTopic);

        postService.banAuthorsWithTooManyUnverifiedPosts();

        verify(messagePublisher).publish(banUserTopic, authorId1);
        verify(messagePublisher).publish(banUserTopic, authorId2);
    }

    @Test
    void testBanAuthorsWithTooManyUnverifiedPosts_withSomeAuthorsExceedLimitAndSomeNot() {
        Long authorId1 = 1L;
        Long authorId2 = 2L;
        String banUserTopic = "banUserTopic";

        List<Post> posts = new ArrayList<>();
        posts.addAll(createUnverifiedPosts(authorId1, MAX_UNVERIFIED_POSTS_BEFORE_BAN + 1));
        posts.addAll(createUnverifiedPosts(authorId2, MAX_UNVERIFIED_POSTS_BEFORE_BAN - 1));

        when(postRepository.findByVerifiedIsFalse()).thenReturn(posts);
        when(redisTopicProperties.getBanUserTopic()).thenReturn(banUserTopic);

        postService.banAuthorsWithTooManyUnverifiedPosts();

        verify(messagePublisher).publish(banUserTopic, authorId1);
        verify(messagePublisher, never()).publish(banUserTopic, authorId2);
    }

    private List<Post> createUnverifiedPosts(Long authorId, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    Post post = new Post();
                    post.setAuthorId(authorId);
                    post.setVerified(false);
                    return post;
                })
                .collect(Collectors.toList());
    }
}
