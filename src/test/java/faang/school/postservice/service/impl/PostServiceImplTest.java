package faang.school.postservice.service.impl;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostValidator validator;

    @Spy
    private PostMapperImpl postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    private Long postId;
    private Post post;
    private PostDto postDto;
    private List<Post> posts;
    private List<PostDto> postDtos;

    @BeforeEach
    void setUp() {
        PostMapperImpl postMapper = new PostMapperImpl();
        postDto = PostDto.builder()
                .id(1L)
                .content("Hello world!")
                .authorId(123L)
                .projectId(456L)
                .build();
        post = postMapper.toEntity(postDto);
        postId = post.getId();
        posts = List.of(post);
        postDtos = List.of(postDto);
    }

    @Test
    void createDraftPost_ExistsCreator_AuthorIdIsNull() {
        Long projectId = postDto.getProjectId();
        postDto.setAuthorId(null);

        postService.createDraftPost(postDto);

        verify(projectServiceClient).existsProjectById(projectId);
        verify(userServiceClient, never()).existsUserById(anyLong());
        verifyCreateDraftPost();
    }

    @Test
    void createDraftPost_ExistsCreator_ProjectIdIsNull() {
        Long authorId = postDto.getAuthorId();
        postDto.setProjectId(null);

        postService.createDraftPost(postDto);

        verify(userServiceClient).existsUserById(authorId);
        verify(projectServiceClient, never()).existsProjectById(anyLong());
        verifyCreateDraftPost();
    }

    private void verifyCreateDraftPost() {
        verify(postMapper).toEntity(postDto);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void publishPost_AlreadyPublished() {
        Optional<Post> optionalPost = Optional.of(post);
        configureSearchPost(optionalPost);
        post.setPublished(true);

        postService.publishPost(postId);

        verify(postRepository, never()).save(post);
    }

    @Test
    void publishPost_Published() {
        Optional<Post> optionalPost = Optional.of(post);
        configureSearchPost(optionalPost);
        post.setPublished(false);

        postService.publishPost(postId);

        verify(postRepository).save(postCaptor.capture());

        Post post = postCaptor.getValue();
        assertTrue(post.isPublished());
        assertNotNull(post.getPublishedAt());
    }

    @Test
    void updateContentPost() {
        String content = "Hello world!";

        postService.updateContentPost(content, postId);

        verify(postRepository).updateContentByPostId(postId, content);
    }

    @Test
    void softDeletePost() {
        postService.softDeletePost(postId);

        verify(postRepository).softDeletePostById(postId);
    }

    @Test
    void getPost() {
        Optional<Post> optionalPost = Optional.of(post);
        configureSearchPost(optionalPost);

        PostDto postDto = postService.getPost(postId);

        verify(postRepository).findById(postId);
        assertEquals(this.postDto, postDto);
    }

    private void configureSearchPost(Optional<Post> optionalPost) {
        when(postRepository.findById(postId)).thenReturn(optionalPost);
    }

    @Test
    void getDraftPostsByUserId() {
        Long authorId = postDto.getAuthorId();
        when(postRepository.findByAuthorIdAndUnpublished(authorId)).thenReturn(posts);

        var result = postService.getDraftPostsByUserId(authorId);

        verify(postRepository).findByAuthorIdAndUnpublished(authorId);
        verify(postMapper).toDto(posts);
        assertEquals(postDtos, result);
    }

    @Test
    void getDraftPostsByProjectId() {
        Long projectId = postDto.getProjectId();
        when(postRepository.findByProjectIdAndUnpublished(projectId)).thenReturn(posts);

        var result = postService.getDraftPostsByProjectId(projectId);

        verify(postRepository).findByProjectIdAndUnpublished(projectId);
        verify(postMapper).toDto(posts);
        assertEquals(postDtos, result);
    }

    @Test
    void getPublishedPostsByUserId() {
        Long authorId = postDto.getAuthorId();
        when(postRepository.findByAuthorIdAndPublished(authorId)).thenReturn(posts);

        var result = postService.getPublishedPostsByUserId(authorId);

        verify(postRepository).findByAuthorIdAndPublished(authorId);
        verify(postMapper).toDto(posts);
        assertEquals(postDtos, result);
    }

    @Test
    void getPublishedPostsByProjectId() {
        Long projectId = postDto.getProjectId();
        when(postRepository.findByProjectIdAndPublished(projectId)).thenReturn(posts);

        var result = postService.getPublishedPostsByProjectId(projectId);

        verify(postRepository).findByProjectIdAndPublished(projectId);
        verify(postMapper).toDto(posts);
        assertEquals(postDtos, result);
    }

    @Test
    void getAuthorsWithMoreFiveUnverifiedPosts() {
        List<Long> violatorIds = List.of(1L, 2L, 3L);
        when(postRepository.findAuthorsWithMoreThanFiveUnverifiedPosts()).thenReturn(violatorIds);

        List<Long> result = postService.getAuthorsWithMoreFiveUnverifiedPosts();

        assertEquals(violatorIds, result);
    }
}