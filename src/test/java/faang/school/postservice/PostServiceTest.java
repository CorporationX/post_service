package faang.school.postservice;

import faang.school.postservice.dto.posts.PostCreatingRequest;
import faang.school.postservice.dto.posts.PostResultResponse;
import faang.school.postservice.dto.posts.PostUpdatingDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.utils.PostUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostUtil postUtil;
    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    private static PostCreatingRequest postCreatingRequest;
    private static PostResultResponse postResultResponse;
    private static Post post;

    @BeforeEach
    public void setUp() {
        postCreatingRequest = PostCreatingRequest.builder()
                .content("This is a test content")
                .authorId(1L)
                .projectId(null)
                .build();

        postResultResponse = PostResultResponse.builder()
                .id(postCreatingRequest.authorId())
                .build();

        post = Post.builder()
                .id(1L)
                .content("HeLlO_W0o0oo0orlxD!")
                .authorId(postCreatingRequest.authorId())
                .published(false)
                .deleted(false)
                .build();
    }

    @Test
    public void createPost_PostWasCreatedSuccessfully() {
        Mockito.when(postUtil.validateCreator(postCreatingRequest.authorId(), postCreatingRequest.projectId()))
                .thenReturn(0);
        Mockito.when(postRepository.save(any(Post.class)))
                .thenReturn(post);

        PostResultResponse result = postService.createPost(postCreatingRequest);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result, postResultResponse);
        verify(postUtil, times(1)).validateCreator(postCreatingRequest.authorId(), postCreatingRequest.projectId());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    public void publishPost_PostSuccessfullyPublished() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        PostResultResponse result = postService.publishPost(post.getId());

        Assertions.assertEquals(postResultResponse, result);
    }

    @Test
    public void publishPost_PostAlreadyPublished() {
        Post setPost = Post.builder()
                .id(1L)
                .content("HeLlO_W0o0oo0orlxD!")
                .authorId(postCreatingRequest.authorId())
                .published(true)
                .deleted(false)
                .build();

        setPost.setPublished(true);

        when(postRepository.findById(setPost.getId())).thenReturn(Optional.of(setPost));

        Assertions.assertThrows(IllegalArgumentException.class, () -> postService.publishPost(setPost.getId()));
    }

    @Test
    public void updatePost_PostWasUpdatedSuccessfully() {
        Post setPost = Post.builder()
                .id(1L)
                .content("HeLlO_W0o0oo0orlxD!")
                .authorId(postCreatingRequest.authorId())
                .published(true)
                .deleted(false)
                .build();

        PostUpdatingDto postUpdatingDto = PostUpdatingDto.builder()
                .postId(1L)
                .updatingContent("New content")
                .build();

        Long postId = setPost.getId();
        String postContent = setPost.getContent() + "New content";

        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(setPost));


        PostResultResponse result = postService.updatePost(postUpdatingDto);

        Assertions.assertEquals(result, postResultResponse);
    }

    @Test
    public void updatePost_PostWasDeleted() {
        Post setPost = Post.builder()
                .id(1L)
                .content("HeLlO_W0o0oo0orlxD!")
                .authorId(postCreatingRequest.authorId())
                .published(true)
                .deleted(true)
                .build();

        updatePostWithError(setPost);
    }

    @Test
    public void updatePost_PostWasNotPublishedYet() {
        Post setPost = Post.builder()
                .id(1L)
                .content("HeLlO_W0o0oo0orlxD!")
                .authorId(postCreatingRequest.authorId())
                .published(false)
                .deleted(false)
                .build();

        updatePostWithError(setPost);
    }

    @Test
    public void softDeletePost_PostWasSoftDeletedSuccessfully() {
        Long postId = post.getId();

        Mockito.when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        PostResultResponse result = postService.softDelete(postId);

        Assertions.assertEquals(result, postResultResponse);
    }

    @Test
    public void softDeletePost_ThrowsExceptionWhenAlreadyDeleted() {
        Long postId = post.getId();

        post.setDeleted(true);

        Mockito.when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        Assertions.assertThrows(IllegalArgumentException.class, () -> postService.softDelete(postId));
    }

    private void updatePostWithError(Post setPost) {
        PostUpdatingDto postUpdatingDto = PostUpdatingDto.builder()
                .postId(1L)
                .updatingContent("New content")
                .build();

        Long postId = setPost.getId();
        String postContent = setPost.getContent() + "New content";

        when(postRepository.findById(postId)).thenReturn(Optional.of(setPost));

        Assertions.assertThrows(IllegalArgumentException.class, () -> postService.updatePost(postUpdatingDto));
    }
}
