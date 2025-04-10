//package faang.school.postservice.service.post;
//
//
//import faang.school.postservice.client.ProjectServiceClient;
//import faang.school.postservice.client.UserServiceClient;
//import faang.school.postservice.dto.post.PostDto;
//import faang.school.postservice.exception.DataValidationException;
//import faang.school.postservice.mapper.post.PostMapperImpl;
//import faang.school.postservice.model.Post;
//import faang.school.postservice.repository.PostRepository;
//import feign.FeignException;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class PostServiceTest {
//
//    @InjectMocks
//    private PostService postService;
//
//    @Mock
//    private ProjectServiceClient projectServiceClient;
//
//    @Mock
//    private UserServiceClient userServiceClient;
//
//    @Mock
//    private PostRepository postRepository;
//
//    @Spy
//    private PostMapperImpl postMapper;
//
//    private final long postId = 1;
//    private final long authorId = 2;
//    private final long projectId= 3;
//    private final Post post = new Post();
//    private final PostDto postDto = new PostDto();
//    private final Post post1 = Post.builder().id(1L).published(true).createdAt(LocalDateTime.now().minusDays(1)).build();
//    private final Post post2 = Post.builder().id(2L).published(true).createdAt(LocalDateTime.now().minusDays(2)).build();
//    private final Post post3 = Post.builder().id(3L).published(true).createdAt(LocalDateTime.now().minusDays(3)).build();
//    private final Post draftPost1 = Post.builder().id(4L).createdAt(LocalDateTime.now().minusDays(4)).build();
//    private final Post draftPost2 = Post.builder().id(5L).createdAt(LocalDateTime.now().minusDays(5)).build();
//    private final Post draftPost3 = Post.builder().id(6L).createdAt(LocalDateTime.now().minusDays(6)).build();
//
//    private final List<Post> foundList = List.of(
//            post1,
//            Post.builder().id(7L).published(true).deleted(true).createdAt(LocalDateTime.now().minusDays(7)).build(),
//            post2,
//            Post.builder().id(10L).deleted(true).createdAt(LocalDateTime.now().minusDays(10)).build(),
//            post3,
//            draftPost1,
//            Post.builder().id(8L).published(true).deleted(true).createdAt(LocalDateTime.now().minusDays(8)).build(),
//            draftPost2,
//            Post.builder().id(9L).deleted(true).createdAt(LocalDateTime.now().minusDays(9)).build(),
//            draftPost3,
//            Post.builder().id(11L).deleted(true).createdAt(LocalDateTime.now().minusDays(11)).build());
//
//    private PostDto createDto;
//    private List<PostDto> posts;
//    private List<PostDto> draftPosts;
//
//    @BeforeEach
//    public void setUp() {
//        post.setContent("content");
//        postDto.setContent("content");
//        posts = List.of(
//                postMapper.toDto(post1),
//                postMapper.toDto(post2),
//                postMapper.toDto(post3)
//        );
//
//        draftPosts = List.of(
//                postMapper.toDto(draftPost1),
//                postMapper.toDto(draftPost2),
//                postMapper.toDto(draftPost3));
//    }
//
//    /**
//     * createDraftPost
//     */
//
//    @Test
//    public void testCreateAuthorDraftPost() {
//        postDto.setAuthorId(authorId);
//        post.setAuthorId(authorId);
//        when(postRepository.save(post)).thenReturn(post);
//
//        createDto = postService.createDraftPost(postDto, );
//
//        verify(postRepository, times(1)).save(post);
//        assertEquals(postDto, createDto);
//    }
//
//    @Test
//    public void testCreateProjectDraftPost() {
//        postDto.setProjectId(projectId);
//        post.setProjectId(projectId);
//        when(postRepository.save(post)).thenReturn(post);
//
//        PostDto createDto = postService.createDraftPost(postDto, );
//
//        verify(postRepository, times(1)).save(post);
//        assertEquals(postDto, createDto);
//    }
//
//    @Test
//    public void testNotCreateDraftPostWhenAuthorNotExists() {
//        postDto.setAuthorId(authorId);
//        post.setAuthorId(authorId);
//        when(userServiceClient.getUser(authorId)).thenThrow(FeignException.class);
//
//        assertThrows(EntityNotFoundException.class, () -> postService.createDraftPost(postDto, ));
//        verify(userServiceClient, times(1)).getUser(authorId);
//    }
//
//    @Test
//    public void testNotCreateDraftPostWhenProjectNotExists() {
//        postDto.setProjectId(projectId);
//        post.setProjectId(projectId);
//        when(projectServiceClient.getProject(projectId)).thenThrow(FeignException.class);
//
//        assertThrows(EntityNotFoundException.class, () -> postService.createDraftPost(postDto, ));
//        verify(projectServiceClient, times(1)).getProject(projectId);
//    }
//
//    @Test
//    public void testNotCreateDraftPostWhenAuthorAndProjectNull() {
//        assertThrows(DataValidationException.class, () -> postService.createDraftPost(postDto, ));
//    }
//
//    @Test
//    public void testNotCreateDraftPostWhenAuthorAndProjectNotNull() {
//        postDto.setAuthorId(authorId);
//        postDto.setProjectId(projectId);
//        assertThrows(DataValidationException.class, () -> postService.createDraftPost(postDto, ));
//    }
//
//    /**
//     * publishPost
//     */
//
//    @Test
//    public void testPublishPost() {
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//
//        createDto = postService.publishPost(postId);
//
//        assertTrue(createDto.isPublished());
//        assertNotNull(createDto.getPublishedAt());
//        verify(postRepository, times(1)).findById(postId);
//        verify(postMapper, times(1)).toDto(post);
//    }
//
//    @Test
//    public void testNotPublishPostWhenPostNotExists() {
//        when(postRepository.findById(postId)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> postService.publishPost(postId));
//        verify(postRepository, times(1)).findById(postId);
//    }
//
//    @Test
//    public void testNotPublishPostWhenPostDeleted() {
//        post.setDeleted(true);
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//
//        assertThrows(DataValidationException.class, () -> postService.publishPost(postId));
//        verify(postRepository, times(1)).findById(postId);
//    }
//
//    @Test
//    public void testNotPublishPostWhenPostPublished() {
//        post.setPublished(true);
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//
//        assertThrows(DataValidationException.class, () -> postService.publishPost(postId));
//        verify(postRepository, times(1)).findById(postId);
//    }
//
//    /**
//     * updatePost
//     */
//
//    @Test
//    public void testUpdatePost() {
//        String updatedContent = "updatedContent";
//        postDto.setContent(updatedContent);
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//
//        createDto = postService.updatePost(postId, postDto);
//
//        assertEquals(updatedContent, createDto.getContent());
//        verify(postRepository, times(1)).findById(postId);
//        verify(postMapper, times(1)).updatePostFromDto(postDto, post);
//        verify(postMapper, times(1)).toDto(post);
//    }
//
//    @Test
//    public void testNotUpdateWhenPostNotExists() {
//        when(postRepository.findById(postId)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> postService.updatePost(postId, postDto));
//        verify(postRepository, times(1)).findById(postId);
//    }
//
//    @Test
//    public void testNotUpdateWhenPostDeleted() {
//        post.setDeleted(true);
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//
//        assertThrows(DataValidationException.class, () -> postService.updatePost(postId, postDto));
//        verify(postRepository, times(1)).findById(postId);
//    }
//
//    /**
//     * deletePost
//     */
//
//    @Test
//    public void testDeletePost() {
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//
//        assertDoesNotThrow(() -> postService.deletePost(postId));
//        verify(postRepository, times(1)).findById(postId);
//    }
//
//    @Test
//    public void testNotDeleteWhenPostNotExists() {
//        when(postRepository.findById(postId)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> postService.deletePost(postId));
//        verify(postRepository, times(1)).findById(postId);
//    }
//
//    @Test
//    public void testNotDeleteWhenPostDeleted() {
//        post.setDeleted(true);
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//
//        assertThrows(DataValidationException.class, () -> postService.deletePost(postId));
//        verify(postRepository, times(1)).findById(postId);
//    }
//
//    /**
//     * getPost
//     */
//
//    @Test
//    public void testGetPost() {
//        post.setId(1L);
//        post.setPublished(true);
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//
//        createDto = postService.getPost(postId);
//
//        assertEquals(postId, createDto.getId());
//        assertTrue(createDto.isPublished());
//        verify(postRepository, times(1)).findById(postId);
//    }
//
//    @Test
//    public void testNotGetWhenPostNotExists() {
//        post.setPublished(true);
//        when(postRepository.findById(postId)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> postService.getPost(postId));
//        verify(postRepository, times(1)).findById(postId);
//    }
//
//    @Test
//    public void testNotGetWhenPostDeleted() {
//        post.setPublished(true);
//        post.setDeleted(true);
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//
//        assertThrows(DataValidationException.class, () -> postService.getPost(postId));
//        verify(postRepository, times(1)).findById(postId);
//    }
//
//    @Test
//    public void testNotGetWhenPostNotPublished() {
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//
//        assertThrows(DataValidationException.class, () -> postService.getPost(postId));
//        verify(postRepository, times(1)).findById(postId);
//    }
//
//    @Test
//    public void testGetAllAuthorDraftPosts() {
//        when(postRepository.findByAuthorId(authorId)).thenReturn(foundList);
//        assertEquals(draftPosts, postService.getAllAuthorDraftPosts(authorId));
//    }
//
//    @Test
//    public void testNotGetAllDraftPostsWhenAuthorNotExists() {
//        when(userServiceClient.getUser(authorId)).thenThrow(FeignException.class);
//        assertThrows(EntityNotFoundException.class, () -> postService.getAllAuthorDraftPosts(authorId));
//    }
//
//    @Test
//    public void testGetAllAuthorPosts() {
//        when(postRepository.findByAuthorId(authorId)).thenReturn(foundList);
//        assertEquals(posts, postService.getAllAuthorPosts(authorId));
//    }
//
//    @Test
//    public void testNotGetAllPostsWhenAuthorNotExists() {
//        when(userServiceClient.getUser(authorId)).thenThrow(FeignException.class);
//        assertThrows(EntityNotFoundException.class, () -> postService.getAllAuthorPosts(authorId));
//    }
//
//    @Test
//    public void testGetAllProjectDraftPosts() {
//        when(postRepository.findByProjectId(projectId)).thenReturn(foundList);
//        assertEquals(draftPosts, postService.getAllProjectDraftPosts(projectId));
//    }
//
//    @Test
//    public void testNotGetAllDraftPostsWhenProjectNotExists() {
//        when(projectServiceClient.getProject(projectId)).thenThrow(FeignException.class);
//        assertThrows(EntityNotFoundException.class, () -> postService.getAllProjectDraftPosts(projectId));
//    }
//
//    @Test
//    public void testGetAllProjectPosts() {
//        when(postRepository.findByProjectId(projectId)).thenReturn(foundList);
//        assertEquals(posts, postService.getAllProjectPosts(projectId));
//    }
//
//    @Test
//    public void testNotGetAllPostsWhenProjectNotExists() {
//        when(projectServiceClient.getProject(projectId)).thenThrow(FeignException.class);
//        assertThrows(EntityNotFoundException.class, () -> postService.getAllProjectPosts(projectId));
//    }
//}