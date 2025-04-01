package faang.school.postservice.PostService;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static faang.school.postservice.service.PostService.CANT_UPDATE_DELETED_POST;
import static faang.school.postservice.service.PostService.NO_POST_FOUND;
import static faang.school.postservice.service.PostService.POST_HAS_ALREADY_BEEN_DELETED;
import static faang.school.postservice.utils.validationUtils.PostValidation.POST_ALREADY_PUBLISHED;
import static faang.school.postservice.utils.validationUtils.PostValidation.POST_DELETED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;
    @Mock
    private LikeRepository likeRepository;

    @Spy
    private PostMapperImpl postMapper;

    private PostRequestDto postRequestDto;
    private Post post;
    private final Long id = 1L;

    @BeforeEach
    public void startUp() {
        postRequestDto = new PostRequestDto(1L, "content", 1L,
                null, false, false);
        post = Post.builder().id(1L).content("content").authorId(1L).build();
    }

    @Test
    public void testCreateDraftPost_savedDraft() {
        postService.createDraftPost(postRequestDto);

        verify(postRepository, times(1))
                .save(postMapper.toPost(postRequestDto));
    }

    @Test
    public void testPublishPost_noDraft() {
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.empty());
        PostNotFoundException exception = assertThrows(PostNotFoundException.class,
                () -> postService.publishPost(id)
        );
        assertEquals(String.format(NO_POST_FOUND, postRequestDto.getId()), exception.getMessage());
    }

    @Test
    public void testPublishPost_deletedPost() {
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.of(post));
        post.setDeleted(true);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.publishPost(id)
        );
        assertEquals(String.format(POST_DELETED, post.getId()), exception.getMessage());
    }

    @Test
    public void testPublishPost_postPublished() {
        post.setPublished(true);
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.of(post));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.publishPost(id)
        );
        assertEquals(String.format(POST_ALREADY_PUBLISHED, post.getId()), exception.getMessage());

    }

    @Test
    public void testUpdatePost_noPost() {
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.empty());
        PostNotFoundException exception = assertThrows(PostNotFoundException.class,
                () -> postService.updatePost(postRequestDto)
        );
        assertEquals(String.format(NO_POST_FOUND, postRequestDto.getId()), exception.getMessage());
    }

    @Test
    public void testUpdatePost_deletedPost() {
        post.setDeleted(true);
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.of(post));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.updatePost(postRequestDto)
        );
        assertEquals(CANT_UPDATE_DELETED_POST, exception.getMessage());
    }

    @Test
    public void testUpdatePost_updateContent() {
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.of(post));
        postRequestDto.setContent("New content");
        post.setContent("New content");
        postService.updatePost(postRequestDto);
        verify(postRepository, times(1))
                .save(post);
    }

    @Test
    public void testDeletePost_noPost() {
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.empty());
        PostNotFoundException exception = assertThrows(PostNotFoundException.class,
                () -> postService.deletePost(id)
        );
        assertEquals(String.format(NO_POST_FOUND, postRequestDto.getId()), exception.getMessage());
    }

    @Test
    public void testDeletePost_postAlreadyDeleted() {
        post.setDeleted(true);
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.of(post));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.deletePost(id)
        );
        assertEquals(POST_HAS_ALREADY_BEEN_DELETED, exception.getMessage());
    }

    @Test
    public void testDeletePost_deletePost() {
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.of(post));
        postService.deletePost(id);
        post.setDeleted(true);
        verify(postRepository, times(1))
                .save(post);
    }

    @Test
    public void testGetPostById_noPost() {
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.empty());
        PostNotFoundException exception = assertThrows(PostNotFoundException.class,
                () -> postService.getPostById(1L)
        );
        assertEquals(String.format(NO_POST_FOUND, 1L), exception.getMessage());
    }

    @Test
    public void testGetPostById_postFound() {
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.of(post));
        when(likeRepository.countByPostId(post.getId())).thenReturn(5L);

        PostResponseDto responseDto = postService.getPostById(1L);

        PostResponseDto expected = postMapper.toPostResponseDto(post);
        expected.setLikesCount(5L);

        assertEquals(expected, responseDto);
    }

    @Test
    public void testGetUserDraftPosts_draftsFound() {
        when(postRepository.findDraftsByAuthorId(1L)).thenReturn(List.of(post));
        when(likeRepository.countByPostId(post.getId())).thenReturn(3L);

        List<PostResponseDto> responseDtos = postService.getUserDraftPosts(1L);

        PostResponseDto expected = postMapper.toPostResponseDto(post);
        expected.setLikesCount(3L);

        assertEquals(List.of(expected), responseDtos);
    }

    @Test
    public void testProjectDraftPosts_draftsFound() {
        when(postRepository.findDraftsByProjectId(1L)).thenReturn(List.of(post));
        when(likeRepository.countByPostId(post.getId())).thenReturn(2L);

        List<PostResponseDto> responseDtos = postService.getProjectDraftPosts(1L);

        PostResponseDto expected = postMapper.toPostResponseDto(post);
        expected.setLikesCount(2L);

        assertEquals(List.of(expected), responseDtos);
    }

    @Test
    public void testUserPublishedPosts_draftsFound() {
        when(postRepository.findPublishedByAuthorId(1L)).thenReturn(List.of(post));
        when(likeRepository.countByPostId(post.getId())).thenReturn(7L);

        List<PostResponseDto> responseDtos = postService.getUserPublishedPosts(1L);

        PostResponseDto expected = postMapper.toPostResponseDto(post);
        expected.setLikesCount(7L);

        assertEquals(List.of(expected), responseDtos);
    }

    @Test
    public void testProjectPublishedPosts_draftsFound() {
        when(postRepository.findPublishedByProjectId(1L)).thenReturn(List.of(post));
        when(likeRepository.countByPostId(post.getId())).thenReturn(4L);

        List<PostResponseDto> responseDtos = postService.getProjectPublishedPosts(1L);

        PostResponseDto expected = postMapper.toPostResponseDto(post);
        expected.setLikesCount(4L);

        assertEquals(List.of(expected), responseDtos);
    }
}
