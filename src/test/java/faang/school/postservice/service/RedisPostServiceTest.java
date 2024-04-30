package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.dto.UserBanEventDto;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.mapper.ResourceMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.publisher.UserBanEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RedisPostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapperImpl postMapper;

    @Mock
    private PostValidator postValidator;
    @Mock
    private UserBanEventPublisher userBanEventPublisher;

    @InjectMocks
    private PostService postService;

    private PostDto postDto = new PostDto();
    private Post post1;
    private Post post2;
    private Post post3;
    @Spy
    private ResourceMapperImpl resourceMapper;

    @Mock
    private ResourceService resourceService;
    private final long existencePostId = 5L;
    private Post post;
    private Post updatePost;
    private PostDto updatePostDto;
    List<MultipartFile> files = new ArrayList<>();
    List<Resource> resources = new ArrayList<>();
    List<Resource> updatedResources = new ArrayList<>();
    private final long notExistencePostId = existencePostId + 1;


    @BeforeEach
    public void init() {
        postDto.setContent("content");
        postDto.setId(10L);
        postDto.setAuthorId(1L);
        postDto.setProjectId(2L);

        LocalDateTime createdAt1 = LocalDateTime.of(2024, Month.JANUARY, 28, 1, 1, 1);
        LocalDateTime createdAt2 = LocalDateTime.of(2024, Month.JANUARY, 28, 1, 1, 2);
        LocalDateTime createdAt3 = LocalDateTime.of(2024, Month.JANUARY, 28, 1, 1, 3);


        post1 = Post.builder()
                .id(1L)
                .content("another content")
                .authorId(1L)
                .published(true)
                .deleted(false)
                .createdAt(createdAt1)
                .build();
        post2 = Post.builder()
                .id(2L)
                .authorId(1L)
                .content("2")
                .createdAt(createdAt2)
                .deleted(false)
                .published(false)
                .build();
        post3 = Post.builder()
                .id(3L)
                .authorId(1L)
                .content("3")
                .createdAt(createdAt3)
                .deleted(true)
                .published(true)
                .build();

        MultipartFile mockFile = mock(MultipartFile.class);
        int filesAmount = 3;
        IntStream.range(0, filesAmount).forEach((i) ->
                files.add(mockFile)
        );

        int resourcesAmount = 3;
        IntStream.range(0, resourcesAmount).forEach((i) ->
                resources.add(Resource.builder().id((long) i).build())
        );

        post = Post.builder()
                .id(existencePostId)
                .content("content")
                .resources(resources)
                .build();

        postDto = PostDto.builder()
                .id(existencePostId)
                .content("content")
                .resourceIds(post.getResources().stream().map(Resource::getId).toList())
                .build();

        int updatedResourcesAmount = resourcesAmount - 1;
        IntStream.range(0, updatedResourcesAmount).forEach(i ->
                updatedResources.add(resources.get(i))
        );
        updatePost = Post.builder()
                .id(existencePostId)
                .content("content")
                .resources(updatedResources)
                .build();

        updatePostDto = PostDto.builder()
                .id(existencePostId)
                .content("content")
                .resourceIds(updatePost.getResources().stream().map(Resource::getId).toList())
                .build();

    }

    @Test
    public void testCreateDraftSuccess() {
        postService.createPostDraft(postDto);
        Post post = postMapper.toEntity(postDto);
        when(postRepository.save(post)).thenReturn(post);
        Post savedPost = postRepository.save(post);

        assertSame(post, savedPost);
    }

    @Test
    public void testCreateDraftFailed() {
        postService.createPostDraft(postDto);
        Post post = postMapper.toEntity(postDto);
        post.setId(30L);
        when(postRepository.save(post1)).thenReturn(post1);
        Post savedPost = postRepository.save(post1);
        assertNotEquals(post.getId(), savedPost.getId());
    }

    @Test
    public void testPublishPostSuccess() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        postService.publishPost(1L, 1L);
        assertTrue(post1.isPublished());
    }

    @Test
    public void testUpdatePostSuccess() {
        PostDto updatedDto = new PostDto();
        updatedDto.setContent("updated content");
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        postService.updatePost(1L, 1L, updatedDto);
        assertSame("updated content", post1.getContent());
    }

    @Test
    public void testUpdatePostFailed() {
        PostDto updatedDto = new PostDto();
        updatedDto.setContent("updated content");
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        postService.updatePost(1L, 1L, updatedDto);
        assertNotEquals("not updated content", post1.getContent());
    }

    @Test
    public void testDeletePostSuccess() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        postService.deletePost(1L, 1L);
        assertTrue(post1.isDeleted());
    }

    @Test
    public void testGetPostByIdSuccess() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        postService.getPost(1L);
        assertSame(post1, postService.getPost(1L));
    }

    @Test
    public void testGetAuthorDraftsSuccess() {
        List<Post> posts = List.of(post1, post2, post3);
        when(postRepository.findByAuthorId(1L)).thenReturn(posts);
        List<PostDto> postDtos = postService.getAuthorDrafts(1L);
        verify(postRepository, Mockito.times(1)).findByAuthorId(1L);
        assertEquals(1, postDtos.size());
    }

    @Test
    public void testGetAuthorDraftsFailed() {
        List<Post> posts = List.of(post1, post2, post3);
        when(postRepository.findByAuthorId(1L)).thenReturn(posts);
        List<PostDto> postDtos = postService.getAuthorDrafts(1L);
        verify(postRepository, Mockito.times(1)).findByAuthorId(1L);
        assertNotEquals(3, postDtos.size());
    }

    @Test
    public void testGetProjectDraftsSuccess() {
        post1.setAuthorId(null);
        post1.setProjectId(1L);
        post2.setAuthorId(null);
        post2.setProjectId(1L);
        post3.setAuthorId(null);
        post3.setProjectId(1L);
        List<Post> posts = List.of(post1, post2, post3);
        lenient().when(postRepository.findByProjectId(1L)).thenReturn(posts);
        List<PostDto> postDtos = postService.getProjectDrafts(1L);
        assertEquals(1, postDtos.size());
    }

    @Test
    public void testGetProjectDraftsFailed() {
        post1.setAuthorId(null);
        post1.setProjectId(1L);
        post2.setAuthorId(null);
        post2.setProjectId(1L);
        post3.setAuthorId(null);
        post3.setProjectId(1L);
        List<Post> posts = List.of(post1, post2, post3);
        lenient().when(postRepository.findByProjectId(1L)).thenReturn(posts);
        List<PostDto> postDtos = postService.getProjectDrafts(1L);
        assertNotEquals(2, postDtos.size());
    }

    @Test
    public void testGetAuthorPostsSuccess() {
        List<Post> posts = List.of(post1, post2, post3);
        when(postRepository.findByAuthorId(1L)).thenReturn(posts);
        List<PostDto> postDtos = postService.getAuthorDrafts(1L);
        assertEquals(1, postDtos.size());
    }

    @Test
    public void testGetAuthorPostsFailed() {
        List<Post> posts = List.of(post1, post2, post3);
        when(postRepository.findByAuthorId(1L)).thenReturn(posts);
        List<PostDto> postDtos = postService.getAuthorDrafts(1L);
        assertNotEquals(5, postDtos.size());
    }

    @Test
    public void testGetProjectPostsSuccess() {
        post1.setAuthorId(null);
        post1.setProjectId(1L);
        post2.setAuthorId(null);
        post2.setProjectId(1L);
        post3.setAuthorId(null);
        post3.setProjectId(1L);
        List<Post> posts = List.of(post1, post2, post3);
        when(postRepository.findByProjectId(1L)).thenReturn(posts);
        List<PostDto> postDtos = postService.getProjectPosts(1L);
        assertEquals(1, postDtos.size());
    }

    @Test
    public void testGetProjectPostsFailed() {
        post1.setAuthorId(null);
        post1.setProjectId(1L);
        post2.setAuthorId(null);
        post2.setProjectId(1L);
        post3.setAuthorId(null);
        post3.setProjectId(1L);
        List<Post> posts = List.of(post1, post2, post3);
        when(postRepository.findByProjectId(1L)).thenReturn(posts);
        List<PostDto> postDtos = postService.getProjectPosts(1L);
        assertNotEquals(3, postDtos.size());
    }

    @Test
    public void testSortDraftsSuccess() {
        long ownerId = 1;
        List<Post> posts = List.of(post1, post2, post3);
        lenient().when(postRepository.findByProjectId(ownerId)).thenReturn(posts);
        List<PostDto> expectedSortedDrafts = postMapper.toDtoList(List.of(post2));
        List<PostDto> sortedDrafts = postService.sortDrafts(posts);

        assertEquals(expectedSortedDrafts, sortedDrafts);
    }

    @Test
    public void testSortDraftsFailed() {
        long ownerId = 1;
        List<Post> posts = List.of(post1, post2, post3);
        lenient().when(postRepository.findByProjectId(ownerId)).thenReturn(posts);
        List<PostDto> expectedSortedDrafts = postMapper.toDtoList(List.of(post2, post3));
        List<PostDto> sortedDrafts = postService.sortDrafts(posts);

        assertNotEquals(expectedSortedDrafts, sortedDrafts);
    }

    @Test
    public void testSortPostsSuccess() {
        long ownerId = 1;
        List<Post> posts = List.of(post1, post2, post3);
        lenient().when(postRepository.findByProjectId(ownerId)).thenReturn(posts);
        List<PostDto> expectedSortedDrafts = postMapper.toDtoList(List.of(post1));
        List<PostDto> sortedDrafts = postService.sortPosts(posts);

        assertEquals(expectedSortedDrafts, sortedDrafts);
    }

    @Test
    public void testSortPostsFailed() {
        long ownerId = 1;
        List<Post> posts = List.of(post1, post2, post3);
        lenient().when(postRepository.findByProjectId(ownerId)).thenReturn(posts);
        List<PostDto> expectedSortedDrafts = postMapper.toDtoList(List.of(post1, post2));
        List<PostDto> sortedDrafts = postService.sortPosts(posts);

        assertNotEquals(expectedSortedDrafts, sortedDrafts);
    }

    @Test
    void testGetPostDto_postExists_returnsPostDto() {
        mockFindById(true);

        PostDto postDtoByService = postService.getPostDto(existencePostId);

        assertEquals(post.getId(), postDtoByService.getId());
        assertEquals(postDto, postDtoByService);
    }

    @Test
    void testGetPostDto_postNotExists_throwsEntityNotFoundException() {
        mockFindById(false);

        assertThrows(
                EntityNotFoundException.class,
                () -> postService.getPostDto(notExistencePostId)
        );
    }

    @Test
    void testCheckAndBanAuthors_moreThenFiveNotVerifiedPosts_publishUserBanEvent() {
        long firstAuthorId = 2L;
        long secondAuthorId = 3L;
        List<Long> expectedAuthorIdsToBan = List.of(firstAuthorId, secondAuthorId);
        UserBanEventDto firstExpectedUserBanEventDto = new UserBanEventDto(firstAuthorId);
        UserBanEventDto secondExpectedUserBanEventDto = new UserBanEventDto(secondAuthorId);

        when(postRepository.findAuthorIdsByNotVerifiedPosts(anyInt())).thenReturn(expectedAuthorIdsToBan);

        postService.checkAndBanAuthors();

        verify(userBanEventPublisher, times(1)).publish(firstExpectedUserBanEventDto);
        verify(userBanEventPublisher, times(1)).publish(secondExpectedUserBanEventDto);
    }

    @Test
    void testCheckAndBanAuthors_lessThenFiveNotVerifiedPosts_nothingHappens() {
        List<Long> expectedAuthorIdsToBan = Collections.emptyList();

        when(postRepository.findAuthorIdsByNotVerifiedPosts(anyInt())).thenReturn(expectedAuthorIdsToBan);

        postService.checkAndBanAuthors();

        verify(userBanEventPublisher, never()).publish(any());
    }

    @Test
    void testCreatePost_withNoFiles_savePostWithNoFiles() {
        files = null;
        Post postToSave = postMapper.toEntity(postDto);
        PostDto expectedDto = postMapper.toDto(postToSave);

        when(postRepository.save(postToSave)).thenReturn(postToSave);

        PostDto postDtoByService = postService.createPost(postDto, files);

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());
        Post savedPost = captor.getValue();

        assertEquals(postToSave, savedPost);
        assertEquals(expectedDto, postDtoByService);
    }

    @Test
    void testCreatePost_withFiles_savePostWithFiles() {
        List<ResourceDto> resourceDtos = resources.stream().map(resourceMapper::toDto).toList();
        List<Long> expectedResourceIds = new ArrayList<>(resourceDtos.stream().map(ResourceDto::getId).toList());
        expectedResourceIds.addAll(post.getResources().stream().map(Resource::getId).toList());
        Post postToSave = postMapper.toEntity(postDto);

        when(postRepository.save(postToSave)).thenReturn(post);
        when(resourceService.createResources(post, files)).thenReturn(resourceDtos);

        PostDto postDtoByService = postService.createPost(postDto, files);

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());
        Post savedPost = captor.getValue();

        assertEquals(postToSave, savedPost);
        assertEquals(expectedResourceIds, postDtoByService.getResourceIds());
    }

    @Test
    void testUpdatePost_postNotExists_throwsEntityNotFoundException() {
        mockFindById(false);
        assertThrows(
                EntityNotFoundException.class,
                () -> postService.updatePost(notExistencePostId, postDto, files)
        );
    }

    @Test
    void testUpdatePost_postDtoWithNoOneResourceId_updatePostAndDeleteOneFile() {
        Post postToUpdate = mockFindById(true);
        postToUpdate.setResources(updatedResources);

        when(postRepository.save(any())).thenReturn(updatePost);

        PostDto postDtoByService = postService.updatePost(existencePostId, updatePostDto, null);

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());
        Post savedPost = captor.getValue();


        assertEquals(updatePostDto.getContent(), postDtoByService.getContent());
        assertEquals(postMapper.toDto(updatePost), postMapper.toDto(savedPost));
        assertEquals(updatePostDto.getResourceIds().size(), savedPost.getResources().size());
    }

    private Post mockFindById(boolean exist) {
        if (!exist) {
            when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
            return null;
        }
        when(postRepository.findById(existencePostId)).thenReturn(Optional.of(post));
        return post;
    }

}
