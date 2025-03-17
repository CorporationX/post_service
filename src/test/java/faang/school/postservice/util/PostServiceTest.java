package faang.school.postservice.util;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
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
import static faang.school.postservice.service.PostService.POST_WITH_HAS_ALREADY_BEEN_CREATED;
import static faang.school.postservice.utils.validationUtils.PostValidation.POST_ALREADY_PUBLISHED;
import static faang.school.postservice.utils.validationUtils.PostValidation.POST_AUTHORS_ERROR;
import static faang.school.postservice.utils.validationUtils.PostValidation.POST_DELETED;
import static faang.school.postservice.utils.validationUtils.PostValidation.POST_DRAFT_CANT_BE_DELETED;
import static faang.school.postservice.utils.validationUtils.PostValidation.POST_DRAFT_CANT_BE_PUBLISHED;
import static faang.school.postservice.utils.validationUtils.PostValidation.PROJECT_ID_CANT_BE_NULL;
import static faang.school.postservice.utils.validationUtils.PostValidation.USER_ID_CANT_BE_NULL;
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

    @Spy
    private PostMapper postMapper;

    private PostRequestDto postRequestDto;
    private Post post;

    @BeforeEach
    public void startUp() {
        postRequestDto = new PostRequestDto(1L, "content", 1L,
                null, false, false);
        post = Post.builder().id(1L).content("content").authorId(1L).build();
    }

    @Test
    public void testCreateDraftPost_bothAuthorsAbsent() {
        postRequestDto.setAuthorId(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.createDraftPost(postRequestDto)
        );
        assertEquals(POST_AUTHORS_ERROR, exception.getMessage());
    }

    @Test
    public void testCreateDraftPost_bothAuthorsSet() {
        postRequestDto.setProjectId(1L);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.createDraftPost(postRequestDto)
        );
        assertEquals(POST_AUTHORS_ERROR, exception.getMessage());
    }

    @Test
    public void testCreateDraftPost_postAlreadyDeleted() {
        postRequestDto.setDeleted(true);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.createDraftPost(postRequestDto)
        );
        assertEquals(POST_DRAFT_CANT_BE_DELETED, exception.getMessage());
    }

    @Test
    public void testCreateDraftPost_postAlreadyPublished() {
        postRequestDto.setPublished(true);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.createDraftPost(postRequestDto)
        );
        assertEquals(POST_DRAFT_CANT_BE_PUBLISHED, exception.getMessage());
    }

    @Test
    public void testCreateDraftPost_draftAlreadyCreated() {
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.of(new Post()));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.createDraftPost(postRequestDto)
        );
        assertEquals(String.format(POST_WITH_HAS_ALREADY_BEEN_CREATED, postRequestDto.getId()),
                exception.getMessage());
    }

    @Test
    public void testCreateDraftPost_savedDraft() {
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.empty());
        postService.createDraftPost(postRequestDto);
        verify(postRepository, times(1))
                .save(postMapper.ToPost(postRequestDto));
    }

    @Test
    public void testPublishPost_noDraft() {
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.empty());
        PostNotFoundException exception = assertThrows(PostNotFoundException.class,
                () -> postService.publishPost(postRequestDto)
        );
        assertEquals(String.format(NO_POST_FOUND, postRequestDto.getId()), exception.getMessage());
    }

    @Test
    public void testPublishPost_postAlreadyPublished() {
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.of(post));
        post.setPublished(true);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.publishPost(postRequestDto)
        );
        assertEquals(String.format(POST_ALREADY_PUBLISHED, post.getId()), exception.getMessage());
    }

    @Test
    public void testPublishPost_deletedPost() {
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.of(post));
        post.setDeleted(true);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.publishPost(postRequestDto)
        );
        assertEquals(String.format(POST_DELETED, post.getId()), exception.getMessage());
    }

    @Test
    public void testPublishPost_postPublished() {
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.of(post));
        postService.publishPost(postRequestDto);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.publishPost(postRequestDto)
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
                () -> postService.deletePost(postRequestDto)
        );
        assertEquals(String.format(NO_POST_FOUND, postRequestDto.getId()), exception.getMessage());
    }

    @Test
    public void testDeletePost_postAlreadyDeleted() {
        post.setDeleted(true);
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.of(post));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.deletePost(postRequestDto)
        );
        assertEquals(POST_HAS_ALREADY_BEEN_DELETED, exception.getMessage());
    }

    @Test
    public void testDeletePost_deletePost() {
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.of(post));
        postService.deletePost(postRequestDto);
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
        PostResponseDto responseDto = postService.getPostById(1L);
        assertEquals(postMapper.toPostResponseDto(post), responseDto);
    }

    @Test
    public void testGetUserDraftPosts_nullUserId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.getUserDraftPosts(null)
        );
        assertEquals(USER_ID_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testGetUserDraftPosts_draftsFound() {
        when(postRepository.findDraftsByAuthorId(1L)).thenReturn(List.of(post));
        List<PostResponseDto> responseDtos = postService.getUserDraftPosts(1L);
        assertEquals(responseDtos, postMapper.toPostResponseDtoList(List.of(post)));
    }

    @Test
    public void testProjectDraftPosts_nullUserId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.getProjectDraftPosts(null)
        );
        assertEquals(PROJECT_ID_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testProjectDraftPosts_draftsFound() {
        when(postRepository.findDraftsByProjectId(1L)).thenReturn(List.of(post));
        List<PostResponseDto> responseDtos = postService.getProjectDraftPosts(1L);
        assertEquals(responseDtos, postMapper.toPostResponseDtoList(List.of(post)));
    }

    @Test
    public void testUserPublishedPosts_nullUserId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.getUserPublishedPosts(null)
        );
        assertEquals(USER_ID_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testUserPublishedPosts_draftsFound() {
        when(postRepository.findPublishedByAuthorId(1L)).thenReturn(List.of(post));
        List<PostResponseDto> responseDtos = postService.getUserPublishedPosts(1L);
        assertEquals(responseDtos, postMapper.toPostResponseDtoList(List.of(post)));
    }

    @Test
    public void testProjectPublishedPosts_nullUserId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.getProjectPublishedPosts(null)
        );
        assertEquals(PROJECT_ID_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testProjectPublishedPosts_draftsFound() {
        when(postRepository.findPublishedByProjectId(1L)).thenReturn(List.of(post));
        List<PostResponseDto> responseDtos = postService.getProjectPublishedPosts(1L);
        assertEquals(responseDtos, postMapper.toPostResponseDtoList(List.of(post)));
    }
}
